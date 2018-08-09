package com.geekbrains.weather.receiver;

import android.app.NotificationChannel;
import android.content.res.Resources;
import android.os.Build;
import android.provider.SyncStateContract;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.geekbrains.weather.Constants;
import com.geekbrains.weather.R;
import com.geekbrains.weather.ui.base.BaseActivity;
import com.geekbrains.weather.ui.login.LoginFragment;


public class SMSReceiver extends BroadcastReceiver {

    private int messageId = 0;
    private String CHANNEL_ID = "DEFAULT";
    private NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String name = context.getResources().getString(R.string.channel_name);
        String description = context.getResources().getString(R.string.channel_description);
        createNotificationChannel(name,description);
        if (intent != null && intent.getAction() != null) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }
            String smsFromPhone = messages[0].getDisplayOriginatingAddress();
            StringBuilder body = new StringBuilder();
            for (int i = 0; i < messages.length; i++) {
                body.append(messages[i].getMessageBody());
            }
            String bodyText = body.toString();
            makeNote(context, smsFromPhone, bodyText);
            if(smsFromPhone.equals(Constants.SERVER_SMS_NUMBER)){
                setVerificationCode(body.toString(),context);
            }
// Это будет работать только на Android ниже 4.4
            abortBroadcast();
        }
    }

    private void setVerificationCode(String code, Context c) {
        try {
            Intent intentSMS = new Intent(LoginFragment.VERIFICATION_CODE_ACTION);
            intentSMS.putExtra(LoginFragment.VERIFICATION_CODE, code);
            c.sendBroadcast(intentSMS);
        } catch (Throwable t1) {
            Toast.makeText(c,
                    "Exception: " + t1.toString(), Toast.LENGTH_SHORT)
                    .show();
        }

    }

    private void makeNote(Context context, String smsFromPhone, String bodyText) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_add_24dp)
                .setContentTitle(String.format("Sms EEEEE [%s]", smsFromPhone))
                .setContentText(bodyText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(context, SMSReceiver.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        notificationManager.notify(messageId++, builder.build());
    }

    private void createNotificationChannel(String name, String description) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }
    }
}
