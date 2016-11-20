package com.android.podoal.project_podoal.datamodel;

/**
 * Created by tj on 2016-11-17.
 */

public class SightDTO {

    private String sight_id;
    private double latitude;
    private double longitude;
    private double radius;

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
        this.info = dto.getName();
        this.local_number_ID = dto.getLocal_number_ID();
    }


    @Override
    public String toString() {
        return "SightDTO{" +
                "sight_id='" + sight_id + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", radius=" + radius +
                ", name='" + name + '\'' +
                ", info='" + info + '\'' +
                ", local_number_ID='" + local_number_ID + '\'' +
                '}';
    }

}
