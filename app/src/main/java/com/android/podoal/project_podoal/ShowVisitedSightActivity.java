package com.android.podoal.project_podoal;

import android.os.AsyncTask;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.android.podoal.project_podoal.arrayAdapter.VisitedSightAdapter;
import com.android.podoal.project_podoal.datamodel.VisitedSightDTO;
import com.android.podoal.project_podoal.dataquery.SelectQueryGetter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;

public class ShowVisitedSightActivity extends AppCompatActivity {

    private ArrayList<VisitedSightDTO> visitedSightList = new ArrayList<VisitedSightDTO>();
    SelectQueryGetter dbConnector;
    ListView listView;
    VisitedSightAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_visited_sight);
        listView = (ListView)findViewById(R.id.visited_sight_list);

        dbConnector = new SelectQueryGetter();

        //임시 코드
        String member_id = new String ("2011003155");

        try {
            String result = dbConnector.execute("http://" + GlobalApplication.SERVER_IP_ADDR + ":" + GlobalApplication.SERVER_IP_PORT + "/podoal/db_get_visited_sight.php?member_id=" + member_id).get();
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
        String sight_name;

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject entity = jsonArray.getJSONObject(i);

                member_id = (entity.getString("member_id"));
                sight_id = (entity.getString("sight_id"));
                visited_date = (Date.valueOf(entity.getString("visited_date")));
                visited_id = (entity.getInt("visited_id"));
                sight_name = (entity.getString("sight_name"));


                visitedSightList.add(new VisitedSightDTO(member_id,
                        sight_id,
                        visited_date,
                        visited_id,
                        sight_name));
            }

            arrayAdapter = new VisitedSightAdapter(this,R.layout.visited_sight_item,visitedSightList);
            listView.setAdapter(arrayAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
/*
        txtView.setText("member_id:" + visitedSightList.get(0).getMember_id() +
                "\nsight_id:" + visitedSightList.get(0).getSight_id() +
                "\nvisited_date:" + visitedSightList.get(0).getVisited_date() +
                "\nvisited_id:" + visitedSightList.get(0).getVisited_id());
*/
    }
}
