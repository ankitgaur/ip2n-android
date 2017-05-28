package com.ip2n.mobile.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.ip2n.mobile.R;
import com.ip2n.mobile.services.LoginService;


public class NigeriaLoginActivity extends Activity implements View.OnClickListener {
    String result = null;
    private Button mSignInButton;
    private Button mSignUpButton;
    private Context mContext;
    private EditText loginEditText;
    private EditText passwordEditText;
    private SharedPreferences mSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = this;
        mSharedPrefs = getSharedPreferences("NIGERIA_PLEDGE", 0);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);


        setContentView(R.layout.activity_nigeria_login);



    }



    @Override
    protected void onResume() {
        super.onResume();
        init();

    }

    private void init() {
        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        loginEditText = (EditText) findViewById(R.id.login_text);
        passwordEditText = (EditText) findViewById(R.id.password_text);

        mSignInButton.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.sign_in_button:

                String stringUrl = "";
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    mSharedPrefs = getSharedPreferences("NIGERIA_PLEDGE", 0);
                    SharedPreferences.Editor editor = mSharedPrefs.edit();

                    String loginEncoded = new String(Base64.encode((loginEditText.getText().toString().trim() + ":" + passwordEditText.getText().toString().trim()).getBytes(), Base64.NO_WRAP));
                    editor.putString("NIGERIA_LOGIN_KEY","Basic " + loginEncoded);
                    editor.commit();

                    LoginService.login(mContext);
                }


                break;
            case R.id.sign_up_button:
                break;
            default:
                break;


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);


    }

}

