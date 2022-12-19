package com.qualifeed.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.qualifeed.R;
import com.qualifeed.databinding.ActivityTodoBinding;
import com.qualifeed.utils.DataManager;
import com.qualifeed.utils.SessionManager;

public class ToDoAct extends AppCompatActivity {
    ActivityTodoBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =   DataBindingUtil.setContentView(this,R.layout.activity_todo);
        initViews();
    }

    private void initViews() {

        binding.btnStartIns.setOnClickListener(v -> {startActivity(new Intent(this,ScanAct.class)
        .putExtra("from","start"));});

        binding.btnChkPoint.setOnClickListener(v -> {
            startActivity(new Intent(this,TrainProductTypeAct.class).putExtra("chkType","Training"));
        });

        binding.btnInspection.setOnClickListener(v -> {
          //  startActivity(new Intent(this,ControlScanAct.class));
            startActivity(new Intent(this,TrainProductTypeAct.class).putExtra("chkType","Control"));

        });

        binding.btnLogout.setOnClickListener(v -> {
            LogOutAlert();
        });

        binding.ivUser.setOnClickListener(v -> {
            startActivity(new Intent(this,MyAccountAct.class));

        });

        binding.btntodo.setOnClickListener(v -> {
            startActivity(new Intent(this,TaskListAct.class));
        });





        Glide.with(ToDoAct.this).load(DataManager.getInstance().getUserData(ToDoAct.this).result.image)
                .override(40,40)
                .error(R.drawable.place_holder)
                .into(binding.ivUser);
    }


    public void ExitAlert(){
        AlertDialog.Builder  builder1 = new AlertDialog.Builder(ToDoAct.this);
        builder1.setMessage(getResources().getString(R.string.are_you_sure_you_want_to_exit_this_app));
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
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

    @Override
    public void onBackPressed() {
        ExitAlert();
    }


    public void LogOutAlert(){
        AlertDialog.Builder  builder1 = new AlertDialog.Builder(ToDoAct.this);
        builder1.setMessage(getResources().getString(R.string.are_you_sure_you_want_to_logout_this_app));
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        SessionManager.clearsession(ToDoAct.this);
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


    @Override
    protected void onResume() {
        super.onResume();
        if(SessionManager.readString(ToDoAct.this,"ChkTraining","").equals("true")){
            binding.ivTwo.setImageDrawable(getDrawable(R.drawable.ic_active));
        }
        else  binding.ivTwo.setImageDrawable(getDrawable(R.drawable.ic_deactive));


        if(SessionManager.readString(ToDoAct.this,"ChkControl","").equals("true")){
            binding.ivThree.setImageDrawable(getDrawable(R.drawable.ic_active));
        }
        else  binding.ivThree.setImageDrawable(getDrawable(R.drawable.ic_deactive));

    }
}
