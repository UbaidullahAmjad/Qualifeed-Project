package com.qualifeed.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.qualifeed.R;
import com.qualifeed.adapter.TypeAdapter;
import com.qualifeed.databinding.ActivityProductTypeBinding;
import com.qualifeed.databinding.ActivityProductTypeCopyBinding;
import com.qualifeed.model.ProductTypeModel;
import com.qualifeed.retrofit.ApiClient;
import com.qualifeed.retrofit.QualifeedInterface;
import com.qualifeed.retrofit.onPosListener;
import com.qualifeed.utils.DataManager;
import com.qualifeed.utils.NetworkAvailablity;
import com.qualifeed.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductTypeCopy extends AppCompatActivity implements onPosListener {
    public String TAG = "ProductTypeCopy";
    ActivityProductTypeCopyBinding binding;
    QualifeedInterface apiInterface;
    String preId="",type1="",type2="";
    TypeAdapter adapter;
    ArrayList<ProductTypeModel.Result> arrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(QualifeedInterface.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_type_copy);
        initViews();
    }

    private void initViews() {
        arrayList = new ArrayList<>();

     /*   Glide.with(ProductTypeCopy.this).load(DataManager.getInstance().getUserData(ProductTypeCopy.this).result.image)
                .override(40,40)
                .error(R.drawable.place_holder)
                .into(binding.ivUser);*/

        adapter = new TypeAdapter(ProductTypeCopy.this,arrayList,ProductTypeCopy.this);
        binding.spinnerType.setAdapter(adapter);


        if(getIntent()!=null){
            preId = getIntent().getStringExtra("pre");
            type1 = getIntent().getStringExtra("type1");
            type2 = getIntent().getStringExtra("type2");
            if(NetworkAvailablity.checkNetworkStatus(ProductTypeCopy.this)) getProductType();
            else Toast.makeText(ProductTypeCopy.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
        }


        binding.ivCross.setOnClickListener(v -> {

            startActivity(new Intent(ProductTypeCopy.this,DefectListAct.class)
                    .putExtra("pre",preId)
                    .putExtra("type1",type1.trim())
                    .putExtra("type2",type2.trim()));
            finish();
                }/*sessionDialog()*/


        );

    /*    if(NetworkAvailablity.checkNetworkStatus(ProductTypeCopy.this))  startTimer(productId);
        else Toast.makeText(ProductTypeCopy.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();*/

    }



    public void startTimer(String productId){
        //  DataManager.getInstance().showProgressMessage(ProductTypeCopy.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("product_id", "2");
        map.put("worker_id", DataManager.getInstance().getUserData(ProductTypeCopy.this).result.id);
        Log.e(TAG, "Start Timer Request " + map);
        Call<Map<String,String>> loginCall = apiInterface.startTimerrrr(map);
        loginCall.enqueue(new Callback<Map<String,String>>() {
            @Override
            public void onResponse(Call<Map<String,String>> call, Response<Map<String,String>> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    Map<String,String> data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Start Timer Response :" + responseString);
                    if (data.get("status").equals("1")) {


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



    public void getProductType(){
        DataManager.getInstance().showProgressMessage(ProductTypeCopy.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        Call<ProductTypeModel> loginCall = apiInterface.getProductType();
        loginCall.enqueue(new Callback<ProductTypeModel>() {
            @Override
            public void onResponse(Call<ProductTypeModel> call, Response<ProductTypeModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    ProductTypeModel data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Get Product Type Response :" + responseString);
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
            public void onFailure(Call<ProductTypeModel> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    @Override
    public void onPos(int pos, String type) {
      //  SessionManager.writeString();
        startActivity(new Intent(ProductTypeCopy.this,DefectListCopyAct.class)
                .putExtra("pre",preId)
                .putExtra("type1",arrayList.get(pos).productType1.trim())
                .putExtra("type2",arrayList.get(pos).productType2.trim()));
        finish();
    }



 /*   public void sessionDialog(){
        final Dialog dialog = new Dialog(ProductTypeCopy.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_session);

        TextView tvYes =  dialog.findViewById(R.id.tvYes);
        TextView tvNo =  dialog.findViewById(R.id.tvNo);

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkAvailablity.checkNetworkStatus(ProductTypeCopy.this)) repairFun("Session");
                else Toast.makeText(ProductTypeCopy.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();

    }




    private void repairFun(String status) {
        DataManager.getInstance().showProgressMessage(ProductTypeCopy.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("product_id", productId);
        map.put("worker_id",DataManager.getInstance().getUserData(ProductTypeCopy.this).result.id);
        Log.e(TAG, "Add Repair time Request " + map);
        Call<Map<String,String>> defactCall = apiInterface.addRepair(map);
        defactCall.enqueue(new Callback<Map<String,String>>() {
            @Override
            public void onResponse(Call<Map<String,String>> call, Response<Map<String,String>> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    Map<String,String> data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Add Repair time Response :" + responseString);
                    if (data.get("status").equals("1")) {
                        if(status.equals("Session")){
                            startActivity(new Intent(ProductTypeCopy.this,ToDoAct.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }
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
    }*/

}

