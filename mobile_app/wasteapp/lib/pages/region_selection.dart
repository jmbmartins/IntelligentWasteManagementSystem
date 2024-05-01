// region_selection.dart
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'waste_bins.dart';

class RegionSelection extends StatefulWidget {
  @override
  _RegionSelectionState createState() => _RegionSelectionState();
}

class _RegionSelectionState extends State<RegionSelection> {
  final regions = ['Region 1', 'Region 2', 'Region 3']; // replace with your actual regions

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Select your region'),
        actions: [
          IconButton(
            icon: Icon(Icons.close),
            onPressed: () => Navigator.pop(context),
          ),
        ],
      ),
      body: ListView.builder(
        itemCount: regions.length,
        itemBuilder: (context, index) {
          return ListTile(
            title: Text(regions[index]),
            onTap: () async {
              final prefs = await SharedPreferences.getInstance();
              prefs.setString('lastRegion', regions[index]);
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => WasteBins(region: regions[index])),
              );
            },
          );
        },
      ),
    );
  }
}
