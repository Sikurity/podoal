package com.android.podoal.project_podoal.datamodel;

import java.util.List;

/**
 * Created by tj on 2016-11-17.
 */

public class SightDTO {

    private String sight_id;
    private double latitude;
    private double longitude;
    private double radius;

    private String name;
    private String info;
    private String local_number_ID;


    public SightDTO() {

    }

    public SightDTO(SightDTO dto) {
        this.sight_id = dto.getSight_id();
        this.latitude = dto.getLatitude();
        this.longitude = dto.getLongitude();
        this.radius = dto.getRadius();
        this.name = dto.getName();
        this.info = dto.getInfo();
        this.local_number_ID = dto.getLocal_number_ID();
    }

    public String getSight_id() {
        return sight_id;
    }

    public void setSight_id(String sight_id) {
        this.sight_id = sight_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getLocal_number_ID() {
        return local_number_ID;
    }

    public void setLocal_number_ID(String local_number_ID) {
        this.local_number_ID = local_number_ID;
    }

    public boolean isVisitedSight(List<VisitedSightDTO> visitedSightList) {
        for (int i = 0; i < visitedSightList.size(); i++) {
            VisitedSightDTO visitedSight = visitedSightList.get(i);

            if (this.getSight_id().compareTo(visitedSight.getSight_id()) == 0) {
                return true;
            }
        }
        return false;
    }
}
