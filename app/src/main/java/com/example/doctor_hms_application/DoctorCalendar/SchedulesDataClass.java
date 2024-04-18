package com.example.doctor_hms_application.DoctorCalendar;

import android.os.Parcel;
import android.os.Parcelable;

public class SchedulesDataClass implements Parcelable {
    private String email;

    private String scheduleStatus;
    private String fullDateTime;
    private String cancellationReason;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Empty constructor required by Firebase
    public SchedulesDataClass() {
        // Initialize values if needed
    }

    // Parameterized constructor for Parcelable
    public SchedulesDataClass(String email, String scheduleStatus, String fullDateTime, String cancellationReason) {
        this.email = email;
        this.scheduleStatus = "available";
        this.fullDateTime = fullDateTime;
        this.cancellationReason = cancellationReason;
        this.id = ""; // Initialize id here
    }

    protected SchedulesDataClass(Parcel in) {
        email = in.readString();
        scheduleStatus = in.readString();
        fullDateTime = in.readString();
        cancellationReason = in.readString();  // Add this line
    }


    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
    public void setScheduleStatus(String scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }


    public static final Creator<SchedulesDataClass> CREATOR = new Creator<SchedulesDataClass>() {
        @Override
        public SchedulesDataClass createFromParcel(Parcel in) {
            return new SchedulesDataClass(in);
        }

        @Override
        public SchedulesDataClass[] newArray(int size) {
            return new SchedulesDataClass[size];
        }
    };


    public String getEmail() {
        return email;
    }

    public String getScheduleStatus() {
        return scheduleStatus;
    }

    public String getFullDateTime() {
        return fullDateTime;
    }

    @Override
    public String toString() {
        return "Date and Time: " + fullDateTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(scheduleStatus);
        dest.writeString(fullDateTime);
        dest.writeString(cancellationReason);
    }

    public String getCancellationReason() {
        return cancellationReason;
    }
}

