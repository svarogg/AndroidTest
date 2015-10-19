package com.example.mike.androidtest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mike.androidtest.R;
import com.example.mike.androidtest.handlers.IntentHandler;
import com.example.mike.androidtest.model.Contact;

import java.util.List;

/**
 * Created by Mike on 19/10/2015.
 */
public class ContactDetailsAdapter extends BaseAdapter
{
    private List<String> phoneNumbers, emailAddresses;
    private int phoneNumbersCount, emailAddressesCount;
    private LayoutInflater inflater;
    private Context context;

    public ContactDetailsAdapter(Context context, Contact contact){
        this.context = context;

        phoneNumbers = contact.getPhoneNumbers();
        emailAddresses = contact.getEmailAddresses();

        phoneNumbersCount = phoneNumbers.size();
        emailAddressesCount = emailAddresses.size();

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return phoneNumbersCount + emailAddressesCount;
    }

    @Override
    public Object getItem(int position) {
        return position < phoneNumbersCount
                ? phoneNumbers.get(position)
                : emailAddresses.get(position - phoneNumbersCount);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return position < phoneNumbersCount
                ? getPhoneNumberView(position)
                : getEmailAddressView(position - phoneNumbersCount);
    }

    private View getEmailAddressView(int position) {
        final String emailAddress = emailAddresses.get(position);

        View rowView = inflater.inflate(R.layout.contact_detail_email_address, null);

        TextView emailAddressView = (TextView)rowView.findViewById(R.id.emailAddressView);
        emailAddressView.setText(emailAddress);

        ImageView emailButton = (ImageView)rowView.findViewById(R.id.emailButton);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHandler.sendEmail(context, emailAddress);
            }
        });

        return rowView;
    }

    private View getPhoneNumberView(int position) {
        final String phoneNumber = phoneNumbers.get(position);

        View rowView = inflater.inflate(R.layout.contact_detail_phone_number, null);

        TextView phoneNumberView = (TextView)rowView.findViewById(R.id.phoneNumberView);
        phoneNumberView.setText(phoneNumber);

        ImageView callButton = (ImageView)rowView.findViewById(R.id.callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHandler.call(context, phoneNumber);
            }
        });

        ImageView smsButton = (ImageView)rowView.findViewById(R.id.smsButton);
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHandler.sendSms(context, phoneNumber);
            }
        });

        return rowView;
    }
}
