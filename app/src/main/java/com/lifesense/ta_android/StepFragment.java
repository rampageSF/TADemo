package com.lifesense.ta_android;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lifesense.lshybird.step.IStepChangeListener;
import com.lifesense.lshybird.step.StepCounterManage;
import com.lifesense.rpm.doctor.dev.R;

/**
 * Created by Sinyi.liu on 2019/8/27.
 */
public class StepFragment extends Fragment implements IStepChangeListener {
    TextView mTextView, textView1;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextView = view.findViewById(R.id.textView);

        mTextView.setText("今天步数:" + StepCounterManage.getInstance().getCurrentDateStep());
        StepCounterManage.getInstance().addStepChangeListener(this);
        textView1 = view.findViewById(R.id.textView1);
        textView1.setText("上一次步数:" + StepCounterManage.getInstance().getLastStepEntry());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmnet_step, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        StepCounterManage.getInstance().removeStepChangeListener(this);
    }


    @Override
    public void onStepChange(String time, long utc, int step) {
        if (getActivity() != null) {
            mTextView.setText("当前步数\n" + time + " step:" + step + " \n utc:" + utc);
        }
    }
}
