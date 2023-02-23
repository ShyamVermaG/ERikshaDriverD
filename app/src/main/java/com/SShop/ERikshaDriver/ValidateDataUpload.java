package com.SShop.ERikshaDriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

public class ValidateDataUpload extends AppCompatActivity {

    //this is for checking how many work done
    int TotalUpload=0;
     AlertDialog alertDialogS = null;

    String currentDate="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_data_upload);

        Button btn_cancel = findViewById(R.id.notNow);
        Button btn_ok = findViewById(R.id.updload);

        SessionManager sessionManager = new SessionManager(this);


        //getting yester day date
        Calendar calendar = Calendar.getInstance();
        currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 1);
        String yestDate = DateFormat.getDateInstance().format(calendar.getTime());
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 2);
        String yestPreDate = DateFormat.getDateInstance().format(calendar.getTime());


        if (sessionManager.getKeySystemDataDate() == yestDate) {
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();
                }
            });
        } else {
            btn_cancel.setClickable(false);
        }


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

//
                if (networkInfo != null && networkInfo.isConnected() == true) {
                    //Internet is available
                    final AlertDialog.Builder alertS = new AlertDialog.Builder(ValidateDataUpload.this);
                    View mview = getLayoutInflater().inflate(R.layout.show_uploading, null);


                    alertS.setView(mview);

                    alertDialogS = alertS.create();
                    alertDialogS.setCanceledOnTouchOutside(false);

                    alertDialogS.show();


                    //here we check for  update and block activity,price
                    //for taking control data
                    //check for the version

                    FirebaseDatabase.getInstance().getReference().child("Control").child(KeyCon.ERikshaDriverCon).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot snapshot) {

                            String varyPrice = snapshot.child(KeyCon.ERikshaDriverVaryPrice).getValue(String.class);
                            String onePrice = snapshot.child(KeyCon.ERikshaDriverPrice).getValue(String.class);
                            String onePriceVill = snapshot.child(KeyCon.ERikshaDriverPriceVill).getValue(String.class);

                            String blockValue = snapshot.child("Block").getValue(String.class);
                            String updateValue = snapshot.child("Update").getValue(String.class);
                            Boolean verAvail = true;


                            ////setting up price

                            sessionManager.setKEY_VaryPrice(varyPrice);
                            sessionManager.setKeyOneKmPrice(Integer.parseInt(onePrice));
                            sessionManager.setKeyOneKmPriceVill(Integer.parseInt(onePriceVill));

                            //finding that version is available or not
                            ArrayList<String> availableVer = new ArrayList<>();
                            availableVer.addAll(Arrays.asList(updateValue.split("<>")));
                            for (String ver : availableVer) {
                                if (ver.equals(getApplicationContext().getString(R.string.versionName))) {
                                    verAvail = false;
                                }
                            }

                            if (blockValue != null && updateValue != null)
                                if (blockValue.equals("true")) {
                                    startActivity(new Intent(getApplicationContext(), BlockActivity.class));
                                    finish();
                                } else if (verAvail) {
                                    startActivity(new Intent(getApplicationContext(), UpdateActivity.class));
                                    finish();
                                }


                            TotalUpload++;

                        }
                    });

                    //here we get GMAP_KEy
                    FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaDriverGMapKey).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                sessionManager.setKeyGmapKey(dataSnapshot.getValue(String.class));
                                TotalUpload++;
                            }
                        }
                    });


                    //setting up all default location
                    ArrayList<String> locAddr = new ArrayList<>();
                    ArrayList<String> locAddrName = new ArrayList<>();
                    FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaLoc).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    locAddr.add(data.getValue(String.class));
                                    locAddrName.add(data.getKey());
                                }

                                String data = KeyCon.convertDefaultLocToStr(locAddrName, locAddr);
                                sessionManager.setKeyAllKeyLocation(data);
//                        Toast.makeText(HomeActivity.this, ""+data, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "No Default Location found", Toast.LENGTH_SHORT).show();
                            }

                            TotalUpload++;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_SHORT).show();
                        }
                    });




                    //for taking array list data

                    //this is for Travel data
                    //on yes
                    ArrayList<ArrayList<String>> data = new ArrayList<>();
                    data = sessionManager.showDataTravel();

                    //for emplty data
                    if(data==null||data.isEmpty())
                        TotalUpload++;

                    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 1);
                    String yestDate = DateFormat.getDateInstance().format(calendar.getTime());

//                Location
                    HashMap<String, String> TravelS = new HashMap<>();
                    for (ArrayList<String> a : data) {
                        TravelS = new HashMap<>();


                        TravelS.put(KeyCon.TransCon.ERikshaID, sessionManager.getKeyRikshaid());
                        TravelS.put(KeyCon.TransCon.NotPaid, KeyCon.False);
                        TravelS.put(KeyCon.TransCon.CustomerMode, KeyCon.TransCon.Value.CUS_OFFLINE);
                        TravelS.put(KeyCon.TransCon.Date, yestDate);
                        TravelS.put(KeyCon.TransCon.OnlineUsrId, "");
                        TravelS.put(KeyCon.TransCon.Price, "");
                        TravelS.put(KeyCon.TransCon.RequERiksha, "");
                        TravelS.put(KeyCon.TransCon.RequS, "");

                        TravelS.put(KeyCon.TransCon.StartLoc, a.get(1));
                        TravelS.put(KeyCon.TransCon.DestLoc, a.get(2));
                        TravelS.put(KeyCon.TransCon.TtlPerson, a.get(3));
                        TravelS.put(KeyCon.TransCon.PassengerType, a.get(4));
                        TravelS.put(KeyCon.TransCon.Status, a.get(5));


                        //here we add data and also delete data
                        FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaTranspCon).child(a.get(0)).setValue(TravelS).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                sessionManager.deleteDataTravel(a.get(0));
                                if(sessionManager.checkEmptyTravel()){
                                    TotalUpload++;
                                }
                            }
                        });
//                    a.get(0);
                    }


                    //this is for change location data

                    //on yes
                    data = new ArrayList<>();
                    data = sessionManager.showDataChgLoc();

                    //for emplty data
                    if(data==null||data.isEmpty())
                        TotalUpload++;
//            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 1);
//            String yestDate = DateFormat.getDateInstance().format(calendar.getTime());

//                Location
                    HashMap<String, String> dataChg = new HashMap<>();
                    for (ArrayList<String> a : data) {

                        dataChg = new HashMap<>();

                        dataChg.put(KeyCon.ChgLoc.RikshaId, sessionManager.getKeyRikshaid());
                        dataChg.put(KeyCon.ChgLoc.Fetched, a.get(1));
                        dataChg.put(KeyCon.ChgLoc.Selected, a.get(2));


                        //here we add data and also delete data
                        FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaDriverChgLoc).child(a.get(0)).setValue(dataChg).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                sessionManager.deleteDataChgLoc(a.get(0));
                                if (sessionManager.checkEmptyChgLoc()) {
                                        TotalUpload++;
                                    }
//                                else{
//                                    sessionManager.setKeySystemDataDate(yestPreDate);
//                                    onBackPressed();

//                                    Toast.makeText(ValidateDataUpload.this, "Not Updated? Try again", Toast.LENGTH_SHORT).show();

//                                }
                            }
                        });
//                    a.get(0);
                    }

                    ifSuccessThanQuit();


                }else{
                    Toast.makeText(ValidateDataUpload.this,"Please Connect Network",Toast.LENGTH_SHORT).show();
                }

            }

        });

    }

    private void ifSuccessThanQuit() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //if all work successfully done
                if(TotalUpload>=5){
                    SessionManager sessionManager=new SessionManager(getApplicationContext());
                    sessionManager.setKeySystemDataDate(currentDate);
                    alertDialogS.dismiss();

                    Toast.makeText(ValidateDataUpload.this, "Sucessfully Updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ValidateDataUpload.this,HomeActivity.class));


                }else{
                    ifSuccessThanQuit();
                }

            }
        }, 200);
    }


}