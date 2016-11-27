package com.android.podoal.project_podoal.datamodel;

import android.icu.util.ValueIterator;

import java.net.URLEncoder;
import java.util.Date;

/**
 * Created by tj on 2016-11-12.
 */

public class VisitedSightDTO {

    public VisitedSightDTO(String member_id, String sight_id, Date visited_date, int visited_id, String sight_name) {
        this.member_id = member_id;
        this.sight_id = sight_id;
        this.visited_date = visited_date;
        this.visited_id = visited_id;
        this.sight_name = sight_name;
    }

    public VisitedSightDTO() {
        this.member_id = "";
        this.sight_name = "";
        this.visited_date = new Date();
        this.visited_id = 0;
        this.sight_name = "";
    }

    public VisitedSightDTO(VisitedSightDTO dto) {
        this.member_id = dto.getMember_id();
        this.sight_id = dto.getSight_id();
        this.visited_date = dto.getVisited_date();
        this.visited_id = dto.getVisited_id();
        this.sight_name = dto.getSight_name();
    }

    private String member_id;
    private String sight_id;
    private Date   visited_date;
    private int   visited_id;
    private String sight_name;

    public String getSight_id() {
        return sight_id;
    }

    public void setSight_id(String sight_id) {
        this.sight_id = sight_id;
    }

    public Date getVisited_date() {
        return visited_date;
    }

    public void setVisited_date(Date visited_date) {
        this.visited_date = visited_date;
    }

    public int getVisited_id() {
        return visited_id;
    }

    public void setVisited_id(int visited_id) {
        this.visited_id = visited_id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }


    public String getSight_name() {
        return sight_name;
    }

    public void setSight_name(String sight_name) {
        this.sight_name = sight_name;
    }

    public String makePostData() {
        String postData = new String();

        try {
            postData += URLEncoder.encode("member_id", "UTF-8") + "=" + URLEncoder.encode(this.member_id, "UTF-8");
            postData += "&" + URLEncoder.encode("sight_id", "UTF-8") + "=" + URLEncoder.encode(this.getSight_id(), "UTF-8");
            postData += "&" + URLEncoder.encode("visited_id", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(this.getVisited_id()), "UTF-8");
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return postData;
    }
}
