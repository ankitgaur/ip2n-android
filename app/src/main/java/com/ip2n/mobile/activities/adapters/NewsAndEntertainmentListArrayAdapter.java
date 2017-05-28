package com.ip2n.mobile.activities.adapters;

import android.app.Activity;
import android.content.Context;
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
import com.ip2n.mobile.models.JosContent;

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
public class NewsAndEntertainmentListArrayAdapter extends ArrayAdapter<JosContent> {
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<JosContent> data = null;
    private String imageUrl;
    private Bitmap bitmap;
    private ImageView articleImageView;

    public NewsAndEntertainmentListArrayAdapter(Context mContext, int layoutResourceId, ArrayList<JosContent> data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        if (convertView == null) {

            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        // object item based on the position
        JosContent objectItem = data.get(position);


        // description.setText((CharSequence) objectItem.getDescription());
        TextView dateTime = (TextView) convertView.findViewById(R.id.date_time_textview);

        String fdate = "";
        try {
            Date dt = new Date(objectItem.getCreated());
            fdate = fdate + " at ";
            fdate = fdate + new SimpleDateFormat("HH:mm").format(dt);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Kritika", "Error" + e.toString());

        }
        Log.i("Kritika", "date is" + fdate);

        dateTime.setText("written by " + objectItem.getUser() + " " + "on " + fdate);
        TextView incidentTitle = (TextView) convertView.findViewById(R.id.title_textview);

        incidentTitle.setText(objectItem.getTitle());
        imageUrl = objectItem.getImg();

        articleImageView = (ImageView) convertView.findViewById(R.id.news_imageview);
        if(imageUrl == null)
            articleImageView.setLayoutParams(new RelativeLayout.LayoutParams(0,0));
        else
            loadImage(imageUrl, articleImageView);


        return convertView;

    }

    private class ImageLoadTask extends AsyncTask<String, String, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String imageUrl = null;

        public ImageLoadTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }


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
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap b1 = BitmapFactory.decodeStream(input);
                int width = articleImageView.getWidth();
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
            if (imageViewReference != null) {
                final ImageView imageView = imageViewReference.get();

                if (imageView != null && bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
                else if(imageView != null && bitmap == null)
                    imageView.setLayoutParams(new RelativeLayout.LayoutParams(0,0));


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
