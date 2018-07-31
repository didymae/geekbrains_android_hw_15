package com.geekbrains.weather.ui.temperature;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geekbrains.weather.R;
import com.geekbrains.weather.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TemperatureFragment extends BaseFragment {

        //объявление переменных
        private RecyclerView recyclerView;
        private List<String> tempHistory;
        private List<String>  days;
        private EditText textAddT;
        private EditText textAddD;



        @Override
        public void onAttach (Context context){
        super.onAttach(context);
        Toast.makeText(getContext(), "onAttachAction", Toast.LENGTH_SHORT).show();

        }


        @Nullable
        @Override
        public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup
        container, @Nullable Bundle savedInstanceState){
        //обращаемся к layout который будет содержать наш фрагмент
        return inflater.inflate(R.layout.temperature_fragment, container, false);
    }

        @Override
        public void onResume () {
        super.onResume();
    }

        @Override
        protected void initLayout (View view, Bundle savedInstanceState){
        initTemperatureList();
        Button buttonAdd = view.findViewById(R.id.add_button);
         textAddT = (EditText) view.findViewById(R.id.add_text_t);
         textAddD = (EditText) view.findViewById(R.id.add_text_d);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        final CustomAdapter customAdapter = new CustomAdapter(getContext(),tempHistory,days );
        recyclerView.setAdapter(customAdapter);

            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = customAdapter.getItemCount();
                    tempHistory.add(position,textAddT.getText().toString().trim());
                    days.add(position,textAddD.getText().toString().trim());
                    customAdapter.notifyItemInserted(customAdapter.getItemCount());


                }
            });

    }

        private void initTemperatureList () {
             tempHistory = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.temp_history)));
             days = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.days)));

        }
    }

