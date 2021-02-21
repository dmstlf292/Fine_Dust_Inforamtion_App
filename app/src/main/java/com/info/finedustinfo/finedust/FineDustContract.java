package com.info.finedustinfo.finedust;

import com.info.finedustinfo.model.dust_material.FineDust;

public class FineDustContract {
    //View에 대한 인터페이스 만들기
    public interface View {
        //유저가 동작하는것 말고 그 이외의 기타 행위 = 화면이 갱신되는 행위 (화면변화)
        //데이터를 화면에 표시하는것
        void showFineDustResult(FineDust fineDust);
        //에러표시
        void showLoadError(String message);
        //로딩시작될때 빙글빙글 뷰변화 시키기
        void loadingStart();
        //로딩끝났을때
        void loadingEnd();
        //재로딩
        void reload(double lat, double lng);
    }
    public interface UserActionsListener{
        //화면 프레그먼트를 봤을때 유저가 하는 액션이 뭐가 있는지에 대한 정의
        //1. 데이터 로드하는것
        void loadFineDustData();
    }

}
