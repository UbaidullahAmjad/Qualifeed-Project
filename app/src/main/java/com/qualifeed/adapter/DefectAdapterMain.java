package com.qualifeed.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qualifeed.R;
import com.qualifeed.activity.DefectListAct;
import com.qualifeed.databinding.ItemDefactBinding;
import com.qualifeed.databinding.ItemDefactMainBinding;
import com.qualifeed.model.DefectModelMain;
import com.qualifeed.model.GetDefactModel;
import com.qualifeed.retrofit.onPosListener;

import java.util.ArrayList;

public class DefectAdapterMain extends RecyclerView.Adapter<DefectAdapterMain.MyViewHolder> {
    Context context;
    ArrayList<DefectModelMain.Result> arrayList;
    onPosListener listener;

    public DefectAdapterMain(Context context, ArrayList<DefectModelMain.Result> arrayList,onPosListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDefactMainBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_defact_main,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       holder.binding.tvDefact.setText(arrayList.get(position).name);
        holder.binding.btnCount.setText(arrayList.get(position).productDefectsRequest.size()+"");

       /* Glide.with(context)
                .load(R.drawable.images_dummy)
                .centerCrop()
                .override(100,100)
                .error(R.drawable.i)
                .into(holder.binding.ivDefact);*/
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemDefactMainBinding binding;
        public MyViewHolder(@NonNull ItemDefactMainBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.ivDefact.setOnClickListener(v -> listener.onPos(getAdapterPosition(),"Detail"));

            binding.btnPlus.setOnClickListener(v -> listener.onPos(getAdapterPosition(),"Add"));

            binding.btnMinus.setOnClickListener(v -> {
                DefectListAct.arrayList2.clear();
                DefectListAct.arrayList2.addAll(arrayList.get(getAdapterPosition()).productDefectsRequest);
                listener.onPos(getAdapterPosition(),"Minus");


            });

        }
    }
}
