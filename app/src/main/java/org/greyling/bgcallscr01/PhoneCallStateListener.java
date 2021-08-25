
/*
cf https://www.vogella.com/tutorials/AndroidBroadcastReceiver/article.html
*/

package org.greyling.bgcallscr01;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PhoneCallStateListener extends PhoneStateListener {
    private static final String TAG = "MyPhoneReceiverListener";
    private Context context;
    private final Intent intent;
    private final SharedPreferences sharedPrefs;
    private static final String PREFERENCES = "prefs";

    public PhoneCallStateListener(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
        this.sharedPrefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public void onCellLocationChanged (CellLocation location) {
        Log.w(TAG, "←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←");
        Log.w(TAG, "46 CellLocation: " + location);
        if (location instanceof GsmCellLocation) {
            GsmCellLocation gcLoc = (GsmCellLocation) location;
        }
        if (location instanceof CdmaCellLocation) {
            CdmaCellLocation ccLoc = (CdmaCellLocation) location;
            Log.w(TAG, "CdmaCellLocation: \n" +
                "\nBaseStationId: " + ccLoc.getBaseStationId() +
                "\nBaseStationLatitude: " + ccLoc.getBaseStationLatitude() +
                "\nBaseStationLongitude: " + ccLoc.getBaseStationLongitude() +
                "\nNetworkId: " + ccLoc.getNetworkId() +
                ""
            );
        }
        Log.w(TAG, "←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←");
    }

    public void onCellInfoChanged (List<CellInfo> cellInfo) {
        Log.w(TAG, "←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←");
        Log.w(TAG, "52 CellInfo: ");
        for (CellInfo item : cellInfo) {
            Log.w(TAG, "52 CellInfo item: ");
            // Log.w(TAG, ": " + item.getCellConnectionStatus());
            Log.w(TAG, "TimestampMillis: " + item.getTimestampMillis());
            Log.w(TAG, "CellSignalStrength: " + item.getCellSignalStrength());
            Log.w(TAG, "CellIdentity: " + item.getCellIdentity());
        }
        Log.w(TAG, "←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←");
    }

    @Override
    public void onSignalStrengthsChanged (SignalStrength signalStrength) {
        Log.w(TAG, "68 signalStrength: " + signalStrength);
        Log.w(TAG, "69 signalStrength level: " + signalStrength.getLevel());
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        /* NOTE: incomingNumber is also sometimes the dialed number!!! */

        super.onCallStateChanged(state, incomingNumber);

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        Log.w(TAG, "←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←");
        Log.w(TAG, "65 state now: " + state);
        Log.w(TAG, "66 incomingNumber: " + incomingNumber);
        Log.w(TAG, "←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←");

        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                Log.w(TAG, "state: TelephonyManager.CALL_STATE_RINGING - " + TelephonyManager.CALL_STATE_RINGING);
            case PhoneStateListener.LISTEN_CALL_STATE:
                Log.w(TAG, "state: PhoneStateListener.LISTEN_CALL_STATE - " + PhoneStateListener.LISTEN_CALL_STATE);
            case TelephonyManager.CALL_STATE_IDLE:
                Log.w(TAG, "state: TelephonyManager.CALL_STATE_IDLE - " + TelephonyManager.CALL_STATE_IDLE);
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Log.w(TAG, "state: TelephonyManager.CALL_STATE_OFFHOOK - " + TelephonyManager.CALL_STATE_OFFHOOK);
        }

        Bundle extras = intent.getExtras();
        Log.w(TAG, "łłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłł");
        Log.w(TAG, "Bundle extras: " + extras);
        Set<String> keys = extras.keySet();
        for (String key : keys) {
            Log.w(TAG, "key: " + key);
            Log.w(TAG, "val: " + extras.get(key));
        }
        Log.w(TAG, "łłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłł");

        if (state == TelephonyManager.CALL_STATE_OFFHOOK)   return; // break off if outgoing
        if (state == TelephonyManager.CALL_STATE_IDLE)      return; // break off if idle

        if (extras != null) {
            String stateAsStr = extras.getString(TelephonyManager.EXTRA_STATE);
            Log.w(TAG, 39 + " stateAsStr: " + stateAsStr);
            if (stateAsStr==null) return; /*no incoming number available so break off here*/
            if (stateAsStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                /* don't use the extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER) as phonenumber - it could hold an older value */
                // String phoneNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                // Log.w(TAG, 43 + " phoneNumber: " + phoneNumber);

                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    List<CellInfo> allCellInfo = telephony.getAllCellInfo();
                    Log.w(TAG, "«««««««««««««««««««««««««««««««««««««««««««««««««««««««««««««");
                    if (allCellInfo == null)
                        Log.w(TAG, "allCellInfo: " + null);
                    if (allCellInfo != null) {
                        Log.w(TAG, "allCellInfo: " + allCellInfo.toString());
                        for (CellInfo cellInfo : allCellInfo) {
                            Log.w(TAG, "cellInfo: " + cellInfo.toString());
                        }
                    }
                    Log.w(TAG, "«««««««««««««««««««««««««««««««««««««««««««««««««««««««««««««");
                }
                else {
                    Log.w(TAG, "«««««««««««««««««««««««««««««««««««««««««««««««««««««««««««««");
                    Log.w(TAG, "Perm ACCESS_FINE_LOCATION not granted so can't show 'allCellInfo");
                    Log.w(TAG, "«««««««««««««««««««««««««««««««««««««««««««««««««««««««««««««");
                }

                boolean blacklisted = false;

                Map<String, ?> savedNumbers = sharedPrefs.getAll();
                Log.w(TAG, "ðððððððððððððððððððððððððððððððððððððððððððððððððððððððððððð");
                Log.w(TAG, "... checking if incoming no. is on the blacklist ...");
                for (Map.Entry<String, ?> entry : savedNumbers.entrySet()) {
                    String _telNo       = (String) entry.getKey();
                    if (incomingNumber.contains(_telNo))
                        Log.w(TAG, " (on the list) ");
                    boolean _isActive   = (Boolean) entry.getValue();
                    if (_isActive && incomingNumber.contains(_telNo)) {
                        Log.w(TAG, " AND BLACKLISTED !!! ");
                        blacklisted = true;
                        break;
                    }
                }
                if (!blacklisted) {
                    Log.w(TAG, "Not blacklisted.");
                }
                Log.w(TAG, "ðððððððððððððððððððððððððððððððððððððððððððððððððððððððððððð");

                if (blacklisted) {
                    Toast
                        .makeText(context, incomingNumber + " is a match!!!", Toast.LENGTH_LONG)
                        .show();
                    Log.w(TAG, "łłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłł");
                    Log.w(TAG, 64 + " phoneNumber " + incomingNumber + " is a match!!!");
                    Log.w(TAG, "łłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłłł");

                    Log.w(TAG, "’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’’");
                    Log.w(TAG, 96 + " will now try to endcall() programmatically ...");

                    try {
                        endCall(telephony);
                    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    Log.w(TAG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                }
            }
        }

    }

    // cf https://stackoverflow.com/questions/18977012/why-itelephony-aidl-works/18989160
    // also https://stackoverflow.com/a/56011629
    // https://android.googlesource.com/platform/frameworks/base/+/f1e1e7714375b3b83f2cc3956b112293face56a1/telephony/java/android/telephony/TelephonyManager.java
    private void endCall(TelephonyManager telephony) throws ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Class c = Class.forName(telephony.getClass().getName());
        Method m = c.getDeclaredMethod("getITelephony");
        m.setAccessible(true);
        Object telephonyService = m.invoke(telephony); // Get the internal ITelephony object
        c = Class.forName(telephonyService.getClass().getName()); // Get its class
        m = c.getDeclaredMethod("endCall"); // Get the "endCall()" method
        m.setAccessible(true); // Make it accessible
        m.invoke(telephonyService); // invoke endCall()
    }

}

// Log.w(TAG, "ACTION_CONFIGURE_VOICEMAIL: " + TelephonyManager.ACTION_CONFIGURE_VOICEMAIL);

/*
// Field[] fields = TelephonyManager.class.getDeclaredFields();
// int modifiers = -1;
// String name = "";
// for (int i=0; i<fields.length; i++) {
//     modifiers = fields[i].getModifiers();
//     if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
//         Object value = null;
//         name = fields[i].getName();
//         // Log.w(TAG, "39 name: " + name + ", value: " + fields[i]);
//         try {
//             Field field = fields[i].getClass().getDeclaredField(name);
//             value = field.get(fields[i]);
//         } catch (NoSuchFieldException | IllegalAccessException e) {
//             e.printStackTrace();
//         }
//         String objStr = (value == null) ? "NULL" : value.toString();
//         Log.w(TAG, "48 name: " + name + ", value: " + objStr);
//     }
// }

Log.w(TAG, "ACTION_CONFIGURE_VOICEMAIL: " + TelephonyManager.ACTION_CONFIGURE_VOICEMAIL);
Log.w(TAG, "ACTION_PHONE_STATE_CHANGED: " + TelephonyManager.ACTION_PHONE_STATE_CHANGED);
Log.w(TAG, "ACTION_RESPOND_VIA_MESSAGE: " + TelephonyManager.ACTION_RESPOND_VIA_MESSAGE);
Log.w(TAG, "CALL_STATE_IDLE: " + TelephonyManager.CALL_STATE_IDLE);
Log.w(TAG, "CALL_STATE_OFFHOOK: " + TelephonyManager.CALL_STATE_OFFHOOK);
Log.w(TAG, "CALL_STATE_RINGING: " + TelephonyManager.CALL_STATE_RINGING);
Log.w(TAG, "DATA_ACTIVITY_DORMANT: " + TelephonyManager.DATA_ACTIVITY_DORMANT);
Log.w(TAG, "DATA_ACTIVITY_IN: " + TelephonyManager.DATA_ACTIVITY_IN);
Log.w(TAG, "DATA_ACTIVITY_INOUT: " + TelephonyManager.DATA_ACTIVITY_INOUT);
Log.w(TAG, "DATA_ACTIVITY_NONE: " + TelephonyManager.DATA_ACTIVITY_NONE);
Log.w(TAG, "DATA_ACTIVITY_OUT: " + TelephonyManager.DATA_ACTIVITY_OUT);
Log.w(TAG, "DATA_CONNECTED: " + TelephonyManager.DATA_CONNECTED);
Log.w(TAG, "DATA_CONNECTING: " + TelephonyManager.DATA_CONNECTING);
Log.w(TAG, "DATA_DISCONNECTED: " + TelephonyManager.DATA_DISCONNECTED);
Log.w(TAG, "DATA_SUSPENDED: " + TelephonyManager.DATA_SUSPENDED);
Log.w(TAG, "DATA_UNKNOWN: " + TelephonyManager.DATA_UNKNOWN);
Log.w(TAG, "EXTRA_INCOMING_NUMBER: " + TelephonyManager.EXTRA_INCOMING_NUMBER);
Log.w(TAG, "EXTRA_STATE: " + TelephonyManager.EXTRA_STATE);
Log.w(TAG, "EXTRA_STATE_IDLE: " + TelephonyManager.EXTRA_STATE_IDLE);
Log.w(TAG, "EXTRA_STATE_OFFHOOK: " + TelephonyManager.EXTRA_STATE_OFFHOOK);
Log.w(TAG, "EXTRA_STATE_RINGING: " + TelephonyManager.EXTRA_STATE_RINGING);
Log.w(TAG, "NETWORK_TYPE_1xRTT: " + TelephonyManager.NETWORK_TYPE_1xRTT);
Log.w(TAG, "NETWORK_TYPE_CDMA: " + TelephonyManager.NETWORK_TYPE_CDMA);
Log.w(TAG, "NETWORK_TYPE_EDGE: " + TelephonyManager.NETWORK_TYPE_EDGE);
Log.w(TAG, "NETWORK_TYPE_EHRPD: " + TelephonyManager.NETWORK_TYPE_EHRPD);
Log.w(TAG, "NETWORK_TYPE_EVDO_0: " + TelephonyManager.NETWORK_TYPE_EVDO_0);
Log.w(TAG, "NETWORK_TYPE_EVDO_A: " + TelephonyManager.NETWORK_TYPE_EVDO_A);
Log.w(TAG, "NETWORK_TYPE_EVDO_B: " + TelephonyManager.NETWORK_TYPE_EVDO_B);
Log.w(TAG, "NETWORK_TYPE_GPRS: " + TelephonyManager.NETWORK_TYPE_GPRS);
Log.w(TAG, "NETWORK_TYPE_GSM: " + TelephonyManager.NETWORK_TYPE_GSM);
Log.w(TAG, "NETWORK_TYPE_HSDPA: " + TelephonyManager.NETWORK_TYPE_HSDPA);
Log.w(TAG, "NETWORK_TYPE_HSPA: " + TelephonyManager.NETWORK_TYPE_HSPA);
Log.w(TAG, "NETWORK_TYPE_HSPAP: " + TelephonyManager.NETWORK_TYPE_HSPAP);
Log.w(TAG, "NETWORK_TYPE_HSUPA: " + TelephonyManager.NETWORK_TYPE_HSUPA);
Log.w(TAG, "NETWORK_TYPE_IDEN: " + TelephonyManager.NETWORK_TYPE_IDEN);
Log.w(TAG, "NETWORK_TYPE_IWLAN: " + TelephonyManager.NETWORK_TYPE_IWLAN);
Log.w(TAG, "NETWORK_TYPE_LTE: " + TelephonyManager.NETWORK_TYPE_LTE);
Log.w(TAG, "NETWORK_TYPE_TD_SCDMA: " + TelephonyManager.NETWORK_TYPE_TD_SCDMA);
Log.w(TAG, "NETWORK_TYPE_UMTS: " + TelephonyManager.NETWORK_TYPE_UMTS);
Log.w(TAG, "NETWORK_TYPE_UNKNOWN: " + TelephonyManager.NETWORK_TYPE_UNKNOWN);
Log.w(TAG, "PHONE_TYPE_CDMA: " + TelephonyManager.PHONE_TYPE_CDMA);
Log.w(TAG, "PHONE_TYPE_GSM: " + TelephonyManager.PHONE_TYPE_GSM);
Log.w(TAG, "PHONE_TYPE_NONE: " + TelephonyManager.PHONE_TYPE_NONE);
Log.w(TAG, "PHONE_TYPE_SIP: " + TelephonyManager.PHONE_TYPE_SIP);
Log.w(TAG, "SIM_STATE_ABSENT: " + TelephonyManager.SIM_STATE_ABSENT);
Log.w(TAG, "SIM_STATE_CARD_IO_ERROR: " + TelephonyManager.SIM_STATE_CARD_IO_ERROR);
Log.w(TAG, "SIM_STATE_NETWORK_LOCKED: " + TelephonyManager.SIM_STATE_NETWORK_LOCKED);
Log.w(TAG, "SIM_STATE_NOT_READY: " + TelephonyManager.SIM_STATE_NOT_READY);
Log.w(TAG, "SIM_STATE_PERM_DISABLED: " + TelephonyManager.SIM_STATE_PERM_DISABLED);
Log.w(TAG, "SIM_STATE_PIN_REQUIRED: " + TelephonyManager.SIM_STATE_PIN_REQUIRED);
Log.w(TAG, "SIM_STATE_PUK_REQUIRED: " + TelephonyManager.SIM_STATE_PUK_REQUIRED);
Log.w(TAG, "SIM_STATE_READY: " + TelephonyManager.SIM_STATE_READY);
Log.w(TAG, "SIM_STATE_UNKNOWN: " + TelephonyManager.SIM_STATE_UNKNOWN);
Log.w(TAG, "VVM_TYPE_CVVM: " + TelephonyManager.VVM_TYPE_CVVM);
Log.w(TAG, "VVM_TYPE_OMTP: " + TelephonyManager.VVM_TYPE_OMTP);

outupts

ACTION_CONFIGURE_VOICEMAIL: android.telephony.action.CONFIGURE_VOICEMAIL
ACTION_PHONE_STATE_CHANGED: android.intent.action.PHONE_STATE
ACTION_RESPOND_VIA_MESSAGE: android.intent.action.RESPOND_VIA_MESSAGE
CALL_STATE_IDLE: 0
CALL_STATE_OFFHOOK: 2
CALL_STATE_RINGING: 1
DATA_ACTIVITY_DORMANT: 4
DATA_ACTIVITY_IN: 1
DATA_ACTIVITY_INOUT: 3
DATA_ACTIVITY_NONE: 0
DATA_ACTIVITY_OUT: 2
DATA_CONNECTED: 2
DATA_CONNECTING: 1
DATA_DISCONNECTED: 0
DATA_SUSPENDED: 3
DATA_UNKNOWN: -1
EXTRA_INCOMING_NUMBER: incoming_number
EXTRA_STATE: state
EXTRA_STATE_IDLE: IDLE
EXTRA_STATE_OFFHOOK: OFFHOOK
EXTRA_STATE_RINGING: RINGING
NETWORK_TYPE_1xRTT: 7
NETWORK_TYPE_CDMA: 4
NETWORK_TYPE_EDGE: 2
NETWORK_TYPE_EHRPD: 14
NETWORK_TYPE_EVDO_0: 5
NETWORK_TYPE_EVDO_A: 6
NETWORK_TYPE_EVDO_B: 12
NETWORK_TYPE_GPRS: 1
NETWORK_TYPE_GSM: 16
NETWORK_TYPE_HSDPA: 8
NETWORK_TYPE_HSPA: 10
NETWORK_TYPE_HSPAP: 15
NETWORK_TYPE_HSUPA: 9
NETWORK_TYPE_IDEN: 11
NETWORK_TYPE_IWLAN: 18
NETWORK_TYPE_LTE: 13
NETWORK_TYPE_TD_SCDMA: 17
NETWORK_TYPE_UMTS: 3
NETWORK_TYPE_UNKNOWN: 0
PHONE_TYPE_CDMA: 2
PHONE_TYPE_GSM: 1
PHONE_TYPE_NONE: 0
PHONE_TYPE_SIP: 3
SIM_STATE_ABSENT: 1
SIM_STATE_CARD_IO_ERROR: 8
SIM_STATE_NETWORK_LOCKED: 4
SIM_STATE_NOT_READY: 6
SIM_STATE_PERM_DISABLED: 7
SIM_STATE_PIN_REQUIRED: 2
SIM_STATE_PUK_REQUIRED: 3
SIM_STATE_READY: 5
SIM_STATE_UNKNOWN: 0
VVM_TYPE_CVVM: vvm_type_cvvm
VVM_TYPE_OMTP: vvm_type_omtp

*/

/*
Method[] methods = c.getDeclaredMethods();
Log.w(TAG, "………………………………………………………………………………………………………………………………………………………………………………………………………");
for (Method _method : methods) {
    Log.w(TAG, "method: " + _method.getName());
}
Log.w(TAG, "………………………………………………………………………………………………………………………………………………………………………………………………………");

delivers:

IsDomesticRoaming
IsInternationalRoaming
NSRI_requestProc
SimSlotActivation
SimSlotOnOff
answerRingingCall
answerRingingCallForSubscriber
asBinder
calculateAkaResponse
calculateGbaBootstrappingResponse
calculateNafExternalKey
call
callForSubscriber
canChangeDtmfToneLength
checkCarrierPrivilegesForPackage
checkCarrierPrivilegesForPackageAnyPhone
checkNSRIUSIMstate_int
dial
dialForSubscriber
disableDataConnectivity
disableLocationUpdates
disableLocationUpdatesForSubscriber
enableDataConnectivity
enableLocationUpdates
enableLocationUpdatesForSubscriber
enableVideoCalling
endCall
endCallForSubscriber
factoryReset
getActiveFgCallState
getActivePhoneType
getActivePhoneTypeForSubscriber
getAllCellInfo
getAtr
getAtrUsingSlotId
getCalculatedPreferredNetworkType
getCallState
getCallStateForSubscriber
getCarrierPackageNamesForIntentAndPhone
getCarrierPrivilegeStatus
getCdmaEriIconIndex
getCdmaEriIconIndexForSubscriber
getCdmaEriIconMode
getCdmaEriIconModeForSubscriber
getCdmaEriText
getCdmaEriTextForSubscriber
getCdmaMdn
getCdmaMin
getCellLocation
getCellNetworkScanResults
getCurrentUATI
getDataActivity
getDataEnabled
getDataNetworkType
getDataNetworkTypeForSubscriber
getDataRoamingEnabled
getDataRoamingEnabledUsingSubID
getDataServiceState
getDataServiceStateUsingSubId
getDataState
getDataStateSimSlot
getDefaultSim
getDeviceId
getDisable2g
getEuimid
getFeliCaUimLockStatus
getHandsetInfo
getImei
getInterfaceDescriptor
getIpAddressFromLinkProp
getLgt3GDataStatus
getLgtOzStartPage
getLine1AlphaTagForDisplay
getLine1NumberForDisplay
getLocaleFromDefaultSim
getLteCellInfo
getLteOnCdmaMode
getLteOnCdmaModeForSubscriber
getMeid
getMergedSubscriberIds
getMobileQualityInformation
getModemActivityInfo
getMultiSimForegroundPhoneId
getMultiSimLastRejectIncomingCallPhoneId
getNeighboringCellInfo
getNetworkType
getNetworkTypeForSubscriber
getPcscfAddress
getPhoneServiceState
getPreferredNetworkType
getRadioAccessFamily
getSdnAvailable
getSelectedApn
getServiceState
getServiceStateUsingSubId
getSimPinRetry
getSimPinRetryForSubscriber
getSimPukRetry
getSimPukRetryForSubscriber
getSubIdForPhoneAccount
getTetherApnRequired
getTimeInfo
getVoiceMessageCount
getVoiceMessageCountForSubscriber
getVoiceNetworkTypeForSubscriber
getVoicePhoneServiceState
getVoicePhoneServiceStateUsingSubId
getWipiSysInfo
handlePinMmi
handlePinMmiForSubscriber
hasIccCard
hasIccCardUsingSlotId
iccCloseLogicalChannel
iccCloseLogicalChannelUsingSlotId
iccExchangeSimIO
iccExchangeSimIOUsingSlotId
iccOpenLogicalChannel
iccOpenLogicalChannelUsingSlotId
iccTransmitApduBasicChannel
iccTransmitApduBasicChannelUsingSlotId
iccTransmitApduLogicalChannel
iccTransmitApduLogicalChannelUsingSlotId
invokeOemRilRequestRaw
invokeOemRilRequestRawForSubscriber
isApnTypeAvailable
isApnTypeAvailableUsingSubId
isDataConnectivityPossible
isDualBTConnection
isHearingAidCompatibilitySupported
isIdle
isIdleForSubscriber
isImsCall
isImsRegistered
isOffhook
isOffhookForSubscriber
isRadioOn
isRadioOnForSubscriber
isRinging
isRingingForSubscriber
isSimFDNEnabled
isSimFDNEnabledForSubscriber
isSimPinEnabled
isTtyModeSupported
isVideoCall
isVideoCallingEnabled
isVideoTelephonyAvailable
isVolteAvailable
isWifiCallingAvailable
isWorldPhone
needMobileRadioShutdown
needsOtaServiceProvisioning
notifyMissedCall
notifyVoIPCallStateChangeIntoBT
nvReadItem
nvResetConfig
nvWriteCdmaPrl
nvWriteItem
sendEnvelopeWithStatus
sendRequestRawToRIL
sendRequestToRIL
setAirplaneMode
setBTUserWantsAudioOn
setBTUserWantsSwitchAudio
setCellInfoListRate
setDataEnabled
setDataRoamingEnabled
setDisable2g
setDmCmd
setEPSLOCI
setGbaBootstrappingParams
setImsRegistrationState
setLine1NumberForDisplayForSubscriber
setMultiSimForegroundPhoneId
setMultiSimLastRejectIncomingCallPhoneId
setNetworkBand
setNetworkSelectionModeAutomatic
setNetworkSelectionModeManual
setOperatorBrandOverride
setPreferredNetworkType
setRadio
setRadioCapability
setRadioForSubscriber
setRadioPower
setRoamingOverride
setSelectedApn
setTransmitPower
setUimRemoteLockStatus
setVoiceMailNumber
shutdownMobileRadios
silenceRinger
sms_NSRI_decryptsms
sms_NSRI_decryptsmsintxside
sms_NSRI_encryptsms
startGlobalNetworkSearchTimer
startGlobalNoSvcChkTimer
startMobileQualityInformation
startVoiceLessOtaProvisioning
stopGlobalNetworkSearchTimer
stopGlobalNoSvcChkTimer
stopMobileQualityInformation
supplyPerso
supplyPin
supplyPinForSubscriber
supplyPinReportResult
supplyPinReportResultForSubscriber
supplyPuk
supplyPukForSubscriber
supplyPukReportResult
supplyPukReportResultForSubscriber
toggleRadioOnOff
toggleRadioOnOffForSubscriber
transmitIccAPDU
updateServiceLocation
updateServiceLocationForSubscriber
validateMsisdn

*/