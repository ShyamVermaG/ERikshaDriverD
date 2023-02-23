package com.SShop.ERikshaDriver;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SessionManager {
    SharedPreferences userSession;
    SharedPreferences.Editor editor;
    Context context;

    //these are all key name only
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_RIKSHAID = "rikshaId";
    public static final String KEY_TRAVELID = "travelId";
    public static final String KEY_NAME = "Name";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_IMG = "Img";
    public static final String KEY_ORGIMG = "OrgImg";
    public static final String KEY_TTL_CAPACITY = "ttlCapacity";
    public static final String KEY_CURRENT_LOC = "currentLoc";
    public static final String KEY_BOOKED_SEAT = "bookedSeat";
    public static final String KEY_APP_LANGUAGE = "keyLanguage";
    public static final String KEY_HINDI_LANGUAGE_DOWNLD = "HindiLanguageDownld";
    public static final String KEY_ONE_KM_PRICE = "oneKmPrice";
    public static final String KEY_ONE_KM_PRICE_VILL = "oneKmPriceVillage";
    public static final String KEY_ALL_KEY_LOCATION = "allKeyLocation";
    public static final String KEY_TRAVEL_DATA = "TravelData";
    public static final String KEY_CHG_LOC = "changeLocation";
    public static final String KEY_VaryPrice = "varyPrice";
    public static final String KEY_SYSTEM_DATA_DATE = "system_data_update";
    public static final String KEY_SPEAK = "speakAudio";
    public static final String KEY_GMAP_KEY = "GMAP_KEY";

    public static final String KEY_RikshaStatus = "riskhaStatus";
    public static final String KEY_RikshaStatusUpdated = "riskhaStatusUpdated";
    public static final String KEY_NotifiToken = "notifiToken";


    public static final String KEY_LAST_LOC = "GMAP_KEY";
    public static final String KEY_LAST_ORD = "LstOrd";


    public SessionManager(Context context1) {
        context = context1;
        userSession = context.getSharedPreferences("userLoginSessionERikshaDriver", Context.MODE_PRIVATE);
//        editor.putString(KEY_RikshaStatus, KeyCon.ERikshaDriver.Value.Rik_Stat_Off);
        editor = userSession.edit();
    }

    public void createLoginSession(String rikshaId, String travelId, String name, String address, String mobile, String Img, String OrgImg, int ttlCapa, String currentLoc, String appLang) {
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_RikshaStatus, KeyCon.ERikshaDriver.Value.Rik_Stat_Off);
        editor.putString(KEY_RIKSHAID, rikshaId);
        editor.putString(KEY_TRAVELID, travelId);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_ADDRESS, address);
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_IMG, Img);
        editor.putString(KEY_ORGIMG, OrgImg);
        editor.putInt(KEY_TTL_CAPACITY, ttlCapa);
        editor.putString(KEY_CURRENT_LOC, currentLoc);
        editor.putString(KEY_APP_LANGUAGE, appLang);
        editor.putInt(KEY_BOOKED_SEAT, 0);
        editor.putInt(KEY_ONE_KM_PRICE, 5);
        editor.putBoolean(KEY_HINDI_LANGUAGE_DOWNLD, false);

        editor.putString(KEY_SYSTEM_DATA_DATE, "");
        editor.putBoolean(KEY_SPEAK, false);
        //set control panel


        editor.apply();
        editor.commit();
    }


    //this is only for login time to only stored the mobile

    public void createLoginSession(String rikshaId) {
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_RIKSHAID, rikshaId);
        editor.putString(KEY_RikshaStatus, KeyCon.ERikshaDriver.Value.Rik_Stat_Off);


        editor.apply();

        editor.commit();
    }


    public String getKeyName() {
        return userSession.getString(KEY_NAME, null);
    }

    public String getKeyAddress() {
        return userSession.getString(KEY_ADDRESS, null);
    }

    public String getKeyMobile() {
        return userSession.getString(KEY_MOBILE, null);
    }

    public String getKeyRikshaid() {
        return userSession.getString(KEY_RIKSHAID, null);
    }

    public String getKeyTravelid() {
        return userSession.getString(KEY_TRAVELID, null);
    }

    public String getKeyImg() {
        return userSession.getString(KEY_IMG, null);
    }

    public String getKeyOrgimg() {
        return userSession.getString(KEY_ORGIMG, null);
    }

    public String getKeyCurrentLoc() {
        return userSession.getString(KEY_CURRENT_LOC, "22.12121,21.32323");
    }

    public int getKeyTtlCapacity() {
        return userSession.getInt(KEY_TTL_CAPACITY, -1);
    }

    public int getKeyBookedSeat() {
        return userSession.getInt(KEY_BOOKED_SEAT, -1);
    }

    public int getKeyOneKmPrice() {
        return userSession.getInt(KEY_ONE_KM_PRICE, -1);
    }

    public String getKeyAppLng() {
        return userSession.getString(KEY_APP_LANGUAGE, "0");
    }

    public Boolean getKeyHindiLanguageDownld() {
        return userSession.getBoolean(KEY_HINDI_LANGUAGE_DOWNLD, false);
    }

    public String getKeyAllKeyLocation() {
        return userSession.getString(KEY_ALL_KEY_LOCATION, null);
    }

    public String getKEY_VaryPrice() {
        return userSession.getString(KEY_VaryPrice, null);
    }

    public String getKeySystemDataDate() {
        return userSession.getString(KEY_SYSTEM_DATA_DATE, "");
    }

    public Boolean getKeySpeak() {
        return userSession.getBoolean(KEY_SPEAK, true);
    }

    public String getKeyGMAP_KEY() {
        return userSession.getString(KEY_GMAP_KEY, "");
    }

    public int getKeyOneKmPriceVill() {
        return userSession.getInt(KEY_ONE_KM_PRICE_VILL, -1);
    }

    public String getKeyLastLoc() {
        return userSession.getString(KEY_LAST_LOC, "");
    }

    public String getKEY_RikshaStatus() {
        return userSession.getString(KEY_RikshaStatus, "");
    }

    public String getKEY_NotifiToken() {
        return userSession.getString(KEY_NotifiToken, "");
    }

    public Boolean getKEY_RikshaStatusUpdated() {
        return userSession.getBoolean(KEY_RikshaStatusUpdated, false);
    }
    public String getLstOrd() {
        return userSession.getString(KEY_LAST_ORD, "2323323");
    }


    public HashMap<String, String> getLoginSession() {
        HashMap<String, String> userData = new HashMap<String, String>();


        userData.put(KEY_NAME, userSession.getString(KEY_NAME, null));
        userData.put(KEY_ADDRESS, userSession.getString(KEY_ADDRESS, null));
        userData.put(KEY_MOBILE, userSession.getString(KEY_MOBILE, null));
        userData.put(KEY_RIKSHAID, userSession.getString(KEY_RIKSHAID, null));
        userData.put(KEY_TRAVELID, userSession.getString(KEY_TRAVELID, null));
        userData.put(KEY_IMG, userSession.getString(KEY_IMG, "https://firebasestorage.googleapis.com/v0/b/myapplication-1214d.appspot.com/o/ic_baseline_image_24.xml?alt=media&token=cf0637bc-efa3-4668-92a2-20b7cf89aa52"));
        userData.put(KEY_ORGIMG, userSession.getString(KEY_ORGIMG, null));
        userData.put(KEY_APP_LANGUAGE, userSession.getString(KEY_APP_LANGUAGE, "-1"));
        userData.put(KEY_TTL_CAPACITY, String.valueOf(userSession.getInt(KEY_TTL_CAPACITY, -1)));
        userData.put(KEY_BOOKED_SEAT, String.valueOf(userSession.getInt(KEY_BOOKED_SEAT, -1)));
        userData.put(KEY_CURRENT_LOC, userSession.getString(KEY_CURRENT_LOC, null));

//        https://firebaestorage.googleapis.com/v0/b/myapplication-1214d.appspot.com/o/ProfileImg%2F1622528320749.jpeg?alt=media&token=5e3de043-db70-46d6-826c-fd7cc59ef1df
        return userData;
    }

    public boolean checkLogin() {
        return userSession.getBoolean(IS_LOGIN, false);
    }

    public void logoutUserFromSession() {
        editor.clear();
        editor.apply();

        editor.commit();
    }


    //setters

    public void setKeyName(String key) {
        editor.putString(KEY_NAME, key);
        editor.commit();
        editor.apply();
    }

    public void setKeyAddress(String key) {
        editor.putString(KEY_ADDRESS, key);
        editor.commit();
        editor.apply();
    }

    public void setKeyMobile(String key) {
        editor.putString(KEY_MOBILE, key);
        editor.commit();
        editor.apply();
    }

    public void setKeyRikshaid(String key) {
        editor.putString(KEY_RIKSHAID, key);
        editor.commit();
        editor.apply();
    }

    public void setKeyTravelid(String key) {
        editor.putString(KEY_TRAVELID, key);
        editor.commit();
        editor.apply();
    }

    public void setKeyImg(String key) {
        editor.putString(KEY_IMG, key);
        editor.commit();
        editor.apply();
    }

    public void setKeyOrgimg(String key) {
        editor.putString(KEY_ORGIMG, key);
        editor.commit();
        editor.apply();
    }

    public void setKeyCurrentLoc(String key) {
        editor.putString(KEY_CURRENT_LOC, key);
        editor.commit();
        editor.apply();
    }

    public void setKeyTtlCapacity(int key) {
        editor.putInt(KEY_TTL_CAPACITY, key);
        editor.commit();
        editor.apply();
    }

    public void setKeyBookedSeat(int key) {
        editor.putInt(KEY_BOOKED_SEAT, key);
        editor.commit();
        editor.apply();
    }

    public void setKeyOneKmPrice(int key) {
        editor.putInt(KEY_ONE_KM_PRICE, key);
        editor.commit();
        editor.apply();
    }

    public void setKeyAppLanguage(String key) {
        editor.putString(KEY_APP_LANGUAGE, key);
        editor.commit();
        editor.apply();
    }

    public void setKeyHindiLanguageDownld(Boolean key) {
        editor.putBoolean(KEY_HINDI_LANGUAGE_DOWNLD, key);
        editor.commit();
        editor.apply();
    }

    public void setKeyAllKeyLocation(String key) {
        editor.putString(KEY_ALL_KEY_LOCATION, key);
        editor.commit();
        editor.apply();
    }

    public void setKEY_VaryPrice(String key) {
        editor.putString(KEY_VaryPrice, key);
        editor.commit();
        editor.apply();
    }

    public void setKeySystemDataDate(String key) {
        editor.putString(KEY_SYSTEM_DATA_DATE, key);
        editor.commit();
        editor.apply();
    }

    public void setKeySpeak(Boolean key) {
        editor.putBoolean(KEY_SPEAK, key);
        editor.commit();
        editor.apply();
    }

    public void setKeyGmapKey(String key) {
        editor.putString(KEY_GMAP_KEY, key);
        editor.commit();
        editor.apply();
    }

    public void setKeyOneKmPriceVill(int key) {
        editor.putInt(KEY_ONE_KM_PRICE_VILL, key);
        editor.commit();
        editor.apply();
    }

    public void setKeyLastLoc(String key) {
        editor.putString(KEY_LAST_LOC, key);
        editor.commit();
        editor.apply();
    }

    public void setKEY_RikshaStatus(String key) {
        editor.putString(KEY_RikshaStatus, key);
        editor.commit();
        editor.apply();
    }

    public void setKEY_NotifiToken(String key) {
        editor.putString(KEY_NotifiToken, key);
        editor.commit();
        editor.apply();
    }


    public void setKEY_RikshaStatusUpdated(Boolean key) {
        editor.putBoolean(KEY_RikshaStatusUpdated, key);
        editor.commit();
        editor.apply();
    }

    public void setKEY_LstOrd(String key) {
        editor.putString(KEY_LAST_ORD, key);
        editor.commit();
        editor.apply();
    }


    //update booked seat and status of eriksha
    public void subBookedSeat(int key) {

        int seatsB = userSession.getInt(KEY_BOOKED_SEAT, 0);

        if (seatsB <= key) {
            //if seat is release is more than booked or all seat going to off then free the riksha
            editor.putInt(KEY_BOOKED_SEAT, 0);
            editor.putString(KEY_RikshaStatus, KeyCon.ERikshaDriver.Value.Rik_Stat_Free);
        } else {
            seatsB -= key;
            editor.putInt(KEY_BOOKED_SEAT, seatsB);
            editor.putString(KEY_RikshaStatus, KeyCon.ERikshaDriver.Value.Rik_Stat_Run);
        }


        //set the status to update is false
        editor.putBoolean(KEY_RikshaStatusUpdated, false);

        editor.commit();
        editor.apply();

    }

    //for adding seat
    public void addBookedSeat(int key) {

        int seatsB = userSession.getInt(KEY_BOOKED_SEAT, 0);

        seatsB += key;
        editor.putInt(KEY_BOOKED_SEAT, seatsB);
        editor.putString(KEY_RikshaStatus, KeyCon.ERikshaDriver.Value.Rik_Stat_Run);

        //set the status to update is false
        editor.putBoolean(KEY_RikshaStatusUpdated, false);


        editor.commit();
        editor.apply();

    }


    //<--------------free to used-------------------->
    private ArrayList<ArrayList<String>> getDataInList(String string) {

        ArrayList<String> list1 = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();
        ArrayList<ArrayList<String>> listT = new ArrayList<ArrayList<String>>();

        //if string null
        if (string.equals(""))
            return listT;


        list1.addAll(Arrays.asList(string.split("<>")));

        for (String a : list1) {
            list2 = new ArrayList<>();
            list2.addAll(Arrays.asList(a.split("@@")));

            //adding data to top list
            listT.add(list2);


        }

        return listT;
    }


    //<--------------Only for Travel Data-------------------->

    //here is all for creating new data format
    public ArrayList<ArrayList<String>> showDataTravel() {


        String string = "";
        string = userSession.getString(KEY_TRAVEL_DATA, "");

        ArrayList<ArrayList<String>> listT = new ArrayList<ArrayList<String>>();
        listT = getDataInList(string);

        return listT;
    }

    public void chgStatusDestDataTravel(String id, String status, String dest) {
        String string = "";

        //fetchdata from session
        string = userSession.getString(KEY_TRAVEL_DATA, "");

        ArrayList<ArrayList<String>> listT = new ArrayList<ArrayList<String>>();
        listT = getDataInList(string);


        string = "";

        //here we traverse all araay if we found element than we dont add it
        for (int i = 0; i < listT.size(); i++) {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(listT.get(i).get(0));
            stringBuilder.append("@@");
            stringBuilder.append(listT.get(i).get(1));
            stringBuilder.append("@@");

            //only for status changing
            if (listT.get(i).get(0).equals(id)) {
                //update the value /add new value
                stringBuilder.append(dest);
                stringBuilder.append("@@");

            } else {

                stringBuilder.append(listT.get(i).get(2));
                stringBuilder.append("@@");

            }

            stringBuilder.append(listT.get(i).get(3));
            stringBuilder.append("@@");


            //only for status changing
            if (listT.get(i).get(0).equals(id)) {
                //update the value /add new value
                stringBuilder.append(status);
                stringBuilder.append("@@");

            } else {

                stringBuilder.append(listT.get(i).get(4));
                stringBuilder.append("@@");

            }

            stringBuilder.append(listT.get(i).get(5));
            stringBuilder.append("@@");


            string += stringBuilder.toString();
            string += "<>";
        }


        editor.putString(KEY_TRAVEL_DATA, string);
        editor.commit();
        editor.apply();
    }

    //here is all for inserting data format
    public void addDataTravel(String UploadId, String StartLoc, String DestLoc, String TtlPerson, String passengerType, String Status) {
        String string = "";
        //fetchdata
        string = userSession.getString(KEY_TRAVEL_DATA, "");


        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(UploadId);
        stringBuilder.append("@@");

        stringBuilder.append(StartLoc);
        stringBuilder.append("@@");
        stringBuilder.append(DestLoc);
        stringBuilder.append("@@");
        stringBuilder.append(TtlPerson);
        stringBuilder.append("@@");
        stringBuilder.append(passengerType);
        stringBuilder.append("@@");
        stringBuilder.append(Status);
        stringBuilder.append("@@");

        string += stringBuilder.toString();
        string += "<>";


        editor.putString(KEY_TRAVEL_DATA, string);
        editor.commit();
        editor.apply();
    }


    public void deleteDataTravel(String id) {
        String string = "";

        //fetchdata from session
        string = userSession.getString(KEY_TRAVEL_DATA, "");

        ArrayList<ArrayList<String>> listT = new ArrayList<ArrayList<String>>();
        listT = getDataInList(string);


        string = "";

        //here we traverse all araay if we found element than we dont add it
        for (int i = 0; i < listT.size(); i++) {


            if (listT.get(i).get(0).equals(id)) {

            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(listT.get(i).get(0));
                stringBuilder.append("@@");
                stringBuilder.append(listT.get(i).get(1));
                stringBuilder.append("@@");
                stringBuilder.append(listT.get(i).get(2));
                stringBuilder.append("@@");
                stringBuilder.append(listT.get(i).get(3));
                stringBuilder.append("@@");
                stringBuilder.append(listT.get(i).get(4));
                stringBuilder.append("@@");
                stringBuilder.append(listT.get(i).get(5));
                stringBuilder.append("@@");

                string += stringBuilder.toString();
                string += "<>";
            }
        }


        editor.putString(KEY_TRAVEL_DATA, string);
        editor.commit();
        editor.apply();
    }


    //<----------------All for changesInLoc ------------------>

    //here is all for creating new data format
    public ArrayList<ArrayList<String>> showDataChgLoc() {

        String string = "";
        string = userSession.getString(KEY_CHG_LOC, "");

        ArrayList<ArrayList<String>> listT = new ArrayList<ArrayList<String>>();
        listT = getDataInList(string);

        return listT;
    }

    //here is all for inserting data format
    public void addDataChgLoc(String uploadId, String fatched, String selected) {
        String string = "";
        //fetchdata
        string = userSession.getString(KEY_CHG_LOC, "");

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(uploadId);
        stringBuilder.append("@@");
        stringBuilder.append(fatched);
        stringBuilder.append("@@");
        stringBuilder.append(selected);
        stringBuilder.append("@@");


        string += stringBuilder.toString();
        string += "<>";


        editor.putString(KEY_CHG_LOC, string);
        editor.commit();
        editor.apply();
    }


    public Boolean checkEmptyChgLoc() {
        if (userSession.getString(KEY_CHG_LOC, "").equals("")) {
            return true;
        }
        return false;

    }

    public Boolean checkEmptyTravel() {
        if (userSession.getString(KEY_TRAVEL_DATA, "").equals("")) {
            return true;
        }
        return false;

    }

    public void deleteDataChgLoc(String id) {
        String string = "";

        //fetchdata from session
        string = userSession.getString(KEY_CHG_LOC, "");


        ArrayList<ArrayList<String>> listT = new ArrayList<ArrayList<String>>();
        listT = getDataInList(string);

        string = "";


        //here we traverse all araay if we found element than we dont add it
        for (int i = 0; i < listT.size(); i++) {


            if (listT.get(i).get(0).equals(id)) {

            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(listT.get(i).get(0));
                stringBuilder.append("@@");
                stringBuilder.append(listT.get(i).get(1));
                stringBuilder.append("@@");
                stringBuilder.append(listT.get(i).get(2));
                stringBuilder.append("@@");
                stringBuilder.append(listT.get(i).get(3));
                stringBuilder.append("@@");
                stringBuilder.append(listT.get(i).get(4));
                stringBuilder.append("@@");

                string += stringBuilder.toString();
                string += "<>";
            }
        }


        editor.putString(KEY_CHG_LOC, string);
        editor.commit();
        editor.apply();
//        return string;
    }





    ArrayList<String> locName = new ArrayList<>();
    ArrayList<String> locationAddrList = new ArrayList<>();
    ArrayList<String> locationListType = new ArrayList<>();

    //initiallie loction list
    private void initLocList() {

        if (locName.isEmpty()) {
            locationListType.clear();
            locationAddrList.clear();
            locName.clear();
            //get data from session of default locaitons and keys
            String tamp = getKeyAllKeyLocation();

            ArrayList<ArrayList<String>> data = new ArrayList<>();

//        Toast.makeText(this, ""+tamp, Toast.LENGTH_SHORT).show();
            data = KeyCon.convertStrToDefaultLoc(tamp);
            ArrayList<String> locNameHE = new ArrayList<>();


            locNameHE = data.get(0);
            locationAddrList.addAll(data.get(1));

            //here we get only language data
            int lng = 0;
//            if (getKeyAppLng().equals(KeyCon.ERiksha.Value.LNG_ENG)) {
//                lng = 0;
//            }

            ArrayList<String> ta = new ArrayList<>();
            int i = 0;
            for (String a : locNameHE) {
                ta = new ArrayList<>();
                ta.addAll(Arrays.asList(a.split("<>")));

                //set mohalla or village according to location
//            if (ta.get(2).equals(KeyCon.Mohalla)) {
//                ;
//            } else {
//            }

                //for parent location
                locName.add(ta.get(lng));
                locationListType.add(ta.get(2));
                i++;
            }
        }


    }


    //for checking ner villge
    public Boolean checkNearIsVillage(Location startPo) {

        //if its not initillikwe properly nd for individul use
        initLocList();



        Boolean vill = true;


        //here we find min distance point
        float distnaceMin = 1000000;
        String areaName = "Null<>Null<>";
//        String locaMinS = "20.232323";

        Location endPo = new Location("locationB");


        float tamp = 0;
        //bcz we niglet selectlocation
        for (int i = 1; i < locationAddrList.size(); i++) {

            endPo = KeyCon.strToLocation(locationAddrList.get(i));

            tamp = startPo.distanceTo(endPo);
            if (distnaceMin >= tamp) {
                distnaceMin = tamp;
//                locaMinS = locationAddrList.get(i);
                areaName = locName.get(i);
                if (locationListType.get(i).equals("0")) {
                    vill = false;
                }
//                else{
//                    vill=true;
//                }
            }
        }


        return vill;

    }


    public String getNearLoctionName(Location startPo){
        //if its not initillikwe properly nd for individul use
        initLocList();


        //here we find min distance point
        float distnaceMin = 1000000;
        String areaName = "Null<>Null<>";
//        String locaMinS = "20.232323";

        Location endPo = new Location("locationB");


        float tamp = 0;
        //bcz we niglet selectlocation
        for (int i = 1; i < locationAddrList.size(); i++) {

            endPo = KeyCon.strToLocation(locationAddrList.get(i));

            tamp = startPo.distanceTo(endPo);
            if (distnaceMin >= tamp) {
                distnaceMin = tamp;
                areaName = locName.get(i);
            }
        }

        return areaName;

    }



    public String getNearLoctionAddress(Location startPo){
        //if its not initillikwe properly nd for individul use
        initLocList();


        //here we find min distance point
        float distnaceMin = 1000000;
        String locadd = "Null<>Null<>";
//        String locaMinS = "20.232323";

        Location endPo = new Location("locationB");


        float tamp = 0;
        //bcz we niglet selectlocation
        for (int i = 1; i < locationAddrList.size(); i++) {

            endPo = KeyCon.strToLocation(locationAddrList.get(i));

            tamp = startPo.distanceTo(endPo);
            if (distnaceMin >= tamp) {
                distnaceMin = tamp;
                locadd = locationAddrList.get(i);
            }
        }

        return locadd;

    }

    public float calculateTtlPrice(float oneP,int ttlPers,String passType,Boolean newUser){

        float ttlPrice=0;

        if(passType.equals(KeyCon.TransCon.Value.PassType_Passenger)){
            ttlPrice=oneP*ttlPers;

            if(newUser){
                ttlPrice-=oneP;
            }
        }else {
            //for container


            //for 50,100,200,300 Kg weights
            switch (ttlPers) {
                case 1: {
                    ttlPrice = oneP * 5;
                    break;
                }
                case 2: {
                    ttlPrice = oneP * 10;
                    break;
                }
                case 3: {
                    ttlPrice = oneP * 20;
                    break;
                }
                case 4: {
                    ttlPrice = oneP * 30;
                    break;
                }
            }

        }

        return ttlPrice;
    }

    public float calculateOnePersonPr(Location strt, Location end, String passengerType) {
        //first initillie loctions
        initLocList();


        float price = 0;
        float distM = strt.distanceTo(end);


        int oneP = getKeyOneKmPrice();
        int onePVill = getKeyOneKmPriceVill();
        if (passengerType == KeyCon.TransCon.Value.PassType_Container) {
            price = distM * oneP / 1000;
        } else

        if (checkNearIsVillage(strt) || checkNearIsVillage(end)) {
            //for village price no variation performed
            price = distM * onePVill / 1000;
        } else {
            price = distM * oneP / 1000;
            //verifyig for varyPrice
            String varyPriceS = getKEY_VaryPrice();


            if (varyPriceS != null) {
                int priceV = Integer.parseInt(varyPriceS);

                //min limit
                if (price < 8) {
                    price = 8;
                } else if (price > oneP + priceV) {
                    price = oneP + priceV;
                } else {
                    //nothing to do its all right
                }

            }
        }


        return price;
    }




}