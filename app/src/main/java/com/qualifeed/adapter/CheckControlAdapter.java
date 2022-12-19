package com.qualifeed.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qualifeed.R;
import com.qualifeed.databinding.ItemCheckBinding;
import com.qualifeed.databinding.ItemTrainingBinding;
import com.qualifeed.model.DefectModelMain;
import com.qualifeed.model.DefectModelMain22;
import com.qualifeed.model.GetDefactModel;

import java.util.ArrayList;

public class CheckControlAdapter extends RecyclerView.Adapter<CheckControlAdapter.MyViewHolder> {
    Context context;
    ArrayList<DefectModelMain22.Result> arrayList;

    public CheckControlAdapter(Context context,ArrayList<DefectModelMain22.Result>arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType){
        ItemCheckBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_check, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder (@NonNull MyViewHolder holder, int position){
        Glide.with(context)
                .load(arrayList.get(position).getProductDefectsImage())
                .centerCrop()
                .override(100,100)
                .error(R.drawable.place_holder)
                .into(holder.binding.ivDefact);
    }

    @Override
    public int getItemCount () {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemCheckBinding binding;

        public MyViewHolder(@NonNull ItemCheckBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
