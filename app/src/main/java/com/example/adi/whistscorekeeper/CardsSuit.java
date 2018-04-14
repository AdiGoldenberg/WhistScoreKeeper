package com.example.adi.whistscorekeeper;

/**
 * This class holds the image and the name for each cards suit
 */

public class CardsSuit {
    // States
    private String mName;
    private int mImageResourceId;

    // Constructor
    public CardsSuit(String name, int imageResourceId) {
        mName = name;
        mImageResourceId = imageResourceId;
    }

    /**
     * @return This method returns the name of the suit
     */
    public String getName() {
        return mName;
    }

    /**
     * @return This method returns the resource id number of the image
     */
    public int getImageResourceId() {
        return mImageResourceId;
    }
}
