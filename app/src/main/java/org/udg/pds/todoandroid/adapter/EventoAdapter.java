package org.udg.pds.todoandroid.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Evento;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.DateUtil;
import org.udg.pds.todoandroid.util.ExpandAndCollapseViewUtil;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHoler> {

    private List<Evento> eventos;
    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private ApiRest apiRest;

    public void setItem(int position, Evento evento) {

        eventos.set(position, evento);
    }

    public interface OnItemClickListener {
        public void onItemClick(Evento e, int position);
    }

    public EventoAdapter(Context context, List<Evento> eventos, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.eventos = eventos;
        this.mOnItemClickListener = onItemClickListener;
        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();
    }


    public static class EventoViewHoler extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView usernameAdmin;
        TextView emailAdmin;
        ImageView imagenAdmin;
        ImageView imagenEvento;
        TextView deporteEvento;
        TextView participantesEvento;
        TextView municipioEvento;
        TextView duracionEvento;
        TextView tituloEvento;
        TextView descripcionEvento;
        ConstraintLayout layoutTitulo;
        ConstraintLayout layoutDescripcion;
        ImageView imagenTitulo;
        TextView diaMesEvento;
        TextView horaEvento;
        TextView mesEvento;
        TextView diaSemanaEvento;

        EventoViewHoler(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardview);
            usernameAdmin = itemView.findViewById(R.id.cardview_username_administrador);
            emailAdmin = itemView.findViewById(R.id.cardview_email_administrador);
            imagenAdmin = itemView.findViewById(R.id.cardview_imagen_administrador);
            imagenEvento = itemView.findViewById(R.id.cardview_imagen_evento);
            deporteEvento = itemView.findViewById(R.id.cardview_deporte_evento);
            participantesEvento = itemView.findViewById(R.id.cardview_participantes_evento);
            municipioEvento = itemView.findViewById(R.id.cardview_evento_municipio);
            duracionEvento = itemView.findViewById(R.id.cardview_duracion_evento);
            tituloEvento = itemView.findViewById(R.id.cardview_titulo_evento);
            layoutTitulo = itemView.findViewById(R.id.constraintLayout14);
            layoutDescripcion = itemView.findViewById(R.id.constraintLayout15);
            imagenTitulo = itemView.findViewById(R.id.cardview_icono_descripcion);
            descripcionEvento = itemView.findViewById(R.id.cardview_descripcion_evento);
            diaMesEvento = itemView.findViewById(R.id.cardview_dia_mes_evento);
            horaEvento = itemView.findViewById(R.id.cardview_hora_evento);
            mesEvento = itemView.findViewById(R.id.cardview_mes_evento);
            diaSemanaEvento = itemView.findViewById(R.id.cardview_string_dia);


        }
    }


    @NonNull
    @Override
    public EventoAdapter.EventoViewHoler onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_evento, viewGroup, false);
        return new EventoViewHoler(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final EventoAdapter.EventoViewHoler holder, final int position) {


        final Evento eventoActual = eventos.get(position);

        if (eventos.get(position).getId() != null) {
            // Obtener nombre imagen usuario actual para completar la URL
            final Call<String> nombreImagen = apiRest.nombreImagenUsuario(eventoActual.getAdministrador().getId());
            nombreImagen.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                        String imagenNombre = response.body();
                        RequestOptions options = new RequestOptions().centerCrop();
                        Glide.with(context).load(Global.BASE_URL + Global.IMAGE_USER + eventoActual.getAdministrador().getId().toString() + "/" + imagenNombre).apply(options).into(holder.imagenAdmin);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("ERROR: ", t.getMessage());
                }
            });

        } else {
            holder.imagenAdmin.setImageDrawable(null);
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

        holder.usernameAdmin.setText(eventoActual.getAdministrador().getUsername());
        holder.emailAdmin.setText(eventoActual.getAdministrador().getEmail());
        holder.deporteEvento.setText(eventoActual.getDeporte().getDeporte());
        // Participantes ilimitados
        if (eventoActual.getNumeroParticipantes() == 0) {
            holder.participantesEvento.setText(String.valueOf(eventoActual.getParticipantesRegistrados()));
        } else
            holder.participantesEvento.setText(String.valueOf(eventoActual.getParticipantesRegistrados()) + " / " + String.valueOf(eventoActual.getNumeroParticipantes()));
        holder.municipioEvento.setText(eventoActual.getMunicipio().getMunicipio());
        // Duración ilimitada
        if (eventoActual.getDuracion() == 0) {
            holder.duracionEvento.setText("ilimitada");
        } else
            holder.duracionEvento.setText(String.valueOf(eventoActual.getDuracion()) + " minutos");
        holder.tituloEvento.setText(eventoActual.getTitulo());
        holder.descripcionEvento.setText(eventoActual.getDescripcion());

        Date fechaEvento = eventoActual.getFechaEvento();
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaEvento);

        holder.diaMesEvento.setText(DateUtil.dosDigitos(cal.get(Calendar.DAY_OF_MONTH)));
        holder.diaSemanaEvento.setText(DateUtil.diaSemana(cal.get(Calendar.DAY_OF_WEEK)));
        holder.mesEvento.setText(DateUtil.mes(cal.get(Calendar.MONTH)) + " de " + String.valueOf(cal.get(Calendar.YEAR)));
        holder.horaEvento.setText(DateUtil.dosDigitos(cal.get(Calendar.HOUR_OF_DAY)) + ":" + DateUtil.dosDigitos(cal.get(Calendar.MINUTE)));

        holder.layoutTitulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.layoutDescripcion.getVisibility() == View.GONE) {
                    ExpandAndCollapseViewUtil.expand(holder.layoutDescripcion, 100);// duration ms.
                    holder.imagenTitulo.setImageResource(R.mipmap.ic_expand_description);
                    Animation animation = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setFillAfter(true);
                    animation.setDuration(250);
                    holder.imagenTitulo.startAnimation(animation);
                } else {
                    ExpandAndCollapseViewUtil.collapse(holder.layoutDescripcion, 100);
                    holder.imagenTitulo.setImageResource(R.mipmap.ic_collapse_description);
                    Animation animation = new RotateAnimation(0.0f, 180.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setFillAfter(true);
                    animation.setDuration(250);
                    holder.imagenTitulo.startAnimation(animation);
                }
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(eventoActual, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}
