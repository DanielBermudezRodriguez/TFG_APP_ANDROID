package org.udg.pds.todoandroid.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MensajeForoAdapter extends BaseAdapter {


    private List<MensajeForo> messages = new ArrayList<MensajeForo>();
    private Context context;
    private ApiRest apiRest;

    public MensajeForoAdapter(Context context) {
        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();
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


    @SuppressLint("InflateParams")
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final MensajeForo message = messages.get(i);

        if (message.isBelongsToCurrentUser() && messageInflater != null) {
            convertView = messageInflater.inflate(R.layout.tab_foro_evento_mensaje_propio, null);
            holder.messageBody = convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);
            holder.messageBody.setText(message.getText());
            holder.fecha = convertView.findViewById(R.id.message_body_hour);
            holder.fecha.setText(message.getData().getDate());
        } else if (messageInflater != null) {

            convertView = messageInflater.inflate(R.layout.tab_foro_evento_mensaje_alieno, null);
            holder.avatarForo = convertView.findViewById(R.id.avatar_foro);
            holder.name = convertView.findViewById(R.id.name);
            holder.messageBody = convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);
            holder.fecha = convertView.findViewById(R.id.message_body_hour);
            holder.fecha.setText(message.getData().getDate());
            holder.name.setText(message.getData().getName());
            holder.messageBody.setText(message.getText());
            // Obtener nombre imagen usuario actual para completar la URL
            final Call<String> nombreImagen = apiRest.nombreImagenUsuario(message.getData().getId());
            nombreImagen.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                        String imagenNombre = response.body();
                        RequestOptions options = new RequestOptions().centerCrop();
                        Glide.with(context).load(Global.BASE_URL + Global.IMAGE_USER + message.getData().getId().toString() + "/" + imagenNombre).apply(options).into(holder.avatarForo);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("ERROR: ", t.getMessage());
                }
            });
        }

        return convertView;
    }

    class MessageViewHolder {
        public ImageView avatarForo;
        public TextView name;
        TextView messageBody;
        TextView fecha;
    }

}



