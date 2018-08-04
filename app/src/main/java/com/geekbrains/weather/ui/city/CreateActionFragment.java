package com.geekbrains.weather.ui.city;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.geekbrains.weather.R;
import com.geekbrains.weather.model.SelectedCity;
import com.geekbrains.weather.ui.temperature.TemperatureFragment;
import com.geekbrains.weather.ui.weather.WeatherFragment;
import com.geekbrains.weather.ui.base.BaseFragment;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;



public class CreateActionFragment extends BaseFragment {

    private EditText editTextCountry;
    private EditText editTextName;
    OnHeadlineSelectedListener mCallback;
    Intent intent;
    String country;
    TextView textViewName;
    private RecyclerView recyclerView;
    private ArrayList<SelectedCity> cityList;
    private TextInputLayout textInputLayout;




    public interface OnHeadlineSelectedListener {
        void onArticleSelected(ArrayList<String> position);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.create_action_fragment, container, false);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Toast.makeText(getContext(), "onAttachAction", Toast.LENGTH_SHORT).show();

        try {
            mCallback = (OnHeadlineSelectedListener) getBaseActivity().getAnotherFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getBaseActivity().getAnotherFragment().toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Toast.makeText(getContext(), "onDetachAction", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void initLayout(View view, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        initCountryList();

        textInputLayout = view.findViewById(R.id.text_input);

        recyclerView = view.findViewById(R.id.list_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        CityAdapter cityAdapter = new CityAdapter(getContext(), cityList, mCallback,getBaseActivity());
        recyclerView.setAdapter(cityAdapter);

        //инициализация edittext и листенер на ключи при взаимодействии с ним, когда мы нашимаем enter у нас опускается клавиатура и запускается WeatherFragment
        editTextCountry = (EditText) view.findViewById(R.id.et_country);
        editTextName = (EditText) view.findViewById(R.id.etname);
        intent = getActivity().getIntent();
        Button saveButton = (Button) view.findViewById(R.id.save);
        Button historyButton = (Button) view.findViewById(R.id.t_history);

        editTextCountry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    country = editTextCountry.getText().toString();
                    if (country.matches("(^[a-zA-Z]+)")) {
                        hideError();
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    } else
                        showError();
                      InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                      imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String name = editTextName.getText().toString().trim();
                            intent.putExtra("name", name);
                            textViewName = (TextView) getBaseActivity().findViewById(R.id.tvUsername);
                            textViewName.setText(name);
                            getBaseActivity().replaceMainFragment(new WeatherFragment());


                        }
                    });

        historyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getBaseActivity().replaceMainFragment(new TemperatureFragment());

                        }
                    });
                }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.create_action_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_add:
                Toasty.success(getContext(), "Success menu!!").show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        Toast.makeText(getContext(), "onStartAction", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getContext(), "onResumeAction", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        Toast.makeText(getContext(), "onPauseAction", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        Toast.makeText(getContext(), "onStopAction", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toast.makeText(getContext(), "onDestroyAction", Toast.LENGTH_SHORT).show();
    }
    private void showError() {
        textInputLayout.setError("Error!!");
    }

    private void hideError() {
        textInputLayout.setError("");
    }

    private void initCountryList() {
        cityList = new ArrayList<>();
        SelectedCity selectedCity1 = new SelectedCity();
        selectedCity1.setCity("Moscow");
        selectedCity1.setSelected(false);

        SelectedCity selectedCity2 = new SelectedCity();
        selectedCity2.setCity("Saint Petersburg");
        selectedCity2.setSelected(false);

        SelectedCity selectedCity3 = new SelectedCity();
        selectedCity3.setCity("Kazan");
        selectedCity3.setSelected(false);

        cityList.add(selectedCity1);
        cityList.add(selectedCity2);
        cityList.add(selectedCity3);
    }

}
