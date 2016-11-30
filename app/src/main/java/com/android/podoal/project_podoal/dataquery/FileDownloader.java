package com.android.podoal.project_podoal.dataquery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.android.podoal.project_podoal.GlobalApplication;

import java.io.InputStream;
import java.net.URL;

public class FileDownloader extends AsyncTask<String,Void,Bitmap> {

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap bitmap = null;
        InputStream is = null;

        try
        {
            String filePath = "http://" + GlobalApplication.SERVER_IP_ADDR + ":" + GlobalApplication.SERVER_IP_PORT + "/podoal/uploads/" + strings[0] + ".jpg";
            URL url = new URL(filePath);
            is = url.openStream();
            bitmap = BitmapFactory.decodeStream(is);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (is != null)
                    is.close();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                is = null;
            }
        }

        return bitmap;
    }
}
