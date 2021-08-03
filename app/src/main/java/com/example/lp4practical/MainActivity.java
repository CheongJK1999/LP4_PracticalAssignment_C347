package com.example.lp4practical;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


public class MainActivity extends AppCompatActivity {
    EditText etLat, etLong;
    Button btnSave, btnShow;
    private GoogleMap map;
    String folderLocation;
    FusedLocationProviderClient client;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etLat = findViewById(R.id.etLat);
        etLong = findViewById(R.id.etLong);
        btnSave = findViewById(R.id.btnSave);
        btnShow = findViewById(R.id.btnShow);

        client = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)
                fm.findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady (GoogleMap googleMap) {
                UiSettings ui = map.getUiSettings();

                ui.setZoomControlsEnabled(true);

                LatLng poi_RP = new LatLng(1.44224, 103.785753);

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(poi_RP, 15));
                Marker rp = map.addMarker(new MarkerOptions()
                        .position(poi_RP)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                int permissionCheck = ContextCompat.checkSelfPermission((MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

                if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                } else {
                    Log.e("GMap - Permission", "GPS access has not been granted");
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                }
            }
        });

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Creating Folder
                        folderLocation = getFilesDir().getAbsoluteFile() + "/LP4";
                        File folder = new File(folderLocation, "19043996_CheongJunKai.txt");

                        if(folder.exists() == false) {
                            boolean result = folder.mkdir();
                            if(result == true) {
                                Log.d("File Read/Write", "Folder created");
                            }
                        }

                        try {
                            String folderLocation= getFilesDir().getAbsolutePath() + "/LP4";
                            File folderInternal = new File(folderLocation, "19043996_CheongJunKai.txt");
                                    FileWriter writeInternal = new FileWriter(folderInternal, true);
                            writeInternal.write("test data" + "\n");
                            writeInternal.flush();
                            writeInternal.close();
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Failed to write!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                        if (folder.exists() == true) {
                            String data ="";
                            try {
                                FileReader reader = new FileReader(folder);
                                BufferedReader br= new BufferedReader(reader);

                                String line = br.readLine();
                                while (line != null){
                                    data += line + "\n";line = br.readLine();
                                }
                                br.close();
                                reader.close();
                            }
                            catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Failed to read!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                            Log.d("Content", data);
                        }

                    }

                });

        checkPermission();
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>(){
            @Override
            public void onSuccess(Location location) {
                //Got last known location. In some situation this can be null
                if(location !=null) {
                    String msg = "Lat : " + location.getLatitude() +
                            " Lng : " + location.getLongitude();
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    String msg = "No Last Known Location found";



                    btnShow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    LocationRequest mLocationRequest = LocationRequest.create();
                                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                    mLocationRequest.setInterval(10000);
                                    mLocationRequest.setFastestInterval(5000);
                                    mLocationRequest.setSmallestDisplacement(100);

                                }
                            });
    }
    private boolean checkPermission() {
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
                
}