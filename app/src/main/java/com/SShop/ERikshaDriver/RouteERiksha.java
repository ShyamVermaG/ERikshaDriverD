package com.SShop.ERikshaDriver;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.SShop.ERikshaDriver.Notification.FcmNotificationsSender;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
//import com.google.type.LatLng;
import com.google.android.gms.maps.model.LatLng;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RouteERiksha extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, RoutingListener {


    //this will contain all the routes
    //1 one is mylocation
    ArrayList<LatLng> allPoints = new ArrayList<com.google.android.gms.maps.model.LatLng>();
    ArrayList<LatLng> allOnlinePickUpPoints = new ArrayList<com.google.android.gms.maps.model.LatLng>();

    TextView nextSt;


    LinearLayout pickUpC;


    //for google maps
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


    SessionManager sessionManager;

    ArrayList<String> locationAddrName=new ArrayList<>();
    ArrayList<String> locationAddr=new ArrayList<>();

    TextView nextStH;

    com.google.android.gms.location.LocationRequest locationRequest;


    View map;
    LinearLayout header;
    TextView headingText;
    Boolean mapFullScreen = false;

    String GMapKey="";

    LinearLayout con;

//    ArrayList<String> locationAddrList = new ArrayList<>();
//    ArrayList<String> locationList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_eriksha);

        con = findViewById(R.id.pickUpC);


        //to initillie reuests
        initiallOnlineReq();


        map = findViewById(R.id.map);
        nextStH = findViewById(R.id.nextStH);
        nextSt = findViewById(R.id.nextSt);
        pickUpC = findViewById(R.id.pickUpC);

        header = findViewById(R.id.header);
        headingText = findViewById(R.id.headingText);

        //get data from session of default locaitons and keys
        sessionManager = new SessionManager(this);
        String tamp = sessionManager.getKeyAllKeyLocation();

        ArrayList<ArrayList<String>> data = new ArrayList<>();

        data = KeyCon.convertStrToDefaultLoc(tamp);

        ArrayList<String> locNameHE = new ArrayList<>();

        locNameHE = data.get(0);
        locationAddr.addAll(data.get(1));

        //here we get only language data
        int lng = 1;
        if (sessionManager.getKeyAppLng().equals(KeyCon.ERikshaDriver.Value.LNG_ENG)) {
            lng = 0;
        }
        ArrayList<String> ta = new ArrayList<>();
        for (String a : locNameHE) {
            ta = new ArrayList<>();
            ta.addAll(Arrays.asList(a.split("<>")));
            locationAddrName.add(ta.get(lng));
        }


//        //get data from session of default locaitons and keys
//        String tamp = sessionManager.getKeyAllKeyLocation();
//
//        ArrayList<ArrayList<String>> data = new ArrayList<>();
//
//        data = KeyCon.convertStrToDefaultLoc(tamp);
//        locationAddrName = data.get(0);
//        locationAddr = data.get(1);
//

//        binding = ActivityMapsBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

        //request location permission.
        requestPermision();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


//\\Initiallizing all ids


        if (sessionManager.getKeySpeak()) {
            nextSt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    KeyCon.speakStr(getString(R.string.nxtStop) + nextSt.getText().toString(), getApplicationContext());
                }
            });
            nextStH.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    KeyCon.speakStr(getString(R.string.nxtStop) + nextSt.getText().toString(), getApplicationContext());
                }
            });


            nextStH.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakTVLP(nextStH, getApplicationContext());
                    return false;
                }
            });
            nextSt.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakTVLP(nextSt, getApplicationContext());
                    return false;
                }
            });

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //setting up text
        nextStH.setText(R.string.nxtStop);
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

    int i = 0;

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
//                MarkerOptions endMarker = new MarkerOptions();
//                endMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
//                endMarker.title("Destinationsdf").icon(KeyCon.bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_directions_car_24));
//                mMap.addMarker(endMarker);


//                myLocation = location;
//                LatLng ltlng = new LatLng(location.getLatitude(), location.getLongitude());
//
//
//                sessionManager.setKeyCurrentLoc(location.getLatitude()+","+location.getLongitude());
//
//                if(i==0){
//                    //initiallizeAllRoutes here
//                    initiateAll(ltlng);
//
////                    Toast.makeText(RouteERiksha.this, "Location added", Toast.LENGTH_SHORT).show();
//
//                    i++;
//                }

            }
        });

        //get destination location when user click on map
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//
//                end = latLng;
//
//                mMap.clear();
//
//                start = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
//
//                //start route finding
//                Findroutes(start, end);
//            }
//        });

    }


    // function to find Routes.
    public void Findroutes(ArrayList<LatLng> path) {

        //<------------Use allOnlineRe to find pickup points------------->

        GMapKey=sessionManager.getKeyGMAP_KEY();

        if (path.isEmpty()) {
                Toast.makeText(RouteERiksha.this, "Unable to get location", Toast.LENGTH_LONG).show();
            } else {

                Routing routing = new Routing.Builder()
                        .travelMode(AbstractRouting.TravelMode.WALKING)
                        .withListener(this)
                        .alternativeRoutes(true)
                        .waypoints(path)
                        .key(GMapKey)  //also define your api key here.
                        .build();
                routing.execute();
            }

        }




    //Routing call back functions.
    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
    Findroutes(allPoints);
    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(RouteERiksha.this, "Finding Route...", Toast.LENGTH_LONG).show();
    }

    //If Route finding success..
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

//        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
//        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if (polylines != null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng = null;
        LatLng polylineEndLatLng = null;


        polylines = new ArrayList<>();

        //to clear all previous prlages
        mMap.clear();
        setDefaultLocationWName();
        //add route(s) to the map using polyline
        for (int i = 0; i < route.size(); i++) {

            if (i == shortestRouteIndex) {
                polyOptions.color(getResources().getColor(R.color.yellow));
                polyOptions.width(10);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());

                Polyline polyline = mMap.addPolyline(polyOptions);
                polylineStartLatLng = polyline.getPoints().get(0);
                int k = polyline.getPoints().size();
                polylineEndLatLng = polyline.getPoints().get(k - 1);
                polylines.add(polyline);

            } else {

            }

        }


        //here we find & set the area name of next location
        nextSt.setText(setAreaName(allPoints.get(1)));

        //Add Marker on current  position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(allPoints.get(0));
        endMarker.title("MyLocation").icon(KeyCon.bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_start));
        mMap.addMarker(endMarker);

        //for setting up next stop
        nextSt.setText(setAreaName(allPoints.get(1)));

        int i = 1;
        for (; i + 1 < allPoints.size(); i++) {
            //Add Marker on route at every station position
            MarkerOptions startMarker = new MarkerOptions();
            startMarker.position(allPoints.get(i));
            if(checkIsPickUpPoint(allPoints.get(i)))
                startMarker.title("PickUp").icon(KeyCon.bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_person_pin_circle_24));
            else
                startMarker.title("Stopage").icon(KeyCon.bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_outlined_flag_24));

            mMap.addMarker(startMarker);

        }

        //Add Marker on route at end station position
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(allPoints.get(i));
        if(checkIsPickUpPoint(allPoints.get(i)))
            startMarker.title("PickUp").icon(KeyCon.bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_person_pin_circle_24));
        else
            startMarker.title("Stopage").icon(KeyCon.bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_flag_final));
        mMap.addMarker(startMarker);

        //focus camera to the current loca

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                allPoints.get(0), 20f);
        mMap.animateCamera(cameraUpdate);
    }

    private boolean checkIsPickUpPoint(LatLng latLng) {
        boolean tik=false;
        for(LatLng a:allOnlinePickUpPoints){
            if(latLng.equals(a)){
                tik=true;
            }
        }

        return tik;
    }


    //this will set the area name
    //according to the nearest predefined locations
    private String setAreaName(LatLng latLng) {

        float distnaceMin = 1000000;
        String areaName = "Null<>Null<>";

        Location startPo = new Location("locationA");
        Location endPo = new Location("locationB");

        startPo.setLatitude(latLng.latitude);
        startPo.setLongitude(latLng.longitude);


        float tamp = 0;
        for (int i = 0; i < locationAddr.size(); i++) {

            endPo = strToLocation(locationAddr.get(i));

            tamp = startPo.distanceTo(endPo);
            if (distnaceMin > tamp) {
                distnaceMin = tamp;
                areaName = locationAddrName.get(i);
            }
        }

        return areaName;

        //here we set area name according to the language
//        ArrayList<String> hee = new ArrayList<>();
//        hee.addAll(Arrays.asList(areaName.split("<>")));

//        if (sessionManager.getKeyAppLng().equals(KeyCon.ERikshaDriver.Value.LNG_ENG))
//        else
//            nextSt.setText(hee.get(1));
//        setTextAllLng(nextSt, areaName);
    }

    private Translator translatorHindi;


    private void setTextAllLng(TextView view, String areaName) {


        TranslatorOptions translatorOptionsHindi = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.HINDI)
                .build();

        translatorHindi = Translation.getClient(translatorOptionsHindi);


        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.getKeyAppLng().equals(KeyCon.ERikshaDriver.Value.LNG_ENG)) {
            //for english language direct set text
            view.setText(areaName);
        } else if (sessionManager.getKeyHindiLanguageDownld()) {
            translatorHindi.translate(areaName)
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            view.setText(s);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            view.setText(e.toString());
                        }
                    });
        } else {
            DownloadConditions downloadConditions = new DownloadConditions.Builder()
                    .requireWifi()
                    .build();

            translatorHindi.downloadModelIfNeeded(downloadConditions)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            sessionManager.setKeyHindiLanguageDownld(true);

                            //add the text
                            translatorHindi.translate(areaName)
                                    .addOnSuccessListener(new OnSuccessListener<String>() {
                                        @Override
                                        public void onSuccess(String s) {
                                            view.setText(s);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            view.setText(e.toString());
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(RouteERiksha.this, "Unable to Download Hindi", Toast.LENGTH_SHORT).show();
                        }
                    });

            Toast.makeText(RouteERiksha.this, "Downloading Hindi Language", Toast.LENGTH_SHORT).show();


        }


    }

    @Override
    public void onRoutingCancelled() {
        Findroutes(allPoints);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Findroutes(allPoints);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        myLocation=KeyCon.strToLocation(sessionManager.getKeyCurrentLoc());




        //for speak location name
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                KeyCon.speakStr(marker.getTitle(),RouteERiksha.this);
                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                if (mapFullScreen) {
                    header.setVisibility(View.VISIBLE);
                    pickUpC.setVisibility(View.VISIBLE);
                    headingText.setVisibility(View.VISIBLE);

//                    map.setMinimumWidth(-1);
                    map.setMinimumHeight(350);


                    mapFullScreen = false;

                } else {



                    header.setVisibility(View.GONE);
                    pickUpC.setVisibility(View.GONE);
                    headingText.setVisibility(View.GONE);

//                    map.setMinimumWidth(-1);
                    map.setMinimumHeight(-1);


                    mapFullScreen = true;
                }


            }
        });

//        getMyLocation();
//
//        myLocation = strToLocation(sessionManager.getKeyCurrentLoc());



//        //Add Marker on current  position
//        MarkerOptions endMarker = new MarkerOptions();
//        endMarker.position(strToLatLng(sessionManager.getKeyCurrentLoc()));
//        endMarker.title("MyLocation");
//        mMap.addMarker(endMarker);
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
//                strToLatLng(sessionManager.getKeyCurrentLoc()), 20f);
//        mMap.animateCamera(cameraUpdate);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return;

//            com.google.android.gms.location.LocationRequest locationRequest=LocationRequest.create();


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





                        Toast.makeText(RouteERiksha.this, "Location found", Toast.LENGTH_SHORT).show();
                        LocationServices.getFusedLocationProviderClient(RouteERiksha.this).removeLocationUpdates(this);
                        int index = locationResult.getLocations().size() - 1;



                        //here is a looper for updated Route
                        //if current location is 100 m distance extra
                        //for first move
                        if(myLocation==null||myLocation.distanceTo(locationResult.getLocations().get(index))>100){


                            myLocation = locationResult.getLocations().get(index);




//                        mMap.clear();
                            //                //Add Marker on route ending position
                            MarkerOptions endMarker = new MarkerOptions();
                            endMarker.position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
                            endMarker.title("CurrentLocation").icon(KeyCon.bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_directions_car_24));
                            mMap.addMarker(endMarker);
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 20f);
                            mMap.animateCamera(cameraUpdate);




                            initiateAll();

                            //navigato current location
//                            setDefaultLocationWName();



                        }


                    } else {
                        Toast.makeText(RouteERiksha.this, "Not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }, Looper.getMainLooper());
//                LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest,new LocationCallback(){
//                @Override
//                public void onLocationResult(@NonNull LocationResult lr){
//                super.onLocationResult( lr);
//
//                LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(This);
//                },Looper.getMainLooper())

            LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
//                        myLocation = location;


//

                    }
                }
            });


//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//
//
//
//
//
//
//        //for finding the location
//        Geocoder geocoder=new Geocoder(this, Locale.getDefault());
//        try {
//            List addressList=geocoder.getFromLocationName("Atrauli",10);
//            if( addressList!=null&&addressList.size()>0){
//                Address address=(Address) addressList.get(0);
//
//                // Add a marker in Sydney and move the camera
//                LatLng atrauli = new LatLng(address.getLatitude(), address.getLongitude());
//                mMap.addMarker(new MarkerOptions().position(atrauli).title("Atrauli"));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(atrauli));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
////        AIzaSyB90S7ctS975iltlA5OAObp19JWdzDTTZg
//

        }
    }

    private void setDefaultLocationWName() {
        LatLng locat;

        for (int i = 0; i < locationAddrName.size(); i++) {
            MarkerOptions endMarker = new MarkerOptions();
            locat=KeyCon.strToLatLng(locationAddr.get(i));
            endMarker.position(locat);
            endMarker.title(locationAddrName.get(i)).icon(KeyCon.bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_location_on_24));
            mMap.addMarker(endMarker);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 20f);
            mMap.animateCamera(cameraUpdate);


        }

    }

//this will take the list of all points and return the searial list
//which has min shortest path

    private ArrayList<LatLng> calculateShortPath(ArrayList<LatLng> allPoints) {

        //the main list which will contain actual path
        ArrayList<LatLng> path = new ArrayList<>();

        //validation for min two points
        if (allPoints.size() < 2) {
            //return empty path
            return path;
        }


        //here we store all site distance
        ArrayList<ArrayList<Integer>> allSideDist = new ArrayList<>();


        //here we only find distance to alll point from all points
        int distance = 0;

        int i = 0;
        int size = allPoints.size();
        for (; i < size; i++) {

            ArrayList<Integer> oneDist = new ArrayList<>();

            for (int k = 0; k < size; k++) {
                if (i == k) {
                    oneDist.add(0);
                } else if (k < i) {
                    oneDist.add(allSideDist.get(k).get(i));
                } else {
                    Location startPo = new Location("locationA");
                    startPo.setLatitude(allPoints.get(i).latitude);
                    startPo.setLongitude(allPoints.get(i).longitude);

                    Location endPo = new Location("locationB");
                    endPo.setLatitude(allPoints.get(k).latitude);
                    endPo.setLongitude(allPoints.get(k).longitude);


                    distance = (int) startPo.distanceTo(endPo);

                    oneDist.add(distance);


                }
            }
            allSideDist.add(oneDist);
        }


        //now here we find the shortest path
        //By find jumping to the near short node
        int tamp = 0;

        int currentNode = 0;
        while (allPoints.size() > 0) {
            //this will return the index of min distance point
            path.add(allPoints.get(currentNode));

            //remove the element from the main list
            allPoints.remove(currentNode);

            //find the shortest element
            tamp = findshortVal(allSideDist.get(currentNode));


//            if()


            //remove the current node from allSideDist

            //re,remove perticular node from all dist

            //here we add 1 in condition bcz we have already deleted the row
            for (int b = 0; b < allPoints.size() + 1; b++) {
                allSideDist.get(b).remove(currentNode);
            }
            //here delete the whole list
            allSideDist.remove(currentNode);

            //if the element is stored in up part of the list then decrease the index by one
            if (tamp > currentNode) {
                tamp--;
            }


            //update the index to new shortest distance node
            currentNode = tamp;

        }

        return path;
    }

    //this will give the index of  shortest distance node
    private int findshortVal(ArrayList<Integer> list) {
        int minDist = -1;
        int minDistPoint = -1;


        //search only min two points
        if (list.size() < 2) {
            return -1;
        }


        //for default initiallization
        if (list.get(0) != 0) {
            minDistPoint = 0;
            minDist = list.get(0);
        } else {
            minDistPoint = 1;
            minDist = list.get(1);
        }

        //here we find
        for (int k = 0; k < list.size(); k++) {

            if (list.get(k) == 0) {
                continue;
            } else if (list.get(k) < minDist) {
                minDistPoint = k;
                minDist = list.get(k);
            }
        }

        return minDistPoint;
    }


    public void initiateAll() {

        //cler lll things
        pickUpC.removeAllViews();
        allPoints.clear();
        allOnlinePickUpPoints.clear();



        // adding current loc to route
        allPoints.add(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));


        //for offline data adding in showing time
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        data = sessionManager.showDataTravel();

        for (ArrayList<String> a : data) {
            if (a.get(4).equals(KeyCon.TransCon.Value.ValueTransport.TRAV_RUNNING)) {
                allPoints.add(KeyCon.strToLatLng(a.get(2)));
            }
        }






//.equalTo(sessionManager.getKeyRikshaid(), KeyCon.TransCon.ERikshaID)..equalTo(KeyCon.TransCon.Value.TRAV_RUNNING, KeyCon.TransCon.Status)
        //only take running orders for single userd,and also for online pickUpPoints
        FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaTranspCon).orderByChild(KeyCon.TransCon.ERikshaID).equalTo(sessionManager.getKeyRikshaid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {


                String add = "", status,passNo,id,token;
                if (dataSnapshot.exists()) {

                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        status = data.child(KeyCon.TransCon.Status).getValue(String.class);
                        if (status.equals(KeyCon.TransCon.Value.ValueTransport.TRAV_RUNNING)) {

//                        Toast.makeText(getApplicationContext(),add,Toast.LENGTH_SHORT).show();
                            add = String.valueOf(data.child(KeyCon.TransCon.DestLoc).getValue());
                            allPoints.add(strToLatLng(add));
//                        Toast.makeText(RouteERiksha.this, ""+add, Toast.LENGTH_SHORT).show();

                        }else if(status.equals(KeyCon.TransCon.Value.ValueTransport.TRAV_ACCEPT_DRIVER)||status.equals(KeyCon.TransCon.Value.ValueTransport.TRAV_DRIVER_REACHED)){
                            add = String.valueOf(data.child(KeyCon.TransCon.StartLoc).getValue());
                            passNo = String.valueOf(data.child(KeyCon.TransCon.TtlPerson).getValue());
                            token = String.valueOf(data.child(KeyCon.TransCon.OnlineUsrToken).getValue());
                            id = data.getKey();
                            allPoints.add(strToLatLng(add));

                            //this will used to check point is for pickUp or not
                            //and also for update location pickUp reminder
                            allOnlinePickUpPoints.add(strToLatLng(add));

                            //convert to loc name
                            add=setAreaName(KeyCon.strToLatLng(add));


                            //ddd view to the pickiup
                            addView(add, Integer.parseInt(passNo),id,token,status);
                        }
                    }


                    //calculate shoPath
                    allPoints = calculateShortPath(allPoints);

                    Findroutes(allPoints);

                } else {
                    Toast.makeText(RouteERiksha.this, "Online Passenger Not Found.", Toast.LENGTH_SHORT).show();
                }


//                nextSt.setText("Null");
//                addView("khat", 2);

            }
        });




    }

    private Location strToLocation(String str) {
        Location des = new Location(LocationManager.GPS_PROVIDER);

        String[] latlong = new String[1];
        if (str != null) {
            latlong = str.split(",");
            des.setLatitude(Double.parseDouble(latlong[0]));
            des.setLongitude(Double.parseDouble(latlong[1]));
        }
        return des;
    }


    private LatLng strToLatLng(String str) {

        String[] latlong = new String[1];
        if (str != null) {
            latlong = str.split(",");
        }

        return new LatLng(Double.parseDouble(latlong[0]), Double.parseDouble(latlong[1]));

    }

    public void addView(String stop, int pass,String id,String token,String status) {
        final View view = View.inflate(this, R.layout.pick_up_person, null);
//\\Declare the all  Views
//

        TextView mohalla,ttlPerson;
        Button reached,pickup;


//\\Initiallizing all ids
//
        reached = view.findViewById(R.id.reached);
        pickup = view.findViewById(R.id.pickUp);

        mohalla = view.findViewById(R.id.mohalla);
        ttlPerson = view.findViewById(R.id.ttlPerson);

        if (status.equals(KeyCon.TransCon.Value.ValueTransport.TRAV_DRIVER_REACHED)){
            reached.setVisibility(View.GONE);
            pickup.setVisibility(View.VISIBLE);
        }

        mohalla.setText(stop);
        ttlPerson.setText(pass + " passenger");

        mohalla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyCon.speakTVLP(mohalla,getApplicationContext());
            }
        });
        ttlPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyCon.speakTVLP(ttlPerson,getApplicationContext());
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),InsideOrder.class);
                intent.setData(Uri.parse(id));
                startActivity(intent);
            }
        });

        reached.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send notifiction to be reched to user
                FcmNotificationsSender fcmNotificationsSender=new FcmNotificationsSender(token,"Tirri","Driver Reached",getApplicationContext(),RouteERiksha.this);
                fcmNotificationsSender.SendNotifications();


                //updte dat

                FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaTranspCon).child(id).child(KeyCon.TransCon.Status).setValue(KeyCon.TransCon.Value.ValueTransport.TRAV_DRIVER_REACHED).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(),"Set To Reached",Toast.LENGTH_SHORT).show();
                    }
                });

                reached.setVisibility(View.GONE);
                pickup.setVisibility(View.VISIBLE);

            }
        });

        //here we pick up user
          pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updte dat

                FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaTranspCon).child(id).child(KeyCon.TransCon.Status).setValue(KeyCon.TransCon.Value.ValueTransport.TRAV_RUNNING).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(),"User Acuired Sucessfully ",Toast.LENGTH_SHORT).show();
                    }
                });

                reached.setVisibility(View.GONE);
                pickup.setVisibility(View.GONE);
                pickUpC.removeView(view);

            }
        });


        pickUpC.addView(view);
    }


    public void gotoBack(View view) {
        onBackPressed();
    }



    public void addNewLoc(View view){

        final AlertDialog.Builder alert = new AlertDialog.Builder(RouteERiksha.this);
        View mview = getLayoutInflater().inflate(R.layout.add_new_loc, null);


        Button uploadV = mview.findViewById(R.id.updload);
        Button cancel = mview.findViewById(R.id.cancel);
        EditText iputV=mview.findViewById(R.id.inputView);



        alert.setView(mview);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);


        uploadV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                iputV.getText().toString();

                //upload the name
                Calendar calendar = Calendar.getInstance();
                String currentDate = DateFormat.getDateInstance().format(calendar.getTime());


                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(KeyCon.NewLocToAdd.Date,currentDate);
                hashMap.put(KeyCon.NewLocToAdd.ERikshaId,sessionManager.getKeyRikshaid());
                hashMap.put(KeyCon.NewLocToAdd.LocationName,iputV.getText().toString());
                hashMap.put(KeyCon.NewLocToAdd.LocationAddr,KeyCon.locToString(myLocation));


                FirebaseDatabase.getInstance().getReference().child(KeyCon.NewLocToAddCon).child(String.valueOf(System.currentTimeMillis())).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(RouteERiksha.this, "Location will be added soon.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
//        alertDialog.dismiss();
        alertDialog.show();

    }





    private void initiallOnlineReq(){

        //jhere use single value event listener


        SessionManager sessionManager = new SessionManager(this);
//.equalTo(sessionManager.getKeyRikshaid(), KeyCon.TransCon.ERikshaID)..equalTo(KeyCon.TransCon.Value.TRAV_RUNNING, KeyCon.TransCon.Status)
        //only take running orders for single userd
        FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaTranspCon).orderByChild(KeyCon.TransCon.Status).equalTo(KeyCon.TransCon.Value.ValueTransport.TRAV_Initiated).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {


                String add = "", reqEriksha;
                if (dataSnapshot.exists()) {

//                    Toast.makeText(RouteERiksha.this, "Travel is available", Toast.LENGTH_SHORT).show();
                    String startLoc,destLoc,id="",token;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
//                        Toast.makeText(RouteERiksha.this, "Travel is available"+data.getKey(), Toast.LENGTH_SHORT).show();

                         reqEriksha=data.child(KeyCon.TransCon.RequERiksha).getValue(String.class);
//                         if(checkRikshaIdAvai(reqEriksha)){
                             //pointote
                             startLoc=data.child(KeyCon.TransCon.StartLoc).getValue(String.class);
                             destLoc=data.child(KeyCon.TransCon.DestLoc).getValue(String.class);
                             token=data.child(KeyCon.TransCon.OnlineUsrToken).getValue(String.class);
                             id=data.getKey();


                             final View view = View.inflate(getApplicationContext(), R.layout.req_eriksha, null);

                             TextView startLocT=view.findViewById(R.id.startLoc);
                             TextView destLocT=view.findViewById(R.id.endLoc);
                             TextView ttlDistT=view.findViewById(R.id.travelDist);
                        startLocT.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                KeyCon.speakTVLP(startLocT,getApplicationContext());
                            }
                        });

                        destLocT.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                KeyCon.speakTVLP(destLocT,getApplicationContext());
                            }
                        });

                        ttlDistT.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                KeyCon.speakTVLP(ttlDistT,getApplicationContext());
                            }
                        });


                             startLocT.setText(sessionManager.getNearLoctionName(KeyCon.strToLocation(startLoc)));
                             destLocT.setText(sessionManager.getNearLoctionName(KeyCon.strToLocation(destLoc)));
                             ttlDistT.setText(String.valueOf(KeyCon.strToLocation(destLoc).distanceTo(KeyCon.strToLocation(startLoc))));


                             //for setting up btn
                             Button no=view.findViewById(R.id.decline);
                             Button ok=view.findViewById(R.id.accept);

                        String finalId1 = id;
                        view.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View v) {
                                     Intent intent=new Intent(getApplicationContext(),InsideOrder.class);
                                     intent.setData(Uri.parse(finalId1));
                                     startActivity(intent);
                                 }
                             });

                             no.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View view) {
                                     con.removeView(view);
                                 }
                             });
                             String finalId = id;
                             String finalToken = token;
                             ok.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View view) {
                                     con.removeView(view);
                                     //check if avialable then registere
                                     FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaTranspCon).child(finalId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                         @Override
                                         public void onSuccess(DataSnapshot dataSnapshot) {


                                             //check for  no booked seat
                                             if(dataSnapshot.exists()&&dataSnapshot!=null&&dataSnapshot.child(KeyCon.TransCon.ERikshaID).getValue(String.class).equals(Travel.ID_NULL)){
                                                 //here we set the id
                                                 HashMap<String,Object> map=new HashMap<>();
                                                 map.put(KeyCon.TransCon.ERikshaID,sessionManager.getKeyRikshaid());
                                                 map.put(KeyCon.TransCon.Status,KeyCon.TransCon.Value.ValueTransport.TRAV_ACCEPT_DRIVER);

                                                 FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaTranspCon).child(finalId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                     @Override
                                                     public void onSuccess(Void unused) {

                                                         //send notifictrion
                                                         //send notifiction to be reched to user
                                                         FcmNotificationsSender fcmNotificationsSender=new FcmNotificationsSender(finalToken,"Tirri","Driver Found",getApplicationContext(),RouteERiksha.this);
                                                         fcmNotificationsSender.SendNotifications();





                                                         Toast.makeText(getApplicationContext(),"Req Accepted",Toast.LENGTH_SHORT).show();

                                                         //reinitillie reuests
                                                         initiateAll();
                                                     }
                                                 });

                                             }
                                         }
                                     }).addOnFailureListener(new OnFailureListener() {
                                         @Override
                                         public void onFailure(@NonNull Exception e) {
                                             Toast.makeText(getApplicationContext(),"You are late.Pre booked",Toast.LENGTH_SHORT).show();
                                         }
                                     });
                                 }
                             });
                             //set teh reminder for this page


                             con.addView(view);

                             //store location to got to the loc
                         }


                }

            }
        });


    }


    private Boolean checkRikshaIdAvai(String data){
        ArrayList<String> list=new ArrayList<>();
        list.addAll(Arrays.asList(data.split("<>")));
        for(String a:list){
            if(a.equals(sessionManager.getKeyRikshaid()))
                return true;
        }
        return false;
    }
}


