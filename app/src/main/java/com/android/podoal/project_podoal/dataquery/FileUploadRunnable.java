package com.android.podoal.project_podoal.dataquery;

import com.android.podoal.project_podoal.GlobalApplication;

/**
 * Created by Administrator on 2016-11-27.
 */

public class FileUploadRunnable implements Runnable
{
    private String filepath;
    private String maxVisitedNum;

    public FileUploadRunnable(String filepath, String maxVisitedNum)
    {
        this.filepath = filepath;
        this.maxVisitedNum = maxVisitedNum;
    }

    public void run()
    {
        FileUploader fileUploader = new FileUploader(filepath, maxVisitedNum);
        fileUploader.upload("http://" + GlobalApplication.SERVER_IP_ADDR + ":" + GlobalApplication.SERVER_IP_PORT + "/podoal/upload.php");
    }
}