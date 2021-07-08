package com.ips.online;

import android.net.wifi.ScanResult;
import java.io.*;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Filtering {
    private int k;
    private float epsilon;
    private float theta;
    private HashMap<String,ModelInput> HighQualityData;
    public Filtering(int k,float epsilon, float theta, HashMap<String,List<ScanResult>> RawOnlineInput){
        this.k = k;
        this.epsilon = epsilon;
        this.theta = theta;
        this.HighQualityData = FinalFiltering(RawOnlineInput);

    }

    public HashMap<String, ModelInput> getHighQualityData() {
        return HighQualityData;
    }

    public Boolean checkValidity(Double Exi, int xi, float theta){
        if( (xi < (Exi-theta)) || (xi > (Exi+theta)) ){
            return false;
        }else{
            return true;
        }
    }
    public HashMap<String,ModelInput> FinalFiltering(HashMap<String,List<ScanResult>> RawOnlineInput){

        Timestamp ts = new Timestamp(System.currentTimeMillis());

        HashMap<String,ModelInput> HighQualityData = new HashMap<String,ModelInput>();
        HashMap<String,Float> Prob = LossProb(RawOnlineInput);
        HashMap<String,Double> ExpectedValue = LogMeanAndSD(RawOnlineInput);

        for(HashMap.Entry<String,List<ScanResult>> AP : RawOnlineInput.entrySet()){

            String APid = AP.getKey();          //BSSID(MAC address) of the AP
            List<ScanResult> APScanDataList = AP.getValue();

            ModelInput AccessPoint = new ModelInput();
            AccessPoint.setBSSID(APid);
            AccessPoint.setSSID( (APScanDataList.get(0).SSID) );
            AccessPoint.setTimestamp(ts);

            if(Prob.get(APid) >= this.epsilon){
                AccessPoint.setRSS(0);
                /*
                setting RSS value 0 essentially means that this AP was not detected.
                */
            }else{
                int counter = 0;
                Double sum  = 0d;
                for(ScanResult scanData : APScanDataList){
                    if(checkValidity(ExpectedValue.get(APid), -scanData.level, this.theta)){
                        sum += scanData.level;
                        counter++;
                    }
                }
                sum = sum/counter;
                AccessPoint.setRSS(sum);
            }
        }
        return HighQualityData;
    }

    /*

    RawOnlineInput Stores the Data received from k-continuous Scan. They are Stored in the HashTable with AP BSSID(MAC Address)
    as the key of the hashtable. Value corresponding to each key is the the Data collected in each scan corresponding to the particular AP.
    length of List<ScanResult> will be at most k. There might be some AP which were detected in (let's say) 1 out of k scans,
    then length of that List will be 1.
    RawOnlineInput = {
                      "AP1" : [AP1_ScanResult1, AP1_ScanResult2, .....],
                      "AP2" : [AP2_ScanResult1, AP2_ScaneResult2, ....],
                      ...
                      ...
                        }
    */
    /*
    Prob stores the information about what is the probability that a given AP will not be detected in a given Scan
    out of the k scans.
    */
    /*
    ExpectedValue contains the information about the Expected Value of RSS of a given AP considering a Log-Normal Ditribution
    of the RSS value at the given Location(unknown location where we want to find the co-ordinates).
    */
    public HashMap<String,Float> LossProb(HashMap<String,List<ScanResult>> RawOnlineInput){
        HashMap<String,Float> Prob = new HashMap<String,Float>();
        for(HashMap.Entry<String,List<ScanResult>> AP : RawOnlineInput.entrySet()){
            Prob.put(AP.getKey(), (float) (1-(AP.getValue().size()/k)));
        }
        return Prob;
    }
    public HashMap<String, Double> LogMeanAndSD(HashMap<String,List<ScanResult>> RawOnlineInput ){
        HashMap<String,Double> ExpectedValue = new HashMap<String,Double>();
        for(HashMap.Entry<String,List<ScanResult>> AP : RawOnlineInput.entrySet()){
            double Logmean = 0d;
            int counter = 0;
            double LogStandardDeviation = 0d;
            for(ScanResult ithScan : AP.getValue()){
                Logmean += -(ithScan.level);
                LogStandardDeviation += Math.pow(-(ithScan.level),2);
                counter++;
            }
            Logmean = Logmean/counter;
            LogStandardDeviation = LogStandardDeviation/counter;
            LogStandardDeviation = LogStandardDeviation - Math.pow(Logmean,2);
            double ExpectedVal = Math.exp(Logmean+LogStandardDeviation/2);
            ExpectedValue.put(AP.getKey(), ExpectedVal);
        }
        return ExpectedValue;
    }
}
