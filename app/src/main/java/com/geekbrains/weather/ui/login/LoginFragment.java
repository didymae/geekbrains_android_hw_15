package com.geekbrains.weather.ui.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.geekbrains.weather.Constants;
import com.geekbrains.weather.R;
import com.geekbrains.weather.data.PrefsData;
import com.geekbrains.weather.data.PrefsHelper;
import com.geekbrains.weather.receiver.SMSReceiver;
import com.geekbrains.weather.service.MyService;
import com.geekbrains.weather.ui.base.BaseActivity;
import com.geekbrains.weather.ui.base.BaseFragment;
import com.geekbrains.weather.ui.weather.WeatherFragment;


public class LoginFragment extends BaseFragment {

    //объявление переменных
        private EditText textPhone;
        private EditText textPhoneCode;
        private EditText textSMSCode;
        private Button btnSendCode;
        private Button btnLogin;
        private TextView textError;
        public final static String VERIFICATION_CODE_ACTION = "VERIFICATION_CODE_ACTION";
        public final static String  VERIFICATION_CODE = "VERIFICATION_CODE";
        private PrefsHelper prefsHelper;
        String verificationCode;


    @Override
        public void onAttach (Context context){
            super.onAttach(context);
        }


        @Nullable
        @Override
        public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup
                container, @Nullable Bundle savedInstanceState){
            //обращаемся к layout который будет содержать наш фрагмент
            return inflater.inflate(R.layout.login_fragment, container, false);
        }

        @Override
        public void onResume () {
            super.onResume();
        }

        @Override
        protected void initLayout (View view, Bundle savedInstanceState){
            textPhone = (EditText) view.findViewById(R.id.et_phone);
            textPhoneCode = (EditText) view.findViewById(R.id.et_phone_code);
            textSMSCode = (EditText) view.findViewById(R.id.et_sms_code);
            textError =  view.findViewById(R.id.et_error_login);
            btnSendCode = view.findViewById(R.id.send_code);
            btnLogin = view.findViewById(R.id.login);
            prefsHelper = new PrefsData(getBaseActivity());

            btnSendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendCode();
                    //заглушка/должен идти запрос на сервер с просьбой отправить нам код
                }
            });

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doLogin();
                }
            });
        }

    private void sendCode() {
        String phoneNumber = textPhone.getText().toString().replaceAll("\\s+","");
        if (phoneNumber.matches("\\d{7}+"))
        {
            getBaseActivity().requestReceiveSMSPermission();
            startListenerBroadcast();
            textPhone.setText(phoneNumber);
            textError.setVisibility(View.GONE);
        //вызов сервера для отправки нам sms. для проверки из эмулятора нужно отправить 0101
        } else {
            textError.setText(getString(R.string.incorrect_phone));
            textError.setVisibility(View.VISIBLE);
        }
    }

    private void doLogin() {
        if(textSMSCode.getText().toString().equals(checkVerificationCode())){
            prefsHelper.saveSharedPreferences(VERIFICATION_CODE,textSMSCode.getText().toString());
            getBaseActivity().replaceMainFragment(new WeatherFragment());

        } else {
            textError.setText(getString(R.string.incorrect_code));
            textError.setVisibility(View.VISIBLE);
        }
    }

    private String checkVerificationCode() {
        // должна быть проверка на сервере
        return Constants.VERIFICATION_CODE_SMS;
    }

    private void startListenerBroadcast(){

        IntentFilter intentValue = new IntentFilter(VERIFICATION_CODE_ACTION);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                verificationCode = intent.getStringExtra(LoginFragment.VERIFICATION_CODE);
                textSMSCode.setText(verificationCode);
                doLogin();
            }
        };
        getBaseActivity().registerReceiver(broadcastReceiver, intentValue);
    }

}


