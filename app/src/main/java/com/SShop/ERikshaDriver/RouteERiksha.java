package com.SShop.ERikshaDriver;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RouteERiksha extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, RoutingListener {


    TextToSpeech tts;

    private void speakStr(String str){

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int res = 0;
                    //setting up text language
                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    if (sessionManager.getKeyAppLng().equals(KeyCon.ERikshaDriver.Value.LNG_ENG)) {
                        res = tts.setLanguage(Locale.ENGLISH);
                    } else {
                        res = tts.setLanguage(Locale.forLanguageTag("hin"));

                    }

//                    int result=tts.setLanguage(Locale.)
                    if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getApplicationContext(), "Language Not supp", Toast.LENGTH_SHORT).show();
                    } else {
                        tts.setPitch(0.4f);
                        tts.setSpeechRate(0.8f);
                        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }
        });

    }
    //for textView on longPress it activated
    private void speakTVLP(TextView view) {

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int res = 0;
                    //setting up text language
                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    if (sessionManager.getKeyAppLng().equals(KeyCon.ERikshaDriver.Value.LNG_ENG)) {
                        res = tts.setLanguage(Locale.ENGLISH);
                    } else {
                        res = tts.setLanguage(Locale.forLanguageTag("hin"));

                    }

//                    int result=tts.setLanguage(Locale.)
                    if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getApplicationContext(), "Language Not supp", Toast.LENGTH_SHORT).show();
                    } else {
                        tts.setPitch(0.4f);
                        tts.setSpeechRate(0.8f);
                        tts.speak(view.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        tts.stop();
//        tts.shutdown();
    }


    //this will contain all the routes
    //1 one is mylocation
    ArrayList<LatLng> allPoints = new ArrayList<com.google.android.gms.maps.model.LatLng>();

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

    ArrayList<String> locationAddrName;
    ArrayList<String> locationAddr;

    TextView nextStH;

    com.google.android.gms.location.LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_eriksha);


//        locationRequest=LocationRequest;
//        locationRequest.set
        sessionManager = new SessionManager(this);


        //get data from session of default locaitons and keys
        String tamp = sessionManager.getKeyAllKeyLocation();

        ArrayList<ArrayList<String>> data = new ArrayList<>();

        data = KeyCon.convertStrToDefaultLoc(tamp);
        locationAddrName = data.get(0);
        locationAddr = data.get(1);


//        binding = ActivityMapsBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

        //request location permission.
        requestPermision();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


//\\Initiallizing all ids

        nextStH = findViewById(R.id.nextStH);
        nextSt = findViewById(R.id.nextSt);
        pickUpC = findViewById(R.id.pickUpC);

        nextSt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakStr(getString(R.string.nxtStop)+nextSt.getText().toString());
            }
        });
        nextStH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakStr(getString(R.string.nxtStop)+nextSt.getText().toString());
            }
        });



        nextStH.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(nextStH);
                return false;
            }
        });
        nextSt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(nextSt);
                return false;
            }
        });


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
//                endMarker.title("Destinationsdf").icon(KeyCon.bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_arrow));
//                mMap.addMarker(endMarker);
//

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
        if (path.isEmpty()) {
            Toast.makeText(RouteERiksha.this, "Unable to get location", Toast.LENGTH_LONG).show();
        } else {

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.WALKING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(path)
                    .key(getResources().getString(R.string.key))  //also define your api key here.
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
//    Findroutes(start,end);
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
        setAreaName(allPoints.get(1));

        //Add Marker on current  position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(allPoints.get(0));
        endMarker.title("MyLocation").icon(KeyCon.bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_start));
        mMap.addMarker(endMarker);

        int i = 1;
        for (; i + 1 < allPoints.size(); i++) {
            //Add Marker on route at every station position
            MarkerOptions startMarker = new MarkerOptions();
            startMarker.position(allPoints.get(i));
            startMarker.title("Stopage").icon(KeyCon.bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_outlined_flag_24));
            mMap.addMarker(startMarker);

        }

        //Add Marker on route at end station position
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(allPoints.get(i));
        startMarker.title("Stopage").icon(KeyCon.bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_flag_final));
        mMap.addMarker(startMarker);

        //focus camera to the current loca

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                allPoints.get(0), 20f);
        mMap.animateCamera(cameraUpdate);
    }


    //this will set the area name
    //according to the nearest predefined locations
    private void setAreaName(LatLng latLng) {

        float distnaceMin = 1000000;
        String areaName = "Null";

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


//        nextSt.setText(areaName);
        setTextAllLng(nextSt, areaName);
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

        getMyLocation();

        myLocation = strToLocation(sessionManager.getKeyCurrentLoc());

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
                        LocationServices.getFusedLocationProviderClient(RouteERiksha.this).removeLocationUpdates(this);
                        int index = locationResult.getLocations().size() - 1;
                        myLocation=locationResult.getLocations().get(index);

                        mMap.clear();
                        //                //Add Marker on route ending position
                        MarkerOptions endMarker = new MarkerOptions();
                        endMarker.position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
                        endMarker.title("Destinationsdf").icon(KeyCon.bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_directions_car_24));
                        mMap.addMarker(endMarker);
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                                new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 20f);
                        mMap.animateCamera(cameraUpdate);

                        initiateAll(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
                    }else{
                        Toast.makeText(RouteERiksha.this,"Not found",Toast.LENGTH_SHORT).show();
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
                        myLocation = location;


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

//this will take the list of all points and return the searial list
//which has min shortest path

        private ArrayList<LatLng> calculateShortPath (ArrayList < LatLng > allPoints) {

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
        private int findshortVal (ArrayList < Integer > list) {
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


        public void initiateAll (LatLng mylocation){

            allPoints.add(mylocation);

            SessionManager sessionManager = new SessionManager(this);
//.equalTo(sessionManager.getKeyRikshaid(), KeyCon.TransCon.ERikshaID)..equalTo(KeyCon.TransCon.Value.TRAV_RUNNING, KeyCon.TransCon.Status)
            //only take running orders for single userd
            FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaTranspCon).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {


                    String add = "", status;
                    if (dataSnapshot.exists()) {

                        for (DataSnapshot data : dataSnapshot.getChildren()) {

                            status = data.child(KeyCon.TransCon.Status).getValue(String.class);
                            if (status.equals(KeyCon.TransCon.Value.TRAV_RUNNING)) {

//                        Toast.makeText(getApplicationContext(),add,Toast.LENGTH_SHORT).show();
                                add = String.valueOf(data.child(KeyCon.TransCon.DestLoc).getValue());
                                allPoints.add(strToLatLng(add));
//                        Toast.makeText(RouteERiksha.this, ""+add, Toast.LENGTH_SHORT).show();

                            }
                        }


                        //calculate shoPath
                        allPoints = calculateShortPath(allPoints);

                        Findroutes(allPoints);

                    } else {
                        Toast.makeText(RouteERiksha.this, "Passenger Not Found.", Toast.LENGTH_SHORT).show();
                    }


                    nextSt.setText("Null");
//                addView("khat", 2);

                }
            });


        }

        private Location strToLocation (String str){
            Location des = new Location(LocationManager.GPS_PROVIDER);

            String[] latlong = new String[1];
            if (str != null) {
                latlong = str.split(",");
                des.setLatitude(Double.parseDouble(latlong[0]));
                des.setLongitude(Double.parseDouble(latlong[1]));
            }
            return des;
        }


        private LatLng strToLatLng (String str){

            String[] latlong = new String[1];
            if (str != null) {
                latlong = str.split(",");
            }

            return new LatLng(Double.parseDouble(latlong[0]), Double.parseDouble(latlong[1]));

        }

        public void addView (String stop,int pass){
            final View view = View.inflate(this, R.layout.pick_up_person, null);
//\\Declare the all  Views
//

            TextView mohalla;


            TextView ttlPerson;


//\\Initiallizing all ids
//
            mohalla = view.findViewById(R.id.mohalla);
            ttlPerson = view.findViewById(R.id.ttlPerson);


            mohalla.setText(stop);
            ttlPerson.setText(pass + " passenger");

            pickUpC.addView(view);
        }


        public void gotoBack (View view){
            onBackPressed();
        }
    }


