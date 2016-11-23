package com.android.podoal.project_podoal.arrayAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.podoal.project_podoal.R;
import com.android.podoal.project_podoal.datamodel.VisitedSightDTO;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by tj on 2016-11-23.
 */

public class VisitedSightAdapter extends BaseAdapter {

    private int layout;
    private List<VisitedSightDTO> visitedSightList;
    private LayoutInflater inflater;

    public VisitedSightAdapter(Context context, int layout, List<VisitedSightDTO> visitedSightList) {
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
        this.visitedSightList = visitedSightList;
    }

    @Override
    public VisitedSightDTO getItem(int i) {
        return this.visitedSightList.get(i);
    }

    @Override
    public int getCount() {
        return this.visitedSightList.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        if(convertView==null){
            convertView=inflater.inflate(layout,parent,false);
        }

        VisitedSightDTO item = visitedSightList.get(i);

        TextView visitedTime = (TextView)convertView.findViewById(R.id.visited_time);
        visitedTime.setText(item.getVisited_date().toString());

        TextView visitedSight = (TextView)convertView.findViewById(R.id.visited_sight);
        visitedSight.setText(item.getSight_name());



        return null;
    }
}
