package com.ips.online.model;

import java.sql.Timestamp;

public class ModelInput {
    private String BSSID;
    private String SSID;
    private double RSS;
    private Timestamp timestamp;


    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public void setRSS(double RSS) {
        this.RSS = RSS;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
