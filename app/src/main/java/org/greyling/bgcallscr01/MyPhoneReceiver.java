
/* Refs:
* - https://www.sitepoint.com/phone-callbacks-in-android-using-telephonymanager/
* - https://www.vogella.com/tutorials/AndroidBroadcastReceiver/article.html
* - https://www.javatpoint.com/android-telephony-manager-tutorial
* - https://kodlogs.com/89712/how-to-block-incoming-calls-in-android-programmatically
* */

package org.greyling.bgcallscr01;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyPhoneReceiver extends BroadcastReceiver {

    private static final String TAG = "MyPhoneReceiver";

    // public MyPhoneReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        /* cf https://stackoverflow.com/questions/18977012/why-itelephony-aidl-works/18989160 */
        Class c = null;
        Method m = null;
        Object telephonyService = null;
        try {
            c = Class.forName(telephony.getClass().getName());
            m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = m.invoke(telephony); // Get the internal ITelephony object
            c = Class.forName(telephonyService.getClass().getName()); // Get its class
            m = c.getDeclaredMethod("endCall"); // Get the "endCall()" method
            m.setAccessible(true); // Make it accessible
            // m.invoke(telephonyService); // invoke endCall()
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        PhoneCallStateListener customPhoneListener = new PhoneCallStateListener(context);
        telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        Log.w(TAG, "ŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧŧ");
        Log.w(TAG, "29 intent: " + intent);
        Log.w(TAG, "30 intent: " + intent.toString());
        Log.w(TAG, "↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        StringBuilder sb = new StringBuilder();
        sb.append("Action: " + intent.getAction() + "\n");
        sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
        String log = sb.toString();
        Log.d(TAG, 36 + " " + log);
        Log.w(TAG, "€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€€");

        /* cf https://www.vogella.com/tutorials/AndroidBroadcastReceiver/article.html */
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String state = extras.getString(TelephonyManager.EXTRA_STATE);
            Log.w(TAG,  43+ " state: " +state);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                String phoneNumber = extras
                        .getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                String carrierID = extras
                        .getString(TelephonyManager.EXTRA_CARRIER_ID);
                Log.w(TAG, 47 + " phoneNumber: " + phoneNumber);
                Log.w(TAG, "æææææææææææææææææææææææææææææææææææææææææææææææææææææææææææææææææææ");

                // +16467703197

                if (phoneNumber.contains("491712243636")) {
                    Toast.makeText(context, phoneNumber + " is a match!!!", Toast.LENGTH_LONG);
                    Log.w(TAG, "łłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłł");
                    Log.w(TAG, 64 + " phoneNumber " + phoneNumber + " is a match!!!");
                    Log.w(TAG, "łłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłł");

                    Log.w(TAG, "’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’");
                    Log.w(TAG, 96 + " will now try to endcall programmatically ...");
                    try {
                        /* cf https://stackoverflow.com/questions/18977012/why-itelephony-aidl-works/18989160 */
                        m.invoke(telephonyService); // invoke endCall()
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    Log.w(TAG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                }
            }
        }
    }
}