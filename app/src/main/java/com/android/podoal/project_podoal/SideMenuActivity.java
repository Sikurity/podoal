package com.android.podoal.project_podoal;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.podoal.project_podoal.datamodel.SightDTO;
import com.android.podoal.project_podoal.datamodel.VisitedSightDTO;
import com.android.podoal.project_podoal.dataquery.FileUploader;
import com.android.podoal.project_podoal.dataquery.InsertQueryGetter;
import com.android.podoal.project_podoal.dataquery.SelectQueryGetter;

import java.util.List;

public class SideMenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    FragmentManager fragmentManager;
    Fragment fragment = null;
    Class fragmentClass = null;

    private List<SightDTO> sightList;
    private List<VisitedSightDTO> visitedSightList;
    private Location location;
    private SelectQueryGetter dbSelector;

    static final int REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        System.out.println("SIDE_MENU_ACTIVITY_ON_CREATE_BEGIN");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sightList = MapsFragment.getSightList();
        visitedSightList = MapsFragment.getVisitedSightList();

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        cameraSetup();

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                getLocationPermission();
            }
        }

        System.out.println("SIDE_MENU_ACTIVITY_ON_CREATE_END");
    }

    private void getLocationPermission(){

        // 다이어로그같은것을 띄워서 사용자에게 해당 권한이 필요한 이유에 대해 설명합니다
        // 해당 설명이 끝난뒤 requestPermissions()함수를 호출하여 권한허가를 요청해야 합니다
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("권한이 필요합니다.")
                    .setMessage("이 기능을 사용하기 위해서는 단말기의 \"위치\" 권한이 필요합니다. 계속하시겠습니까?")
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
                            }

                        }
                    })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(SideMenuActivity.this, "기능을 취소했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create()
                    .show();
        } else {
            // CALL_PHONE 권한을 Android OS 에 요청한다.
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.side_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        boolean bFragmentChange = false;

        if (id == R.id.nav_list)
        {
            Intent intent = new Intent(this, ShowVisitedSightActivity.class);
            startActivity(intent);
            // Handle the camera action
        }
        else if (id == R.id.nav_gallery)
        {

        }
        else if (id == R.id.nav_setting) {

        }

//        if (bFragmentChange) {
//            try {
//                //fragment = (Fragment) fragmentClass.newInstance();
///*
//                fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
//*/
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void cameraSetup()
    {
        System.out.println("CAMERA_SETUP_BEGIN");
        Button camera_btn = (Button)this.findViewById(R.id.content_include).findViewById(R.id.camera_button);

        camera_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,REQUEST_CAMERA);
            }
        });
        System.out.println("CAMERA_SETUP_END");
    }

    private class Circle {
        public double x;
        public double y;
        public double r;
    }

    private SightDTO ValidateSight()
    {
        double latitude = MapsFragment.getLatitude();
        double longitude = MapsFragment.getLongitude();

        Circle c = new Circle();

        for (int i = 0; i < sightList.size(); i++) {
            SightDTO sight = sightList.get(i);

            c.x = sight.getLatitude();
            c.y = sight.getLongitude();
            c.r = sight.getRadius();

            if ((((  (longitude - c.x) * (latitude - c.x)) + ((longitude - c.y) * (longitude - c.y))) < (c.r * c.r)) && !sight.isVisitedSight(visitedSightList))
            {
                return sight;
            }
        }

        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        location = MapsFragment.getLocation();
        dbSelector = MapsFragment.getDbSelector();
        switch (requestCode) {
            case REQUEST_CAMERA:

                if (resultCode != this.RESULT_OK || data == null) {
                    Toast.makeText(this, "카메라에서 사진 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (location == null) {
                    Toast.makeText(this, "현재 위치를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    SightDTO matchedSight = ValidateSight();

                    if (matchedSight != null)
                    {
                        try {
                            dbSelector = new SelectQueryGetter();
                            InsertQueryGetter dbConnector = new InsertQueryGetter();
                            String maxVisitedId = dbSelector.execute("http://" + GlobalApplication.SERVER_IP_ADDR + ":" + GlobalApplication.SERVER_IP_PORT + "/podoal/db_get_max_visit_sight_id.php").get();
                            Toast.makeText(this, maxVisitedId, Toast.LENGTH_SHORT).show();
                            VisitedSightDTO visitedSightDTO = new VisitedSightDTO();

                            visitedSightDTO.setMember_id("2011003155");
                            visitedSightDTO.setSight_id(matchedSight.getSight_id());
                            visitedSightDTO.setVisited_id(Integer.parseInt(maxVisitedId));
                            String postData = visitedSightDTO.makePostData();
                            System.out.println("postData : " + postData);

                            FileUploader fileUploader = new FileUploader();

                            Boolean bUploadSuccess = fileUploader.execute(data.getData().toString(),maxVisitedId).get();

                            if (!bUploadSuccess.booleanValue())
                            {
                                Toast.makeText(this, "사진 업로드에 실패 했습니다..", Toast.LENGTH_SHORT).show();
                                //return;
                            }

                            String result = dbConnector.execute("http://" + GlobalApplication.SERVER_IP_ADDR + ":" + GlobalApplication.SERVER_IP_PORT + "/podoal/db_insert_visited_sight.php", postData).get();

                            if (result != null) {
                                Toast.makeText(this, "result isn't null : " + result, Toast.LENGTH_SHORT).show();
                                visitedSightList.add(visitedSightDTO);
                                MapsFragment.setMarkers(visitedSightList);
                            } else {
                                Toast.makeText(this, "result is null", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(this, "근처에 관광지가 없거나 이미 방문한 관광지 입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                Toast.makeText(this, "Default", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
