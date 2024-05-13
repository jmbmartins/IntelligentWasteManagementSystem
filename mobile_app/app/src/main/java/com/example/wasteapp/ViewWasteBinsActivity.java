package com.example.wasteapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewWasteBinsActivity extends Activity {

    private List<Container> containers = new ArrayList<>();
    private ContainerAdapter adapter;

    private Float userLat;
    private Float userLong;

    private int regionID;
    private String regionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waste_bins);

        TextView regionTitle = findViewById(R.id.regionTitle);
        ListView listView = findViewById(R.id.listView);
        Button changeRegionButton = findViewById(R.id.changeRegionButton);
        Button quitButton = findViewById(R.id.quitButton);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        regionName = prefs.getString("selectedRegion", "Default Region");
        regionID = prefs.getInt("selectedRegionID", 0);
        userLat = prefs.getFloat("userLat", 0);
        userLong = prefs.getFloat("userLong", 0);
        Log.d("ViewWasteActivity", "Region Name: " + regionName);
        Log.d("ViewWasteActivity", "Region ID: " + regionID);
        Log.d("ViewWasteActivity", "User Latitude: " + userLat);
        Log.d("ViewWasteActivity", "User Longitude: " + userLong);

        regionTitle.setText(regionName);

        getContainersByRegion();

        adapter = new ContainerAdapter(this, containers);
        listView.setAdapter(adapter);
        Log.d("ViewWasteBinsActivity", "Number of items in adapter: " + adapter.getCount());

        changeRegionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("selectedRegion");
                editor.apply();

                Intent intent = new Intent(ViewWasteBinsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("selectedRegion", regionTitle.getText().toString());
                editor.apply();

                finish();
            }
        });
    }

    public void getContainersByRegion() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();

                // First, fetch the containers for the selected region
                Request containersRequest = new Request.Builder()
                        .url("http://snf-63590.vm.okeanos-global.grnet.gr:3000/api/containers")
                        .build();

                try (Response containersResponse = client.newCall(containersRequest).execute()) {
                    String containersData = containersResponse.body().string();
                    Log.d("getContainersByRegion", "Containers data: " + containersData); // Log the containers data

                    JSONArray containersArray = new JSONArray(containersData);

                    List<Integer> containerIds = new ArrayList<>();
                    Map<Integer, Double> containerDistances = new HashMap<>();
                    for (int i = 0; i < containersArray.length(); i++) {
                        JSONObject containerObject = containersArray.getJSONObject(i);

                        if (containerObject.has("ID_Region") && containerObject.has("ID_Container") && containerObject.has("lat") && containerObject.has("long")) {
                            int jsonRegionId = containerObject.getInt("ID_Region");
                            int jsonContainerId = containerObject.getInt("ID_Container");
                            float jsonLat = (float) containerObject.getDouble("latitude");
                            float jsonLong = (float) containerObject.getDouble("longitude");

                            Log.d("getContainersByRegion", "Container " + jsonContainerId + ": Region ID = " + jsonRegionId + ", Lat = " + jsonLat + ", Long = " + jsonLong); // Log the container details

                            // If the container's region matches the selected region, add it to the list
                            if (jsonRegionId == regionID) {
                                containerIds.add(jsonContainerId);

                                // Calculate the distance to the container
                                double distance = calculateDistance(userLat, userLong, jsonLat, jsonLong);
                                containerDistances.put(jsonContainerId, distance);
                            }
                        }
                    }

                    Log.d("getContainersByRegion", "Container IDs: " + containerIds); // Log the container IDs

                    // Then, fetch the final stats for the selected containers
                    Request statsRequest = new Request.Builder()
                            .url("http://snf-63590.vm.okeanos-global.grnet.gr:3000/api/finalStats")
                            .build();

                    try (Response statsResponse = client.newCall(statsRequest).execute()) {
                        String statsData = statsResponse.body().string();
                        Log.d("getContainersByRegion", "Stats data: " + statsData); // Log the stats data

                        JSONArray statsArray = new JSONArray(statsData);

                        for (int i = 0; i < statsArray.length(); i++) {
                            JSONObject statsObject = statsArray.getJSONObject(i);

                            if (statsObject.has("ID_Container") && statsObject.has("fill_level") && statsObject.has("timestamp")) {
                                int id = statsObject.getInt("ID_Container");
                                double fillLevel = statsObject.getDouble("fill_level");
                                String timestamp = statsObject.getString("timestamp");

                                Log.d("getContainersByRegion", "Stats for container " + id + ": Fill level = " + fillLevel + ", Timestamp = " + timestamp); // Log the stats details

                                // If the container is in the list of selected containers, add it to the list
                                if (containerIds.contains(id)) {
                                    double distance = containerDistances.get(id);
                                    Container container = new Container(id, fillLevel, timestamp, distance);
                                    containers.add(container);
                                }
                            }
                        }

                        // Update the ListView on the main thread
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });

                    } catch (Exception e) {
                        Log.e("ViewWasteBinsActivity", "Error in getContainersByRegion", e);
                    }

                } catch (Exception e) {
                    Log.e("ViewWasteBinsActivity", "Error in getContainersByRegion", e);
                }
            }
        });
    }

    private double calculateDistance(double userLat, double userLong, double containerLat, double containerLong) {
        // Convert degrees to radians
        userLat = Math.toRadians(userLat);
        userLong = Math.toRadians(userLong);
        containerLat = Math.toRadians(containerLat);
        containerLong = Math.toRadians(containerLong);

        // Calculate the differences
        double dLat = containerLat - userLat;
        double dLong = containerLong - userLong;

        // Calculate the distance
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(userLat) * Math.cos(containerLat)
                * Math.pow(Math.sin(dLong / 2),2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Convert the distance from radians to kilometers
        double distance = 6371 * c;

        return distance;
    }
}
