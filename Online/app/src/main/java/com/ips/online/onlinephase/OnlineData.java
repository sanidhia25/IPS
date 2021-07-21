package com.ips.online.onlinephase;

import java.sql.Timestamp;

public class OnlineData {
    private int Xcood;
    private int Ycood;
    private Timestamp TS;
    private String Identity;

    public int getXcood() {
        return Xcood;
    }

    public void setXcood(int xcood) {
        Xcood = xcood;
    }

    public int getYcood() {
        return Ycood;
    }

    public void setYcood(int ycood) {
        Ycood = ycood;
    }

    public Timestamp getTS() {
        return TS;
    }

    public void setTS(Timestamp TS) {
        this.TS = TS;
    }

    public String getIdentity() {
        return Identity;
    }

    public void setIdentity(String identity) {
        Identity = identity;
    }
}
