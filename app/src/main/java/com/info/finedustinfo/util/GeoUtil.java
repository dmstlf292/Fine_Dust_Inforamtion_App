package com.info.finedustinfo.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GeoUtil {
    public interface GeoUtilListener {
        void onSuccess(double lat, double lng);
        void onError(String message);//실패시 메세지를 외부에 알려줌
    }



    //지역명 전달시 위도경도 돌려주는 기능 만들기
    public static void getLocationFromName(Context context, String city,GeoUtilListener listener){
        //비동기 동작 코드 --> 외부에서 사용하려면 결과를 받는 리스너를 생성해줘야한다.
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocationName(city, 1);
            if(addresses.size()>0){
                double lat = addresses.get(0).getLatitude();//위도 얻기
                double lng = addresses.get(0).getLongitude();//경도 얻기
                listener.onSuccess(lat, lng);
            } else {
                //결과가 없을경우
                listener.onError("주소 결과가 없습니다.");
            }
        } catch (IOException e) {
            listener.onError(e.getMessage());//오류메시지
        }
    }


}
