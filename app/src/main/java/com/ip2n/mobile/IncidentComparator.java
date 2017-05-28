package com.ip2n.mobile;

import android.util.Log;

import com.ip2n.mobile.models.Incident;

import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 * Created by ankitgaur on 9/3/16.
 */
public class IncidentComparator implements Comparator<Incident> {

    @Override
    public int compare(Incident o1, Incident o2) {

        SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy");
        // descending order (ascending order would be:
        // o1.getGrade()-o2.getGrade())
        long diff = o2.getCreated() - o1.getCreated();

        if(diff>0L){
            Log.i("Kritika sorted : ",o2.getCreated()+":"+o1.getCreated()+":"+"1");
            return 1;

        }
        if(diff<0L){
            Log.i("Kritika sorted : ",o2.getCreated()+":"+o1.getCreated()+":"+"-1");
            return -1;
        }

        Log.i("Kritika sorted : ",o2.getCreated()+":"+o1.getCreated()+":"+"0");
        return 0;
    }


}
