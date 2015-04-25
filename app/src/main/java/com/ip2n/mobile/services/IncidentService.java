package com.ip2n.mobile.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ip2n.mobile.activities.NigeriaSendReportActivity;
import com.ip2n.mobile.activities.NigeriaTimelineActivity;
import com.ip2n.mobile.activities.adapters.MultipartRequest;
import com.ip2n.mobile.models.Incident;
import com.ip2n.mobile.volley.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kritika_pathak on 4/8/2015.
 */
public class IncidentService {
    private static String TAG = "IncidentService";
    private static IncidentService singleton = null;
    private static boolean flag = false;
    private ArrayList<Incident> incidents = new ArrayList<Incident>();
    private int page = 0;
    private int size = 10;
    private long top = 0;

    public IncidentService() {

    }

    public static IncidentService getSingleton() {
        if (singleton == null) {
            singleton = new IncidentService();
        }
        return singleton;
    }

    public void getLatest(final Context context) {

        String url = "http://ipledge2nigeria.com/service/" + "incidents/latest/" + top;

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.d(TAG, response.toString());

                        //categories = new ArrayList<String>();
                        //Intent i = new Intent();
                        ArrayList<Incident> temp = new ArrayList<Incident>();
                        try {
                            for (int x = 0; x < response.length(); x++) {

                                JSONObject resp = (JSONObject) response
                                        .get(x);
                                Incident incident = new Incident();

                                incident.setId(resp.getString("id"));
                                incident.setImage(resp.getString("image"));
                                incident.setDescription(resp.getString("description"));
                                incident.setCreatedBy(resp.getString("createdBy"));
                                incident.setCreatedOn(resp.getString("createdOn"));
                                incident.setGovt(resp.getString("govt"));
                                incident.setQuestions(resp.getString("questions"));
                                incident.setReportDate(resp.getString("reportDate"));
                                incident.setState(resp.getString("state"));
                                incident.setStatus(resp.getString("status"));
                                incident.setType(resp.getString("type"));

                                //content.setImg(getImage(resp.getString("introtext")));
                                if (top < Long.parseLong(incident.getId())) {
                                    top = Long.parseLong(incident.getId());
                                }

                                Log.d(TAG, incident.getId());
                                temp.add(incident);
                            }

                            if (temp.size() > 0) {
                                temp.addAll(incidents);
                                incidents = temp;
                                temp = null;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context,
                                    "Error: " + "Parsing response : " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }


                        Intent i = new Intent("pledge.nigeria.com.nigeriapldge.INCIDENTS_BROADCAST");
                        context.sendBroadcast(i);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(context,
                        "No new Incidents to show! ", Toast.LENGTH_SHORT).show();

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

    public void getMore(final Context context) {


        String url = "http://ipledge2nigeria.com/service/" + "incidents/page/" + page + "/" + size;
        page = page + size;
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        if (response.length() < size) {
                            flag = true;
                        }

                        try {
                            for (int x = 0; x < response.length(); x++) {

                                JSONObject resp = (JSONObject) response
                                        .get(x);
                                Incident incident = new Incident();

                                incident.setId(resp.getString("id"));
                                incident.setImage(resp.getString("image"));
                                incident.setDescription(resp.getString("description"));
                                incident.setCreatedBy(resp.getString("createdBy"));
                                incident.setCreatedOn(resp.getString("createdOn"));
                                incident.setGovt(resp.getString("govt"));
                                incident.setQuestions(resp.getString("questions"));
                                incident.setReportDate(resp.getString("reportDate"));
                                incident.setState(resp.getString("state"));
                                incident.setStatus(resp.getString("status"));
                                incident.setType(resp.getString("type"));


                                Log.d(TAG, incident.getId());
                                incidents.add(incident);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context,
                                    "Error: " + "Parsing response : " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }


                        Intent i = new Intent("pledge.nigeria.com.nigeriapldge.INCIDENTS_BROADCAST");
                        context.sendBroadcast(i);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(context,
                        "Could not retrieve Incidents  : " + error.getMessage(), Toast.LENGTH_SHORT).show();

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

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);

    }

    public void get(final Context context) {


        String url = "http://ipledge2nigeria.com/service/" + "incidents/page/" + page + "/" + size;
        Log.d(TAG, "Kritika : " + url);
        page = page + size;
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        if (response.length() < size) {
                            flag = true;
                        }
                        try {

                            for (int x = 0; x < response.length(); x++) {

                                JSONObject resp = (JSONObject) response
                                        .get(x);
                                Incident incident = new Incident();

                                incident.setId(resp.getString("id"));
                                incident.setImage(resp.getString("image"));
                                incident.setDescription(resp.getString("description"));
                                incident.setCreatedBy(resp.getString("createdBy"));
                                incident.setCreatedOn(resp.getString("createdOn"));
                                incident.setGovt(resp.getString("govt"));
                                incident.setQuestions(resp.getString("questions"));
                                incident.setReportDate(resp.getString("reportDate"));
                                incident.setState(resp.getString("state"));
                                incident.setStatus(resp.getString("status"));
                                incident.setType(resp.getString("type"));

                                if (top < Long.parseLong(incident.getId())) {
                                    top = Long.parseLong(incident.getId());
                                }

                                Log.d(TAG, incident.getId());
                                incidents.add(incident);
                            }

                        } catch (JSONException e) {
                            String line = "";
                            StackTraceElement[] el = e.getStackTrace();
                            for (StackTraceElement ste : el) {
                                if (ste.getClassName().equals(this.getClass().getName())) {
                                    line += ste.getLineNumber() + ",";
                                }
                            }
                            Log.i("Kritika", "Error :" + e.toString() + " line : " + line);

                            Toast.makeText(context,
                                    "Error: " + "Parsing response : " + line + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                        Intent i = new Intent("pledge.nigeria.com.nigeriapldge.INCIDENTS_BROADCAST");
                        context.sendBroadcast(i);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(context,
                        "Could not retrieve Incidents  : " + error.getMessage(), Toast.LENGTH_SHORT).show();

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
        Log.i(TAG, "Kritika:::" + incidents);

    }

    public ArrayList<Incident> getIncidents() {
        Log.i("Kritika1", "Incident Service incidents : " + incidents);

        return incidents;
    }

    public void create(final JSONObject json, final Context context) {
        String url = "http://ipledge2nigeria.com/service/" + "incidents";
        JsonObjectRequest postRequest = new JsonObjectRequest(url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        try {
                            Log.d(TAG + ".CREATE", response.getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (context instanceof NigeriaSendReportActivity) {
                            Log.i("Kritika", "NigeriaSendReportActivity");
                            Intent i = new Intent("pledge.nigeria.com.nigeriapldge.UPLOAD_IMAGE");
                            try {
                                i.putExtra("id", response.getString("id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            context.sendBroadcast(i);
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d(TAG + ".CREATE" + " Error.Response", error.toString());
                        Log.d(TAG + ".CREATE" + " Error.Response", error.getStackTrace().toString());
                        error.printStackTrace();


                        // Log.d(TAG+".CREATE" + " Error.Response", error.);


                    }
                }
        ) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                SharedPreferences prefs = context.getSharedPreferences("NIGERIA_PLEDGE", 0);

                headers.put("Authorization", prefs.getString("NIGERIA_LOGIN_KEY", ""));
                //headers.put("Content-Type", "application/json");
                //headers.put("Accept", "application/json");
                //headers.put("Accept-Encoding","gzip,deflate");

                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(postRequest);
    }


    public <T> void uploadFile(final String tag, final File file, final Context context, final String id) {


        final Map<String, String> headerParams = new HashMap<String, String>();
        SharedPreferences prefs = context.getSharedPreferences("NIGERIA_PLEDGE", 0);

        headerParams.put("Authorization", prefs.getString("NIGERIA_LOGIN_KEY", ""));


        String url = "http://ipledge2nigeria.com/service/" + "incidents/image/" + id;
        MultipartRequest mr = new MultipartRequest(url,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("IMAGEUPLOAD" + " Error.Response", error.toString());

                    }
                },
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String s) {
                        Log.d("IMAGEUPLOAD" + " Success.Response", s);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context).setTitle("Report Submitted")
                                .setMessage("Report has been received with the selected image. We are looking into the matter!")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent i = new Intent(context, NigeriaTimelineActivity.class);

                                        context.startActivity(i);
                                    }
                                });
                        alertDialogBuilder.create().show();

                    }
                }, file, file.length(), null, headerParams,
                "file",
                new MultipartRequest.MultipartProgressListener() {

                    @Override
                    public void transferred(long transfered, int progress) {
                        Log.i(TAG, "UploadImageProgress:::" + transfered + "::" + progress);


                    }
                });

        mr.setTag(tag);

        AppController.getInstance().addToRequestQueue(mr);

    }


}
