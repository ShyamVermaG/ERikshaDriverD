package com.SShop.ERikshaDriver;

public class SignUpData {
    String name,ttlCap,address,curLoc,bookedSeat,travelId,mobNo,usrImg,orgImg,password,date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTtlCap() {
        return ttlCap;
    }

    public void setTtlCap(String ttlCap) {
        this.ttlCap = ttlCap;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCurLoc() {
        return curLoc;
    }

    public void setCurLoc(String curLoc) {
        this.curLoc = curLoc;
    }

    public String getBookedSeat() {
        return bookedSeat;
    }

    public void setBookedSeat(String bookedSeat) {
        this.bookedSeat = bookedSeat;
    }


    public String getTravelId() {
        return travelId;
    }

    public void setTravelId(String travelId) {
        this.travelId = travelId;
    }

    public String getMobNo() {
        return mobNo;
    }

    public void setMobNo(String mobNo) {
        this.mobNo = mobNo;
    }

    public String getUsrImg() {
        return usrImg;
    }

    public void setUsrImg(String usrImg) {
        this.usrImg = usrImg;
    }

    public String getOrgImg() {
        return orgImg;
    }

    public void setOrgImg(String orgImg) {
        this.orgImg = orgImg;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public SignUpData(String name, String ttlCap, String address, String curLoc, String bookedSeat, String travelId, String mobNo, String usrImg, String orgImg, String password,String date) {
        this.name = name;
        this.ttlCap = ttlCap;
        this.address = address;
        this.curLoc = curLoc;
        this.bookedSeat = bookedSeat;
        this.travelId = travelId;
        this.mobNo = mobNo;
        this.usrImg = usrImg;
        this.orgImg = orgImg;
        this.password = password;
        this.date = date;
    }
}
