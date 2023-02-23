package com.SShop.ERikshaDriver;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import javax.crypto.spec.GCMParameterSpec;

public class KeyCon {
    public static final String ERikshaCon = "ERiksha";
    public static final String ERikshaDriverCon = "ERikshaDriver";
    public static final String ERikshaDriverChgLoc = "ERikshaDriverChgLoc";

    //in control session
    public static final String ERikshaDriverPrice = "ERikshaDriverPrice";
    public static final String ERikshaDriverPriceVill = "ERikshaDriverPriceVill";
    public static final String ERikshaDriverFree = "ERikshaDriverFree";
    public static final String ERikshaDriverVaryPrice = "ERikshaDriverVaryPrice";
    public static final String NewLocToAddCon = "NewLocToAddCon";
    public static final String ERikshaDriverHelp = "ERikshaDriverHelp";
    public static final String ERikshaDriverGMapKey = "ERikshaDriverGMapKey";

    public static final String True = "1";
    public static final String False = "0";


    public static final String Mohalla = "0";
    public static final String Village = "1";


    public class NewLocToAdd{
        public static final String ERikshaId = "ERikshaId";
        public static final String LocationAddr = "LocationAddr";
        public static final String LocationName = "LocationName";
        public static final String Date = "Date";

    }


    public class ERiksha {
        //these are the parameters of Users

        public static final String Name = "name";
        public static final String Address = "address";
        public static final String CurLoc = "curLoc";
        public static final String BookedSeat = "bookedSeat";
        public static final String TravelId = "travelId";
        public static final String MobNo = "mobNo";
        public static final String UsrImg = "usrImg";
        public static final String OrgImg = "orgImg";
        public static final String Date = "date";
        public static final String NotifiToken= "NotifiToken";

        public class Value{

            public static final String LNG_ENG = "en";
            public static final String LNG_HIND = "hi";
        }

    }
    public class ERikshaDriver {
        //these are the parameters of Driver

        public static final String Name = "name";
        public static final String TtlCap = "ttlCap";
        public static final String Address = "address";
        public static final String CurLoc = "curLoc";
        public static final String BookedSeat = "bookedSeat";
        public static final String TravelId = "travelId";
        public static final String MobNo = "mobNo";
        public static final String UsrImg = "usrImg";
        public static final String OrgImg = "orgImg";
        public static final String Password = "password";
        public static final String Date = "date";
        public static final String RikshaStatus= "riskhaStatus";
        public static final String NotifiToken= "notifiToken";
        public static final String DriverOnline= "driverOnline";

        public class Value{

            public static final String Rik_Stat_Run = "Running";
            public static final String Rik_Stat_Free = "Free";
            public static final String Rik_Stat_Off = "Off";

            public static final String LNG_ENG = "en";
            public static final String LNG_HIND = "hi";
        }

    }

    public class ChgLoc {
        //these are the parameters of ChgLoc


        public static final String RikshaId = "rikshaId";
        public static final String Fetched = "fetched";
        public static final String Selected = "selected";

    }

    public static final String ERikshaTranspCon = "ERikshaTransPort";

    public static class TransCon {
        public static class Value {


            public static class ValueContainer {
                public static final String weight50 = "1";
                public static final String weight100 = "2";
                public static final String weight200 = "3";
                public static final String weight300 = "4";
                public  static String getWeight(String stat){
                    switch(stat){
                        case ValueContainer.weight50:{
                            return "50 weight";
                        }
                        case ValueContainer.weight100:{
                            return "100 weight";
                        }
                        case ValueContainer.weight200:{
                            return "200 weight";
                        }
                        case ValueContainer.weight300:{
                            return "300 weight";
                        }
                        default:{
                            return "Null";
                        }

                    }
                }
            }
            public static class ValueTransport{

                public static final String TRAV_Initiated="0";
                public static final String TRAV_ACCEPT_DRIVER="1";
                public static final String TRAV_NOT_ACCEPT_DRIVER="2";
                public static final String TRAV_DRIVER_REACHED="3";
                public static final String TRAV_DRIVER_NOT_REACHED="4";
                public static final String TRAV_RUNNING="5";
                public static final String TRAV_FINISHED="6";
                public static final String TRAV_CANCEL_RUNNING="7";
                public static final String TRAV_CANCEL_ANY_TIME="8";
                public static final String TRAV_CANCEL_RUNNING_DRIVER="9";


                public  static String getTravCancelReason(String stat){
                    switch(stat) {
                        case TRAV_NOT_ACCEPT_DRIVER: {
                            return "No Driver Available";
                        }
                        case TRAV_DRIVER_NOT_REACHED: {
                            return "Driver Not Reached";
                        }
                        case TRAV_CANCEL_RUNNING: {
                            return "User Cancel At RunTime";
                        }
                        case TRAV_CANCEL_ANY_TIME:{
                            return "User Cancel the Request";
                        }case TRAV_CANCEL_RUNNING_DRIVER:{
                            return "Driver Cancel the Request at Running";
                        }
                        default:{
                            return "Null";
                        }
                    }
                }
                public  static String getTravStatus(String stat){
                    switch(stat){
                        case TRAV_Initiated:{
                            return "Travel Initiated";
                        }
                        case TRAV_ACCEPT_DRIVER:{
                            return "Travel Req Accepted";
                        }
                        case TRAV_DRIVER_REACHED:{
                            return "Driver Reached";
                        }
                        case TRAV_RUNNING:{
                            return "You are Traveling  ";
                        }
                        case TRAV_FINISHED:{
                            return "Travel Finished";
                        }
                        default:{
                            return "Cancelled";
                        }
                    }
                }

            }
            //these are the values
            public static final String CUS_OFFLINE = "0";
            public static final String CUS_ONLINE = "1";

            public static final String REQS_ACCEPTED = "1";
            public static final String REQS_REJECTED = "0";

            public static final String PassType_Container = "1";
            public static final String PassType_Passenger = "0";

            public static final String TRAV_CANCELED = "0";
            public static final String TRAV_RUNNING = "1";
            public static final String TRAV_FINISHED = "2";

            public static final String LNG_ENG = "0";
            public static final String LNG_HIND = "1";
        }

        //these are the parameters of Travel Id

        public static final String ERikshaID = "erikshaId";
        public static final String CustomerMode = "customerMode";
        public static final String StartLoc = "startLoc";
        public static final String DestLoc = "destLoc";
        public static final String TtlPerson = "ttlPerson";
        public static final String PassengerType = "passengerType";
        public static final String Status = "status";
        public static final String OnlineUsrId = "onlineUsrId";
        public static final String OnlineUsrToken = "onlineUsrToken";
        public static final String RequS = "requS";
        public static final String RequERiksha = "requERiksha";
        public static final String Date = "date";
        public static final String Price = "price";
        public static final String NotPaid = "notPaid";
        public static final String Version = "version";
        public static final String NewUser = "newUser";

    }

    public static final String ERikshaLoc = "ERikshaLocation";




    //for conver locationlist to string

    public static String convertDefaultLocToStr(ArrayList<String> locAddrName, ArrayList<String> locAddr) {
        String maindData="";
        String data="";
        for(int i=0;i<locAddr.size();i++){
            data="";
            data+=locAddrName.get(i);
            data+=":";
            data+=locAddr.get(i);
            //this will seperate two locations
            maindData+=data+"@@";
        }

        return maindData;
    }


    public static ArrayList<ArrayList<String>> convertStrToDefaultLoc(String data) {
        ArrayList<ArrayList<String>> listR=new ArrayList<>();
        ArrayList<String> locationAddr=new ArrayList<>();
        ArrayList<String> locationAddrName=new ArrayList<>();


        ArrayList<String> tamp=new ArrayList<>();

        tamp.addAll(Arrays.asList(data.split("@@")));

        String []strL=new String[2];
        //for each location
        for(String tt:tamp){
            //here we take string and split it by locations

            strL=tt.split(":");
            locationAddrName.add(strL[0]);
            locationAddr.add(strL[1]);
        }

        listR.add(locationAddrName);
        listR.add(locationAddr);

        return listR;
    }


    //for custom marker
    static BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorId){
        Drawable vector= ContextCompat.getDrawable(context,vectorId);
        vector.setBounds(0,0,vector.getIntrinsicWidth(),vector.getIntrinsicHeight());

        Bitmap bitmap= Bitmap.createBitmap(vector.getIntrinsicWidth(),vector.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        vector.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    public static LatLng strToLatLng(String str) {

        String[] latlong = new String[1];
        if(str!=null){
            latlong = str.split(",");
        }

        return new LatLng(Double.parseDouble(latlong[0]), Double.parseDouble(latlong[1]));

    }


    public static Location strToLocation(String str){
        Location des = new Location(LocationManager.GPS_PROVIDER);

        String[] latlong = new String[1];
        if(str!=null) {
            latlong = str.split(",");
            des.setLatitude(Double.parseDouble(latlong[0]));
            des.setLongitude(Double.parseDouble(latlong[1]));
        }
        return des;
    }

    public static String locToString(Location location){
        return location.getLatitude()+","+location.getLongitude();
    }

    public static LatLng locToLatLng(Location location){
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    public static Location latlngToLoc(LatLng latLng){
        Location location=new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return location;
    }


    static TextToSpeech tts;

    public static void speakStr(String str,Context context){

        SessionManager sessionManager=new SessionManager(context);
        if(!sessionManager.getKeySpeak()){
            return;
        }
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int res = 0;
                    //setting up text language
                    SessionManager sessionManager = new SessionManager(context);
                    if (sessionManager.getKeyAppLng().equals(ERikshaDriver.Value.LNG_ENG)) {
                        res = tts.setLanguage(Locale.ENGLISH);
                    } else {
                        res = tts.setLanguage(Locale.forLanguageTag("hi"));

                    }

//                    int result=tts.setLanguage(Locale.)
                    if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(context, "Language Not supp", Toast.LENGTH_SHORT).show();
                    } else {
                        tts.setPitch(0.8f);
                        tts.setSpeechRate(0.8f);
                        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }
        });

    }
    //for textView on longPress it activated
    public static void speakTVLP(TextView view,Context context) {

        SessionManager sessionManager=new SessionManager(context);
        if(!sessionManager.getKeySpeak()){
            return;
        }
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int res = 0;
                    //setting up text language
                    SessionManager sessionManager = new SessionManager(context);
                    if (sessionManager.getKeyAppLng().equals(ERikshaDriver.Value.LNG_ENG)) {
                        res = tts.setLanguage(Locale.ENGLISH);
                    } else {
                        res = tts.setLanguage(Locale. forLanguageTag("hin"));

                    }

//                    int result=tts.setLanguage(Locale.)
                    if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(context, "Language Not supp", Toast.LENGTH_SHORT).show();
                    } else {
                        tts.setPitch(0.8f);
                        tts.setSpeechRate(0.8f);
                        tts.speak(view.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }
        });

    }










}
