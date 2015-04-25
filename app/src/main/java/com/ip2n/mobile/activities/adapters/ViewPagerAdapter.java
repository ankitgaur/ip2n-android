package com.ip2n.mobile.activities.adapters;

/**
 * Created by kritika_pathak on 4/9/2015.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.ip2n.mobile.R;
import com.ip2n.mobile.models.Question;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
    // Declare Variables
    Context context;
    List<Question> questionArrayList;

    LayoutInflater inflater;
    private EditText answerEditText;
    private Spinner answerOptionsSpinner;
    private List<String> answerOptions ;
    private EditText[] mEditTextArray;
    private Spinner[] mSpinnerArray;
    private SharedPreferences mSharedPrefs;


    public ViewPagerAdapter(Context context, List<Question> questionArrayList) {
        this.context = context;
        this.questionArrayList = questionArrayList;
        mEditTextArray = new EditText[questionArrayList.size()];
        mSpinnerArray = new Spinner[questionArrayList.size()];
        mSharedPrefs = context.getSharedPreferences("USER_ANSWERS",0);
    }

    @Override
    public int getCount() {
        return questionArrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        Log.i("Kritika", "Instantiate Item for pos: " + position);
        // Declare Variables
        TextView questions;


        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container,
                false);

        answerEditText = (EditText)itemView.findViewById(R.id.answer_edittext);
        answerOptionsSpinner = (Spinner) itemView.findViewById(R.id.answer_spinner);


        answerOptions =  questionArrayList.get(position).getOptions();

        //Why are we instantiating a new array every time a new item is created?
        //mEditTextArray = new EditText[questionArrayList.size()];
        //mSpinnerArray = new Spinner[questionArrayList.size()];


        if(answerOptions.size() == 0){
            answerEditText.setVisibility(View.VISIBLE);
            answerOptionsSpinner.setVisibility(View.GONE);
            itemView.findViewById(R.id.spinner_layout).setVisibility(View.GONE);
            if(mEditTextArray[position] !=null){
                answerEditText.setText(mEditTextArray[position].getText());
            }
        }
        else{
            answerEditText.setVisibility(View.GONE);
            itemView.findViewById(R.id.spinner_layout).setVisibility(View.VISIBLE);
            ArrayAdapter answerOptionsAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, answerOptions);
            answerOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            answerOptionsSpinner.setAdapter(answerOptionsAdapter);

            if(mSpinnerArray[position] !=null){
                answerOptionsSpinner.setSelection(mSpinnerArray[position].getSelectedItemPosition());
            }
        }

        //why were these inside if else?
        mEditTextArray[position] = answerEditText;
        mSpinnerArray[position] = answerOptionsSpinner;

        if(position == 0)
            itemView.findViewById(R.id.slide_left_imageview).setVisibility(View.INVISIBLE);
        if(position == questionArrayList.size()-1)
            itemView.findViewById(R.id.slide_right_imageview).setVisibility(View.INVISIBLE);





        // Locate the TextViews in viewpager_item.xml
        questions = (TextView) itemView.findViewById(R.id.question);

        // Capture position and set to the TextViews
        questions.setText(questionArrayList.get(position).getName());




        // Add viewpager_item.xml to ViewPager
        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

    public EditText getEditTextItem(int pos) {
        return mEditTextArray[pos];
    }
    public Spinner getSpinnerItem(int pos) {
        return mSpinnerArray[pos];
    }
    public JSONObject getJson() throws JSONException {
        JSONObject obj = new JSONObject();


        for(int i = 0; i<questionArrayList.size();i++){
            EditText etext = mEditTextArray[i];
            Spinner spinner = mSpinnerArray[i];
            String ques = questionArrayList.get(i).getName();
            String ans = null;
            if(etext != null && etext.getVisibility()==View.VISIBLE){

                ans = etext.getText().toString();
            }else if(spinner != null && spinner.getVisibility()==View.VISIBLE){
                ans = spinner.getSelectedItem().toString();
            }
            obj.put(ques,ans);
        }

        return obj;
    }
}