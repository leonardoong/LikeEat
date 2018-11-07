package com.example.android.likeeatapplication.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.likeeatapplication.Model.Restaurant;
import com.example.android.likeeatapplication.R;

import java.util.ArrayList;

/**
 * Created by Leonardo on 10/16/2018.
 */

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantHolder> {

    private ArrayList<Restaurant> mData;
    private Activity mACtivity;

    public RestaurantAdapter(ArrayList<Restaurant> data, Activity activity) {
        this.mData = data;
        this.mACtivity = activity;
    }


    @NonNull
    @Override
    public RestaurantAdapter.RestaurantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_res, parent, false);
        return new RestaurantHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.RestaurantHolder holder, int position) {
        Restaurant restaurant = mData.get(position);

        holder.setName(restaurant.getName());
        holder.setAddress(restaurant.getAddress());
        holder.setRating(restaurant.getRating());


        Glide.with(mACtivity)
                .load(restaurant.getImageUrl())
                .into(holder.restaurantImageView);
    }

    @Override
    public int getItemCount() {
        if (mData == null)
            return 0;
        return mData.size();

    }

    public class RestaurantHolder extends RecyclerView.ViewHolder {
        ImageView restaurantImageView;
        TextView restaurantNameTextView;
        TextView restaurantAddressTextView;
        TextView restaurantRatingTextView;
        TextView distanceTextView;

        public RestaurantHolder(View itemView) {
            super(itemView);

            restaurantImageView = (ImageView) itemView.findViewById(R.id.imageview_restaurant);
            restaurantNameTextView = (TextView) itemView.findViewById(R.id.textview_restaurant_name);
            restaurantAddressTextView = (TextView) itemView.findViewById(R.id.restaurant_address_textview);
            restaurantRatingTextView = (TextView) itemView.findViewById(R.id.rating);

        }
        public void setName(String name) {
            restaurantNameTextView.setText(name);
        }

        public void setAddress(String address) {
            restaurantAddressTextView.setText(address);
        }

        public void setRating(String rating) {
            restaurantRatingTextView.setText(rating);
        }



    }
}
