package com.android.podoal.project_podoal;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import com.android.podoal.project_podoal.arrayAdapter.VisitedSightAdapter;
import com.android.podoal.project_podoal.datamodel.MemberInfo;
import com.android.podoal.project_podoal.datamodel.VisitedSightDTO;
import com.android.podoal.project_podoal.dataquery.FileDownloader;
import com.android.podoal.project_podoal.dataquery.SelectQueryRunnable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ShowVisitedSightActivity extends AppCompatActivity {

    private ArrayList<VisitedSightDTO> visitedSightList = new ArrayList<VisitedSightDTO>();
    ListView listView;
    VisitedSightAdapter arrayAdapter;
    static Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_visited_sight);
        listView = (ListView)findViewById(R.id.visited_sight_list);

        //임시 코드
        //String member_id = new String ("2011003155");
        String member_id = MemberInfo.getInstance().getId();

        System.out.println("member_id : " + member_id);
        try
        {
            new Thread
            (
                new SelectQueryRunnable
                (
                    "http://" + GlobalApplication.SERVER_IP_ADDR + ":" + GlobalApplication.SERVER_IP_PORT + "/podoal/db_get_visited_sight.php?member_id=" + member_id
                )
                {
                    @Override
                    public void postRun(Object... params)
                    {
                        final String result = (String)params[0];
                        new Handler(Looper.getMainLooper()).post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                SetTxtListByResult(result);
                                System.out.println("MAIN_THREAD");
                            }
                        });
                    }
                }
            ).start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void SetTxtListByResult(String result)
    {
        String member_id;
        String sight_id;
        Date visited_date;
        int visited_id;
        String sight_name;

        try
        {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("result");

            if( jsonArray != null)
            {
                for (int i = 0; i < jsonArray.length(); i++)
                {
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
            }
            else
                System.out.println("No Data...");

            arrayAdapter = new VisitedSightAdapter(this,R.layout.visited_sight_item, visitedSightList);
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int visited_id = visitedSightList.get(position).getVisited_id();
                    Bitmap image = null, rotated = null;

                    ImageView imageView = new ImageView(getApplicationContext());
                    Toast toast = new Toast(getApplicationContext());

                    System.out.println("ListItem - VISITED_ID : " + visited_id);
                    try
                    {
                        image = new FileDownloader().execute(Integer.toString(visited_id)).get();
                        if(image != null)
                        {
                            if (image.getHeight() < image.getWidth()) {
                                Matrix matrix = new Matrix();
                                matrix.postRotate(90);
                                rotated = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
                                imageView.setImageBitmap(rotated);
                            }
                            else
                                imageView.setImageBitmap(bitmap);

                        /* 토스트에 뷰 셋팅하기 xml 통째로 넣어도 됨 */
                            toast.setView(imageView);
                        }
                        else
                            toast.setText("이미지를 불러올 수 없습니다");
                            /*txtView.setText
                            (
                                "member_id:" + visitedSightList.get(0).getMember_id() +
                                "\nsight_id:" + visitedSightList.get(0).getSight_id() +
                                "\nvisited_date:" + visitedSightList.get(0).getVisited_date() +
                                "\nvisited_id:" + visitedSightList.get(0).getVisited_id()
                            );*/

                        toast.show();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        //if( image != null )
                        //    image.recycle();
                        //if( rotated != null )
                        //    rotated.recycle();
                    }
                }
            });
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
