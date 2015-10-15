package com.example.mike.androidtest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mike.androidtest.model.Contact;

public class ContactDialogFragment extends DialogFragment {
    private static final String CONTACT_KEY = "contact";
    private Contact contact;

    public static ContactDialogFragment create(Contact contact){
        ContactDialogFragment fragment = new ContactDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(CONTACT_KEY, contact);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Contact contact = (Contact)getArguments().getSerializable(CONTACT_KEY);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View layout = inflater.inflate(R.layout.contact_details, null);
        ((TextView)layout.findViewById(R.id.contactNameView)).setText(contact.getName());

        String phoneNumber = contact.getPhoneNumber();
        if(phoneNumber != null){
            ((TextView)layout.findViewById(R.id.phoneNumberView)).setText(phoneNumber);
            layout.findViewById(R.id.phoneButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callContact(contact);
                }
            });

            layout.findViewById(R.id.smsButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    smsContact(contact);
                }
            });
        }else{
            layout.findViewById(R.id.phoneNumberContainer).setVisibility(View.GONE);
        }

        String emailAddress = contact.getEmailAddress();
        if(emailAddress != null){
            ((TextView)layout.findViewById(R.id.emailAddressView)).setText(emailAddress);
            layout.findViewById(R.id.emailButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    emailContact(contact);
                }
            });
        }else{
            layout.findViewById(R.id.emailAddressContainer).setVisibility(View.GONE);
        }

        String imageUrl = contact.getImageUrl();
        if(imageUrl != null){
            ((ImageView)layout.findViewById(R.id.contactImageView)).setImageURI(Uri.parse(imageUrl));
        }

        builder.setView(layout);

        return builder.create();
    }

    private void emailContact(Contact contact) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{contact.getEmailAddress()});

        startActivity(intent);
    }

    private void smsContact(Contact contact) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("sms:" + contact.getPhoneNumber()));
//        intent.putExtra("address", contact.getPhoneNumber());

        startActivity(intent);
    }

    private void callContact(Contact contact) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + contact.getPhoneNumber()));
//        intent.putExtra("address", contact.getPhoneNumber());

        startActivity(intent);
    }
}