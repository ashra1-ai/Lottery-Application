package com.example.projectv2.Model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class User {
    private boolean isAdmin;
    private boolean adminNotif;
    private String email;
    private String firstName;
    private String lastName;
    private boolean isOrganizer;
    private boolean organizerNotif;
    private long phoneNumber;
    private String profileImage;
    private String deviceID;
    private ArrayList<Map>  adminNotifList;
    private ArrayList<Map>  organizerNotifList;

    //partial constructor that defaults to isAdmin=false and callls the full constructor, call this partial constructor if the user is not intended to be an admin
    public User( String email,String firstName,String lastName,boolean isOrganizer,long phoneNumber,String deviceID){
        this(false,true,email,firstName,lastName,isOrganizer,true,phoneNumber,deviceID);
    }

    // Constructor
    public User(boolean isAdmin, boolean adminNotif, String email, String firstName,String lastName, boolean isOrganizer,boolean organizerNotif, long phoneNumber,String deviceID) {
        this.isAdmin = isAdmin;
        this.adminNotif = adminNotif;
        this.email = email;
        this.firstName = firstName;
        this.lastName=lastName;
        this.isOrganizer = isOrganizer;
        this.organizerNotif = organizerNotif;
        this.phoneNumber = phoneNumber;
        this.deviceID=deviceID;
        this.profileImage="";
    }

    // Getters
    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isAdminNotif() {
        return adminNotif;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return firstName+" "+lastName;
    }

    public boolean isOrganizer() {
        return isOrganizer;
    }

    public boolean isOrganizerNotif() {
        return organizerNotif;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }
    public String getProfileImage(){
        return profileImage;
    }
    public String getDeviceID(){
        return deviceID;
    }
    public ArrayList<Map> getAdminNotifList(){
        return adminNotifList;
    }
    public ArrayList<Map> getOrganizerNotifList(){
        return organizerNotifList;}

    // Setters
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setAdminNotif(boolean adminNotif) {
        this.adminNotif = adminNotif;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String firstName,String lastName) {
        this.firstName = firstName;this.lastName=lastName;
    }

    public void setOrganizer(boolean organizer) {
        isOrganizer = organizer;
    }

    public void setOrganizerNotif(boolean organizerNotif) {
        this.organizerNotif = organizerNotif;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setProfileImage(String imageLink){
        this.profileImage=imageLink;

    }
    public void setDeviceID(String deviceID){
        this.deviceID=deviceID;
    }
    public void setAdminNotifList(ArrayList<Map> adminNotifList){
        this.adminNotifList=adminNotifList;
    }
public void setOrganizerNotifList(ArrayList<Map> organizerNotifList){
        this.organizerNotifList=organizerNotifList;
}
}