package com.android.podoal.project_podoal.dataquery;

import com.android.podoal.project_podoal.MapsFragment;
import com.android.podoal.project_podoal.datamodel.VisitedSightDTO;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class UpdateQueryRunnable implements Runnable
{
    private String serverURL;
    private String postData;

    public UpdateQueryRunnable(String serverURL, String postData)
    {
        this.serverURL = serverURL;
        this.postData = postData;
    }

    public abstract void postRun(Object ...params);

    @Override
    public void run()
    {
        String result = null;
        HttpURLConnection conn = null;
        OutputStream os = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try
        {
            URL url = new URL(serverURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            os = conn.getOutputStream();
            os.write(postData.getBytes("UTF-8"));

            is = conn.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = br.readLine()) != null)
            {
                sb.append(line);
                break;
            }

            result = sb.toString().trim();
            if( result != null )
            {
                System.out.println("DB Update Success");
                postRun(result);
            }
            else
                System.out.println("DB Update Failed..");
        }
        catch(Exception e)
        {
            System.out.println("DB Update Failed..");
            e.printStackTrace();
        }
        finally
        {
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
                if (br != null)
                    br.close();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                br = null;
            }

            try
            {
                if( isr != null )
                    isr.close();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                isr = null;
            }

            try
            {
                if( is != null )
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

            try
            {
                if( conn != null )
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
    }
}
