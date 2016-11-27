package com.android.podoal.project_podoal.dataquery;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.android.podoal.project_podoal.GlobalApplication;

/**
 * Created by tj on 2016-11-23.
 */

public class FileUploader extends AsyncTask<String,Void,Boolean> {

    @Override
    protected Boolean doInBackground(String... params) {

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        String filePath = params[0];
        String fileId   = params[1];
        String uploadName = fileId + ".jpg";

        System.out.println(filePath + " " + uploadName);

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;        //한 번에 읽는 크기 제한 1MB

        try {

            URL url = new URL("http://" + GlobalApplication.SERVER_IP_ADDR + ":" + GlobalApplication.SERVER_IP_PORT + "/podoal/image/upload.php");

            File uploadFile = new File(filePath);

            if (!uploadFile.isFile()) {
                Log.e("uploadFile","read file failure");
                return false;
            }

            FileInputStream fileInputStream = new FileInputStream(uploadFile);

            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); //input 허용
            conn.setDoOutput(true);  // output 허용
            conn.setUseCaches(false);   // cache copy를 허용하지 않는다.
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", uploadName);

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            // 파일 전송시 파라메터명은 filename 파일명은 '$maxVisitedId'.jpg로 설정하여 전송
            dos.writeBytes("Content-Disposition: form-data; name=\"upload_file\";filename=\"" + uploadName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {

                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.i("uploadFile", "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);

            if(serverResponseCode == 200){
                return true;
            }

            //close the streams //
            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }
}
