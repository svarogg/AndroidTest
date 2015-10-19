package com.example.mike.androidtest.handlers;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mike.androidtest.functors.ObjToVoidFunctor;
import com.example.mike.androidtest.model.Contact;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

            contacts.add(new Contact(id, name, imageUrl, emailAddressLoader, phoneNumberLoader));
        }

        return contacts;
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
