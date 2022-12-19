package com.qualifeed.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qualifeed.R;
import com.qualifeed.model.DefactModel;

import java.util.ArrayList;

public class DefactAdapter extends BaseAdapter {
    Context context;
    ArrayList<DefactModel.Result> defactArrayList;
    LayoutInflater inflater;


    public DefactAdapter(Context context, ArrayList<DefactModel.Result> defactArrayList) {
        this.context = context;
        this.defactArrayList = defactArrayList;

        inflater = (LayoutInflater.from(context));

    }

    @Override
    public int getCount() {
        return defactArrayList.size();
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
        convertView = inflater.inflate(R.layout.item_spinner, null);
        TextView names = convertView.findViewById(R.id.item);
        RelativeLayout mainView = convertView.findViewById(R.id.mainView);
        names.setText(defactArrayList.get(position).name);


      /*  mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPositionListener.clickPos(position,status);
            }
        });*/

        return convertView;
    }
}

