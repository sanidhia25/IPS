package com.ips.online;


import com.ips.online.offlinephase.OfflineData;
import com.ips.online.onlinephase.OnlineData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {
    @POST("/index")
    Call<Void> executeSendLocationData(@Body OnlineData ReqBody);

    @POST("/train")
    Call<Void> executeSendOfflineData(@Body OfflineData ReqBody);
}