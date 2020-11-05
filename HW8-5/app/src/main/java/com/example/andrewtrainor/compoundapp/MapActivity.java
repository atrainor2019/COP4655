package com.example.andrewtrainor.compoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String USER_INPUT2 = "";
    //initialize variables to assign by element ID and display data
    String API_KEY = "4eba6e69b643edd0fca09316822bd45e";

    //initialize return button
    private Button goBackbutton;
    //initialize google map
    private GoogleMap mMap;

    private RequestQueue mQueue;

    //initialize the variables
    double lat, lon;


    //create the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        //get the intent
        Intent intent = getIntent();

        //get the user input from the previous activity
        String userinput = intent.getStringExtra(MainActivity.USER_INPUT);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.action_weather_map);

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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //setup new request queue using volley
        mQueue = Volley.newRequestQueue(this);

        //parse the JSON data according to the user input
        jsonParse(userinput);


    }

    //set map on ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        TileProvider tileProvider = new UrlTileProvider(256, 256) {        @Override
        public URL getTileUrl(int x, int y, int zoom) {            /* Define the URL pattern for the tile images */
            String s = String.format("https://tile.openweathermap.org/map/temp_new/%d/%d/%d.png?appid=4eba6e69b643edd0fca09316822bd45e", zoom, x, y);
            if (!checkTileExists(x, y, zoom)) {
                return null;
            }            try {
                return new URL(s);
            } catch (MalformedURLException e) {
                throw new AssertionError(e);
            }
        }        /*
         * Check that the tile server supports the requested x, y and zoom.
         * Complete this stub according to the tile range you support.
         * If you support a limited range of tiles at different zoom levels, then you
         * need to define the supported x, y range at each zoom level.
         */
            private boolean checkTileExists(int x, int y, int zoom) {
                int minZoom = 12;
                int maxZoom = 16;            return (zoom >= minZoom && zoom <= maxZoom);
            }
        };
        Double lat = 28.5383;
        Double lon = -81.3792;
        LatLng loc = new LatLng(lat,lon);
        googleMap.addMarker(new MarkerOptions().position(loc).title("You are here!"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(12));
        TileOverlay tileOverlay = googleMap.addTileOverlay(new TileOverlayOptions()
                .tileProvider(tileProvider));
    }

    //check whether the user has input a City, a Zipcode, or a GPS string structured as (lat,lon)
    private String getCityZipGps(String userinput){
        try{
            int yourNumber = Integer.parseInt(userinput);
            String zip = "https://api.openweathermap.org/data/2.5/weather?zip=" + userinput;
            return zip;
        }catch (NumberFormatException ex) {

            if(userinput.indexOf(',') != -1){
                double latitude, longitude;
                String[] numberArray;
                numberArray = userinput.split(",");

                latitude = Double.parseDouble(numberArray[0]);
                longitude = Double.parseDouble(numberArray[1]);

                String latlong ="https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude;

                return latlong;

            }
            String city = "https://api.openweathermap.org/data/2.5/weather?q=" + userinput;
            return city;
        }

    }

    //Parsing the JSON data for userinput string.
    private void jsonParse(String userinput){

        //get the url which we want to retrieve JSON Data
        String url = getCityZipGps(userinput) + "&APPID=" + API_KEY;

        //setup a new request and Parse Data
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {//set the TextView element resulting values in the application for the data that is retrieved.

                            lat = response.getJSONObject("coord").getDouble("lat");
                            lon = response.getJSONObject("coord").getDouble("lon");


                            newLocation(mMap);

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


    //update the location of the map
    void newLocation(GoogleMap googleMap) {
        if (googleMap != null) {

            TileProvider tileProvider = new UrlTileProvider(256, 256) {        @Override
            public URL getTileUrl(int x, int y, int zoom) {            /* Define the URL pattern for the tile images */
                String s = String.format("https://tile.openweathermap.org/map/temp_new/%d/%d/%d.png?appid=4eba6e69b643edd0fca09316822bd45e", zoom, x, y);
                if (!checkTileExists(x, y, zoom)) {
                    return null;
                }            try {
                    return new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
            }        /*
             * Check that the tile server supports the requested x, y and zoom.
             * Complete this stub according to the tile range you support.
             * If you support a limited range of tiles at different zoom levels, then you
             * need to define the supported x, y range at each zoom level.
             */
                private boolean checkTileExists(int x, int y, int zoom) {
                    int minZoom = 12;
                    int maxZoom = 16;            return (zoom >= minZoom && zoom <= maxZoom);
                }
            };

            LatLng loc = new LatLng(lat,lon);
            googleMap.addMarker(new MarkerOptions().position(loc).title("You are here!"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(12));
            TileOverlay tileOverlay = googleMap.addTileOverlay(new TileOverlayOptions()
                    .tileProvider(tileProvider));

        }
    }

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
