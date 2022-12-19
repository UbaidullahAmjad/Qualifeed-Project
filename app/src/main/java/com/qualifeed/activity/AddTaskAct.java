package com.qualifeed.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.qualifeed.R;
import com.qualifeed.adapter.TaskAdapter;
import com.qualifeed.databinding.ActivityAddTaskBinding;
import com.qualifeed.databinding.ActivityTaskBinding;
import com.qualifeed.model.TaskModel;
import com.qualifeed.retrofit.ApiClient;
import com.qualifeed.retrofit.QualifeedInterface;
import com.qualifeed.utils.App;
import com.qualifeed.utils.DataManager;
import com.qualifeed.utils.NetworkAvailablity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTaskAct extends AppCompatActivity {
    public String TAG ="AddTaskAct";
    ActivityAddTaskBinding binding;
    QualifeedInterface apiInterface;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(QualifeedInterface.class);
      binding = DataBindingUtil.setContentView(this,R.layout.activity_add_task);
        initViews();
    }

    private void initViews() {

        binding.btnSave.setOnClickListener(v -> {
            if(binding.edComment.getText().toString().equals("")){
                Toast.makeText(AddTaskAct.this, getString(R.string.please_add_task), Toast.LENGTH_SHORT).show();
            }
            else {
                if (NetworkAvailablity.checkNetworkStatus(AddTaskAct.this)) AddTaskkk();
                else
                    App.showToast(AddTaskAct.this, getString(R.string.network_failure), Toast.LENGTH_LONG);
            }
        });


        binding.ivCross.setOnClickListener(v -> {
             finish();
        });
    }

    private void AddTaskkk() {
        DataManager.getInstance().showProgressMessage(AddTaskAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("worker_id", DataManager.getInstance().getUserData(AddTaskAct.this).result.id);
        map.put("name", binding.edComment.getText().toString());
        Log.e(TAG, "Add Task Request " + map);
        Call<Map<String,String>> defactCall = apiInterface.addTask(map);
        defactCall.enqueue(new Callback<Map<String,String>>() {
            @Override
            public void onResponse(Call<Map<String,String>> call, Response<Map<String,String>> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    Map<String,String> data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Add Task Response :" + responseString);
                    if (data.get("status").equals("1")) {
                        finish();
                    } else if (data.get("status").equals("0")) {

                    }


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
