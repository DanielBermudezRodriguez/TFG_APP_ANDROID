package org.udg.pds.todoandroid.adapter;


import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Evento;

import java.util.List;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHoler>{

    private List<Evento> eventos;

    public EventoAdapter(List<Evento> eventos){
        this.eventos = eventos;
    }


    public static class EventoViewHoler extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tituloEvento;

        EventoViewHoler(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardview);
            tituloEvento = itemView.findViewById(R.id.cardview_titulo_evento);

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
        holder.tituloEvento.setText(eventos.get(position).getTitulo());
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
