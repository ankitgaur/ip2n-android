package com.ip2n.mobile.activities.adapters;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ip2n.mobile.R;
import com.ip2n.mobile.models.Incident;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by kritika_pathak on 2/15/2015.
 */
public class IncidentListArrayAdapter extends ArrayAdapter<Incident> {
    Context mContext;
    int layoutResourceId;
    ArrayList<Incident> data = null;
    private String imageUrl;
    private ImageView incidentImageView;

    public IncidentListArrayAdapter(Context mContext, int layoutResourceId, ArrayList<Incident> data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
        Log.i("Kritika","size : "+data.size());

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("Kritika","size : "+data.size());


        Log.i("Kritika","Image  :"+ "Get View");

        if (convertView == null) {

            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        Incident objectItem = data.get(position);

        // get the TextView and then set the text (item name) and tag (item ID) values
        TextView userName = (TextView) convertView.findViewById(R.id.user_name);
        userName.setText("Incident Reported By" + " " + objectItem.getCreatedBy());
        TextView description = (TextView) convertView.findViewById(R.id.incident_desc);
        String desc = objectItem.getDescription();
        if(desc.trim().equals("null")){
            desc = "";
        }

        description.setText(desc);


        TextView dateTime = (TextView) convertView.findViewById(R.id.date_time);


        String fdate = "";
        try {
            Date dt = new Date(Long.parseLong(objectItem.getCreatedOn()));
            fdate = new SimpleDateFormat("dd MMM yyyy").format(dt);
            fdate = fdate + " at ";
            fdate = fdate + new SimpleDateFormat("HH:mm").format(dt);
        } catch (Exception e) {
            e.printStackTrace();

        }

        dateTime.setText(fdate);
        if(objectItem.getImage()!=null && !objectItem.getImage().trim().isEmpty() && !objectItem.getImage().trim().equals("null")){
            imageUrl = "http://ipledge2nigeria.com/service/" + "incidents/image/"+objectItem.getId();
        }
        else{
            imageUrl =null;
        }
        Log.i("Kritika","Image URL :"+objectItem.getImage());

        incidentImageView = (ImageView) convertView.findViewById(R.id.incident_imageview);

        if(objectItem.getImage() == null) {
            incidentImageView.setVisibility(View.GONE);
            Log.i("Kritika", "Image URL : Not Loading image at position : " + position + " : " + objectItem.getImage() + " : " + imageUrl);
        }
        else{
            incidentImageView.setVisibility(View.VISIBLE);
            loadImage(imageUrl, incidentImageView);

        }


        return convertView;

    }
    private class ImageLoadTask extends AsyncTask<String, String, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String imageUrl = null;

        public ImageLoadTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }
        int ratio;

        @Override
        protected void onPreExecute() {
            Log.i("ImageLoadTask", "Loading image...");
        }

        // PARAM[0] IS IMG URL
        protected Bitmap doInBackground(String... param) {
            imageUrl = param[0];
            if(imageUrl == null) {
                return null;
            }

            try {
                Log.i("Kritika","doInBackground");

                //SharedPreferences prefs = mContext.getSharedPreferences("NIGERIA_PLEDGE", 0);
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                //connection.setRequestProperty("Authorization", prefs.getString("NIGERIA_LOGIN_KEY", ""));
                connection.connect();
                InputStream input = connection.getInputStream();
                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inSampleSize = 4;
                Bitmap b1 = BitmapFactory.decodeStream(input,null,options);
                int width = incidentImageView.getWidth();
                int height = (width*b1.getHeight())/b1.getWidth();
                Bitmap b = Bitmap.createScaledBitmap(b1,width,height,false);
                return b;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onProgressUpdate(String... progress) {
            // NO OP
        }

        protected void onPostExecute(Bitmap bitmap) {
            Log.i("Kritika","Post execute");

            if (imageViewReference != null) {
                final ImageView imageView = imageViewReference.get();

                if (imageView != null && bitmap != null) {
                    imageView.setImageBitmap(bitmap);


                }
                else if(imageView != null && bitmap == null) {
                    imageView.setVisibility(View.GONE);
                }


            }
        }
    }
    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<ImageLoadTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             ImageLoadTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<ImageLoadTask>(bitmapWorkerTask);
        }

        public ImageLoadTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }


    public void loadImage(String imageUrl, ImageView articleImageView) {

        if (cancelPotentialWork(imageUrl, articleImageView)) {
            Log.i("Kritika","Load Image");
            final ImageLoadTask task = new ImageLoadTask(articleImageView);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(mContext.getResources(), null, task);
            articleImageView.setImageDrawable(asyncDrawable);
            task.execute(imageUrl);
        }
    }
    public static boolean cancelPotentialWork(String imageUrl, ImageView imageView) {
        final ImageLoadTask bitmapWorkerTask = getImageLoadTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.imageUrl;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == null || !bitmapData.equals(imageUrl)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static ImageLoadTask getImageLoadTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

}
