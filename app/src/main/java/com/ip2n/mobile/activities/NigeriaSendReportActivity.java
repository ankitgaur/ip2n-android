package com.ip2n.mobile.activities;

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
import android.graphics.BitmapFactory;
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
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Response;
import com.ip2n.mobile.R;
import com.ip2n.mobile.activities.adapters.IncidentListArrayAdapter;
import com.ip2n.mobile.activities.adapters.MultipartRequest;
import com.ip2n.mobile.activities.adapters.NewsAndEntertainmentListArrayAdapter;
import com.ip2n.mobile.activities.adapters.ViewPagerAdapter;
import com.ip2n.mobile.models.Question;
import com.ip2n.mobile.services.EntertainmentService;
import com.ip2n.mobile.services.IncidentService;
import com.ip2n.mobile.services.NewsService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;



public class NigeriaSendReportActivity extends Activity implements LocationListener{
    private static boolean isDateDialogShowing = false;
    private static boolean isTimeDialogShowing = false;

    private Context mContext;

    private Button submitButton;

    private long id;

    private EditText dateEditText;
    private EditText timeEditText;
    private Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener date;
    private TimePickerDialog.OnTimeSetListener time;
    private String reportTime = null;
    private List<Question> questionList;
    private ViewPager questionsViewPager;
    private Button nextButton;
    private String type;
    private MyReceiver myReceiver;
    private String incidentID = null;
    private LocationManager lManager;
    private Location location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_nigeria_send_report);
        mContext = this;
        String questions = getIntent().getStringExtra("questions");
        this.type = getIntent().getStringExtra("type");
        questionList = getQuestionMap(questions);

    }

    @Override
    protected void onResume() {
        super.onResume();
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("pledge.nigeria.com.nigeriapldge.UPLOAD_IMAGE");
        registerReceiver(myReceiver, intentFilter);
        init();
    }
    @Override
    protected void onPause() {

        super.onPause();
        unregisterReceiver(myReceiver);
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
        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000L,500.0f, this);
        Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        List<Address> addresses = null;

        double latitude=0;
        double longitude=0;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
             addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
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

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("pledge.nigeria.com.nigeriapldge.UPLOAD_IMAGE")) {
                String id = intent.getStringExtra("id");
                if(id == null)
                    id = "1";
                incidentID = id;

                showInstructionPopup();
            }

        }


    }
    private void init() {
        questionsViewPager = (ViewPager) findViewById(R.id.question_view_flipper);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(mContext, questionList);
        questionsViewPager.setAdapter(adapter);
        TextView categoryTextView = (TextView) findViewById(R.id.category_textview);
        String category = type.split("\\|")[1].charAt(0)+  type.split("\\|")[1].substring(1,type.split("\\|")[1].length()).toLowerCase();


        categoryTextView.setText("Report Type : " + category);
        if(checkLocationEnabled()){
            Log.i("Kritika","Location : "+getLocation().get(0).getLocality());
            EditText stateEditText = (EditText)findViewById(R.id.stateEditText);
            stateEditText.setText(getLocation().get(0).getLocality());
        }




            nextButton = (Button) findViewById(R.id.next_button);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (questionList.size() - 1 > questionsViewPager.getCurrentItem()) {
                        Log.i("Kritika", "Array Size :" + questionList.size() + "Current Item : " + questionsViewPager.getCurrentItem());
                        questionsViewPager.setCurrentItem(questionsViewPager.getCurrentItem() + 1);

                    }


                }
            });
            questionsViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    Log.i("Kritika", "Page selected :" + position);

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                    if (questionList.size() - 1 > questionsViewPager.getCurrentItem()) {
                        submitButton.setVisibility(View.GONE);
                        nextButton.setVisibility(View.VISIBLE);

                    } else {
                        submitButton.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.GONE);

                    }



                }
            });



            dateEditText = (EditText) findViewById(R.id.dateEditText);
            timeEditText = (EditText) findViewById(R.id.timeEditText);


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
//                if(datePickerDialog.isShowing())
//                    datePickerDialog.cancel();
                }


            };
            time = new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    isTimeDialogShowing = false;

                    myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    myCalendar.set(Calendar.MINUTE, minute);
                    reportTime = hourOfDay + ":" + minute + ":" + "00";
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


            submitButton = (Button) findViewById(R.id.submit_button);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dateEditText.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please fill Date Field before proceeding!",
                                Toast.LENGTH_LONG).show();

                    }
                    else if(timeEditText.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Please fill Time Field before proceeding!",
                                Toast.LENGTH_LONG).show();

                    }

                    else {

                        String questions = null;

                        JSONObject obj = null;
                        try {
                            obj = adapter.getJson();
                            questions = obj.toString();
                        } catch (JSONException e) {
                            obj = new JSONObject();
                            questions = new JSONObject().toString();
                        }

                        String state = "null";
                        String govt = "null";
                        try {
                            state = obj.getString("state");
                            govt = obj.getString("govt");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        String description = ((EditText) findViewById(R.id.description_edittext)).getText().toString();
                        try {
                            JSONObject reportDetails = new JSONObject();
                            //reportDetails.put("createdOn", String.valueOf(new Date().getTime()));
                            reportDetails.put("createdOn", new Date().getTime());
                            reportDetails.put("description", description);
                            reportDetails.put("type", type);
                            reportDetails.put("state", state);
                            reportDetails.put("govt", govt);
                            reportDetails.put("questions", questions);
                            try {
                                Long reportDt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateEditText.getText().toString() + " " + reportTime).getTime();
                                reportDetails.put("reportDate", reportDt);
                            } catch (ParseException e) {
                                reportDetails.put("reportDate", null);
                            }
                            reportDetails.put("createdBy", "admin");
                            IncidentService.getSingleton().create(reportDetails, mContext);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }


                }
            });

        }





    private void updateDate() {

        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateEditText.setText(sdf.format(myCalendar.getTime()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nigeria_send_report, menu);
        return true;
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

    public class Uploader {
        public void upload() {

            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);
        }


    }
    Bitmap bitmap;
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
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
                    Toast.makeText(getApplicationContext(), "Unknown path",
                            Toast.LENGTH_LONG).show();
                    Log.e("Bitmap", "Unknown path");
                }

                if (filePath != null) {
                    File file = new File(filePath);
                    if(file.exists()) {
                        IncidentService.getSingleton().uploadFile("incident_image", file, mContext, incidentID);
                    }
                   // decodeFile(filePath);
                } else {
                    bitmap = null;
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Image cannot be uploaded.",
                        Toast.LENGTH_LONG).show();
            }
        }


    }
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
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


    public static List<Question> getQuestionMap(String questions){
        List<Question> quest = new ArrayList<Question>();

        InputStreamReader ireader = new InputStreamReader(new ByteArrayInputStream(questions.getBytes()));

        JsonReader reader = new JsonReader(ireader);

        try {
            reader.beginObject();
            while(reader.hasNext()){
                Question q = new Question();

                q.setName(reader.nextName());
                List<String> options = new ArrayList<String>();
                if(reader.peek() != JsonToken.NULL){
                    reader.beginArray();
                    while(reader.hasNext()){
                        options.add(reader.nextString());
                    }
                    reader.endArray();
                }else{
                    reader.nextNull();
                }
                q.setOptions(options);
                quest.add(q);
            }reader.endObject();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return quest;
    }
    public void showInstructionPopup(){
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
                        .setMessage("Report has been received. We are looking into the matter!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(mContext, NigeriaTimelineActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                mContext.startActivity(i);
                            }
                        });
                alertDialogBuilder.create().show();
            }
        });
        dialog.show();
    }


}
