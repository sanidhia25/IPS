package com.ips.online;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OnlinePhase extends AppCompatActivity {

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:3000";

    WifiManager wifiManager;
    String MACID;
    WifiReciever wifiReceiver;

    int K = 5;
    EditText editK;
    float theta;
    float epsilon;
    


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onlinephase);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        //Value of K by default is 5. Can change via the EditText Box
        editK = (EditText) findViewById(R.id.editK);
        if(editK.getText().toString() != ""){
            K = Integer.valueOf(editK.getText().toString());
        }

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiReciever();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        setUserMACID();

        HashMap<String,List<ScanResult>> RawOnlineData = scanWifiOnline();
        Filtering OnlineFiltering = new Filtering(K,epsilon,theta, RawOnlineData);
        HashMap<String,ModelInput> HighQualityData = OnlineFiltering.getHighQualityData();
        //fed this HighQualityData to the Model after appropriate postprocessing according to the Model Input format.
        //TODO: Model PrProcessing



    }

    public void sendOnlineLocation(View v){
        HashMap<String,List<ScanResult>> RawOnlineData = scanWifiOnline();

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

    private HashMap<String, List<ScanResult>> scanWifiOnline(){
        HashMap<String,List<ScanResult>> RawOnlineData = new HashMap<String,List<ScanResult>>();
        for(int i = 0; i < K; i++){
            wifiManager.startScan();
            List<ScanResult> ithScanResult = wifiManager.getScanResults();
            for(ScanResult AP:ithScanResult){
                if(RawOnlineData.containsKey(AP.BSSID)){
                    List<ScanResult> temp = RawOnlineData.get(AP.BSSID);
                    temp.add(AP);
                }else{
                    List<ScanResult> temp = new ArrayList<ScanResult>();
                    temp.add(AP);
                    RawOnlineData.put(AP.BSSID,temp);
                }
            }
        }
        return RawOnlineData;
    }
}
