package com.info.finedustinfo.data;

import com.info.finedustinfo.model.dust_material.FineDust;
import com.info.finedustinfo.util.FineDustUtil;

import retrofit2.Callback;

public class LocationFineDustRepository implements  FineDustRepository{
    //FineDustRepository 인터페이스를 구현하는 코드를 먼저 만들기

    private FineDustUtil mFineDustUtil;
    private double mLatitude;
    private double mLongitude;

    //1. 기본생성자 만들기
    public LocationFineDustRepository() {
        //초기화 하기
        mFineDustUtil  = new FineDustUtil();
    }

    //2. 위도 경도를 받는 생성자 만들기
    public LocationFineDustRepository(double lat, double lng) {
        this(); // 기본생성자 호출
        this.mLatitude = lat;
        this.mLongitude = lng;
    }

    @Override
    public boolean isAvailable() {
        if(mLatitude != 0.0 && mLongitude !=0.0){
            return true;
        }
        return false;
    }

    //실제로 데이터 가져오는 코드 (추상화 작업)
    @Override
    public void getFineDustData(Callback<FineDust> callback) {//callback으로 던져준다.
        mFineDustUtil.getApi().getFineDust(mLatitude,mLongitude)
                .enqueue(callback); //비동기로 수행하는 것을 enqueue 라고 한다
    }
}
