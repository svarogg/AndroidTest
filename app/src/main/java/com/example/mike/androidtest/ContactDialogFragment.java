package com.example.mike.androidtest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.mike.androidtest.adapters.ContactDetailsAdapter;
import com.example.mike.androidtest.adapters.ContactListAdapter;
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

        View layout = inflater.inflate(R.layout.contact_details_dialog, null);
        ((TextView)layout.findViewById(R.id.contactNameView)).setText(contact.getName());

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

        ContactDetailsAdapter contactDetailsAdapter = new ContactDetailsAdapter(getContext(), contact);
        ListView contactDetailsList = (ListView)layout.findViewById(R.id.contactDetailsList);
        contactDetailsList.setAdapter(contactDetailsAdapter);

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