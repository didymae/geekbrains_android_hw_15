package com.geekbrains.weather.ui.city;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geekbrains.weather.R;

import java.util.ArrayList;


public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private ArrayList<String> cityList = new ArrayList<>();
    private CreateActionFragment.OnHeadlineSelectedListener mCallback;
    private Context context;
    private ArrayList<String> selectedCities = new ArrayList<>();
    private Boolean isRemove = false;

    public CityAdapter (Context context, ArrayList<String> cityList, CreateActionFragment.OnHeadlineSelectedListener mCallback) {
        this.cityList = cityList;
        this.mCallback = mCallback;
        this.context = context;
    }

    @NonNull
    @Override
    public CityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CityAdapter.ViewHolder holder, final int position) {
        holder.textView.setText(cityList.get(position));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < selectedCities.size(); i++) {
                    if (selectedCities.get(i).equals(cityList.get(position))) {
                        holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                        selectedCities.remove(selectedCities.get(i));
                        mCallback.onArticleSelected(selectedCities);
                        isRemove = true;
                    }
                }
                if (!isRemove) {
                    holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.blue));
                    selectedCities.add(cityList.get(position));
                    mCallback.onArticleSelected(selectedCities);
                } else {
                    isRemove = false;
                }
            }
        });

        if (cityList.get(position).equals("Moscow")) {
           // holder.cardView.setCardBackgroundColor(Color.RED);
        }

    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public void setMoscowRed() {
        cityList.add("Tula");
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private LinearLayout linearLayout;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linearlayout_item);
            textView = itemView.findViewById(R.id.textview_item);
            cardView = itemView.findViewById(R.id.card_view);

        }
    }
}
