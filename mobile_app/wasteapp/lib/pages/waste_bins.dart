// waste_bins.dart
import 'package:flutter/material.dart';
import 'region_selection.dart';

class WasteBins extends StatelessWidget {
  final String region;

  const WasteBins({Key? key, required this.region}) : super(key: key);
  

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Waste Bins in $region'),
        actions: [
          IconButton(
            icon: Icon(Icons.close),
            onPressed: () => Navigator.pop(context),
          ),
        ],
      ),
      body: Center(
    child: ElevatedButton(
          child: Text('Reselect Region'),
          onPressed: () {
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => RegionSelection()),
            );
          },
        ),
      ),
    );
  }
}
