package com.example.mike.androidtest.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
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
    private ImageLoader imageLoader;

    public ContactListAdapter(Context context, List<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = prepareImageLoader(context);
    }

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
        NetworkImageView contactNetworkImageView = (NetworkImageView)rowView.findViewById(R.id.contactNetworkImageView);

        contactNameView.setText(contact.getName());

        String imageUrl = contact.getImageUrl();
        if(imageUrl != null) {
            if(imageUrl.startsWith("content://")){
                contactImageView.setImageURI(Uri.parse(imageUrl));
            }else {
                contactNetworkImageView.setImageUrl(imageUrl, imageLoader);
                contactNetworkImageView.setVisibility(View.VISIBLE);
                contactImageView.setVisibility(View.GONE);
            }
        }

        return rowView;
    }
}
