package com.qualifeed.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qualifeed.R;
import com.qualifeed.databinding.ActivityLoginBinding;
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

public class LoginAct extends AppCompatActivity {
    public String TAG = "LoginAct";
    ActivityLoginBinding binding;
    public static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    QualifeedInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(QualifeedInterface.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        initViews();
    }

    private void initViews() {

        binding.btnLogin.setOnClickListener(v -> validation());

        binding.btnForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginAct.this, ForgotPassAct.class));
        });
    }

    private void validation() {
        if (binding.edMail.getText().toString().equals("")) {
            binding.edMail.setError(getString(R.string.required));
            binding.edMail.setFocusable(true);
        } else if (!binding.edMail.getText().toString().matches(emailPattern)) {
            binding.edMail.setError(getString(R.string.wrong_email));
            binding.edMail.setFocusable(true);
        }else if (binding.edPassword.getText().toString().equals("")) {
            binding.edPassword.setError(getString(R.string.required));
            binding.edPassword.setFocusable(true);
        }
        else {
            if(NetworkAvailablity.checkNetworkStatus(LoginAct.this)) userLogin();
            else App.showToast(LoginAct.this,getString(R.string.network_failure), Toast.LENGTH_LONG);
        }
    }

    private void userLogin() {
        DataManager.getInstance().showProgressMessage(LoginAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("email", binding.edMail.getText().toString());
        map.put("password", binding.edPassword.getText().toString());
        Log.e(TAG, "Login Request " + map);
        Call<LoginModel> loginCall = apiInterface.userLogin(map);
        loginCall.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    LoginModel data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Login Response :" + responseString);
                    if (data.status.equals("1")) {
                        App.showToast(LoginAct.this, "Login Successfully", Toast.LENGTH_SHORT);
                        SessionManager.writeString(LoginAct.this, Constant.USER_INFO, responseString);
                        startActivity(new Intent(LoginAct.this, ToDoAct.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    } else if (data.status.equals("0")) App.showToast(LoginAct.this, data.message, Toast.LENGTH_SHORT);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }
}