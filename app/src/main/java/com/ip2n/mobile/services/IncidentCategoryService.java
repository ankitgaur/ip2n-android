package com.ip2n.mobile.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.ip2n.mobile.activities.NigeriaCategoryActivity;
import com.ip2n.mobile.models.IncidentType;
import com.ip2n.mobile.volley.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by kritika_pathak on 2/1/2015.
 */
public class IncidentCategoryService {

    private static String TAG = "IncidentCategoryService";
    private static Map<String,IncidentType> categories;

    public void getAll(final Context context) {


        String url = "http://dev.insodel.com/api/incidentTypes";
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        categories = new HashMap<String,IncidentType>();
                        //Intent i = new Intent();
                        ArrayList<String> cats = new ArrayList<String>();
                        try {
                            // Parsing json array response
                            // loop through each json object
                            //jsonResponse = "";

                            for (int x = 0; x < response.length(); x++) {

                                JSONObject resp = (JSONObject) response
                                        .get(x);

                                String id = resp.getString("id");
                                String name = resp.getString("name");
                                String questions = resp.getString("questions");
                                IncidentType itype = new IncidentType();
                                itype.setId(id);
                                itype.setName(name);
                                itype.setQuestions(questions);

                                Log.d(TAG, name);
                                cats.add(id+"|"+name);
                                categories.put(id,itype);
                            }
                            Intent i = new Intent(context, NigeriaCategoryActivity.class);
                            i.putStringArrayListExtra("categories", cats);
                            context.startActivity(i);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context,
                                    "Error: " + "Parsing response : " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(context,
                        "Could not retrieve categories : " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                SharedPreferences prefs = context.getSharedPreferences("NIGERIA_PLEDGE", 0);

                headers.put("Authorization", prefs.getString("NIGERIA_LOGIN_TOKEN", ""));
                return headers;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
        Log.i("Kritika", "sending categories" + categories);

    }

    public static Map<String,IncidentType> getAllTypes() {
        if (categories != null)
            return categories;
        else
            return null;

    }

    public static IncidentType getTypeById(String id) {
        if (categories != null)
            return categories.get(id);
        else
            return null;

    }

}
