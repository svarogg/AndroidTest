package com.example.mike.androidtest.model;

import android.content.ContentResolver;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Mike on 14/10/2015.
 */
public class Contact implements Parcelable, Serializable {
    private int id;
    private String name;
    private List<String> phoneNumbers;
    private List<String> emailAddresses;
    private String imageUrl;

    private Loader emailLoader;
    private Loader phoneNumberLoader;

    protected Contact(Parcel in) {
        id = in.readInt();
        name = in.readString();
        phoneNumbers = in.readArrayList(String.class.getClassLoader());
        emailAddresses = in.readArrayList(String.class.getClassLoader());
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
        dest.writeList(phoneNumbers);
        dest.writeList(emailAddresses);
        dest.writeString(imageUrl);
    }

    public interface Loader{
        public List<String> loadList(int contactId);
    }

    public Contact(int id, String name, String imageUrl, String emailAddress, String phoneNumber){
        this.id = id;
        this.name = name;
        this.emailAddresses = emailAddress == null
                ? new LinkedList<String>()
                : new LinkedList<String>(Arrays.asList(emailAddress));
        this.phoneNumbers = phoneNumber == null
                ? new LinkedList<String>()
                : new LinkedList<>(Arrays.asList(phoneNumber));
        this.imageUrl = imageUrl;
    }

    public Contact(int id, String name, String imageUrl, Loader emailLoader, Loader phoneNumberLoader){
        this.id = id;
        this.name = name;
        this.emailLoader = emailLoader;
        this.phoneNumberLoader = phoneNumberLoader;
        this.imageUrl = imageUrl;
    }

    public List<String> getEmailAddresses() {
        if(emailAddresses == null){
            if(emailLoader != null) {
                setEmailAddresses(emailLoader.loadList(id));
            }else{
                setEmailAddresses(new LinkedList<String>());
            }
        }
        return emailAddresses;
    }

    public void setEmailAddresses(List<String> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    public List<String> getPhoneNumbers() {
        if(phoneNumbers == null){
            if(phoneNumberLoader != null) {
                setPhoneNumbers(phoneNumberLoader.loadList(id));
            }else{
                setPhoneNumbers(new LinkedList<String>());
            }
        }

        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
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

    public boolean hasPhoneNumber(){
        List<String> phoneNumbers = getPhoneNumbers();
        return phoneNumbers.size() > 0;
    }

    public boolean hasEmailAddress(){
        List<String> emailAddresses = getEmailAddresses();
        return emailAddresses.size() > 0;
    }

}
