package com.example.mike.androidtest.handlers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Mike on 19/10/2015.
 */
public class BitmapHandler {
    public static Bitmap downloadBitmap(String imageUrl) throws IOException {
        URL imageUrlObject = new URL(imageUrl);
        return BitmapFactory.decodeStream(imageUrlObject.openConnection().getInputStream());
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight){
        int currentWidth = bitmap.getWidth();
        int currentHeight = bitmap.getHeight();

        double scaleFactor = Math.min((double)maxWidth/currentWidth, (double)maxHeight/currentHeight);
        if(scaleFactor > 1)
            return bitmap;

        int targetWidth = (int)(currentWidth * scaleFactor);
        int targetHeight = (int)(currentHeight * scaleFactor);

        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false);
    }
}
