package com.geekbrains.weather;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by shkryaba on 24/06/2018.
 */

public class CreateActionFragment extends BaseFragment {

    private EditText edittext;
    private EditText edittextName;
    OnHeadlineSelectedListener mCallback;
    Intent intent;
    String country;
    TextView textViewName;

    public interface OnHeadlineSelectedListener {
        public void onArticleSelected(String position);
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
        edittext = (EditText) view.findViewById(R.id.et);
        edittextName = (EditText) view.findViewById(R.id.etname) ;
        intent= getActivity().getIntent();
        Button save = (Button)view.findViewById(R.id.save);
        Button history = (Button)view.findViewById(R.id.t_history);

        edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    country = edittext.getText().toString().trim();
                    intent.putExtra("country",country);
                    mCallback.onArticleSelected(edittext.getText().toString().trim());
//                    getBaseActivity().startActionFragment(edittext.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edittextName.getText().toString().trim();
                intent.putExtra("name",name);
                textViewName = (TextView) getBaseActivity().findViewById(R.id.tvUsername);
                textViewName.setText(name);
                getBaseActivity().replaceMainFragment(WeatherFragment.newInstance(country));


            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getBaseActivity().replaceMainFragment(new TemperatureFragment());

            }
        });
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

}
