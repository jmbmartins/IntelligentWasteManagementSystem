CREATE TABLE `Companie` (
  `ID_Employee` varchar(255) PRIMARY KEY,
  `email` varchar(255),
  `password` varchar(255),
  `role` varchar(255)
);

CREATE TABLE `Regions` (
  `ID_Region` varchar(255) PRIMARY KEY,
  `name_region` varchar(255)
);

CREATE TABLE `Containers` (
  `ID_Container` varchar(255) PRIMARY KEY,
  `region_id` varchar(255),
  `latitude` float,
  `longitude` float
);

CREATE TABLE `SensorData` (
  `ID_Record` varchar(255) PRIMARY KEY,
  `ID_Container` varchar(255),
  `s1_r` float,
  `s1_o` float,
  `s2_r` float,
  `s2_o` float,
  `s3_r` float,
  `s3_o` float,
  `timestamp` timestamp
);

CREATE TABLE `Final_Stats` (
  `ID_Result` varchar(255) PRIMARY KEY,
  `ID_Container` varchar(255),
  `fill_level` float,
  `timestamp` timestamp
);

ALTER TABLE `Containers` ADD FOREIGN KEY (`region_id`) REFERENCES `Regions` (`ID_Region`);

ALTER TABLE `SensorData` ADD FOREIGN KEY (`ID_Container`) REFERENCES `Containers` (`ID_Container`);

ALTER TABLE `Final_Stats` ADD FOREIGN KEY (`ID_Container`) REFERENCES `Containers` (`ID_Container`);
