package com.example.andrewtrainor.compoundapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String USER_INPUT = "";
    private final int REQ_CODE = 100;

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
                        startActivity(new Intent(getApplicationContext(), MapActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.action_weather_history:
                        startActivity(new Intent(getApplicationContext(), ListViewActivity.class));
                        overridePendingTransition(0,0);
                        return true;
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

    public void speechToText(View v) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
        try {
            startActivityForResult(intent, REQ_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry your device not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    EditText editText = (EditText) findViewById(R.id.weatherInput);
                    String results = String.valueOf(result.get(0));
                    editText.setText(results);

                    Intent intent = new Intent(this, SecondaryActivity.class);

                    //get the userinput
                    String userInput = editText.getText().toString();

                    //pass the userinput data to the next activity
                    intent.putExtra(USER_INPUT, userInput);

                    //move to the next activity
                    startActivity(intent);
                }
                break;
            }
        }
    }



}