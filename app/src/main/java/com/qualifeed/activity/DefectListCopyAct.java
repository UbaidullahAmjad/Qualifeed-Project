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
import com.qualifeed.adapter.DefectAdapterMain;
import com.qualifeed.adapter.DefectAdapterMainCopy;
import com.qualifeed.adapter.TypeAdapter;
import com.qualifeed.databinding.ActivityDefectListBinding;
import com.qualifeed.databinding.ActivityProductTypeBinding;
import com.qualifeed.model.DefectModelMain;
import com.qualifeed.model.DefectModelMainCopy;
import com.qualifeed.model.ProductTypeModel;
import com.qualifeed.retrofit.ApiClient;
import com.qualifeed.retrofit.QualifeedInterface;
import com.qualifeed.retrofit.onPosListener;
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

public class DefectListCopyAct extends AppCompatActivity implements onPosListener {
    public String TAG = "DefectListCopyAct";
    ActivityDefectListBinding binding;
    QualifeedInterface apiInterface;
    String productId = "", subAdminId = "", defectId = "",preId="",type1="",type2="";
    DefectAdapterMainCopy adapter;
    ArrayList<DefectModelMainCopy.Result> arrayList;
    public static ArrayList<DefectModelMainCopy.Result.ProductDefectsRequest> arrayList2;
    String str_image_path = "";
    private static final int REQUEST_CAMERA = 1;
    private static final int MY_PERMISSION_CONSTANT = 5;
    private Uri uriSavedImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(QualifeedInterface.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_defect_list);
        initViews();
    }

    private void initViews() {
        arrayList = new ArrayList<>();
        arrayList2 = new ArrayList<>();

        adapter = new DefectAdapterMainCopy(DefectListCopyAct.this, arrayList,DefectListCopyAct.this);
        binding.rvDefect.setAdapter(adapter);


        if (getIntent() != null) {
        //    productId = getIntent().getStringExtra("productId");
        //    subAdminId = getIntent().getStringExtra("subAdminId");
            preId = getIntent().getStringExtra("pre");
            type1 = getIntent().getStringExtra("type1");
            type2 = getIntent().getStringExtra("type2");

           /* if (NetworkAvailablity.checkNetworkStatus(DefectListCopyAct.this)) getAllDefect(productId);
            else
                Toast.makeText(DefectListCopyAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();*/
        }



        binding.btnSusDefect.setOnClickListener(v -> {
            startActivity(new Intent(DefectListCopyAct.this, SuspectDefectAct.class)
                    .putExtra("pre",preId)
                    .putExtra("type1",type1.trim())
                    .putExtra("type2",type2.trim()));
        });

        binding.layoutBlock.setOnClickListener(v -> {
            startActivity(new Intent(DefectListCopyAct.this, AddScrapAct.class)
                    .putExtra("pre",preId)
                    .putExtra("type1",type1.trim())
                    .putExtra("type2",type2.trim()));
        });

       binding.layoutRepair.setOnClickListener(v -> {
            startActivity(new Intent(DefectListCopyAct.this, RepairAct.class)
                    .putExtra("pre",preId)
                    .putExtra("type1",type1.trim())
                    .putExtra("type2",type2.trim()));
        });
/*
        binding.layoutNext.setOnClickListener(v -> {

/*  if(NetworkAvailablity.checkNetworkStatus(DefectListCopyAct.this)) repairFun("Session");
         else Toast.makeText(DefectListCopyAct.this, "", Toast.LENGTH_SHORT).show();*//*

            startActivity(new Intent(DefectListCopyAct.this, ScanCopyAct.class).putExtra("from", "end").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });
  */

        binding.ivCross.setOnClickListener(v -> {
            sessionDialog();
        });


        binding.layoutType.setOnClickListener(v -> {
            startActivity(new Intent(DefectListCopyAct.this, ProductTypeCopy.class)
                    .putExtra("pre",preId)
                    .putExtra("type1",type1.trim())
                    .putExtra("type2",type2.trim()));
            finish();
        });



    }

    private void getAllDefect() {
        DataManager.getInstance().showProgressMessage(DefectListCopyAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("product_type_1", type1.trim());
        map.put("product_type_2", type2.trim());
        map.put("productref", preId);
        Log.e(TAG, "GetProduct Type Request  " + map);
        Call<DefectModelMainCopy> loginCall = apiInterface.getDefectListt(map);
        loginCall.enqueue(new Callback<DefectModelMainCopy>() {
            @Override
            public void onResponse(Call<DefectModelMainCopy> call, Response<DefectModelMainCopy> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    DefectModelMainCopy data = response.body();
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
            public void onFailure(Call<DefectModelMainCopy> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    @Override
    public void onPos(int pos, String type) {

        if (type.equals("Detail")) {
            startActivity(new Intent(DefectListCopyAct.this, DetailAct.class).putExtra("title", arrayList.get(pos).getTitle())
                    .putExtra("img", arrayList.get(pos).getProductDefectsImage())
                    .putExtra("des", arrayList.get(pos).getComment()));
        } else if (type.equals("Add")) {
            defectId = arrayList.get(pos).getId() + "";
            if (checkPermisssionForReadStorage())
                showImageSelection();
        } else if (type.equals("Minus")) {
            defectId = arrayList.get(pos).getId() + "";
            if (arrayList2.size() != 0) {
                if (NetworkAvailablity.checkNetworkStatus(DefectListCopyAct.this))
                    deleteDefect(defectId,arrayList2.get(arrayList2.size() - 1).getId());
                else
                    Toast.makeText(DefectListCopyAct.this, getText(R.string.network_failure), Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(DefectListCopyAct.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

        }

    }


    public void showImageSelection() {

        final Dialog dialog = new Dialog(DefectListCopyAct.this);
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

        uriSavedImage = FileProvider.getUriForFile(DefectListCopyAct.this,
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
                if (NetworkAvailablity.checkNetworkStatus(DefectListCopyAct.this)) incrementDefect();
                else
                    Toast.makeText(DefectListCopyAct.this, getText(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

        }
    }


    //CHECKING FOR Camera STATUS
    public boolean checkPermisssionForReadStorage() {
        if (ContextCompat.checkSelfPermission(DefectListCopyAct.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(DefectListCopyAct.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ||

                ContextCompat.checkSelfPermission(DefectListCopyAct.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(DefectListCopyAct.this,
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(DefectListCopyAct.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(DefectListCopyAct.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)


            ) {


                ActivityCompat.requestPermissions(DefectListCopyAct.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);

            } else {

                //explain("Please Allow Location Permission");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(DefectListCopyAct.this,
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
                        Toast.makeText(DefectListCopyAct.this, " permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DefectListCopyAct.this, "  permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                }
                // return;
            }


        }
    }


    private void incrementDefect() {
        DataManager.getInstance().showProgressMessage(DefectListCopyAct.this, getString(R.string.please_wait));
        MultipartBody.Part filePart;
        if (!str_image_path.equalsIgnoreCase("")) {
            File file = DataManager.getInstance().saveBitmapToFile(new File(str_image_path));
            filePart = MultipartBody.Part.createFormData("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }
          RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), DataManager.getInstance().getUserData(DefectListCopyAct.this).result.id);
        RequestBody defect_id = RequestBody.create(MediaType.parse("text/plain"), defectId);
      //  RequestBody product_id = RequestBody.create(MediaType.parse("text/plain"), productId);

        Call<Map<String, String>> signupCall = apiInterface.addDefectMain(user_id,defect_id, filePart);
        signupCall.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    Map<String, String> data = response.body();
                    String dataResponse = new Gson().toJson(response.body());
                    Log.e("MapMap", "Save Add Scrap RESPONSE" + dataResponse);
                    if (data.get("status").equals("1")) {

                        if (NetworkAvailablity.checkNetworkStatus(DefectListCopyAct.this))
                            getAllDefect();
                        else
                            Toast.makeText(DefectListCopyAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();


                    } else if (data.get("status").equals("0")) {
                        Toast.makeText(DefectListCopyAct.this, data.get("message"), Toast.LENGTH_SHORT).show();
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

    private void deleteDefect(String defectId,String productId) {
        // DataManager.getInstance().showProgressMessage(DefectListCopyAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        //   map.put("product_id", productId);
        map.put("product_defects_id", defectId);
        map.put("product_id", productId);

        Log.e(TAG, "Delete Defect Request" + map);
        Call<Map<String, String>> loginCall = apiInterface.decrementDefect(map);
        loginCall.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    Map<String, String> data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "Delete Defect Response :" + responseString);
                    if (data.get("status").equals("1")) {
                        if (NetworkAvailablity.checkNetworkStatus(DefectListCopyAct.this))
                            getAllDefect();
                        else
                            Toast.makeText(DefectListCopyAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();

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
        final Dialog dialog = new Dialog(DefectListCopyAct.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_session);

        TextView tvYes = dialog.findViewById(R.id.tvYes);
        TextView tvNo = dialog.findViewById(R.id.tvNo);

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkAvailablity.checkNetworkStatus(DefectListCopyAct.this)) repairFun("Session");
                else
                    Toast.makeText(DefectListCopyAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
        DataManager.getInstance().showProgressMessage(DefectListCopyAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("productref", preId);
        map.put("product_type_1", type1);
        map.put("product_type_2", type2);
        map.put("worker_id", DataManager.getInstance().getUserData(DefectListCopyAct.this).result.id);
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
                            startActivity(new Intent(DefectListCopyAct.this, ToDoAct.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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


    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkAvailablity.checkNetworkStatus(DefectListCopyAct.this)) getAllDefect();
        else
            Toast.makeText(DefectListCopyAct.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();

    }
}
