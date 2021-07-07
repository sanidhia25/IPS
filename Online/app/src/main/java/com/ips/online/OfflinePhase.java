package com.ips.online;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OfflinePhase extends AppCompatActivity {

    ListView myListView;
    TextView textViewGPS;
    EditText editX;
    EditText editY;
    Button sendData;


    WifiManager wifiManagerOffline;
    WifiReciever wifiRecieverOffline;
    ListAdapter listAdapter;
    List<ScanResult> wifiListOffline;

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:3000";
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offlinephase);

        myListView = (ListView) findViewById(R.id.myListView);
        textViewGPS = (TextView) findViewById(R.id.textViewGPS);
        editX = (EditText) findViewById(R.id.editX);
        editY = (EditText) findViewById(R.id.editY);
        sendData = (Button) findViewById(R.id.sendData);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        wifiManagerOffline =(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiRecieverOffline = new WifiReciever();
        registerReceiver(wifiRecieverOffline,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    }
    public void sendData(View v){
        scanWifiOffline();
        HashMap<String, OfflineData> refinedOfflineData = ConvertScanResultToHashMap();
        sendOfflineDataToServer(refinedOfflineData);
    }
    private void scanWifiOffline(){
        Boolean hasScaned = wifiManagerOffline.startScan();
        wifiListOffline = wifiManagerOffline.getScanResults();
        setAdapter();
    }
    private void setAdapter(){
        listAdapter = new ListAdapter(getApplicationContext(),wifiListOffline);
        myListView.setAdapter(listAdapter);
    }

    private HashMap<String, OfflineData> ConvertScanResultToHashMap(){
        HashMap<String,OfflineData> refinedOfflineData = new HashMap<String,OfflineData>();
        for(ScanResult rawData:wifiListOffline){
            OfflineData temp = new OfflineData();
            temp.setBSSID(rawData.BSSID);
            temp.setRSS(rawData.level);
            temp.setSSID(rawData.SSID);
            temp.setTimestamp(rawData.timestamp);
            temp.setX(Double.parseDouble(editX.getText().toString()));
            temp.setY(Double.parseDouble(editY.getText().toString()));
            refinedOfflineData.put(rawData.BSSID,temp);
        }
        return refinedOfflineData;
    }

    private void sendOfflineDataToServer(HashMap<String,OfflineData> requestBody){
        Call<Void> call = retrofitInterface.executeSendOfflineData(requestBody);
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
    }

}

