package com.SShop.ERikshaDriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

//import com.SShop.ERikshaDriver.databinding.ActivityMapsBinding;
import com.SShop.ERikshaDriver.databinding.ActivityMapsBinding;
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
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class SplashScreen  extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, RoutingListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


//                new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//        startActivity(new Intent(SplashScreen.this,HomeActivity.class));

//            }
//        },3000);

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

                myLocation=location;
//
//                SessionManager sessionManager=new SessionManager(getApplicationContext());
//                sessionManager.setKeyCurrentLoc(location.getLatitude()+","+location.getLongitude());

//                startActivity(new Intent(SplashScreen.this,HomeActivity.class));
//                finish();



            }
        });

        locationRequest = new LocationRequest();
//        locationRequest.setInterval()
        locationRequest.setInterval(5000); // two minute interval
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    LocationServices.getFusedLocationProviderClient(SplashScreen.this).removeLocationUpdates(this);
                    int index = locationResult.getLocations().size() - 1;
                    myLocation=locationResult.getLocations().get(index);
//                    myLocation=location;


                    //                //Add Marker on route ending position
//                    MarkerOptions endMarker = new MarkerOptions();
//                    endMarker.position(new LatLng(location.getLatitude(),location.getLongitude()));
//                    endMarker.title("Destinationsdf").icon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_arrow));
//

                    SessionManager sessionManager=new SessionManager(getApplicationContext());
                    sessionManager.setKeyCurrentLoc(myLocation.getLatitude()+","+myLocation.getLongitude());

                    startActivity(new Intent(SplashScreen.this,HomeActivity.class));
                    finish();
                }else{
                    Toast.makeText(SplashScreen.this,"Not found",Toast.LENGTH_SHORT).show();
                }
            }
        }, Looper.getMainLooper());

        LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
//                    myLocation=location;
//
//
//                    //                //Add Marker on route ending position
////                    MarkerOptions endMarker = new MarkerOptions();
////                    endMarker.position(new LatLng(location.getLatitude(),location.getLongitude()));
////                    endMarker.title("Destinationsdf").icon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_arrow));
////
//
//                    SessionManager sessionManager=new SessionManager(getApplicationContext());
//                    sessionManager.setKeyCurrentLoc(location.getLatitude()+","+location.getLongitude());
//
//                    startActivity(new Intent(SplashScreen.this,HomeActivity.class));
//                    finish();


                }
            }
        });



    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap=googleMap;
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
}