package com.SShop.ERikshaDriver;


//import android.support.v7.app.AppCompatActivity;

import static androidx.core.location.LocationManagerCompat.getCurrentLocation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.SShop.ERikshaDriver.databinding.ActivityMapsBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class onePerson extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, RoutingListener {
//Declare the all  Views


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
                        res = tts.setLanguage(Locale.forLanguageTag(KeyCon.ERikshaDriver.Value.LNG_HIND));

                    }

//                    int result=tts.setLanguage(Locale.)
                    if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getApplicationContext(), "Language Not supp", Toast.LENGTH_SHORT).show();
                    } else {
                        tts.setPitch(0.2f);
                        tts.setSpeechRate(0.6f);
                        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }
        });

    }

    //for textView on longPress it activated
    private void speakTVLP(TextView view){

        tts=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i==TextToSpeech.SUCCESS){
                    int res=0;
                    //setting up text language
                    SessionManager sessionManager=new SessionManager(getApplicationContext());
                    if(sessionManager.getKeyAppLng().equals(KeyCon.ERikshaDriver.Value.LNG_ENG)){
                        res=tts.setLanguage(Locale.ENGLISH);
                    }else{
                        res=tts.setLanguage(Locale.forLanguageTag("hin"));

                    }

//                    int result=tts.setLanguage(Locale.)
                    if(res==TextToSpeech.LANG_MISSING_DATA|| res==TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast.makeText(getApplicationContext(),"Language Not supp",Toast.LENGTH_SHORT).show();
                    }else{
                        tts.setPitch(0.2f);
                        tts.setSpeechRate(0.6f);
                        tts.speak(view.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
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




    int personAddF = 0;
    LatLng destinationF;

    EditText inputPl;


    TextView oneP, ttlPerson, ttlPrice, distance;


    TextView onePriceM;

    String place, latlang;


    private GoogleMap map;
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


    SessionManager sessionManager;


    ArrayList<String> locationList = new ArrayList<>();
    ArrayList<String> locationAddrList = new ArrayList<>();

    //place attributes
    Spinner locationSpin;

    LocationRequest locationRequest;

    LinearLayout lastPriceC;

    TextView startLocV;
    Spinner locationSpinStrt;
    LinearLayout locationSpinStrtCon;

    //this will be stored for security purpose
    Location fetched,selected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_person);
        sessionManager = new SessionManager(this);
        locationSpin = findViewById(R.id.locationSpin);

        locationList.add("Select location");
        locationAddrList.add("abc");
        //get data from session of default locaitons and keys
        String tamp = sessionManager.getKeyAllKeyLocation();

        ArrayList<ArrayList<String>> data = new ArrayList<>();

//        Toast.makeText(this, ""+tamp, Toast.LENGTH_SHORT).show();
        data = KeyCon.convertStrToDefaultLoc(tamp);
        locationList = data.get(0);
        locationAddrList = data.get(1);


        

        startLocV=findViewById(R.id.startLoc);

        locationSpinStrt=findViewById(R.id.locationSpinStrt);
        locationSpinStrtCon=findViewById(R.id.locationSpinStrtCon);

        startLocV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocV.setVisibility(View.GONE);
                locationSpinStrtCon.setVisibility(View.VISIBLE);
//                locationSpinStrtCon.
            }
        });

        startLocV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(startLocV);
                return false;
            }
        });
        
        
        //for setting up start loc
        ArrayAdapter<String> locationAdapS = new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_item, locationList);
        readyCurrLngList(locationList,locationAdapS);

        locationAdapS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //setting userAdapter
        locationSpinStrt.setAdapter(locationAdapS);

//        locationAdap.notifyDataSetChanged();
        //this is for Season
        locationSpinStrt.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                speakStr(locationList.get(i));
                return false;
            }
        });
        locationSpinStrt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Select location")) {

//                    mMap.clear();

                } else {

//                    setTextAllLng(inputPl,locationList.get(position));
                    startLocV.setText(locationList.get(position));

                    fetched=myLocation;
                    myLocation= strToLocation(locationAddrList.get(position));
                    selected=myLocation;

                    //add marker
                    //Add Marker on route ending position
                    MarkerOptions sMarker = new MarkerOptions();
                    sMarker.position(start);
                    sMarker.title("Start");
                    map.addMarker(sMarker);


                    Findroutes(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), end);

                }

                startLocV.setVisibility(View.VISIBLE);
                locationSpinStrtCon.setVisibility(View.GONE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });






        lastPriceC=findViewById(R.id.lastPriceC);

 
  
//        Toast.makeText(this,locationList+"aa"+locationAddrList,Toast.LENGTH_SHORT).show();


        myLocation=KeyCon.strToLocation(sessionManager.getKeyCurrentLoc());
        //here we add click lisstener
        // for country
        ArrayAdapter<String> locationAdap = new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_item, locationList);
        readyCurrLngList(locationList,locationAdap);

        locationAdap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //setting userAdapter
        locationSpin.setAdapter(locationAdap);

//        locationAdap.notifyDataSetChanged();
        //this is for Season
        locationSpin.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                speakStr(locationList.get(i));

                return false;
            }
        });
        locationSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Select location")) {

//                    mMap.clear();

                } else {

//                    setTextAllLng(inputPl,locationList.get(position));
                    inputPl.setText(locationList.get(position));
                    end = strToLatLng(locationAddrList.get(position));
                    //add marker
                    //Add Marker on route ending position
                    MarkerOptions endMarker = new MarkerOptions();
                    endMarker.position(end);
                    endMarker.title("Destination");
                    map.addMarker(endMarker);


                    Findroutes(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), end);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//        FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaLoc).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
//            @Override
//            public void onSuccess(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot data : dataSnapshot.getChildren()) {
//                        locationList.add(data.getKey());
//                        locationAddrList.add(data.getValue(String.class));
////                        locationSpin.notify();
//                        locationAdap.notifyDataSetChanged();
//                    }
//
//                } else {
//                    Toast.makeText(getApplicationContext(), "Nothing found", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_SHORT).show();
//            }
//        });


//        binding = ActivityMapsBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(onePerson.this);

//        map=onePerson.
//        map=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).gM;

//        getCurrentLocation(,)


        //request location permission.
        requestPermision();

//\\Initiallizing all ids

        inputPl = findViewById(R.id.inputPl);
        oneP = findViewById(R.id.oneP);
        onePriceM = findViewById(R.id.onePriceM);
        ttlPerson = findViewById(R.id.ttlPerson);
        ttlPrice = findViewById(R.id.ttlPrice);
        distance = findViewById(R.id.distance);


        ///for change language
        selectLocT = findViewById(R.id.selectLocH);
        onePersonT = findViewById(R.id.onePersonT);
        ttlDisT = findViewById(R.id.ttlDistT);

        btn = findViewById(R.id.searchBtn);

        inputPl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(inputPl);
                return false;
            }
        });
        ttlDisT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakStr(getString(R.string.ttlDis)+distance.getText().toString());
            }
        });
        distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakStr(getString(R.string.ttlDis)+distance.getText().toString());
            }
        });
        lastPriceC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakStr(getString(R.string.ttlPrice)+ttlPrice.getText().toString());
            }
        });
        onePersonT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakStr(getString(R.string.onePerPrice)+oneP.getText().toString());
            }
        });
        oneP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakStr(getString(R.string.onePerPrice)+oneP.getText().toString());
            }
        });
        oneP.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(oneP);

                return false;
            }
        });
  onePriceM.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(onePriceM);
                return false;
            }
        });
  ttlPerson.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(ttlPerson);
                return false;
            }
        });
  distance.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(distance);
                return false;
            }
        });
  ttlPrice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(ttlPrice);
                return false;
            }
        });
  selectLocT.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(selectLocT);
                return false;
            }
        });
  onePersonT.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(onePersonT);
                return false;
            }
        });
  ttlDisT.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(ttlDisT);
                return false;
            }
        });
  btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(btn);
                return false;
            }
        });

//        SessionManager sessionManager=new SessionManager(this);
        if (sessionManager.getKeyAppLng() == KeyCon.ERikshaDriver.Value.LNG_HIND) {
            changeLanguage(KeyCon.ERikshaDriver.Value.LNG_HIND);
        } else {
            changeLanguage(KeyCon.ERikshaDriver.Value.LNG_ENG);
        }
    }


    //this is  for creating radylist
    private void readyCurrLngList(ArrayList<String> areaName,ArrayAdapter<String> adapter) {

        ArrayList<String> list = new ArrayList<>();


        TranslatorOptions translatorOptionsHindi = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.HINDI)
                .build();

        Translator translatorHindi = Translation.getClient(translatorOptionsHindi);


        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.getKeyAppLng().equals(KeyCon.ERikshaDriver.Value.LNG_ENG)) {

            //nothing to do
        } else if (sessionManager.getKeyHindiLanguageDownld()) {

            for (String a : areaName) {
                translatorHindi.translate(a)
                        .addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                list.add(s);
                                if (list.size() == areaName.size()) {
                                    //all string converted succ

                                    locationList.clear();
                                    locationList=new ArrayList<>();

                                    locationList=list;
                                    adapter.notifyDataSetChanged();

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(onePerson.this, "Error Occured while translating", Toast.LENGTH_SHORT).show();
//                                break;
                            }
                        });
            }


        } else {
            DownloadConditions downloadConditions = new DownloadConditions.Builder()
                    .requireWifi()
                    .build();

            translatorHindi.downloadModelIfNeeded(downloadConditions)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            sessionManager.setKeyHindiLanguageDownld(true);
                            for (String a : areaName) {
                                translatorHindi.translate(a)
                                        .addOnSuccessListener(new OnSuccessListener<String>() {
                                            @Override
                                            public void onSuccess(String s) {
                                                list.add(s);
                                                if (list.size() == areaName.size()) {
                                                    //all string converted succ
                                                    locationList.clear();
                                                    locationList=new ArrayList<>();

                                                    locationList=list;
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(onePerson.this, "Error Occured while translating", Toast.LENGTH_SHORT).show();
//                                break;
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(onePerson.this, "Unable to Download Hindi", Toast.LENGTH_SHORT).show();
                        }
                    });

            Toast.makeText(onePerson.this, "Downloading Hindi Language", Toast.LENGTH_SHORT).show();


        }


//        return list;
    }



//this is for convert text and set it

    private void setTextAllLng(EditText view, String areaName) {


        TranslatorOptions translatorOptionsHindi = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.HINDI)
                .build();


        Translator translatorHindi = Translation.getClient(translatorOptionsHindi);


        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.getKeyAppLng().equals(KeyCon.ERikshaDriver.Value.LNG_ENG)) {
            //for english language direct set text
            view.setText(areaName);
        } else if (sessionManager.getKeyHindiLanguageDownld()) {
            translatorHindi.translate(areaName)
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
//                            view.se
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

                            Toast.makeText(onePerson.this, "Unable to Download Hindi", Toast.LENGTH_SHORT).show();
                        }
                    });

            Toast.makeText(onePerson.this, "Downloading Hindi Language", Toast.LENGTH_SHORT).show();


        }


    }


    TextView selectLocT, onePersonT, ttlDisT;
    Button btn;

    private void changeLanguage(String langCode) {
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
//        Locale locale=new Locale(langCode);
//        Locale.setDefault(locale);
        configuration.locale = new Locale(langCode);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        onConfigurationChanged(configuration);
//        Toast.makeText(this, "lang change Init", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        selectLocT.setText(R.string.seleLoc);
        onePersonT.setText(R.string.onePerPrice);
        ttlDisT.setText(R.string.ttlDis);
        btn.setText(R.string.search);
//        Toast.makeText(this, "lang change", Toast.LENGTH_SHORT).show();

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
        map.setMyLocationEnabled(true);
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

//                myLocation = location;
//
//                LatLng ltlng = new LatLng(location.getLatitude(), location.getLongitude());
////                start=ltlng;
//
//                sessionManager.setKeyCurrentLoc(location.getLatitude()+","+location.getLongitude());
//
//
//                if(i==0){
//                    Toast.makeText(getApplicationContext(), "Map is readyy", Toast.LENGTH_SHORT).show();
//
//                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
//                            ltlng, 20f);
//                    map.animateCamera(cameraUpdate);
//
//                    i++;
//                }


//                //Add Marker on route ending position
//                MarkerOptions endMarker = new MarkerOptions();
//                endMarker.position(ltlng);
//                endMarker.title("Destinationsdf");
//                map.addMarker(endMarker);
//
//                Toast.makeText(getApplicationContext(), "Location added", Toast.LENGTH_SHORT).show();
//


            }
        });

        //get destination location when user click on map
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {


                map.clear();

//                start route finding
                Findroutes(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), latLng);
            }
        });

    }

    // function to find Routes.
    public void Findroutes(LatLng Start, LatLng End) {
        if (Start == null || End == null) {
            Toast.makeText(onePerson.this, "Unable to get location", Toast.LENGTH_LONG).show();
        } else {

            start = Start;
            end = End;
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.WALKING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
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
        Toast.makeText(onePerson.this, "Finding Route...", Toast.LENGTH_LONG).show();
    }

    //If Route finding success..
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        map.clear();


        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if (polylines != null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng = null;
        LatLng polylineEndLatLng = null;


        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i < route.size(); i++) {

            if (i == shortestRouteIndex) {
                polyOptions.color(getResources().getColor(R.color.orange));
                polyOptions.width(10);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());

                Polyline polyline = map.addPolyline(polyOptions);
                polylineStartLatLng = polyline.getPoints().get(0);
                int k = polyline.getPoints().size();
                polylineEndLatLng = polyline.getPoints().get(k - 1);
                polylines.add(polyline);

            } else {

            }

        }

        //Add Marker on route starting position
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(polylineStartLatLng);
        startMarker.title("My Location").icon(KeyCon.bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_start));
        map.addMarker(startMarker);

        //Add Marker on route ending position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
        endMarker.title("Destination").icon(KeyCon.bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_flag_final));
        map.addMarker(endMarker);


        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                polylineStartLatLng, 20f);
        map.animateCamera(cameraUpdate);


        destinationF = new LatLng(polylineEndLatLng.latitude, polylineEndLatLng.longitude);
        Location des = new Location(LocationManager.GPS_PROVIDER);
        des.setLatitude(destinationF.latitude);
        des.setLongitude(destinationF.longitude);

        int distM = (int) myLocation.distanceTo(des);
        showData(distM * sessionManager.getKeyOneKmPrice(), personAddF, String.valueOf(distM));

        speakStr(getString(R.string.ttlPrice)+distM*sessionManager.getKeyOneKmPrice()*personAddF/1000);
//        speakStr(getString(R.string.ttlPrice)+);
    }

    @Override
    public void onRoutingCancelled() {
        Findroutes(start, end);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Findroutes(start, end);

    }


    public void showData(int price, int ttlPers, String ttlDistance) {

        oneP.setText(String.valueOf(price / 1000));
        onePriceM.setText(String.valueOf(price / 1000));
        ttlPerson.setText(String.valueOf(ttlPers));
        ttlPrice.setText(String.valueOf((price / 1000) * ttlPers));
        distance.setText(ttlDistance + " m");


    }


    @Override
    protected void onStart() {

        Intent intent = getIntent();
        personAddF = Integer.parseInt(String.valueOf(intent.getData()));

        super.onStart();
    }

    public void cancelRide(View view) {
        onBackPressed();
    }

    public void AddPerson(View view) {


        //add person to the Database


        //this is for if the selected and fetched loc is different
        if(selected!=null&&!selected.equals("")){
            HashMap<String,String> dataChange=new HashMap<>();
            dataChange.put("rikshaId",sessionManager.getKeyRikshaid());
            dataChange.put("fetched", String.valueOf(fetched));
            dataChange.put("selected", String.valueOf(selected));

            FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaDriverChgLoc).child(String.valueOf(System.currentTimeMillis())).setValue(dataChange);
        }


        //getting time
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());


        SessionManager sessionManager = new SessionManager(this);

        Travel travel = new Travel(sessionManager.getKeyRikshaid(),
                Travel.CUS_OFFLINE,
                String.valueOf(myLocation.getLatitude()) + "," + String.valueOf(myLocation.getLongitude()),
                String.valueOf(destinationF.latitude) + "," + String.valueOf(destinationF.longitude),
                String.valueOf(personAddF),
                KeyCon.TransCon.Value.TRAV_RUNNING,
                currentDate,
                "",
                "",
                "",
                "1.0");

//        Toast.makeText(this, "--"+travel.getPrice(), Toast.LENGTH_SHORT).show();
        String id = String.valueOf(System.currentTimeMillis());

        FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaTranspCon).child(id).setValue(travel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(onePerson.this, "User Acquired Succesfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(onePerson.this, "Please try Again", Toast.LENGTH_SHORT).show();

            }
        });


        //goto home
        onBackPressed();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        map = googleMap;


        getMyLocation();

//        //setting up current location from session
//        myLocation = strToLocation(sessionManager.getKeyCurrentLoc());
//
//
//        //Add Marker on current  position
//        MarkerOptions endMarker = new MarkerOptions();
//        endMarker.position(strToLatLng(sessionManager.getKeyCurrentLoc()));
//        endMarker.title("MyLocation").icon(KeyCon.bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_start));
//        map.addMarker(endMarker);
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
//                strToLatLng(sessionManager.getKeyCurrentLoc()), 20f);
//        map.animateCamera(cameraUpdate);

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
                    LocationServices.getFusedLocationProviderClient(onePerson.this).removeLocationUpdates(this);
                    int index = locationResult.getLocations().size() - 1;
                    myLocation=locationResult.getLocations().get(index);

                    //here we get near by stop and set it to mylocation and text also


                    setAreaNameStrt(new LatLng( myLocation.getLatitude(),myLocation.getLongitude()));


                    map.clear();
                    //                //Add Marker on route ending position
                    MarkerOptions endMarker = new MarkerOptions();
                    endMarker.position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
                    endMarker.title("Destinationsdf").icon(KeyCon.bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_directions_car_24));
                    map.addMarker(endMarker);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                            new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 20f);
                    map.animateCamera(cameraUpdate);

//                    myLocation=location;


                    //                //Add Marker on route ending position
//                    MarkerOptions endMarker = new MarkerOptions();
//                    endMarker.position(new LatLng(location.getLatitude(),location.getLongitude()));
//                    endMarker.title("Destinationsdf").icon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_arrow));
//
//
//                    SessionManager sessionManager=new SessionManager(getApplicationContext());
//                    sessionManager.setKeyCurrentLoc(myLocation.getLatitude()+","+myLocation.getLongitude());
//
//                    startActivity(new Intent(SplashScreen.this,HomeActivity.class));
//                    finish();
                }else{
                    Toast.makeText(onePerson.this,"Not found",Toast.LENGTH_SHORT).show();
                }
            }
        }, Looper.getMainLooper());


//        LocationServices.getFusedLocationProviderClient(this).ge
        LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
//                    myLocation = location;
//
//
//                    map.clear();
//                    //                //Add Marker on route ending position
//                    MarkerOptions endMarker = new MarkerOptions();
//                    endMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
//                    endMarker.title("Destinationsdf").icon(KeyCon.bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_directions_car_24));
//                    map.addMarker(endMarker);
//                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
//                            new LatLng(location.getLatitude(), location.getLongitude()), 20f);
//                    map.animateCamera(cameraUpdate);
//

                }
            }
        });


    }

    private LatLng strToLatLng(String str) {
        String[] latlong = new String[1];
        if (str != null) {
            latlong = str.split(",");
        }

        return new LatLng(Double.parseDouble(latlong[0]), Double.parseDouble(latlong[1]));

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

    public void searchTxt(View view) {
        String txt = inputPl.getEditableText().toString();
        txt += " Atrauli";

        List<Address> addressList = null;

        Geocoder geocoder = new Geocoder(this);
        try {
            addressList = geocoder.getFromLocationName(txt, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = addressList.get(0);
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        Findroutes(start, latLng);

        //mark location
    }


    //for taking input from voice
    public void getData(View view) {
        //here we for now we only take voice and make the code
        //By using some keywords only Hard Code
        // <-----------It can be Implement By Using NFA Non Defined Automata --------------->

        String data = "Create LinearLayout which has orientation vertical and padding 10dp";

        //here we taking data
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");


        startActivityForResult(intent, 200);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            ArrayList<String> arra = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String voice = arra.get(0);

            inputPl.setText(voice);

//            setTextAllLng(inputPl,voice);
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

        }
    }


    //this will set the area name
    //according to the nearest predefined locations
    private void setAreaNameStrt(LatLng latLng) {

        float distnaceMin = 1000000;
        String areaName = "Null";

        Location startPo = new Location("locationA");
        Location endPo = new Location("locationB");

        startPo.setLatitude(latLng.latitude);
        startPo.setLongitude(latLng.longitude);


        float tamp = 0;
        for (int i = 0; i < locationAddrList.size(); i++) {

            endPo = strToLocation(locationAddrList.get(i));

            tamp = startPo.distanceTo(endPo);
            if (distnaceMin > tamp) {
                distnaceMin = tamp;
                areaName = locationList.get(i);
            }
        }


//        nextSt.setText(areaName);
        startLocV.setText(areaName);
//        setTextAllLng(startLocV, areaName);
    }


}
