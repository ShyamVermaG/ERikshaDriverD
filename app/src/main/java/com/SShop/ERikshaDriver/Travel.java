package com.SShop.ERikshaDriver;

import com.SShop.ERikshaDriver.KeyCon;

public class Travel {

    public static String CUS_OFFLINE="0";
    public static String CUS_ONLINE="1";


    //for id is null request is not accepted
    public static String ID_NULL="Null";




    String ERikshaId,CustomerMode,StartLoc,DestLoc,TtlPerson,Status,PassengerType,OnlineUsrId,OnlineUsrToken,RequERiksha,Date,Price,NotPaid,Version;

    public Travel( String customerMode, String startLoc, String destLoc, String ttlPerson, String passengerType,String status, String date, String onlineUsrId, String onlineUsrToken, String requERiksha) {
        this.ERikshaId = "Null";
        CustomerMode = customerMode;
        StartLoc = startLoc;
        DestLoc = destLoc;
        TtlPerson = ttlPerson;
        PassengerType = passengerType;
        Status = status;
        Date = date;
        OnlineUsrId = onlineUsrId;
        OnlineUsrToken = onlineUsrToken;
        RequERiksha = requERiksha;
        Price = "0";
        NotPaid = KeyCon.False;
        Version="1.0";
    }
    public Travel(String ERikshaId, String startLoc, String destLoc, String ttlPerson, String passengerType, String date) {
        this.ERikshaId = ERikshaId;
        CustomerMode = KeyCon.TransCon.Value.CUS_OFFLINE;
        StartLoc = startLoc;
        DestLoc = destLoc;
        TtlPerson = ttlPerson;
        PassengerType = passengerType;
        Status = KeyCon.TransCon.Value.ValueTransport.TRAV_RUNNING;
        Date = date;
        OnlineUsrId = "";
        RequERiksha = "";
        Price = "1.0F";
        NotPaid = KeyCon.False;
        Version="1.0";
    }

    public String getERikshaId() {
        return ERikshaId;
    }

    public void setERikshaId(String ERikshaId) {
        this.ERikshaId = ERikshaId;
    }

    public String getCustomerMode() {
        return CustomerMode;
    }

    public void setCustomerMode(String customerMode) {
        CustomerMode = customerMode;
    }

    public String getStartLoc() {
        return StartLoc;
    }

    public void setStartLoc(String startLoc) {
        StartLoc = startLoc;
    }

    public String getDestLoc() {
        return DestLoc;
    }

    public void setDestLoc(String destLoc) {
        DestLoc = destLoc;
    }

    public String getTtlPerson() {
        return TtlPerson;
    }

    public void setTtlPerson(String ttlPerson) {
        TtlPerson = ttlPerson;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getOnlineUsrId() {
        return OnlineUsrId;
    }

    public void setOnlineUsrId(String onlineUsrId) {
        OnlineUsrId = onlineUsrId;
    }

    public String getOnlineUsrToken() {
        return OnlineUsrToken;
    }

    public void setOnlineUsrToken(String onlineUsrToken) {
        OnlineUsrToken = onlineUsrToken;
    }

    public String getRequERiksha() {
        return RequERiksha;
    }

    public void setRequERiksha(String requERiksha) {
        RequERiksha = requERiksha;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getPrice() {
        return Price;
    }

    public String getNotPaid() {
        return NotPaid;
    }

    public void setNotPaid(String notPaid) {
        NotPaid = notPaid;
    }

    public String getPassengerType() {
        return PassengerType;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public void setPassengerType(String passengerType) {
        PassengerType = passengerType;
    }

    public void setPrice(String price) {
        Price= price;
    }
}