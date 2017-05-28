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
import com.ip2n.mobile.models.JosContent;
import com.ip2n.mobile.volley.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kritika_pathak on 4/8/2015.
 */
public class EntertainmentService {

    private static String TAG = "EntertainmentService";
    private static EntertainmentService singleton = null;
    private int page = 0;
    private int size = 10;
    private long top = 0;
    private int limit = 100;
    private ArrayList<JosContent> entertainment = new ArrayList<JosContent>();

    private EntertainmentService() {

    }

    public static EntertainmentService getSingleton() {
        if (singleton == null) {
            singleton = new EntertainmentService();
        }
        return singleton;
    }

    public void getLatest(final Context context) {
        String url = "http://dev.insodel.com/api/" + "articles/entertainment/" + limit;

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.d(TAG, response.toString());

                        //categories = new ArrayList<String>();
                        //Intent i = new Intent();
                        ArrayList<JosContent> temp = new ArrayList<JosContent>();
                        try {
                            // Parsing json array response
                            // loop through each json object
                            //jsonResponse = "";
                            for (int x = 0; x < response.length(); x++) {

                                JSONObject resp = (JSONObject) response
                                        .get(x);
                                JosContent content = new JosContent();
                                content.setTitle(resp.getString("title"));
                                content.setId(resp.getString("id"));
                                content.setCreated(resp.getLong("createdOn"));
                                content.setUser(resp.getString("author"));
                                content.setCreatedStr(resp.getString("createdOnStr"));

                                //content.setUrl("http://www.ipledge2nigeria.com/index.php?option=com_content&view=article&id=" + content.getId());
                                String img = resp.getString("displayImage");
                                content.setImg(img);
                                content.setIntrotext(resp.getString("intro"));

                                Log.d(TAG, content.getTitle());
                                temp.add(content);
                            }

                            if(temp.size() > 0){
                                temp.addAll(entertainment);
                                entertainment = temp;
                                temp = null;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context,
                                    "Error: " + "Parsing response : " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                        //

                        Intent i = new Intent("pledge.nigeria.com.nigeriapldge.ENTERTAINMENT_BROADCAST");
                        context.sendBroadcast(i);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(context,
                        "Could not retrieve news  : " + error.getMessage(), Toast.LENGTH_SHORT).show();

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
        Log.i(TAG, "Kritika:::" + entertainment);
    }


    public void getMore(final Context context) {
        String url = "http://dev.insodel.com/api/" + "articles/entertainment/" + limit;
        page = page + size;

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.d(TAG, response.toString());

                        //categories = new ArrayList<String>();
                        //Intent i = new Intent();

                        try {
                            // Parsing json array response
                            // loop through each json object
                            //jsonResponse = "";
                            for (int x = 0; x < response.length(); x++) {

                                JSONObject resp = (JSONObject) response
                                        .get(x);
                                JosContent content = new JosContent();
                                content.setTitle(resp.getString("title"));
                                content.setId(resp.getString("id"));
                                content.setCreated(resp.getLong("createdOn"));
                                content.setUser(resp.getString("author"));
                                content.setCreatedStr(resp.getString("createdOnStr"));

                                //content.setUrl("http://www.ipledge2nigeria.com/index.php?option=com_content&view=article&id=" + content.getId());
                                String img = resp.getString("displayImage");
                                content.setImg(img);
                                content.setIntrotext(resp.getString("intro"));


                                Log.d(TAG, content.getTitle());
                                entertainment.add(content);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context,
                                    "Error: " + "Parsing response : " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                        //

                        Intent i = new Intent("pledge.nigeria.com.nigeriapldge.ENTERTAINMENT_BROADCAST");
                        context.sendBroadcast(i);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(context,
                        "Could not retrieve news  : " + error.getMessage(), Toast.LENGTH_SHORT).show();

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
        Log.i(TAG, "Kritika:::" + entertainment);
    }

    public void get(final Context context) {

        String url = "http://dev.insodel.com/api/" + "articles/entertainment/" + limit;
        page = page + size;

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.d(TAG, response.toString());

                        //categories = new ArrayList<String>();
                        //Intent i = new Intent();

                        try {
                            // Parsing json array response
                            // loop through each json object
                            //jsonResponse = "";
                            for (int x = 0; x < response.length(); x++) {

                                JSONObject resp = (JSONObject) response
                                        .get(x);
                                JosContent content = new JosContent();
                                content.setTitle(resp.getString("title"));
                                content.setId(resp.getString("id"));
                                content.setCreated(resp.getLong("createdOn"));
                                content.setUser(resp.getString("author"));
                                content.setCreatedStr(resp.getString("createdOnStr"));

                                //content.setUrl("http://www.ipledge2nigeria.com/index.php?option=com_content&view=article&id=" + content.getId());
                                String img = resp.getString("displayImage");
                                content.setImg(img);
                                content.setIntrotext(resp.getString("intro"));

                                Log.d(TAG, content.getTitle());
                                entertainment.add(content);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context,
                                    "Error: " + "Parsing response : " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                        Intent i = new Intent("pledge.nigeria.com.nigeriapldge.ENTERTAINMENT_BROADCAST");
                        context.sendBroadcast(i);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(context,
                        "Could not retrieve news  : " + error.getMessage(), Toast.LENGTH_SHORT).show();

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
        Log.i(TAG, "Kritika:::" + entertainment);
    }

    public ArrayList<JosContent> getEntertainment() {
        Log.i("Kritika1", "Entertainment Service entertainment :" + entertainment);

        return entertainment;
    }

    private String getImage(String html){
        String url = null;
        Document doc = Jsoup.parse(html);
        org.jsoup.select.Elements elements = doc.getElementsByTag("img");
        for(Element element : elements){
            url = element.attr("src");
            break;
        }
        url = "http://www.ipledge2nigeria.com/" + url;

        return url.replace(" ","%20");
    }


}

