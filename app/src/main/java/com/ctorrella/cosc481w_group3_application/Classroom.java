package com.ctorrella.cosc481w_group3_application;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Classroom {

    //name of this particular class
    public String name;

    //participants
    public ArrayList participants;

    //location
    public float longitude, latitude;
    public long accuracy;
    public String location_name;

    //time
    public int beginHour, beginMinute, endHour, endMinute;

    public void updateFromSnapshot(DataSnapshot dataSnapshot){
        name = dataSnapshot.child("Room_Values").child("class1").child("name").getValue(String.class);

        participants = new ArrayList<String>();

        for (DataSnapshot s : dataSnapshot.child("Room_Values").child("class1").child("participants").getChildren()){
            participants.add(s.getValue());
        }

        location_name = dataSnapshot.child("Room_Values").child("class1").child("location").child("name").getValue(String.class);

        longitude = Float.parseFloat(dataSnapshot.child("Room_Values").child("class1").child("location").child("coord").getValue(String.class).split(", ")[0]);
        latitude = Float.parseFloat(dataSnapshot.child("Room_Values").child("class1").child("location").child("coord").getValue(String.class).split(", ")[1]);
        accuracy = dataSnapshot.child("Room_Values").child("class1").child("location").child("accuracy").getValue(long.class);

        beginHour = dataSnapshot.child("Room_Values").child("class1").child("timeBounds").child("beginHour").getValue(int.class);
        endHour = dataSnapshot.child("Room_Values").child("class1").child("timeBounds").child("endHour").getValue(int.class);
        beginMinute = dataSnapshot.child("Room_Values").child("class1").child("timeBounds").child("beginMinute").getValue(int.class);
        endMinute = dataSnapshot.child("Room_Values").child("class1").child("timeBounds").child("endMinute").getValue(int.class);

    }

}
