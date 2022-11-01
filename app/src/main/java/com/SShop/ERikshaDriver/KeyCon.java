package com.SShop.ERikshaDriver;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;

public class KeyCon {
    public static final String ERikshaDriverCon = "ERikshaDriver";
    public static final String ERikshaDriverChgLoc = "ERikshaDriverChgLoc";
    public static final String ERikshaDriverPrice = "ERikshaDriverPrice";

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

        public class Value{

            public static final String LNG_ENG = "en";
            public static final String LNG_HIND = "hi";
        }

    }

    public static final String ERikshaTranspCon = "ERikshaTransPort";

    public class TransCon {
        public class Value {

            //these are the values
            public static final String CUS_OFFLINE = "0";
            public static final String CUS_ONLINE = "1";

            public static final String REQS_ACCEPTED = "1";
            public static final String REQS_REJECTED = "0";

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
        public static final String Status = "status";
        public static final String OnlineUsrId = "onlineUsrId";
        public static final String RequS = "requS";
        public static final String RequERiksha = "requERiksha";
        public static final String Date = "date";
        public static final String Price = "price";

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

}
