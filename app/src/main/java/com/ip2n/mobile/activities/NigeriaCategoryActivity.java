package com.ip2n.mobile.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ip2n.mobile.R;
import com.ip2n.mobile.models.Question;
import com.ip2n.mobile.services.IncidentCategoryService;
import com.ip2n.mobile.services.IncidentService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NigeriaCategoryActivity extends Activity {
    public static boolean responseTimeService = false;
    public static boolean nigeriaStateService = false;
    List<String> categoryId = null;
    private ListView mCategoryListView;
    private Context mContext;
    private List<String> categories;
    private SharedPreferences mSharedPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nigeria_category);
        mContext = this;

        categories = getIntent().getStringArrayListExtra("categories");

        initListView();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nigeria_category, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void initListView() {
        Resources res = this.getResources();
        List<String> values = null;

        if (categories != null) {
            values = new ArrayList<String>();
            categoryId = new ArrayList<String>();
            int i = 0;
            for (String cat : categories) {
                values.add(cat.split("\\|")[1]);
                categoryId.add(cat.split("\\|")[0]);

                i++;
            }


            // Define a new Adapter
            // First parameter - Context
            // Second parameter - Layout for the row
            // Third parameter - ID of the TextView to which the data is written
            // Forth - the Array of data


            Log.i("Kritika", "" + values);
            Log.i("Kritika", "id array:" + categoryId);

            ArrayAdapter categoryAdapter = new ArrayAdapter(mContext, R.layout.category_gridview_item, R.id.category, values);
            mCategoryListView = (ListView) findViewById(R.id.category_list_view);
            mCategoryListView.setAdapter(categoryAdapter);


            mCategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String idt = null;
                    if(categoryId != null) {
                         idt = categoryId.get(position);
                    }
                    String questions = null;
                    if(IncidentCategoryService.getTypeById(idt) != null) {

                        questions = IncidentCategoryService.getTypeById(idt).getQuestions();
                    }

                    Intent i = new Intent(mContext, NigeriaSendReportActivity.class);
                    i.putExtra("questions", questions);
                    i.putExtra("type", categories.get(position));
                    mContext.startActivity(i);
                }
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





}
