package com.geekbrains.weather.ui.temperature;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.geekbrains.weather.R;

import java.util.List;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private Context context;
    private List<String> tempHistory;
    private List<String>  days;
    private TextView addText;
    private Button addButton;

    public CustomAdapter(Context context, List<String> tempHistory, List<String> days) {
        this.tempHistory = tempHistory;
        this.context = context;
        this.days = days;
    }

    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.temperature_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomAdapter.ViewHolder holder, final int position) {
        holder.textViewT.setText(tempHistory.get(position));
        holder.textViewD.setText(days.get(position));

    }

    @Override
    public int getItemCount() {
        return tempHistory.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewT;
        private TextView textViewD;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewT = itemView.findViewById(R.id.textview_item_t);
            textViewD = itemView.findViewById(R.id.textview_item_day);
        }
    }
}
