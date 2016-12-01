package com.android.podoal.project_podoal.dataquery;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class FileUploadRunnable implements Runnable
{
    private String filepath;
    private File image;
    private String maxVisitedNum;
    private String URL;

    public FileUploadRunnable(String URL, String filepath, String maxVisitedNum)
    {
        this.URL = URL;
        this.filepath = filepath;
        this.maxVisitedNum = maxVisitedNum;
        this.image = null;

        System.out.println(URL + "/" + filepath + "/" +  maxVisitedNum);
    }

    public FileUploadRunnable(String URL, File image, String maxVisitedNum)
    {
        this.URL = URL;
        this.filepath = null;
        this.maxVisitedNum = maxVisitedNum;
        this.image = image;

        System.out.println(URL + "/" + filepath + "/" +  maxVisitedNum);
    }

    abstract public void postRun(Object ...params);

    @Override
    public void run()
    {
        HttpURLConnection conn = null;
        OutputStream os = null;
        DataOutputStream dos = null;
        FileInputStream fis = null;

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        String newName = maxVisitedNum + ".jpg";
        File sourceFile = new File(filepath);

        if (!sourceFile.isFile())
        {
            Log.e("uploadFile", "Source File not exist :" + filepath);

            return;
        }
        else
        {
            try
            {
                // open a URL connection to the Servlet
                URL url = new URL(URL);
                System.out.println("server_url : " + URL);
                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", newName);

                os = conn.getOutputStream();
                dos = new DataOutputStream(os);

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + newName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                fis = new FileInputStream(sourceFile);
                bytesAvailable = fis.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fis.read(buffer, 0, bufferSize);

                while (bytesRead > 0)
                {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fis.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fis.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                int serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
                if( serverResponseCode == 200 )
                    postRun();
            }
            catch (Exception ex)
            {
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            }
            finally
            {
                try
                {
                    if( fis != null )
                        fis.close();
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                finally
                {
                    fis = null;
                }

                try
                {
                    if( dos != null )
                    {
                        dos.flush();
                        dos.close();
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                finally
                {
                    dos = null;
                }

                try
                {
                    if( os != null )
                    {
                        os.flush();
                        os.close();
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                finally
                {
                    os = null;
                }

                try
                {
                    if( conn != null)
                        conn.disconnect();
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                finally
                {
                    conn = null;
                }
            }
        } // End else block
    }
}