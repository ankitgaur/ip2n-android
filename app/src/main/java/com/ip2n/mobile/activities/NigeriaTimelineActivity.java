package com.ip2n.mobile.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ip2n.mobile.R;
import com.ip2n.mobile.activities.adapters.IncidentListArrayAdapter;
import com.ip2n.mobile.activities.adapters.NewsAndEntertainmentListArrayAdapter;
import com.ip2n.mobile.models.Incident;
import com.ip2n.mobile.models.JosContent;
import com.ip2n.mobile.services.EntertainmentService;
import com.ip2n.mobile.services.IncidentCategoryService;
import com.ip2n.mobile.services.IncidentService;
import com.ip2n.mobile.services.NewsService;

import java.util.ArrayList;




public class NigeriaTimelineActivity extends Activity implements View.OnClickListener {
    private static int buttonClicked = -1;
    private static boolean loadMoreNews = false;
    private static boolean loadMoreIncidents = false;
    private static boolean loadMoreEntertainment = false;
    private Button reportIncidentButton;
    private ListView incidentListView;

    private ArrayList<Incident> incidentsArrayList;
    private ArrayList<JosContent> newsArrayList;
    private ArrayList<JosContent> entertainmentArrayList;

    private NewsAndEntertainmentListArrayAdapter newsAndEntertainmentListArrayAdapter;
    private IncidentListArrayAdapter incidentListArrayAdapter;

    private LinearLayout baseLayout;
    private Button incidentsFeedButton;
    private Button entertainmentButton;
    private Button newsButton;
    private SwipeRefreshLayout swipeContainer;
    private Context mContext;
    private Button signOutButton;
    private MyReceiver myReceiver;
    private SharedPreferences settings;

    public int getButtonClicked() {
        return buttonClicked;
    }

    public void setButtonClicked(int buttonClicked) {
        NigeriaTimelineActivity.buttonClicked = buttonClicked;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nigeria_timeline);
        mContext = this;
        settings = getSharedPreferences("FIRST_TIME_RUN", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        buttonClicked = getButtonClicked();
        loadMoreNews = false;
        loadMoreEntertainment = false;
        loadMoreIncidents = false;
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("pledge.nigeria.com.nigeriapldge.NEWS_BROADCAST");
        intentFilter.addAction("pledge.nigeria.com.nigeriapldge.INCIDENTS_BROADCAST");
        intentFilter.addAction("pledge.nigeria.com.nigeriapldge.ENTERTAINMENT_BROADCAST");
        registerReceiver(myReceiver, intentFilter);
        if (buttonClicked != 2 && buttonClicked != 3) {
            if(incidentsArrayList == null)
                IncidentService.getSingleton().getLatest(mContext);
            else
                IncidentService.getSingleton().get(mContext);

        }

        if(settings.getBoolean("incident_first_run",true)) {
            showInstructionPopup();
            settings.edit().putBoolean("incident_first_run", false).commit();

        }
        init();


    }

    @Override
    protected void onPause() {

        super.onPause();
        unregisterReceiver(myReceiver);
        setButtonClicked(buttonClicked);
    }

    private void init() {
        //initBaseView();
        //initListView();
        reportIncidentButton = (Button) findViewById(R.id.report_incident_button);
        reportIncidentButton.setOnClickListener(this);

        incidentsFeedButton = (Button) findViewById(R.id.feed_button);
        entertainmentButton = (Button) findViewById(R.id.entertainment_button);
        newsButton = (Button) findViewById(R.id.news_button);
        signOutButton = (Button) findViewById(R.id.sign_out_button);
        if(buttonClicked == 2) {
            entertainmentButton.setSelected(true);
            newsButton.setSelected(false);
            incidentsFeedButton.setSelected(false);

        }
        else if(buttonClicked == 3) {
            newsButton.setSelected(true);
            entertainmentButton.setSelected(false);
            incidentsFeedButton.setSelected(false);
        }
        else {
            incidentsFeedButton.setSelected(true);
            entertainmentButton.setSelected(false);
            newsButton.setSelected(false);

        }


        incidentsFeedButton.setOnClickListener(this);
        entertainmentButton.setOnClickListener(this);
        newsButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);

    }

    private void initListView(final ArrayAdapter timelineListAdapter) {
        if (incidentListView == null)
            incidentListView = (ListView) findViewById(R.id.incident_list_view);

        LayoutInflater inflater = ((Activity) this).getLayoutInflater();
        View convertView = inflater.inflate(R.layout.timeline_listview_header, null, false);
        if (incidentListView.getHeaderViewsCount() <= 0)
            incidentListView.addHeaderView(convertView);
        if (incidentListView.getFooterViewsCount() <= 0)
            incidentListView.addFooterView(convertView);

        incidentListView.setAdapter(timelineListAdapter);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
                refreshContent();
            }

            private void refreshContent() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if(buttonClicked == 2) {
                            EntertainmentService.getSingleton().getLatest(mContext);
                        }

                        else if(buttonClicked ==3) {
                            NewsService.getSingleton().getLatest(mContext);
                        }

                        else {
                            IncidentService.getSingleton().getLatest(mContext);
                        }
                        swipeContainer.setRefreshing(false);

                    }


                }, 1000);
                timelineListAdapter.notifyDataSetChanged();

            }

        });



        incidentListView.setOnScrollListener(new AbsListView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,int totalItemCount) {
               loadMoreDataOnScroll(firstVisibleItem,visibleItemCount, totalItemCount);

            }
        }

    );

        incidentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (buttonClicked == 2) {
                    Intent i = new Intent(mContext, WebViewActivity.class);

                    i.putExtra("WEBVIEW_URL", entertainmentArrayList.get(position-1).getUrl());
                    startActivity(i);


                } else if (buttonClicked == 3) {
                    Intent i = new Intent(mContext, WebViewActivity.class);

                    i.putExtra("WEBVIEW_URL", newsArrayList.get(position-1).getUrl());
                    startActivity(i);


                }


            }

        });

    }
    private void loadMoreDataOnScroll(int firstVisibleItem, int visibleItemCount, int totalItemCount){
        int topRowVerticalPosition =
                (incidentListView == null || incidentListView.getChildCount() == 0) ?
                        0 : incidentListView.getChildAt(0).getTop();
        swipeContainer.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
        boolean loadMore = /* maybe add a padding */
                firstVisibleItem + visibleItemCount >= totalItemCount && totalItemCount >= 10;
        if (loadMore) {


            if (buttonClicked == 2 && !loadMoreEntertainment) {
                Log.i("Ankit", "Change Load Entertainment : " + loadMoreEntertainment);
                loadMoreEntertainment = true;
                EntertainmentService.getSingleton().getMore(mContext);
                loadMore = false;
            } else if (buttonClicked == 3 && !loadMoreNews) {
                loadMoreNews = true;
                NewsService.getSingleton().getMore(mContext);
                loadMore = false;
            } else if (!loadMoreIncidents) {
                loadMoreIncidents = true;
                IncidentService.getSingleton().getMore(mContext);
                loadMore = false;
            }


        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.report_incident_button:
                new IncidentCategoryService().getAll(this);
                buttonClicked = 1;
                break;
            case R.id.feed_button:

                buttonClicked = 1;

                incidentsFeedButton.setSelected(true);
                entertainmentButton.setSelected(false);
                newsButton.setSelected(false);
                IncidentService.getSingleton().get(mContext);

                break;
            case R.id.entertainment_button:
                buttonClicked = 2;

                if(settings.getBoolean("entertainment_first_run",true)) {
                    showInstructionPopup();
                    settings.edit().putBoolean("entertainment_first_run", false).commit();

                }
                incidentsFeedButton.setSelected(false);
                entertainmentButton.setSelected(true);
                newsButton.setSelected(false);

                EntertainmentService.getSingleton().get(mContext);

                break;
            case R.id.news_button:
                buttonClicked = 3;

                if(settings.getBoolean("news_first_run",true)) {
                    showInstructionPopup();
                    settings.edit().putBoolean("news_first_run", false).commit();

                }
                incidentsFeedButton.setSelected(false);
                entertainmentButton.setSelected(false);
                newsButton.setSelected(true);

                NewsService.getSingleton().get(mContext);

                break;
            case R.id.sign_out_button:
                SharedPreferences sharedPrefs = getSharedPreferences("NIGERIA_PLEDGE", 0);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("NIGERIA_LOGIN_KEY", null);
                editor.commit();
                Intent i = new Intent(mContext, NigeriaLoginActivity.class);

                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                break;
            default:
                incidentsFeedButton.setSelected(true);
                entertainmentButton.setSelected(false);
                newsButton.setSelected(false);

                break;


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setButtonClicked(-1);



    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("pledge.nigeria.com.nigeriapldge.NEWS_BROADCAST")) {
                if (!loadMoreNews) {
                    newsArrayList = NewsService.getSingleton().getNews();
                    newsAndEntertainmentListArrayAdapter = new NewsAndEntertainmentListArrayAdapter(mContext, R.layout.nigeria_timeline_listview_news_item, newsArrayList);
                    initListView(newsAndEntertainmentListArrayAdapter);
                } else {
                    loadMoreNews = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (newsAndEntertainmentListArrayAdapter != null)

                                newsAndEntertainmentListArrayAdapter.notifyDataSetChanged();
                        }
                    });
                }

            } else if (intent.getAction().equalsIgnoreCase("pledge.nigeria.com.nigeriapldge.ENTERTAINMENT_BROADCAST")) {
                if (!loadMoreEntertainment) {

                    entertainmentArrayList = EntertainmentService.getSingleton().getEntertainment();

                    newsAndEntertainmentListArrayAdapter = new NewsAndEntertainmentListArrayAdapter(mContext, R.layout.nigeria_timeline_listview_news_item, entertainmentArrayList);

                    initListView(newsAndEntertainmentListArrayAdapter);
                } else {
                    loadMoreEntertainment = false;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (newsAndEntertainmentListArrayAdapter != null)

                                newsAndEntertainmentListArrayAdapter.notifyDataSetChanged();
                        }
                    });
                    //incidentListView.invalidate();
                }


            } else if (intent.getAction().equalsIgnoreCase("pledge.nigeria.com.nigeriapldge.INCIDENTS_BROADCAST")) {
                if (!loadMoreIncidents) {

                    incidentsArrayList = IncidentService.getSingleton().getIncidents();
                    Log.i("Kritika","incidentsArrayList :"+incidentsArrayList.size());

                    incidentListArrayAdapter = new IncidentListArrayAdapter(mContext, R.layout.nigeria_timeline_listview_item, incidentsArrayList);

                    initListView(incidentListArrayAdapter);
                } else {
                    loadMoreIncidents = false;


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (incidentListArrayAdapter != null){
                                incidentListArrayAdapter.notifyDataSetChanged();
                                incidentListView.invalidate();
                            }

                        }
                    });
                    //incidentListView.invalidate();
                }

            }

        }


    }

    private void showInstructionPopup(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

        dialog.setContentView(R.layout.instruction_popup_window);
        dialog.setCanceledOnTouchOutside(true);
        TextView popupTextView = (TextView) dialog.findViewById(R.id.popup_textview);
        if(buttonClicked == 2 || buttonClicked == 3)
            popupTextView.setText(R.string.news_instruction_text);
        else
            popupTextView.setText(R.string.incidents_instruction_text);

        View popupViewBtn = dialog.findViewById(R.id.popup_btn);
        popupViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


 }
