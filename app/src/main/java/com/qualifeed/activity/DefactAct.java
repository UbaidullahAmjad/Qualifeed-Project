package com.qualifeed.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.qualifeed.BuildConfig;
import com.qualifeed.R;
import com.qualifeed.adapter.MainDefactAdapter;
import com.qualifeed.databinding.ActivityDefactBinding;
import com.qualifeed.model.DefactModel;
import com.qualifeed.model.GetDefactModel;
import com.qualifeed.model.LoginModel;
import com.qualifeed.model.QrCodeModel;
import com.qualifeed.retrofit.ApiClient;
import com.qualifeed.retrofit.Constant;
import com.qualifeed.retrofit.QualifeedInterface;
import com.qualifeed.utils.App;
import com.qualifeed.utils.DataManager;
import com.qualifeed.utils.NetworkAvailablity;
import com.qualifeed.utils.SessionManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DefactAct extends AppCompatActivity {
    public String TAG ="DefactAct";
    ActivityDefactBinding binding;
    QrCodeModel qrCodeModel;
    String str_image_path = "";
    private static final int REQUEST_CAMERA = 1;
    private static final int MY_PERMISSION_CONSTANT = 5;
    private Uri uriSavedImage;
    QualifeedInterface apiInterface;
    ArrayList<GetDefactModel.Result>arrayList;
    MainDefactAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(QualifeedInterface.class);
        binding =  DataBindingUtil.setContentView(this, R.layout.activity_defact);
        initViews();
    }

    private void initViews() {
        if(getIntent()!=null) {

            qrCodeModel = new Gson().fromJson(getIntent().getStringExtra("scanData"), QrCodeModel.class);
            Log.e(TAG, "QrCode Data :" + getIntent().getStringExtra("scanData"));


            binding.partNumber.setText("Part Number : " + qrCodeModel.result.partNumber);
            binding.partPlacement.setText("Part Placement : "+ qrCodeModel.result.partPlacement);
            binding.carType.setText("Car Type : "+ qrCodeModel.result.carType);
        }


        binding.btnRepair.setOnClickListener(v -> {
           // startActivity(new Intent(DefactAct.this,RepairAct.class).putExtra("product_id",qrCodeModel.result.id+""));
           // finish();
        });

        binding.btnAdd.setOnClickListener(v ->{
            if(checkPermisssionForReadStorage())
                showImageSelection();
        } );

        binding.btnScrap.setOnClickListener(v -> {
            startActivity(new Intent(DefactAct.this,AddScrapAct.class).putExtra("product_id",qrCodeModel.result.id+""));
        });

        arrayList = new ArrayList<>();

        adapter = new MainDefactAdapter(DefactAct.this,arrayList);
        binding.rvDefact.setAdapter(adapter);

    }


    @Override
    public void onBackPressed() {
      //  super.onBackPressed();

        startActivity(new Intent(DefactAct.this,ScanAct.class));
        finish();
    }




    public void showImageSelection() {

        final Dialog dialog = new Dialog(DefactAct.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Widget_Material_ListPopupWindow;
        dialog.setContentView(R.layout.dialog_show_image_selection);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        LinearLayout layoutCamera = (LinearLayout) dialog.findViewById(R.id.layoutCemera);

        layoutCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                openCamera();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void openCamera() {

        File dirtostoreFile = new File(Environment.getExternalStorageDirectory() + "/Qualifeed/Images/");

        if (!dirtostoreFile.exists()) {
            dirtostoreFile.mkdirs();
        }

        String timestr = DataManager.getInstance().convertDateToString(Calendar.getInstance().getTimeInMillis());

        File tostoreFile = new File(Environment.getExternalStorageDirectory() + "/Qualifeed/Images/" + "IMG_" + timestr + ".jpg");

        str_image_path = tostoreFile.getPath();

        uriSavedImage = FileProvider.getUriForFile(DefactAct.this,
                BuildConfig.APPLICATION_ID + ".provider",
                tostoreFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

        startActivityForResult(intent, REQUEST_CAMERA);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.e("Result_code", requestCode + "");
            if (requestCode == REQUEST_CAMERA) {
                startActivity(new Intent(DefactAct.this,TakePhotoAct.class)
                        .putExtra("product_id",qrCodeModel.result.id)
                        .putExtra("defact_img",str_image_path));

            }

        }
    }


    //CHECKING FOR Camera STATUS
    public boolean checkPermisssionForReadStorage() {
        if (ContextCompat.checkSelfPermission(DefactAct.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(DefactAct.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ||

                ContextCompat.checkSelfPermission(DefactAct.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(DefactAct.this,
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(DefactAct.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(DefactAct.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)


            ) {


                ActivityCompat.requestPermissions(DefactAct.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);

            } else {

                //explain("Please Allow Location Permission");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(DefactAct.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);
            }
            return false;
        } else {

            //  explain("Please Allow Location Permission");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    boolean camera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean read_external_storage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean write_external_storage = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (camera && read_external_storage && write_external_storage) {
                        showImageSelection();
                    } else {
                        Toast.makeText(DefactAct.this, " permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DefactAct.this, "  permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                }
                // return;
            }


        }
    }


    private void getAddedDefact() {
        DataManager.getInstance().showProgressMessage(DefactAct.this, getString(R.string.please_wait));
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
                    binding.tvSize.setText(arrayList.size()+"");

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
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkAvailablity.checkNetworkStatus(DefactAct.this)) getAddedDefact();
        else
            App.showToast(DefactAct.this, getString(R.string.network_failure), Toast.LENGTH_LONG);

    }
}
