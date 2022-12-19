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
import com.qualifeed.activity.DefectListCopyAct;
import com.qualifeed.databinding.ItemDefactMainBinding;
import com.qualifeed.model.DefectModelMain;
import com.qualifeed.model.DefectModelMainCopy;
import com.qualifeed.retrofit.onPosListener;

import java.util.ArrayList;

public class DefectAdapterMainCopy extends RecyclerView.Adapter<DefectAdapterMainCopy.MyViewHolder> {
    Context context;
    ArrayList<DefectModelMainCopy.Result> arrayList;
    onPosListener listener;


    public DefectAdapterMainCopy(Context context, ArrayList<DefectModelMainCopy.Result> arrayList,onPosListener listener) {
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
       if(arrayList.get(position).getTitle().equals("")) holder.binding.tvDefact.setText(arrayList.get(position).getProductref());
       else holder.binding.tvDefact.setText(arrayList.get(position).getTitle());
       holder.binding.btnCount.setText(arrayList.get(position).getProductDefectsRequest().size()+"");

      //  holder.binding.tvDefact.setText("Defect");
      //  holder.binding.btnCount.setText("3");

        Glide.with(context)
                .load(R.drawable.images_dummy)
                .centerCrop()
                .override(100,100)
                .error(R.drawable.images_dummy)
                .into(holder.binding.ivDefact);
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
                DefectListCopyAct.arrayList2.clear();
                DefectListCopyAct.arrayList2.addAll(arrayList.get(getAdapterPosition()).getProductDefectsRequest());
                listener.onPos(getAdapterPosition(),"Minus");


            });


        }
    }
}