// main.dart
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'pages/region_selection.dart';
import 'pages/waste_bins.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Waste based in IoT App',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: FutureBuilder(
        future: SharedPreferences.getInstance(),
        builder: (BuildContext context, AsyncSnapshot<SharedPreferences> snapshot) {
          if (snapshot.connectionState == ConnectionState.done) {
            final prefs = snapshot.data;
            if (prefs != null) {
              final lastRegion = prefs.getString('lastRegion');
              if (lastRegion != null) {
                return WasteBins(region: lastRegion);
              }
            }
          }
          return RegionSelection();
        },
      ),
    );
  }
}
