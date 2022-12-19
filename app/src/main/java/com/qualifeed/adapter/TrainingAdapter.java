package com.qualifeed.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qualifeed.R;
import com.qualifeed.databinding.ItemDefactBinding;
import com.qualifeed.databinding.ItemTrainingBinding;
import com.qualifeed.model.DefectModelMain;
import com.qualifeed.model.DefectModelMain22;
import com.qualifeed.model.GetDefactModel;
import com.qualifeed.retrofit.onPosListener;

import java.util.ArrayList;

public class TrainingAdapter extends RecyclerView.Adapter<TrainingAdapter.MyViewHolder> {
    Context context;
    ArrayList<DefectModelMain22.Result>arrayList;
    onPosListener listener;

    public TrainingAdapter(Context context, ArrayList<DefectModelMain22.Result>arrayList,onPosListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent,int viewType){
            ItemTrainingBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_training, parent, false);
            return new MyViewHolder(binding);
        }

        @Override
        public void onBindViewHolder (@NonNull MyViewHolder holder,int position){
            holder.binding.tvDefact.setText(arrayList.get(position).getProductref());
            holder.binding.tvDes.setText(arrayList.get(position).getComment());

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
            ItemTrainingBinding binding;

            public MyViewHolder(@NonNull ItemTrainingBinding itemView) {
                super(itemView.getRoot());
                binding = itemView;

                binding.layoutMain.setOnClickListener(v -> listener.onPos(getAdapterPosition(),binding.tvDefact.getText().toString()));
            }
        }
    }

