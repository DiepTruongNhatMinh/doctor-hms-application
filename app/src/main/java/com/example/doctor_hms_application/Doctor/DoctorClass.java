package com.example.doctor_hms_application.Doctor;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class DoctorClass {
    private ArrayList cert;
    private String address;
    private String date_of_birth;
    private String moblie_phone;
    private String specialist;
    private String gender;
    private String fullName;
    private String registrationProfileStatus;
    private String email;
    private String password;
    private String registrationStatus;
    private String price;
    private String workExperience;




    public DoctorClass() {
        // Default constructor required for calls to DataSnapshot.getValue(HelperClass.class)
    }

    public DoctorClass(ArrayList cert, String address, String date_of_birth, String moblie_phone, String specialist, String gender, String fullName, String registrationProfileStatus, String email, String password, String registrationStatus, String price, String workExperience) {
        this.cert = cert;
        this.address = address;
        this.date_of_birth = date_of_birth;
        this.moblie_phone = moblie_phone;
        this.specialist = specialist;
        this.gender = gender;
        this.fullName = fullName;
        this.registrationProfileStatus = registrationProfileStatus;
        this.email = email;
        this.registrationStatus = registrationStatus;
        this.password = password;
        this.price = price;
        this.workExperience = workExperience;
    }

    public ArrayList getCert() {
        return cert;
    }

    public void setCert(ArrayList cert) {
        this.cert = cert;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getMoblie_phone() {
        return moblie_phone;
    }

    public void setMoblie_phone(String moblie_phone) {
        this.moblie_phone = moblie_phone;
    }

    public String getSpecialist() {
        return specialist;
    }

    public void setSpecialist(String specialist) {
        this.specialist = specialist;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRegistrationProfileStatus() {
        return registrationProfileStatus;
    }

    public void setRegistrationProfileStatus(String registrationProfileStatus) {
        this.registrationProfileStatus = registrationProfileStatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(String workExperience) {
        this.workExperience = workExperience;
    }
}

