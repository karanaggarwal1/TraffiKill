package com.example.karan.traffikill.Adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.karan.traffikill.R;
import com.example.karan.traffikill.models.CurrentData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Karan on 31-07-2017.
 */

public class WeatherItemsAdapter extends RecyclerView.Adapter<WeatherItemsAdapter.WeatherHolder> {
    ArrayList<CurrentData> hourlyList;
    ArrayList<CurrentData> currentList;
    String type;
    Context context;
    View view;

    public WeatherItemsAdapter(Context context) {
        this.context = context;
        this.hourlyList = new ArrayList<>();
        this.currentList = new ArrayList<>();
    }

    public void setView(View view) {
        this.view = view;
    }

    public void updateData(ArrayList<CurrentData> viewModels, String type) {

        this.hourlyList.clear();
        this.currentList.clear();
        this.type = type;
        if (type.equals("hourly"))
            this.hourlyList.addAll(viewModels);
        else
            this.currentList.addAll(viewModels);
        notifyDataSetChanged();
    }


    @Override
    public WeatherHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = li.inflate(R.layout.weather_list_item, parent, false);
        return new WeatherHolder(itemView);
    }

    public void setIcon(int position, ArrayList<CurrentData> currentList, WeatherHolder holder) {
        if (currentList.get(position).getIcon().equals("clear-day")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.ivWeatherIcon.setImageDrawable(this.context.getDrawable(R.drawable.clear_day_icon));
            }
        } else if (currentList.get(position).getIcon().equals("clear-night")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.ivWeatherIcon.setImageDrawable(this.context.getDrawable(R.drawable.clear_night_icon));
            }
        } else if (currentList.get(position).getIcon().equals("cloudy")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.ivWeatherIcon.setImageDrawable(this.context.getDrawable(R.drawable.cloudy_icon));
            }
        } else if (currentList.get(position).getIcon().equals("partly-clear-day")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.ivWeatherIcon.setImageDrawable(this.context.getDrawable(R.drawable.partly_cloudy_day_icon));
            }
        } else if (currentList.get(position).getIcon().equals("partly-clear-night")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.ivWeatherIcon.setImageDrawable(this.context.getDrawable(R.drawable.clear_night_icon));
            }
        } else if (currentList.get(position).getIcon().equals("fog")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.ivWeatherIcon.setImageDrawable(this.context.getDrawable(R.drawable.fog_icon));
            }
        } else if (currentList.get(position).getIcon().equals("rain")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.ivWeatherIcon.setImageDrawable(this.context.getDrawable(R.drawable.rain_icon));
            }
        }
    }

    @Override
    public void onBindViewHolder(WeatherHolder holder, int position) {
        if (!this.type.equals("hourly")) {
            final int x = position;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WeatherItemsAdapter.this.view.findViewById(R.id.weatherPreview).setVisibility(View.VISIBLE);
                    DateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                    String[] parts = format.format(new Date(Long.parseLong(WeatherItemsAdapter.
                            this.currentList.get(x).getTime()) * 1000L)).split(" ");
                    String time = parts[0] + " " + parts[1] + " " + parts[2];
                    format = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                    DateFormat newFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String formatted = newFormat.format(new Date(Long.parseLong(WeatherItemsAdapter.
                            this.currentList.get(x).getTime()) * 1000L));
                    format.setTimeZone(TimeZone.getDefault());
                    Date d = null;
                    try {
                        d = format.parse(time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (d.after(format.parse(formatted + " " + "7:00 PM"))) {
                            WeatherItemsAdapter.this.view.setBackgroundResource(R.drawable.night_sky);
                        } else {
                            WeatherItemsAdapter.this.view.setBackgroundResource(R.drawable.clear_sky);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    ((TextView) (WeatherItemsAdapter.this.view.findViewById(R.id.tvTempBig))).
                            setText(WeatherItemsAdapter.this.currentList.get(x).getTemperature() + " °C");
                    ((TextView) (WeatherItemsAdapter.this.view.findViewById(R.id.tvSummary))).
                            setText(WeatherItemsAdapter.this.currentList.get(x).getSummary());
                    ((TextView) (WeatherItemsAdapter.this.view.findViewById(R.id.tvTime))).
                            setText(format.format(new Date(Long.parseLong(WeatherItemsAdapter.
                                    this.currentList.get(x).getTime()) * 1000L)));
                    ((TextView) (WeatherItemsAdapter.this.view.findViewById(R.id.tvProbability))).
                            setText("It is " + Math.round(Double.parseDouble(WeatherItemsAdapter.
                                    this.currentList.get(x).getPrecipProbability()) * 10000.0) / 100.0 + "" +
                                    "% likely to get stuck in traffic");
                    ((TextView) (WeatherItemsAdapter.this.view.findViewById(R.id.tvHumidity))).
                            setText("Humidity = " + Math.round(Double.parseDouble(WeatherItemsAdapter.this.currentList.get(x).getHumidity()) * 10000.0) / 100.0 + "%");
                    ((TextView) (WeatherItemsAdapter.this.view.findViewById(R.id.tvPrecipIntensity))).
                            setText("It might rain as intense as " + WeatherItemsAdapter.this.currentList.get(x).getPrecipIntensity() + " inches of water per hour ");
                }
            });
            if (position == 0) {
                holder.itemView.performClick();
            }
            holder.tvTempValue.setText(Math.round(this.currentList.get(position).getTemperature() * 100.0) / 100.0 + "°C");
            setIcon(position, this.currentList, holder);
            DateFormat format = new SimpleDateFormat("hh:mm a");
            format.setTimeZone(TimeZone.getDefault());
            holder.tvTime.setText(format.format(new Date(Long.parseLong(this.currentList.get(position).getTime()) * 1000L)));
        } else {
            final int x = position;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WeatherItemsAdapter.this.view.findViewById(R.id.weatherPreview).setVisibility(View.VISIBLE);
                    DateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                    String[] parts = format.format(new Date(Long.parseLong(WeatherItemsAdapter.
                            this.hourlyList.get(x).getTime()) * 1000L)).split(" ");
                    String time = parts[1] + " " + parts[2];
                    format = new SimpleDateFormat("hh:mm a");
                    DateFormat newFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String formatted = newFormat.format(new Date(Long.parseLong(WeatherItemsAdapter.
                            this.hourlyList.get(x).getTime()) * 1000L));
                    format.setTimeZone(TimeZone.getDefault());
                    Date d = null;
                    try {
                        d = format.parse(time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (d.after(format.parse(formatted + " " + "7:00 PM")) && d.before(format.parse(formatted + " " + "5:00 AM"))) {
                            if (d.before(format.parse(formatted + " " + "11:59 PM")) ||
                                    (d.after(format.parse(formatted + " 12:00 AM")) &&
                                            d.before(format.parse(formatted + " 5:00 AM"))))
                            WeatherItemsAdapter.this.view.setBackgroundResource(R.drawable.night_sky);
                        } else {
                            WeatherItemsAdapter.this.view.setBackgroundResource(R.drawable.clear_sky);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    ((TextView) (WeatherItemsAdapter.this.view.findViewById(R.id.tvTempBig))).
                            setText(WeatherItemsAdapter.this.hourlyList.get(x).getTemperature() + "");
                    ((TextView) (WeatherItemsAdapter.this.view.findViewById(R.id.tvSummary))).
                            setText(WeatherItemsAdapter.this.hourlyList.get(x).getSummary());
                    ((TextView) (WeatherItemsAdapter.this.view.findViewById(R.id.tvTime))).
                            setText(format.format(new Date(Long.parseLong(WeatherItemsAdapter.
                                    this.hourlyList.get(x).getTime()) * 1000L)));
                    ((TextView) (WeatherItemsAdapter.this.view.findViewById(R.id.tvProbability))).
                            setText("It is " + Math.round(Double.parseDouble(WeatherItemsAdapter.
                                    this.hourlyList.get(x).getPrecipProbability()) * 10000.0) / 100.0 + "" +
                                    "% likely to get stuck in traffic");
                    ((TextView) (WeatherItemsAdapter.this.view.findViewById(R.id.tvHumidity))).
                            setText("Humidity = " + WeatherItemsAdapter.this.hourlyList.get(x).getHumidity());
                    ((TextView) (WeatherItemsAdapter.this.view.findViewById(R.id.tvPrecipIntensity))).
                            setText("Precipitation Intensity = " + WeatherItemsAdapter.this.hourlyList.get(x).getSummary());
                }
            });
            if (position == 0) {
                holder.itemView.performClick();
            }
            holder.tvTempValue.setText(Math.round(this.hourlyList.get(position).getTemperature() * 100.0) / 100.0 + "°C");
            setIcon(position, this.hourlyList, holder);
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            format.setTimeZone(TimeZone.getDefault());
            holder.tvTime.setText(format.format(new Date(Long.parseLong(this.hourlyList.get(position).getTime()) * 1000L)));
        }

    }

    @Override
    public int getItemCount() {
        if (this.type.equals("hourly")) {
            return this.hourlyList.size();
        } else {
            return this.currentList.size();
        }
    }

    public void setType(String type) {
        this.type = type;
    }


    public class WeatherHolder extends RecyclerView.ViewHolder {
        TextView tvTempValue, tvTime;
        ImageView ivWeatherIcon;
        View itemView;

        public WeatherHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.ivWeatherIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            this.tvTempValue = (TextView) itemView.findViewById(R.id.tvTempValue);
            this.tvTime = (TextView) itemView.findViewById(R.id.tvTime);
        }
    }
}
