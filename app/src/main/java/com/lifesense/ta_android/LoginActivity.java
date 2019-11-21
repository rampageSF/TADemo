package com.lifesense.ta_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.immersionbar.ImmersionBar;
import com.lifesense.lshybird.LifesenseAgent;
import com.lifesense.lshybird.user.LoginInfo;
import com.lifesense.lshybird.debug.DebugActivity;
import com.lifesense.rpm.doctor.dev.R;
import com.lifesense.ta_android.utils.SpUtils;

import java.net.URI;

/**
 * @author linquandong
 * @create 2019-08-26
 * @Describe
 **/
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText alPhoneCet;
    private EditText alKeyCet, etServiceUrl;
    private TextView alUserLoginTv, tvCurrentH5Url, tvCurrentServiceUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ta_ac_login);

        ImmersionBar.with(this)
                .statusBarDarkFont(true)
                .fitsSystemWindows(true)
                .init();
        initView();
    }

    private void setTextUrl() {
        tvCurrentH5Url.setText("当前网页链接：\n" + MyApp.getH5Url());
        tvCurrentServiceUrl.setText("当前服务器地址：\n" + MyApp.getH5Url());
        etServiceUrl.setText(MyApp.getServerUrl() + "");
        alKeyCet.setText(MyApp.getH5Url());

    }

    private void initView() {
        etServiceUrl = findViewById(R.id.etServiceUrl);
        tvCurrentH5Url = findViewById(R.id.tvCurrentH5Url);
        tvCurrentServiceUrl = findViewById(R.id.tvCurrentServiceUrl);
        alPhoneCet = (EditText) findViewById(R.id.al_phone_Cet);
        alKeyCet = (EditText) findViewById(R.id.al_key_Cet);
        alUserLoginTv = (TextView) findViewById(R.id.al_user_login_tv);
        alUserLoginTv.setOnClickListener(this);
        alPhoneCet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    alUserLoginTv.setEnabled(true);
                    if("10086".equals(s.toString())){
                        findViewById(R.id.tv_debug).setVisibility(View.VISIBLE);
                    }else{
                        findViewById(R.id.tv_debug).setVisibility(View.GONE);
                    }
                } else {
                    alUserLoginTv.setEnabled(false);
                }
            }
        });
        if (!TextUtils.isEmpty(SpUtils.getUserId())) {
            alPhoneCet.setText(SpUtils.getUserId());
            if(SpUtils.getUserId().length()<=9){
                alPhoneCet.setSelection(SpUtils.getUserId().length());
            }
        }
        if (!TextUtils.isEmpty(SpUtils.getDomain())) {
            alKeyCet.setText(SpUtils.getDomain());
            alKeyCet.setSelection(SpUtils.getDomain().length());
        }
        setTextUrl();

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.al_user_login_tv:
                String url = alKeyCet.getText().toString();

                //设置用户信息
                String userId = alPhoneCet.getText().toString();
                String serviceUrl = etServiceUrl.getText().toString();
                if (TextUtils.isEmpty(serviceUrl)) {
                    Toast.makeText(this, "请输入服务器地址", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(userId)) {
                    Toast.makeText(this, "请输入用户ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(url)) {
                    Toast.makeText(this, "请输入网页地址", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    URI.create(url);
                } catch (Exception e) {
                    Toast.makeText(this, "请输入正确的网页地址", Toast.LENGTH_SHORT).show();
                }
                try {
                    URI.create(serviceUrl);
                } catch (Exception e) {
                    Toast.makeText(this, "请输入正确的服务地址", Toast.LENGTH_SHORT).show();
                }


                //保存自定义配置
                SpUtils.saveDomain(url);
                SpUtils.saveUserId(userId);
                SpUtils.put("serviceUrl", serviceUrl);
                setTextUrl();

                //设置用户信息和服务器地址
                LifesenseAgent.login(LoginInfo.build(userId));
//                LifesenseAgent.setServerUrl(serviceUrl);


                //进入首页 LifesenseAgent.buildLsFragment(MyApp.getH5Url())
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);

                break;
        }
    }

    public void onDebug(View view) {
        startActivity(new Intent(this, DebugActivity.class));
    }
}
