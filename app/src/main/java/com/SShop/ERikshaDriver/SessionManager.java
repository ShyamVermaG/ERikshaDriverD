package com.SShop.ERikshaDriver;

import android.content.Context;
import android.content.SharedPreferences;

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
    public static final String KEY_ALL_KEY_LOCATION = "allKeyLocation";


    public SessionManager(Context context1) {
        context = context1;
        userSession = context.getSharedPreferences("userLoginSessionERiksha", Context.MODE_PRIVATE);
        editor = userSession.edit();
    }

    public void createLoginSession(String rikshaId,String travelId,String name, String address, String mobile, String Img,String OrgImg,int ttlCapa,String currentLoc,String appLang) {
        editor.putBoolean(IS_LOGIN, true);

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

        //set control panel




        editor.apply();
        editor.commit();
    }



    //this is only for login time to only stored the mobile

    public void createLoginSession( String rikshaId) {
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_RIKSHAID, rikshaId);



        editor.apply();

        editor.commit();
    }


    public String getKeyName(){
        return userSession.getString(KEY_NAME,null);
    }
    public String getKeyAddress(){
        return userSession.getString(KEY_ADDRESS,null);
    }
    public String getKeyMobile(){
        return userSession.getString(KEY_MOBILE,null);
    }
    public String getKeyRikshaid(){
        return userSession.getString(KEY_RIKSHAID,null);
    }
    public String getKeyTravelid(){
        return userSession.getString(KEY_TRAVELID,null);
    }
    public String getKeyImg(){
        return userSession.getString(KEY_IMG,null);
    }
    public String getKeyOrgimg(){
        return userSession.getString(KEY_ORGIMG,null);
    }
    public String getKeyCurrentLoc(){
        return userSession.getString(KEY_CURRENT_LOC,null);
    }
    public int getKeyTtlCapacity(){
        return userSession.getInt(KEY_TTL_CAPACITY,-1);
    }
    public int getKeyBookedSeat(){
        return userSession.getInt(KEY_BOOKED_SEAT,-1);
    }
    public int getKeyOneKmPrice(){
        return userSession.getInt(KEY_ONE_KM_PRICE,-1);
    }
    public String getKeyAppLng(){
        return userSession.getString(KEY_APP_LANGUAGE,"0");
    }
    public Boolean getKeyHindiLanguageDownld(){
        return userSession.getBoolean(KEY_HINDI_LANGUAGE_DOWNLD,false);
    }
    public String getKeyAllKeyLocation(){
        return userSession.getString(KEY_ALL_KEY_LOCATION,null);
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

    public void setKeyName(String key){
        editor.putString(KEY_NAME,key);
        editor.commit();
        editor.apply();
    }

    public void setKeyAddress(String key){
        editor.putString(KEY_ADDRESS,key);
        editor.commit();
        editor.apply();
    }

    public void setKeyMobile(String key){
        editor.putString(KEY_MOBILE,key);
        editor.commit();
        editor.apply();
    }

    public void setKeyRikshaid(String key){
        editor.putString(KEY_RIKSHAID,key);
        editor.commit();
        editor.apply();
    }

    public void setKeyTravelid(String key){
        editor.putString(KEY_TRAVELID,key);
        editor.commit();
        editor.apply();
    }

    public void setKeyImg(String key){
        editor.putString(KEY_IMG,key);
        editor.commit();
        editor.apply();
    }

    public void setKeyOrgimg(String key){
        editor.putString(KEY_ORGIMG,key);
        editor.commit();
        editor.apply();
    }

    public void setKeyCurrentLoc(String key){
        editor.putString(KEY_CURRENT_LOC,key);
        editor.commit();
        editor.apply();
    }

    public void setKeyTtlCapacity(int key){
        editor.putInt(KEY_TTL_CAPACITY,key);
        editor.commit();
        editor.apply();
    }
    public void setKeyBookedSeat(int key){
        editor.putInt(KEY_BOOKED_SEAT,key);
        editor.commit();
        editor.apply();
    }
    public void setKeyOneKmPrice(int key){
        editor.putInt(KEY_ONE_KM_PRICE,key);
        editor.commit();
        editor.apply();
    }
    public void setKeyAppLanguage(String key){
        editor.putString(KEY_APP_LANGUAGE,key);
        editor.commit();
        editor.apply();
    }
    public void setKeyHindiLanguageDownld(Boolean key){
        editor.putBoolean(KEY_HINDI_LANGUAGE_DOWNLD,key);
        editor.commit();
        editor.apply();
    }

    public void setKeyAllKeyLocation(String key){
        editor.putString(KEY_ALL_KEY_LOCATION,key);
        editor.commit();
        editor.apply();
    }




}