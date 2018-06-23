package org.udg.pds.todoandroid.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import org.udg.pds.todoandroid.activity.Imagen;
import org.udg.pds.todoandroid.entity.Evento;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.DateUtil;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TabEventoInformacion extends Fragment implements View.OnClickListener {

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
    private Evento evento;

    public TabEventoInformacion() {
    }

    @SuppressLint("ValidFragment")
    public TabEventoInformacion(Long idEvento) {
        this.idEventoActual = idEvento;
    }

    // Actualizar datos vista al apuntar o desapuntar participante del evento
    public void update(int posicion) {
        if (posicion == 0) {
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_informacion_evento_layout, container, false);

        // Actualizamos visibilidad del floatingbutton de apuntar o desapuntar participante al evento actual
        if (getActivity() != null) {
            ((EventoDetalle) getActivity()).actualizarVisibilidadBotonRegistroParticipantes();
        }

        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();

        imagenEvento = rootView.findViewById(R.id.tab_evento_informacion_imagen_evento);
        imagenEvento.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            // Mostrar imagen evento
            case R.id.tab_evento_informacion_imagen_evento:
                if (getActivity() != null && evento != null) {

                    // Obtener nombre imagen evento actual para completar la URL
                    final Call<String> nombreImagen = apiRest.nombreImagenEvento(evento.getId());
                    nombreImagen.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                                String imagenNombre = response.body();
                                Intent imagenIntent = new Intent(getActivity().getApplicationContext(), Imagen.class);
                                imagenIntent.putExtra(Global.URL_IMAGEN, Global.BASE_URL + Global.IMAGE_EVENT + evento.getId().toString() + "/" + imagenNombre);
                                startActivity(imagenIntent);
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e("ERROR: ", t.getMessage());
                        }
                    });

                }
                break;
        }

    }

    private void obtenerDatosEvento() {

        Call<Evento> peticionEventos = apiRest.obtenerInformacionEvento(idEventoActual);
        peticionEventos.enqueue(new Callback<Evento>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Evento> call, Response<Evento> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    evento = response.body();

                    if (evento != null) {
                        if (getActivity() != null) {
                            // Obtener nombre imagen evento actual para completar la URL
                            final Call<String> nombreImagen = apiRest.nombreImagenEvento(evento.getId());
                            nombreImagen.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                                        String imagenNombre = response.body();
                                        RequestOptions options = new RequestOptions().centerCrop();
                                        Glide.with(getActivity().getApplicationContext()).load(Global.BASE_URL + Global.IMAGE_EVENT + evento.getId().toString() + "/" + imagenNombre).apply(options).into(imagenEvento);
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.e("ERROR: ", t.getMessage());
                                }
                            });
                        }

                        tituloEvento.setText(evento.getTitulo());
                        descripcionEvento.setText(evento.getDescripcion());
                        deporteEvento.setText(evento.getDeporte().getDeporte());
                        municipioEvento.setText(evento.getMunicipio().getMunicipio());

                        // Número de participantes ilimitados
                        if (evento.getNumeroParticipantes() == 0) {
                            participantesEvento.setText(String.valueOf(evento.getParticipantesRegistrados()) + " " + evento.getEstado().getEstado());
                        } else
                            participantesEvento.setText(String.valueOf(evento.getParticipantesRegistrados()) + "/" + evento.getNumeroParticipantes() + " " + evento.getEstado().getEstado());
                        // Duración evento ilimitada
                        if (evento.getDuracion() == 0) {
                            duracionEvento.setText(getString(R.string.informacion_evento_duracion_ilimitada));
                        } else
                            duracionEvento.setText(String.valueOf(evento.getDuracion()) + " " + getString(R.string.informacion_evento_duracion_minutos));
                        fechaEvento.setText(DateUtil.parseData(evento.getFechaEvento()));
                    }

                } else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.informacion_evento_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<Evento> call, Throwable t) {
                Log.e(getString(R.string.log_error), t.getMessage(), t);
                if (getActivity() != null) {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.informacion_evento_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
