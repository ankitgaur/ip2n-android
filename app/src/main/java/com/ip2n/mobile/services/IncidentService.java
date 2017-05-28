package com.ip2n.mobile.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.ip2n.mobile.activities.NigeriaSendReportActivity;
import com.ip2n.mobile.activities.NigeriaTimelineActivity;
import com.ip2n.mobile.activities.adapters.MultipartRequest;
import com.ip2n.mobile.asynctasks.ImageLoadTask;
import com.ip2n.mobile.models.Incident;
import com.ip2n.mobile.volley.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.examples.ListLinks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by kritika_pathak on 4/8/2015.
 */
public class IncidentService {
    private static String TAG = "IncidentService";
    private static IncidentService singleton = null;
    private static boolean flag = false;
    private Set<Incident> incidents = new TreeSet<Incident>();
    private int page = 0;
    private int size = 10;
    private long top = 0;
    private int limit = 1000;

    public IncidentService() {

    }

    public static IncidentService getSingleton() {
        if (singleton == null) {
            singleton = new IncidentService();
        }
        return singleton;
    }

    public void getLatest(final Context context) {

        //String url = "http://dev.insodel.com/api/" + "incidents/latest/" + top;
        //TODO : String url = "http://dev.insodel.com/api/" + "incidents/limit/" + limit;

        String url = "http://dev.insodel.com/api/" + "incidents";
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.d(TAG, response.toString());

                        //categories = new ArrayList<String>();
                        //Intent i = new Intent();
                        Set<Incident> temp = new TreeSet<Incident>();
                        try {
                            for (int x = 0; x < response.length(); x++) {

                                JSONObject resp = (JSONObject) response
                                        .get(x);
                                Incident incident = new Incident();

                                incident.setId(resp.getString("id"));
                                incident.setImage(resp.getString("image"));
                                Log.i("Kritika","image: "+incident.getImage());

                                JSONObject obj = resp.getJSONObject("questions");

                                incident.setDescription(obj.getString("Brief Description"));
                                incident.setCreatedBy(resp.getString("author"));
                                incident.setCreatedOn(resp.getString("createdOnStr"));
                                incident.setCreated(resp.getLong("createdOn"));
                                incident.setGovt(resp.getString("govt"));
                                incident.setQuestions(obj.toString());
                                //incident.setReportDate(resp.getString("reportDate"));
                                incident.setState(resp.getString("state"));
                                incident.setStatus(resp.getString("status"));
                                incident.setType(resp.getString("type"));
                                try {
                                    incident.setImage(resp.getString("image_id"));
                                }
                                catch(Exception e){
                                    //do nothing
                                }


                                //content.setImg(getImage(resp.getString("introtext")));
//                                if (top < Long.parseLong(incident.getId())) {
//                                    top = Long.parseLong(incident.getId());
//                                }

                                //ImageLoadTask task = new ImageLoadTask(incident);
                                //task.execute();

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

                headers.put("Authorization", prefs.getString("NIGERIA_LOGIN_TOKEN", ""));
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(req);
    }

    public void getMore(final Context context) {

        //TODO: correct url
        String url = "http://dev.insodel.com/api/" + "incidents";
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
                                try {
                                    incident.setImage(resp.getString("image_id"));
                                }
                                catch(Exception e){
                                    //do nothing
                                }
                                //ImageLoadTask task = new ImageLoadTask(incident);
                                //task.execute();
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

                headers.put("Authorization", prefs.getString("NIGERIA_LOGIN_TOKEN", ""));
                return headers;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);

    }

    public void get(final Context context) {

        //TODO: Update URLs
        String url = "http://dev.insodel.com/api/" + "incidents";
        //String url = "http://dev.insodel.com/api/" + "incidents/page/" + page + "/" + size;
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
                                Log.i("Kritika","image: "+incident.getImage());

                                JSONObject obj = resp.getJSONObject("questions");

                                incident.setDescription(obj.getString("Brief Description"));
                                incident.setCreatedBy(resp.getString("author"));
                                incident.setCreatedOn(resp.getString("createdOnStr"));
                                incident.setCreated(resp.getLong("createdOn"));
                                incident.setGovt(resp.getString("govt"));
                                incident.setQuestions(obj.toString());
                                //incident.setReportDate(resp.getString("reportDate"));
                                incident.setState(resp.getString("state"));
                                incident.setStatus(resp.getString("status"));
                                incident.setType(resp.getString("type"));
                                try {
                                    incident.setImage(resp.getString("image_id"));
                                }
                                catch(Exception e){
                                    //do nothing
                                }
//                                if (top < Long.parseLong(incident.getId())) {
//                                    top = Long.parseLong(incident.getId());
//                                }

                                //ImageLoadTask task = new ImageLoadTask(incident);
                                //task.execute();

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

                headers.put("Authorization", prefs.getString("NIGERIA_LOGIN_TOKEN", ""));
                return headers;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
        Log.i(TAG, "Kritika:::" + incidents);

    }

    public Set<Incident> getIncidents() {
        Log.i("Kritika1", "Incident Service incidents : " + incidents);

        return incidents;
    }

    public void create(final JSONObject json, final Context context) {
        Log.d("Creating Incident : ",json.toString());
        //String url = "http://dev.insodel.com/api/" + "incidents";
        String url = "http://dev.insodel.com/api/" + "incidents";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response

                        String id = null;

                        try {
                            JSONObject resp = new JSONObject(response);
                            id = resp.getString("id");
                            Log.d(TAG + ".CREATE", id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Intent i = new Intent(context, NigeriaTimelineActivity.class);
                        //i.putExtra("add_new_incident",true);
                        //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                        //context.startActivity(i);
                        if (context instanceof NigeriaSendReportActivity) {
                            Log.i("Kritika", "NigeriaSendReportActivity");
                            Intent i = new Intent("pledge.nigeria.com.nigeriapldge.UPLOAD_IMAGE");
                            try {
                                i.putExtra("id", id);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            context.sendBroadcast(i);
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Ankit514 : error :" , error.toString());
                        Log.d("Ankit514 :error :", error.getStackTrace().toString());
                        error.printStackTrace();

                        Toast.makeText(context,
                                "Your report could not be submitted",
                                Toast.LENGTH_LONG).show();
                        Intent i = new Intent(context, NigeriaTimelineActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);

                        // Log.d(TAG+".CREATE" + " Error.Response", error.);

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                //@RequestParam("type") String type,
                //@RequestParam("state") String state,
                //@RequestParam("govt") String govt,
                //@RequestParam(name = "displayImage", required = false) MultipartFile displayImage,
                //@RequestParam("category") String category,
                //@RequestParam("description") String description,
                //@RequestParam("questions") String questions)
                Map<String, String>  params = new HashMap<String, String>();
                params.put("type", "incidents");
                try {
                    params.put("state", json.getString("state"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    params.put("govt", json.getString("govt"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //params.put("displayImage", "");
                try {
                    params.put("category", json.getString("type"));
                    Log.d("Ankit514", json.getString("type"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    params.put("description", json.getString("description"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    params.put("questions", json.getString("questions"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                SharedPreferences prefs = context.getSharedPreferences("NIGERIA_PLEDGE", 0);

                headers.put("Authorization", prefs.getString("NIGERIA_LOGIN_TOKEN", ""));
                //headers.put("Content-Type", "application/json");
                //headers.put("Accept", "application/json");
                //headers.put("Accept-Encoding","gzip,deflate");

                return headers;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(postRequest);
    }


    public <T> void uploadFile(final String tag, final File file, final Context context, final String id) {

        String tmp = null;

        try {
            byte[] imgbytes = FileUtil.getBytes(file);
            if (imgbytes != null && imgbytes.length > 0) {
                tmp = Base64.encodeToString(imgbytes,Base64.NO_WRAP);
            }
        } catch (NullPointerException npe) {
            // do nothing
        }

        final String content = tmp;
        final String fname = file.getName();

        if(content!=null && !content.isEmpty()) {
            final Map<String, String> headerParams = new HashMap<String, String>();
            SharedPreferences prefs = context.getSharedPreferences("NIGERIA_PLEDGE", 0);

            headerParams.put("Authorization", prefs.getString("NIGERIA_LOGIN_TOKEN", ""));


            String url = "http://dev.insodel.com/api/" + "incidents/image/" + id;

            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            // response

                            Log.d("IMAGEUPLOAD", "Success.Response : " + response);
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context).setTitle("Report Submitted")
                                    .setMessage("Thanks for your valuable feedback! Your report with the selected image will form part of our analysis on www.ipledge2nigeria.com.")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Intent i = new Intent(context, NigeriaTimelineActivity.class);
                                            i.putExtra("add_new_incident", true);
                                            context.startActivity(i);
                                        }
                                    });
                            alertDialogBuilder.create().show();

                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("IMAGEUPLOAD", " Error.Response" + error.toString());
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context).setTitle("Report Submitted")
                                    .setMessage("There was some error in uploading the image, report has been received. We are looking into the matter!")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Intent i = new Intent(context, NigeriaTimelineActivity.class);
                                            i.putExtra("add_new_incident", true);
                                            context.startActivity(i);
                                        }
                                    });
                            alertDialogBuilder.create().show();

                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {
                    //@RequestParam("type") String type,
                    //@RequestParam("state") String state,
                    //@RequestParam("govt") String govt,
                    //@RequestParam(name = "displayImage", required = false) MultipartFile displayImage,
                    //@RequestParam("category") String category,
                    //@RequestParam("description") String description,
                    //@RequestParam("questions") String questions)
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("content", content);
                    params.put("fname", fname);

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    SharedPreferences prefs = context.getSharedPreferences("NIGERIA_PLEDGE", 0);

                    headers.put("Authorization", prefs.getString("NIGERIA_LOGIN_TOKEN", ""));
                    //headers.put("Content-Type", "application/json");
                    //headers.put("Accept", "application/json");
                    //headers.put("Accept-Encoding","gzip,deflate");

                    return headers;
                }
            };

            postRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(postRequest);





            /*MultipartRequest mr = new MultipartRequest(url,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("IMAGEUPLOAD", " Error.Response" + error.toString());
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context).setTitle("Report Submitted")
                                    .setMessage("There was some error in uploading the image, report has been received. We are looking into the matter!")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Intent i = new Intent(context, NigeriaTimelineActivity.class);
                                            i.putExtra("add_new_incident", true);
                                            context.startActivity(i);
                                        }
                                    });
                            alertDialogBuilder.create().show();


                        }
                    },
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String s) {
                            Log.d("IMAGEUPLOAD", "Success.Response : " + s);
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context).setTitle("Report Submitted")
                                    .setMessage("Thanks for your valuable feedback! Your report with the selected image will form part of our analysis on www.ipledge2nigeria.com.")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Intent i = new Intent(context, NigeriaTimelineActivity.class);
                                            i.putExtra("add_new_incident", true);
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

            AppController.getInstance().addToRequestQueue(mr);*/
        }

    }


}
