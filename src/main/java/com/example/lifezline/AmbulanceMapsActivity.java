package com.example.lifezline;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lifezline.utils.DirectionsJSONParser;
import com.example.lifezline.utils.LocationAddress;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AmbulanceMapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
//    private EditText mAutocompleteTextView;
    private PlacesAutocompleteTextView mAutocompleteTextView, mAutocompleteToTextView;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    HashMap<String, String> hospitals;
    LatLng currentLatLng, toLatLng;
    LinearLayout driver_layout;
    Button call_ambulance;
    ProgressDialog progressDialog;

//    private AdapterView.OnItemClickListener mAutocompleteClickListener
//            = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
//            final String placeId = String.valueOf(item.placeId);
////            Log.i(LOG_TAG, "Selected: " + item.description);
//            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
//                    .getPlaceById(mGoogleApiClient, placeId);
//            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
////            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
//
//        }
//    };
//
//
//    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
//            = new ResultCallback<PlaceBuffer>() {
//        @Override
//        public void onResult(PlaceBuffer places) {
//            if (!places.getStatus().isSuccess()) {
////                Log.e(LOG_TAG, "Place query did not complete. Error: " +
////                        places.getStatus().toString());
//                return;
//            }
//            // Selecting the first object buffer.
//            final Place place = places.get(0);
//            LatLng queriedLocation = place.getLatLng();
//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(queriedLocation);
//            markerOptions.title("Current Position");
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//            mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
//            CharSequence attributions = places.getAttributions();
//
//        }
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambulance_maps);

//        getSupportActionBar().setTitle("Map Location Activity");
        com.google.android.libraries.places.api.Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        mAutocompleteTextView = findViewById(R.id.autoCompleteTextView);
        mAutocompleteToTextView = findViewById(R.id.autoCompleteToTextView);
        call_ambulance = findViewById(R.id.call_ambulance);
        driver_layout = findViewById(R.id.driver_layout);
//        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
//        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
//                BOUNDS_MOUNTAIN_VIEW, null);
//        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);

//        mAutocompleteTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME,  com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);
//                Intent intent = new Autocomplete.IntentBuilder(
//                        AutocompleteActivityMode.OVERLAY, fields)
//                        .build(AmbulanceMapsActivity.this);
//                startActivityForResult(intent, 1101);
//            }
//        });
        hospitals = new HashMap<>();
        hospitals.put("Civil Hospital Karachi","24.859304,67.010408");
        hospitals.put("Jinnah Hospital Karachi","24.852006, 67.044228");
        hospitals.put("PNS Shifa Hospital","24.8369017,67.0479938");
        hospitals.put("Dr. Ziauddin Memorial Hospital","24.817172,67.007214");
        hospitals.put("South City Hospital Karachi","24.8155823,67.0173004");

        progressDialog = new ProgressDialog(AmbulanceMapsActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait..");

        mAutocompleteToTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHospitals();
            }
        });

        call_ambulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                                call_ambulance.setVisibility(View.GONE);
                                                driver_layout.setVisibility(View.VISIBLE);

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(AmbulanceMapsActivity.this, "my_channel_01")
                                                                        .setSmallIcon(R.drawable.ic_launcher_background) // notification icon
                                                                        .setContentTitle("Alert!") // title for notification
                                                                        .setContentText("Your Ambulance has arrived!") // message for notification
                                                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                                                        .setAutoCancel(true); // clear notification after click

                                                                int notifyID = 1;
                                                                String CHANNEL_ID = "my_channel_01";// The id of the channel.
                                                                CharSequence name = "My Channel";// The user-visible name of the channel.
                                                                int importance = NotificationManager.IMPORTANCE_HIGH;


                                                                Intent intent = new Intent(AmbulanceMapsActivity.this, AmbulanceMapsActivity.class);
                                                                @SuppressLint("WrongConstant") PendingIntent pi = PendingIntent.getActivity(AmbulanceMapsActivity.this,0,intent,Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                mBuilder.setContentIntent(pi);
                                                                NotificationManager mNotificationManager =
                                                                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                    NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                                                                    mNotificationManager.createNotificationChannel(mChannel);
                                                                }
                                                                mNotificationManager.notify(1, mBuilder.build());

                                                            }
                                                        });
                                                    }
                                                }, 3000);

                                            }
                                        });
                                    }
                                }, 3000);

                            }
                        });
                    }
                });
            }
        });

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mGoogleMap=googleMap;
//        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
//        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
//        if (mCurrLocationMarker != null) {
//            mCurrLocationMarker.remove();
//        }

        if (mCurrLocationMarker == null) {
//            mCurrLocationMarker.remove();


            //Place current location marker
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(location.getLatitude(), location.getLongitude(),
                    getApplicationContext(), new GeocoderHandler());

            currentLatLng = latLng;
            //move map camera
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));

        }

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(AmbulanceMapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1101) {
            if (resultCode == RESULT_OK) {
                com.google.android.libraries.places.api.model.Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("LIFEzLINE", "ManishPlace: " + place.getName() + ", " + place.getId());
                mAutocompleteTextView.setText(place.getName());

                LatLng latLng = place.getLatLng();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

                currentLatLng = latLng;
                LocationAddress locationAddress = new LocationAddress();
                locationAddress.getAddressFromLocation(latLng.latitude, latLng.longitude,
                        getApplicationContext(), new GeocoderHandler());
                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));

                if(toLatLng != null) {
//                    PolylineOptions polylineOptions = new PolylineOptions();
//                    polylineOptions.add(currentLatLng);
//                    polylineOptions.add(latLng);
//                    polylineOptions.width(12);
//                    polylineOptions.color(Color.RED);
//                    polylineOptions.geodesic(true);
//
//                    mGoogleMap.addPolyline(polylineOptions);
                    String url = getDirectionsUrl(currentLatLng, toLatLng);

                    DownloadTask downloadTask = new DownloadTask();

                    call_ambulance.setVisibility(View.VISIBLE);

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("LIFEzLINE", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
//            tvAddress.setText(locationAddress);
            mAutocompleteTextView.setText(locationAddress);
        }
    }


    public void showHospitals()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(AmbulanceMapsActivity.this);
        builder.setTitle("Select a Hospital");

// add a list
        final String[] hospitalArr = {"Civil Hospital Karachi", "Jinnah Hospital Karachi", "PNS Shifa Hospital", "Dr. Ziauddin Memorial Hospital", "South City Hospital Karachi"};

        builder.setItems(hospitalArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String latlong = "";
                double lat = 0;
                double lng = 0;
                LatLng latLng;
                MarkerOptions markerOptions;
                PolylineOptions polylineOptions;

                switch (which) {
                    case 0:
                        mAutocompleteToTextView.setText(hospitalArr[0]);
                        latlong = hospitals.get(hospitalArr[0]);

                        lat = Double.parseDouble(latlong.split(",")[0]);
                        lng = Double.parseDouble(latlong.split(",")[1]);

                        latLng = new LatLng(lat, lng);
                        markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(hospitalArr[0]);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

                        toLatLng = latLng;
                        //move map camera
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));

                        if(currentLatLng != null) {
//                            polylineOptions = new PolylineOptions();
//                            polylineOptions.add(currentLatLng);
//                            polylineOptions.add(latLng);
//                            polylineOptions.width(12);
//                            polylineOptions.color(Color.RED);
//                            polylineOptions.geodesic(true);
//
//                            mGoogleMap.addPolyline(polylineOptions);
                            String url = getDirectionsUrl(currentLatLng, toLatLng);

                            DownloadTask downloadTask = new DownloadTask();

                            call_ambulance.setVisibility(View.VISIBLE);

                            // Start downloading json data from Google Directions API
                            downloadTask.execute(url);
                        }
                        break;
                    case 1:

                        mAutocompleteToTextView.setText(hospitalArr[1]);
                        latlong = hospitals.get(hospitalArr[1]);

                        lat = Double.parseDouble(latlong.split(",")[0]);
                        lng = Double.parseDouble(latlong.split(",")[1]);

                        latLng = new LatLng(lat, lng);
                        markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(hospitalArr[1]);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

                        toLatLng = latLng;
                        //move map camera
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));

                        if(currentLatLng != null) {
//                            polylineOptions = new PolylineOptions();
//                            polylineOptions.add(currentLatLng);
//                            polylineOptions.add(latLng);
//                            polylineOptions.width(12);
//                            polylineOptions.color(Color.RED);
//                            polylineOptions.geodesic(true);
//
//                            mGoogleMap.addPolyline(polylineOptions);
                            String url = getDirectionsUrl(currentLatLng, toLatLng);

                            DownloadTask downloadTask = new DownloadTask();

                            call_ambulance.setVisibility(View.VISIBLE);

                            // Start downloading json data from Google Directions API
                            downloadTask.execute(url);
                        }

                        break;
                    case 2:

                        mAutocompleteToTextView.setText(hospitalArr[2]);
                        latlong = hospitals.get(hospitalArr[2]);

                        lat = Double.parseDouble(latlong.split(",")[0]);
                        lng = Double.parseDouble(latlong.split(",")[1]);

                        latLng = new LatLng(lat, lng);
                        markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(hospitalArr[2]);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

                        toLatLng = latLng;
                        //move map camera
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));

                        if(currentLatLng != null) {
//                            polylineOptions = new PolylineOptions();
//                            polylineOptions.add(currentLatLng);
//                            polylineOptions.add(latLng);
//                            polylineOptions.width(12);
//                            polylineOptions.color(Color.RED);
//                            polylineOptions.geodesic(true);
//
//                            mGoogleMap.addPolyline(polylineOptions);

                            String url = getDirectionsUrl(currentLatLng, toLatLng);

                            DownloadTask downloadTask = new DownloadTask();

                            call_ambulance.setVisibility(View.VISIBLE);

                            // Start downloading json data from Google Directions API
                            downloadTask.execute(url);
                        }

                        break;
                    case 3:

                        mAutocompleteToTextView.setText(hospitalArr[3]);
                        latlong = hospitals.get(hospitalArr[3]);

                        lat = Double.parseDouble(latlong.split(",")[0]);
                        lng = Double.parseDouble(latlong.split(",")[1]);

                        latLng = new LatLng(lat, lng);
                        markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(hospitalArr[3]);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

                        toLatLng = latLng;
                        //move map camera
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));

                        if(currentLatLng != null) {
//                            polylineOptions = new PolylineOptions();
//                            polylineOptions.add(currentLatLng);
//                            polylineOptions.add(latLng);
//                            polylineOptions.width(12);
//                            polylineOptions.color(Color.RED);
//                            polylineOptions.geodesic(true);
//
//                            mGoogleMap.addPolyline(polylineOptions);

                            String url = getDirectionsUrl(currentLatLng, toLatLng);

                            DownloadTask downloadTask = new DownloadTask();

                            call_ambulance.setVisibility(View.VISIBLE);

                            // Start downloading json data from Google Directions API
                            downloadTask.execute(url);
                        }

                        break;
                    case 4:

                        mAutocompleteToTextView.setText(hospitalArr[4]);
                        latlong = hospitals.get(hospitalArr[4]);

                        lat = Double.parseDouble(latlong.split(",")[0]);
                        lng = Double.parseDouble(latlong.split(",")[1]);

                        latLng = new LatLng(lat, lng);
                        markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(hospitalArr[4]);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

                        toLatLng = latLng;

                        //move map camera
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));


                        if(currentLatLng != null) {
//                            polylineOptions = new PolylineOptions();
//                            polylineOptions.add(currentLatLng);
//                            polylineOptions.add(latLng);
//                            polylineOptions.width(12);
//                            polylineOptions.color(Color.RED);
//                            polylineOptions.geodesic(true);
//
//                            mGoogleMap.addPolyline(polylineOptions);
                            String url = getDirectionsUrl(currentLatLng, toLatLng);

                            DownloadTask downloadTask = new DownloadTask();

                            call_ambulance.setVisibility(View.VISIBLE);

                            // Start downloading json data from Google Directions API
                            downloadTask.execute(url);
                        }

                        break;
                }
            }
        });

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat").toString());
                    double lng = Double.parseDouble(point.get("lng").toString());
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            mGoogleMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key="+getResources().getString(R.string.google_maps_key);
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

}