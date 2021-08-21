package org.greyling.bgcallscr01;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import java.lang.reflect.Method;

public class PhoneCallStateListener extends PhoneStateListener {
    private Context context;
    public PhoneCallStateListener(Context context){
        this.context = context;
    }


    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);

        switch (state) {

            case TelephonyManager.CALL_STATE_RINGING:
                // if
                break;
            case PhoneStateListener.LISTEN_CALL_STATE:

        }
        super.onCallStateChanged(state, incomingNumber);
    }
}
