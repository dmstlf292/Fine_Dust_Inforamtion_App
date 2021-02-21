package com.info.finedustinfo.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.info.finedustinfo.R;

public class AddLocationDialogFragment extends DialogFragment {
    private EditText mCityEditText;

    private OnClickListener mOkClickListener;

    public interface OnClickListener{
        void onOkClicked(String city);//도시입력
    }

    //외부에서 연결할 수 있는 메서드
    public void setOnClickListener(OnClickListener listener){
        mOkClickListener = listener;
    }

    //리스너를 다이얼로그 생성시 바로 전달할 것
    public static AddLocationDialogFragment newInstance(OnClickListener listener){
        AddLocationDialogFragment fragment = new AddLocationDialogFragment();
        fragment.setOnClickListener(listener);
        return fragment;
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {//다이어로그 레이아웃 붙이기!
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_add_location, null, false);
        mCityEditText = view.findViewById(R.id.city_edit);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("위치 추가");
        builder.setView(view);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String city = mCityEditText.getText().toString();
                mOkClickListener.onOkClicked(city);//외부에 전달하기
            }
        });
        builder.setNegativeButton("취소", null);

        return builder.create();
    }
}
