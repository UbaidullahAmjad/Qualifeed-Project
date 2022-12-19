package com.qualifeed.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;
import com.google.zxing.Result;
import com.google.zxing.qrcode.encoder.QRCode;
import com.qualifeed.R;
import com.qualifeed.databinding.ActivityScanBinding;
import com.qualifeed.model.LoginModel;
import com.qualifeed.model.QrCodeModel;
import com.qualifeed.retrofit.ApiClient;
import com.qualifeed.retrofit.Constant;
import com.qualifeed.retrofit.QualifeedInterface;
import com.qualifeed.utils.App;
import com.qualifeed.utils.DataManager;
import com.qualifeed.utils.NetworkAvailablity;
import com.qualifeed.utils.SessionManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ScanAct extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    public String TAG = "ScanAct";
    ActivityScanBinding binding;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private boolean isOpen = false;

    private Handler customHandler = new Handler();
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    long secs,mins;
    QualifeedInterface apiInterface;
    private ZXingScannerView mScannerView;
    int PERMISSION_ID = 44;
    String from ="",type1="",type2="";
    String code[];



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(QualifeedInterface.class);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_scan);
        initViews();
    }



    private void initViews() {

        if(getIntent()!=null){
            from = getIntent().getStringExtra("from");
        }

        if(from.equals("start")) binding.tvTitle.setText(getText(R.string.quality_control_starting1));
        if(from.equals("end")) binding.tvTitle.setText(getText(R.string.scan_next_qr_code));


        Glide.with(ScanAct.this).load(DataManager.getInstance().getUserData(ScanAct.this).result.image)
                .override(40,40)
                .error(R.drawable.place_holder)
                .into(binding.ivUserImg);

       // initialiseDetectorsAndSources();
    //  if(checkPermissions())
    //  else requestPermissions();
        addScanner();
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);

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

    private void addScanner(){
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        contentFrame.addView(mScannerView);
    }




   /* private void initialiseDetectorsAndSources() {
        barcodeDetector = new BarcodeDetector.Builder(ScanAct.this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        cameraSource = new CameraSource.Builder(ScanAct.this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();
        binding.surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScanAct.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(binding.surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScanAct.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    if (barcodes.valueAt(0).displayValue != null && !isOpen) {
                        isOpen = true;
                        Log.e(TAG,"QR Code "+ barcodes.valueAt(0).displayValue );
                        //listener.Success(barcodes.valueAt(0).displayValue);
                       // dismiss();
                      if(NetworkAvailablity.checkNetworkStatus(ScanAct.this))  scanQrCode(barcodes.valueAt(0).displayValue);
                      else App.showToast(ScanAct.this,getString(R.string.network_failure), Toast.LENGTH_LONG);

                    }
                }
                else Toast.makeText(ScanAct.this, "Not available", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //initialiseDetectorsAndSources();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();

    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }


    private void scanQrCode1(String code) {
        DataManager.getInstance().showProgressMessage(ScanAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("code", code);
        Log.e(TAG, "QrCode Request " + map);
        Call<QrCodeModel> loginCall = apiInterface.getCodeInfo(map);
        loginCall.enqueue(new Callback<QrCodeModel>() {
            @Override
            public void onResponse(Call<QrCodeModel> call, Response<QrCodeModel> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    QrCodeModel data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "QrCode Response :" + responseString);
                    if (data.status.equals("1")) {
                      //  startActivity(new Intent(ScanAct.this,DefactAct.class).putExtra("scanData",responseString));
                     //   finish();

                        startActivity(new Intent(ScanAct.this,ProductTypeAct.class).putExtra("productId",data.result.id)
                        .putExtra("subAdminId",data.result.subAdminId));
                        finish();

                    } else if (data.status.equals("0")) {
                       // isOpen = false;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mScannerView.resumeCameraPreview(ScanAct.this);
                            }
                        }, 2000);
                        App.showToast(ScanAct.this, data.message, Toast.LENGTH_SHORT);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<QrCodeModel> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }


    private void scanQrCode(String Qcode) {
        code = Qcode.split("-");
        DataManager.getInstance().showProgressMessage(ScanAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("name", DataManager.getInstance().getUserData(ScanAct.this).result.userName);
        map.put("productref", code[0]);
        map.put("product_type_1", code[1]);
        map.put("product_type_2", code[2]);
        map.put("worker_id", DataManager.getInstance().getUserData(ScanAct.this).result.id);
        Log.e(TAG, "QrCode Request " + map);
        Call<Map<String,String>> loginCall = apiInterface.putCode(map);
        loginCall.enqueue(new Callback<Map<String,String>>() {
            @Override
            public void onResponse(Call<Map<String,String>> call, Response<Map<String,String>> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    Map<String,String> data = response.body();
                    String responseString = new Gson().toJson(response.body());
                    Log.e(TAG, "QrCode Response :" + responseString);
                    if (data.get("status").equals("1")) {
                        //  startActivity(new Intent(ScanAct.this,DefactAct.class).putExtra("scanData",responseString));
                        //   finish();

                      /*  startActivity(new Intent(ScanAct.this,ProductTypeAct.class).putExtra("productId",data.result.id)
                                .putExtra("subAdminId",data.result.subAdminId));
                        finish();*/
                        startActivity(new Intent(ScanAct.this,DefectListCopyAct.class)
                                .putExtra("pre",code[0])
                                .putExtra("type1",code[1])
                                .putExtra("type2",code[2]));
                        finish();

                    } else if (data.get("status").equals("0")) {
                        // isOpen = false;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mScannerView.resumeCameraPreview(ScanAct.this);
                            }
                        }, 2000);
                      //  App.showToast(ScanAct.this, data.get("message"), Toast.LENGTH_SHORT);
                        Toast.makeText(ScanAct.this, data.get("message"), Toast.LENGTH_SHORT).show();

                    }
                    else if (data.get("status").equals("2")) {
                        Toast.makeText(ScanAct.this, "This product is blocked", Toast.LENGTH_SHORT).show();
                        finish();
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
    public void handleResult(Result rawResult) {
      //  Toast.makeText(this, "Sc = " + rawResult.getText() +
       //         ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();


        Log.e("Contents =",rawResult.getText()+", Format ===" +rawResult.getBarcodeFormat().toString());

        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        if(NetworkAvailablity.checkNetworkStatus(ScanAct.this)){
            if(rawResult.getText().contains("-")) scanQrCode(rawResult.getText());
            else {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScannerView.resumeCameraPreview(ScanAct.this);
                    }
                }, 2000);
                Toast.makeText(ScanAct.this, getString(R.string.invalid_qr_code), Toast.LENGTH_SHORT).show();
            }
        }
        else App.showToast(ScanAct.this,getString(R.string.network_failure), Toast.LENGTH_LONG);

    }


    private static class CustomViewFinderView extends ViewFinderView {
        public static final String TRADE_MARK_TEXT = "Qualifeed";
        public static final int TRADE_MARK_TEXT_SIZE_SP = 40;
        public final Paint PAINT = new Paint();

        public CustomViewFinderView(Context context) {
            super(context);
            init();
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            PAINT.setColor(Color.WHITE);
            PAINT.setAntiAlias(true);
            float textPixelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    TRADE_MARK_TEXT_SIZE_SP, getResources().getDisplayMetrics());
            PAINT.setTextSize(textPixelSize);
            setSquareViewFinder(true);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            drawTradeMark(canvas);
        }

        private void drawTradeMark(Canvas canvas) {
            Rect framingRect = getFramingRect();
            float tradeMarkTop;
            float tradeMarkLeft;
            if (framingRect != null) {
                tradeMarkTop = framingRect.bottom + PAINT.getTextSize() + 10;
                tradeMarkLeft = framingRect.left;
            } else {
                tradeMarkTop = 10;
                tradeMarkLeft = canvas.getHeight() - PAINT.getTextSize() - 10;
            }
            canvas.drawText(TRADE_MARK_TEXT, tradeMarkLeft, tradeMarkTop, PAINT);
        }
    }


    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(ScanAct.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED  ) {
            return true;
        }
        return false;
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                ScanAct.this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_ID
        );
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               addScanner();
            }
        }
    }


}
