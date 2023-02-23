package com.SShop.ERikshaDriver;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

//import com.SShop.ERikshaDriver.databinding.ActivityMapsBinding;
//import com.SShop.ERikshaDriver.databinding.ActivityMapsBinding;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SplashScreen extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, RoutingListener {

    private GoogleMap mMap;
//    private ActivityMapsBinding binding;

    //google map object
//    private GoogleMap mMap;

    //current and destination location objects
    Location myLocation = null;
    Location destinationLocation = null;
    protected LatLng start = null;
    protected LatLng end = null;

    //to get location permissions.
    private final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission = false;

    //polyline object
    private List<Polyline> polylines = null;

    LocationRequest locationRequest;

    Boolean AlreadyGone = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        SessionManager sessionManager = new SessionManager(this);


        locationRequest = new LocationRequest();
//        locationRequest.setInterval()
        locationRequest.setInterval(5000); // two minute interval
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


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
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                // this is for checking if its already gone
                if (!AlreadyGone)
                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                        AlreadyGone = true;

                        LocationServices.getFusedLocationProviderClient(SplashScreen.this).removeLocationUpdates(this);
                        int index = locationResult.getLocations().size() - 1;
                        myLocation = locationResult.getLocations().get(index);
//                    myLocation=location;


                        //                //Add Marker on route ending position
//                    MarkerOptions endMarker = new MarkerOptions();
//                    endMarker.position(new LatLng(location.getLatitude(),location.getLongitude()));
//                    endMarker.title("Destinationsdf").icon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_arrow));
//

                        SessionManager sessionManager = new SessionManager(getApplicationContext());
                        sessionManager.setKeyCurrentLoc(KeyCon.locToString(myLocation));

                        if (sessionManager.checkLogin())
                            gotoDestAfterCheck();
                        else {
                            startActivity(new Intent(SplashScreen.this, LoginFront.class));
                            finish();
                        }
                    } else {

                        Toast.makeText(SplashScreen.this, "Not found", Toast.LENGTH_SHORT).show();
                    }
            }
        }, Looper.getMainLooper());


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // this is for checking if its already gone
                if (!AlreadyGone) {
                    if (sessionManager.checkLogin()) {
                        gotoDestAfterCheck();
                    } else {
                        startActivity(new Intent(SplashScreen.this, LoginFront.class));
                        finish();
                    }

                    AlreadyGone = true;

                }


            }
        }, 10000);

        //request location permission.
        requestPermision();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    private void requestPermision() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {
            locationPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //if permission granted.
                    locationPermission = true;
                    getMyLocation();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    //to get user location
    private void getMyLocation() {
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
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {


                // this is for checking if its already gone
                if (!AlreadyGone) {
                    myLocation = location;


                    //here if fetched then will go
                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    sessionManager.setKeyCurrentLoc(myLocation.getLatitude() + "," + myLocation.getLongitude());

                    if (sessionManager.checkLogin())
                        gotoDestAfterCheck();
                    else {
                        startActivity(new Intent(SplashScreen.this, LoginFront.class));
                        finish();
                    }

                    AlreadyGone = true;
                }

            }
        });


    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        getMyLocation();
    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {

    }

    @Override
    public void onRoutingCancelled() {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public void gotoDestAfterCheck() {


        SessionManager sessionManager = new SessionManager(this);


        //this is  uploading data and update the data if lasupdate is equal to yesterday data

        //getting yester day date
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());


        //first check fro trri is off
        if (sessionManager.getKEY_RikshaStatus().equals(KeyCon.ERikshaDriver.Value.Rik_Stat_Off)) {
            startActivity(new Intent(SplashScreen.this, StartActivity.class));
            finish();
        } else if (!sessionManager.getKeySystemDataDate().equals(currentDate)) {

            startActivity(new Intent(SplashScreen.this, ValidateDataUpload.class));

            finish();


            //here we force the used to upload data online
            //show view here

            //for taking topics


//            final TextInputLayout nameV = mview.findViewById(R.id.text);
//            final TextInputLayout problemV=mview.findViewById(R.id.problem);

//            TextView headV = mview.findViewById(R.id.heading);

        } else {
            //welcome voice
            KeyCon.speakStr(getString(R.string.welc) + sessionManager.getKeyName(), getApplicationContext());


            startActivity(new Intent(SplashScreen.this, HomeActivity.class));
            finish();
        }


    }

}