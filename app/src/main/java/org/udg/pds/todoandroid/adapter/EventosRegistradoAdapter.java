package org.udg.pds.todoandroid.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import org.udg.pds.todoandroid.util.DateUtil;
import org.udg.pds.todoandroid.util.Global;

import java.util.ArrayList;
import java.util.List;


public class EventosRegistradoAdapter extends RecyclerView.Adapter<EventosRegistradoAdapter.EventoRegistradoViewHolder> {

    private List<Evento> eventos = new ArrayList<Evento>();
    private Context context;
    private EventosRegistradoAdapter.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        public void visualizardetalleEvento(Evento e);

        public void desapuntarDelEvento(Evento e);
    }

    public EventosRegistradoAdapter(Context context, List<Evento> eventos, EventosRegistradoAdapter.OnItemClickListener onItemClickListener) {
        this.eventos = eventos;
        this.context = context;
        this.mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public EventosRegistradoAdapter.EventoRegistradoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_evento_registrado, parent, false);
        return new EventosRegistradoAdapter.EventoRegistradoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventosRegistradoAdapter.EventoRegistradoViewHolder holder, int position) {

        final Evento eventoActual = eventos.get(position);
        RequestOptions options = new RequestOptions();
        options.centerCrop();


        holder.desapuntarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.desapuntarDelEvento(eventoActual);
            }
        });

        Glide.with(context).load(Global.BASE_URL + "imagen/evento/" + eventoActual.getId().toString()).apply(options).into(holder.imagenEvento);
        holder.deporteEvento.setText(eventoActual.getDeporte().getDeporte());
        holder.fechavento.setText(DateUtil.parseData(eventoActual.getFechaEvento()));
        holder.estadoEvento.setText(eventoActual.getEstado().getEstado());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.visualizardetalleEvento(eventoActual);
            }
        });

    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    public class EventoRegistradoViewHolder extends RecyclerView.ViewHolder {

        ImageView imagenEvento;
        TextView deporteEvento;
        TextView fechavento;
        ConstraintLayout desapuntarEvento;
        CardView cardView;
        TextView estadoEvento;

        public EventoRegistradoViewHolder(View itemView) {
            super(itemView);

            imagenEvento = itemView.findViewById(R.id.cardview_evento_registrado_imagen);
            deporteEvento = itemView.findViewById(R.id.cardview_evento_registrado_deporte);
            fechavento = itemView.findViewById(R.id.cardview_evento_registrado_fecha);
            desapuntarEvento = itemView.findViewById(R.id.cardview_evento_registrado_desapuntar);
            cardView = itemView.findViewById(R.id.cardview_evento_registrado);
            estadoEvento = itemView.findViewById(R.id.cardview_evento_registrado_estado);
        }


    }
}
