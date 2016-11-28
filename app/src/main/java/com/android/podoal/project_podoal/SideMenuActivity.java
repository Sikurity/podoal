package com.android.podoal.project_podoal;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.podoal.project_podoal.datamodel.MemberInfo;
import com.android.podoal.project_podoal.datamodel.SightDTO;
import com.android.podoal.project_podoal.datamodel.VisitedSightDTO;
import com.android.podoal.project_podoal.dataquery.FileUploadRunnable;
import com.android.podoal.project_podoal.dataquery.InsertQueryGetter;
import com.android.podoal.project_podoal.dataquery.SelectQueryGetter;

import java.util.ArrayList;
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
    private TextView currentPosition = null;

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

        ArrayList<String> tmp = new ArrayList<>();

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M)
        {
            int locPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (locPermissionCheck != PackageManager.PERMISSION_GRANTED)
                tmp.add(Manifest.permission.ACCESS_FINE_LOCATION);

            int locPermissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (locPermissionCheck2 != PackageManager.PERMISSION_GRANTED)
                tmp.add(Manifest.permission.READ_EXTERNAL_STORAGE);

            int filePermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (filePermissionCheck != PackageManager.PERMISSION_GRANTED)
                tmp.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if( !tmp.isEmpty())
        {
            String permissions[] = new String[tmp.size()];
            int i = 0;
            for(String p : tmp)
            {
                permissions[i++] = p;
            }

            requestPermissions(permissions, 255);
        }

        cameraSetup();

        currentPosition = (TextView) findViewById(R.id.current_position);
        currentPosition.setText("Current Position");

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    locationListener);
        }

        if( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED )
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    locationListener);
        }

        System.out.println("SIDE_MENU_ACTIVITY_ON_CREATE_END");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode != 255)
        {
            if( grantResults.length > 0 )
            {
                LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

                for( String p : permissions)
                {
                    if ( p.compareTo(Manifest.permission.ACCESS_FINE_LOCATION) == 0 )
                    {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                                    100, // 통지사이의 최소 시간간격 (miliSecond)
                                    1, // 통지사이의 최소 변경거리 (m)
                                    locationListener);
                        }
                    }
                    else if ( p.compareTo(Manifest.permission.ACCESS_COARSE_LOCATION) == 0 )
                    {
                        if( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED )
                        {
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                                    100, // 통지사이의 최소 시간간격 (miliSecond)
                                    1, // 통지사이의 최소 변경거리 (m)
                                    locationListener);
                        }
                    }
                }
            }
            else
                System.out.println("All Permission Denied!");
        }
        else
            System.out.println("Permission Granted Error!");

        return;
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            Log.d("test", "onLocationChanged, location:" + location);
            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            float accuracy = location.getAccuracy();    //정확도
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.

            currentPosition.setText
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
            Uri uri = Uri.parse("content://media/external/images/media");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 0);
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
        ImageButton camera_btn = (ImageButton)this.findViewById(R.id.content_include).findViewById(R.id.camera_button);

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

        for (int i = 0; i < sightList.size(); i++)
        {
            SightDTO sight = sightList.get(i);

            c.x = sight.getLatitude();
            c.y = sight.getLongitude();
            c.r = sight.getRadius();

            if ((((  (latitude - c.x) * (latitude - c.x)) + ((longitude - c.y) * (longitude - c.y))) < (c.r * c.r)) && !sight.isVisitedSight(visitedSightList))
            {
                if(  !sight.isVisitedSight(visitedSightList) )
                    return sight;
                else
                    return null;
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
                            //Toast.makeText(this, maxVisitedId, Toast.LENGTH_SHORT).show();
                            VisitedSightDTO visitedSightDTO = new VisitedSightDTO();

                            visitedSightDTO.setMember_id(MemberInfo.getInstance().getId());
                            visitedSightDTO.setSight_id(matchedSight.getSight_id());
                            visitedSightDTO.setVisited_id(Integer.parseInt(maxVisitedId));
                            String postData = visitedSightDTO.makePostData();
                            System.out.println("postData : " + postData);

                            Uri uri = null;
                            if( data.getData() == null ) // 될지 안될지 모름
                            {
                                String[] IMAGE_PROJECTION =
                                {
                                    MediaStore.Images.ImageColumns.DATA,
                                    MediaStore.Images.ImageColumns._ID,
                                };

                                try
                                {
                                    Cursor cursorImages = getContentResolver().query(
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            IMAGE_PROJECTION, null, null,null);
                                    if (cursorImages != null && cursorImages.moveToLast())
                                    {
                                        uri = Uri.parse(cursorImages.getString(0)); //경로
                                        System.out.println("uri1 : " + uri.toString());
                                        //int id = cursorImages.getInt(1); //아이디
                                        cursorImages.close(); // 커서 사용이 끝나면 꼭 닫아준다.
                                    }
                                    else
                                        System.out.println("Cannot Access Image File");
                                }
                                catch(Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                uri = data.getData();
                                System.out.println("uri2 : " + uri.toString());
                            }

                            Cursor cursor = getContentResolver().query(uri, null, null, null, null );
                            cursor.moveToNext();
                            String filepath = cursor.getString( cursor.getColumnIndex( "_data" ) );
                            cursor.close();

                            new Thread(new FileUploadRunnable(filepath, maxVisitedId)).start();

                            String result = dbConnector.execute("http://" + GlobalApplication.SERVER_IP_ADDR + ":" + GlobalApplication.SERVER_IP_PORT + "/podoal/db_insert_visited_sight.php", postData).get();

                            if (result != null)
                            {
                                System.out.println("result isn't null : " + result);
                                visitedSightList.add(visitedSightDTO);
                                MapsFragment.setMarkers(visitedSightList);
                            }
                            else
                                System.out.println("result is null");

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            Toast.makeText(this, "오류, 운영자에게 문의하세요", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(this, "근처에 관광지가 없거나 이미 방문한 관광지 입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                //Toast.makeText(this, "Default", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}