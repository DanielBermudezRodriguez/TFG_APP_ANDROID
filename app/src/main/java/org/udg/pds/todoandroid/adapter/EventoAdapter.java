package org.udg.pds.todoandroid.adapter;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.activity.Principal;
import org.udg.pds.todoandroid.entity.Evento;
import org.udg.pds.todoandroid.entity.UsuarioActual;
import org.udg.pds.todoandroid.util.Global;

import java.io.IOException;
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

        EventoViewHoler(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardview);
            usernameAdmin = itemView.findViewById(R.id.cardview_username_administrador);
            emailAdmin = itemView.findViewById(R.id.cardview_email_administrador);
            imagenAdmin = itemView.findViewById(R.id.cardview_imagen_administrador);
            imagenEvento = itemView.findViewById(R.id.cardview_imagen_evento);

        }
    }


    @NonNull
    @Override
    public EventoAdapter.EventoViewHoler onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_evento, viewGroup, false);
        EventoViewHoler eventoViewHoler = new EventoViewHoler(v);
        return eventoViewHoler;
    }

    @Override
    public void onBindViewHolder(@NonNull EventoAdapter.EventoViewHoler holder, int position) {

        if (holder.imagenAdmin.getDrawable() == null){
            Picasso.with(context).load(Global.BASE_URL + "imagen/usuario/" + eventos.get(position).getAdministrador().getId().toString()).into(holder.imagenAdmin);

        }
        if (holder.imagenEvento.getDrawable() == null){
            Picasso.with(context).load(Global.BASE_URL + "imagen/usuario/" + eventos.get(position).getAdministrador().getId().toString()).resize(150,150).into(holder.imagenEvento);
        }

        holder.usernameAdmin.setText(eventos.get(position).getAdministrador().getUsername());
        holder.emailAdmin.setText(eventos.get(position).getAdministrador().getEmail());


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
