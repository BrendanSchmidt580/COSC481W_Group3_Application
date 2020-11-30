package com.ctorrella.cosc481w_group3_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.core.app.ActivityCompat;

import android.Manifest;
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



import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    //Initialize variables
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, SubmitAttendance.class);

        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);

        fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser() != null){ //Check if the user is already logged in.
            startActivity(intent);
        }


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do something when the button is clicked
                String email = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if(TextUtils.isEmpty(email)){ //If email field is empty, return error
                    usernameEditText.setError("Email is Required to Login.");
                    return;
                }
                if(TextUtils.isEmpty(password)){ //If password field is empty, return error
                    passwordEditText.setError("Password is Required to Login.");
                    return;
                }
                //If user not logged in (Doesn't exist in DB), register them, then push to next View.
                //Otherwise they're logged in (Does Exist in DB), push to next View

                login(email, password, intent);
            }
        };

        loginButton.setOnClickListener(listener);

    }

    public void login(String email, String password, Intent intent)
    {
        fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "User Logged In", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
                else {
                    fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {   //Register went fine.
                                Toast.makeText(getApplicationContext(), "User Created.", Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                            }
                            else
                            {   //Register didn't go fine.
                                Toast.makeText(getApplicationContext(), "Error in Registration " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

}
