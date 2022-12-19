package com.qualifeed.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qualifeed.R;
import com.qualifeed.activity.DefectListAct;
import com.qualifeed.databinding.ItemDefactMainBinding;
import com.qualifeed.databinding.ItemTaskBinding;
import com.qualifeed.model.DefectModelMain;
import com.qualifeed.model.TaskModel;
import com.qualifeed.retrofit.onPosListener;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {
    Context context;
    ArrayList<TaskModel.Result> arrayList;
    onPosListener listener;

    public TaskAdapter(Context context, ArrayList<TaskModel.Result> arrayList,onPosListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_task,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvTask.setText(arrayList.get(position).name);

        if(arrayList.get(position).status.equals("Complete")) holder.binding.chkStatus.setChecked(true);
       else if(arrayList.get(position).status.equals("Pending")) holder.binding.chkStatus.setChecked(false);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemTaskBinding binding;
        public MyViewHolder(@NonNull ItemTaskBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

/*
            binding.chkStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 if() binding.chkStatus.isChecked()
                }
            });
*/

          binding.chkStatus.setOnClickListener(v -> {
                  if(arrayList.get(getAdapterPosition()).status.equals("Pending")) {
                      listener.onPos(getAdapterPosition(),"Change");
                      notifyDataSetChanged();
                  }else {
                      binding.chkStatus.setChecked(true);
                      notifyDataSetChanged();
                  }

          });



        }
    }
}