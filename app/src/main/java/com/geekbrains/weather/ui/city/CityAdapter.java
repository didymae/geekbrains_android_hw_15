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

        import com.geekbrains.weather.Constants;
        import com.geekbrains.weather.data.PrefsData;
        import com.geekbrains.weather.data.PrefsHelper;
        import com.geekbrains.weather.R;
        import com.geekbrains.weather.model.SelectedCity;
        import com.geekbrains.weather.ui.base.BaseActivity;

        import java.util.ArrayList;



public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private ArrayList<SelectedCity> cityList = new ArrayList<>();
    private CreateActionFragment.OnHeadlineSelectedListener mCallback;
    private Context context;
    private ArrayList<String> selectedCities = new ArrayList<>();
    private Boolean isRemoove = false;
    private PrefsHelper prefsHelper;

    public CityAdapter (Context context, ArrayList<SelectedCity> cityList, CreateActionFragment.OnHeadlineSelectedListener mCallback, BaseActivity baseActivity) {
        this.cityList = cityList;
        this.mCallback = mCallback;
        this.context = context;
        prefsHelper = new PrefsData(baseActivity);
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
        final SelectedCity selectedCity = cityList.get(position);
        holder.textView.setText(selectedCity.getCity());
        if (selectedCity.getSelected()) {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.blue));
        } else {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelected(selectedCity);
            }
        });

    }

    private void setSelected(SelectedCity selectedCity) {
        int pos = 5;
        for (int i = 0; i < cityList.size(); i++) {
            if (cityList.get(i).getCity().equals(selectedCity.getCity())) {
                pos = i;
                if (!cityList.get(i).getSelected()) {
                    cityList.get(i).setSelected(true);
                } else {
                    cityList.get(i).setSelected(false);
                }
            }
        }

        for (int i = 0; i < cityList.size(); i++) {
            if (i != pos) {
                cityList.get(i).setSelected(false);
            }
        }

        if (selectedCity.getSelected()) {
            saveInPref(selectedCity.getCity());
        } else {
            deleteInPref();
        }


        notifyDataSetChanged();
    }

    private void deleteInPref() {
        prefsHelper.deleteSharedPreferences(Constants.CITY);
    }

    private void saveInPref(String city) {
        prefsHelper.saveSharedPreferences(Constants.CITY, city);
    }

    @Override
    public int getItemCount() {
        return cityList.size();
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
