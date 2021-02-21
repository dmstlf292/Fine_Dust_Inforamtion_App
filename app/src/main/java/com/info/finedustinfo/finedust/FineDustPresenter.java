package com.info.finedustinfo.finedust;

import com.info.finedustinfo.data.FineDustRepository;
import com.info.finedustinfo.model.dust_material.FineDust;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//실제로 일어나는 동작들을 다 여기서 처리할 예정, 로직들도 다 여기에 들어갈 예정
public class FineDustPresenter implements FineDustContract.UserActionsListener {
    private final FineDustRepository mRepository;
    private final FineDustContract.View mView;

    public FineDustPresenter(FineDustRepository repository, FineDustContract.View view) {
        this.mRepository = repository;
        this.mView = view;
    }

    @Override
    public void loadFineDustData() {
        if(mRepository.isAvailable()){
            mView.loadingStart();
            mRepository.getFineDustData(new Callback<FineDust>() {//데이터 가져와서 결과 받는것
                //결과 코드 작성
                @Override
                public void onResponse(Call<FineDust> call, Response<FineDust> response) {
                    mView.showFineDustResult(response.body());
                    mView.loadingEnd();
                }

                @Override
                public void onFailure(Call<FineDust> call, Throwable t) {
                    mView.showLoadError(t.getLocalizedMessage());
                    mView.loadingEnd();
                }
            });
        }
    }
}
