package com.example.andrewtrainor.compoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ListViewActivity extends AppCompatActivity {
    public static final String USER_INPUT2 = "";
    //initialize variables to assign by element ID and display data
    String API_KEY = "4eba6e69b643edd0fca09316822bd45e";

    //initialize return button
    private Button goBackbutton;

    private RequestQueue mQueue;

    //initialize the variables
    TextView temperature, temphigh, templow, humidity, city, sunrise, sunset, latitude, longitude, pressure, windspeed;
    double lat, lon;
    String day1,day2,day3,day4,day5,city1;
    int temp1, temp2, temp3, temp4, temp5;



    //create the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //get the intent
        Intent intent = getIntent();

        //get the user input from the previous activity
        String userinput = intent.getStringExtra(MainActivity.USER_INPUT);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.action_weather_history);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.action_reco_weather:
                        sendWeatherInfotoHome(this);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.action_weather_results:
                        sendWeatherInfotoTTS(this);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.action_weather_map:
                        sendWeatherInfotoMap(this);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.action_weather_history:
                        sendWeatherInfotoHistory(this);
                        overridePendingTransition(0,0);
                        return true;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
                return true;
            }
        });


        //initialize elements by ID
        humidity = findViewById(R.id.humidity);
        temperature = findViewById(R.id.temperature);
        temphigh = findViewById(R.id.temphigh);
        templow = findViewById(R.id.templow);
        city = findViewById(R.id.city);
        sunrise = findViewById(R.id.sunrise);
        sunset = findViewById(R.id.sunset);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        pressure = findViewById(R.id.pressure);
        windspeed = findViewById(R.id.windspeed);

        //setup new request queue using volley
        mQueue = Volley.newRequestQueue(this);

        //parse the JSON data according to the user input
        jsonParse(userinput);

    }

    //check whether the user has input a City, a Zipcode, or a GPS string structured as (lat,lon)
    private String getCityZipGps(String userinput){
        try{
            int yourNumber = Integer.parseInt(userinput);
            String zip = "https://api.openweathermap.org/data/2.5/forecast?zip=" + userinput;
            return zip;
        }catch (NumberFormatException ex) {

            if(userinput.indexOf(',') != -1){
                double latitude, longitude;
                String[] numberArray;
                numberArray = userinput.split(",");

                latitude = Double.parseDouble(numberArray[0]);
                longitude = Double.parseDouble(numberArray[1]);

                String latlong ="https://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude;

                return latlong;

            }
            String city = "https://api.openweathermap.org/data/2.5/forecast?q=" + userinput;
            return city;
        }

    }

    //Parsing the JSON data for userinput string.
    private void jsonParse (String userinput){

        //get the url which we want to retrieve JSON Data
        String url = getCityZipGps(userinput) + "&APPID=" + API_KEY;

        //setup a new request and Parse Data
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {//set the TextView element resulting values in the application for the data that is retrieved.


                            //parse json forecast data
                            day1 = response.getJSONArray("list").getJSONObject(0).getString("dt_txt");
                            day2 = response.getJSONArray("list").getJSONObject(7).getString("dt_txt");
                            day3 = response.getJSONArray("list").getJSONObject(16).getString("dt_txt");
                            day4 = response.getJSONArray("list").getJSONObject(23).getString("dt_txt");
                            day5 = response.getJSONArray("list").getJSONObject(31).getString("dt_txt");

                            temp1 = (response.getJSONArray("list").getJSONObject(0).getJSONObject("main").getInt("feels_like") - 273) * 9/5 + 32;
                            temp2 = (response.getJSONArray("list").getJSONObject(7).getJSONObject("main").getInt("feels_like") - 273) * 9/5 + 32;
                            temp3 = (response.getJSONArray("list").getJSONObject(16).getJSONObject("main").getInt("feels_like") - 273) * 9/5 + 32;
                            temp4 = (response.getJSONArray("list").getJSONObject(23).getJSONObject("main").getInt("feels_like") - 273) * 9/5 + 32;
                            temp5 = (response.getJSONArray("list").getJSONObject(31).getJSONObject("main").getInt("feels_like") - 273) * 9/5 + 32;




                            ListView resultsListView = (ListView) findViewById(R.id.simpleListView);

                            //new HashMap For Weather Data
                            HashMap<String, String> weatherInfo = new HashMap<>();
                            weatherInfo.put(day1, "Feels Like: " + temp1);
                            weatherInfo.put(day2, "Feels Like: " + temp2);
                            weatherInfo.put(day3, "Feels Like: " + temp3);
                            weatherInfo.put(day4, "Feels Like: " + temp4);
                            weatherInfo.put(day5, "Feels Like: " + temp5);


                            //create New List with HashMap List Items to display using adapter
                            List<HashMap<String, String>> listItems = new ArrayList<>();
                            SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), listItems, R.layout.activity_listview,
                                    new String[]{"First Line", "Second Line", "Third Line"},
                                    new int[]{R.id.text1, R.id.text2});


                            Iterator it = weatherInfo.entrySet().iterator();
                            while (it.hasNext())
                            {
                                HashMap<String, String> resultsMap = new HashMap<>();
                                Map.Entry pair = (Map.Entry)it.next();
                                resultsMap.put("First Line", pair.getKey().toString());
                                resultsMap.put("Second Line", pair.getValue().toString());
                                resultsMap.put("Third Line", pair.getValue().toString());
                                listItems.add(resultsMap);
                            }

                            resultsListView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        //add the request using volley.
        mQueue.add(request);
    }

    //sendIntotoTTS
    public void sendWeatherInfotoTTS(BottomNavigationView.OnNavigationItemSelectedListener view){
        Intent intent = getIntent();

        //get the user input from the previous activity
        String userinput = intent.getStringExtra(MainActivity.USER_INPUT);

        Intent i = new Intent(this, TextToSpeechActivity.class);
        i.putExtra(USER_INPUT2, userinput);

        //move to the next activity
        startActivity(i);
    }

    public void sendWeatherInfotoMap(BottomNavigationView.OnNavigationItemSelectedListener view){
        Intent intent = getIntent();

        //get the user input from the previous activity
        String userinput = intent.getStringExtra(MainActivity.USER_INPUT);

        Intent i = new Intent(this, MapActivity.class);
        i.putExtra(USER_INPUT2, userinput);

        //move to the next activity
        startActivity(i);
    }

    public void sendWeatherInfotoHome(BottomNavigationView.OnNavigationItemSelectedListener view){
        Intent intent = getIntent();

        //get the user input from the previous activity
        String userinput = intent.getStringExtra(MainActivity.USER_INPUT);

        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(USER_INPUT2, userinput);

        //move to the next activity
        startActivity(i);
    }

    public void sendWeatherInfotoHistory(BottomNavigationView.OnNavigationItemSelectedListener view){
        Intent intent = getIntent();

        //get the user input from the previous activity
        String userinput = intent.getStringExtra(MainActivity.USER_INPUT);

        Intent i = new Intent(this, ListViewActivity.class);
        i.putExtra(USER_INPUT2, userinput);

        //move to the next activity
        startActivity(i);
    }

}
