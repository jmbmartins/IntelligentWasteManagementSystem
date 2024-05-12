package com.example.wasteapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Button quitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.region_selection);

        listView = findViewById(R.id.listView);
        quitButton = findViewById(R.id.quitButton);

        // Check if the user has already selected a region
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String selectedRegion = prefs.getString("selectedRegion", "");
        if (!selectedRegion.isEmpty()) {
            // If the user has already selected a region, go to the "View waste bins" page
            startActivity(new Intent(this, ViewWasteBinsActivity.class));
            finish();
            return;
        }

        // Get the list of regions
        List<Region> regions = getRegions();

        // Create an adapter for the ListView
        ArrayAdapter<Region> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, regions);
        listView.setAdapter(adapter);

        // Set an item click listener for the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When a region is selected, save the selection and go to the "View waste bins" page
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("selectedRegion", regions.get(position).getName());
                editor.apply();

                startActivity(new Intent(MainActivity.this, ViewWasteBinsActivity.class));
                finish();
            }
        });

        // Set a click listener for the "Quit" button
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the "Quit" button is clicked, close the app
                finish();
            }
        });
    }

    private List<Region> getRegions() {
        List<Region> regions = new ArrayList<>();
        regions.add(new Region("Region 1"));
        regions.add(new Region("Region 2"));
        regions.add(new Region("Region 3"));
        // Add more regions as needed
        return regions;
    }
}