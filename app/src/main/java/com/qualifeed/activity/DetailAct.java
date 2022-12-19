package com.qualifeed.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.qualifeed.R;
import com.qualifeed.databinding.ActivityDetailBinding;

public class DetailAct extends AppCompatActivity {
    ActivityDetailBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     binding = DataBindingUtil.setContentView(this,R.layout.activity_detail);
     initViews();
    }

    private void initViews() {
        if(getIntent()!=null){
            Glide.with(DetailAct.this)
                    .load(getIntent().getStringExtra("img"))
                    .centerCrop()
                    .override(100,100)
                    .placeholder(R.drawable.place_holder)
                    .error(R.drawable.place_holder)
                    .into(binding.ivImg);
            binding.tvTitle.setText(getIntent().getStringExtra("title"));
            binding.tvDes.setText(getIntent().getStringExtra("des"));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
