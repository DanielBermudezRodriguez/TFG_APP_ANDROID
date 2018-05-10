package org.udg.pds.todoandroid.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Evento;
import org.udg.pds.todoandroid.entity.ParticipanteEvento;
import org.udg.pds.todoandroid.util.Global;

import java.util.ArrayList;
import java.util.List;

public class EventosCreadosAdapter extends RecyclerView.Adapter<EventosCreadosAdapter.EventoCreadoViewHolder> {

    private List<Evento> eventos = new ArrayList<Evento>();
    private Context context;

    public EventosCreadosAdapter (Context context, List<Evento> eventos){
        this.eventos = eventos;
        this.context = context;
    }

    @NonNull
    @Override
    public EventosCreadosAdapter.EventoCreadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_eventos_creados, parent, false);
        return new EventosCreadosAdapter.EventoCreadoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventosCreadosAdapter.EventoCreadoViewHolder holder, int position) {

        Evento eventoActual = eventos.get(position);
        RequestOptions options = new RequestOptions();
        options.centerCrop();

        Glide.with(context).load(Global.BASE_URL + "imagen/evento/" + eventoActual.getId().toString()).apply(options).into(holder.imagenEvento);

        holder.deporteEvento.setText(eventoActual.getDeporte().getDeporte());

    }


    @Override
    public int getItemCount() {
        return eventos.size();
    }

    public class EventoCreadoViewHolder extends RecyclerView.ViewHolder {

        ImageView imagenEvento;
        TextView deporteEvento;

        public EventoCreadoViewHolder(View itemView) {
            super(itemView);

            imagenEvento = itemView.findViewById(R.id.cardview_evento_creado_imagen);
            deporteEvento = itemView.findViewById(R.id.cardview_evento_creado_deporte);
        }
    }
}
