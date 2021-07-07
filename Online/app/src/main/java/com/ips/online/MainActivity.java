package com.ips.online;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    //TextView status = (TextView) findViewById(R.id.status);
    //Button getLocation = (Button) findViewById(R.id.getLocation);
    WifiManager wifiManager;
    String MACID;
    WifiReciever wifiReceiver;
    List<ScanResult> WifiListOnline;
    List<ScanResult> WifiListOffline;
    int k = 4;

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        //status.setText("Click on Get Location to get Your Location");
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiReciever();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        setUserMACID();

    }


    public void openOfflinePhaseActivity(View v){
        Intent intent = new Intent(this,OfflinePhase.class);
        startActivity(intent);
    }
    public void openOnlinePhaseActivity(View v){
        Intent intent = new Intent(this,OnlinePhase.class);
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

    private HashMap<String,List<ScanResult>> scanWifiOnline(){
        HashMap<String,List<ScanResult>> RawOnlineInput = null;
        for(int i = 0; i < k; i++){
            wifiManager.startScan();
            List<ScanResult> ithScanResult = wifiManager.getScanResults();
            for(ScanResult AP:ithScanResult){
                if(RawOnlineInput.containsKey(AP.BSSID)){
                    List<ScanResult> temp = RawOnlineInput.get(AP.BSSID);
                    temp.add(AP);
                }else{
                    List<ScanResult> temp = new ArrayList<ScanResult>();
                    temp.add(AP);
                    RawOnlineInput.put(AP.BSSID,temp);
                }
            }
        }
        return RawOnlineInput;
    }

    /*private void scanWifiOffline(){
        Boolean isScan = wifiManager.startScan();
        WifiListOnline = wifiManager.getScanResults();
        Log.i("Info",isScan.toString());
        return;
    }*/

    /*private void sendRSSDataToServer(){
        HashMap<String,RSSData> requestBody = ConvertScanResultToHashMap();
        Call<Void> call =retrofitInterface.executeSendRSSData(requestBody);
        call.enqueue(new Callback<Void>(){

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200 ){
                    //TODO : Success
                }else{

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                //TODO: error occured in transmission
            }
        });
    }*/

    private OnlineData ModelOutput(){
        OnlineData locationData = new OnlineData();
        //TODO : Implement the model to get the location of the User in Online Phase
        //       using WifiListOnline obtained from the Scan;

        return locationData;
    }
    private void sendLocationDataToServer(){
        OnlineData locationData = ModelOutput();
        Call<Void> call = retrofitInterface.executeSendLocationData(locationData);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200){
                    //TODO : Sucess
                }else{

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                //TODO : error occured in transmission
            }
        });
    }

    /*
    Converts the Result obtained from wifiScan to a HashMap to sent it using Retrofit.
    This wifiScan is performed when sending Data to create DB on which Model will be Trained
     */

    /*private HashMap<String, RSSData> ConvertScanResultToHashMap(){
        HashMap<String,RSSData> map = new HashMap<String,RSSData>();
        for(ScanResult rawData:WifiListOffline){
            RSSData temp = new RSSData();
            temp.setBSSID(rawData.BSSID);
            temp.setRSS(rawData.level);
            temp.setSSID(rawData.SSID);
            temp.setTimestamp(rawData.timestamp);
            temp.setX();
            temp.setY();
            map.put(rawData.BSSID,temp);
        }
        return map;
    }*/

}