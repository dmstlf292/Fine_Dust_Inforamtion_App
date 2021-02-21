package com.info.finedustinfo.data;

import com.info.finedustinfo.model.dust_material.FineDust;

import retrofit2.Callback;

public interface FineDustRepository {
    //나중에 코딩이 복잡해지지 않도록 하는것
    boolean isAvailable();//정보 가져올 수 있는 유무
    void getFineDustData (Callback<FineDust> callback); // 레트로핏을 이용해서 데이터 결과 받아오기

}