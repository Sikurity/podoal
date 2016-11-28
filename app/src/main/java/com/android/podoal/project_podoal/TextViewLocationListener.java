package com.android.podoal.project_podoal;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Administrator on 2016-11-29.
 */

public class TextViewLocationListener implements LocationListener
{
    private TextView txtView;

    public TextViewLocationListener(TextView tv)
    {
        this.txtView = tv;
    }

    public void onLocationChanged(Location location)
    {
        //여기서 위치값이 갱신되면 이벤트가 발생한다.
        //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

        Log.d("test", "onLocationChanged, location:" + location);
        double longitude = location.getLongitude(); //경도
        double latitude = location.getLatitude();   //위도
        float accuracy = location.getAccuracy();    //정확도
        //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
        //Network 위치제공자에 의한 위치변화
        //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.

        txtView.setText
            (
                    "위도 : " + longitude
                    + "\n경도 : " + latitude
                    + "\n정확도 : "  + accuracy
            );
    }

    public void onProviderDisabled(String provider) {
        // Disabled시
        Log.d("test", "onProviderDisabled, provider:" + provider);
    }

    public void onProviderEnabled(String provider) {
        // Enabled시
        Log.d("test", "onProviderEnabled, provider:" + provider);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // 변경시
        Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
    }
};