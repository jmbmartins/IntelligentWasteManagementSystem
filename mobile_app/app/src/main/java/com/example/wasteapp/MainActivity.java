package com.example.wasteapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.location.LocationListener;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Button quitButton;
    private List<Region> regions = new ArrayList<>();

    private LocationManager locationManager;

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
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        requestLocationPermissions();

        // Set an item click listener for the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Check for location permissions
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Request location updates
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            // If a location is available, save it in shared preferences
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putFloat("userLat", (float) location.getLatitude());
                            editor.putFloat("userLong", (float) location.getLongitude());
                            editor.apply();


                        }

                        // Implement other methods as needed
                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {}

                        @Override
                        public void onProviderEnabled(String provider) {}

                        @Override
                        public void onProviderDisabled(String provider) {}
                    });
                }
                else {
                    Log.d("MainActivity", "No permissions for location");
                }

                // When a region is selected, save the selection and go to the "View waste bins" page
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("selectedRegionID", regions.get(position).getId());
                editor.putString("selectedRegion", regions.get(position).getName());
                editor.apply();

                // Log the saved region
                Log.d("MainActivity", "Saved region: " + regions.get(position).getName() + "  ID:" + regions.get(position).getId());

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

        getRegions(new RegionsCallback() {
            @Override
            public void onCallback(List<Region> regions) {
                // Update your UI with the regions here
                MainActivity.this.regions = regions;
                ArrayAdapter<Region> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, regions);
                listView.setAdapter(adapter);
            }
        });
    }

    public interface RegionsCallback {
        void onCallback(List<Region> regions);
    }

    private void getRegions(RegionsCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Region> regions = new ArrayList<>();
                try {
                    URL url = new URL("http://snf-63590.vm.okeanos-global.grnet.gr:3000/api/regions");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Parse the response
                    JSONArray jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String regionName = jsonObject.getString("name_region");
                        int regionId = jsonObject.getInt("ID_Region");
                        regions.add(new Region(regionName, regionId));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("MainActivity", "Error fetching regions", e);
                }

                Log.d("MainActivity", "Fetched " + regions.size() + " regions");

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onCallback(regions);
                    }
                });
            }
        });
    }

    private void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // The location permissions were granted
            } else {
                // The location permissions were not granted
                Toast.makeText(this, "Location permissions are required to select a region", Toast.LENGTH_SHORT).show();
            }
        }
    }
}