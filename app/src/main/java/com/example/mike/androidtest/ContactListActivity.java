package com.example.mike.androidtest;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.mike.androidtest.adapters.ContactListAdapter;
import com.example.mike.androidtest.functors.ObjToVoidFunctor;
import com.example.mike.androidtest.functors.VoidToVoidFunctor;
import com.example.mike.androidtest.handlers.ContactHandler;
import com.example.mike.androidtest.model.Contact;

import java.util.List;

public class ContactListActivity extends AppCompatActivity {
    private List<Contact> contacts;
    private ImageLoader imageLoader;

    private ImageLoader prepareImageLoader(Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<>(10);
            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        };
        return new ImageLoader(requestQueue, imageCache);
    }

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
                ContactListAdapter contactListAdapter = new ContactListAdapter(context, imageLoader, contacts);
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
        super.onCreate(savedInstanceState);

        imageLoader = prepareImageLoader(this);

        final ContactListActivity context = this;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_contact_list);

        contacts = ContactHandler.getContacts(this);
        ListView contactsListView = (ListView)findViewById(R.id.contactsListView);
        contactsListView.setAdapter(new ContactListAdapter(this, imageLoader, contacts));

        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = context.getContacts().get(position);
                ContactDialogFragment.create(contact, context.imageLoader).show(getSupportFragmentManager(), "contact");
            }
        });

        final View onDragMenu = findViewById(R.id.onDragMenu);
        onDragMenu.bringToFront();
        onDragMenu.setOnDragListener(new MainDragListener(onDragMenu));

        contactsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Contact contact = context.getContacts().get(position);

                ClipData data = ClipData.newPlainText("phoneNumber", contact.getPhoneNumber());
                String imageUrl = contact.getImageUrl();
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view.findViewById(
                        imageUrl != null && !imageUrl.startsWith("content://")
                            ? R.id.contactNetworkImageView
                            : R.id.contactImageView));
                view.startDrag(data, shadowBuilder, view, 0);

                onDragMenu.setVisibility(View.VISIBLE);

                View dragButtonsContainer = onDragMenu.findViewById(R.id.dragButtonsContainer);
                final TextView dragText = (TextView) onDragMenu.findViewById(R.id.dragText);
                if(contact.hasPhoneNumber()) {
                    dragText.setText("");
                    dragButtonsContainer.setVisibility(View.VISIBLE);
                    View callButton = onDragMenu.findViewById(R.id.callDragButton);
                    View smsButton = onDragMenu.findViewById(R.id.smsDragButton);
                    setDragEvent(callButton, onDragMenu, dragText, "Call", new VoidToVoidFunctor() {
                        @Override
                        public void execute() {
                            ContactHandler.callContact(context, contact);
                        }
                    });
                    setDragEvent(smsButton, onDragMenu, dragText, "SMS", new VoidToVoidFunctor() {
                        @Override
                        public void execute() {
                            ContactHandler.smsContact(context, contact);
                        }
                    });
                }else{
                    dragButtonsContainer.setVisibility(View.GONE);
                    dragText.setText("No Phone Number");
                }

                return true;
            }
        });
    }

    private void setDragEvent(View button, final View onDragMenu, final TextView dragText, final String hoverText, final VoidToVoidFunctor dropCallback) {
        button.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                final int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_EXITED:
                        dragText.setText("");
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        dragText.setText(hoverText);
                        break;
                    case DragEvent.ACTION_DROP:
                        if(dropCallback!= null) {
                            dropCallback.execute();
                        }
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        dragText.setText("");
                        onDragMenu.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
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
