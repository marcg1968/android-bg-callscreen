package org.greyling.bgcallscr01;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyPhoneReceiverMainAct";
    private static final int MULTIPLE_PERMISSIONS = 1;
    private final int READ_PHONE_STATE = 1;
    String[] permissions = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private View popupInputDialogView = null;
    private View popupInputDialogViewMultiple = null;
    private View popupInputNumberChange = null;
    private EditText telNumberEditText = null;
    private EditText telNumbEditTextMul = null;
    private Button saveButton = null;
    private Button cancelButton = null;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch telNoEnabled = null;
    private static final String PREFERENCES = "prefs";
    private SharedPreferences sharedPrefs;
    private boolean switchIsChecked;
    private ArrayAdapter adapter;
    private ArrayList<String> list = new ArrayList<>();
    private HashMap<String, Boolean> numberActive;
    private ArrayList<String> listLabels = new ArrayList<>();
    private int currentlySelected = -1;
    private String currentlySelectedTelNumber = "";
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getApplicationContext();

        sharedPrefs = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        /* backButton click listener - not required if alert dialogs are set cancelable */
        // DialogInterface.OnKeyListener overrideClickBack = new Dialog.OnKeyListener() {
        //     @Override
        //     public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        //         Log.w(TAG, "86 keyCode: " + keyCode);
        //         if (keyCode == KeyEvent.KEYCODE_BACK) {
        //             dialog.cancel();
        //             return true;
        //         }
        //         return false;
        //     }
        // };
        /* lambda equivalent to above code */
        DialogInterface.OnKeyListener overrideClickBack = (dialog, keyCode, event) -> {
            Log.w(TAG, "86 keyCode: " + keyCode);
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.cancel();
                return true;
            }
            return false;
        };

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //         .setAction("Action", null).show();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle("Add Tel Number Dialog.");

                /* true causes cancel if tapped outside dialog, in which case backbutton override is not required */
                alertDialogBuilder.setCancelable(true);

                initPopupViewControls();

                alertDialogBuilder.setView(popupInputDialogView);

                // final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { handleSaveAdd(v, alertDialog); }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { alertDialog.cancel(); }
                });

                /* capture backbutton click to dismiss alertdialog */
                // alertDialog.setOnKeyListener(overrideClickBack);
            }
        });

        /* long click allows multi-line input of phone numbers */
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Snackbar.make(v, "Wow! That was a long click!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle("Add Tel Number Dialog.");
                alertDialogBuilder.setCancelable(true);

                initPopupViewControlsMultiple();

                alertDialogBuilder.setView(popupInputDialogViewMultiple);

                // final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { handleSaveAddMultiple(v, alertDialog); }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { alertDialog.cancel(); }
                });

                // alertDialog.setOnKeyListener(overrideClickBack);

                return true; /* prevent normal "short" click being activated */
            }
        });

        ComponentName receiver = new ComponentName(context, MyPhoneReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        // showPhoneStatePermission(); // for single perm
        doMultiplePermissionsCheck(); // for multiple perms

        // if (sharedPrefs.getAll().size() < 1) {
        //     return; // break off here if no saved numbers
        // }

        final ListView listview = findViewById(R.id.listViewTelNo);
        numberActive = generateActiveNumbFromPrefs(); /* lookup for number set active or not */
        ArrayList<HashMap<String, Boolean>> struct = generateStructFromPrefs();
        list = extractTelNumbsAsList(struct, false);
        listLabels = extractTelNumbsAsList(struct, true);

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listLabels);
        listview.setAdapter(adapter);
        ListViewHelper.getListViewSize(listview);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // String msg = "Click ListItem Number " + position + " " + list.get(position);
                // Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                currentlySelected = position;

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle("En-/Dis-able");
                alertDialogBuilder.setCancelable(false);

                final boolean _isActive = toNotNullable(numberActive.get(list.get(position)), false);
                initPopupChangeNumber(list.get(position), _isActive);

                alertDialogBuilder.setView(popupInputNumberChange);

                // final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleSaveEnable(v, alertDialog);
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });

                alertDialog.setOnKeyListener(overrideClickBack);

            }
        });

        final DialogInterface.OnClickListener dialogClickListenerRmPref = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        removeTelNumbFromPrefs(currentlySelectedTelNumber);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                currentlySelected = position;
                currentlySelectedTelNumber = list.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder
                    .setTitle("DELETE ITEM #" + currentlySelected)
                    .setMessage("This will delete " + currentlySelectedTelNumber + ". Are you sure?")
                    .setPositiveButton("Yes", dialogClickListenerRmPref)
                    .setNegativeButton("No", dialogClickListenerRmPref)
                    .setCancelable(true)
                    .show();

                return true;
            }
        });

    }

    private ArrayList<HashMap<String, Boolean>> generateStructFromPrefs() {
        ArrayList<HashMap<String, Boolean>> list = new ArrayList<>();
        Map<String, ?> savedNumbers = sharedPrefs.getAll();
        SortedSet<String> keys = new TreeSet<>(savedNumbers.keySet());
        for (String key : keys) {
            HashMap<String, Boolean> hm = new HashMap<>();
            hm.put(key, (Boolean) savedNumbers.get(key));
            list.add(hm);
        }
        return list;
    }

    private ArrayList<String>  extractTelNumbsAsList(ArrayList<HashMap<String, Boolean>> list, boolean asLabels) {
        ArrayList<String> _list = new ArrayList<>();
        for (int i=0; i<list.size(); i++) {
            HashMap<String, Boolean> hm = list.get(i);
            Set<Map.Entry<String, Boolean>> set = hm.entrySet();
            String item = "";
            String isActive = "";
            for (Map.Entry<String, ?> entry : hm.entrySet()) {
                item = entry.getKey();
                if (asLabels) {
                    isActive = ((Boolean)entry.getValue()) ? "" : "inactive";
                    item += " " + isActive;
                }
            }
            _list.add(item);
        }
        return _list;
    }

    private HashMap<String, Boolean> generateActiveNumbFromPrefs() {
        HashMap<String, Boolean> numberActive = new HashMap<>();

        if (sharedPrefs.getAll().size() < 1) return null;

        Map<String, ?> savedNumbers = sharedPrefs.getAll();
        String _telNo = "";
        for (Map.Entry<String, ?> entry : savedNumbers.entrySet()) {
            _telNo = entry.getKey();
            numberActive.put(_telNo, (Boolean)entry.getValue());
        }
        return numberActive;
    }

    private void initPopupChangeNumber(String telNumber, boolean isActive) {
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);

        switchIsChecked = isActive;

        // Inflate the popup dialog from a layout xml file.
        popupInputNumberChange = layoutInflater.inflate(R.layout.popup_input_number_change, null);

        telNumberEditText   = popupInputNumberChange.findViewById(R.id.telNumb);
        saveButton          = popupInputNumberChange.findViewById(R.id.button_save);
        cancelButton        = popupInputNumberChange.findViewById(R.id.button_cancel);
        telNoEnabled        = popupInputNumberChange.findViewById(R.id.switch1);

        telNoEnabled.setChecked(isActive);

        telNoEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchIsChecked = isChecked;
                Context c = MainActivity.this.getApplicationContext();
                String msg = "The Switch is " + (isChecked ? "on" : "off");
                Toast.makeText(c, msg, Toast.LENGTH_LONG).show();
                Log.w(TAG, "ſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſ");
                Log.w(TAG, "237 isChecked: " + isChecked);
                Log.w(TAG, "238 buttonView.isChecked(): " + buttonView.isChecked());
                Log.w(TAG, "239 switchIsChecked: " + switchIsChecked);
                Log.w(TAG, "ſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſſ");
            }
        });

        telNumberEditText.setText(telNumber);

    }

    public static class ListViewHelper {
        private static final String TAG = "MyPhoneReceiverMainAct";

        public static void getListViewSize(ListView myListView) {
            ListAdapter myListAdapter = myListView.getAdapter();
            if (myListAdapter==null) {
                //do nothing return null
                return;
            }
            //set listAdapter in loop for getting final size
            int totalHeight = 0;
            for (int size=0; size < myListAdapter.getCount(); size++) {
                View listItem=myListAdapter.getView(size, null, myListView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            //setting listview item in adapter
            ViewGroup.LayoutParams params = myListView.getLayoutParams();
            params.height=totalHeight + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
            myListView.setLayoutParams(params);
            // print height of adapter on log
            Log.i(TAG,"height of listItem: " + String.valueOf(totalHeight));
        }
    }

    private void handleSaveEnable(View v, AlertDialog alertDialog) {
        String _telNumber = telNumberEditText.getText().toString().trim();

        // Switch telNoEnabled = (Switch) findViewById(R.id.switch1);
        Log.i(TAG,"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        Log.i(TAG,"302 - _telNumber: " + _telNumber);
        Log.i(TAG,"303 - switchIsChecked: " + switchIsChecked);
        Log.i(TAG,"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        sharedPrefs = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        Log.i(TAG,"←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←");
        Map<String, ?> savedNumbers = sharedPrefs.getAll();
        for (Map.Entry<String, ?> entry : savedNumbers.entrySet()) {
            String _telNo = entry.getKey();
            boolean _isActive = (Boolean)entry.getValue();
            Log.i(TAG,_telNo + " | " + _isActive);
        }
        Log.i(TAG,"←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←");
        if (sharedPrefs.contains(_telNumber)) {
            boolean isEnabled = sharedPrefs.getBoolean(_telNumber, false);
            Toast
                .makeText(
                    getApplicationContext(),
                    "tel " + _telNumber + " enabled from " + isEnabled + " to " + switchIsChecked,
                    Toast.LENGTH_LONG
                )
                .show();
            if (switchIsChecked != isEnabled) {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putBoolean(_telNumber, switchIsChecked);
                editor.apply();

                /* update the list and notify change */
                String isNowEnabled = (switchIsChecked) ? "ACTIVATED" : "DE-ACTIVATED";
                listLabels.remove(currentlySelected);
                listLabels.add(currentlySelected, _telNumber + " " + isNowEnabled);
                ListViewHelper.getListViewSize(findViewById(R.id.listViewTelNo));
                adapter.notifyDataSetChanged();
            }
        }

        alertDialog.cancel();
    }

    private void handleSaveAdd(View v, AlertDialog alertDialog) {
        String _telNumber = telNumberEditText.getText().toString().trim();

        sharedPrefs = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if (sharedPrefs.contains(_telNumber)) {
            Toast
                .makeText(
                    getApplicationContext(),
                    "tel " + _telNumber + " already known",
                    Toast.LENGTH_LONG
                )
                .show();
            alertDialog.cancel();
            return;
        }
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(_telNumber, true);
        editor.apply();

        list.add(0, _telNumber);
        listLabels.add(0, _telNumber + " (NEW)");
        adapter.notifyDataSetChanged();
        ListViewHelper.getListViewSize(findViewById(R.id.listViewTelNo));

        alertDialog.cancel();
    }

    private void handleSaveAddMultiple(View v, AlertDialog alertDialog) {
        String text = telNumbEditTextMul.getText().toString().trim();
        // String lines[] = text.split("\\r?\\n");
        String lines[] = text.split("[\\r?\\n]+");

        if (lines.length > 0) {
            sharedPrefs = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            for (String line : lines) {
                // line = line.trim().replaceAll("\\w", "");
                line = line.trim().replaceAll("\\D", "");
                editor.putBoolean(line, true);
                list.add(0, line);
                listLabels.add(0, line + " (NEW)");
            }
            editor.apply();
            adapter.notifyDataSetChanged();
            ListViewHelper.getListViewSize(findViewById(R.id.listViewTelNo));
        }

        alertDialog.cancel();
    }

    private void removeTelNumbFromPrefs(String telNumber) {
        sharedPrefs = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        if (!sharedPrefs.contains(telNumber)) return;
        // sharedPrefs.getBoolean(telNumber)

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove(telNumber);
        editor.apply();

        int i = list.indexOf(telNumber);
        list.remove(i);
        listLabels.remove(i);
        adapter.notifyDataSetChanged();
        ListViewHelper.getListViewSize(findViewById(R.id.listViewTelNo));
    }

    private void initPopupViewControls() {
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);

        // Inflate the popup dialog from a layout xml file.
        popupInputDialogView = layoutInflater.inflate(R.layout.popup_input_dialog, null);

        telNumberEditText   = popupInputDialogView.findViewById(R.id.telNumb);
        saveButton          = popupInputDialogView.findViewById(R.id.button_save);
        cancelButton        = popupInputDialogView.findViewById(R.id.button_cancel);

    }

    private void initPopupViewControlsMultiple() {
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);

        popupInputDialogViewMultiple = layoutInflater.inflate(R.layout.popup_input_multiplenumbers, null);

        telNumbEditTextMul  = popupInputDialogViewMultiple.findViewById(R.id.telNumbMultiple);
        saveButton          = popupInputDialogViewMultiple.findViewById(R.id.button_save);
        cancelButton        = popupInputDialogViewMultiple.findViewById(R.id.button_cancel);
    }

    /**
     * for single perm
     **/
    private void showPhoneStatePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_PHONE_STATE);
        // int permissionCheck = ContextCompat.checkSelfPermission(
        //         this, Manifest.permission); // READ_PRIVILEGED_PHONE_STATE

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
                showExplanation("Permission Needed", "Rationale", Manifest.permission.READ_PHONE_STATE, READ_PHONE_STATE);
            } else {
                requestPermission(Manifest.permission.READ_PHONE_STATE, READ_PHONE_STATE);
            }
        } else {
            Toast.makeText(MainActivity.this, "Permission (already) Granted!", Toast.LENGTH_SHORT).show();
        }
    }

    /* adapted from https://stackoverflow.com/questions/34342816/android-6-0-multiple-permissions */
    private void doMultiplePermissionsCheck() {
        int result;
        List<String> listPermsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermsNeeded.add(p);
            }
        }
        if (!listPermsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermsNeeded.toArray(new String[listPermsNeeded.size()]), MULTIPLE_PERMISSIONS
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_PHONE_STATE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

    /**
     * cf https://www.logicbig.com/tutorials/core-java-tutorial/java-language/autoboxing-and-unboxing.html
     */
    public static <T> T toNotNullable(T object, T defaultValue) {
        Objects.requireNonNull(defaultValue, "The default value cannot be null");
        return object == null ? defaultValue : object;
    }

}

