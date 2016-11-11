package com.android.podoal.project_podoal.datamodel;

import java.util.Date;

/**
 * Created by tj on 2016-11-12.
 */

public class VisitedSightDTO {

    public VisitedSightDTO(String member_id, String sight_id, Date visited_date, int visited_id) {
        this.member_id = member_id;
        this.sight_id = sight_id;
        this.visited_date = visited_date;
        this.visited_id = visited_id;
    }

    private String member_id;
    private String sight_id;
    private Date   visited_date;
    private int   visited_id;

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
}
