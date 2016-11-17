package com.android.podoal.project_podoal;

import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.content.Context;

import com.android.podoal.project_podoal.datamodel.SightDTO;
import com.android.podoal.project_podoal.datamodel.VisitedSightDTO;
import com.android.podoal.project_podoal.dataquery.InsertQueryGetter;
import com.android.podoal.project_podoal.dataquery.SelectQueryGetter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import android.location.LocationListener;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener{

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Location location;
    private String lmProvider;


    private SelectQueryGetter dbSelector;
    private List<SightDTO> sightList;


    private double longditude;
    private double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        dbSelector = new SelectQueryGetter();

        sightSetup();
        cameraSetup();
        gpsSetup();


    }

    private void sightSetup() {
        try {
            String result = dbSelector.execute("http://127.0.0.1/podoal/db_get_sight_list.php").get();

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject entity = jsonArray.getJSONObject(i);
                    SightDTO dto = new SightDTO();

                    dto.setSight_id(entity.getString("sight_id"));
                    dto.setLatitude(entity.getLong("latitude"));
                    dto.setLongditude(entity.getLong("longditude"));
                    dto.setRadius(entity.getLong("radius"));
                    dto.setName(entity.getString("name"));
                    dto.setInfo(entity.getString("info"));
                    dto.setLocal_number_ID(entity.getString("local_number_ID"));

                    sightList.add(new SightDTO(dto));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception e){
            e.printStackTrace();
        } finally {

        }
    }

    private void gpsSetup() {
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        lmProvider = locationManager.getBestProvider(criteria, true);

        if (lmProvider == null || locationManager.isProviderEnabled(lmProvider)) {
            List<String> providerList = locationManager.getAllProviders();

            for (int i = 0; i < providerList.size(); i++) {
                String providerName = providerList.get(i);

                if (locationManager.isProviderEnabled(providerName)) {
                    lmProvider = providerName;
                    break;
                }
            }
        }

        try {
            location = locationManager.getLastKnownLocation(lmProvider);
        } catch (SecurityException e){

        } finally {
            if (location == null) {
                Toast.makeText(this,"현재 위치를 찾을 수 없습니다.",Toast.LENGTH_SHORT).show();
            } else {
                onLocationChanged(location);
            }
        }
    }

    private void cameraSetup(){
        Button camera_btn = (Button)findViewById(R.id.camera_button);

        camera_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,1);
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng seoul = new LatLng(37.56, 126.97);
        mMap.addMarker( new MarkerOptions().position(seoul).title( "Marker in Seoul" ) );
        mMap.moveCamera( CameraUpdateFactory.newLatLng(seoul));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 12));

        for (int i = 0; i < sightList.size(); i++) {
            SightDTO dto = sightList.get(i);
            mMap.addMarker(new MarkerOptions().position(new LatLng(dto.getLatitude(), dto.getLongditude()))
                                                .title(dto.getName()));
        }

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(37.555873, 127.049488))
                .title("Hanyang Univ. IT/BT"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode) {
            case 1:
                if (location == null) {
                    Toast.makeText(this, "현재 위치를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    SightDTO matchedSight = ValidateSight();

                    if (matchedSight != null) {
                        InsertQueryGetter dbConnector = new InsertQueryGetter();
                        VisitedSightDTO visitedSightDTO = new VisitedSightDTO();

                        visitedSightDTO.setMember_id("tester");
                        visitedSightDTO.setSight_id(matchedSight.getSight_id());
                        String postData = visitedSightDTO.makePostData();

                        try {
                            String result = dbConnector.execute("http://127.0.0.1/podoal/db_insert_visited_sight.php", postData).get();

                            if (result != null) {

                            } else {

                            }

                        } catch (Exception e) {

                        }

                    } else {

                    }
                }
                break;
            default:
                break;
        }
    }

    private class Circle {
        public float x;
        public float y;
        public float r;
    }

    private SightDTO ValidateSight() {

        Circle c = new Circle();

        for (int i = 0; i < sightList.size(); i++) {
            SightDTO sight = sightList.get(i);

            c.x = sight.getLatitude();
            c.y = sight.getLongditude();
            c.r = sight.getRadius();

            if (((  (latitude - c.x) * (latitude - c.x)) +
                    ((longditude - c.y) * (longditude - c.y))) < (c.r * c.r)) {
                return sight;
            }
        }

        return null;
    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longditude = location.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }
}
