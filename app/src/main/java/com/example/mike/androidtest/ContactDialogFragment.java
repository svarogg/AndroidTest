package com.example.mike.androidtest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.mike.androidtest.handlers.ContactHandler;
import com.example.mike.androidtest.model.Contact;

public class ContactDialogFragment extends DialogFragment {
    private static final String CONTACT_KEY = "contact";
    private Contact contact;
    private ImageLoader imageLoader;

    public static ContactDialogFragment create(Contact contact, ImageLoader imageLoader){
        ContactDialogFragment fragment = new ContactDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(CONTACT_KEY, contact);
        fragment.setArguments(bundle);
        fragment.setImageLoader(imageLoader);

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
            layout.findViewById(R.id.callButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContactHandler.callContact(getContext(), contact);
                }
            });

            layout.findViewById(R.id.smsButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContactHandler.smsContact(getContext(), contact);
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
                    ContactHandler.emailContact(getContext(), contact);
                }
            });
        }else{
            layout.findViewById(R.id.emailAddressContainer).setVisibility(View.GONE);
        }

        String imageUrl = contact.getImageUrl();
        ImageView contactImageView = (ImageView)layout.findViewById(R.id.contactImageView);
        NetworkImageView contactNetworkImageView = (NetworkImageView)layout.findViewById(R.id.contactNetworkImageView);
        if(imageUrl != null){
            if(imageUrl.startsWith("content://")) {
                contactImageView.setImageURI(Uri.parse(imageUrl));
            }else{
                contactImageView.setVisibility(View.GONE);
                contactNetworkImageView.setVisibility(View.VISIBLE);
                contactNetworkImageView.setImageUrl(imageUrl, imageLoader);
            }
        }

        builder.setView(layout);

        return builder.create();
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }
}