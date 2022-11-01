package com.SShop.ERikshaDriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {


    Switch switchL;

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

    TextView person1,person2,person3,person4,chgLng,addPerson;
    LinearLayout add1Pers,add2Pers,add3Pers,add4Pers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        Locale locale=new Locale("hi","IN");

        person1=findViewById(R.id.person1);
        person2=findViewById(R.id.person2);
        person3=findViewById(R.id.person3);
        person4=findViewById(R.id.person4);
        chgLng=findViewById(R.id.chgLng);
        addPerson=findViewById(R.id.addPerson);

        add1Pers=findViewById(R.id.add1Pers);
        add2Pers=findViewById(R.id.add2Pers);
        add3Pers=findViewById(R.id.add3Pers);
        add4Pers=findViewById(R.id.add4Pers);

        add1Pers.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakStr(getString(R.string.add)+person1.getText().toString());
                return false;
            }
        });

        add2Pers.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakStr(getString(R.string.add)+person2.getText().toString());
                return false;
            }
        });
        add3Pers.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakStr(getString(R.string.add)+person3.getText().toString());
                return false;
            }
        });
        add4Pers.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakStr(getString(R.string.add)+person4.getText().toString());
                return false;
            }
        });



        chgLng.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(chgLng);
                return false;
            }
        });
        addPerson.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(addPerson);
                return false;
            }
        });
        person1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(person1);
                return false;
            }
        });
        person2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(person2);
                return false;
            }
        });
        person3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(person3);
                return false;
            }
        });
        person4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                speakTVLP(person4);
                return false;
            }
        });
        SessionManager sessionManager=new SessionManager(this);
        switchL=findViewById(R.id.switchL);

        if(sessionManager.getKeyAppLng()== KeyCon.ERikshaDriver.Value.LNG_ENG){
            changeLanguage("en");
            switchL.setChecked(false);
        }else{

            //hindi
            changeLanguage("hi");
            switchL.setChecked(true);
        }


        switchL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                sessionManager.setKeyAppLanguage(b ? KeyCon.ERikshaDriver.Value.LNG_HIND: KeyCon.ERikshaDriver.Value.LNG_ENG);

                if(b){
                    changeLanguage(KeyCon.ERikshaDriver.Value.LNG_HIND);
                    sessionManager.setKeyAppLanguage(KeyCon.ERikshaDriver.Value.LNG_HIND);
                }else{
                    changeLanguage(KeyCon.ERikshaDriver.Value.LNG_ENG);
                    sessionManager.setKeyAppLanguage(KeyCon.ERikshaDriver.Value.LNG_ENG);
                }
            }
        });
    }

    private void changeLanguage(String langCode) {
        Resources resources=getResources();
        Configuration configuration=resources.getConfiguration();
//        Locale locale=new Locale(langCode);
//        Locale.setDefault(locale);
        configuration.locale=new Locale(langCode);
        resources.updateConfiguration(configuration,resources.getDisplayMetrics());
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
//        Toast.makeText(this, "lang change", Toast.LENGTH_SHORT).show();

    }

    public void gotoPassenList(View view) {
        Intent intent=new Intent(this,passenger.class);
        startActivity(intent);
    }

    public void gotoRoute(View view) {
        Intent intent=new Intent(this,RouteERiksha.class);
        startActivity(intent);
    }

    public void add1Person(View view) {

        Intent intent=new Intent(this,onePerson.class);
        intent.setData(Uri.parse("1"));
        startActivity(intent);
    }

    public void add2Person(View view) {
        Intent intent=new Intent(this,onePerson.class);
        intent.setData(Uri.parse("2"));
        startActivity(intent);
    }

    public void add3Person(View view) {
        Intent intent=new Intent(this,onePerson.class);
        intent.setData(Uri.parse("3"));
        startActivity(intent);
    }

    public void add4Person(View view) {
        Intent intent=new Intent(this,onePerson.class);
        intent.setData(Uri.parse("4"));
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        SessionManager sessionManager=new SessionManager(this);
        if(sessionManager.checkLogin()){

            //check for the version

            FirebaseDatabase.getInstance().getReference().child("Control").child(KeyCon.ERikshaDriverCon).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot snapshot) {
                    String blockValue=snapshot.child("Block").getValue(String.class);
                    String updateValue=snapshot.child("Update").getValue(String.class);
                    Boolean verAvail=true;

                    //finding that version is available or not
                    ArrayList<String> availableVer=new ArrayList<>();
                    availableVer.addAll(Arrays.asList(updateValue.split("<>")));
                    for(String ver:availableVer){
                        if(ver.equals(getApplicationContext().getString(R.string.versionName))){
                            verAvail=false;
                        }
                    }

                    if (blockValue != null && updateValue!=null)
                        if (blockValue.equals("true")) {
                            startActivity(new Intent(getApplicationContext(), BlockActivity.class));
                            finish();
                        } else if (verAvail) {
                            startActivity(new Intent(getApplicationContext(), UpdateActivity.class));
                            finish();
                        }




                }
            });


//setting up price
            FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaDriverPrice).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        sessionManager.setKeyOneKmPrice(Integer.parseInt(Objects.requireNonNull(dataSnapshot.getValue(String.class))));
                    }
                }
            });


            //setting up all default location
ArrayList<String> locAddr=new ArrayList<>();
ArrayList<String> locAddrName=new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child(KeyCon.ERikshaLoc).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            locAddr.add(data.getValue(String.class));
                            locAddrName.add(data.getKey());
                        }

                        String data=KeyCon.convertDefaultLocToStr(locAddrName,locAddr);
                        sessionManager.setKeyAllKeyLocation(data);
//                        Toast.makeText(HomeActivity.this, ""+data, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "No Default Location found", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            startActivity(new Intent(this,LoginFront.class));
            finish();
        }
    }


    public void gotoPenList(View view) {
        startActivity(new Intent(this,passenger.class));

    }
}