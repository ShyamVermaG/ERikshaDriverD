package com.SShop.ERikshaDriver;

public class Travel {

    public static String CUS_OFFLINE="0";
    public static String CUS_ONLINE="1";

    public static String REQS_ACCEPTED="1";
    public static String REQS_REJECTED="0";

    public static String TRAV_CANCELED="0";
    public static String TRAV_RUNNING="1";
    public static String TRAV_FINISHED="2";

    String ERikshaId,CustomerMode,StartLoc,DestLoc,TtlPerson,Status,OnlineUsrId,RequS,RequERiksha,Date,Price;

    public Travel(String ERikshaId, String customerMode, String startLoc, String destLoc, String ttlPerson, String status, String date, String onlineUsrId, String requS, String requERiksha,String price) {
        this.ERikshaId = ERikshaId;
        CustomerMode = customerMode;
        StartLoc = startLoc;
        DestLoc = destLoc;
        TtlPerson = ttlPerson;
        Status = status;
        Date = date;
        OnlineUsrId = onlineUsrId;
        RequS = requS;
        RequERiksha = requERiksha;
        Price = price;
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

    public String getRequS() {
        return RequS;
    }

    public void setRequS(String requS) {
        RequS = requS;
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

    public void setPrice(String price) {
        Price= price;
    }
}
