package com.ip2n.mobile.activities.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.ip2n.mobile.activities.NigeriaSendReportActivity;
import com.ip2n.mobile.models.State;
import com.ip2n.mobile.services.StateService;

/**
 * Created by ankit on 4/26/15.
 */
public class GovtDialog {

    private Context mContext;
    private AlertDialog self;

    public GovtDialog(Context context) {

        mContext = context;

    }

    public void showOptions(final String []items){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Select Government");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                NigeriaSendReportActivity.setGovtEditText(items[item]);
                NigeriaSendReportActivity.setIsGovtDialogShowing(false);

                dialog.dismiss();

            }
        }).show();
        NigeriaSendReportActivity.setIsGovtDialogShowing(true);
    }

}
