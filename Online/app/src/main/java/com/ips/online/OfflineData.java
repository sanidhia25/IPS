package com.ips.online;

public class OfflineData {
    private String BSSID;
    private String SSID;
    private double X;
    private double Y;
    private double RSS;
    private long timestamp;


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
