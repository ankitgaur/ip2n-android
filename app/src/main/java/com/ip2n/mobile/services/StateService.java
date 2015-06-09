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
import com.ip2n.mobile.models.State;
import com.ip2n.mobile.volley.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by kritika_pathak on 2/1/2015.
 */
public class StateService {

    private static String TAG = "StateService";
    private static Map<String,State> states;
    private static boolean fetched = false;

    public static boolean isFetched() {
        return fetched;
    }

    public void fetch(final Context context) {


        String url = "http://ipledge2nigeria.com/service/nigeriaStates";
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        states = new HashMap<String,State>();
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
                                String currGovt = resp.getString("currGovt");
                                JSONArray jsonArray = resp.getJSONArray("govts");

                                ArrayList<String> govts = new ArrayList<String>();

                                if (jsonArray != null) {
                                    int len = jsonArray.length();
                                    for (int i=0;i<len;i++){
                                        govts.add(jsonArray.get(i).toString());
                                    }
                                }

                                State state = new State();
                                state.setId(id);
                                state.setName(name);
                                state.setCurrGovt(currGovt);
                                state.setGovts(govts);

                                Log.d(TAG, name);

                                states.put(state.getName(),state);
                            }
                            //Intent i = new Intent(context, NigeriaCategoryActivity.class);
                            //i.putStringArrayListExtra("categories", cats);
                            //context.startActivity(i);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context,
                                    "Error: " + "Parsing response : " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }finally{
                            fetched = true;
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, "Error: " + error.getMessage());
                fetched = true;
                Toast.makeText(context,
                        "Could not retrieve States : " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                SharedPreferences prefs = context.getSharedPreferences("NIGERIA_PLEDGE", 0);

                headers.put("Authorization", prefs.getString("NIGERIA_LOGIN_KEY", ""));
                return headers;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
        //Log.i("Kritika", "sending categories" + categories);

    }

    public static String[] getStates() {
        if (states != null){
            Set<String> temp =  states.keySet();
            String[] arr = new String[temp.size()];

            temp.toArray(arr);

            Arrays.sort(arr);
            return arr;
        }

           return null;


    }

    public static State getStateByName(String name) {
        if (states != null)
            return states.get(name);
        else
            return null;

    }

}
