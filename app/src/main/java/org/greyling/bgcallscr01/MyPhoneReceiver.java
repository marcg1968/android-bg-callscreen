
/* Refs:
* - https://www.sitepoint.com/phone-callbacks-in-android-using-telephonymanager/
* - https://www.vogella.com/tutorials/AndroidBroadcastReceiver/article.html
* - https://www.javatpoint.com/android-telephony-manager-tutorial
* - https://kodlogs.com/89712/how-to-block-incoming-calls-in-android-programmatically
*
* - https://stackoverflow.com/questions/32409741/permission-denial-with-broadcast-receiver
* */

/*
Minimum permissions:

Top level in manifest:

    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.PHONE_STATE" />
    <uses-permission android:name="android.permission.CALL_PHONE" />

then

    <application
        ...
        <receiver
            android:name=".MyPhoneReceiver"
            android:enabled="true"
            android:exported="true"
        >
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED"/>
                <action android:name="android.intent.action.READ_PHONE_STATE" /><!-- definitely needed to get phone number -->
                <action android:name="android.intent.action.PHONE_STATE" />
                <action android:name="android.intent.action.INPUT_METHOD_CHANGED" />
            </intent-filter>
        </receiver>
 */

package org.greyling.bgcallscr01;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class MyPhoneReceiver extends BroadcastReceiver {

    private static final String TAG = "MyPhoneReceiver";

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        // method called when the BroadcastReceiver is receiving an Intent broadcast
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        PhoneCallStateListener customPhoneListener = new PhoneCallStateListener(context, intent);
        // listener to do all the work:
        telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

}