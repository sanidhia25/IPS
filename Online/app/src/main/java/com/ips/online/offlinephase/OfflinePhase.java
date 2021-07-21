package com.ips.online.offlinephase;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ips.online.utilityclasses.ListAdapter;
import com.ips.online.R;
import com.ips.online.RetrofitInterface;
import com.ips.online.utilityclasses.WifiReciever;

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

    FusedLocationProviderClient mFusedLocationClient;
    double GPSLatitude;
    double GPSLongitude;

    boolean flag = true;
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);



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


    ////==============================Code to get GPS Co-ordinates================================
    ////==========================================================================================
    ////==========================================================================================
    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if(checkPermissions()){
            if(isLocationEnabled()){
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>(){
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            GPSLatitude = location.getLatitude();
                            GPSLongitude = location.getLongitude();
                            textViewGPS.setText("Latitude : " + location.getLatitude() + "\n" + "Longitude : " + location.getLongitude());
                        }
                    }
                });
            }else{
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        }else{
            requestPermissions();
        }

    }
    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //mLocationRequest.setInterval(5);
        //mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            textViewGPS.setText("Latitude : " + mLastLocation.getLatitude() + "\n" + "Longitude : " + mLastLocation.getLongitude());
        }
    };
    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    // If everything is alright then
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, 44);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }
    ////============================END of GPS Code===============================================
    ////==========================================================================================
    ////==========================================================================================


    public void sendData(View v){
        scanWifiOffline();
        Log.i("Info",wifiListOffline.toString());
        HashMap<String, OfflineData> refinedOfflineData = ConvertScanResultToHashMap();
        Log.i("Info",refinedOfflineData.toString());
        getLastLocation();
        if(flag){
            sendOfflineDataToServer(refinedOfflineData);
        }
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
            temp.setGPSLatitude(GPSLatitude);
            temp.setGPSLongitude(GPSLongitude);
            try{
            temp.setX(Double.parseDouble(editX.getText().toString()));
            temp.setY(Double.parseDouble(editY.getText().toString()));
            }catch (Exception e){
                flag = false;
                Toast.makeText(this, "Enter Proper Co-ordinates", Toast.LENGTH_SHORT).show();
            }
            refinedOfflineData.put(rawData.BSSID,temp);
        }
        return refinedOfflineData;
    }

    private void sendOfflineDataToServer(HashMap<String,OfflineData> requestBodyWhole){
        Log.i("info", "sendOfflineDataToServer: sendinData");

        for(HashMap.Entry<String,OfflineData> requestBody : requestBodyWhole.entrySet()){
            Log.i("Info","Inside for loop");
            Call<Void> call = retrofitInterface.executeSendOfflineData(requestBody.getValue());
            call.enqueue(new Callback<Void>(){
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == 200 ){
                        //TODO : Success
                        Toast.makeText(OfflinePhase.this, "Data Pushed Succesfully",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(OfflinePhase.this,"Failed",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    //TODO: error occured in transmission
                    Log.i("error",t.getMessage());
                }
            });
        }
    }

}

