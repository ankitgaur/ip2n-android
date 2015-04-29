package com.ip2n.mobile.activities.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ip2n.mobile.R;
import com.ip2n.mobile.activities.dialogs.StateDialog;
import com.ip2n.mobile.models.JosContent;
import com.ip2n.mobile.models.Question;
import com.ip2n.mobile.services.StateService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by kritika_pathak on 2/15/2015.
 */
public class MoreDetailsListArrayAdapter extends ArrayAdapter<Question> implements View.OnClickListener {
    private Context mContext;
    private int layoutResourceId;
    ArrayList<Question> questionArrayList;
    private Map<String,String> ansmap = new HashMap<String,String>();
    private Map<String,String> quesmap = new HashMap<String,String>();
    LayoutInflater inflater;
    private EditText answerEditText;
    private List<String> answerOptions ;
    private EditText[] mEditTextArray;
    private TextView[] mTextViewArray;
    private SharedPreferences mSharedPrefs;
//    private TextView answerTextView;
    private static int position1 = 0;
    private static final int STATE_DIALOG = 1;
    private static final int GOVT_DIALOG = 2;
    private static final int MORE_INFO_DIALOG = 3;
    private static int listPosition = 0;




    public MoreDetailsListArrayAdapter(Context mContext, int layoutResourceId, ArrayList<Question> data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        questionArrayList = data;
        //Kri
        mTextViewArray = new TextView[questionArrayList.size()];



    }
    //TextView questions

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);


        if (convertView == null) {

            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }
        if(position == 0) {
            convertView.findViewById(R.id.send_report_header_layout).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.send_report_more_details_layout).setVisibility(View.GONE);
            mTextViewArray[position] = null;
            return convertView;


        }
        else {
            convertView.findViewById(R.id.send_report_more_details_layout).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.send_report_header_layout).setVisibility(View.GONE);
            Log.i("Kritika", "position : " + position);


            String question = questionArrayList.get(position).getName();
            answerOptions = questionArrayList.get(position).getOptions();




            answerEditText = (EditText) convertView.findViewById(R.id.answer_edittext);




            mEditTextArray = new EditText[questionArrayList.size()];
            mTextViewArray = new TextView[questionArrayList.size()];
            mSharedPrefs = mContext.getSharedPreferences("USER_ANSWERS", 0);


            if (answerOptions.size() == 0) {
                convertView.findViewById(R.id.question_layout).setVisibility(View.GONE);
                convertView.findViewById(R.id.question_edittext_layout).setVisibility(View.VISIBLE);
                TextView questions = (TextView) convertView.findViewById(R.id.question_textview);
                questions.setText(question);




            } else {
                convertView.findViewById(R.id.question_layout).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.question_edittext_layout).setVisibility(View.GONE);
                RelativeLayout questionLayout = (RelativeLayout) convertView.findViewById(R.id.question_layout);
                questionLayout.setTag(position);

                TextView answerTextView = (TextView) convertView.findViewById(R.id.answer_textview);

                if(ansmap.get(""+position)!=null){
                    answerTextView.setText(ansmap.get(""+position));
                }

                Log.i("Ankit","setview "+answerTextView.getText().toString());
                TextView questions = (TextView) convertView.findViewById(R.id.question);
                questions.setText(question);

                questionLayout.setOnClickListener(this);
                ansmap.put(""+position,answerTextView.getText().toString());
                quesmap.put(""+position,question);

            }

            return convertView;
        }






    }


    @Override
    public void onClick(View v) {

                int key = (int) v.getTag();

                TextView selected = (TextView) v.findViewById(R.id.answer_textview);

                //TextView selected = ansmap.get(""+key);
                Question ques = questionArrayList.get(key);
                String[] answers = new String[ques.getOptions().size()];
                ques.getOptions().toArray(answers);
                Log.i("Kritika","position :"+key);
                Log.i("Ankit","onclick "+selected.getText().toString());
                showOptions(answers, "Select " + key, MORE_INFO_DIALOG,key);
    }


    public void showOptions(final String []items , String title, final int id, final int key){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (id){
                    case MORE_INFO_DIALOG :
                        //Kri

                        ansmap.put(""+key,items[item]);
                        notifyDataSetChanged();
                        break;


                }


                dialog.dismiss();

            }
        }).show();
    }

    public JSONObject getJson() throws JSONException {
        JSONObject obj = new JSONObject();

        Set<String> keys = quesmap.keySet();

        for(String key : keys){
            String ques = quesmap.get(key);
            String ans = ansmap.get(key);
            obj.put(ques,ans);
        }

        return obj;
    }
}
