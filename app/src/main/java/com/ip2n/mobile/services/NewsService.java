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
public class NewsService {


    private static String TAG = "NewsService";
    private static NewsService singleton = null;
    private ArrayList<JosContent> news = new ArrayList<JosContent>();
    private int page = 0;
    private int size = 10;
    private long top = 0;

    public NewsService() {

    }

    public static NewsService getSingleton() {
        if (singleton == null) {
            singleton = new NewsService();
        }
        return singleton;
    }

    public void getLatest(final Context context) {

        String url = "http://ipledge2nigeria.com/service/" + "news/latest/" + top;

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.d(TAG, response.toString());

                        //categories = new ArrayList<String>();
                        //Intent i = new Intent();
                        ArrayList<JosContent> temp = new ArrayList<JosContent>();
                        try {
                            for (int x = 0; x < response.length(); x++) {

                                JSONObject resp = (JSONObject) response
                                        .get(x);
                                JosContent content = new JosContent();
                                content.setTitle(resp.getString("title"));
                                content.setId(resp.getString("id"));
                                content.setAlias(resp.getString("alias"));
                                content.setCreated(resp.getString("created"));
                                content.setUser(resp.getString("user"));
                                content.setUrl("http://www.ipledge2nigeria.com/index.php?option=com_content&view=article&id=" + content.getId());
                                content.setImg(getImage(resp.getString("introtext")));
                                if(top < Long.parseLong(content.getId())){
                                    top = Long.parseLong(content.getId());
                                }

                                Log.d(TAG, content.getTitle());
                                temp.add(content);
                            }

                            if(temp.size() > 0){
                                temp.addAll(news);
                                news = temp;
                                temp = null;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context,
                                    "Error: " + "Parsing response : " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }


                        Intent i = new Intent("pledge.nigeria.com.nigeriapldge.NEWS_BROADCAST");
                        context.sendBroadcast(i);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(context,
                        "Could not retrieve news  : " + error.getMessage(), Toast.LENGTH_SHORT).show();

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

        String url = "http://ipledge2nigeria.com/service/" + "news/" + page + "/" + size;
        page = page + size;
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            for (int x = 0; x < response.length(); x++) {

                                JSONObject resp = (JSONObject) response
                                        .get(x);
                                JosContent content = new JosContent();
                                content.setTitle(resp.getString("title"));
                                content.setId(resp.getString("id"));
                                content.setAlias(resp.getString("alias"));
                                content.setCreated(resp.getString("created"));
                                content.setUser(resp.getString("user"));
                                content.setUrl("http://www.ipledge2nigeria.com/index.php?option=com_content&view=article&id=" + content.getId());
                                content.setImg(getImage(resp.getString("introtext")));


                                Log.d(TAG, content.getTitle());
                                news.add(content);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context,
                                    "Error: " + "Parsing response : " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }


                        Intent i = new Intent("pledge.nigeria.com.nigeriapldge.NEWS_BROADCAST");
                        context.sendBroadcast(i);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(context,
                        "Could not retrieve news  : " + error.getMessage(), Toast.LENGTH_SHORT).show();

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

        String url = "http://ipledge2nigeria.com/service/" + "news/" + page + "/" + size;
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
                                content.setAlias(resp.getString("alias"));
                                content.setCreated(resp.getString("created"));
                                content.setUser(resp.getString("user"));
                                content.setUrl("http://www.ipledge2nigeria.com/index.php?option=com_content&view=article&id=" + content.getId());
                                content.setImg(getImage(resp.getString("introtext")));
                                //incident.setDescription(resp.getString("description"));
                                //incident.setCreatedOn(resp.getString("created_on"));
                                //incident.setUserName("Kritika");
                                if(top < Long.parseLong(content.getId())){
                                    top = Long.parseLong(content.getId());
                                }

                                Log.d(TAG, content.getTitle());
                                news.add(content);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context,
                                    "Error: " + "Parsing response : " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                        Intent i = new Intent("pledge.nigeria.com.nigeriapldge.NEWS_BROADCAST");
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

                headers.put("Authorization", prefs.getString("NIGERIA_LOGIN_KEY", ""));
                return headers;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
        Log.i(TAG, "Kritika:::" + news);
    }

    public ArrayList<JosContent> getNews() {
        Log.i("Kritika1", "News Service news : " + news);

        return news;
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
