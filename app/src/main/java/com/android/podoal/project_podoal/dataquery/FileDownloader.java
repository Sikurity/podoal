package com.android.podoal.project_podoal.dataquery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.android.podoal.project_podoal.GlobalApplication;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by tj on 2016-11-28.
 */

public class FileDownloader extends AsyncTask<String,Void,Bitmap> {

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap bitmap = null;
        InputStream inputStream = null;

        try {
            String filePath = "http://" + GlobalApplication.SERVER_IP_ADDR + ":" + GlobalApplication.SERVER_IP_PORT + "/podoal/uploads/" + strings[0] + ".jpg";;
            URL url = new URL(filePath);
            inputStream = url.openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return bitmap;
    }
}
