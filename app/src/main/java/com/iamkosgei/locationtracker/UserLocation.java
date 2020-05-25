package com.iamkosgei.locationtracker;

public class UserLocation {
    private Long time;
    private Double latitude;
    private Double longitude;

    public  UserLocation(){}
    public UserLocation(Long time, Double latitude, Double longitude) {
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
