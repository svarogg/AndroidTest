package com.example.mike.androidtest.handlers;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import com.example.mike.androidtest.model.Contact;

/**
 * Created by Mike on 19/10/2015.
 */
public class IntentHandler {
    public static void sendSms(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("sms:" + phoneNumber));

        context.startActivity(intent);
    }

    public static void call(Context context, String phoneNumber) {
        if (context.checkCallingOrSelfPermission("android.permission.CALL_PHONE") != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "User denied permission to call contact", Toast.LENGTH_SHORT);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));

        context.startActivity(intent);
    }

    public static void sendEmail(Context context, String emailAddress) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});

        context.startActivity(intent);
    }
}
