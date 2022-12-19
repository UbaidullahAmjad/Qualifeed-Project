package com.qualifeed.activity;

import android.content.Intent;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.qualifeed.R;
import com.qualifeed.adapter.CheckControlAdapter;
import com.qualifeed.adapter.TrainingAdapter;
import com.qualifeed.databinding.ActivityCheckControlBinding;
import com.qualifeed.databinding.ActivityTrainingBinding;
import com.qualifeed.model.DefectModelMain;
import com.qualifeed.model.DefectModelMain22;
import com.qualifeed.model.GetDefactModel;
import com.qualifeed.model.ProductTypeTrainModel;
import com.qualifeed.model.QrCodeModel;
import com.qualifeed.retrofit.ApiClient;
import com.qualifeed.retrofit.QualifeedInterface;
import com.qualifeed.utils.App;
import com.qualifeed.utils.DataManager;
import com.qualifeed.utils.NetworkAvailablity;
import com.qualifeed.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckControlAct extends AppCompatActivity {
    public String TAG ="CheckControlAct";
    ActivityCheckControlBinding binding;
    ProductTypeTrainModel.Result qrCodeModel;
    QualifeedInterface apiInterface;
    ArrayList<DefectModelMain22.Result> arrayList;
    CheckControlAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(QualifeedInterface.class);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_check_control);
        initViews();
    }

    private void initViews() {

        arrayList = new ArrayList<>();
        if(getIntent()!=null) {
            qrCodeModel = (ProductTypeTrainModel.Result) getIntent().getSerializableExtra("scanData");
            Log.e(TAG, "QrCode Data :" + qrCodeModel.getId());
        }

        adapter =  new CheckControlAdapter(CheckControlAct.this,arrayList);
        binding.rvCheck.setAdapter(adapter);


        if (NetworkAvailablity.checkNetworkStatus(CheckControlAct.this)) getAllDefect(/*qrCodeModel.getId()*/qrCodeModel.getProductType1(),qrCodeModel.getProductType2());
        else
            App.showToast(CheckControlAct.this, getString(R.string.network_failure), Toast.LENGTH_LONG);





        binding.btnDone.setOnClickListener(v -> {
            SessionManager.writeString(CheckControlAct.this,"ChkControl","true");
            startActivity(new Intent(this,ToDoAct.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });
    }


 /*   private void getAddedDefact() {
        DataManager.getInstance().showProgressMessage(CheckControlAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("product_id", qrCodeModel.result.id);
        Log.e(TAG, "Get Added Defact Request " + map);
        Call<GetDefactModel> defactCall = apiInterface.getDefactListttt(map);
        defactCall.enqueue(new Callback<GetDefactModel>() {
            @Override
            public void onResponse(Call<GetDefactModel> call, Response<GetDefactModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    GetDefactModel data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Get Added Defact Response :" + responseString);
                    if (data.status.equals("1")) {
                        arrayList.clear();
                        arrayList.addAll(data.result);
                        adapter.notifyDataSetChanged();

                    } else if (data.status.equals("0")) {
                        arrayList.clear();
                        adapter.notifyDataSetChanged();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GetDefactModel> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }*/


    private void getAllDefect(/*String productTypeId*/  String typ1,String typ2) {
        DataManager.getInstance().showProgressMessage(CheckControlAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
     //   map.put("product_type_id", productTypeId);
        map.put("product_type_1", typ1.trim());
        map.put("product_type_2", typ2.trim());
        Log.e(TAG, "Get Product Type Request " + map);
        Call<DefectModelMain22> loginCall = apiInterface.getDefectListtCopy22(map);
        loginCall.enqueue(new Callback<DefectModelMain22>() {
            @Override
            public void onResponse(Call<DefectModelMain22> call, Response<DefectModelMain22> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    DefectModelMain22 data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Get Product Type Response :" + responseString);
                    if (data.getStatus().equals("1")) {
                        binding.tvNotFound.setVisibility(View.GONE);
                        arrayList.clear();
                        arrayList.addAll(data.getResult());
                        adapter.notifyDataSetChanged();

                    } else if (data.getStatus().equals("0")) {
                        binding.tvNotFound.setVisibility(View.VISIBLE);
                        arrayList.clear();
                        adapter.notifyDataSetChanged();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<DefectModelMain22> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }



}
