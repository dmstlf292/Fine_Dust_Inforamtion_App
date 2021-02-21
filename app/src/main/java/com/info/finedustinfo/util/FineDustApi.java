package com.info.finedustinfo.util;

import com.info.finedustinfo.model.dust_material.FineDust;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface FineDustApi {
    String BASE_URL="HTTP://api.weatherplanet.co.kr/";

    //header에 있는 앱키 아주 중요함
    @Headers("appKey:6b200e091d1a4d7e83fb9b4732809b33")
    //요청정보, 응답객체<받는형태>
    @GET("weather/dust?version=1")
    //그리고 겟방식의 뒤의 쿼리를 주려면 @Query 써야함...
    Call<FineDust> getFineDust(@Query("lat") double latitude,
                               @Query("lon") double longitude);


}