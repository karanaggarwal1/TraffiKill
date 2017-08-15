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


public class NearbyHotelsAdapter extends RecyclerView.Adapter<NearbyHotelsAdapter.HotelHolder> {

    private ArrayList<ResultData> itemList;
    private Context context;

    public NearbyHotelsAdapter(Context context, ArrayList<ResultData> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public HotelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutView = layoutInflater.inflate(R.layout.nearby_hotel_list_item, null);
        return new HotelHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(HotelHolder holder, int position) {
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
        final int x = position;
        holder.showInMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" +
                        NearbyHotelsAdapter.this.itemList.get(x).getGeometry().getLocationData().getLatitude() + "," +
                        NearbyHotelsAdapter.this.itemList.get(x).getGeometry().getLocationData().getLongitude() +
                        "(" + NearbyHotelsAdapter.this.itemList.get(x).getName() + ")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                NearbyHotelsAdapter.this.context.startActivity(mapIntent);
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

    public class HotelHolder extends RecyclerView.ViewHolder {
        ImageView placePhoto;
        TextView placeName;
        RatingBar ratingBar;
        TextView showInMaps;

        public HotelHolder(View itemView) {
            super(itemView);
            this.placePhoto = (ImageView) itemView.findViewById(R.id.iv_place_photo);
            this.placeName = (TextView) itemView.findViewById(R.id.place_name);
            this.ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            this.showInMaps = (TextView) itemView.findViewById(R.id.tvMaps);
        }
    }
}
