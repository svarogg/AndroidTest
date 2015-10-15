package com.example.mike.androidtest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mike.androidtest.adapters.ContactListAdapter;
import com.example.mike.androidtest.handlers.ContactHandler;
import com.example.mike.androidtest.model.Contact;

import java.util.List;

public class ContactListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        final List<Contact> contacts = ContactHandler.getContacts(this);
        ListView contactsListView = (ListView)findViewById(R.id.contactsListView);
        contactsListView.setAdapter(new ContactListAdapter(this, contacts));

        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = contacts.get(position);
                ContactDialogFragment.create(contact).show(getSupportFragmentManager(), "contact");
            }
        });
    }
}
