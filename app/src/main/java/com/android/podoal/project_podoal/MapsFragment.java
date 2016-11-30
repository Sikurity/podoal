package com.android.podoal.project_podoal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.android.podoal.project_podoal.arrayAdapter.SightInfoAdapter;
import com.android.podoal.project_podoal.datamodel.MemberInfo;
import com.android.podoal.project_podoal.datamodel.SightDTO;
import com.android.podoal.project_podoal.datamodel.VisitedSightDTO;
import com.android.podoal.project_podoal.dataquery.SelectQueryRunnable;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsFragment extends SupportMapFragment implements OnMapReadyCallback
{

    private static GoogleMap mMap;

    private static ArrayList<SightDTO> sightList = null;
    private static ArrayList<VisitedSightDTO> visitedSightList = null;

    public static ArrayList<SightDTO>  getSightList() { return sightList; }
    public static void setSightList(ArrayList<SightDTO> sightList)
    {
        MapsFragment.sightList = sightList;
    }

    public static ArrayList<VisitedSightDTO> getVisitedSightList() { return visitedSightList; }
    public static void  setVisitedSightList(ArrayList<VisitedSightDTO> visitedSightList)
    {
        MapsFragment.visitedSightList = visitedSightList;
    }

    public MapsFragment()
    {
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
            sightSetup();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            getActivity().moveTaskToBack(true);
            getActivity().finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        System.out.println("MAPS_FRAGMENT_ON_ACTIVITY_CREATED_END");
    }

    private void sightSetup() throws Exception
    {
        try
        {
            System.out.println("SIGHT_SETUP_BEGIN");

            new Thread(new SelectQueryRunnable("http://" + GlobalApplication.SERVER_IP_ADDR + ":" + GlobalApplication.SERVER_IP_PORT + "/podoal/db_get_sight_list.php")
            {
                @Override
                public void postRun(Object... params)
                {
                    String result = (String)params[0];
                    System.out.println("SIGHT_RESULT : " + result);

                    try
                    {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");

                        MapsFragment.setSightList(new ArrayList<SightDTO>());
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject entity = jsonArray.getJSONObject(i);
                            SightDTO dto = new SightDTO();

                            dto.setSight_id(entity.getString("sight_id"));
                            dto.setLatitude(entity.getDouble("latitude"));
                            dto.setLongitude(entity.getDouble("longitude"));
                            dto.setRadius(entity.getDouble("radius"));
                            dto.setName(entity.getString("name"));
                            dto.setInfo(entity.getString("info"));
                            dto.setLocal_number_ID(entity.getString("local_number_ID"));
                            MapsFragment.getSightList().add(new SightDTO(dto));
                        }

                        new Thread(new SelectQueryRunnable("http://" + GlobalApplication.SERVER_IP_ADDR + ":" + GlobalApplication.SERVER_IP_PORT + "/podoal/db_get_visited_sight.php?member_id=" + MemberInfo.getInstance().getId())
                        {
                            @Override
                            public void postRun(Object... params)
                            {
                                String result = (String)params[0];
                                System.out.println("VISITED_SIGHT_RESULT : " + result);

                                try
                                {
                                    JSONObject jsonObject = new JSONObject(result);
                                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                                    MapsFragment.setVisitedSightList(new ArrayList<VisitedSightDTO>());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject entity = jsonArray.getJSONObject(i);
                                        VisitedSightDTO dto = new VisitedSightDTO();

                                        dto.setSight_id(entity.getString("sight_id"));

                                        MapsFragment.getVisitedSightList().add(new VisitedSightDTO(dto));
                                    }

                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            MapsFragment.setMarkers();
                                            System.out.println("SET_MARKETS_POST_MAIN_THREAD");
                                        }
                                    });
                                }

                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }).start();

            System.out.println("SIGHT_SETUP_END");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
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

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                public boolean onMarkerClick(Marker marker) {

                    String sight_name = marker.getTitle();
                    String sight_info = marker.getSnippet();
                    double sight_latitude = marker.getPosition().latitude;
                    double sight_longitude = marker.getPosition().longitude;

                    Intent intent = new Intent(getActivity(), MapInfoActivity.class);

                    intent.putExtra("sight_name", sight_name);
                    intent.putExtra("sight_info", sight_info);
                    intent.putExtra("sight_latitude", sight_latitude);
                    intent.putExtra("sight_longitude", sight_longitude);

                    startActivity(intent);

                    return true;
                }
            });

            if( sightList != null && visitedSightList != null )
            {
                setMarkers();
                System.out.println("SET_MARKETS_POST_ONMAPREADY");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("ON_MAP_READY_END");
    }

    public static void setMarkers()
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