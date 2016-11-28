package com.android.podoal.project_podoal;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.podoal.project_podoal.arrayAdapter.SightInfoAdapter;
import com.android.podoal.project_podoal.datamodel.MemberInfo;
import com.android.podoal.project_podoal.datamodel.SightDTO;
import com.android.podoal.project_podoal.datamodel.VisitedSightDTO;
import com.android.podoal.project_podoal.dataquery.SelectQueryGetter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends SupportMapFragment implements OnMapReadyCallback, LocationListener{

    private static GoogleMap mMap;
    private LocationManager locationManager;
    private String lmProvider;

    private static Location location;
    private static double longitude;
    private static double latitude;

    private static ArrayList<SightDTO> sightList;
    private static ArrayList<VisitedSightDTO> visitedSightList;

    public static Location getLocation() { return location; }

    public static double getLongitude() { return longitude; }

    public static double getLatitude() { return latitude; }

    public static ArrayList<SightDTO>  getSightList() { return sightList; }

    public static ArrayList<VisitedSightDTO>  getVisitedSightList() { return visitedSightList; }

    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        System.out.println("MAPS_FRAGMENT_ON_ACTIVITY_CREATED_BEGIN");

        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        try
        {
            sightList = new ArrayList<SightDTO>();
            visitedSightList = new ArrayList<VisitedSightDTO>();

            sightSetup(sightList, visitedSightList);
        }
        catch(Exception e)
        {
            getActivity().moveTaskToBack(true);
            getActivity().finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        System.out.println("MAPS_FRAGMENT_ON_ACTIVITY_CREATED_END");
    }

    private void sightSetup(ArrayList<SightDTO> sightList, ArrayList<VisitedSightDTO> visitedSightList)
    {
        String result = "";
        try
        {
            System.out.println("SIGHT_SETUP_BEGIN");

            SelectQueryGetter dbSelector = new SelectQueryGetter();
            result = dbSelector.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "http://" + GlobalApplication.SERVER_IP_ADDR + ":" + GlobalApplication.SERVER_IP_PORT + "/podoal/db_get_sight_list.php").get();
            System.out.println("SIGHT_RESULT : " + result);

            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("result");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject entity = jsonArray.getJSONObject(i);
                SightDTO dto = new SightDTO();

                dto.setSight_id(entity.getString("sight_id"));
                dto.setLatitude(entity.getDouble("latitude"));
                dto.setLongitude(entity.getDouble("longitude"));
                dto.setRadius(entity.getDouble("radius"));
                dto.setName(entity.getString("name"));
                dto.setInfo(entity.getString("info"));
                dto.setLocal_number_ID(entity.getString("local_number_ID"));

                sightList.add(new SightDTO(dto));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            System.out.println("VISITED_SIGHT_SETUP_BEGIN");
            //String member_id = "2011003155";
            String member_id = MemberInfo.getInstance().getId();

            SelectQueryGetter dbSelector = new SelectQueryGetter();
            result = dbSelector.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "http://" + GlobalApplication.SERVER_IP_ADDR + ":" + GlobalApplication.SERVER_IP_PORT + "/podoal/db_get_visited_sight.php?member_id=" + member_id).get();
            System.out.println("VISITED_SIGHT_RESULT : " + result);

            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("result");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject entity = jsonArray.getJSONObject(i);
                VisitedSightDTO dto = new VisitedSightDTO();

                dto.setSight_id(entity.getString("sight_id"));

                visitedSightList.add(new VisitedSightDTO(dto));
            }
            System.out.println("SIGHT_SETUP_END");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void gpsSetup()
    {
        System.out.println("GPS_SETUP_BEGIN");

        try
        {
            locationManager = SideMenuActivity.getLocationManager();
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

            // location = locationManager.getLastKnownLocation(lmProvider);
            List<String> providers = locationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {

                Location loc = locationManager.getLastKnownLocation(provider);
                if (loc == null) {
                    continue;
                }
                if (bestLocation == null || loc.getAccuracy() < bestLocation.getAccuracy()) {
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
                Toast.makeText(getActivity(),"현재 위치를 찾을 수 없습니다.",Toast.LENGTH_SHORT).show();
            else
                onLocationChanged(location);

            System.out.println("GPS_SETUP_END");
        }
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
    public void onMapReady(GoogleMap googleMap)
    {
        System.out.println("ON_MAP_READY_BEGIN");

        try
        {
            mMap = googleMap;

            mMap.setInfoWindowAdapter(new SightInfoAdapter(this.getContext()));

            // Add a marker in Sydney and move the camera
            LatLng seoul = new LatLng(37.56, 126.97);
            mMap.moveCamera( CameraUpdateFactory.newLatLng(seoul));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 12));

            gpsSetup();
            setMarkers(sightList, visitedSightList);

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                public boolean onMarkerClick(Marker marker) {

                    String sight_name = marker.getTitle();
                    String sight_info = marker.getSnippet();
                    double sight_latitude = marker.getPosition().latitude;
                    double sight_longitude = marker.getPosition().longitude;

                    Intent intent = new Intent(getActivity(), MapInfoActivity.class);

                    intent.putExtra("sight_name",sight_name);
                    intent.putExtra("sight_info",sight_info);
                    intent.putExtra("sight_latitude",sight_latitude);
                    intent.putExtra("sight_longitude",sight_longitude);

                    startActivity(intent);

                    return true;
                }
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("ON_MAP_READY_END");
    }

    @Override
    public void onLocationChanged(Location location)
    {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
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

    public static void setMarkers(List<SightDTO> sightList, List<VisitedSightDTO> visitedSightList)
    {
        mMap.clear();

        if( sightList != null)
        {
            for( SightDTO dto : sightList)
            {
                if (dto.isVisitedSight(visitedSightList))
                    mMap.addMarker(new MarkerOptions().position(new LatLng(dto.getLatitude(), dto.getLongitude())).title(dto.getName()).snippet(dto.getInfo()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                else
                    mMap.addMarker(new MarkerOptions().position(new LatLng(dto.getLatitude(), dto.getLongitude())).title(dto.getName()).snippet(dto.getInfo()));
            }
        }
    }
}