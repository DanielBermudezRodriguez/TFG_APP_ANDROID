package org.udg.pds.todoandroid.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.MensajeForo;
import org.udg.pds.todoandroid.util.Global;

import java.util.ArrayList;
import java.util.List;


public class MensajeForoAdapter extends BaseAdapter {


    List<MensajeForo> messages = new ArrayList<MensajeForo>();
    Context context;

    public MensajeForoAdapter(Context context) {
        this.context = context;
    }

    public void add(MensajeForo message) {
        this.messages.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        MensajeForo message = messages.get(i);

        if (message.isBelongsToCurrentUser()) {
            convertView = messageInflater.inflate(R.layout.tab_foro_evento_mensaje_propio, null);
            holder.messageBody =  convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);
            holder.messageBody.setText(message.getText());
        } else {

            convertView = messageInflater.inflate(R.layout.tab_foro_evento_mensaje_alieno, null);
            holder.avatarForo = convertView.findViewById(R.id.avatar_foro);
            holder.name = convertView.findViewById(R.id.name);
            holder.messageBody = convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);

            holder.name.setText(message.getData().getName());
            holder.messageBody.setText(message.getText());
            Glide.with(context).load(Global.BASE_URL + "imagen/usuario/" + message.getData().getId().toString()).apply(new RequestOptions().circleCrop()).into(holder.avatarForo);
        }

        return convertView;
    }

}

class MessageViewHolder {
    public ImageView avatarForo;
    public TextView name;
    public TextView messageBody;
}

