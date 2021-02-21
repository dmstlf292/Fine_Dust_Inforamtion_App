package com.info.finedustinfo.util;

import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FineDustUtil {
    private FineDustApi mGetApi;
    //아래 생성자는 레트로핏 사용할때 매번사용하는것이다.
    //생성자 만들기 (안에 레트로핏이랑 Gson 생성해서 사용하기)
    public FineDustUtil() {
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(FineDustApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mGetApi = mRetrofit.create(FineDustApi.class);
    }

    //외부에서 가져다 쓰게 하기
    public FineDustApi getApi(){
        return mGetApi;
    }
}