package com.example.mike.androidtest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mike.androidtest.adapters.ContactListAdapter;
import com.example.mike.androidtest.functors.ObjToVoidFunctor;
import com.example.mike.androidtest.handlers.ContactHandler;
import com.example.mike.androidtest.model.Contact;

import java.io.IOException;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {
    private List<Contact> contacts;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_contact_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_load_from_server:
                loadContactsFromServer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void loadContactsFromServer() {
        final ContactListActivity context = this;
        ContactHandler.loadFromServer(this.getBaseContext(), new ObjToVoidFunctor<List<Contact>>() {
            @Override
            public void execute(List<Contact> contacts) {
                context.setContacts(contacts);
                ListView contactsListView = (ListView) findViewById(R.id.contactsListView);
                ContactListAdapter contactListAdapter = new ContactListAdapter(context, contacts);
                contactsListView.setAdapter(contactListAdapter);
                contactListAdapter.notifyDataSetChanged();
            }
        }, new ObjToVoidFunctor<Exception>() {
            @Override
            public void execute(Exception arg) {
                Toast.makeText(context, "Failed to load contacts from server.\nPlease check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final ContactListActivity context = this;
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_contact_list);

        contacts = ContactHandler.getContacts(this);
        ListView contactsListView = (ListView)findViewById(R.id.contactsListView);
        contactsListView.setAdapter(new ContactListAdapter(this, contacts));

        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = context.getContacts().get(position);
                ContactDialogFragment.create(contact).show(getSupportFragmentManager(), "contact");
            }
        });

        final View onDragMenu = findViewById(R.id.onDragMenu);

        onDragMenu.bringToFront();
        onDragMenu.setOnDragListener(new MainDragListener(onDragMenu));

        contactsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = context.getContacts().get(position);

                ClipData data = ClipData.newPlainText("phoneNumber", contact.getPhoneNumber());
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view.findViewById(R.id.contactImageView));
                view.startDrag(data, shadowBuilder, view, 0);

                onDragMenu.setVisibility(View.VISIBLE);
                return true;
            }
        });
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    private class MainDragListener implements View.OnDragListener{
        private final View onDragMenu;

        public MainDragListener(View onDragMenu) {
            this.onDragMenu = onDragMenu;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            if(event.getAction() == DragEvent.ACTION_DRAG_ENDED) {
                onDragMenu.setVisibility(View.GONE);
            }
            return true;
        }
    }
}
