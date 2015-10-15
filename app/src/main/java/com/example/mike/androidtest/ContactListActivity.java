package com.example.mike.androidtest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mike.androidtest.adapters.ContactListAdapter;
import com.example.mike.androidtest.handlers.ContactHandler;
import com.example.mike.androidtest.model.Contact;

import java.util.List;

public class ContactListActivity extends AppCompatActivity {
    public static class ContactDialogFragment extends DialogFragment {
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
            Contact contact = (Contact)getArguments().getSerializable(CONTACT_KEY);
            LayoutInflater inflater = getActivity().getLayoutInflater();

            View layout = inflater.inflate(R.layout.contact_details, null);
            ((TextView)layout.findViewById(R.id.contactNameView)).setText(contact.getName());

            String phoneNumber = contact.getPhoneNumber();
            if(phoneNumber!=null){
                ((TextView)layout.findViewById(R.id.phoneNumberView)).setText(phoneNumber);
            }else{
                layout.findViewById(R.id.phoneNumberContainer).setVisibility(View.GONE);
            }

            String emailAddress = contact.getEmailAddress();
            if(emailAddress!=null){
                ((TextView)layout.findViewById(R.id.emailAddressView)).setText(emailAddress);
            }else{
                layout.findViewById(R.id.emailAddressContainer).setVisibility(View.GONE);
            }
            
            builder.setView(layout);

            return builder.create();
        }
    }

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
