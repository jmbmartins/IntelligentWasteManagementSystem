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
import android.widget.Toast;

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

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waste_bins);

        TextView regionTitle = findViewById(R.id.regionTitle);
        listView = findViewById(R.id.listView);
        Button changeRegionButton = findViewById(R.id.changeRegionButton);
        Button quitButton = findViewById(R.id.quitButton);
        Button intelligentSuggestionButton = findViewById(R.id.intelligentSuggestionButton);


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

        intelligentSuggestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (containers.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No containers available", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (containers.size() == 1) {
                    Toast.makeText(getApplicationContext(), "Only one container available: ", Toast.LENGTH_SHORT).show();
                    return;
                }

                double maxScore = -1;
                Container betterContainer = null;

                for (Container container : containers) {
                    // Calculate the score for each container
                    // Lower fill level and closer distance results in a higher score
                    // Squaring the distance gives it more weight in the calculation
                    double score = 1.0 / (container.getFillLevel() * Math.pow(container.getDistance(), 2));
                    Log.d("ViewDecision", "Container: " + containers.indexOf(container) + " Score:" + score);

                    // If this container's score is higher than the current max score,
                    // update the max score and the better container
                    if (score > maxScore) {
                        maxScore = score;
                        betterContainer = container;
                    }
                }

                // Display the position of the better container in the ListView
                if (betterContainer != null) {
                    int position = containers.indexOf(betterContainer);
                    String message = String.format("Better container is at position %d in the list", position + 1);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
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

                Request containersRequest = new Request.Builder()
                        .url("http://snf-63590.vm.okeanos-global.grnet.gr:3000/api/containers")
                        .build();

                try (Response containersResponse = client.newCall(containersRequest).execute()) {
                    String containersData = containersResponse.body().string();
                    JSONArray containersArray = new JSONArray(containersData);

                    List<Integer> containerIds = new ArrayList<>();
                    Map<Integer, Double> containerDistances = new HashMap<>();
                    for (int i = 0; i < containersArray.length(); i++) {
                        JSONObject containerObject = containersArray.getJSONObject(i);

                        if (containerObject.has("ID_Region") && containerObject.has("ID_Container") && containerObject.has("latitude") && containerObject.has("longitude")) {
                            int jsonRegionId = containerObject.getInt("ID_Region");
                            int jsonContainerId = containerObject.getInt("ID_Container");
                            float jsonLat = (float) containerObject.getDouble("latitude");
                            float jsonLong = (float) containerObject.getDouble("longitude");

                            Log.d("getContainersByRegion", "Container ID: " + jsonContainerId + ", Region ID: " + jsonRegionId + ", Latitude: " + jsonLat + ", Longitude: " + jsonLong);

                            if (jsonRegionId == regionID) {
                                containerIds.add(jsonContainerId);
                                double distance = calculateDistance(userLat, userLong, jsonLat, jsonLong);
                                containerDistances.put(jsonContainerId, distance);

                                Log.d("getContainersByRegion", "Added container with ID: " + jsonContainerId + ", Distance: " + distance);
                            }
                        }
                    }

                    Request statsRequest = new Request.Builder()
                            .url("http://snf-63590.vm.okeanos-global.grnet.gr:3000/api/finalStats")
                            .build();

                    try (Response statsResponse = client.newCall(statsRequest).execute()) {
                        String statsData = statsResponse.body().string();
                        JSONArray statsArray = new JSONArray(statsData);

                        // Create a map to keep track of the latest timestamp for each container
                        Map<Integer, Container> latestContainers = new HashMap<>();

                        for (int i = 0; i < statsArray.length(); i++) {
                            JSONObject statsObject = statsArray.getJSONObject(i);

                            if (statsObject.has("ID_Container") && statsObject.has("fill_level") && statsObject.has("timestamp")) {
                                int id = statsObject.getInt("ID_Container");
                                double fillLevel = statsObject.getDouble("fill_level");
                                String timestamp = statsObject.getString("timestamp");

                                Log.d("getContainersByRegion", "Stats - Container ID: " + id + ", Fill Level: " + fillLevel + ", Timestamp: " + timestamp);

                                if (containerIds.contains(id)) {
                                    double distance = containerDistances.get(id);
                                    Container container = new Container(id, fillLevel, timestamp, distance);

                                    // If this container has not been encountered before, or if this record's timestamp is later than the latest timestamp for this container
                                    if (!latestContainers.containsKey(id) || container.getTimestamp().compareTo(latestContainers.get(id).getTimestamp()) > 0) {
                                        // Update the record for this container
                                        latestContainers.put(id, container);
                                    }
                                }
                            }
                        }

                        // Replace the containers list with the values from the latestContainers map
                        containers = new ArrayList<>(latestContainers.values());

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Log the details of the containers that are going to be displayed in the ListView
                                for (Container container : containers) {
                                    Log.d("getContainersByRegion", "Post - Container ID: " + container.getId() + ", Fill Level: " + container.getFillLevel() + ", Timestamp: " + container.getTimestamp() + ", Distance: " + container.getDistance());
                                }
                                adapter = new ContainerAdapter(ViewWasteBinsActivity.this, containers);
                                listView.setAdapter(adapter);
                                Log.d("ViewWasteBinsActivity", "Number of items in adapter: " + adapter.getCount());
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
