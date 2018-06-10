package org.udg.pds.todoandroid.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.activity.EventoDetalle;
import org.udg.pds.todoandroid.adapter.ParticipanteEventoAdapter;
import org.udg.pds.todoandroid.entity.GenericId;
import org.udg.pds.todoandroid.entity.ParticipanteEvento;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressLint("ValidFragment")
public class TabEventoParticipantes extends Fragment {


    private RecyclerView recyclerView;
    private ApiRest apiRest;
    private Long idEventoActual;
    private List<ParticipanteEvento> participanteEventos = new ArrayList<>();
    private ParticipanteEventoAdapter participanteEventoAdapter;
    private Long administrador;
    // Refrescar recyclerview scroll hacia arriba
    private SwipeRefreshLayout swipeRefreshLayout;

    @SuppressLint("ValidFragment")
    public TabEventoParticipantes(Long id, Long administrador) {
        this.idEventoActual = id;
        this.administrador = administrador;
    }


    // Actualizar información al apuntar o desapuntar un participante del evento
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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_participantes_evento_layout, container, false);

        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();

        if (getActivity() != null) {
            ((EventoDetalle) getActivity()).actualizarVisibilidadBotonRegistroParticipantes();
        }
        recyclerView = rootView.findViewById(R.id.recyclerview_participantes_evento);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        participanteEventoAdapter = new ParticipanteEventoAdapter(getActivity().getApplicationContext(), administrador, participanteEventos, ((EventoDetalle) getActivity()).getEsAdministradorEvento(), new ParticipanteEventoAdapter.OnItemClickListener() {

            @Override
            public void desapuntarDelEvento(ParticipanteEvento p, int position) {
                desapuntarUsuarioEvento(idEventoActual, p.getId(), position);
            }

        });
        recyclerView.setAdapter(participanteEventoAdapter);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (participanteEventos != null)
                            participanteEventos.clear();
                        obtenerParticipantesEvento();
                    }
                });

        // Obtenemos participantes registrados en el evento
        obtenerParticipantesEvento();
        return rootView;
    }

    public void obtenerParticipantesEvento() {

        Call<List<ParticipanteEvento>> peticionRest = apiRest.obtenerParticipantesEvento(idEventoActual);
        peticionRest.enqueue(new Callback<List<ParticipanteEvento>>() {
            @Override
            public void onResponse(Call<List<ParticipanteEvento>> call, Response<List<ParticipanteEvento>> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {

                    List<ParticipanteEvento> participantes = response.body();
                    if (participantes != null) {
                        participanteEventos.clear();
                        participanteEventos.addAll(participantes);
                    }
                    participanteEventoAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);

                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<ParticipanteEvento>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e(getString(R.string.log_error), t.getMessage());
            }
        });

    }

    private void desapuntarUsuarioEvento(Long idEvento, Long idParticipante, final int position) {
        final Call<GenericId> eliminarParticipante = apiRest.eliminarParticipanteEvento(idEvento, idParticipante);
        eliminarParticipante.enqueue(new Callback<GenericId>() {
            @Override
            public void onResponse(Call<GenericId> call, Response<GenericId> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    if (getActivity() != null) {
                        ((EventoDetalle) getActivity()).showSnackBar(getString(R.string.participantes_evento_eliminar_participante), false);
                    }
                    participanteEventos.remove(position);
                    recyclerView.removeViewAt(position);
                    participanteEventoAdapter.notifyItemRemoved(position);
                    participanteEventoAdapter.notifyItemRangeChanged(position, participanteEventos.size());

                } else
                    try {
                        JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                        if (getActivity() != null) {
                            ((EventoDetalle) getActivity()).showSnackBar(jObjError.getString(getString(R.string.error_server_message)), false);
                        }
                    } catch (Exception e) {
                        Log.e(getString(R.string.log_error), e.getMessage());
                    }
            }

            @Override
            public void onFailure(Call<GenericId> call, Throwable t) {
                Log.e(getString(R.string.log_error), t.getMessage(), t);
                if (getActivity() != null) {
                    ((EventoDetalle) getActivity()).showSnackBar(getString(R.string.participantes_evento_error_eliminar), false);
                }
            }
        });
    }


}
