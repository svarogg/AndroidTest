package com.example.mike.androidtest.handlers;


import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mike.androidtest.functors.ObjToVoidFunctor;
import com.example.mike.androidtest.model.Contact;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mike on 14/10/2015.
 */
public class ContactHandler {
    private static final String CONTACTS_SERVER_URL = "http://1-dot-vimi-demo-chat-server.appspot.com/stam";

    public static List<Contact> getContacts(Context context) {
        final ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        final int displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        final int photoUrlIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);

        LinkedList<Contact> contacts = new LinkedList<Contact>();

        while (cursor.moveToNext()) {
            contacts.add(getContactFromCursor(contentResolver, displayNameIndex, photoUrlIndex, cursor));
        }

        return contacts;
    }

    @NonNull
    private static Contact getContactFromCursor(final ContentResolver contentResolver, int displayNameIndex, int photoUrlIndex, Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        String name = cursor.getString(displayNameIndex);
        String imageUrl = cursor.getString(photoUrlIndex);

        Contact.Loader emailAddressLoader = new Contact.Loader() {
            @Override
            public List<String> loadList(int contactId) {
                return resolveEmailAddresses(contentResolver, Integer.toString(contactId));
            }
        };
        Contact.Loader phoneNumberLoader = new Contact.Loader() {
            @Override
            public List<String> loadList(int contactId) {
                return resolvePhoneNumbers(contentResolver, Integer.toString(contactId));
            }
        };

        return new Contact(id, name, imageUrl, emailAddressLoader, phoneNumberLoader);
    }

    public static void addContact(Context context, Contact contact) throws RemoteException, OperationApplicationException {
        final ContentResolver contentResolver = context.getContentResolver();

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation
                .newInsert(RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_TYPE, null)
                .withValue(RawContacts.ACCOUNT_NAME, null)
                .build());
        ops.add(ContentProviderOperation
                .newInsert(Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.DISPLAY_NAME, contact.getName()).build());
        for (String phoneNumber : contact.getPhoneNumbers()) {
            ops.add(ContentProviderOperation
                    .newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                    .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                    .withValue(Phone.NUMBER, phoneNumber)
                    .withValue(Phone.TYPE, Phone.TYPE_HOME)
                    .build());
        }
        for(String emailAddress : contact.getEmailAddresses()){
            ops.add(ContentProviderOperation
                    .newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                    .withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
                    .withValue(Email.TYPE, Email.TYPE_HOME)
                    .withValue(Email.DATA, emailAddress)
                    .build());
        }
        String imageUrl = contact.getImageUrl();
        if(imageUrl != null){
            try {
                Bitmap bitmap = BitmapHandler.downloadBitmap(imageUrl);
                bitmap = BitmapHandler.resizeBitmap(bitmap, 720, 720);
                ByteArrayOutputStream imageByteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageByteArrayOutputStream);
                ops.add(ContentProviderOperation
                        .newInsert(Data.CONTENT_URI)
                        .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                        .withValue(Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE)
                        .withValue(Photo.PHOTO, imageByteArrayOutputStream.toByteArray())
                        .build());

            } catch (IOException e) {
                // do nothing - if image url is not good - than ignore it.
            }

        }

        contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
    }

    public static boolean hasContact(Context context, String name){
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[] { ContactsContract.PhoneLookup._ID };
        String selection = StructuredName.DISPLAY_NAME + " = ?";
        String[] selectionArguments = { name };
        Cursor cursor = contentResolver.query(Data.CONTENT_URI, projection, selection, selectionArguments, null);
        try {
            return cursor.getCount() > 0;
        }finally {
            cursor.close();
        }
    }

    private static List<String> resolveEmailAddresses(ContentResolver contentResolver, String contactId) {
        Cursor emailAddressCursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactId}, null);
        int emailAddressIndex = emailAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);

        try {
            List<String> emailAddresses = new LinkedList<>();
            while(emailAddressCursor.moveToNext()){
                emailAddresses.add(emailAddressCursor.getString(emailAddressIndex));
            }
            return emailAddresses;
        } finally {
            emailAddressCursor.close();
        }
    }

    private static List<String> resolvePhoneNumbers(ContentResolver contentResolver, String contactId) {
        Cursor phoneNumberCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactId}, null);
        int phoneNumberIndex = phoneNumberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        try {
            List<String> phoneNumbers = new LinkedList<>();
            while(phoneNumberCursor.moveToNext()){
                phoneNumbers.add(phoneNumberCursor.getString(phoneNumberIndex));
            }
            return phoneNumbers;
        } finally {
            phoneNumberCursor.close();
        }
    }

    public static void loadFromServer(Context context, final ObjToVoidFunctor<List<Contact>> successCallback, final ObjToVoidFunctor<Exception> errorCallback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, CONTACTS_SERVER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                successCallback.execute(parseRawContacts(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorCallback.execute(error);
            }
        });
        queue.add(request);
    }

    private static String parseContactToken(String token){
        return token.equals("unknown") ? null : token;
    }

    private static List<Contact> parseRawContacts(String rawContacts) {
        List<Contact> parsedContacts = new LinkedList<Contact>();

        Pattern regex = Pattern.compile("([^,]*), ([^,]*), ([^,]*), ([^,]*)");
        Scanner scanner = new Scanner(rawContacts);
        for(int i = 0; scanner.hasNext(); i++){
            Matcher matcher = regex.matcher(scanner.nextLine());
            if(matcher.find()) {
                parsedContacts.add(new Contact(i,
                        parseContactToken(matcher.group(1)),
                        parseContactToken(matcher.group(4)),
                        parseContactToken(matcher.group(2)),
                        parseContactToken(matcher.group(3))));
            }
        }

        return parsedContacts;
    }
}
