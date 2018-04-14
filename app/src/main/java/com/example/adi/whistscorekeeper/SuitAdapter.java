package com.example.adi.whistscorekeeper;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ADI on 10/04/2018.
 */

public class SuitAdapter extends ArrayAdapter<CardsSuit> {
    /**
     * This is a custom constructor. The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists.
     *
     * @param context       the activity context
     * @param cardsSuitList an ArrayList array with Words as the stored objects
     */
    public SuitAdapter(Activity context, ArrayList<CardsSuit> cardsSuitList) {
        // Initializing the ArrayAdapter's internal storage for the context and the list.
        super(context, 0, cardsSuitList);
    }

    /**
     * This override returns a view for an AdapterView when the spinner is created.
     * It does so by calling the getCustomView method.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    /**
     * This override returns a view for an AdapterView when the user clicks on the already created spinner.
     * It does so by calling the getCustomView method.
     */
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    /**
     * This methods returns a view for an AdapterView. It's called in getView and in getDropDownView.
     *
     * @param position    Index of position the Word ListArray that should be displayed.
     * @param convertView The recycled view to alter.
     * @param parent      The parent ViewGroup used for inflation.
     * @return The View for the next position in the AdapterView.
     */
    public View getCustomView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.spinner_item, parent, false);
        }

        // Get the Word object located at this position in the list
        CardsSuit currentSuit = getItem(position);

        // Find the ImageView in the spinner_list.xml layout where the image should go to
        ImageView suitImageView = (ImageView) listItemView.findViewById(R.id.suitImage);
        // Get the image resource id from the current CardsSuit object and set this to be the image
        suitImageView.setImageResource(currentSuit.getImageResourceId());

        // Find the TextView in the spinner_list.xml layout where the suit name should go
        TextView suitNameView = (TextView) listItemView.findViewById(R.id.suitName);
        // Get the suit name from the current CardsSuit object and set this text on the suitNameView
        suitNameView.setText(currentSuit.getName());

        // Return the list item layout
        return listItemView;
    }
}
