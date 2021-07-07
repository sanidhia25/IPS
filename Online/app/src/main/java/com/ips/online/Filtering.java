package com.ips.online;

import android.net.wifi.ScanResult;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Filtering {
    int k;
    float epsilon;
    float theta;
    public Filtering(int k,float epsilon, float theta, HashMap<String,List<ScanResult>> RawOnlineInput){
        this.k = k;
        this.epsilon = epsilon;
        this.theta = theta;

    }

    public Boolean checkValidity(Double Exi, int xi, float theta){
        if( (xi < (Exi-theta)) || (xi > (Exi+theta)) ){
            return false;
        }else{
            return true;
        }
    }
    public void HQOnlineData(HashMap<String,List<ScanResult>> RawOnlineInput){
        HashMap<String,OfflineData> HQData = new HashMap<String,OfflineData>();
        HashMap<String,Float> Prob = LossProb(RawOnlineInput);
        HashMap<String,Double> ExpectedValue = LogMeanAndSD(RawOnlineInput);
        for(HashMap.Entry<String,List<ScanResult>> AP : RawOnlineInput.entrySet()){
            String APid = AP.getKey();
            List<ScanResult> APList = AP.getValue();
            OfflineData AccessPoint = new OfflineData();
            AccessPoint.setBSSID(APid);
            AccessPoint.setSSID((APList.get(0).SSID));
            if(Prob.get(APid) >= this.epsilon){
                AccessPoint.setRSS(0);
            }else{
                int counter = 0;
                Double sum  = 0d;
                for(ScanResult scanData : APList){
                    if(checkValidity(ExpectedValue.get(APid), -scanData.level, this.theta)){
                        sum += scanData.level;
                        counter++;
                    }
                }
                sum = sum/counter;
                AccessPoint.setRSS(sum);
            }
        }
    }

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
