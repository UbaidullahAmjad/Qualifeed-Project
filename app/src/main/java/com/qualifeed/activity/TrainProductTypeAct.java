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
import com.qualifeed.adapter.TrainTypeAdapter;
import com.qualifeed.adapter.TypeAdapter;
import com.qualifeed.databinding.ActivityTrainingProductBinding;
import com.qualifeed.model.ProductTypeModel;
import com.qualifeed.model.ProductTypeTrainModel;
import com.qualifeed.retrofit.ApiClient;
import com.qualifeed.retrofit.QualifeedInterface;
import com.qualifeed.retrofit.onPosListener;
import com.qualifeed.utils.DataManager;
import com.qualifeed.utils.NetworkAvailablity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainProductTypeAct extends AppCompatActivity implements onPosListener {
    public String TAG = "TrainProductTypeAct";
    ActivityTrainingProductBinding binding;
    QualifeedInterface apiInterface;
    TrainTypeAdapter adapter;
    ArrayList<ProductTypeTrainModel.Result> arrayList;
    String chk = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(QualifeedInterface.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_training_product);
        initViews();
    }

    private void initViews() {
        if (getIntent() != null) chk = getIntent().getStringExtra("chkType");

        arrayList = new ArrayList<>();

        adapter = new TrainTypeAdapter(TrainProductTypeAct.this, arrayList, TrainProductTypeAct.this);
        binding.spinnerType.setAdapter(adapter);


        if (NetworkAvailablity.checkNetworkStatus(TrainProductTypeAct.this)) getProductType();
        else
            Toast.makeText(TrainProductTypeAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();

    }


    public void getProductType() {
        DataManager.getInstance().showProgressMessage(TrainProductTypeAct.this, getString(R.string.please_wait));
        Call<ProductTypeTrainModel> loginCall = apiInterface.getAllTraining();
        loginCall.enqueue(new Callback<ProductTypeTrainModel>() {
            @Override
            public void onResponse(Call<ProductTypeTrainModel> call, Response<ProductTypeTrainModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    ProductTypeTrainModel data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Get Product Type Response :" + responseString);
                    if (data.getStatus().equals("1")) {
                        arrayList.clear();
                        arrayList.addAll(data.getResult());
                        adapter.notifyDataSetChanged();

                    } else if (data.getStatus().equals("0")) {
                        arrayList.clear();
                        adapter.notifyDataSetChanged();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ProductTypeTrainModel> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    @Override
    public void onPos(int pos, String type) {

        if (chk.equals("Training")) {
            startActivity(new Intent(TrainProductTypeAct.this, TrainningAct.class).putExtra("scanData", arrayList.get(pos)));
            finish();
        } else if (chk.equals("Control")) {
            startActivity(new Intent(TrainProductTypeAct.this, CheckControlAct.class).putExtra("scanData", arrayList.get(pos)));
            finish();
        }
    }
}
