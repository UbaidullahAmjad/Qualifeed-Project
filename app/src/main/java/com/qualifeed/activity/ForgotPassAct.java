package com.qualifeed.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.qualifeed.R;
import com.qualifeed.databinding.ActivityForgotPassBinding;
import com.qualifeed.model.LoginModel;
import com.qualifeed.retrofit.ApiClient;
import com.qualifeed.retrofit.Constant;
import com.qualifeed.retrofit.QualifeedInterface;
import com.qualifeed.utils.App;
import com.qualifeed.utils.DataManager;
import com.qualifeed.utils.NetworkAvailablity;
import com.qualifeed.utils.NetworkReceiver;
import com.qualifeed.utils.SessionManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassAct extends AppCompatActivity {
    public String TAG = "ForgotPassAct";
    ActivityForgotPassBinding binding;
    public static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    QualifeedInterface apiInterface;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(QualifeedInterface.class);
        binding  =  DataBindingUtil.setContentView(this, R.layout.activity_forgot_pass);
        initView();
    }

    private void initView() {
        binding.btnSend.setOnClickListener(v -> {
          validation();
        });


        binding.ivBack.setOnClickListener(v -> {
           finish();
        });
    }

    private void validation() {
        if (binding.edMail.getText().toString().equals("")) {
            binding.edMail.setError(getString(R.string.required));
            binding.edMail.setFocusable(true);
        } else if (!binding.edMail.getText().toString().matches(emailPattern)) {
            binding.edMail.setError(getString(R.string.wrong_email));
            binding.edMail.setFocusable(true);
        }
        else {
            if(NetworkAvailablity.checkNetworkStatus(ForgotPassAct.this)) ForgotPass();
            else App.showToast(ForgotPassAct.this,getString(R.string.network_failure), Toast.LENGTH_LONG);
        }
    }

    private void ForgotPass() {
        DataManager.getInstance().showProgressMessage(ForgotPassAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("email", binding.edMail.getText().toString());
        Log.e(TAG, "Forgotpass Request " + map);
        Call<Map<String,String>> loginCall = apiInterface.forgotPassword(map);
        loginCall.enqueue(new Callback<Map<String,String>>() {
            @Override
            public void onResponse(Call<Map<String,String>> call, Response<Map<String,String>> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    Map<String,String> data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Forgotpass Response :" + responseString);
                    if (data.get("status").equals("1")) {
                        App.showToast(ForgotPassAct.this, "Link Send your Email id", Toast.LENGTH_SHORT);
                        finish();
                    } else if (data.get("status").equals("0")) App.showToast(ForgotPassAct.this, data.get("message"), Toast.LENGTH_SHORT);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Map<String,String>> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }
}
