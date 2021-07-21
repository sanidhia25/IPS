package com.ips.online.onlinephase;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.ips.online.onlinefilter.Filtering;
import com.ips.online.model.ModelInput;
import com.ips.online.R;
import com.ips.online.RetrofitInterface;
import com.ips.online.utilityclasses.WifiReciever;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OnlinePhase extends AppCompatActivity {

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:3000";

    WifiManager wifiManager;
    String MACID;
    WifiReciever wifiReceiver;

    EditText editK;
    /*
    We will have to initialize epsilon and theta according to the epsilon and theta used in the Offline
    Training Phase of our SVR Model.
    We can set K as our wish during the Online Phase. Bigger K => Better accuracy
    */
    int K = 5;
    float theta = 4f;
    float epsilon = .7f;
    


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
    }

    public void sendOnlineLocation(View v){
        HashMap<String,List<ScanResult>> RawOnlineData = scanWifiOnline();
        Filtering OnlineFiltering = new Filtering(K,epsilon,theta, RawOnlineData);
        HashMap<String, ModelInput> HighQualityData = OnlineFiltering.getHighQualityData();
        //fed this HighQualityData to the Model after appropriate postprocessing according to the Model Input format.
        //TODO: Model PrProcessing
        Object ModelOutput;
        //sendLocationDataToServer(ModelOutput);
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

    private OnlineData ModelOutputToOnlineData(Object ModelOutput){
        OnlineData ls = new OnlineData();
        return ls;
    }

    private void sendLocationDataToServer(Object ModelOutput){
        OnlineData locationData = ModelOutputToOnlineData(ModelOutput);
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
}
