package com.example.expense.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.biometrics.BiometricManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expense.Adapters.CurrencyAdapter;
import com.example.expense.Model.CurrencyModel;
import com.example.expense.R;
import com.example.expense.Tools.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class SettingsActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 101;
    SharedPrefs prefs;
    ConstraintLayout appLock, currencylayout, dateformatlayout, themelayout, logoutLayout;
    TextView textCurrency, textdateformat, textTheme;
    List<CurrencyModel> listCurrency = new ArrayList<>();
    CheckBox lockCheckBox, cashforwardCheckbox;
    AlertDialog.Builder alertDialog;

    String dateFormats[] = {"dd-MM-yyyy", "dd/MM/yyyy", "MM/dd/yyyy", "MM-dd-yyyy", "dd.MM.yyyy", "yyyy-MM-dd", "yyyy/MM/dd"};
    String themes[] = {"Original Light", "Original Dark"};
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize SharedPrefs
        prefs = new SharedPrefs(this);

        // Apply theme
        try {
            getTheme().applyStyle(prefs.getInt("newtheme"), true);
        } catch (Exception e) {
            Log.e("SettingsActivity", "Error applying theme", e);
        }

        setContentView(R.layout.activity_settings);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        // Initialize views first
        inStart();

        // Set listeners for initialized views
        setListeners();
    }

    private void setListeners() {

        themelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setThemeDialog();
            }
        });


        dateformatlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateFormats();
            }
        });


        lockCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    checkBiometricStatus();
                } else {
                    setapplockCheckbox(false);

                }

            }
        });

        currencylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadJSONDataintoList();
                popupCurrencyDialog();
            }
        });

        cashforwardCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setCashForwardCheckbox(b);
            }
        });
        logoutLayout.setOnClickListener(view -> showLogoutDialog());

    }
    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Clear login state
            prefs.setBool("isLoggedIn", false);

            // Redirect to login activity
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }
    private void setThemeDialog() {
        LayoutInflater li = LayoutInflater.from(SettingsActivity.this);
        final View customLayout = li.inflate(R.layout.theme_layot, null);
        alertDialog.setView(customLayout);

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.item_theme, themes);
        ListView listView = customLayout.findViewById(R.id.theme_listview);
        listView.setAdapter(adapter);

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });


        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                textTheme.setText(themes[i]);
                if (themes[i].equalsIgnoreCase("Original Light")) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                recreate(); // Use recreate() instead of startActivity and finish
                alert.dismiss();
            }
        });
    }

    private void setCashForwardCheckbox(boolean b) {
        prefs.setBool("cashforward", b);

    }

    private void popupCurrencyDialog() {
        Dialog dialog;
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_currency);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        dialog.show();
        RecyclerView recyclerView = dialog.findViewById(R.id.currency_RecyclerView);
        EditText searchtxt = dialog.findViewById(R.id.search_currecy);
        ImageView back = dialog.findViewById(R.id.back);
        CurrencyAdapter adapter = new CurrencyAdapter(listCurrency, dialog.getContext(), dialog);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SettingsActivity.this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        searchtxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                List<CurrencyModel> filterlist = new ArrayList<>();
                for (CurrencyModel item : listCurrency) {


                    if (item.getName().toLowerCase().contains(editable.toString().toLowerCase())) {
                        filterlist.add(item);
                    } else if (item.getCode().toLowerCase().contains(editable.toString().toLowerCase())) {
                        filterlist.add(item);
                    }


                    adapter.filteredList(filterlist);
                }
                if (filterlist.isEmpty()) {

                } else {
                }


            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                setUpCurrency();
            }
        });


    }

    private void loadJSONDataintoList() {
        listCurrency.clear();
        JSONObject obj = null;
        try {
            obj = new JSONObject(loadJSONFromAsset());
            JSONArray keys = obj.names();
            for (int i = 0; i < keys.length(); i++) {
                JSONObject key = obj.getJSONObject(keys.getString(i));
                String abbr = keys.getString(i);
                String code = key.getString("code");
                String symbol_native = key.getString("symbol_native");
                String name = key.getString("name");
                CurrencyModel currencyModel = new CurrencyModel(name, symbol_native, abbr, code);
                listCurrency.add(currencyModel);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkBiometricStatus() {
        androidx.biometric.BiometricManager biometricManager = androidx.biometric.BiometricManager.from(SettingsActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            switch (biometricManager.canAuthenticate(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG | androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
                case BiometricManager.BIOMETRIC_SUCCESS:
                    biometricPrompt.authenticate(promptInfo);
                    Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    setapplockCheckbox(false);
                    lockCheckBox.setChecked(false);
                    Toast.makeText(this, "This device don't have biometric hardware.", Toast.LENGTH_SHORT).show();
                    break;
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:


                    break;
            }
        }
    }

    private void setapplockCheckbox(Boolean value) {
        prefs.setBool("applock", value);
    }

    private void setapplockCheckbox() {
        Boolean value = prefs.getBool("applock");
        if (value) {
            lockCheckBox.setChecked(true);
        } else {
            lockCheckBox.setChecked(false);
        }
    }

    private void inStart() {
        listCurrency.clear();
        prefs = new SharedPrefs(SettingsActivity.this);
        appLock = findViewById(R.id.appLock);
        lockCheckBox = findViewById(R.id.appLockCheck);
        cashforwardCheckbox = findViewById(R.id.cashForwardcheckbox);
        textCurrency = findViewById(R.id.txt_currency);
        currencylayout = findViewById(R.id.currencylayout);
        dateformatlayout = findViewById(R.id.dateformatlayout);
        textdateformat = findViewById(R.id.txt_dateformat);
        textTheme = findViewById(R.id.txt_theme);
        themelayout = findViewById(R.id.themelayout);
        logoutLayout = findViewById(R.id.logoutLayout);
        alertDialog = new AlertDialog.Builder(SettingsActivity.this);
        setapplockCheckbox();
        setupBiometrics();
        setUpCurrency();
        setCashForwardCheckbox();
        setDateForm();
        setThemeData();


    }

    private void setThemeData() {
        String value = prefs.getStr("theme");
        if (value.equalsIgnoreCase("none")) {
            textTheme.setText("Original");
        } else {
            textTheme.setText(value);
        }
    }

    private void setDateForm() {
        String value = prefs.getStr("dateformat");
        if (value.equalsIgnoreCase("none")) {
            textdateformat.setText("dd-MM-yyyy");
        } else {
            textdateformat.setText(value);
        }
    }

    private void setDateFormats() {
        LayoutInflater li = LayoutInflater.from(SettingsActivity.this);
        final View customLayout = li.inflate(R.layout.item_date, null);
        alertDialog.setView(customLayout);

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.item_dateformats, dateFormats);
        ListView listView = customLayout.findViewById(R.id.dateformats_listview);
        listView.setAdapter(adapter);

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                prefs.setStr("dateformat", dateFormats[i]);
                textdateformat.setText(dateFormats[i]);
                alert.dismiss();
            }
        });
    }

    private void setCashForwardCheckbox() {
        Boolean cashforward = prefs.getBool("cashforward");
        if (cashforward) {
            cashforwardCheckbox.setChecked(true);

        } else {
            cashforwardCheckbox.setChecked(false);
        }

    }

    private void setUpCurrency() {
        String currency = prefs.getStr("currency");
        if (currency.equalsIgnoreCase("none")) {
            textCurrency.setText("VND - Vietnamese Dong ");
            prefs.setStr("currency", "VND - Vietnamese Dong");

        } else {
            textCurrency.setText(currency);
        }

    }


    private void setupBiometrics() {
        Boolean value = prefs.getBool("applock");
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(SettingsActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);

            }
            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                setapplockCheckbox(true);
                lockCheckBox.setChecked(true);


            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("Currency.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
