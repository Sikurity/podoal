package com.android.podoal.project_podoal;

import android.os.AsyncTask;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


import com.android.podoal.project_podoal.datamodel.VisitedSightDTO;
import com.android.podoal.project_podoal.dataquery.SelectQueryGetter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

public class ShowVisitedSightActivity extends AppCompatActivity {

    private ArrayList<VisitedSightDTO> visitedSightList = new ArrayList<VisitedSightDTO>();
    SelectQueryGetter dbConnector;
    TextView txtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_visited_sight);
        txtView = (TextView)findViewById(R.id.visited_sight_txv);

        dbConnector = new SelectQueryGetter();

        try {
            String result = dbConnector.execute("http://localhost/podoal/db_get_visited_sight.php").get();
            SetTxtListByResult(result);
        } catch (Exception e){
            e.printStackTrace();
        } finally {

        }
    }

    private void SetTxtListByResult(String result) {

        String member_id;
        String sight_id;
        Date visited_date;
        int visited_id;

        try
        {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject entity = jsonArray.getJSONObject(i);

                member_id = (entity.getString("member_id"));
                sight_id = (entity.getString("sight_id"));
                visited_date = (Date.valueOf(entity.getString("visited_date")));
                visited_id = (entity.getInt("visited_id"));

                visitedSightList.add
                    (
                        new VisitedSightDTO
                            (
                                member_id,
                                sight_id,
                                visited_date,
                                visited_id,
                                ""
                            )
                    );
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        txtView.setText("member_id:" + visitedSightList.get(0).getMember_id() +
                "\nsight_id:" + visitedSightList.get(0).getSight_id() +
                "\nvisited_date:" + visitedSightList.get(0).getVisited_date() +
                "\nvisited_id:" + visitedSightList.get(0).getVisited_id());

    }
}
