package com.example.mike.androidtest.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mike.androidtest.R;
import com.example.mike.androidtest.model.Contact;

import java.util.List;

/**
 * Created by Mike on 14/10/2015.
 */
public class ContactListAdapter extends BaseAdapter {
    Context context;
    List<Contact> contacts;
    private static LayoutInflater inflater;

    public ContactListAdapter(Context context, List<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contact contact = contacts.get(position);

        View rowView = inflater.inflate(R.layout.contact_list, null);
        TextView contactNameView = (TextView)rowView.findViewById(R.id.contactNameView);
        ImageView contactImageView = (ImageView)rowView.findViewById(R.id.contactImageView);

        contactNameView.setText(contact.getName());

        String imageUrl = contact.getImageUrl();
        if(imageUrl != null) {
            contactImageView.setImageURI(Uri.parse(imageUrl));
        }

        return rowView;
    }
}
