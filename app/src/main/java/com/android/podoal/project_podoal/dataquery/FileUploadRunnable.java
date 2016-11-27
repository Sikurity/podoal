package com.android.podoal.project_podoal.dataquery;

import com.android.podoal.project_podoal.GlobalApplication;

/**
 * Created by Administrator on 2016-11-27.
 */

public class FileUploadRunnable implements Runnable
{
    private String filepath;

    public FileUploadRunnable(String filepath)
    {
        this.filepath = filepath;
    }

    public void run()
    {
        FileUploader fileUploader = new FileUploader(filepath);
        fileUploader.upload("http://" + GlobalApplication.SERVER_IP_ADDR + ":" + GlobalApplication.SERVER_IP_PORT + "/podoal/upload.php");
    }
}
