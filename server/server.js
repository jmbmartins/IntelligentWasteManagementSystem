// Import required modules
const express = require('express');
const bodyParser = require('body-parser');
const mysql = require('mysql');
require('dotenv').config({ path: '../db_credentials.env' });

// Create an Express application
const app = express();
const port = 3000;

// Middleware to parse JSON bodies for this app
app.use(bodyParser.json());

// Create a connection to the database
const db = mysql.createConnection({
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASS,
  database: process.env.DB_NAME
});

// Connect to the database
db.connect((err) => {
  if (err) {
    throw err;
  }
  console.log('Connected to the database');
});

// Routes for handling CRUD operations on 'Companie'
app.get('/api/companie', (req, res) => {
  let sql = 'SELECT * FROM Companie';
  db.query(sql, (err, result) => {
    if (err) throw err;
    res.send(result);
  });
});

app.post('/api/companie', (req, res) => {
  let data = {
    ID_Employee: req.body.ID_Employee,
    email: req.body.email,
    password: req.body.password,
    admin: req.body.admin,
    token: req.body.token,
    timer: req.body.timer
  };
  let sql = 'INSERT INTO Companie SET ?';
  db.query(sql, data, (err, result) => {
    if (err) throw err;
    res.send('Company added...');
  });
});

app.put('/api/companie/:id', (req, res) => {
  let data = [req.body.email, req.body.password, req.body.admin, req.body.token, req.body.timer, req.params.id];
  let sql = 'UPDATE Companie SET email = ?, password = ?, admin = ?, token = ?, timer = ? WHERE ID_Employee = ?';
  db.query(sql, data, (err, result) => {
    if (err) throw err;
    res.send('Company updated...');
  });
});

app.delete('/api/companie/:id', (req, res) => {
  let data = [req.params.id];
  let sql = 'DELETE FROM Companie WHERE ID_Employee = ?';
  db.query(sql, data, (err, result) => {
    if (err) throw err;
    res.send('Company deleted...');
  });
});

// Routes for handling CRUD operations on 'Regions'
app.get('/api/regions', (req, res) => {
  let sql = 'SELECT * FROM Regions';
  db.query(sql, (err, result) => {
    if (err) throw err;
    res.send(result);
  });
});

app.post('/api/regions', (req, res) => {
  let data = {ID_Region: req.body.ID_Region, name_region: req.body.name_region};
  let sql = 'INSERT INTO Regions SET ?';
  db.query(sql, data, (err, result) => {
    if (err) throw err;
    res.send('Region added...');
  });
});

app.put('/api/regions/:id', (req, res) => {
  let data = [req.body.name_region, req.params.id];
  let sql = 'UPDATE Regions SET name_region = ? WHERE ID_Region = ?';
  db.query(sql, data, (err, result) => {
    if (err) throw err;
    res.send('Region updated...');
  });
});

app.delete('/api/regions/:id', (req, res) => {
  let data = [req.params.id];
  let sql = 'DELETE FROM Regions WHERE ID_Region = ?';
  db.query(sql, data, (err, result) => {
    if (err) throw err;
    res.send('Region deleted...');
  });
});


// Routes for handling CRUD operations on 'Containers'
app.get('/api/containers', (req, res) => {
  let sql = 'SELECT * FROM Containers';
  db.query(sql, (err, result) => {
    if (err) throw err;
    res.send(result);
  });
});

app.post('/api/containers', (req, res) => {
  let sql = 'INSERT INTO Containers SET ID_Container = ?, ID_Region = ?, latitude = ?, longitude = ?';
  let data = [req.body.ID_Container, req.body.ID_Region, req.body.latitude, req.body.longitude];
  db.query(sql, data, (err, result) => {
    if (err) throw err;
    res.send('Container added...');
  });
});

app.put('/api/containers/:id', (req, res) => {
  let sql = 'UPDATE Containers SET ID_Region = ?, latitude = ?, longitude = ? WHERE ID_Container = ?';
  let data = [req.body.ID_Region, req.body.latitude, req.body.longitude, req.params.id];
  db.query(sql, data, (err, result) => {
    if (err) throw err;
    res.send('Container updated...');
  });
});

app.delete('/api/containers/:id', (req, res) => {
  let sql = 'DELETE FROM Containers WHERE ID_Container = ?';
  let data = [req.params.id];
  db.query(sql, data, (err, result) => {
    if (err) throw err;
    res.send('Container deleted...');
  });
});

// Routes for handling CRUD operations on 'SensorData'
app.get('/api/sensorData', (req, res) => {
  let sql = 'SELECT * FROM SensorData';
  db.query(sql, (err, result) => {
    if (err) throw err;
    res.send(result);
  });
});

app.post('/api/sensorData', (req, res) => {
  let data = {
    ID_Record: req.body.ID_Record,
    ID_Container: req.body.ID_Container,
    s1_r: req.body.s1_r,
    s1_o: req.body.s1_o,
    s2_r: req.body.s2_r,
    s2_o: req.body.s2_o,
    s3_r: req.body.s3_r,
    s3_o: req.body.s3_o,
    timestamp: req.body.timestamp
  };
  let sql = 'INSERT INTO SensorData SET ?';
  db.query(sql, data, (err, result) => {
    if (err) throw err;
    res.send('Sensor data added...');
  });
});

app.put('/api/sensorData/:id', (req, res) => {
  let data = [
    req.body.ID_Container,
    req.body.s1_r,
    req.body.s1_o,
    req.body.s2_r,
    req.body.s2_o,
    req.body.s3_r,
    req.body.s3_o,
    req.body.timestamp,
    req.params.id
  ];
  let sql = 'UPDATE SensorData SET ID_Container = ?, s1_r = ?, s1_o = ?, s2_r = ?, s2_o = ?, s3_r = ?, s3_o = ?, timestamp = ? WHERE ID_Record = ?';
  db.query(sql, data, (err, result) => {
    if (err) throw err;
    res.send('Sensor data updated...');
  });
});

app.delete('/api/sensorData/:id', (req, res) => {
  let data = [req.params.id];
  let sql = 'DELETE FROM SensorData WHERE ID_Record = ?';
  db.query(sql, data, (err, result) => {
    if (err) throw err;
    res.send('Sensor data deleted...');
  });
});

// Routes for handling CRUD operations on 'Final_Stats'
app.get('/api/finalStats', (req, res) => {
  let sql = 'SELECT * FROM Final_Stats';
  db.query(sql, (err, result) => {
    if (err) throw err;
    res.send(result);
  });
});

app.post('/api/finalStats', (req, res) => {
  let data = {ID_Result: req.body.ID_Result, ID_Container: req.body.ID_Container, fill_level: req.body.fill_level, timestamp: req.body.timestamp};
  let sql = 'INSERT INTO Final_Stats SET ?';
  db.query(sql, data, (err, result) => {
    if (err) throw err;
    res.send('Final stat added...');
  });
});

app.put('/api/finalStats/:id', (req, res) => {
  let data = [req.body.ID_Container, req.body.fill_level, req.body.timestamp, req.params.id];
  let sql = 'UPDATE Final_Stats SET ID_Container = ?, fill_level = ?, timestamp = ? WHERE ID_Result = ?';
  db.query(sql, data, (err, result) => {
    if (err) throw err;
    res.send('Final stat updated...');
  });
});

app.delete('/api/finalStats/:id', (req, res) => {
  let data = [req.params.id];
  let sql = 'DELETE FROM Final_Stats WHERE ID_Result = ?';
  db.query(sql, data, (err, result) => {
    if (err) throw err;
    res.send('Final stat deleted...');
  });
});

// Start the server
app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});
