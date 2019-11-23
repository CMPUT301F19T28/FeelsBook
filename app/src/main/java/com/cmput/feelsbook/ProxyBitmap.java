package com.cmput.feelsbook;

import java.io.Serializable;
import android.graphics.Bitmap;

/**
 * // Reference: http://xperience57.blogspot.com/2015/09/android-saving-bitmap-as-serializable.html
 * gives bitmap the ability to become serializable
 */

public class ProxyBitmap implements Serializable {
        private final int [] pixels;
        private final int width , height;

        public ProxyBitmap(Bitmap bitmap){
            width = bitmap.getWidth();
            height = bitmap.getHeight();
            pixels = new int [width*height];
            bitmap.getPixels(pixels,0,width,0,0,width,height);
        }

        public Bitmap getBitmap(){
            return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
        }

    }




