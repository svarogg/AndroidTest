package com.example.mike.androidtest.model;

import android.content.ContentResolver;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Mike on 14/10/2015.
 */
public class Contact implements Parcelable, Serializable {
    private int id;
    private String name;
    private String phoneNumber;
    private String emailAddress;
    private String imageUrl;

    private Loader emailLoader;
    private Loader phoneNumberLoader;

    protected Contact(Parcel in) {
        id = in.readInt();
        name = in.readString();
        phoneNumber = in.readString();
        emailAddress = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeString(emailAddress);
        dest.writeString(imageUrl);
    }

    public interface Loader{
        public String loadString(int contactId);
    }

    public Contact(int id, String name, String imageUrl, Loader emailLoader, Loader phoneNumberLoader){
        this.id = id;
        this.name = name;
        this.emailLoader = emailLoader;
        this.phoneNumberLoader = phoneNumberLoader;
        this.imageUrl = imageUrl;
    }

    public String getEmailAddress() {
        if(emailAddress == null){
            setEmailAddress(emailLoader.loadString(id));
        }
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        if(phoneNumber == null){
            setPhoneNumber(phoneNumberLoader.loadString(id));
        }

        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
