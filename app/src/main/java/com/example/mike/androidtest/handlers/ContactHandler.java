package com.example.mike.androidtest.handlers;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.example.mike.androidtest.model.Contact;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Mike on 14/10/2015.
 */
public class ContactHandler {
    public static List<Contact> getContacts(Context context){
        final ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        final int displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        final int photoUrlIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);

        LinkedList<Contact> contacts = new LinkedList<Contact>();

        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(displayNameIndex);
            String imageUrl = cursor.getString(photoUrlIndex);

            Contact.Loader emailAddressLoader = new Contact.Loader() {
                @Override
                public String loadString(int contactId) {
                    return getEmailAddress(contentResolver, Integer.toString(contactId));
                }
            };
            Contact.Loader phoneNumberLoader = new Contact.Loader() {
                @Override
                public String loadString(int contactId) {
                    return getPhoneNumber(contentResolver, Integer.toString(contactId));
                }
            };

            contacts.add(new Contact(id, name, imageUrl, emailAddressLoader, phoneNumberLoader));
        }

        return contacts;
    }

    private static String getEmailAddress(ContentResolver contentResolver, String contactId) {
        Cursor emailCursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactId}, null);
        try {
            return emailCursor.moveToFirst()
                    ? emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    : null;
        }finally {
            emailCursor.close();
        }
    }

    private static String getPhoneNumber(ContentResolver contentResolver, String contactId) {
        Cursor phoneNumberCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactId}, null);

        try {
            return phoneNumberCursor.moveToFirst()
                    ? phoneNumberCursor.getString(phoneNumberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    : null;
        }
        finally {
            phoneNumberCursor.close();
        }
    }
}
