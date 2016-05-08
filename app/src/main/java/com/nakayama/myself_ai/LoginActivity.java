package com.nakayama.myself_ai;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created by nakayama on 2016/02/10.
 */
public class LoginActivity extends Activity {
    private static String GCM_KEY = "AIzaSyBw78Um66uZwkCisT2P_AFvAqZqVJBFWZQ";
    private GoogleCloudMessaging gcm;
    private EditText mEditText;
    private Button mLoginButton;

    private final String PROPERTY_REG_ID = "RegisterationId";
    private final String PROPERTY_APP_VERSION = "PropertyAppVersion";
    private final String SENDER_ID = "690837436448";
    static final int PLAY_SERVICES_RESOLUTION_REQUEST = -1;
    private String mRegistrationId;

    @Override
    public void onCreate(Bundle savedInstancedState){
        super.onCreate(savedInstancedState);
        setContentView(R.layout.activity_login);
        gcm = GoogleCloudMessaging.getInstance(this);
        registerInBackground();
        mEditText = (EditText)findViewById(R.id.user_id);
        mLoginButton = (Button)findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = mEditText.getText().toString();
                if(userId != null){
                    new UpdateRequestTask(userId).execute();
                    Intent intent = new Intent(LoginActivity.this,MenuActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        initGCM();
    }

    private void initGCM(){
        if(checkPlayServices()){
            gcm = GoogleCloudMessaging.getInstance(this);
            mRegistrationId = getRegistrationId();

            if(mRegistrationId.isEmpty()){
                registerInBackground();
                //Log.d(TAG,"registerInbackground =>");
            }else{
                //Log.i(TAG,"registrationID = " + registrationId);
            }
        }else{
            //Log.i(TAG,"No valid Google Play Services APK found");
        }
    }


    private String getRegistrationId(){
        final SharedPreferences prefs = getGCMPreferences();
        String regId = prefs.getString(PROPERTY_REG_ID, "");
        if(regId.isEmpty()){
            //Log.i(TAG,"Registration not found");
            return "";
        }

        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(this);
        if(registeredVersion != currentVersion){
            //Log.i(TAG,"App version changed.");
            return "";
        }
        return regId;
    }

    private SharedPreferences getGCMPreferences(){
        return getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context){
        try{
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        }catch (Exception e){
            throw new RuntimeException("Could not get package name :"+ e);
        }
    }

    private boolean checkPlayServices(){
        try{
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
            if(resultCode != ConnectionResult.SUCCESS){
                if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                    GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                }else{
                    //Log.i(TAG,"This device is not supported.");
                    finish();
                }
                return false;
            }
        }catch(Exception e){
            //Log.d(TAG,e.getLocalizedMessage());
        }
        return true;
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(LoginActivity.this);
                    }
                    mRegistrationId = gcm.register(SENDER_ID);
                    storeRegistrationId(LoginActivity.this,mRegistrationId);
                    msg = "Device registered, registration ID = " + mRegistrationId;
                    Log.d("resultdata-register", "Device registered, registration ID=" + msg);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                // registration IDを取得
                // 従来であれば、ここから送信サーバーへregistration IDを送信するような流れになる
                Log.d("resultdata-key", msg);
            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context,String regId){
        final SharedPreferences prefs = getGCMPreferences();
        int appVersion = getAppVersion(context);
        //Log.i(TAG,"Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

}
