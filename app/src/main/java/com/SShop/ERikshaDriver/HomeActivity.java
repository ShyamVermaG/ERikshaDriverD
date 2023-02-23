package com.SShop.ERikshaDriver;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {


    Switch switchL, switchS;

    TextToSpeech tts;

    String AreaNameF="Current Location";

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        tts.stop();
//        tts.shutdown();
    }

    TextView person1, person2, person3, person4,person5, chgLng, addPerson,addContainerT, ttlDistTraveled,headCurr;
    LinearLayout add1Pers, add2Pers, add3Pers, add4Pers,add5Pers,addContainer;


    //this will be stored for security purpose

    TextView startLocV;
//    Spinner locationSpinStrt;
//    LinearLayout locationSpinStrtCon;

    //this will be stored for security purpose
    Location fetched, selected;

    SessionManager sessionManager;
//    Spinner locationSpin;

    //for here calculate
    ArrayList<String> locationList = new ArrayList<>();
    ArrayList<String> locationAddrList = new ArrayList<>();


    ArrayList<String> locationListMoh = new ArrayList<>();
    ArrayList<String> locationAddrListMoh = new ArrayList<>();
    ArrayList<String> locationListVill = new ArrayList<>();
    ArrayList<String> locationAddrListVill = new ArrayList<>();


    com.google.android.gms.location.LocationRequest locationRequest;

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        sessionManager = new SessionManager(this);



        //for setting up drawerLayout
        //this is for initializing drawer layout
        drawerLayout=findViewById(R.id.drawer_layout);

        TextView userName = drawerLayout.findViewById(R.id.userName);
        ImageView userImg=drawerLayout.findViewById(R.id.userImg);
        TextView userAddr=drawerLayout.findViewById(R.id.userAdd);

        switchS = drawerLayout.findViewById(R.id.switchS);
        switchL = drawerLayout.findViewById(R.id.switchL);




        userName.setText(sessionManager.getKeyName());
        userAddr.setText(sessionManager.getKeyAddress());







        locationListMoh.add("Select Mohalla");
        locationAddrListMoh.add("abc");
        locationListVill.add("Select Village");
        locationAddrListVill.add("abc");

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
        int i=0;
        for (String a : locNameHE) {
            ta = new ArrayList<>();
            ta.addAll(Arrays.asList(a.split("<>")));
            //set mohalla or village according to location
            if(ta.get(2).equals(KeyCon.Mohalla)){
                locationListMoh.add(ta.get(lng));
                locationAddrListMoh.add(locationAddrList.get(i));
            }
            else{
                locationListVill.add(ta.get(lng));
                locationAddrListVill.add(locationAddrList.get(i));
            }

            //for parent location
            locationList.add(ta.get(lng));

            i++;
        }




//        End of taking input










        startLocV = findViewById(R.id.startLoc);

//        locationSpinStrt = findViewById(R.id.locationSpinStrt);
//        locationSpinStrtCon = findViewById(R.id.locationSpinStrtCon);

        startLocV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //here we take input
               verifyLocation();
            }
        });



        locationRequest = new LocationRequest();
//        locationRequest.setInterval()
        locationRequest.setInterval(5000);
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
//        Loca
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    LocationServices.getFusedLocationProviderClient(HomeActivity.this).removeLocationUpdates(this);
                    int index = locationResult.getLocations().size() - 1;
                    fetched = locationResult.getLocations().get(index);

                    sessionManager.setKeyCurrentLoc(KeyCon.locToString(fetched));
                    setAreaName(fetched);

                    //here we verify current location

                    verifyLocation();



                    ////                        mMap.clear();
//                    //                //Add Marker on route ending position
//                    MarkerOptions endMarker = new MarkerOptions();
//                    endMarker.position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
//                    endMarker.title("Destinationsdf").icon(KeyCon.bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_directions_car_24));
//                    mMap.addMarker(endMarker);
//                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
//                            new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 20f);
//                    mMap.animateCamera(cameraUpdate);
//
//
//                    allPoints.add(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
//
//
//                    //for offline data adding in showing time
//                    ArrayList<ArrayList<String>> data = new ArrayList<>();
//                    SessionManager sessionManager = new SessionManager(getApplicationContext());
//                    data = sessionManager.showDataTravel();
//
//                    for (ArrayList<String> a : data) {
//                        if (a.get(4).equals(KeyCon.TransCon.Value.TRAV_RUNNING)) {
//                            allPoints.add(KeyCon.strToLatLng(a.get(2)));
//                        }
//                    }
//
//
//                    initiateAll();
                } else {
                    Toast.makeText(HomeActivity.this, "Not found", Toast.LENGTH_SHORT).show();
                }
            }
        }, Looper.getMainLooper());


//        fetched = KeyCon.strToLocation(sessionManager.getKeyCurrentLoc());
//        setAreaName(fetched);
//        startLocV.setText();

        ttlDistTraveled = findViewById(R.id.ttlDistTraveled);


//        Locale locale=new Locale("hi","IN");

        person1 = findViewById(R.id.person1);
        person2 = findViewById(R.id.person2);
        person3 = findViewById(R.id.person3);
        person4 = findViewById(R.id.person4);
        person5 = findViewById(R.id.person5);
        chgLng = findViewById(R.id.chgLng);
        addPerson = findViewById(R.id.addPerson);
        addContainer = findViewById(R.id.addContainer);
        addContainerT = findViewById(R.id.addContainerT);
        headCurr = findViewById(R.id.headCurr);

        add1Pers = findViewById(R.id.add1Pers);
        add2Pers = findViewById(R.id.add2Pers);
        add3Pers = findViewById(R.id.add3Pers);
        add4Pers = findViewById(R.id.add4Pers);
        add5Pers = findViewById(R.id.add5Pers);


        if (sessionManager.getKeySpeak()) {
            addContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakTVLP(addContainerT, getApplicationContext());
                    return false;
                }
            });
            startLocV.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakTVLP(startLocV, getApplicationContext());
                    return false;
                }
            });

            add1Pers.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakStr(getString(R.string.add) + person1.getText().toString(), getApplicationContext());
                    return false;
                }
            });


            add2Pers.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakStr(getString(R.string.add) + person2.getText().toString(), getApplicationContext());
                    return false;
                }
            });
            add3Pers.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakStr(getString(R.string.add) + person3.getText().toString(), getApplicationContext());
                    return false;
                }
            });
            add4Pers.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakStr(getString(R.string.add) + person4.getText().toString(), getApplicationContext());
                    return false;
                }
            });
            add5Pers.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakStr(getString(R.string.add) + person5.getText().toString(), getApplicationContext());
                    return false;
                }
            });


            chgLng.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakTVLP(chgLng, getApplicationContext());
                    return false;
                }
            });
            addPerson.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakTVLP(addPerson, getApplicationContext());
                    return false;
                }
            });
            person1.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakTVLP(person1, getApplicationContext());
                    return false;
                }
            });
            person2.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakTVLP(person2, getApplicationContext());
                    return false;
                }
            });
            person3.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakTVLP(person3, getApplicationContext());
                    return false;
                }
            });
            person4.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakTVLP(person4, getApplicationContext());
                    return false;
                }
            });
            person5.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    KeyCon.speakTVLP(person5, getApplicationContext());
                    return false;
                }
            });
        }

//        SessionManager sessionManager=new SessionManager(this);

        if (sessionManager.getKeyAppLng().equals(KeyCon.ERikshaDriver.Value.LNG_ENG)) {
            changeLanguage(KeyCon.ERikshaDriver.Value.LNG_ENG);
            switchL.setChecked(false);
        } else {

            //hindi
            changeLanguage(KeyCon.ERikshaDriver.Value.LNG_HIND);
            switchL.setChecked(true);
        }


        switchL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                sessionManager.setKeyAppLanguage(b ? KeyCon.ERikshaDriver.Value.LNG_HIND: KeyCon.ERikshaDriver.Value.LNG_ENG);

                if (b) {
                    changeLanguage(KeyCon.ERikshaDriver.Value.LNG_HIND);
                    sessionManager.setKeyAppLanguage(KeyCon.ERikshaDriver.Value.LNG_HIND);
                } else {
                    changeLanguage(KeyCon.ERikshaDriver.Value.LNG_ENG);
                    sessionManager.setKeyAppLanguage(KeyCon.ERikshaDriver.Value.LNG_ENG);
                }
            }
        });


        //for speaking

        switchS.setChecked(sessionManager.getKeySpeak());


        switchS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                sessionManager.setKeyAppLanguage(b ? KeyCon.ERikshaDriver.Value.LNG_HIND: KeyCon.ERikshaDriver.Value.LNG_ENG);

                sessionManager.setKeySpeak(b);

            }
        });

    }




    private  void verifyLocation(){

        //This is for confirming current Location
        final AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
        View mview = getLayoutInflater().inflate(R.layout.confirm_crrent_loc, null);


        TextView currLoc=mview.findViewById(R.id.locName);
        Spinner spinnerMoh=mview.findViewById(R.id.locationSpinStrtMoh);
        Spinner spinnerVill=mview.findViewById(R.id.locationSpinStrtVill);
        Button Ok=mview.findViewById(R.id.Ok);

        currLoc.setText(AreaNameF);



        alert.setView(mview);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);



        //for setting Current loc from mohalla
        ArrayAdapter<String> locationAdapSMohll = new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_item, locationListMoh);
//        readyCurrLngList(locationList,locationAdapS);

        locationAdapSMohll.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //setting userAdapter
        spinnerMoh.setAdapter(locationAdapSMohll);

        spinnerMoh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Select Mohalla")) {

//                    mMap.clear();

                } else {

//                    setTextAllLng(inputPl,locationList.get(position));
//                    startLocV.setText(locationList.get(position));

                    startLocV.setText(locationListMoh.get(position));
//                    setAreaName(KeyCon.strToLocation(locationAddrListMoh.get(position)));
                    selected = KeyCon.strToLocation(locationAddrListMoh.get(position));

                    sessionManager.setKeyCurrentLoc(locationAddrListMoh.get(position));

                    String tamp=locationListMoh.get(position);
                    currLoc.setText(tamp);
                    KeyCon.speakStr(tamp,getApplicationContext());

                }

//                startLocV.setVisibility(View.VISIBLE);
//                locationSpinStrtCon.setVisibility(View.GONE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        spinnerMoh.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                Toast.makeText(HomeActivity.this, "hee", Toast.LENGTH_SHORT).show();
//                spinnerMoh.getSelectedItemPosition();
//                return false;
//            }
//        });


        spinnerMoh.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),i+" long "+l,Toast.LENGTH_SHORT).show();
                return false;
            }
        });
//        spinnerMoh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getApplicationContext(),i+" cli "+l,Toast.LENGTH_SHORT).show();
//            }
//        });

        //for setting up start loca from village
        ArrayAdapter<String> locationAdapSVill = new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_item, locationListVill);
//        readyCurrLngList(locationList,locationAdapS);

        locationAdapSVill.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //setting userAdapter
        spinnerVill.setAdapter(locationAdapSVill);

        spinnerVill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Select Village")) {

//                    mMap.clear();

                } else {

//                    setTextAllLng(inputPl,locationList.get(position));
//                    startLocV.setText(locationList.get(position));

                    startLocV.setText(locationListVill.get(position));

                    selected = KeyCon.strToLocation(locationAddrListVill.get(position));

                    sessionManager.setKeyCurrentLoc(locationAddrListVill.get(position));

                    String tamp= locationListVill.get(position);
                    currLoc.setText(tamp);

                    KeyCon.speakStr(tamp,getApplicationContext());

                }

//                startLocV.setVisibility(View.VISIBLE);
//                locationSpinStrtCon.setVisibility(View.GONE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }
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

        chgLng.setText(R.string.changeLng);
        addPerson.setText(R.string.addName);
        person1.setText(R.string.onePerson);
        person2.setText(R.string.twoPerson);
        person3.setText(R.string.threePerson);
        person4.setText(R.string.fourPerson);
        person5.setText(R.string.fifthPerson);

        headCurr.setText(R.string.current_Loc);
        addContainerT.setText(R.string.addContainer);
//        Toast.makeText(this, "lang change", Toast.LENGTH_SHORT).show();

    }


    public void gotoPassenList(View view) {
        updateChgLocIfAva();

        Intent intent = new Intent(this, passenger.class);
        startActivity(intent);
        updateChgLocIfAva();
    }

    public void gotoRoute(View view) {
        updateChgLocIfAva();

        Intent intent = new Intent(this, RouteERiksha.class);
        startActivity(intent);
    }

    public void add1Person(View view) {
        updateChgLocIfAva();

        Intent intent = new Intent(this, onePerson.class);
        intent.setData(Uri.parse("1"));
        startActivity(intent);
    }

    public void add2Person(View view) {
        updateChgLocIfAva();

        Intent intent = new Intent(this, onePerson.class);
        intent.setData(Uri.parse("2"));
        startActivity(intent);
    }

    public void add3Person(View view) {
        updateChgLocIfAva();

        Intent intent = new Intent(this, onePerson.class);
        intent.setData(Uri.parse("3"));
        startActivity(intent);
    }

    public void add4Person(View view) {
        updateChgLocIfAva();

        Intent intent = new Intent(this, onePerson.class);
        intent.setData(Uri.parse("4"));
        startActivity(intent);
    }
    public void add5Person(View view) {
        updateChgLocIfAva();

        Intent intent = new Intent(this, onePerson.class);
        intent.setData(Uri.parse("5"));
        startActivity(intent);
    }

    public void addContainer(View view) {
        updateChgLocIfAva();



        final AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
        View mview = getLayoutInflater().inflate(R.layout.transport_quantity, null);


        ConstraintLayout add1Trans = mview.findViewById(R.id.add1Trans);
        ConstraintLayout add2Trans = mview.findViewById(R.id.add2Trans);
        ConstraintLayout add3Trans = mview.findViewById(R.id.add3Trans);
        ConstraintLayout add4Trans = mview.findViewById(R.id.add4Trans);

        TextView add1TransT = mview.findViewById(R.id.trans1);
        TextView add2TransT = mview.findViewById(R.id.trans2);
        TextView add3TransT = mview.findViewById(R.id.trans3);
        TextView add4TransT = mview.findViewById(R.id.trans4);

//        Button cancel = mview.findViewById(R.id.cancel);
//        EditText iputV=mview.findViewById(R.id.inputView);

        alert.setView(mview);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);

        add1Trans.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                KeyCon.speakTVLP(add1TransT,getApplicationContext());
                return false;
            }
        });
        add2Trans.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                KeyCon.speakTVLP(add2TransT,getApplicationContext());
                return false;
            }
        });
        add3Trans.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                KeyCon.speakTVLP(add3TransT,getApplicationContext());
                return false;
            }
        });
        add4Trans.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                KeyCon.speakTVLP(add4TransT,getApplicationContext());
                return false;
            }
        });

        add1Trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();

                Intent intent = new Intent(getApplicationContext(), onePerson.class);
                intent.setData(Uri.parse("-1<>1"));
                startActivity(intent);
            }
        });
        add2Trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();

                Intent intent = new Intent(getApplicationContext(), onePerson.class);
                intent.setData(Uri.parse("-1<>2"));
                startActivity(intent);
            }
        });
        add3Trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();

                Intent intent = new Intent(getApplicationContext(), onePerson.class);
                intent.setData(Uri.parse("-1<>3"));
                startActivity(intent);
            }
        });
        add4Trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();

                Intent intent = new Intent(getApplicationContext(), onePerson.class);
                intent.setData(Uri.parse("-1<>4"));
                startActivity(intent);
            }
        });



        alertDialog.show();


    }

    private void updateChgLocIfAva() {
        if (selected != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo == null || networkInfo.isConnected() == false) {
                //Internet is not available

                //store on session
                sessionManager.addDataChgLoc(String.valueOf(System.currentTimeMillis()), fetched.getLatitude() + "," + fetched.getLongitude(), selected.getLatitude() + "," + selected.getLongitude());
            } else {
                //store on web database
                HashMap<String, String> dataChange = new HashMap<>();
                dataChange.put(KeyCon.ChgLoc.RikshaId, sessionManager.getKeyRikshaid());
                dataChange.put(KeyCon.ChgLoc.Fetched, String.valueOf(fetched));
                dataChange.put(KeyCon.ChgLoc.Selected, String.valueOf(selected));

                FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaDriverChgLoc).child(String.valueOf(System.currentTimeMillis())).setValue(dataChange);

            }
        }

    }




    public void gotoPenList(View view) {
        startActivity(new Intent(this, passenger.class));

    }


    //this will set the area name
    //according to the nearest predefined locations
    private void setAreaName(Location startPo) {

        float distnaceMin = 1000000;
        String areaName = "Null<>Null<>";
        String locaMinS = "20.232323";

        Location endPo = new Location("locationB");



        float tamp = 0;
        //bcz we niglet selectlocation
        for (int i = 1; i < locationAddrList.size(); i++) {

            endPo = KeyCon.strToLocation(locationAddrList.get(i));

            tamp = startPo.distanceTo(endPo);
            if (distnaceMin > tamp) {
                distnaceMin = tamp;
                locaMinS = locationAddrList.get(i);
                areaName = locationList.get(i);
            }
        }



        startLocV.setText(areaName);

        AreaNameF=areaName;


        //now here we check if the location is far then 1 km than we request it to add it
//        Location gotLoc=findNearAreaLoc(areaName);
        Location gotLoc=KeyCon.strToLocation(locaMinS);

        if(gotLoc!=null){
            if(startPo.distanceTo(gotLoc)>1000){
                //request to add new place


                final AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
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
                        hashMap.put(KeyCon.NewLocToAdd.LocationAddr,KeyCon.locToString(startPo));


                        FirebaseDatabase.getInstance().getReference().child(KeyCon.NewLocToAddCon).child(String.valueOf(System.currentTimeMillis())).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(HomeActivity.this, "Location will be added soon.", Toast.LENGTH_SHORT).show();
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
//                alertDialog.dismiss();
                alertDialog.show();
            }
        }
//        Toast.makeText(this, areaName, Toast.LENGTH_SHORT).show();

//        //here we set area name according to the language
//        ArrayList<String> hee = new ArrayList<>();
//        hee.addAll(Arrays.asList(areaName.split("<>")));
//
//        if (sessionManager.getKeyAppLng().equals(KeyCon.ERikshaDriver.Value.LNG_ENG))
//            startLocV.setText(hee.get(0));
//        else
//            startLocV.setText(hee.get(1));
//        setTextAllLng(nextSt, areaName);
    }


    //this will return the address of location if it present in list else null

    private Location findNearAreaLoc(String locName) {
        for(int i=0;i<locationList.size();i++){
            if(locationList.get(i)==locName){
                return KeyCon.strToLocation(locationAddrList.get(i));
            }
        }

        return null;

//        return areaName;
    }


    public void gotoProfile(View view) {
        startActivity(new Intent(this, Profile.class));
        drawerLayout.close();

    }




    public void shareApplication(View view){
//
//Bitmap bitmap ;
//
//                    if((orgImg.size()>0)){
//                        bitmap=textDecod(orgImg.get(MainImageShowing));
//                    }else{
//                        bitmap= ((BitmapDrawable) imageDrawable.getDrawable()).getBitmap();
//                    }

//
        Bitmap bitMap=((BitmapDrawable)getDrawable(R.drawable.e_riksha)).getBitmap();
//        Bitmap bitMap= ((BitmapDrawable)shrImg.getDrawable()).getBitmap();

        String bitPath= MediaStore.Images.Media.insertImage(getContentResolver(),bitMap,"title",null);

//    try {
//        bitPath = MediaStore.Images.Media.insertImage(getContentResolver(), String.valueOf(R.drawable.e_riksha), "title", null);
//    } catch (FileNotFoundException e) {
//        e.printStackTrace();
//    }

//    Uri uris = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
//            "://" + getResources().getResourcePackageName(R.drawable.e_riksha)
//            + '/' + getResources().getResourceTypeName(R.drawable.e_riksha) + '/' + getResources().getResourceEntryName(R.drawable.e_riksha) );
//
        Uri uris = Uri.parse(String.valueOf(bitPath));

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uris);
        //share playstore link with itemId
        intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.SShop.ERikshaDriver" );
        startActivity(Intent.createChooser(intent, "Share"));
        drawerLayout.close();

    }

    //for drawer click
    public void gotoHelp(View view){
        startActivity(new Intent(this,HelpActivity.class));
        drawerLayout.close();
    }

    public void gotoPrivacyPolicy(View view) {
        startActivity(new Intent(this, PrivacyPolicy.class));
        drawerLayout.close();
    }

    public void gotoTermCondition(View view) {
        startActivity(new Intent(this, TermsCondition.class));
        drawerLayout.close();
    }

    public void gotoDriverLoc(View view) {
        startActivity(new Intent(this, DriversOnline.class));
        drawerLayout.close();
    }

    public void setOffTirri(View view) {
        SessionManager sessionManager=new SessionManager(this);
        sessionManager.setKEY_RikshaStatus(KeyCon.ERikshaDriver.Value.Rik_Stat_Off);


        //stop background updater
        endServiceBackUpdate();
        //exit from app
        System.exit(0);
    }



    //for closing the background loc,status updater
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void endServiceBackUpdate() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Log.d("ExampleJobService", "Job Cancelled");
    }



    public void restartService(View view){

        endServiceBackUpdate();
        startBackGroundTirriUpdate();

    }


    //for starting the background loc,status updater

    ///for stle the job for cheange status
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startBackGroundTirriUpdate() {
        ComponentName componentName = new ComponentName(this, ExampleJobService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic( 1* 60 * 1000)
                .build();
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d("ExampleJobService", "Job Scheduled");
            Toast.makeText(this, "Job Scheduled", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("ExampleJobService", "Job Scheduling failed");
        }
    }



}