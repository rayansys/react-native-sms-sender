package ir.rayansys.rnsendsms;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.telephony.SmsManager;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.Callback;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class SendSMSModule extends ReactContextBaseJavaModule{

    private final ReactApplicationContext reactContext;

    String SMS_SENT = "SMS_SENT";
    String SMS_DELIVERED = "SMS_DELIVERED";

    public SendSMSModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }
    @Override
    public String getName() {
        return "SendSMS";
    }

    public void sendCallback(int status) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("SMS_STATUS_LISTENER", status);
    }
    @ReactMethod
    public void send(ReadableMap options) {
        try {
            String body = options.hasKey("body") ? options.getString("body") : "";
            ReadableArray recipients = options.hasKey("recipients") ? options.getArray("recipients") : null;
            SmsManager smsManager = SmsManager.getDefault();

            reactContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            sendCallback(1);
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            sendCallback(3);
                            break;
                    }
                }
            }, new IntentFilter(SMS_SENT));
            reactContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            sendCallback(2);
                            break;
                        case Activity.RESULT_CANCELED:
                            sendCallback(4);
                            break;
                    }
                }
            }, new IntentFilter(SMS_DELIVERED));

            PendingIntent sentSMS;
            PendingIntent deliverSMS;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                sentSMS = PendingIntent.getBroadcast(reactContext, 0, new Intent(SMS_SENT), PendingIntent.FLAG_MUTABLE);
                deliverSMS = PendingIntent.getBroadcast(reactContext, 0, new Intent(SMS_DELIVERED),  PendingIntent.FLAG_MUTABLE);
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    sentSMS = PendingIntent.getBroadcast(reactContext, 0, new Intent(SMS_SENT), PendingIntent.FLAG_IMMUTABLE);
                    deliverSMS = PendingIntent.getBroadcast(reactContext, 0, new Intent(SMS_DELIVERED),  PendingIntent.FLAG_IMMUTABLE);
                }else{
                    sentSMS = PendingIntent.getBroadcast(reactContext, 0, new Intent(SMS_SENT), 0);
                    deliverSMS = PendingIntent.getBroadcast(reactContext, 0, new Intent(SMS_DELIVERED), 0);
                }

            }

            if (recipients != null) {
                for (int i = 0; i < recipients.size(); i++) {
                    smsManager.sendTextMessage(recipients.getString(i), null, body, sentSMS, deliverSMS);
                }
            }
            sendCallback(0);
        } catch (Exception e) {
            sendCallback(5);
            throw e;
        }
    }

}
