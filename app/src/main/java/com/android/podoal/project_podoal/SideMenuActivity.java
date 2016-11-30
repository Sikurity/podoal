package com.android.podoal.project_podoal;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
import com.android.podoal.project_podoal.dataquery.SelectQueryRunnable;
import com.android.podoal.project_podoal.dataquery.UpdateQueryRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SideMenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener
{
    private Location location;
    private double longitude;
    private double latitude;
    private float accuracy;

    private static LocationManager locationManager;
    public static LocationManager getLocationManager()
    {
        return locationManager;
    }

    static final int REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        System.out.println("SIDE_MENU_ACTIVITY_ON_CREATE_BEGIN");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M)
        {
            ArrayList<String> tmp = new ArrayList<>();

            int locByGpsPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (locByGpsPermissionCheck != PackageManager.PERMISSION_GRANTED)
                tmp.add(Manifest.permission.ACCESS_FINE_LOCATION);

            int locByNetworkPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (locByNetworkPermissionCheck != PackageManager.PERMISSION_GRANTED)
                tmp.add(Manifest.permission.ACCESS_COARSE_LOCATION);

            int filePermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (filePermissionCheck != PackageManager.PERMISSION_GRANTED)
                tmp.add(Manifest.permission.READ_EXTERNAL_STORAGE);

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
        }

        gpsSetup();
        cameraSetup();

        TextView currentPosition = (TextView) findViewById(R.id.current_position);
        currentPosition.setText("Current Position");

        System.out.println("SIDE_MENU_ACTIVITY_ON_CREATE_END");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        System.out.println("ON_POST_CREATE_BEGIN");
        super.onPostCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, // GPS를 통해 위치파악
                    1000, // 통지사이의 최소 시간간격 (miliSecond)
                    10, // 통지사이의 최소 변경거리 (m)
                    this);
            System.out.println("LocationListener By GPS Attached1");
        }

        if( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED )
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 네트워크를 통해 위치파악, 미개통 스마트폰 사용 불가
                    15000, // 통지사이의 최소 시간간격 (miliSecond)
                    150, // 통지사이의 최소 변경거리 (m)
                    this);
            System.out.println("LocationListener By Network Attached1");
        }

        System.out.println("ON_POST_CREATE_END");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == 255)
        {
            int i = 0;
            for( String p : permissions)
            {
                if( grantResults[i++]  == PackageManager.PERMISSION_GRANTED )
                {
                    System.out.println(p + " Granted");
                    if ( p.compareTo(Manifest.permission.ACCESS_FINE_LOCATION) == 0 )
                    {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, // GPS를 통해 위치파악
                                    1000, // 통지사이의 최소 시간간격 (miliSecond)
                                    10, // 통지사이의 최소 변경거리 (m)
                                    this);
                        }
                        System.out.println("LocationListener By GPS Attached2");
                    }
                    else if ( p.compareTo(Manifest.permission.ACCESS_COARSE_LOCATION) == 0 )
                    {
                        if( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED )
                        {
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 네트워크를 통해 위치파악, 미개통 스마트폰 사용 불가
                                    15000, // 통지사이의 최소 시간간격 (miliSecond)
                                    150, // 통지사이의 최소 변경거리 (m)
                                    this);
                            System.out.println("LocationListener By Network Attached2");
                        }
                    }
                }
                else
                    System.out.println(p + " Denied");
            }
        }
        else
            System.out.println("Permission Granted Error! - " + requestCode);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        //boolean bFragmentChange = false;
        int id = item.getItemId(); // Handle navigation view item clicks here.

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
        }

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

    private void gpsSetup()
    {
        System.out.println("GPS_SETUP_BEGIN");

        try
        {
            locationManager = SideMenuActivity.getLocationManager();
            Criteria criteria = new Criteria();
            String lmProvider= locationManager.getBestProvider(criteria, true);

            if (lmProvider == null || locationManager.isProviderEnabled(lmProvider))
            {
                List<String> providerList = locationManager.getAllProviders();

                for (int i = 0; i < providerList.size(); i++)
                {
                    String providerName = providerList.get(i);

                    if (locationManager.isProviderEnabled(providerName))
                    {
                        lmProvider = providerName;
                        break;
                    }
                }
            }

            // location = locationManager.getLastKnownLocation(lmProvider);
            List<String> providers = locationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers)
            {
                Location loc = locationManager.getLastKnownLocation(provider);
                if (loc == null)
                {
                    continue;
                }
                if (bestLocation == null || loc.getAccuracy() < bestLocation.getAccuracy())
                {
                    // Found best last known location: %s", l);
                    bestLocation = loc;
                }
            }

            location = bestLocation;
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (location == null)
                Toast.makeText(this,"현재 위치를 찾을 수 없습니다.",Toast.LENGTH_SHORT).show();
            else
                onLocationChanged(location);

            System.out.println("GPS_SETUP_END");
        }
    }

    private class Circle {
        public double x;
        public double y;
        public double r;
    }

    private SightDTO ValidateSight()
    {
        List<SightDTO> sightList = MapsFragment.getSightList();
        List<VisitedSightDTO> visitedSightList = MapsFragment.getVisitedSightList();

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
        switch (requestCode)
        {
            case REQUEST_CAMERA:
                if (resultCode != RESULT_OK || data == null)
                {
                    Toast.makeText(this, "카메라에서 사진 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                gpsSetup();
                if (location == null)
                {
                    System.out.println("LOCATION IS NULL");
                    return;
                }
                else
                {
                    final Intent imageData = data;
                    final SightDTO matchedSight = ValidateSight();

                    if( MapsFragment.getSightList() == null || MapsFragment.getVisitedSightList() == null )
                        Toast.makeText(this, "관광지 정보를 불러오는 중입니다...", Toast.LENGTH_SHORT).show();
                    else if ( matchedSight != null )
                    {
                        try
                        {
                            new Thread
                            (
                                new SelectQueryRunnable
                                (
                                    "http://" + GlobalApplication.SERVER_IP_ADDR + ":" + GlobalApplication.SERVER_IP_PORT + "/podoal/db_get_max_visit_sight_id.php"
                                )
                                {
                                    @Override
                                    public void postRun(Object ...params)
                                    {
                                        final String maxVisitedId = (String)params[0];
                                        System.out.println("##" + maxVisitedId);
                                        //Toast.makeText(this, maxVisitedId, Toast.LENGTH_SHORT).show();
                                        final VisitedSightDTO visitedSightDTO = new VisitedSightDTO();

                                        visitedSightDTO.setMember_id(MemberInfo.getInstance().getId());
                                        visitedSightDTO.setSight_id(matchedSight.getSight_id());
                                        visitedSightDTO.setVisited_id(Integer.parseInt(maxVisitedId));

                                        String postData = visitedSightDTO.makePostData();
                                        System.out.println("postData : " + postData);

                                        new Thread
                                        (
                                            new UpdateQueryRunnable
                                                (
                                                    "http://" + GlobalApplication.SERVER_IP_ADDR + ":" + GlobalApplication.SERVER_IP_PORT + "/podoal/db_insert_visited_sight.php",
                                                    postData
                                                )
                                                {
                                                @Override
                                                public void postRun(Object ...params)
                                                {
                                                    Uri uri = null;
                                                    String filepath = "default.jpg";
                                                    if( imageData.getData() == null ) // 될지 안될지 모름
                                                    {
                                                        String[] IMAGE_PROJECTION =
                                                            {
                                                                MediaStore.Images.ImageColumns.DATA,
                                                                MediaStore.Images.ImageColumns._ID,
                                                            };

                                                        try
                                                        {
                                                            Cursor cursorImages = getContentResolver().query
                                                            (
                                                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                                IMAGE_PROJECTION,
                                                                null,
                                                                null,
                                                                null
                                                            );

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
                                                        uri = imageData.getData();
                                                        System.out.println("uri2 : " + uri.toString());
                                                    }

                                                    if( uri != null )
                                                    {
                                                        Cursor cursor = getContentResolver().query(uri, null, null, null, null );
                                                        if( cursor != null)
                                                        {
                                                            cursor.moveToNext();
                                                            filepath = cursor.getString(cursor.getColumnIndex("_data"));
                                                            cursor.close();
                                                        }
                                                    }

                                                    new Thread
                                                    (
                                                        new FileUploadRunnable
                                                        (
                                                            "http://" + GlobalApplication.SERVER_IP_ADDR + ":" + GlobalApplication.SERVER_IP_PORT + "/podoal/upload.php",
                                                            filepath,
                                                            maxVisitedId
                                                        )
                                                        {
                                                            @Override
                                                            public void postRun(Object ...params)
                                                            {
                                                                new Handler(Looper.getMainLooper()).post(new Runnable()
                                                                {
                                                                    @Override
                                                                    public void run()
                                                                    {
                                                                        MapsFragment.getVisitedSightList().add(visitedSightDTO);
                                                                        MapsFragment.setMarkers();
                                                                        System.out.println("SET_MARKETS_REQUEST_CAMERA_RESULT_OK");
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    ).start();
                                                }
                                            }
                                        ).start();
                                    }
                                }
                            ).start();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            Toast.makeText(this, "오류, 운영자에게 문의하세요", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                        Toast.makeText(this, "근처에 관광지가 없거나 이미 방문한 관광지 입니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                //Toast.makeText(this, "Default", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        //여기서 위치값이 갱신되면 이벤트가 발생한다.
        //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

        Log.d("test", "onLocationChanged, location:" + location);
        longitude = location.getLongitude(); //경도
        latitude = location.getLatitude();   //위도
        accuracy = location.getAccuracy();    //정확도
        //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
        //Network 위치제공자에 의한 위치변화
        //Network 위치는 Gps에 setTextView 비해 정확도가 많이 떨어진다.

        ((TextView)findViewById(R.id.current_position)).setText
        (
            "위도 : " + longitude
            + "\n경도 : " + latitude
            + "\n정확도 : "  + accuracy
        );
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        // TODO Auto-generated method stub
    }
    @Override
    public void onProviderEnabled(String provider)
    {
        // TODO Auto-generated method stub
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        // TODO Auto-generated method stub
    }
}