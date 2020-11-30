package com.ctorrella.cosc481w_group3_application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class SubmitAttendance extends AppCompatActivity {
     //Initialize variables
        SupportMapFragment supportMapFragment;
        FusedLocationProviderClient client;

    //Could we add a logout button on this page?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_attendance);
        
         //Assign variables
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        //Initialize fused location
        client = LocationServices.getFusedLocationProviderClient(this);

        //Check permission
        if(ActivityCompat.checkSelfPermission(SubmitAttendance.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //When permission is granted call getCurrentLocation method
            getCurrentLocation();
        }else{
            //When permission is denied
            //Request permission
            ActivityCompat.requestPermissions(SubmitAttendance.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
    }
    
    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
    
     private void getCurrentLocation() {
        //Initialize task location
        @SuppressLint("MissingPermission") Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                //When success
                if(location != null){
                    //Sync map
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            //Initialize latitude and longitude
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            //Create marker options
                            MarkerOptions options = new MarkerOptions().position(latLng)
                                    .title("I am here");
                            //Zoom map
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                            //Add marker on map
                            googleMap.addMarker(options);
                        }
                    });
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if( requestCode == 44){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //When permission is granted, call method
                getCurrentLocation();
            }

        }
    }
}
