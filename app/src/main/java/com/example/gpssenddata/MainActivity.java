package com.example.gpssenddata;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class MainActivity extends Activity {

    TextView tvEnabledGPS;
    TextView tvStatusGPS;
    TextView tvLocationGPS;
    TextView tvEnabledNet;
    TextView tvStatusNet;
    TextView tvLocationNet;
    Statement statement;
    int i = 0;
    LocationManager locationManager;
    private LocationProvider _locationProvider;
    StringBuilder sbGPS = new StringBuilder();
    StringBuilder sbNet = new StringBuilder();
    String alt;
    String lat;
    String lon;
    String fu;
    String oi;
    String begin;
    String end;
    ArrayList<String> latitudes = new ArrayList<>();
    ArrayList<String> longtitudes = new ArrayList<>();
    ArrayList<String> altitudes = new ArrayList<>();
    ArrayList<String> fuel = new ArrayList<>();
    ArrayList<String> oil = new ArrayList<>();
    String timebegin = "11.07.2022  18:24:10";
    String timeend = "11.07.2022  22:13:43";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvEnabledGPS = (TextView) findViewById(R.id.tvEnabledGPS);
        tvStatusGPS = (TextView) findViewById(R.id.tvStatusGPS);
        tvLocationGPS = (TextView) findViewById(R.id.tvLocationGPS);
        tvEnabledNet = (TextView) findViewById(R.id.tvEnabledNet);
        tvStatusNet = (TextView) findViewById(R.id.tvStatusNet);
        tvLocationNet = (TextView) findViewById(R.id.tvLocationNet);
        //0
        latitudes.add("55.971603");
        longtitudes.add("37.396181");
        altitudes.add("0");
        fuel.add("100");
        oil.add("99");
        //1
        latitudes.add("55.935020");
        longtitudes.add("37.238689");
        altitudes.add("500");
        fuel.add("98");
        oil.add("97");
        //2
        latitudes.add("55.805964");
        longtitudes.add("37.233746");
        altitudes.add("1500");
        fuel.add("80");
        oil.add("85");
        //3
        latitudes.add("55.559766");
        longtitudes.add("37.981930");
        altitudes.add("2500");
        fuel.add("75");
        oil.add("83");
        //4
        latitudes.add("55.679304");
        longtitudes.add("37.657343");
        altitudes.add("2300");
        fuel.add("54");
        oil.add("75");
        //5
        latitudes.add("55.482473");
        longtitudes.add("37.809894");
        altitudes.add("600");
        fuel.add("42");
        oil.add("63");
        //6
        latitudes.add("55.412624");
        longtitudes.add("37.919363");
        altitudes.add("0");
        fuel.add("29");
        oil.add("49");
        //end
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        _locationProvider = locationManager
                .getProvider(LocationManager.GPS_PROVIDER);
        Toast.makeText(this, "до драйвера", Toast.LENGTH_SHORT).show();
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
            return;
        }
        Toast.makeText(this, "Begin", Toast.LENGTH_SHORT).show();

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.2.236:3306/test", "rootLocal", "123456");
            statement = connection.createStatement();
            Toast.makeText(this, "Connection OK", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000*15, 0, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 15, 0,
                locationListener);
        checkEnabled();


    }


    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                tvStatusGPS.setText("Status: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                tvStatusNet.setText("Status: " + String.valueOf(status));
            }
        }
    };

    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            tvLocationGPS.setText(formatLocation(location));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            location = locationManager.getLastKnownLocation
                    (LocationManager.GPS_PROVIDER);

            _locationProvider.supportsAltitude();


            alt = altitudes.get(i);
            lat = latitudes.get(i);
            lon = longtitudes.get(i);
            fu = fuel.get(i);
            oi = oil.get(i);
            begin = timebegin;
            end = timeend;
            String stat;
            if(alt != null && i < 7) {
                try {
                    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm:ss");
                    String date = df.format(Calendar.getInstance().getTime());
                    statement.executeUpdate("UPDATE test.testair SET " +
                            "altitude = " + "'" + alt + "'" + "," +
                            "latitude = " + "'" + lat + "'" + "," +
                            "fuel = " + "'" + fu + "'" + "," +
                            "oil = " + "'" + oi + "'" + "," +
                            "timebegin = " + "'" + begin + "'" + "," +
                            "timeend = " + "'" + end + "'" + "," +
                            "longtitude = " + "'" + lon + "' WHERE testair.id = 1;");
                    stat = "INSERT INTO test.history (lat, lon, h, time) VALUES (" +
                            lat + "," + lon + "," + alt + "," + "'" + date + "'" + ")";
                    statement.execute(stat);
                    Toast.makeText(this, "Send position OK", Toast.LENGTH_SHORT).show();
                    i++;
                    Thread.sleep(1000);
                } catch (SQLException | InterruptedException throwables) {
                    throwables.printStackTrace();
                    Toast.makeText(this, throwables.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            tvLocationNet.setText(formatLocation(location));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            location = locationManager.getLastKnownLocation
                    (LocationManager.GPS_PROVIDER);

            _locationProvider.supportsAltitude();


            alt = altitudes.get(i);
            lat = latitudes.get(i);
            lon = longtitudes.get(i);
            if(alt != null && i < 7) {
                try {
                    statement.executeUpdate("UPDATE test.testair SET " +
                            "altitude = " + "'" + alt + "'" + "," +
                            "latitude = " + "'" + lat + "'" + "," +
                            "longtitude = " + "'" + lon + "' WHERE testair.id = 1;");
                    Toast.makeText(this, "Send position OK", Toast.LENGTH_SHORT).show();
                    i++;
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    Toast.makeText(this, throwables.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            tvLocationNet.setText(formatLocation(location));
        }

    }

    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT, alt = %.2f",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()), location.getAltitude());
    }

    private void checkEnabled() {
        tvEnabledGPS.setText("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER));
        tvEnabledNet.setText("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    public void onClickLocationSettings(View view) {
        startActivity(new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    };

}