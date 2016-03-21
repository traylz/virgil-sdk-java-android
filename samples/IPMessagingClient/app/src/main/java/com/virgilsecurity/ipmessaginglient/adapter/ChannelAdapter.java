package com.virgilsecurity.ipmessaginglient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.virgilsecurity.ipmessaginglient.R;
import com.virgilsecurity.ipmessaginglient.model.Channel;

import java.util.List;

/**
 * Created by Andrii Iakovenko.
 */
public class ChannelAdapter extends ArrayAdapter<Channel> {
    private final Context context;
    private final List<Channel> values;


    public ChannelAdapter(Context context, List<Channel> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        TextView textView = (TextView) rowView.findViewById(android.R.id.text1);
        textView.setText(values.get(position).getChannelName());
        return rowView;
    }
}
