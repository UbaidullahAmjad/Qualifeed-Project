package com.qualifeed.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.qualifeed.R;
import com.qualifeed.adapter.TaskAdapter;
import com.qualifeed.adapter.TrainingAdapter;
import com.qualifeed.databinding.ActivityTaskBinding;
import com.qualifeed.databinding.ActivityTrainingBinding;
import com.qualifeed.model.GetDefactModel;
import com.qualifeed.model.QrCodeModel;
import com.qualifeed.model.TaskModel;
import com.qualifeed.retrofit.ApiClient;
import com.qualifeed.retrofit.QualifeedInterface;
import com.qualifeed.retrofit.onPosListener;
import com.qualifeed.utils.App;
import com.qualifeed.utils.DataManager;
import com.qualifeed.utils.NetworkAvailablity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskListAct extends AppCompatActivity implements onPosListener {
    public String TAG ="TaskListAct";
    ActivityTaskBinding binding;
    QualifeedInterface apiInterface;
    ArrayList<TaskModel.Result> arrayList;
    TaskAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(QualifeedInterface.class);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_task);
        initViews();
    }


    private void initViews() {
        arrayList = new ArrayList<>();

        adapter =  new TaskAdapter(TaskListAct.this,arrayList,TaskListAct.this);
        binding.rvTask.setAdapter(adapter);

         binding.actionAdd.setOnClickListener(v -> {
            startActivity(new Intent(this,AddTaskAct.class));
         });

    }

    private void getAllTask() {
        DataManager.getInstance().showProgressMessage(TaskListAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("worker_id", DataManager.getInstance().getUserData(TaskListAct.this).result.id);
        Log.e(TAG, "Get All Tasks Request " + map);
        Call<TaskModel> defactCall = apiInterface.getTaskList(map);
        defactCall.enqueue(new Callback<TaskModel>() {
            @Override
            public void onResponse(Call<TaskModel> call, Response<TaskModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    TaskModel data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Get All Tasks Response :" + responseString);
                    if (data.status.equals("1")) {
                        binding.tvNotFound.setVisibility(View.GONE);
                        arrayList.clear();
                        arrayList.addAll(data.result);
                        adapter.notifyDataSetChanged();

                    } else if (data.status.equals("0")) {
                        arrayList.clear();
                        adapter.notifyDataSetChanged();
                        binding.tvNotFound.setVisibility(View.VISIBLE);

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<TaskModel> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }


    private void doneStaus(String taskId) {
        DataManager.getInstance().showProgressMessage(TaskListAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("task_id", taskId);
        map.put("status", "Complete");
        Log.e(TAG, "Update Tasks Request " + map);
        Call<Map<String,String>> defactCall = apiInterface.doneTask(map);
        defactCall.enqueue(new Callback<Map<String,String>>() {
            @Override
            public void onResponse(Call<Map<String,String>> call, Response<Map<String,String>> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    Map<String,String> data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Update Tasks Response :" + responseString);
                    if (data.get("status").equals("1")) {
                        if (NetworkAvailablity.checkNetworkStatus(TaskListAct.this)) getAllTask();
                        else
                            App.showToast(TaskListAct.this, getString(R.string.network_failure), Toast.LENGTH_LONG);

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



    @Override
    public void onPos(int pos,String type) {
     if(type.equals("Change")){
         if (NetworkAvailablity.checkNetworkStatus(TaskListAct.this)) doneStaus(arrayList.get(pos).id);
         else
             App.showToast(TaskListAct.this, getString(R.string.network_failure), Toast.LENGTH_LONG);
     }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkAvailablity.checkNetworkStatus(TaskListAct.this)) getAllTask();
        else
            App.showToast(TaskListAct.this, getString(R.string.network_failure), Toast.LENGTH_LONG);
    }
}
