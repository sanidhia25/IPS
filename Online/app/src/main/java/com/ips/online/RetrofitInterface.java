package com.ips.online;


import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {
    @POST("/index")
    Call<Void> executeSendLocationData(@Body OnlineData ReqBody);

    @POST("/Train")
    Call<Void> executeSendOfflineData(@Body HashMap<String,OfflineData> ReqBody);
}