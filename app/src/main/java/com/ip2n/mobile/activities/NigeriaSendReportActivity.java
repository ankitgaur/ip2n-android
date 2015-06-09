package com.ip2n.mobile.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ip2n.mobile.R;
import com.ip2n.mobile.activities.adapters.MoreDetailsListArrayAdapter;

import com.ip2n.mobile.models.Question;
import com.ip2n.mobile.models.State;
import com.ip2n.mobile.services.IncidentService;
import com.ip2n.mobile.services.StateService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class NigeriaSendReportActivity extends Activity implements  TextWatcher,LocationListener, View.OnClickListener {

    private ListView moreDetailsListView;

    private Context mContext;


    private long id;
    private Menu menu;


    private ArrayList<Question> questionList;

    private String type;
    private MyReceiver myReceiver;
    private String incidentID = null;
    private EditText dateEditText;
    private EditText timeEditText;
    private TextView stateEditText;
    private TextView govtEditText;
    //private EditText descEditText;
    private MoreDetailsListArrayAdapter moreDetailsListArrayAdapter;
    private Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener date;
    private TimePickerDialog.OnTimeSetListener time;
    private static boolean isDateDialogShowing = false;
    private static boolean isTimeDialogShowing = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nigeria_send_report);
        mContext = this;
        String questions = getIntent().getStringExtra("questions");
        this.type = getIntent().getStringExtra("type");
        questionList = getQuestionMap(questions);
        Log.i("Kritika","Question List : "+questionList);

    }

    @Override
    protected void onResume() {
        super.onResume();
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("pledge.nigeria.com.nigeriapldge.UPLOAD_IMAGE");
        registerReceiver(myReceiver, intentFilter);
        initActionBar();
        init();
    }

    private void initActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        String category = type.split("\\|")[1].charAt(0) + type.split("\\|")[1].substring(1, type.split("\\|")[1].length()).toLowerCase();
        actionBar.setTitle(("Report: " + category));

    }


    @Override
    protected void onPause() {

        super.onPause();
        unregisterReceiver(myReceiver);
    }


    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("pledge.nigeria.com.nigeriapldge.UPLOAD_IMAGE")) {
                String id = intent.getStringExtra("id");
                if (id == null)
                    id = "1";
                incidentID = id;

                showInstructionPopup();
            }

        }


    }


    private void init() {

        moreDetailsListView = (ListView) findViewById(R.id.more_details_listview);
        LayoutInflater inflater = ((Activity) this).getLayoutInflater();
        View convertView = inflater.inflate(R.layout.send_report_list_header_layout, null, false);
        if (moreDetailsListView.getHeaderViewsCount() <= 0) {
            initHeaderLayout(convertView);
            moreDetailsListView.addHeaderView(convertView);
        }
        moreDetailsListArrayAdapter = new MoreDetailsListArrayAdapter(mContext, R.layout.viewpager_item, questionList);

        moreDetailsListView.setAdapter(moreDetailsListArrayAdapter);
        moreDetailsListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager im = (InputMethodManager) mContext.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(v.getWindowToken(), 0);

                return false;
            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_nigeria_send_report, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_cancel:
                this.finish();
                break;
            case R.id.action_save:
                //Save Report here
                try {
                    Log.i("Ankitn","SENDING rEPORT");
                    sendReport();
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Report could not be submitted",
                            Toast.LENGTH_LONG).show();
                }
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void sendReport() throws JSONException {
        JSONObject obj = moreDetailsListArrayAdapter.getJson();

        Log.d("Ankit523",obj.toString());

        JSONObject json = new JSONObject();

        //put createdOn
        json.put("createdOn", new Date().getTime());
        json.put("createdBy", "admin");

        //put incident type
        json.put("type", type.split("\\|")[1]);

        //put reportDate
        try {
            //Log.i("Ankitn",dateEditText.getText().toString());
            //Log.i("Ankitn",timeEditText.getText().toString());
            Long reportDt = new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(dateEditText.getText().toString() + " " + timeEditText.getText().toString()).getTime();
            json.put("reportDate", reportDt);
        } catch (ParseException e) {
            json.put("reportDate", null);
        }

        //put state
        String state = stateEditText.getText().toString();
        json.put("state", state);

        //put govt
        String govt = govtEditText.getText().toString();
        json.put("govt", govt);

        //put description
        //String description = descEditText.getText().toString();
        json.put("description", obj.getString("Brief Description"));

        //put questions
        json.put("questions", obj.toString());

        Log.i("Ankitn","Json object : "+json);
        //call IncidentService create
        IncidentService.getSingleton().create(json,mContext);

    }

    public class Uploader {
        public void upload() {

            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);
        }


    }

    Bitmap bitmap;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            String filePath = null;

            try {
                // OI FILE Manager
                String filemanagerstring = selectedImageUri.getPath();

                // MEDIA GALLERY
                String selectedImagePath = getPath(selectedImageUri);

                if (selectedImagePath != null) {
                    filePath = selectedImagePath;
                } else if (filemanagerstring != null) {
                    filePath = filemanagerstring;
                } else {
                    showErrorDialog();

                }

                if (filePath != null) {
                    File file = new File(filePath);
                    if (file.exists()) {
                        IncidentService.getSingleton().uploadFile("incident_image", file, mContext, incidentID);
                    }
                    else{
                        showErrorDialog();
                    }
                    // decodeFile(filePath);
                } else {
                    Log.i("Kritika","Image Error Bitmap null!!!");

                    bitmap = null;
                    showErrorDialog();

                }
            } catch (Exception e) {
                Log.i("Kritika","Image Error!!!");
                showErrorDialog();
            }
        }
        else{
            showErrorDialog();

        }


    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }


    public static ArrayList<Question> getQuestionMap(String questions) {
        ArrayList<Question> quest = null;
        //quest.add(null);
        Log.d("Ankit521",questions);
        try {
            JSONArray arr = new JSONArray(questions);
            if(arr!=null){
                int count = arr.length();
                Log.d("Ankit521","size : "+count);
                quest = new ArrayList<Question>();
                for(int x=0;x<count;x++){
                    JSONObject obj = arr.getJSONObject(x);
                    Question q = new Question();
                    q.setQuestion(obj.getString("question"));
                    q.setOrder(obj.getInt("order"));
                    q.setRequired(obj.getBoolean("required"));
                    Log.d("Ankit521",""+q.getQuestion());
                    String tmp = obj.getString("options");
                    Log.d("Ankit521","checking tmp is null");
                    if(tmp!=null && !tmp.isEmpty() && !tmp.equals("null"))
                    {
                        Log.d("Ankit521",tmp);
                        JSONArray opns = new JSONArray(tmp);
                        ArrayList<String> options = new ArrayList<String>();
                        for (int i = 0; i < opns.length(); i++) {

                            options.add(opns.getString(i));
                        }
                        q.setOptions(options);

                    }
                    Log.d("Ankit521",""+q);

                    quest.add(q);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Ankit521",e.getMessage());
        }

        Collections.sort(quest, new Comparator<Question>() {

            @Override
            public int compare(Question lhs, Question rhs) {
                return 0;
            }
        });
        Log.d("Ankit521","Returning Array of size "+quest.size());
        return quest;
    }

    public void showInstructionPopup() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

        dialog.setContentView(R.layout.show_image_popup_window);
        dialog.setCanceledOnTouchOutside(true);
        TextView popupTextView = (TextView) dialog.findViewById(R.id.show_image_textview);
        popupTextView.setText("Do you wish to upload an image for the incident?");

        View browseBtn = dialog.findViewById(R.id.browse_btn);
        browseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Uploader().upload();
                dialog.dismiss();
            }
        });
        View noBtn = dialog.findViewById(R.id.no_btn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext).setTitle("Report Submitted")
                        .setMessage("Thanks for your valuable feedback! Your report will form part of our analysis on www.ipledge2nigeria.com. ")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(mContext, NigeriaTimelineActivity.class);
                                i.putExtra("add_new_incident",true);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                mContext.startActivity(i);
                            }
                        });
                alertDialogBuilder.create().show();
            }
        });
        dialog.show();
    }

    private void showErrorDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext).setTitle("Report Submitted")
                .setMessage("There was some error in uploading the image, report has been received. We are looking into the matter!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(mContext, NigeriaTimelineActivity.class);
                        i.putExtra("add_new_incident",true);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                        mContext.startActivity(i);
                    }
                });
        alertDialogBuilder.create().show();
    }



    public void initHeaderLayout(View convertView) {
        RelativeLayout stateLayout = (RelativeLayout) convertView.findViewById(R.id.state_layout);
        RelativeLayout govtLayout = (RelativeLayout) convertView.findViewById(R.id.govt_layout);

        stateLayout.setOnClickListener(this);
        govtLayout.setOnClickListener(this);

        ImageView stateArrowImageView = (ImageView) convertView.findViewById(R.id.state_arrow_imageview);
        ImageView govtArrowImageView = (ImageView) convertView.findViewById(R.id.govt_arrow_imageview);

        stateArrowImageView.setOnClickListener(this);
        govtArrowImageView.setOnClickListener(this);
        //if (descEditText == null) {
        //    descEditText = (EditText) convertView.findViewById(R.id.description_edittext);
        //}



        if (stateEditText == null) {
            stateEditText = (TextView) convertView.findViewById(R.id.stateEditText);
        }
        if (govtEditText == null) {
            govtEditText = (TextView) convertView.findViewById(R.id.govtEditText);
        }
        if (stateEditText.getText().toString().isEmpty()) {
            stateEditText.setVisibility(View.GONE);
        } else {
            stateEditText.setVisibility(View.VISIBLE);
        }
        if (govtEditText.getText().toString().isEmpty()) {
            govtEditText.setVisibility(View.GONE);
        } else {
            govtEditText.setVisibility(View.VISIBLE);
        }
        stateEditText.addTextChangedListener(this);
        govtEditText.addTextChangedListener(this);


        if (checkLocationEnabled()) {
            if (getLocation() != null) {
                String state = getLocation().get(0).getAdminArea();
                do {
                    if (StateService.isFetched() && StateService.getStateByName(state) != null) {
                        stateEditText.setVisibility(View.VISIBLE);
                        stateEditText.setText(state);
                    }
                } while (!StateService.isFetched());
            }

        }
        if (dateEditText == null) {
            dateEditText = (EditText) convertView.findViewById(R.id.dateEditText);
        }
        if (timeEditText == null) {
            timeEditText = (EditText) convertView.findViewById(R.id.timeEditText);
        }

        dateEditText.addTextChangedListener(this);
        timeEditText.addTextChangedListener(this);


        myCalendar = Calendar.getInstance();


        date = new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                isDateDialogShowing = false;

                // TODO Auto-generated method stub

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateDate();

            }


        };
        time = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                isTimeDialogShowing = false;

                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                String reportTime = hourOfDay + ":" + minute + ":" + "00";
                Log.i("Kritika", "hour of the DAY :" + hourOfDay);
                if (12 - hourOfDay > 0) {
                    Log.i("Kritika", "AM");
                    if (hourOfDay == 0 && minute >= 10)
                        timeEditText.setText("12" + ":" + minute + " " + "AM");
                    else if (hourOfDay == 0 && minute <= 10)
                        timeEditText.setText("12" + ":" + "0" + minute + " " + "AM");

                    else if (hourOfDay < 10 && minute >= 10)
                        timeEditText.setText("0" + hourOfDay + ":" + minute + " " + "AM");
                    else if (hourOfDay < 10 && minute < 10)
                        timeEditText.setText("0" + hourOfDay + ":" + "0" + minute + " " + "AM");
                    else if (hourOfDay >= 10 && minute < 10)
                        timeEditText.setText(hourOfDay + ":" + "0" + minute + " " + "AM");
                    else if (hourOfDay >= 10 && minute >= 10)
                        timeEditText.setText(hourOfDay + ":" + minute + " " + "AM");


                } else {
                    if (hourOfDay - 12 == 0 && minute >= 10)
                        timeEditText.setText("12" + ":" + minute + " " + "PM");
                    else if (hourOfDay - 12 == 0 && minute < 10)
                        timeEditText.setText("12" + ":" + "0" + minute + " " + "PM");
                    else if (hourOfDay - 12 < 10 && minute >= 10)
                        timeEditText.setText("0" + (hourOfDay - 12) + ":" + minute + " " + "PM");
                    else if (hourOfDay - 12 < 10 && minute < 10)
                        timeEditText.setText("0" + (hourOfDay - 12) + ":" + "0" + minute + " " + "PM");
                    else if (hourOfDay - 12 >= 10 && minute < 10)
                        timeEditText.setText(hourOfDay - 12 + ":" + "0" + minute + " " + "PM");
                    else if (hourOfDay - 12 >= 10 && minute >= 10)
                        timeEditText.setText(hourOfDay - 12 + ":" + minute + " " + "PM");
                }

            }
        };


        dateEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isDateDialogShowing)
                    return true;

                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isDateDialogShowing = false;
                    }
                });

                if (!datePickerDialog.isShowing()) {
                    isDateDialogShowing = true;
                    datePickerDialog.show();
                }

                return true;
            }
        });

        timeEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isTimeDialogShowing)
                    return true;
                TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, time, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), false);
                timePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isTimeDialogShowing = false;
                    }
                });
                if (!timePickerDialog.isShowing()) {
                    isTimeDialogShowing = true;
                    timePickerDialog.show();
                }
                return true;
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(stateEditText.getText().toString().isEmpty()){
            stateEditText.setVisibility(View.GONE);
        }
        else {
            stateEditText.setVisibility(View.VISIBLE);
        }
        if(govtEditText.getText().toString().isEmpty()){
            govtEditText.setVisibility(View.GONE);
        }
        else {
            govtEditText.setVisibility(View.VISIBLE);
        }
        if (dateEditText == null || timeEditText == null || stateEditText == null || govtEditText == null) {
            return;
        } else {

            if (dateEditText.getText().toString().isEmpty() || timeEditText.getText().toString().isEmpty() ||
                    stateEditText.getText().toString().isEmpty() || govtEditText.getText().toString().isEmpty()) {
                menu.getItem(1).setEnabled(false);
            } else {
                menu.getItem(1).setEnabled(true);

            }
        }


    }

    @Override
    public void afterTextChanged(Editable s) {

    }



    private void updateDate() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateEditText.setText(sdf.format(myCalendar.getTime()));
    }
    public void showOptions(final String []items , String title, final int id){

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(mContext, R.style.AlertDialogCustom));
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View customTitleView = inflater.inflate(R.layout.custom_dialog_title,null,false);
        TextView dialogTitleTextView = (TextView)customTitleView.findViewById(R.id.dialog_title_textview);
        dialogTitleTextView.setText(title+" "+":");

        builder.setCustomTitle(customTitleView);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (id){
                    case 1:
                        stateEditText.setText(items[item]);
                        String state = stateEditText.getText().toString();
                        State st = StateService.getStateByName(state);
                        if (st != null) {
                            String currGovt = st.getCurrGovt();
                            govtEditText.setText(currGovt);
                        }
                        break;
                    case 2:
                        govtEditText.setText(items[item]);
                        break;


                }


                dialog.dismiss();

            }
        }).show();
    }
    @Override
    public void onClick(View v) {
        InputMethodManager im = (InputMethodManager) mContext.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(v.getWindowToken(), 0);
        switch (v.getId()) {
            case R.id.state_layout:
            case R.id.state_arrow_imageview:
                showOptions(StateService.getStates(), "Select State", 1);
                break;
            case R.id.govt_layout:
            case R.id.govt_arrow_imageview:
                String state = stateEditText.getText().toString();
                if (!(stateEditText.getVisibility() == View.VISIBLE)) {
                    Toast.makeText(mContext, "Please select a valid State!",
                            Toast.LENGTH_LONG).show();
                    return;

                } else {
                    State st = StateService.getStateByName(state);
                    if (st == null) {
                        Toast.makeText(mContext, "Please select a valid State!",
                                Toast.LENGTH_LONG).show();
                    } else {
                        String[] stts = new String[st.getGovts().size()];
                        st.getGovts().toArray(stts);
                        showOptions(stts, "Select Govt", 2);
                    }

                }
                break;
        }
    }
    private boolean checkLocationEnabled() {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }

    }
    private List<Address> getLocation(){
        LocationManager locManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);

        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000L,500.0f, this);
        Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        List<Address> addresses = null;

        double latitude=0;
        double longitude=0;
        if(location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  addresses;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}