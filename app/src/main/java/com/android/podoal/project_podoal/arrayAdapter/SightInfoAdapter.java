package com.android.podoal.project_podoal.arrayAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.podoal.project_podoal.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by tj on 2016-11-27.
 */

public class SightInfoAdapter implements GoogleMap.InfoWindowAdapter {

    View mView;

    public SightInfoAdapter (final Context context) {
        this.mView = (View)((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.window_sight_info, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        render(marker, mView);
        return null;
    }

    private void render(Marker marker, View view) {

        String title = "관광지명:" + marker.getTitle();
        TextView titleUi = ((TextView) view.findViewById(R.id.txt_sight_name));
        if (title != null) {
            titleUi.setText(title);
        } else {
            titleUi.setText("");
        }

        String snippet = "설명:" + marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(R.id.txt_sight_info));
        if (snippet != null) {
            snippetUi.setText(snippet);
        } else {
            snippetUi.setText("");
        }

    }
}
