package com.android.podoal.project_podoal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class MapInfoActivity extends Activity {

    TextView txt_sight_info;
    TextView txt_sight_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map_info);

        txt_sight_name = (TextView) findViewById(R.id.info_sight_name);
        txt_sight_info = (TextView) findViewById(R.id.info_sight_info);

        Intent intent = getIntent();

        String sight_name = intent.getStringExtra("sight_name");
        String sight_info = intent.getStringExtra("sight_info");
        setTextView(sight_name,sight_info);
    }

    public void setTextView(String sight_name, String sight_info) {
        this.txt_sight_name.setText("<관광지 명>\n" + sight_name);
        this.txt_sight_info.setText("<소개글>\n" + sight_info);
    }
}
