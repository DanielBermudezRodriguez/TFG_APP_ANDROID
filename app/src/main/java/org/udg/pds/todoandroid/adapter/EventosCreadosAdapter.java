package org.udg.pds.todoandroid.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Evento;
import org.udg.pds.todoandroid.entity.UsuarioActual;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.DateUtil;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventosCreadosAdapter extends RecyclerView.Adapter<EventosCreadosAdapter.EventoCreadoViewHolder> {

    private List<Evento> eventos = new ArrayList<Evento>();
    private Context context;
    private EventosCreadosAdapter.OnItemClickListener mOnItemClickListener;
    private ApiRest apiRest;


    public interface OnItemClickListener {
        public void visualizardetalleEvento(Evento e);

        public void cancelarEvento(Evento e, int position);
    }

    public EventosCreadosAdapter(Context context, List<Evento> eventos, EventosCreadosAdapter.OnItemClickListener onItemClickListener) {
        this.eventos = eventos;
        this.context = context;
        this.mOnItemClickListener = onItemClickListener;
        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();
    }

    @NonNull
    @Override
    public EventosCreadosAdapter.EventoCreadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_eventos_creados, parent, false);
        return new EventosCreadosAdapter.EventoCreadoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventosCreadosAdapter.EventoCreadoViewHolder holder, final int position) {

        final Evento eventoActual = eventos.get(position);

        // Evento abierto o completo (No está en un estado final) i el usuario actual es administrador del evento
        if ((eventoActual.getEstado().getId().equals(Global.EVENTO_ABIERTO) || eventoActual.getEstado().getId().equals(Global.EVENTO_COMPLETO)) && eventoActual.getAdministrador().getId().equals(UsuarioActual.getInstance().getId())) {
            holder.cancelarEvento.setVisibility(View.VISIBLE);
            holder.cancelarEvento.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.cancelarEvento(eventoActual, position);
                }
            });
        } else {
            holder.cancelarEvento.setVisibility(View.GONE);
        }

        if (eventos.get(position).getId() != null) {

            // Obtener nombre imagen evento actual para completar la URL
            final Call<String> nombreImagen = apiRest.nombreImagenEvento(eventoActual.getId());
            nombreImagen.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                        String imagenNombre = response.body();
                        RequestOptions options = new RequestOptions().centerCrop();
                        Glide.with(context).load(Global.BASE_URL + Global.IMAGE_EVENT + eventoActual.getId().toString() + "/" + imagenNombre).apply(options).into(holder.imagenEvento);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("ERROR: ", t.getMessage());
                }
            });
        } else {
            holder.imagenEvento.setImageDrawable(null);
        }

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

    public class EventoCreadoViewHolder extends RecyclerView.ViewHolder {

        ImageView imagenEvento;
        TextView deporteEvento;
        TextView fechavento;
        ConstraintLayout cancelarEvento;
        CardView cardView;
        TextView estadoEvento;

        public EventoCreadoViewHolder(View itemView) {
            super(itemView);

            imagenEvento = itemView.findViewById(R.id.cardview_evento_creado_imagen);
            deporteEvento = itemView.findViewById(R.id.cardview_evento_creado_deporte);
            fechavento = itemView.findViewById(R.id.cardview_evento_creado_fecha);
            cancelarEvento = itemView.findViewById(R.id.cardview_evento_creado_cancelar);
            cardView = itemView.findViewById(R.id.cardview_evento_creado);
            estadoEvento = itemView.findViewById(R.id.cardview_evento_creado_estado);
        }


    }
}
