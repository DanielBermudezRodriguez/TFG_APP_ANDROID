package org.udg.pds.todoandroid.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
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
import org.udg.pds.todoandroid.util.Global;

import java.util.List;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHoler> {

    private List<Evento> eventos;
    private Context context;

    public EventoAdapter(Context context , List<Evento> eventos) {
        this.context = context;
        this.eventos = eventos;
    }


    public static class EventoViewHoler extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView usernameAdmin;
        TextView emailAdmin;
        ImageView imagenAdmin;
        ImageView imagenEvento;
        TextView deporteEvento;
        TextView participantesEvento;

        EventoViewHoler(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardview);
            usernameAdmin = itemView.findViewById(R.id.cardview_username_administrador);
            emailAdmin = itemView.findViewById(R.id.cardview_email_administrador);
            imagenAdmin = itemView.findViewById(R.id.cardview_imagen_administrador);
            imagenEvento = itemView.findViewById(R.id.cardview_imagen_evento);
            deporteEvento = itemView.findViewById(R.id.cardview_deporte_evento);
            participantesEvento = itemView.findViewById(R.id.cardview_participantes_evento);

        }
    }


    @NonNull
    @Override
    public EventoAdapter.EventoViewHoler onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_evento, viewGroup, false);
        EventoViewHoler eventoViewHoler = new EventoViewHoler(v);
        return eventoViewHoler;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull EventoAdapter.EventoViewHoler holder, int position) {

        Evento eventoActual = eventos.get(position);
        RequestOptions options = new RequestOptions();
        options.centerCrop();

        if (holder.imagenAdmin.getDrawable() == null){
            //Picasso.with(context).load(Global.BASE_URL + "imagen/usuario/" + eventos.get(position).getAdministrador().getId().toString()).fit().into(holder.imagenAdmin);
            Glide.with(context).load(Global.BASE_URL + "imagen/usuario/" + eventoActual.getAdministrador().getId().toString()).apply(options).into(holder.imagenAdmin);

        }
        if (holder.imagenEvento.getDrawable() == null){
            //Picasso.with(context).load(Global.BASE_URL + "imagen/usuario/" + eventos.get(position).getAdministrador().getId().toString()).fit().into(holder.imagenEvento);
            Glide.with(context).load(Global.BASE_URL + "imagen/usuario/" + eventoActual.getAdministrador().getId().toString()).apply(options).into(holder.imagenEvento);
        }

        holder.usernameAdmin.setText(eventoActual.getAdministrador().getUsername());
        holder.emailAdmin.setText(eventoActual.getAdministrador().getEmail());
        holder.deporteEvento.setText(eventoActual.getTitulo());
        System.out.println("MEK:   " + String.valueOf(eventoActual.getNumeroParticipantes()));
        holder.participantesEvento.setText(String.valueOf(eventoActual.getParticipantesRegistrados()) + " / " + String.valueOf(eventoActual.getNumeroParticipantes()));


    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
