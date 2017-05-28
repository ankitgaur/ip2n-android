package com.ip2n.mobile.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.ip2n.mobile.activities.NigeriaTimelineActivity;
import com.ip2n.mobile.models.JosContent;
import com.ip2n.mobile.volley.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kritika_pathak on 4/12/2015.
 */
public class LoginService {

    public static void login(final Context context) {

        String url = "http://dev.insodel.com/api/" + "login";

        SharedPreferences prefs = context.getSharedPreferences("NIGERIA_PLEDGE", 0);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent i = new Intent(context, NigeriaTimelineActivity.class);
                        try{
                            String token = response.get("token").toString();
                            //Log.i("Ankit",""+token);
                            SharedPreferences mSharedPrefs = context.getSharedPreferences("NIGERIA_PLEDGE", 0);
                            SharedPreferences.Editor editor = mSharedPrefs.edit();
                            editor.putString("NIGERIA_LOGIN_TOKEN","Bearer " + token);
                            editor.commit();
                        }
                        catch(Exception e){

                        }
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context,
                        "UserId or Password is wrong, please enter correct details.  : " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                SharedPreferences prefs = context.getSharedPreferences("NIGERIA_PLEDGE", 0);

                headers.put("Authorization", prefs.getString("NIGERIA_LOGIN_KEY", ""));


                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(req);
    }
}
