package com.example.mike.androidtest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

        final View onDragMenu = findViewById(R.id.onDragMenu);

        onDragMenu.bringToFront();
        onDragMenu.setOnDragListener(new MainDragListener(onDragMenu));

        contactsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = contacts.get(position);

                ClipData data = ClipData.newPlainText("phoneNumber", contact.getPhoneNumber());
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view.findViewById(R.id.contactImageView));
                view.startDrag(data, shadowBuilder, view, 0);

                onDragMenu.setVisibility(View.VISIBLE);
                return true;
            }
        });
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
