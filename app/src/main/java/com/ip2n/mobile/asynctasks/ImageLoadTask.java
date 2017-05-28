package com.ip2n.mobile.asynctasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.ip2n.mobile.models.Incident;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ankit on 5/5/15.
 */
public class ImageLoadTask extends AsyncTask<Void, Void, Void> {

    private Incident incident;

    public ImageLoadTask(Incident incident){
        this.incident = incident;
    }

    private void loadImage(){
        try {
            Log.i("aimg", "loading image " + incident.getImage());

            if(incident.getImage()!=null && !incident.getImage().trim().isEmpty() && !incident.getImage().trim().equals("null")){
                String imageUrl = "http://dev.insodel.com/api/" + "incidents/image/"+incident.getId();
                //SharedPreferences prefs = mContext.getSharedPreferences("NIGERIA_PLEDGE", 0);
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                //connection.setRequestProperty("Authorization", prefs.getString("NIGERIA_LOGIN_KEY", ""));
                connection.connect();
                InputStream input = connection.getInputStream();
                BitmapFactory.Options options=new BitmapFactory.Options();
                //options.inSampleSize = 4;
                Bitmap b1 = BitmapFactory.decodeStream(input,null,options);
                //int width = incidentImageView.getWidth();
                //int height = (width*b1.getHeight())/b1.getWidth();
                //Bitmap b = Bitmap.createScaledBitmap(b1,width,height,false);
                //b.getByteCount();
                //bitmapMap.put(key,b);
                incident.setBitmap(b1);
                if(b1!=null){
                    Log.d("aimg","loaded image for " + incident.getId());
                }
                else{
                    Log.d("aimg","Not loaded image for " + incident.getId());
                }

            }

        } catch (Exception e) {
            Log.d("aimg",e+" "+e.getMessage());
            e.printStackTrace();
            incident.setBitmap(null);
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        loadImage();
        return null;
    }
}
