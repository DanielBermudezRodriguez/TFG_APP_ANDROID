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
import org.udg.pds.todoandroid.entity.ParticipanteEvento;
import org.udg.pds.todoandroid.util.Global;

import java.util.ArrayList;
import java.util.List;

public class ParticipanteEventoAdapter extends RecyclerView.Adapter<ParticipanteEventoAdapter.ParticipanteEventoViewHolder> {

    private List<ParticipanteEvento> participanteEventos = new ArrayList<ParticipanteEvento>();
    private Context context;

    public ParticipanteEventoAdapter (Context context, List<ParticipanteEvento> participantes){
        this.participanteEventos = participantes;
        this.context = context;
    }


    @NonNull
    @Override
    public ParticipanteEventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_participantes_evento, parent, false);
        return new ParticipanteEventoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipanteEventoViewHolder holder, int position) {

        ParticipanteEvento participanteEventoActual = participanteEventos.get(position);
        RequestOptions options = new RequestOptions();
        options.centerCrop();

        Glide.with(context).load(Global.BASE_URL + "imagen/usuario/" + participanteEventoActual.getId().toString()).apply(options).into(holder.imagenParticipante);

        holder.municipioParticipante.setText(participanteEventoActual.getMunicipio());
        holder.usernameParticipante.setText(participanteEventoActual.getUsername());
    }


    @Override
    public int getItemCount() {
        return participanteEventos.size();
    }

    public class ParticipanteEventoViewHolder extends RecyclerView.ViewHolder {

        ImageView imagenParticipante;
        TextView usernameParticipante;
        TextView municipioParticipante;

        public ParticipanteEventoViewHolder(View itemView) {
            super(itemView);

            imagenParticipante = itemView.findViewById(R.id.cardview_imagen_participante_evento);
            usernameParticipante = itemView.findViewById(R.id.cardview_participante_username);
            municipioParticipante = itemView.findViewById(R.id.cardview_participante_municipio);
        }
    }
}
