package org.udg.pds.todoandroid.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.ParticipanteEvento;
import org.udg.pds.todoandroid.entity.UsuarioActual;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParticipanteEventoAdapter extends RecyclerView.Adapter<ParticipanteEventoAdapter.ParticipanteEventoViewHolder> {

    private List<ParticipanteEvento> participanteEventos;
    private Context context;
    private Boolean esAdministradorEvento;
    private ParticipanteEventoAdapter.OnItemClickListener mOnItemClickListener;
    private Long administrador;
    private ApiRest apiRest;

    public interface OnItemClickListener {
        void desapuntarDelEvento(ParticipanteEvento p, int position);

        void onItemClick(Long id);
    }

    public ParticipanteEventoAdapter(Context context, Long administrador, List<ParticipanteEvento> participantes, Boolean esAdministradorEvento, ParticipanteEventoAdapter.OnItemClickListener onItemClickListener) {
        this.participanteEventos = participantes;
        this.context = context;
        this.esAdministradorEvento = esAdministradorEvento;
        this.mOnItemClickListener = onItemClickListener;
        this.administrador = administrador;
        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();
    }


    @NonNull
    @Override
    public ParticipanteEventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_participantes_evento, parent, false);
        return new ParticipanteEventoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ParticipanteEventoViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final ParticipanteEvento participanteEventoActual = participanteEventos.get(position);
        RequestOptions options = new RequestOptions().centerCrop();

        // Si el usuario actual no es el administrador del evento no se le permite desapuntar participantes del evento
        if (!esAdministradorEvento) {
            holder.eliminarParticipante.setVisibility(View.GONE);
        }
        // Usuario actual es el administrador del evento
        else {
            // No dar de baja al propio administrador, para darse de baja de un evento tiene que cancelar el evento
            if (UsuarioActual.getInstance().getId().equals(participanteEventoActual.getId())) {
                holder.eliminarParticipante.setVisibility(View.GONE);
            } else { // Activar la posibilidad al administrador del evento de dar de baja a los participantes
                holder.eliminarParticipante.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.desapuntarDelEvento(participanteEventoActual, position);
                    }
                });
            }

        }
        if (participanteEventoActual.getId().equals(administrador)) {
            // Mostrar icono administrador del evento
            holder.administradorEvento.setVisibility(View.VISIBLE);
        } else {
            holder.administradorEvento.setVisibility(View.GONE);
        }

        // cargar imagen participante
        if (participanteEventos.get(position).getId() != null) {
            // Obtener nombre imagen usuario actual para completar la URL
            final Call<String> nombreImagen = apiRest.nombreImagenUsuario(participanteEventoActual.getId());
            nombreImagen.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                        String imagenNombre = response.body();
                        RequestOptions options = new RequestOptions().centerCrop();
                        Glide.with(context).load(Global.BASE_URL + Global.IMAGE_USER + participanteEventoActual.getId().toString() + "/" + imagenNombre).apply(options).into(holder.imagenParticipante);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("ERROR: ", t.getMessage());
                }
            });

        } else {
            holder.imagenParticipante.setImageDrawable(null);
        }
        holder.municipioParticipante.setText(participanteEventoActual.getMunicipio());
        holder.usernameParticipante.setText(participanteEventoActual.getUsername());

        holder.participante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(participanteEventoActual.getId());
            }
        });
    }


    @Override
    public int getItemCount() {
        return participanteEventos.size();
    }

    public class ParticipanteEventoViewHolder extends RecyclerView.ViewHolder {

        ImageView imagenParticipante;
        TextView usernameParticipante;
        TextView municipioParticipante;
        ImageButton eliminarParticipante;
        ImageView administradorEvento;
        CardView participante;

        public ParticipanteEventoViewHolder(View itemView) {
            super(itemView);

            imagenParticipante = itemView.findViewById(R.id.cardview_imagen_participante_evento);
            usernameParticipante = itemView.findViewById(R.id.cardview_participante_username);
            municipioParticipante = itemView.findViewById(R.id.cardview_participante_municipio);
            eliminarParticipante = itemView.findViewById(R.id.cardview_participante_eliminar);
            administradorEvento = itemView.findViewById(R.id.cardview_imagen_participante_administrador);
            participante = itemView.findViewById(R.id.cardview_participante);
        }
    }
}
