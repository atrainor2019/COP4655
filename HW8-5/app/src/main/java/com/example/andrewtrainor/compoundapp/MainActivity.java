package com.example.andrewtrainor.compoundapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    public static final String USER_INPUT = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.action_reco_weather);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.action_reco_weather:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.action_weather_results:
                        sendWeatherInfo2(this);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.action_weather_map:
                        selectedFragment = new MapFragment();
                        break;

                    case R.id.action_weather_history:
                        selectedFragment = new HistoryFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
                return true;
            }
        });
    }

    //called when the user taps the search button and sends the info to the second activity
    public void sendWeatherInfo(View view){
        Intent intent = new Intent(this, SecondaryActivity.class);
        EditText editText = (EditText) findViewById(R.id.weatherInput);

        //get the userinput
        String userInput = editText.getText().toString();

        //pass the userinput data to the next activity
        intent.putExtra(USER_INPUT, userInput);

        //move to the next activity
        startActivity(intent);
    }

    public void sendWeatherInfo2(BottomNavigationView.OnNavigationItemSelectedListener view){
        Intent intent = new Intent(this, TextToSpeechActivity.class);
        EditText editText = (EditText) findViewById(R.id.weatherInput);

        //get the userinput
        String userInput = editText.getText().toString();

        //pass the userinput data to the next activity
        intent.putExtra(USER_INPUT, userInput);

        //move to the next activity
        startActivity(intent);
    }
}