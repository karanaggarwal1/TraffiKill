package com.example.karan.traffikill.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.karan.traffikill.R;
import com.example.karan.traffikill.models.ResultData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Karan on 01-08-2017.
 */

public class NearbyPlaceAdapter extends RecyclerView.Adapter<NearbyPlaceAdapter.NearbyPlaceHolder> {

    private ArrayList<ResultData> itemList;
    private Context context;

    public NearbyPlaceAdapter(Context context, ArrayList<ResultData> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public NearbyPlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutView = layoutInflater.inflate(R.layout.nearby_place_list_item, null);
        NearbyPlaceHolder rcv = new NearbyPlaceHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(NearbyPlaceHolder holder, final int position) {
        Picasso.with(context)
                .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&key=AIzaSyCcWcxyuUpiemoDQzHGzJx-yd5jW0Pwt14" +
                        "&maxwidth=260&photoreference=" +
                        (this.itemList.get(position).getPhotosData() != null ?
                                this.itemList.get(position).getPhotosData().get(0).getPhotoReference() : ""))
                .fit()
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(holder.placePhoto);
        holder.placeName.setText(this.itemList.get(position).getName());
        holder.ratingBar.setRating(this.itemList.get(position).getRating());
        holder.showInMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" +
                        NearbyPlaceAdapter.this.itemList.get(position).getGeometry().getLocationData().getLatitude() + "," +
                        NearbyPlaceAdapter.this.itemList.get(position).getGeometry().getLocationData().getLongitude() +
                        "(" + NearbyPlaceAdapter.this.itemList.get(position).getName() + ")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                NearbyPlaceAdapter.this.context.startActivity(mapIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public void updateData(ArrayList<ResultData> restaurantList) {
        this.itemList.clear();
        this.itemList.addAll(restaurantList);
        notifyDataSetChanged();
    }

    public class NearbyPlaceHolder extends RecyclerView.ViewHolder {
        ImageView placePhoto;
        TextView placeName;
        RatingBar ratingBar;
        TextView showInMaps;

        public NearbyPlaceHolder(View itemView) {
            super(itemView);
            this.placePhoto = (ImageView) itemView.findViewById(R.id.iv_place_photo);
            this.placeName = (TextView) itemView.findViewById(R.id.place_name);
            this.ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            this.showInMaps = (TextView) itemView.findViewById(R.id.tvMaps);
        }
    }
}
