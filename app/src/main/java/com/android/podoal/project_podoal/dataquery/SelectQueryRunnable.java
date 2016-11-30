package com.android.podoal.project_podoal.dataquery;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class SelectQueryRunnable implements Runnable
{
    private String ServerURL;

    public SelectQueryRunnable(String ServerURL)
    {
        this.ServerURL = ServerURL;
    }

    public abstract void postRun(Object ...params);

    @Override
    public void run()
    {
        String result = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try
        {
            URL url = new URL(ServerURL);
            conn = (HttpURLConnection) url.openConnection();
            //conn.setReadTimeout(3000);
            StringBuilder sb = new StringBuilder();

            is = conn.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            String json;
            while((json = br.readLine()) != null)
            {
                sb.append(json+"\n");
            }

            result = sb.toString().trim();
            if( result != null )
            {
                System.out.println("DB Select Success");
                postRun(result);
            }
            else
                System.out.println("DB Select Failed..");

        }
        catch(Exception e)
        {
            System.out.println("DB Select Failed..");
            e.printStackTrace();
        }
        finally
        {
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
