package com.example.karan.traffikill.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.karan.traffikill.R;
import com.example.karan.traffikill.models.DataItemsHourly;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Karan on 31-07-2017.
 */

public class HourlyDataAdapter extends RecyclerView.Adapter<HourlyDataAdapter.WeatherHolder>
        implements View.OnClickListener {
    ArrayList<DataItemsHourly> hourlyData;
    Context context;
    private int originalHeight = 0;
    private boolean isViewExpanded = false;
    private DataItemsHourly referenceData;
    private String referenceDate;
    private String compareVal;
    private WeatherHolder referenceHolder;

    public HourlyDataAdapter(ArrayList<DataItemsHourly> hourlyData, Context context) {
        this.hourlyData = hourlyData;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public WeatherHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = li.inflate(R.layout.weather_list_item, parent, false);
        return new WeatherHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WeatherHolder holder, int position) {
        DataItemsHourly thisData = this.hourlyData.get(position);
        this.referenceData = thisData;
        this.referenceHolder = holder;
        holder.tvHumidity.setText("=" + thisData.getHumidity());
        holder.tvIntensity.setText("=" + thisData.getPrecipIntensity());
        holder.tvDewPoint.setText("=" + thisData.getDewPoint());
        Date date = new Date(Long.parseLong(thisData.getTime()) * 1000L);
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        DateFormat comparer = new SimpleDateFormat("hh:mm a");
        format.setTimeZone(TimeZone.getDefault());
        comparer.setTimeZone(TimeZone.getDefault());
        String formatted = format.format(date);
        this.referenceDate = formatted;
        this.compareVal = comparer.format(date);
        holder.tvTime.setText(formatted);
        double precipProbability = Double.parseDouble(thisData.getPrecipProbability()) * 100;
        precipProbability = Math.round(precipProbability * 100.0) / 100.0;
        holder.tvPercentageTraffic.setText(precipProbability + "% chances of heavy traffic");
        holder.tvProbability.setText(precipProbability + "");
        holder.tvTempValue.setText(thisData.getTemperature() + "");
        holder.tvSummary.setText(thisData.getSummary());
        HourlyDataAdapter.this.referenceHolder.childView.setVisibility(View.INVISIBLE);
        HourlyDataAdapter.this.referenceHolder.childView.setEnabled(false);
        Drawable d = HourlyDataAdapter.this.referenceHolder.weather_list_item.getBackground();
        HourlyDataAdapter.this.referenceHolder.backgroundContainer.setBackground(d);
        setBackground(HourlyDataAdapter.this.referenceHolder.clickToExpand);
        holder.clickToExpand.setOnClickListener(this);
    }

    public void setBackground(View view) {
        DateFormat comparer = new SimpleDateFormat("hh:mm a");
        if (this.referenceDate.contains("AM")) {
            try {
                Date end = comparer.parse(compareVal);
                if (end.before(comparer.parse("11:59 AM"))) {
                    if (this.referenceData.getSummary().toLowerCase().contains("cloudy") ||
                            this.referenceData.getSummary().toLowerCase().contains("overcast")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Drawable drawable = this.context.getDrawable(R.drawable.cloudy_morning);
                            drawable.setAlpha(99);
                            view.setBackground(drawable);
                        }

                    } else if (this.referenceData.getSummary().toLowerCase().contains("rain") ||
                            this.referenceData.getSummary().toLowerCase().contains("rainy")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Drawable drawable = this.context.getDrawable(R.drawable.rainy_morning);
                            drawable.setAlpha(99);
                            view.setBackground(drawable);
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Drawable drawable = this.context.getDrawable(R.drawable.clear_morning);
                            drawable.setAlpha(99);
                            view.setBackground(drawable);
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (this.referenceDate.contains("PM")) {
            try {
                Date end = comparer.parse(compareVal);
                if (end.before(comparer.parse("7:00 PM"))) {
                    if (this.referenceData.getSummary().toLowerCase().contains("cloudy") ||
                            this.referenceData.getSummary().toLowerCase().contains("overcast")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Drawable drawable = this.context.getDrawable(R.drawable.cloudy_afternoon);
                            drawable.setAlpha(99);
                            view.setBackground(drawable);
                        }
                    } else if (this.referenceData.getSummary().toLowerCase().contains("rain") ||
                            this.referenceData.getSummary().toLowerCase().contains("rainy")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Drawable drawable = this.context.getDrawable(R.drawable.rainy_afternoon);
                            drawable.setAlpha(99);
                            view.setBackground(drawable);
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Drawable drawable = this.context.getDrawable(R.drawable.clear_afternoon);
                            drawable.setAlpha(99);
                            view.setBackground(drawable);
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (this.referenceDate.contains("PM")) {
            try {
                Date end = comparer.parse(compareVal);
                if (end.after(comparer.parse("7:00PM"))) {
                    if (this.referenceData.getSummary().toLowerCase().contains("cloudy") ||
                            this.referenceData.getSummary().toLowerCase().contains("overcast")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Drawable drawable = this.context.getDrawable(R.drawable.cloudy_night);
                            drawable.setAlpha(99);
                            view.setBackground(drawable);
                        }
                    } else if (this.referenceData.getSummary().toLowerCase().contains("rain") ||
                            this.referenceData.getSummary().toLowerCase().contains("rainy")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Drawable drawable = this.context.getDrawable(R.drawable.rainy_night);
                            drawable.setAlpha(99);
                            view.setBackground(drawable);
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Drawable drawable = this.context.getDrawable(R.drawable.clear_night);
                            drawable.setAlpha(99);
                            view.setBackground(drawable);
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.hourlyData.size();
    }

    @Override
    public void onClick(final View v) {
        if (originalHeight == 0) {
            originalHeight = v.getHeight();
        }
        ValueAnimator valueAnimator;
        if (!HourlyDataAdapter.this.isViewExpanded) {
            HourlyDataAdapter.this.referenceHolder.childView.setVisibility(View.VISIBLE);
            HourlyDataAdapter.this.referenceHolder.childView.setEnabled(true);
            HourlyDataAdapter.this.isViewExpanded = true;
            valueAnimator = ValueAnimator.ofInt(originalHeight, originalHeight + (int) (originalHeight * 2.0));
            valueAnimator.setDuration(500);
//            Drawable d= HourlyDataAdapter.this.referenceHolder.weather_list_item.getBackground();
            HourlyDataAdapter.this.referenceHolder.clickToExpand.setBackgroundResource(0);
            setBackground(HourlyDataAdapter.this.referenceHolder.backgroundContainer);
        } else {
            HourlyDataAdapter.this.referenceHolder.childView.setVisibility(View.INVISIBLE);
            HourlyDataAdapter.this.referenceHolder.childView.setEnabled(false);
            HourlyDataAdapter.this.isViewExpanded = false;
            valueAnimator = ValueAnimator.ofInt(originalHeight + (int) (originalHeight * 2.0), originalHeight);
            valueAnimator.setDuration(500);
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    Drawable d = HourlyDataAdapter.this.referenceHolder.weather_list_item.getBackground();
                    HourlyDataAdapter.this.referenceHolder.backgroundContainer.setBackground(d);
                    setBackground(HourlyDataAdapter.this.referenceHolder.clickToExpand);
                }
            });
            Animation a = new AlphaAnimation(1.00f, 0.00f);
            a.setDuration(500);
            a.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    HourlyDataAdapter.this.referenceHolder.childView.setVisibility(View.INVISIBLE);
                    HourlyDataAdapter.this.referenceHolder.childView.setEnabled(false);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            HourlyDataAdapter.this.referenceHolder.childView.startAnimation(a);

        }
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                v.getLayoutParams().height = value.intValue();
                v.requestLayout();
            }
        });
        valueAnimator.start();
    }

    public class WeatherHolder extends RecyclerView.ViewHolder {
        TextView tvTempValue, tvPercentageTraffic, tvSummary, tvProbability, tvIntensity, tvHumidity, tvDewPoint, tvTime;
        LinearLayout backgroundContainer, childView, weather_list_item, clickToExpand;

        public WeatherHolder(View itemView) {
            super(itemView);
            this.tvHumidity = (TextView) itemView.findViewById(R.id.tvHumidity);
            this.tvSummary = (TextView) itemView.findViewById(R.id.tvSummary);
            this.tvDewPoint = (TextView) itemView.findViewById(R.id.tvDewPoint);
            this.tvTempValue = (TextView) itemView.findViewById(R.id.tvTempValue);
            this.tvPercentageTraffic = (TextView) itemView.findViewById(R.id.tvPercentageTraffic);
            this.tvProbability = (TextView) itemView.findViewById(R.id.tvProbability);
            this.tvIntensity = (TextView) itemView.findViewById(R.id.tvIntensity);
            this.tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            this.backgroundContainer = (LinearLayout) itemView.findViewById(R.id.backgroundContainer);
            this.childView = (LinearLayout) itemView.findViewById(R.id.childView);
            this.clickToExpand = (LinearLayout) itemView.findViewById(R.id.clickToExpand);
            this.weather_list_item = (LinearLayout) itemView.findViewById(R.id.weather_list_item);
        }
    }
}

