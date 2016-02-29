package com.virgilsecurity.sdk.samples;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;

import java.util.List;

/**
 * Created by Andrii Iakovenko
 */
public class VirgilCardListAdapter extends ArrayAdapter<VirgilCard> {

    public VirgilCardListAdapter(Context context, List<VirgilCard> values) {
        super(context, R.layout.vc_list_item, values);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.vc_list_item, parent, false);
        TextView label = (TextView) rowView.findViewById(R.id.label);
        TextView confirmed = (TextView) rowView.findViewById(R.id.confirmed);

        VirgilCard card = getItem(position);
        label.setText(card.getId());
        if (card.getConfirmed()) {
            confirmed.setText("Confirmed");
        } else {
            confirmed.setText("Unconfirmed");
        }

        return rowView;
    }
}
