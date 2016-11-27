package com.android.podoal.project_podoal.datamodel;

import android.net.Uri;

import java.net.URL;
import java.util.Date;

/**
 * Created by tj on 2016-11-27.
 */

public class MemberInfo {

    private static MemberInfo memberInfoSingleton;
    private String id;
    private URL profile_image;
    private URL thumbnail_image;
    private Date created;

    private MemberInfo() {

    }

    public static MemberInfo getInstance() {
        if (memberInfoSingleton == null) {
            memberInfoSingleton = new MemberInfo();
        }

        return memberInfoSingleton;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public URL getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(URL profile_image) {
        this.profile_image = profile_image;
    }

    public URL getThumbnail_image() {
        return thumbnail_image;
    }

    public void setThumbnail_image(URL thumbnail_image) {
        this.thumbnail_image = thumbnail_image;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
