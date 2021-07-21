package com.ips.online.utilityclasses;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ips.online.R;

import java.util.List;

public class ListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflator;
    List<ScanResult> wifiList;

    public ListAdapter(Context context, List<ScanResult> wifiList){
        this.context = context;
        this.wifiList = wifiList;
        inflator = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return wifiList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        View view = convertView;
        if(view == null){
            view = inflator.inflate(R.layout.list_item, null);
            holder = new Holder();
            holder.tvDetails =(TextView)view.findViewById(R.id.txtWfiName);
            view.setTag(holder);
        }
        else{
            holder  = (Holder)view.getTag();
        }
        holder.tvDetails.setText("SSID : "+ wifiList.get(position).SSID + " | BSSID : " + wifiList.get(position).BSSID + " | " + wifiList.get(position).level);
        return view;
    }
    class Holder{
        TextView    tvDetails;
    }
}
