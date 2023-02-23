package com.SShop.ERikshaDriver;


//import android.support.v7.app.AppCompatActivity;

import static androidx.core.location.LocationManagerCompat.getCurrentLocation;

import android.Manifest;
import android.app.AlertDialog;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
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
import com.google.android.gms.maps.model.Marker;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class onePerson extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, RoutingListener {
//Declare the all  Views


    int personAddF = 0;
    String transType = KeyCon.TransCon.Value.PassType_Passenger;


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
    ArrayList<String> locationListType = new ArrayList<>();
    ArrayList<String> locationAddrList = new ArrayList<>();


    ArrayList<String> locationListMoh = new ArrayList<>();
    ArrayList<String> locationAddrListMoh = new ArrayList<>();
    ArrayList<String> locationListVill = new ArrayList<>();
    ArrayList<String> locationAddrListVill = new ArrayList<>();

//   ArrayList<String> locationList2 = new ArrayList<>();
//    ArrayList<String> locationAddrList2 = new ArrayList<>();

    //place attributes
    Spinner locationSpin;

    LocationRequest locationRequest;

    LinearLayout lastPriceC;

    TextView labour;

//    TextView startLocV;
//    Spinner locationSpinStrt;
//    LinearLayout locationSpinStrtCon;

    //this will be stored for security purpose
//    Location fetched,selected;


    String GMapKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_person);

        sessionManager = new SessionManager(this);
        labour = findViewById(R.id.labour);


        locationListMoh.add(getString(R.string.slctMoh));
        locationAddrListMoh.add("20.22332,20.232343");
        locationListVill.add(getString(R.string.slctVill));
        locationAddrListVill.add("20.22332,20.232343");

        //get data from session of default locaitons and keys
        String tamp = sessionManager.getKeyAllKeyLocation();

        ArrayList<ArrayList<String>> data = new ArrayList<>();

//        Toast.makeText(this, ""+tamp, Toast.LENGTH_SHORT).show();
        data = KeyCon.convertStrToDefaultLoc(tamp);

        ArrayList<String> locNameHE = new ArrayList<>();

        locNameHE = data.get(0);
        locationAddrList.addAll(data.get(1));

        //here we get only language data
        int lng = 1;
        if (sessionManager.getKeyAppLng().equals(KeyCon.ERikshaDriver.Value.LNG_ENG)) {
            lng = 0;
        }

        ArrayList<String> ta = new ArrayList<>();
        int i = 0;
        for (String a : locNameHE) {
            ta = new ArrayList<>();
            ta.addAll(Arrays.asList(a.split("<>")));
            //set mohalla or village according to location
            if (ta.get(2).equals(KeyCon.Mohalla)) {
                locationListMoh.add(ta.get(lng));
                locationAddrListMoh.add(locationAddrList.get(i));
            } else {
                locationListVill.add(ta.get(lng));
                locationAddrListVill.add(locationAddrList.get(i));
            }

            //for parent location
            locationList.add(ta.get(lng));
            locationListType.add(ta.get(2));
            i++;
        }


        Spinner spinnerMoh = findViewById(R.id.locationSpinStrtMoh);
        Spinner spinnerVill = findViewById(R.id.locationSpinStrtVill);


        //for setting Current loc from mohalla
        ArrayAdapter<String> locationAdapSMohll = new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_item, locationListMoh);
//        readyCurrLngList(locationList,locationAdapS);

        locationAdapSMohll.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //setting userAdapter
        spinnerMoh.setAdapter(locationAdapSMohll);

        spinnerMoh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals(getString(R.string.slctMoh))) {

//                    mMap.clear();

                } else {
                    KeyCon.speakStr(locationListMoh.get(position), getApplicationContext());
                    inputPl.setText(locationListMoh.get(position));
                    end = strToLatLng(locationAddrListMoh.get(position));

                    map.clear();
                    setDefaultLocationWName();

                    //add marker
                    //Add Marker on route ending position
                    MarkerOptions endMarker = new MarkerOptions();
                    endMarker.position(end);
                    endMarker.title("Destination");
                    map.addMarker(endMarker);

                    destinationF = end;

                    Findroutes(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), end);

                }

//                startLocV.setVisibility(View.VISIBLE);
//                locationSpinStrtCon.setVisibility(View.GONE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //for setting up start loca from village
        ArrayAdapter<String> locationAdapSVill = new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_item, locationListVill);
//        readyCurrLngList(locationList,locationAdapS);

        locationAdapSVill.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //setting userAdapter
        spinnerVill.setAdapter(locationAdapSVill);

        spinnerVill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals(getString(R.string.slctVill))) {

//                    mMap.clear();

                } else {

                    KeyCon.speakStr(locationListVill.get(position), getApplicationContext());

                    inputPl.setText(locationListVill.get(position));
                    end = strToLatLng(locationAddrListVill.get(position));

                    map.clear();
                    setDefaultLocationWName();

                    //add marker
                    //Add Marker on route ending position
                    MarkerOptions endMarker = new MarkerOptions();
                    endMarker.position(end);
                    endMarker.title("Destination");
                    map.addMarker(endMarker);

                    destinationF = end;

                    Findroutes(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), end);

                }

//                startLocV.setVisibility(View.VISIBLE);
//                locationSpinStrtCon.setVisibility(View.GONE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//        locationList = data.get(0);
//        locationAddrList = data.get(1);
//    locationList2 = data.get(0);
//        locationAddrList2 = data.get(1);


//        startLocV=findViewById(R.id.startLoc);
//
//        locationSpinStrt=findViewById(R.id.locationSpinStrt);
//        locationSpinStrtCon=findViewById(R.id.locationSpinStrtCon);
//
//        startLocV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startLocV.setVisibility(View.GONE);
//                locationSpinStrtCon.setVisibility(View.VISIBLE);
////                locationSpinStrtCon.
//            }
//        });
//
//        startLocV.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                KeyCon.speakTVLP(startLocV,getApplicationContext());
//                return false;
//            }
//        });


//        //for setting up start loc
//        ArrayAdapter<String> locationAdapS = new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_item, locationList2);
////        readyCurrLngList(locationList2,locationAdapS);
//
//        locationAdapS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        //setting userAdapter
//        locationSpinStrt.setAdapter(locationAdapS);
//
////        locationAdap.notifyDataSetChanged();
//        //this is for Season
//        locationSpinStrt.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                KeyCon.speakStr(String.valueOf(locationSpinStrt.getItemAtPosition(i)),getApplicationContext());
//                KeyCon.speakStr(locationList2.get(i),getApplicationContext());
//                return false;
//            }
//        });
//        locationSpinStrt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (parent.getItemAtPosition(position).equals("Select location")) {
//
////                    mMap.clear();
//
//                } else {
//
////                    setTextAllLng(inputPl,locationList.get(position));
//                    startLocV.setText(locationList2.get(position));
//
//                    fetched=myLocation;
//                    myLocation= strToLocation(locationAddrList2.get(position));
//                    selected=myLocation;
//
//                    //add marker
//                    //Add Marker on route ending position
//                    MarkerOptions sMarker = new MarkerOptions();
//                    sMarker.position(start);
//                    sMarker.title("Start");
//                    map.addMarker(sMarker);
//
//
//                    Findroutes(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), end);
//
//                }
//
//                startLocV.setVisibility(View.VISIBLE);
//                locationSpinStrtCon.setVisibility(View.GONE);
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//


        lastPriceC = findViewById(R.id.lastPriceC);


//        Toast.makeText(this,locationList+"aa"+locationAddrList,Toast.LENGTH_SHORT).show();


        myLocation = KeyCon.strToLocation(sessionManager.getKeyCurrentLoc());
//        //here we add click lisstener
//        // for country
//        ArrayAdapter<String> locationAdap = new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_item, locationList);
////        readyCurrLngList(locationList,locationAdap);
//
//        locationAdap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        //setting userAdapter
//        locationSpin.setAdapter(locationAdap);
//
////        locationAdap.notifyDataSetChanged();
//        //this is for Season
//        locationSpin.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                KeyCon.speakStr(locationList.get(i),getApplicationContext());
//
//                return false;
//            }
//        });
//        locationSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (parent.getItemAtPosition(position).equals("Select location")) {
//
////                    mMap.clear();
//
//                } else {
//
////                    setTextAllLng(inputPl,locationList.get(position));
//                    inputPl.setText(locationList.get(position));
//                    end = strToLatLng(locationAddrList.get(position));
//
//                    map.clear();
//                    //add marker
//                    //Add Marker on route ending position
//                    MarkerOptions endMarker = new MarkerOptions();
//                    endMarker.position(end);
//                    endMarker.title("Destination");
//                    map.addMarker(endMarker);
//
//                    destinationF=end;
//
//                    Findroutes(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), end);
//
//                }
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


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

        if (sessionManager.getKeySpeak()) {
            inputPl.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakTVLP(inputPl, getApplicationContext());
                    return false;
                }
            });
            ttlDisT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    KeyCon.speakStr(getString(R.string.ttlDis) + distance.getText().toString(), getApplicationContext());
                }
            });
            distance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    KeyCon.speakStr(getString(R.string.ttlDis) + distance.getText().toString(), getApplicationContext());
                }
            });
            lastPriceC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    KeyCon.speakStr(getString(R.string.ttlPrice) + ttlPrice.getText().toString(), getApplicationContext());
                }
            });
            onePersonT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    KeyCon.speakStr(getString(R.string.onePerPrice) + oneP.getText().toString(), getApplicationContext());
                }
            });
            oneP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    KeyCon.speakStr(getString(R.string.onePerPrice) + oneP.getText().toString(), getApplicationContext());
                }
            });
            oneP.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakTVLP(oneP, getApplicationContext());

                    return false;
                }
            });
            onePriceM.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakTVLP(onePriceM, getApplicationContext());
                    return false;
                }
            });
            ttlPerson.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakTVLP(ttlPerson, getApplicationContext());
                    return false;
                }
            });
            distance.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakTVLP(distance, getApplicationContext());
                    return false;
                }
            });
            ttlPrice.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakTVLP(ttlPrice, getApplicationContext());
                    return false;
                }
            });
            selectLocT.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakTVLP(selectLocT, getApplicationContext());
                    return false;
                }
            });
            onePersonT.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakTVLP(onePersonT, getApplicationContext());
                    return false;
                }
            });
            ttlDisT.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakTVLP(ttlDisT, getApplicationContext());
                    return false;
                }
            });

        }

//        SessionManager sessionManager=new SessionManager(this);
        if (sessionManager.getKeyAppLng() == KeyCon.ERikshaDriver.Value.LNG_ENG) {
            changeLanguage(KeyCon.ERikshaDriver.Value.LNG_ENG);
        } else {
            changeLanguage(KeyCon.ERikshaDriver.Value.LNG_HIND);
        }
    }


    //this is  for creating radylist
    private void readyCurrLngList(ArrayList<String> areaName, ArrayAdapter<String> adapter) {

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
                                    locationList = new ArrayList<>();

                                    locationList = list;
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
                                                    locationList = new ArrayList<>();

                                                    locationList = list;
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
    ImageView btn;

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
                setDefaultLocationWName();


//                start route finding
                Findroutes(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), latLng);
            }
        });

    }

    // function to find Routes.
    public void Findroutes(LatLng Start, LatLng End) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();


        //this is for if the internet
        if (networkInfo == null || networkInfo.isConnected() == false) {

            Location des = new Location(LocationManager.GPS_PROVIDER);
            des.setLatitude(destinationF.latitude);
            des.setLongitude(destinationF.longitude);
//
            int distM = (int) myLocation.distanceTo(des);
//            int oneP = sessionManager.getKeyOneKmPrice();
//            int onePVill = sessionManager.getKeyOneKmPriceVill();
//
//            int price = 0;
//
//            if (checkNearIsVillage(myLocation) || checkNearIsVillage(des)) {
//                //for village price no variation performed
//                price = distM * onePVill / 1000;
//            } else {
//                price = distM * oneP / 1000;
//                //verifyig for varyPrice
//                String varyPriceS = sessionManager.getKEY_VaryPrice();
//
//
//                if (varyPriceS != null) {
//                    int priceV = Integer.parseInt(varyPriceS);
//                if(price<8){
//                    price=8;
//                }else
//                    if (price > oneP + priceV) {
//                        price = oneP + priceV;
//                    } else {
//                        //nothing to do its all right
//                    }
//
//                }
//            }

            SessionManager sessionManager=new SessionManager(this);

            float priceO=sessionManager.calculateOnePersonPr(myLocation,des,transType);

            Boolean villageType=sessionManager.checkNearIsVillage(myLocation) || sessionManager.checkNearIsVillage(des);

            showData(personAddF, distM,priceO,villageType);

        } else if (Start == null || End == null) {
            Toast.makeText(onePerson.this, "Unable to get location", Toast.LENGTH_LONG).show();
        } else {

            GMapKey = sessionManager.getKeyGMAP_KEY();

            start = Start;
            end = End;
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.WALKING)
                    .withListener(onePerson.this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key(GMapKey)  //also define your api key here.
                    .build();
            routing.execute();

            //here we fetch GMapKey
//            if(GMapKey.equals("")){

//            }else{
//                start = Start;
//                end = End;
//                Routing routing = new Routing.Builder()
//                        .travelMode(AbstractRouting.TravelMode.WALKING)
//                        .withListener(onePerson.this)
//                        .alternativeRoutes(true)
//                        .waypoints(Start, End)
//                        .key(GMapKey)  //also define your api key here.
//                        .build();
//                routing.execute();
//            }


//            sdfs
//            start = Start;
//            end = End;
//            Routing routing = new Routing.Builder()
//                    .travelMode(AbstractRouting.TravelMode.WALKING)
//                    .withListener(this)
//                    .alternativeRoutes(true)
//                    .waypoints(Start, End)
//                    .key(getResources().getString(R.string.key))  //also define your api key here.
//                    .build();
//            routing.execute();
        }
    }

//    // function to find Routes.
//    public void Findroutes(ArrayList<LatLng> path) {
//
//        //here we fetch GMapKey
//        if(GMapKey.equals("")){
//            FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaDriverGMapKey).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
//                @Override
//                public void onSuccess(DataSnapshot dataSnapshot) {
//                    if(dataSnapshot.exists()){
//                        GMapKey=dataSnapshot.getValue(String.class);
//
//                        if (path.isEmpty()) {
//                            Toast.makeText(onePerson.this, "Unable to get location", Toast.LENGTH_LONG).show();
//                        } else {
//
//                            Routing routing = new Routing.Builder()
//                                    .travelMode(AbstractRouting.TravelMode.WALKING)
//                                    .withListener(onePerson.this)
//                                    .alternativeRoutes(true)
//                                    .waypoints(path)
//                                    .key(GMapKey)  //also define your api key here.
//                                    .build();
//                            routing.execute();
//                        }
//                    }
//                }
//            });
//        }else{
//            if (path.isEmpty()) {
//                Toast.makeText(onePerson.this, "Unable to get location", Toast.LENGTH_LONG).show();
//            } else {
//
//                Routing routing = new Routing.Builder()
//                        .travelMode(AbstractRouting.TravelMode.WALKING)
//                        .withListener(this)
//                        .alternativeRoutes(true)
//                        .waypoints(path)
//                        .key(GMapKey)  //also define your api key here.
//                        .build();
//                routing.execute();
//            }
//        }
//
//
//    }

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
        setDefaultLocationWName();


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
        des.setLatitude(polylineEndLatLng.latitude);
        des.setLongitude(polylineEndLatLng.longitude);

        int distM = (int) myLocation.distanceTo(des);

        int oneP = sessionManager.getKeyOneKmPrice();
        int onePVill = sessionManager.getKeyOneKmPriceVill();


//        int price = 0;

        SessionManager sessionManager=new SessionManager(this);

        float priceO=sessionManager.calculateOnePersonPr(myLocation,des,transType);

        Boolean villageType=sessionManager.checkNearIsVillage(myLocation) || sessionManager.checkNearIsVillage(des);

        showData( personAddF, distM,priceO,villageType);

//        KeyCon.speakStr(getString(R.string.ttlPrice)+distM*sessionManager.getKeyOneKmPrice()*personAddF/1000,getApplicationContext());
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


    public void showData( int ttlPers, int distM,float price,Boolean villType) {

//        int price=0;

//        price=sessionManager.calculateOnePersonPr()
//        int onePVill=sessionManager.getKeyOneKmPriceVill();
//        int onePrice=sessionManager.getKeyOneKmPrice();
//
//
//        //this is for labour passengers
//        if (transType==KeyCon.TransCon.Value.PassType_Container) {
//            labour.setVisibility(View.VISIBLE);
//
//            price=distM* onePrice/1000;
//
//        } else {
//            labour.setVisibility(View.GONE);
//
//
//            //this is for checking if the tran is of villagers
//            if (villType) {
//                price = distM * onePVill / 1000;
//            } else {
//                price = distM * onePrice / 1000;
//
//                //verifyig for varyPrice
//                String varyPriceS = sessionManager.getKEY_VaryPrice();
//
//
//                if (varyPriceS != null) {
//                    int priceV = Integer.parseInt(varyPriceS);
//            if(price<8){
//                price=8;
//            }else
//                    if (price > onePrice + priceV) {
//                        price = onePrice + priceV;
//                    } else {
//                        //nothing to do its all right
//                    }
//
//                }
//
//            }
//
//        }
//









        oneP.setText(String.valueOf(price));
        onePriceM.setText(String.valueOf(price));
        if (transType==KeyCon.TransCon.Value.PassType_Container) {

            //for 50,100,200,300 Kg weights
            switch (personAddF){
                case 1:{
                    price*=5;
                    ttlPerson.setText(String.valueOf(5));
                    break;
                }
                case 2:{
                    price*=10;
                    ttlPerson.setText(String.valueOf(10));
                    break;
                }
                case 3:{
                    price*=20;
                    ttlPerson.setText(String.valueOf(20));
                    break;
                }
                case 4:{
                    price*=30;
                    ttlPerson.setText(String.valueOf(30));
                    break;
                }
            }

            ttlPrice.setText(String.valueOf(price ));
        } else {
            ttlPerson.setText(String.valueOf(ttlPers));

            ttlPrice.setText(String.valueOf(price * ttlPers));
        }

        distance.setText(distM + " m");

        KeyCon.speakStr(getString(R.string.ttlPrice) + ttlPrice.getText().toString(), getApplicationContext());


    }


    @Override
    protected void onStart() {

        Intent intent = getIntent();
        String tttt = String.valueOf(intent.getData());
        if (tttt.contains("<>")) {
            ArrayList<String> da = new ArrayList();
            da.addAll(Arrays.asList(tttt.split("<>")));
            da.get(0);

            transType = KeyCon.TransCon.Value.PassType_Container;
            personAddF = Integer.parseInt(da.get(1));
        } else {
            transType = KeyCon.TransCon.Value.PassType_Passenger;
            personAddF = Integer.parseInt(tttt);
        }

        super.onStart();
    }

    public void cancelRide(View view) {
        onBackPressed();
    }

    public void AddPerson(View view) {



        //for destination not selected
        if (destinationF == null) {
            destinationF = KeyCon.strToLatLng(locationAddrListMoh.get(0));
//            Toast.makeText(this, ""+getString(R.string.plsSelctLoc), Toast.LENGTH_SHORT).show();
//            return;
        }

        //add person to the Database

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

//
//        //this is for if the selected and fetched loc is different
//        if(selected!=null&&!selected.equals("")){
//
//            if(networkInfo ==null ||networkInfo.isConnected()==false){
//                //Internet is not available
//
//                //store it on session
//                SessionManager sessionManager=new SessionManager(this);
//                sessionManager.addDataChgLoc(String.valueOf(System.currentTimeMillis()),String.valueOf(fetched),String.valueOf(selected));
//
//            }else{
//                HashMap<String,String> dataChange=new HashMap<>();
//                dataChange.put(KeyCon.ChgLoc.RikshaId,sessionManager.getKeyRikshaid());
//                dataChange.put(KeyCon.ChgLoc.Fetched, String.valueOf(fetched));
//                dataChange.put(KeyCon.ChgLoc.Selected, String.valueOf(selected));
//
//                FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaDriverChgLoc).child(String.valueOf(System.currentTimeMillis())).setValue(dataChange);
//            }
//
//
//        }


        //getting time
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());


        SessionManager sessionManager = new SessionManager(this);
        String id = String.valueOf(System.currentTimeMillis());

        if (networkInfo == null || networkInfo.isConnected() == false) {
            //Internet is not available

            //booked seat for session
            if(transType.equals(KeyCon.TransCon.Value.PassType_Container)){
                sessionManager.addBookedSeat(4);
            }else{
                sessionManager.addBookedSeat(personAddF);
            }

            //store it on session
//            SessionManager sessionManager=new SessionManager(this);
//            if(transType==KeyCon.TransCon.Value.PassType_Container)
//                sessionManager.addDataTravel(id,String.valueOf(myLocation.getLatitude()) + "," + String.valueOf(myLocation.getLongitude()),String.valueOf(destinationF.latitude) + "," + String.valueOf(destinationF.longitude),String.valueOf(personAddF),KeyCon.TransCon.Value.PassType_Container,KeyCon.TransCon.Value.TRAV_RUNNING);
//            else
            sessionManager.addDataTravel(id, KeyCon.locToString(myLocation), KeyCon.locToString(destinationLocation), String.valueOf(personAddF), transType, KeyCon.TransCon.Value.ValueTransport.TRAV_RUNNING);
            Toast.makeText(this, "" + getString(R.string.dataSuccStored), Toast.LENGTH_SHORT).show();
//            Toast.makeText(this,id+":"+String.valueOf(myLocation.getLatitude()) + "," + String.valueOf(myLocation.getLongitude())+":"+String.valueOf(destinationF.latitude) + "," + String.valueOf(destinationF.longitude)+":"+String.valueOf(personAddF)+":"+KeyCon.TransCon.Value.TRAV_RUNNING,Toast.LENGTH_SHORT).show();
        } else {

//            FirebaseDatabase.getInstance().getReference().child()
            Travel travel;
//            if(personAddF==-1){
//                for container adding
            travel = new Travel(sessionManager.getKeyRikshaid(),
                    String.valueOf(myLocation.getLatitude()) + "," + String.valueOf(myLocation.getLongitude()),
                    String.valueOf(destinationF.latitude) + "," + String.valueOf(destinationF.longitude),
                    String.valueOf(personAddF),
                    transType,
                    currentDate);
//            }else{
//                for passenger adding
//                travel = new Travel(sessionManager.getKeyRikshaid(),
//                        String.valueOf(myLocation.getLatitude()) + "," + String.valueOf(myLocation.getLongitude()),
//                        String.valueOf(destinationF.latitude) + "," + String.valueOf(destinationF.longitude),
//                        String.valueOf(personAddF),
//                        KeyCon.TransCon.Value.PassType_Passenger,
//                        currentDate);
//            }


//        Toast.makeText(this, "--"+travel.getPrice(), Toast.LENGTH_SHORT).show();

            FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaTranspCon).child(id).setValue(travel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    //booked seat for session
                    if(transType.equals(KeyCon.TransCon.Value.PassType_Container)){
                        sessionManager.addBookedSeat(4);
                    }else{
                        sessionManager.addBookedSeat(personAddF);
                    }

                    Toast.makeText(onePerson.this, "" + getString(R.string.usrAcqSucc), Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(onePerson.this, "" + getString(R.string.plsTryAga), Toast.LENGTH_SHORT).show();

                }
            });

        }


        //goto home
        onBackPressed();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        map = googleMap;

        setDefaultLocationWName();


        //for speak location name
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                KeyCon.speakStr(marker.getTitle(), onePerson.this);
                return false;
            }
        });


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
//
//        locationRequest = new LocationRequest();
////        locationRequest.setInterval()
//        locationRequest.setInterval(5000); // two minute interval
//        locationRequest.setFastestInterval(2000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
//            @Override
//            public void onLocationResult(@NonNull LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                if (locationResult != null && locationResult.getLocations().size() > 0) {
//                    LocationServices.getFusedLocationProviderClient(onePerson.this).removeLocationUpdates(this);
//                    int index = locationResult.getLocations().size() - 1;
//                    myLocation=locationResult.getLocations().get(index);
//
//                    //here we get near by stop and set it to mylocation and text also
//
//
//                    setAreaNameStrt(new LatLng( myLocation.getLatitude(),myLocation.getLongitude()));
//
//
//                    map.clear();
//                    //                //Add Marker on route ending position
//                    MarkerOptions endMarker = new MarkerOptions();
//                    endMarker.position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
//                    endMarker.title("Destinationsdf").icon(KeyCon.bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_directions_car_24));
//                    map.addMarker(endMarker);
//                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
//                            new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 20f);
//                    map.animateCamera(cameraUpdate);
//
////                    myLocation=location;
//
//
//                    //                //Add Marker on route ending position
////                    MarkerOptions endMarker = new MarkerOptions();
////                    endMarker.position(new LatLng(location.getLatitude(),location.getLongitude()));
////                    endMarker.title("Destinationsdf").icon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_arrow));
////
////
////                    SessionManager sessionManager=new SessionManager(getApplicationContext());
////                    sessionManager.setKeyCurrentLoc(myLocation.getLatitude()+","+myLocation.getLongitude());
////
////                    startActivity(new Intent(SplashScreen.this,HomeActivity.class));
////                    finish();
//                }else{
//                    Toast.makeText(onePerson.this,"Not found",Toast.LENGTH_SHORT).show();
//                }
//            }
//        }, Looper.getMainLooper());
//

////        LocationServices.getFusedLocationProviderClient(this).ge
//        LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if (location != null) {
////                    myLocation = location;
////
////
////                    map.clear();
////                    //                //Add Marker on route ending position
////                    MarkerOptions endMarker = new MarkerOptions();
////                    endMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
////                    endMarker.title("Destinationsdf").icon(KeyCon.bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_directions_car_24));
////                    map.addMarker(endMarker);
////                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
////                            new LatLng(location.getLatitude(), location.getLongitude()), 20f);
////                    map.animateCamera(cameraUpdate);
////
//
//                }
//            }
//        });


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

            searchTxt(inputPl);

//            setTextAllLng(inputPl,voice);
        } else {
            Toast.makeText(this, "" + getString(R.string.smeWenWrn), Toast.LENGTH_SHORT).show();

        }
    }


    //this will set the area name
    //according to the nearest predefined locations
//    private void setAreaNameStrt(LatLng latLng) {
//
//        float distnaceMin = 1000000;
//        String areaName = "Null";
//
//        Location startPo = new Location("locationA");
//        Location endPo = new Location("locationB");
//
//        startPo.setLatitude(latLng.latitude);
//        startPo.setLongitude(latLng.longitude);
//
//
//        float tamp = 0;
//        for (int i = 0; i < locationAddrList.size(); i++) {
//
//            endPo = strToLocation(locationAddrList.get(i));
//
//            tamp = startPo.distanceTo(endPo);
//            if (distnaceMin > tamp) {
//                distnaceMin = tamp;
//                areaName = locationList.get(i);
//            }
//        }
//
//
////        nextSt.setText(areaName);
//        startLocV.setText(areaName);
////        setTextAllLng(startLocV, areaName);
//    }

    private void setDefaultLocationWName() {
        LatLng locat;

        for (int i = 0; i < locationList.size(); i++) {
            MarkerOptions endMarker = new MarkerOptions();
            locat = KeyCon.strToLatLng(locationAddrList.get(i));
            endMarker.position(locat);
            endMarker.title(locationList.get(i)).icon(KeyCon.bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_location_on_24));
            map.addMarker(endMarker);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 20f);
            map.animateCamera(cameraUpdate);


        }

    }


    //here we check for near location belongs to which category
//    private Boolean checkNearIsVillage(Location startPo) {
//
//        Boolean vill = true;
//
//
//        //here we find min distance point
//        float distnaceMin = 1000000;
//        String areaName = "Null<>Null<>";
////        String locaMinS = "20.232323";
//
//        Location endPo = new Location("locationB");
//
//
//        float tamp = 0;
//        //bcz we niglet selectlocation
//        for (int i = 1; i < locationAddrList.size(); i++) {
//
//            endPo = KeyCon.strToLocation(locationAddrList.get(i));
//
//            tamp = startPo.distanceTo(endPo);
//            if (distnaceMin >= tamp) {
//                distnaceMin = tamp;
////                locaMinS = locationAddrList.get(i);
//                areaName = locationList.get(i);
//                if(locationListType.get(i).equals("0")){
//                    vill=false;
//                }
////                else{
////                    vill=true;
////                }
//            }
//        }
//
//
////        Toast.makeText(this,areaName, Toast.LENGTH_SHORT).show();
////        //here we check if the name is known as village
////        //find for 0
////        for (int i = 0; i < locationList.size(); i++) {
////            //if it found tt it town
////            //0 for mohallla  and 1 for village
////
////            //if town is found in list then we update the return
////            if (locationList.get(i).equals(areaName) && locationListType.get(i).equals("0")) {
////                vill = false;
////            }
////        }
//
//
////        Toast.makeText(this," "+vill, Toast.LENGTH_SHORT).show();
//
//        return vill;
//
//
//    }


}
