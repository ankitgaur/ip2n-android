package com.ip2n.mobile.activities.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;

import com.ip2n.mobile.activities.NigeriaSendReportActivity;
import com.ip2n.mobile.models.State;
import com.ip2n.mobile.services.StateService;

/**
 * Created by ankit on 4/26/15.
 */
public class StateDialog {

    private Context mContext;
    private AlertDialog self;

    public StateDialog(Context context) {

        mContext = context;

    }

    public void showOptions(final String []items){
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setTitle("Select State");
//        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                NigeriaSendReportActivity.setIsStateDialogShowing(false);
//
//            }
//        });
//        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int item) {
//
//                NigeriaSendReportActivity.setStateEditText(items[item]);
//                NigeriaSendReportActivity.setIsStateDialogShowing(false);
//
//                String state = NigeriaSendReportActivity.getStateEditText().getText().toString();
//                State st = StateService.getStateByName(state);
//                if(st!=null){
//                    String currGovt = st.getCurrGovt();
//                    NigeriaSendReportActivity.setGovtEditText(currGovt);
//                }
//
//                dialog.dismiss();
//
//            }
//        }).show();
//        NigeriaSendReportActivity.setIsStateDialogShowing(true);
    }

}
