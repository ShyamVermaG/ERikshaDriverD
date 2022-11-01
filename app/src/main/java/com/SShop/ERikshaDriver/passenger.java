package com.SShop.ERikshaDriver;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.directions.route.Route;
import com.directions.route.RouteException;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class passenger extends FragmentActivity implements OnMapReadyCallback,
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
                        res = tts.setLanguage(Locale.forLanguageTag(KeyCon.ERikshaDriver.Value.LNG_HIND));

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
                        res=tts.setLanguage(Locale.forLanguageTag(KeyCon.ERikshaDriver.Value.LNG_HIND));

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


//    \\Declare the all  Views


    LinearLayout con;


    int ttlViewAdd = 0;

    LocationRequest locationRequest;

    TextView startLocV;
    Spinner locationSpinStrt;
    LinearLayout locationSpinStrtCon;

    //this will be stored for security purpose
    Location fetched,selected;

    SessionManager sessionManager;
    Spinner locationSpin;
    ArrayList<String> locationList=new ArrayList<>();
    ArrayList<String> locationAddrList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passenger);


        //request location permission.
        requestPermision();



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


//        ArrayAdapter<String>
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
//        readyCurrLngList(locationList,locationAdapS);

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
                    myLocation= KeyCon.strToLocation(locationAddrList.get(position));
                    selected=myLocation;


                    initiallizeall();
                }

                startLocV.setVisibility(View.VISIBLE);
                locationSpinStrtCon.setVisibility(View.GONE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//\\Initiallizing all ids

        con = findViewById(R.id.con);
        passengerT = findViewById(R.id.passengerT);


//        addPerson("khat", "adda", 2, 20);

        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.getKeyAppLng() == KeyCon.ERikshaDriver.Value.LNG_HIND) {
            changeLanguage(KeyCon.ERikshaDriver.Value.LNG_HIND);
        } else {
            changeLanguage(KeyCon.ERikshaDriver.Value.LNG_ENG);
        }
    }

    TextView passengerT;

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


    }


    //initiallize allview
    public void initiallizeall() {

        con.removeAllViews();
        ttlViewAdd=0;

        SessionManager sessionManager = new SessionManager(this);

        String con = KeyCon.ERikshaTranspCon;
        String rikshaIDK = KeyCon.TransCon.ERikshaID;
        String rikshaIdV = sessionManager.getKeyRikshaid();

//        .equalTo(rikshaIdV,rikshaIDK)
        //only take running orders for single userd
        FirebaseDatabase.getInstance().getReference().child(con).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                String status, id, from, to;
                int persN;
                float price;

                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        status = data.child(KeyCon.TransCon.Status).getValue(String.class);
                        if(status.equals(KeyCon.TransCon.Value.TRAV_RUNNING)){
                            from = data.child(KeyCon.TransCon.StartLoc).getValue(String.class);
                            persN = Integer.parseInt(data.child(KeyCon.TransCon.TtlPerson).getValue(String.class));
                            id = data.getKey();

                            // not Avl for running only for cancelled or completed travels
                            to = data.child(KeyCon.TransCon.DestLoc).getValue(String.class);
                            price = Float.parseFloat(data.child(KeyCon.TransCon.Price).getValue(String.class));

//                        Toast.makeText(passenger.this, "" + id + from + persN + to + price + status, Toast.LENGTH_SHORT).show();
                            addPerson(id, from, persN, to, price, status);
                        }

                    }

                    //for all type of passengerrs
                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        status = data.child(KeyCon.TransCon.Status).getValue(String.class);
                        if(!status.equals(KeyCon.TransCon.Value.TRAV_RUNNING)){
                            from = data.child(KeyCon.TransCon.StartLoc).getValue(String.class);
                            persN = Integer.parseInt(data.child(KeyCon.TransCon.TtlPerson).getValue(String.class));
                            id = data.getKey();

                            // not Avl for running only for cancelled or completed travels
                            to = data.child(KeyCon.TransCon.DestLoc).getValue(String.class);
                            price = Float.parseFloat(data.child(KeyCon.TransCon.Price).getValue(String.class));

//                        Toast.makeText(passenger.this, "" + id + from + persN + to + price + status, Toast.LENGTH_SHORT).show();
                            addPerson(id, from, persN, to, price, status);
                        }


                    }
                } else {
                    Toast.makeText(passenger.this, "Passenger Not Found.", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(passenger.this, "Please try again", Toast.LENGTH_SHORT).show();

            }
        });
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

                myLocation = location;
                LatLng ltlng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                        ltlng, 16f);
                mMap.animateCamera(cameraUpdate);

                if (i == 0) {
//                    initiallizeall();
                }
                i++;
            }
        });


    }


    public void addPerson(String id, String from, int persN, String to, float onePrice, String status) {


        float ttlDist = 0;

        final View view = View.inflate(this, R.layout.one_grp, null);
//\\Declare the all  Views


        LinearLayout head;
        TextView No, Price, onePersT, ttlDistT;


        TextView Name, strtLoc, price, ttlDistV, ttlPerson, ttlPrice;

        LinearLayout conS;

        TextView fromV, toV;
        TextView oneP, onePriceM;

//\\Initiallizing all ids

        head = view.findViewById(R.id.head);
        No = view.findViewById(R.id.No);
        Name = view.findViewById(R.id.Name);
        strtLoc = view.findViewById(R.id.strtLoc);
        price = view.findViewById(R.id.price);


        conS = view.findViewById(R.id.conS);

        fromV = view.findViewById(R.id.from);
        toV = view.findViewById(R.id.to);


        oneP = view.findViewById(R.id.oneP);
        onePriceM = view.findViewById(R.id.onePriceM);

        ttlDistV = view.findViewById(R.id.ttlDist);
        ttlPerson = view.findViewById(R.id.ttlPerson);
        ttlPrice = view.findViewById(R.id.ttlPrice);


        //only for convert lang
        onePersT = view.findViewById(R.id.onePersonT);
        ttlDistT = view.findViewById(R.id.ttlDistT);

        onePersT.setText(R.string.onePerPrice);
        ttlDistT.setText(R.string.ttlDis);



        //for speaking
         No.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(No);
                return false;
            }
        });
         strtLoc.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(strtLoc);
                return false;
            }
        });

         onePersT.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(onePersT);
                return false;
            }
        });
         ttlDistT.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(ttlDistT);
                return false;
            }
        });


         Name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(Name);
                return false;
            }
        });
         strtLoc.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(strtLoc);
                return false;
            }
        });
         price.setOnLongClickListener(new View.OnLongClickListener() {
             @Override
             public boolean onLongClick(View view) {
                 speakTVLP(price);
                 return false;
             }
         });
          ttlDistV.setOnLongClickListener(new View.OnLongClickListener() {
              @Override
              public boolean onLongClick(View view) {
                  speakTVLP(ttlDistV);
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
           ttlPrice.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View view) {
                   speakTVLP(ttlPrice);
                   return false;
               }
           });

//        LinearLayout conS;

         fromV.setOnLongClickListener(new View.OnLongClickListener() {
             @Override
             public boolean onLongClick(View view) {
                 speakTVLP(fromV);
                 return false;
             }
         });
         toV.setOnLongClickListener(new View.OnLongClickListener() {
             @Override
             public boolean onLongClick(View view) {
                 speakTVLP(toV);
                 return false;
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



///etting up text

        String startS = findAreaName(KeyCon.strToLatLng(from));
        fromV.setText(startS);
        strtLoc.setText(startS);
//        setTextAllLng(fromV,startS);

        if (status.equals(KeyCon.TransCon.Value.TRAV_RUNNING)) {
            //set the current value
            to = findAreaName(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()));
//            onePrice = calculatePrice(from);
            ttlDist = calculatePrice(from);
            onePrice = ttlDist / 1000;
            toV.setText(to);
//            setTextAllLng(toV,to);


        } else {


            String endS = findAreaName(KeyCon.strToLatLng(to));

            Location locS = KeyCon.strToLocation(from);
            Location locE = KeyCon.strToLocation(to);

            ttlDist = (int) locS.distanceTo(locE);

            toV.setText(endS);
//
//            setTextAllLng(toV,endS);
        }

        ttlDistV.setText(ttlDist + "m.");

        //setting up price by each km
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        onePrice *= sessionManager.getKeyOneKmPrice();

        No.setText(String.valueOf(ttlViewAdd + 1) + ".");
        Name.setText(persN + " " + getString(R.string.person));


        price.setText(String.format("%.2f",onePrice * persN) + " " + getString(R.string.Rs));


        oneP.setText(String.format("%.2f",onePrice) + " " + getString(R.string.Rs));
        onePriceM.setText(String.format("%.2f",onePrice) + "");


        ttlPerson.setText(String.valueOf(persN));


        ttlPrice.setText(String.format("%.2f",onePrice * persN) + " " + getString(R.string.Rs));


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conS.getVisibility() == View.VISIBLE) {
                    conS.setVisibility(View.GONE);
                } else {
                    speakStr(getString(R.string.ttlPrice)+ttlPrice.getText().toString());
                    conS.setVisibility(View.VISIBLE);
                }
            }
        });
//  head.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (conS.getVisibility() == View.VISIBLE) {
//                    conS.setVisibility(View.GONE);
//                } else {
//                    speakStr(getString(R.string.ttlPrice)+ttlPrice.getText().toString());
//                    conS.setVisibility(View.VISIBLE);
//                }
//            }
//        });


        if (status.equals(KeyCon.TransCon.Value.TRAV_RUNNING)) {
            //yellow
            view.setBackground(getResources().getDrawable(R.drawable.card_img));


            String finalTo = to;
            float finalOnePrice = onePrice;
            float finalOnePrice1 = onePrice;
//            conS.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {

//                }
//            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {


                    final AlertDialog.Builder alert = new AlertDialog.Builder(passenger.this);
                    View mview = passenger.this.getLayoutInflater().inflate(R.layout.yes_no_dialog, null);


                    TextView cancelT = mview.findViewById(R.id.cancelT);
                    TextView finishT = mview.findViewById(R.id.finishT);

                    cancelT.setText(R.string.cancel);
                    finishT.setText(R.string.finish);


                    LinearLayout btn_cancel = mview.findViewById(R.id.No);
                    LinearLayout btn_ok = mview.findViewById(R.id.yes);

                    alert.setView(mview);

                    final AlertDialog alertDialog = alert.create();
                    alertDialog.setCanceledOnTouchOutside(true);

                    btn_cancel.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            speakStr(getString(R.string.cancel));
                            return false;
                        }
                    });
                    btn_ok.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            speakStr(getString(R.string.finish));
                            return false;
                        }
                    });
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            //update the travel
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put(KeyCon.TransCon.Price, String.valueOf(finalOnePrice1));
                            hashMap.put(KeyCon.TransCon.DestLoc, (myLocation.getLatitude())+","+String.valueOf(myLocation.getLongitude()));
                            hashMap.put(KeyCon.TransCon.Status, KeyCon.TransCon.Value.TRAV_CANCELED);

                            FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaTranspCon).child(id).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    con.removeView(view);
                                    Toast.makeText(passenger.this, "Ride Cancelled Succefully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(passenger.this, "Please try Again", Toast.LENGTH_SHORT).show();

                                }
                            });


                        }
                    });

                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            alertDialog.dismiss();
                            //update the travel

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put(KeyCon.TransCon.Price, String.valueOf(finalOnePrice));
                            hashMap.put(KeyCon.TransCon.DestLoc, (myLocation.getLatitude())+","+String.valueOf(myLocation.getLongitude()));
                            hashMap.put(KeyCon.TransCon.Status, KeyCon.TransCon.Value.TRAV_FINISHED);

                            FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaTranspCon).child(id).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    con.removeView(view);
                                    Toast.makeText(passenger.this, "Successlly Updated", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(passenger.this, "Please try Again", Toast.LENGTH_SHORT).show();

                                }
                            });


                        }

                        //for checking password
                    });

                    alertDialog.show();


//                }


//
//                    final AlertDialog.Builder alert = new AlertDialog.Builder(passenger.this);
//                    View mview = getLayoutInflater().inflate(R.layout.yes_no_dialog, null);
//
//                    LinearLayout Yes = mview.findViewById(R.id.yes);
//                    LinearLayout No = mview.findViewById(R.id.No);
//
//                    alert.setView(mview);
//
//                    final AlertDialog alertDialog = alert.create();
//                    alertDialog.setCanceledOnTouchOutside(true);
//
//                    Yes.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            alertDialog.dismiss();
//                        }
//                    });
//
//                    No.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            alertDialog.dismiss();
//                        }
//                    });
//
////                    alert.show();
//                    alertDialog.show();

                    return false;
                }
            });


        } else if (status.equals(KeyCon.TransCon.Value.TRAV_FINISHED)) {
            //green
            view.setBackground(getResources().getDrawable(R.drawable.card_img_nor_green));
        } else if (status.equals(KeyCon.TransCon.Value.TRAV_CANCELED)) {
            //red
            view.setBackground(getResources().getDrawable(R.drawable.card_img_nor_red));
        }

        ttlViewAdd++;
        con.addView(view);
    }



    private void setTextAllLng(TextView view,String areaName){



        TranslatorOptions translatorOptionsHindi = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.HINDI)
                .build();

        Translator translatorHindi = Translation.getClient(translatorOptionsHindi);


        SessionManager sessionManager=new SessionManager(this);
        if(sessionManager.getKeyAppLng().equals(KeyCon.ERikshaDriver.Value.LNG_ENG)){
            //for english language direct set text
            view.setText(areaName);
        }else if(sessionManager.getKeyHindiLanguageDownld()){
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
        }else{
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

                            Toast.makeText(passenger.this, "Unable to Download Hindi", Toast.LENGTH_SHORT).show();
                        }
                    });

            Toast.makeText(passenger.this, "Downloading Hindi Language", Toast.LENGTH_SHORT).show();


        }


    }


    //this will set the area name
    //according to the nearest predefined locations
    private String findAreaName(LatLng latLng) {

        float distnaceMin = 1000000;
        String areaName = "Null";

        Location startPo = new Location("locationA");
        Location endPo = new Location("locationB");

        startPo.setLatitude(latLng.latitude);
        startPo.setLongitude(latLng.longitude);

        SessionManager sessionManager = new SessionManager(this);

        ArrayList<String> locationAddrName;
        ArrayList<String> locationAddr;

        //get data from session of default locaitons and keys
        String tampS = sessionManager.getKeyAllKeyLocation();

        ArrayList<ArrayList<String>> data = new ArrayList<>();

        data = KeyCon.convertStrToDefaultLoc(tampS);
        locationAddrName = data.get(0);
        locationAddr = data.get(1);


        float tamp = 0;
        for (int i = 0; i < locationAddr.size(); i++) {

            endPo = KeyCon.strToLocation(locationAddr.get(i));

            tamp = startPo.distanceTo(endPo);
            if (distnaceMin > tamp) {
                distnaceMin = tamp;
                areaName = locationAddrName.get(i);
            }
        }

        return areaName;
    }


    private int calculatePrice(String data) {
        String mD[] = data.split(",");
        Location location = new Location(LocationManager.GPS_PROVIDER);

        location.setLatitude(Double.parseDouble(mD[0]));
        location.setLongitude(Double.parseDouble(mD[1]));

        return (int) myLocation.distanceTo(location);
    }

    @Override
    protected void onStart() {

        super.onStart();

        passengerT.setText(R.string.passenger);


    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;


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
                    LocationServices.getFusedLocationProviderClient(passenger.this).removeLocationUpdates(this);
                    int index = locationResult.getLocations().size() - 1;
                    myLocation=locationResult.getLocations().get(index);
                    setAreaNameStrt(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()));
//                    myLocation = location;

                    initiallizeall();
                }else{
                    Toast.makeText(passenger.this,"Not found",Toast.LENGTH_SHORT).show();
                }
            }
        }, Looper.getMainLooper());

        LocationServices.getFusedLocationProviderClient(getApplicationContext()).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
//                    myLocation = location;
//
//                    initiallizeall();


                }
            }
        });


    }

    public void gotoRoute(View view) {
        startActivity(new Intent(getApplicationContext(), RouteERiksha.class));
    }

    public void gotoBack(View view) {
        onBackPressed();
    }

    private void setAreaNameStrt(LatLng latLng) {

        float distnaceMin = 1000000;
        String areaName = "Null";

        Location startPo = new Location("locationA");
        Location endPo = new Location("locationB");

        startPo.setLatitude(latLng.latitude);
        startPo.setLongitude(latLng.longitude);


        float tamp = 0;
        for (int i = 0; i < locationAddrList.size(); i++) {

            endPo = KeyCon.strToLocation(locationAddrList.get(i));

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
