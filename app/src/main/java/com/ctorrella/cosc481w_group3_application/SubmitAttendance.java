package com.ctorrella.cosc481w_group3_application;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class SubmitAttendance extends AppCompatActivity {

    public FusedLocationProviderClient fusedLocationClient;

    public DatabaseReference database;

    public double actualLongitude, actualLatitude;
    public Classroom class1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_attendance);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        TextView locationTextView = findViewById(R.id.locationTextView);
        TextView timeTextView = findViewById(R.id.timeTextView);
        Button checkAttendanceButton = findViewById(R.id.checkAttendanceButton);

        checkAttendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptAttendance();

            }
        });


        database = FirebaseDatabase.getInstance().getReference();

        class1 = new Classroom();


        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                class1.updateFromSnapshot(dataSnapshot);

                timeTextView.setText(class1.beginHour + ":" + class1.beginMinute + " - " + class1.endHour + ":" + class1.endMinute);
                locationTextView.setText(class1.location_name + "\n(+/- " + class1.accuracy + " meters)");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });


    }

    private void attemptAttendance() {

        boolean success = true;

        //you there, you're finally awake. (he's not on the list, what should we do???)

        boolean emailFound = false;
        for (Object s : class1.participants) {
            if (s.toString().equals(MainActivity.prefs.getString("email", "nothing"))) {
                emailFound = true;
            }
        }
        if (emailFound) {
            //Toast.makeText(this, "Student found in classlist", Toast.LENGTH_SHORT).show();
        } else {
            //email was not found in the database
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setMessage("Your student email address was not found in the database");
            dlgAlert.setTitle("Error");
            dlgAlert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss the dialog
                        }
                    });
            dlgAlert.create().show();
            //Toast.makeText(this, "Student not in classlist", Toast.LENGTH_SHORT).show();
            success = false;
        }


        //check if time is right
        Date currentTime = Calendar.getInstance().getTime();

        Date classBegin = (Date) currentTime.clone();
        Date classEnd = (Date) currentTime.clone();
        classBegin.setHours(class1.beginHour);
        classBegin.setMinutes(class1.beginMinute);
        classEnd.setHours(class1.endHour);
        classEnd.setMinutes(class1.endMinute);

        if (currentTime.before(classEnd) && currentTime.after(classBegin)) {
            //Toast.makeText(this, "Time valid", Toast.LENGTH_SHORT).show();
        } else {

            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setMessage("You are too early to too late to submit attendance");
            dlgAlert.setTitle("Error");
            dlgAlert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss the dialog
                        }
                    });
            dlgAlert.create().show();
            //Toast.makeText(this, "Time invalid", Toast.LENGTH_SHORT).show();
            success = false;
        }

        //check if location is right
        if (success) getActualLocation();

    }

    private void getActualLocation() {
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
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                        }

                        actualLatitude = location.getLatitude();
                        actualLongitude = location.getLongitude();

                        double distance = distance(actualLatitude, actualLongitude, class1.latitude, class1.longitude, "K") / 1000;
                        if (distance < class1.accuracy) {
                            //Toast.makeText(getContext(), "Location valid" + distance, Toast.LENGTH_SHORT).show();
                            doSubmission();
                        } else {
                            locationErrorDialog();
                        }

                        //Toast.makeText(SubmitAttendance.this, "lat = " + location.getLatitude() + "lon" + location.getLongitude(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public Context getContext(){
        return this.getContext();
    }

    public void locationErrorDialog(){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage("You're too far from the classroom");
        dlgAlert.setTitle("Error");
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss the dialog
                    }
                });
        dlgAlert.create().show();
    }

    private void doSubmission() {
        //Toast.makeText(this, "Submitting Attendance...", Toast.LENGTH_SHORT).show();

        Date date = Calendar.getInstance().getTime();
        database.child("Room_Values").child("class1").child("record").child(date.toString()).setValue(MainActivity.prefs.getString("email", "nothing"));

        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage("Attendance submitted successfully!");
        dlgAlert.setTitle("Success");
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss the dialog
                    }
                });
        dlgAlert.create().show();
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}