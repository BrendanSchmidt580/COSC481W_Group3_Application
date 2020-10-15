package com.ctorrella.cosc481w_group3_application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, SubmitAttendance.class);

        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do something when the button is clicked
                if (MainActivity.login(usernameEditText.getText().toString(), passwordEditText.getText().toString()));
                startActivity(intent);
            }
        };

        loginButton.setOnClickListener(listener);

    }

    public static boolean login(String username, String password){
        if (username.equals(password)) return true;
        return false;
    }




}