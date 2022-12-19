package com.qualifeed.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.qualifeed.R;
import com.qualifeed.databinding.ActivityTypeBinding;

public class TypeAct extends AppCompatActivity {
    ActivityTypeBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_type);
        initViews();
    }

    private void initViews() {

        binding.btnPiece.setOnClickListener(v -> {startActivity(new Intent(this,ScanAct.class));});

        binding.btnBox.setOnClickListener(v -> {});

        binding.btnShipment.setOnClickListener(v -> {});
    }
}
