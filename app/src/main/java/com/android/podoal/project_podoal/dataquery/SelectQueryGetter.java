package com.android.podoal.project_podoal.dataquery;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tj on 2016-11-13.
 */

public class SelectQueryGetter extends AsyncTask<String, Void, String> {

    protected String doInBackground(String... params) {
        String uri = params[0];

        BufferedReader bufferedReader = null;

        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            StringBuilder sb = new StringBuilder();

            bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String json;
            while((json = bufferedReader.readLine())!= null){
                sb.append(json+"\n");
            }

            bufferedReader.close();
            conn.disconnect();
            return sb.toString().trim();

        }catch(Exception e){
            return null;
        }
    }
}
