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
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.qualifeed.databinding.ActivityRepairBinding;
import com.qualifeed.model.GetDefactModel;
import com.qualifeed.retrofit.ApiClient;
import com.qualifeed.retrofit.QualifeedInterface;
import com.qualifeed.utils.App;
import com.qualifeed.utils.DataManager;
import com.qualifeed.utils.NetworkAvailablity;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepairAct extends AppCompatActivity {
    public String TAG = "RepairAct";
    ActivityRepairBinding binding;
    QualifeedInterface apiInterface;
    ArrayList<GetDefactModel.Result> arrayList;
    MainDefactAdapter adapter;
    String productId = "";
    private Handler customHandler = new Handler();
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    long secs, mins;
    String str_image_path = "", preId = "", type1 = "", type2 = "";
    private static final int REQUEST_CAMERA = 1;
    private static final int MY_PERMISSION_CONSTANT = 5;
    private Uri uriSavedImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(QualifeedInterface.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_repair);
        initViews();
    }

    private void initViews() {
        arrayList = new ArrayList<>();
        if (getIntent() != null) {
            //  productId = getIntent().getStringExtra("product_id");
            preId = getIntent().getStringExtra("pre");
            type1 = getIntent().getStringExtra("type1");
            type2 = getIntent().getStringExtra("type2");

        }
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);

        //adapter = new MainDefactAdapter(RepairAct.this,arrayList);
        // binding.rvDefact.setAdapter(adapter);

        //   if (NetworkAvailablity.checkNetworkStatus(RepairAct.this)) getAddedDefact();
        //    else
        //      App.showToast(RepairAct.this, getString(R.string.network_failure), Toast.LENGTH_LONG);


        binding.btnDone.setOnClickListener(v -> {
         /*  if(binding.btnStart1.getText().toString().equals("Start")){
               binding.btnStart1.setText("Stop");
               startTime = SystemClock.uptimeMillis();
               customHandler.postDelayed(updateTimerThread, 0);
           }
           else {*/
            // binding.btnStart1.setText("Start");

            if (str_image_path.equals(""))
                Toast.makeText(this, getString(R.string.please_add_photo), Toast.LENGTH_SHORT).show();
            else {
                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(updateTimerThread);

                if (NetworkAvailablity.checkNetworkStatus(RepairAct.this)) repairFun111();
                else
                    Toast.makeText(RepairAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }


            //  }
        });


        binding.rlPic.setOnClickListener(v -> {
            if (checkPermisssionForReadStorage())
                showImageSelection();
        });


        binding.ivCross.setOnClickListener(v -> {
            sessionDialog();
        });


    }

    private void repairFun111() {
        DataManager.getInstance().showProgressMessage(RepairAct.this, getString(R.string.please_wait));
        MultipartBody.Part filePart;
        if (!str_image_path.equalsIgnoreCase("")) {
            File file = DataManager.getInstance().saveBitmapToFile(new File(str_image_path));
            filePart = MultipartBody.Part.createFormData("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }
        //  RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), DataManager.getInstance().getUserData(AddScrapAct.this).result.id);
        // RequestBody product_id = RequestBody.create(MediaType.parse("text/plain"), productId);

        RequestBody ref = RequestBody.create(MediaType.parse("text/plain"), preId);
        RequestBody typ1 = RequestBody.create(MediaType.parse("text/plain"), type1);
        RequestBody typ2 = RequestBody.create(MediaType.parse("text/plain"), type2);
        RequestBody datee = RequestBody.create(MediaType.parse("text/plain"), DataManager.getCurrent1());
        RequestBody workerId = RequestBody.create(MediaType.parse("text/plain"), DataManager.getInstance().getUserData(RepairAct.this).result.id);

        RequestBody timer = RequestBody.create(MediaType.parse("text/plain"), binding.tvTimer.getText().toString());

        Call<Map<String, String>> signupCall = apiInterface.repairProduct(ref, typ1, typ2, datee, workerId, timer, filePart);
        signupCall.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    Map<String, String> data = response.body();
                    if (data.get("status").equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "Save Repair RESPONSE" + dataResponse);
                        //   Toast.makeText(AddScrapAct.this, getString(R.string.scrap_add_successfully), Toast.LENGTH_SHORT).show();
                        finish();
                     /*   startActivity(new Intent(AddScrapAct.this, ToDoAct.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();*/
                    } else if (data.get("status").equals("0")) {
                        Toast.makeText(RepairAct.this, data.get("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }

        });
    }


    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            secs = (int) (updatedTime / 1000);
            mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            binding.tvTimer.setText(String.format("%02d", mins) + ":"
                    + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }

    };


    private void repairFun(String status) {
        DataManager.getInstance().showProgressMessage(RepairAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("productref", preId);
        map.put("product_type_1", type1);
        map.put("product_type_2", type2);
        map.put("worker_id", DataManager.getInstance().getUserData(RepairAct.this).result.id);
        Log.e(TAG, "Add Repair time Request " + map);
        Call<Map<String, String>> defactCall = apiInterface.addRepair(map);
        defactCall.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    Map<String, String> data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Add Repair time Response :" + responseString);
                    if (data.get("status").equals("1")) {
                        if (status.equals("Session")) {
                            startActivity(new Intent(RepairAct.this, ToDoAct.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }
                        finish();
                    } else if (data.get("status").equals("0")) {

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }


    public void sessionDialog() {
        final Dialog dialog = new Dialog(RepairAct.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_session);

        TextView tvYes = dialog.findViewById(R.id.tvYes);
        TextView tvNo = dialog.findViewById(R.id.tvNo);

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkAvailablity.checkNetworkStatus(RepairAct.this)) repairFun("Session");
                else
                    Toast.makeText(RepairAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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


    public void showImageSelection() {

        final Dialog dialog = new Dialog(RepairAct.this);
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

        uriSavedImage = FileProvider.getUriForFile(RepairAct.this,
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
            binding.tvUpload.setVisibility(View.GONE);
            binding.ivUpload.setVisibility(View.GONE);
            binding.ivPic.setVisibility(View.VISIBLE);
            if (requestCode == REQUEST_CAMERA) {

                Glide.with(RepairAct.this)
                        .load(str_image_path)
                        .centerCrop()
                        .into(binding.ivPic);

            }

        }
    }


    //CHECKING FOR Camera STATUS
    public boolean checkPermisssionForReadStorage() {
        if (ContextCompat.checkSelfPermission(RepairAct.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(RepairAct.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ||

                ContextCompat.checkSelfPermission(RepairAct.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(RepairAct.this,
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(RepairAct.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(RepairAct.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)


            ) {


                ActivityCompat.requestPermissions(RepairAct.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);

            } else {

                //explain("Please Allow Location Permission");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(RepairAct.this,
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
                        Toast.makeText(RepairAct.this, " permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RepairAct.this, "  permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                }
                // return;
            }


        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);
    }
}
