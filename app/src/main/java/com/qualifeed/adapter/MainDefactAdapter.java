package com.qualifeed.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qualifeed.R;
import com.qualifeed.activity.TakePhotoAct;
import com.qualifeed.databinding.ItemDefactBinding;
import com.qualifeed.model.GetDefactModel;

import java.util.ArrayList;

public class MainDefactAdapter extends RecyclerView.Adapter<MainDefactAdapter.MyViewHolder> {
    Context context;
    ArrayList<GetDefactModel.Result>arrayList;

    public MainDefactAdapter(Context context, ArrayList<GetDefactModel.Result> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDefactBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_defact,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
      if(arrayList.get(position).defactId.equals("1")) holder.binding.tvDefact.setText("defective 1");
      else if(arrayList.get(position).defactId.equals("2")) holder.binding.tvDefact.setText("defective 2");
      else if(arrayList.get(position).defactId.equals("3")) holder.binding.tvDefact.setText("defective 3");
      else if(arrayList.get(position).defactId.equals("4")) holder.binding.tvDefact.setText("defective 4");

        Glide.with(context)
                .load(arrayList.get(position).defactImage)
                .centerCrop()
                .override(100,100)
                .error(R.drawable.place_holder)
                .into(holder.binding.ivDefact);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemDefactBinding binding;
        public MyViewHolder(@NonNull ItemDefactBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
