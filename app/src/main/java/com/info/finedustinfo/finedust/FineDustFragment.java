package com.info.finedustinfo.finedust;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.info.finedustinfo.MainActivity;
import com.info.finedustinfo.R;
import com.info.finedustinfo.data.FineDustRepository;
import com.info.finedustinfo.data.LocationFineDustRepository;
import com.info.finedustinfo.model.dust_material.FineDust;

public class FineDustFragment extends Fragment implements FineDustContract.View{//Fragment는  support버전 프래그먼트 사용하기

    //여기 이부분이 "필드" 이다//*************
    private TextView mLocationTextView;
    private TextView mTimeTextView;
    private TextView mDustTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FineDustRepository mRepository;
    private FineDustPresenter mPresenter;

    //프래그먼트를 생성할때도 위도 경도를 통해서 생성하도록 만들기
    //프래그먼트는 매개변수를 받아서 생성이 안되기 때문에 이렇게 한다.
    public static FineDustFragment newInstance (double lat, double lng){
        Bundle args = new Bundle();
        args.putDouble("lat", lat);
        args.putDouble("lng", lng);
        FineDustFragment fragment = new FineDustFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments()!=null){
            double lat = getArguments().getDouble("lat");
            double lng = getArguments().getDouble("lng");
            mRepository= new LocationFineDustRepository(lat,lng);
        }else{
            mRepository = new LocationFineDustRepository(); // 그냥 생성되는 경우 = 좌표가 없는 상태로 생성 (기본생성자로 만들기)
            //프레그먼트 좌표가 없다면 현재 위치로 잡기
            ((MainActivity)getActivity()).getLastKnownLocation();//현재위치 호출하기
        }
        mPresenter = new FineDustPresenter(mRepository, this);
        mPresenter.loadFineDustData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //레이아웃 가져오기
        View view = inflater.inflate(R.layout.fragment_fine_dust,container,false);
        mLocationTextView = view.findViewById(R.id.result_location_text);
        mTimeTextView = view.findViewById(R.id.result_time_text);
        mDustTextView = view.findViewById(R.id.result_dust_text);
        //데이터 저장 복원하는 코드
        if (savedInstanceState!=null){
            mLocationTextView.setText(savedInstanceState.getString("location"));
            mTimeTextView.setText(savedInstanceState.getString("time"));
            mDustTextView.setText(savedInstanceState.getString("dust"));
        }
        //리프레쉬 레이아웃
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE);
        //스와이프 땡겨졌을때
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {// 모든 핵심 로직을 가지고 있는 프리젠터를 이용해서 로딩을 하게끔 만들기
                mPresenter.loadFineDustData();//프리젠터를 가지고 로딩할것

            }
        });
        return view;
    }

    //복원 코드가 있어서
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("location", mLocationTextView.getText().toString());
        outState.putString("time", mTimeTextView.getText().toString());
        outState.putString("dust", mDustTextView.getText().toString());
    }

    //프래그먼트가 할 일 = 뷰를 갱신하는 , 로딩이 끝나면 결과를 보여주는것 (View안에 정의를 했었음.)
    @Override
    public void showFineDustResult(FineDust fineDust) {
        mLocationTextView.setText(fineDust.getWeather().getDust().get(0).getStation().getName());
        mTimeTextView.setText(fineDust.getWeather().getDust().get(0).getTimeObservation());
        mDustTextView.setText(fineDust.getWeather().getDust().get(0)
                .getPm10().getValue() + " ㎍/m³, " + fineDust.getWeather().getDust().get(0).getPm10().getGrade());
    }

    @Override
    public void showLoadError(String message) {
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadingStart() {
        mSwipeRefreshLayout.setRefreshing(true); //돌아가게 만들기
    }

    @Override
    public void loadingEnd() {
        mSwipeRefreshLayout.setRefreshing(false); //로딩끝
    }

    @Override
    public void reload(double lat, double lng) {//처음 로드할때도 이걸로 사용할 것
        mRepository = new LocationFineDustRepository(lat,lng); // 위치 정보를 가지고 객체를 만들것
        mPresenter = new FineDustPresenter(mRepository, this);//뷰는 이 프레그먼트 자체이다!!!
        mPresenter.loadFineDustData();
    }
}

