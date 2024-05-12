package com.example.wasteapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ViewWasteBinsActivity extends Activity {

    private List<Container> containers; // This should be your list of Container objects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waste_bins); // replace with your layout file name

        TextView regionTitle = findViewById(R.id.regionTitle);
        ListView listView = findViewById(R.id.listView);
        Button changeRegionButton = findViewById(R.id.changeRegionButton);
        Button quitButton = findViewById(R.id.quitButton);

        // Set the region title
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String regionName = prefs.getString("selectedRegion", "Default Region");
        regionTitle.setText(regionName);

        // Set up the list view
        containers = new ArrayList<>(); // Populate this list with your Container objects
        ArrayAdapter<Container> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, containers);
        listView.setAdapter(adapter);

        // Set up the change region button
        changeRegionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle change region
            }
        });

        // Set up the quit button
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the selected region
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("selectedRegion", regionTitle.getText().toString());
                editor.apply();

                // Close the app
                finish();
            }
        });
    }
}
