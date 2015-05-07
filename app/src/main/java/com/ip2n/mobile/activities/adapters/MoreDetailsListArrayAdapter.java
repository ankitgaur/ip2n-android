package com.ip2n.mobile.activities.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.ip2n.mobile.R;
import com.ip2n.mobile.activities.DataTransferInterface;
import com.ip2n.mobile.models.JosContent;
import com.ip2n.mobile.models.Question;


import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * Created by kritika_pathak on 2/15/2015.
 */
public class MoreDetailsListArrayAdapter extends ArrayAdapter<Question> implements View.OnClickListener {
    private Context mContext;
    private int layoutResourceId;
    ArrayList<Question> questionArrayList;
    private Map<String, String> ansmap = new HashMap<String, String>();
    private Map<String, String> quesmap = new HashMap<String, String>();
    LayoutInflater inflater;
    private EditText answerEditText;
    private List<String> answerOptions;

    private static final int MORE_INFO_DIALOG = 3;


    private boolean enableSave = false;
    private DataTransferInterface dataTransferInterface;

    private String answerText = null;


    public MoreDetailsListArrayAdapter(Context mContext, int layoutResourceId, ArrayList<Question> data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        questionArrayList = data;
        this.dataTransferInterface = dataTransferInterface;


    }
    //TextView questions

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        if (convertView == null) {

            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }


        String question = questionArrayList.get(position).getName();
        answerOptions = questionArrayList.get(position).getOptions();


        answerEditText = (EditText) convertView.findViewById(R.id.answer_edittext);
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(answerEditText.getWindowToken(), 0);




        if (answerOptions.size() == 0) {
            convertView.findViewById(R.id.question_layout).setVisibility(View.GONE);
            convertView.findViewById(R.id.question_edittext_layout).setVisibility(View.VISIBLE);
            TextView questions = (TextView) convertView.findViewById(R.id.question_textview);
            questions.setText(question);
            EditText et = (EditText)convertView.findViewById(R.id.answer_edittext);
            et.setTag(position);

            et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus) {
                        EditText vet = (EditText)v;
                        int key = (int) vet.getTag();
                        ansmap.put(""+key,vet.getText().toString());
                    }
                }
            });

            quesmap.put(""+position,question);
            if(ansmap.get(""+position)!=null)
            {
                et.setText(ansmap.get(""+position));
            }

        } else {
            convertView.findViewById(R.id.question_layout).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.question_edittext_layout).setVisibility(View.GONE);
            RelativeLayout questionLayout = (RelativeLayout) convertView.findViewById(R.id.question_sub_layout);
            ImageView questionImageView = (ImageView) convertView.findViewById(R.id.more_details_arrow_imageview);

            questionLayout.setTag(position);
            questionImageView.setTag(position);


            TextView answerTextView = (TextView) convertView.findViewById(R.id.answer_textview);
            answerTextView.setVisibility(View.GONE);


            if (ansmap.get("" + position) != null && ansmap.get("" + position).length() > 0) {
                answerTextView.setText(ansmap.get("" + position));
                answerTextView.setVisibility(View.VISIBLE);

            } else {
                answerTextView.setVisibility(View.GONE);

            }

            TextView questions = (TextView) convertView.findViewById(R.id.question);
            questions.setText(question);

            questionLayout.setOnClickListener(this);
            questionImageView.setOnClickListener(this);
            ansmap.put("" + position, answerTextView.getText().toString());
            quesmap.put("" + position, question);

        }

        return convertView;


    }


    public void showOptions(final String[] items, String title, final int id, final int key) {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(mContext, R.style.AlertDialogCustom));
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View customTitleView = inflater.inflate(R.layout.custom_dialog_title, null, false);
        TextView dialogTitleTextView = (TextView) customTitleView.findViewById(R.id.dialog_title_textview);
        dialogTitleTextView.setText(title + " " + ":");

        builder.setCustomTitle(customTitleView);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (id) {
                    case MORE_INFO_DIALOG:
                        ansmap.put("" + key, items[item]);
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

        for (String key : keys) {
            String ques = quesmap.get(key);
            String ans = ansmap.get(key);
            obj.put(ques, ans);
        }

        return obj;
    }

    @Override
    public void onClick(View v) {
        InputMethodManager im = (InputMethodManager) mContext.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(v.getWindowToken(), 0);
        switch (v.getId()) {

            case R.id.question_sub_layout:
            case R.id.more_details_arrow_imageview:
                int key = (int) v.getTag();

                TextView selected = (TextView) v.findViewById(R.id.answer_textview);

                //TextView selected = ansmap.get(""+key);
                Question ques = questionArrayList.get(key);
                String[] answers = new String[ques.getOptions().size()];
                ques.getOptions().toArray(answers);
                showOptions(answers, "Select " + ques.getName(), MORE_INFO_DIALOG, key);
                break;

        }

    }



}