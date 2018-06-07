package org.udg.pds.todoandroid.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;

import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.activity.EventoDetalle;
import org.udg.pds.todoandroid.entity.Evento;
import org.udg.pds.todoandroid.entity.Usuario;
import org.udg.pds.todoandroid.entity.UsuarioActual;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.DateUtil;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TabEventoInformacion extends Fragment {

    private ApiRest apiRest;
    private Long idEventoActual;
    private ImageView imagenEvento;
    private TextView tituloEvento;
    private TextView descripcionEvento;
    private TextView deporteEvento;
    private TextView municipioEvento;
    private TextView fechaEvento;
    private TextView participantesEvento;
    private TextView duracionEvento;

    public TabEventoInformacion() {
    }

    @SuppressLint("ValidFragment")
    public TabEventoInformacion(Long idEvento) {
        this.idEventoActual = idEvento;
    }

    public void update(int posicion) {
        if (posicion == 0){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                   obtenerDatosEvento();
                }
            }, 500);

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_informacion_evento, container, false);

        ((EventoDetalle) getActivity()).actualizarVisibilidadBotonRegistroParticipantes();

        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();

        imagenEvento = rootView.findViewById(R.id.tab_evento_informacion_imagen_evento);
        tituloEvento = rootView.findViewById(R.id.tab_evento_informacion_titulo);
        descripcionEvento = rootView.findViewById(R.id.tab_evento_informacion_descripcion);
        deporteEvento = rootView.findViewById(R.id.tab_evento_informacion_deporte);
        municipioEvento = rootView.findViewById(R.id.tab_evento_informacion_municipio);
        fechaEvento = rootView.findViewById(R.id.tab_evento_informacion_fecha);
        participantesEvento = rootView.findViewById(R.id.tab_evento_informacion_participantes);
        duracionEvento = rootView.findViewById(R.id.tab_evento_informacion_duracion);

        obtenerDatosEvento();

        return rootView;
    }

    private void obtenerDatosEvento() {

        Call<Evento> peticionEventos = apiRest.obtenerInformacionEvento(idEventoActual);
        peticionEventos.enqueue(new Callback<Evento>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Evento> call, Response<Evento> response) {
                if (response.raw().code() != 500 && response.isSuccessful()) {
                    Evento evento = response.body();

                    RequestOptions options = new RequestOptions();
                    options.centerCrop();
                    Glide.with(getActivity().getApplicationContext()).load(Global.BASE_URL + "imagen/evento/" + evento.getId()).apply(options).into(imagenEvento);
                    tituloEvento.setText(evento.getTitulo());
                    descripcionEvento.setText(evento.getDescripcion());
                    deporteEvento.setText(evento.getDeporte().getDeporte());
                    municipioEvento.setText(evento.getMunicipio().getMunicipio());
                    // Número de participantes ilimitados
                    if (evento.getNumeroParticipantes() == 0){
                        participantesEvento.setText(String.valueOf(evento.getParticipantesRegistrados()) + " " + evento.getEstado().getEstado() );
                    }
                    else participantesEvento.setText(String.valueOf(evento.getParticipantesRegistrados()) + "/" + evento.getNumeroParticipantes() + " " + evento.getEstado().getEstado() );
                    // Duración evento ilimitada
                    if (evento.getDuracion() == 0){
                        duracionEvento.setText("ilimitada");
                    }
                    else duracionEvento.setText(String.valueOf(evento.getDuracion()) + " minutos");
                    fechaEvento.setText(DateUtil.parseData(evento.getFechaEvento()));

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Error al obtener la información del evento", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Evento> call, Throwable t) {
                Log.e("ERROR", t.getMessage(), t);
                Toast.makeText(getActivity().getApplicationContext(), "Error al obtener la información del evento", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
