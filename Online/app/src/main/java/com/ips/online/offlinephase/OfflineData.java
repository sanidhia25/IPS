package com.ips.online.offlinephase;

public class OfflineData {
    private String BSSID;
    private String SSID;
    private double X;
    private double Y;
    private double RSS;
    private double GPSLongitude;
    private double GPSLatitude;
    private long timestamp;


    public void setGPSLongitude(double GPSLongitude) {
        this.GPSLongitude = GPSLongitude;
    }

    public void setGPSLatitude(double GPSLatitude) {
        this.GPSLatitude = GPSLatitude;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public void setX(double x) {
        this.X = x;
    }

    public void setY(double y) {
        this.Y = y;
    }

    public void setRSS(double RSS) {
        this.RSS = RSS;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
