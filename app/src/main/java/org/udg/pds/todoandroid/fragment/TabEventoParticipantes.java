package org.udg.pds.todoandroid.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.adapter.ParticipanteEventoAdapter;
import org.udg.pds.todoandroid.entity.ParticipanteEvento;
import org.udg.pds.todoandroid.entity.Ubicacion;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.InitRetrofit;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TabEventoParticipantes extends Fragment {


    private RecyclerView recyclerView;
    private ApiRest apiRest;
    private Long idEventoActual;
    private List<ParticipanteEvento> participanteEventos = new ArrayList<ParticipanteEvento>();
    private ParticipanteEventoAdapter participanteEventoAdapter;


    public TabEventoParticipantes (){}

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

        recyclerView = rootView.findViewById(R.id.recyclerview_participantes_evento);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        obtenerParticipantesEvento();


        return rootView;
    }

    private void obtenerParticipantesEvento() {

        Call<List<ParticipanteEvento>> peticionRest = apiRest.obtenerParticipantesEvento(idEventoActual);
        peticionRest.enqueue(new Callback<List<ParticipanteEvento>>() {
            @Override
            public void onResponse(Call<List<ParticipanteEvento>> call, Response<List<ParticipanteEvento>> response) {
                if (response.raw().code()!=500 && response.isSuccessful()) {

                    participanteEventos = response.body();
                    participanteEventoAdapter = new ParticipanteEventoAdapter(getActivity().getApplicationContext(), participanteEventos);
                    recyclerView.setAdapter(participanteEventoAdapter);


                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getActivity().getApplicationContext(),jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.i("ERROR:", e.getMessage());
                    }
                }
            }
            @Override
            public void onFailure (Call <List<ParticipanteEvento>> call, Throwable t){
                Log.i("ERROR:", t.getMessage());
            }
        });

    }
}
