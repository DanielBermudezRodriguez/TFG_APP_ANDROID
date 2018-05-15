package org.udg.pds.todoandroid.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONObject;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.activity.EventoDetalle;
import org.udg.pds.todoandroid.adapter.EventosRegistradoAdapter;
import org.udg.pds.todoandroid.adapter.ParticipanteEventoAdapter;
import org.udg.pds.todoandroid.entity.Evento;
import org.udg.pds.todoandroid.entity.GenericId;
import org.udg.pds.todoandroid.entity.ParticipanteEvento;
import org.udg.pds.todoandroid.entity.UsuarioActual;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;


@SuppressLint("ValidFragment")
public class TabEventoParticipantes extends Fragment {


    private RecyclerView recyclerView;
    private ApiRest apiRest;
    private Long idEventoActual;
    private List<ParticipanteEvento> participanteEventos = new ArrayList<ParticipanteEvento>();
    private ParticipanteEventoAdapter participanteEventoAdapter;


    public void update(int posicion) {
        if (posicion == 1) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    obtenerParticipantesEvento();
                }
            }, 500);

        }
    }


    @SuppressLint("ValidFragment")
    public TabEventoParticipantes(Long id) {
        this.idEventoActual = id;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_participantes_evento, container, false);

        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();

        ((EventoDetalle) getActivity()).actualizarVisibilidadBotonRegistroParticipantes();
        recyclerView = rootView.findViewById(R.id.recyclerview_participantes_evento);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);



        obtenerParticipantesEvento();


        return rootView;
    }

    public void obtenerParticipantesEvento() {

        Call<List<ParticipanteEvento>> peticionRest = apiRest.obtenerParticipantesEvento(idEventoActual);
        peticionRest.enqueue(new Callback<List<ParticipanteEvento>>() {
            @Override
            public void onResponse(Call<List<ParticipanteEvento>> call, Response<List<ParticipanteEvento>> response) {
                if (response.raw().code() != 500 && response.isSuccessful()) {

                    participanteEventos = response.body();
                    participanteEventoAdapter = new ParticipanteEventoAdapter(getActivity().getApplicationContext(), participanteEventos, ((EventoDetalle)getActivity()).getEsAdministradorEvento(), new ParticipanteEventoAdapter.OnItemClickListener() {

                        @Override
                        public void desapuntarDelEvento(ParticipanteEvento p, int position) {
                            desapuntarUsuarioEvento(idEventoActual,p.getId(),position);
                        }

                    });
                    recyclerView.setAdapter(participanteEventoAdapter);

                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getActivity().getApplicationContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.i("ERROR:", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ParticipanteEvento>> call, Throwable t) {
                Log.i("ERROR:", t.getMessage());
            }
        });

    }

    private void desapuntarUsuarioEvento(Long idEvento,Long idParticipante, final int position) {
        final Call<GenericId> eliminarParticipante = apiRest.eliminarParticipanteEvento(idEvento, idParticipante);
        eliminarParticipante.enqueue(new Callback<GenericId>() {
            @Override
            public void onResponse(Call<GenericId> call, Response<GenericId> response) {
                if (response.raw().code() != 500 && response.isSuccessful()) {
                    GenericId idEvento = response.body();
                    Toast.makeText(getActivity().getApplicationContext(), "El usuario ha sido eliminado", Toast.LENGTH_SHORT).show();
                    participanteEventos.remove(position);
                    recyclerView.removeViewAt(position);
                    participanteEventoAdapter.notifyItemRemoved(position);
                    participanteEventoAdapter.notifyItemRangeChanged(position, participanteEventos.size());

                } else
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getActivity().getApplicationContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.i("ERROR:", e.getMessage());
                    }
            }

            @Override
            public void onFailure(Call<GenericId> call, Throwable t) {
                Log.e("ERROR", t.getMessage(), t);
                Toast.makeText(getActivity().getApplicationContext(), "Error al eliminar el usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
