package com.virgilsecurity.ipmessaginglient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.virgilsecurity.ipmessaginglient.R;
import com.virgilsecurity.ipmessaginglient.model.Channel;
import com.virgilsecurity.ipmessaginglient.model.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Andrii Iakovenko.
 */
public class MessageAdapter extends ArrayAdapter<Message> {
    private final Context context;
    private final List<Message> values;
    private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    public MessageAdapter(Context context, List<Message> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.message_list_item, parent, false);

        Message message = values.get(position);

        TextView senderView = (TextView) rowView.findViewById(R.id.sender);
        TextView dateView = (TextView) rowView.findViewById(R.id.date);
        TextView textView = (TextView) rowView.findViewById(R.id.text);

        senderView.setText(message.getSenderIdentifier());

        if (message.getCreatedAt() != null) {
            dateView.setText(df.format(new Date(message.getCreatedAt())));
        } else {
            dateView.setText("");
        }
        textView.setText(message.getMessage());

        return rowView;
    }
}
