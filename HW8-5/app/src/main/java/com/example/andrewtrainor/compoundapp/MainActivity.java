package com.example.andrewtrainor.compoundapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_reco_weather:
                        Toast.makeText(MainActivity.this, "Speech Recognition Weather", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_weather_results:
                        Toast.makeText(MainActivity.this, "Weather Results", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_weather_map:
                        Toast.makeText(MainActivity.this, "Weather Map", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_weather_history:
                        Toast.makeText(MainActivity.this, "Weather History", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }
}