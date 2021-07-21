package com.ips.online;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;

import com.ips.online.offlinephase.OfflinePhase;
import com.ips.online.onlinephase.OnlineData;
import com.ips.online.onlinephase.OnlinePhase;

public class MainActivity extends AppCompatActivity {

    WifiManager wifiManager;
    String MACID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUserMACID();

    }


    public void openOfflinePhaseActivity(View v){
        Intent intent = new Intent(this, OfflinePhase.class);
        startActivity(intent);
    }
    public void openOnlinePhaseActivity(View v){
        Intent intent = new Intent(this, OnlinePhase.class);
        startActivity(intent);
    }

    private String setUserMACID(){
        //TODO : if wifi is not enabled then returns NULL

        try{
            WifiInfo info = wifiManager.getConnectionInfo();
            MACID = info.getMacAddress();
            return MACID;
        }catch(Exception e){
            MACID = "02:00:00:00:00:00";
            return MACID;
        }
    }

    private OnlineData ModelOutput(){
        OnlineData locationData = new OnlineData();
        //TODO : Implement the model to get the location of the User in Online Phase
        //       using WifiListOnline obtained from the Scan;

        return locationData;
    }
}