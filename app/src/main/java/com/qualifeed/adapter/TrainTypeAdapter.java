package com.qualifeed.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qualifeed.R;
import com.qualifeed.model.ProductTypeModel;
import com.qualifeed.model.ProductTypeTrainModel;
import com.qualifeed.retrofit.onPosListener;

import java.util.ArrayList;

public class TrainTypeAdapter extends BaseAdapter {
    Context context;
    ArrayList<ProductTypeTrainModel.Result> arrayList;
    LayoutInflater inflater;
    onPosListener listener;

    public TrainTypeAdapter(Context context, ArrayList<ProductTypeTrainModel.Result> arrayList,onPosListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;

        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_type, null);
        TextView names = convertView.findViewById(R.id.item);
        RelativeLayout mainView = convertView.findViewById(R.id.mainView);
        names.setText(arrayList.get(position).getProductType1() +"/" + arrayList.get(position).getProductType2());

        mainView.setOnClickListener(v -> listener.onPos(position,""));

        return convertView;
    }
}
