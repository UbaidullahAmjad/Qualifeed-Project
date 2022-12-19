package com.qualifeed.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.qualifeed.R;
import com.qualifeed.databinding.ActivityMyAccountBinding;
import com.qualifeed.utils.DataManager;

public class MyAccountAct extends AppCompatActivity {
    ActivityMyAccountBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_my_account);
        initViews();
    }

    private void initViews() {
        binding.tvName.setText(DataManager.getInstance().getUserData(MyAccountAct.this).result.userName);
        binding.tvType.setText(DataManager.getInstance().getUserData(MyAccountAct.this).result.type);

        Glide.with(MyAccountAct.this).load(DataManager.getInstance().getUserData(MyAccountAct.this).result.image)
                .override(60,60)
                .error(R.drawable.place_holder)
                .into(binding.ivUser);
    }
}
