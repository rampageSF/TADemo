package com.lifesense.ta_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lifesense.lshybird.LifesenseAgent;
import com.lifesense.lshybird.core.HybridConstant;
import com.lifesense.lshybird.param.HybridParamAnimation;
import com.lifesense.lshybird.ui.BaseHyActivity;
import com.lifesense.lshybird.user.LoginInfo;
import com.lifesense.lshybird.utils.ActivityUtil;
import com.lifesense.rpm.doctor.dev.R;
import com.lifesense.ta_android.utils.SpUtils;

/**
 * Created by Sinyi.liu on 2019/8/30.
 */
public class MeFramgnet extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final EditText editText=view.findViewById(R.id.editText);
        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                if (TextUtils.isEmpty(text)) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString(HybridConstant.INTENT_EXTRA_KEY_TOPAGE, text);
                bundle.putSerializable(HybridConstant.INTENT_EXTRA_KEY_ANIMATION, HybridParamAnimation.PUSH);
                bundle.putBoolean(HybridConstant.INTENT_EXTRA_KEY_HASNAVGATION, true);
                final Intent intent = new Intent(getContext(), BaseHyActivity.class);
                intent.putExtras(bundle);
                if (getActivity() != null)
                    getActivity().startActivityForResult(intent, HybridConstant.RQ_RESULT);
            }
        });

        TextView textView = view.findViewById(R.id.textView);
        LoginInfo loginInfo = LifesenseAgent.getLoginInfo();
        textView.setText("我的用户ID：" + loginInfo.getUserId()
                + "\n 服务器地址：" + MyApp.getServerUrl()
                + "\n H5地址：" + MyApp.getH5Url());
    }
}
