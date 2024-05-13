package com.example.wasteapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ContainerAdapter extends ArrayAdapter<Container> {
    public ContainerAdapter(Context context, List<Container> containers) {
        super(context, 0, containers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Container container = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_waste_bin, parent, false);
        }

        TextView fillLevel = (TextView) convertView.findViewById(R.id.fillLevel);
        TextView distance = (TextView) convertView.findViewById(R.id.distance);

        fillLevel.setText("Fill Level: " + container.getFillLevel() + "%");
        distance.setText("Distance: " + container.getDistance() + "km");

        return convertView;
    }
}
