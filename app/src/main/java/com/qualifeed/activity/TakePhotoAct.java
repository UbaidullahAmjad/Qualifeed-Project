package com.qualifeed.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.qualifeed.R;
import com.qualifeed.adapter.DefactAdapter;
import com.qualifeed.databinding.ActivityTakePicBinding;
import com.qualifeed.model.DefactModel;
import com.qualifeed.model.QrCodeModel;
import com.qualifeed.retrofit.ApiClient;
import com.qualifeed.retrofit.QualifeedInterface;
import com.qualifeed.utils.App;
import com.qualifeed.utils.DataManager;
import com.qualifeed.utils.NetworkAvailablity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TakePhotoAct extends AppCompatActivity {
    public String TAG = "TakePhotoAct";
    ActivityTakePicBinding binding;
    QualifeedInterface apiInterface;
    DefactAdapter adapter;
    ArrayList<DefactModel.Result> arrayList;
    String selectDefact = "", imgPath = "", productId = "",path="";
    String fileUri;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(QualifeedInterface.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_take_pic);
        initViews();
    }

    private void initViews() {
        arrayList = new ArrayList<>();

        adapter = new DefactAdapter(TakePhotoAct.this, arrayList);
        binding.spinnerDefact.setAdapter(adapter);


        if (getIntent() != null) {
            productId = getIntent().getStringExtra("product_id");
            imgPath = getIntent().getStringExtra("defact_img");
            Glide.with(TakePhotoAct.this)
                    .load(imgPath)
                    .centerCrop()
                    .into(binding.ivPic);
            if (NetworkAvailablity.checkNetworkStatus(TakePhotoAct.this)) UploadShareDefact();
            else
                App.showToast(TakePhotoAct.this, getString(R.string.network_failure), Toast.LENGTH_LONG);
        }


        binding.spinnerDefact.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectDefact = arrayList.get(position).id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (NetworkAvailablity.checkNetworkStatus(TakePhotoAct.this)) getAllDefactList();
        else
            App.showToast(TakePhotoAct.this, getString(R.string.network_failure), Toast.LENGTH_LONG);


        binding.btnSave.setOnClickListener(v -> {
            if (selectDefact.equals(""))
                Toast.makeText(this, getString(R.string.please_select_defact), Toast.LENGTH_SHORT).show();
            else {
                SaveAlert();
            }
        });


        binding.ivShare.setOnClickListener(v -> {
            if(!path.equals("")) {
                shareImage(path);
            }
        });

        binding.ivDelete.setOnClickListener(v -> finish());

    }


    private void getAllDefactList() {
        DataManager.getInstance().showProgressMessage(TakePhotoAct.this, getString(R.string.please_wait));
        Call<DefactModel> defactCall = apiInterface.getDefact();
        defactCall.enqueue(new Callback<DefactModel>() {
            @Override
            public void onResponse(Call<DefactModel> call, Response<DefactModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    DefactModel data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "DefactList Response :" + responseString);
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
            public void onFailure(Call<DefactModel> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }


    private void saveDefact() {
        DataManager.getInstance().showProgressMessage(TakePhotoAct.this, getString(R.string.please_wait));
        MultipartBody.Part filePart, filePart1;
        if (!imgPath.equalsIgnoreCase("")) {
            File file = DataManager.getInstance().saveBitmapToFile(new File(imgPath));
            filePart = MultipartBody.Part.createFormData("defact_image", file.getName(), RequestBody.create(MediaType.parse("defact_image/*"), file));
        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }
        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), DataManager.getInstance().getUserData(TakePhotoAct.this).result.id);
        RequestBody defact_id = RequestBody.create(MediaType.parse("text/plain"), selectDefact);
        RequestBody product_id = RequestBody.create(MediaType.parse("text/plain"), productId);

        Call<Map<String, String>> signupCall = apiInterface.addDefact(user_id, defact_id, product_id, filePart);
        signupCall.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    Map<String, String> data = response.body();
                    if (data.get("status").equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        String request_id = data.get("request_id");
                        Log.e("MapMap", "Save Defact RESPONSE" + dataResponse);
                        Toast.makeText(TakePhotoAct.this, getString(R.string.defact_add_successfully), Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (data.get("status").equals("0")) {
                        Toast.makeText(TakePhotoAct.this, data.get("message"), Toast.LENGTH_SHORT).show();
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


    private void UploadShareDefact() {
        MultipartBody.Part filePart;
        if (!imgPath.equalsIgnoreCase("")) {
            File file = DataManager.getInstance().saveBitmapToFile(new File(imgPath));
            filePart = MultipartBody.Part.createFormData("defact_image", file.getName(), RequestBody.create(MediaType.parse("defact_image/*"), file));
        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }


        Call<Map<String, String>> signupCall = apiInterface.uplloadImageDefact(  filePart);
        signupCall.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    Map<String, String> data = response.body();
                    if (data.get("status").equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        String request_id = data.get("request_id");
                        Log.e("MapMap", "Upload Share  Defact RESPONSE" + dataResponse);
                        path = data.get("defact_image");
                    } else if (data.get("status").equals("0")) {
                        Toast.makeText(TakePhotoAct.this, data.get("message"), Toast.LENGTH_SHORT).show();
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


    public void shareImage(String url) {
        Picasso.with(getApplicationContext()).load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                try {
                    File mydir = new File(Environment.getExternalStorageDirectory() + "/11zon");
                    if (!mydir.exists()) {
                        mydir.mkdirs();
                    }

                    fileUri = mydir.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".jpg";
                    FileOutputStream outputStream = new FileOutputStream(fileUri);

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
                Uri uri= Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), BitmapFactory.decodeFile(fileUri),null,null));
                // use intent to share image
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/*");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share, "Share Image"));
            }
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
    }


    public void SaveAlert(){
        AlertDialog.Builder  builder1 = new AlertDialog.Builder(TakePhotoAct.this);
        builder1.setMessage(getResources().getString(R.string.are_you_sure_you_want_to_save_this_defact));
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (NetworkAvailablity.checkNetworkStatus(TakePhotoAct.this)) saveDefact();
                        else App.showToast(TakePhotoAct.this, getString(R.string.network_failure), Toast.LENGTH_LONG);

                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


}
