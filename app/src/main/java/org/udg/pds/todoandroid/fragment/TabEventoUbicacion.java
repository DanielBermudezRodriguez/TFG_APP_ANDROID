package org.udg.pds.todoandroid.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.activity.EventoDetalle;
import org.udg.pds.todoandroid.entity.Ubicacion;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TabEventoUbicacion extends Fragment {

    private GoogleMap googleMap;
    private MapView mMapView;
    private ApiRest apiRest;
    private Long idEventoActual;

    public TabEventoUbicacion() {
    }

    @SuppressLint("ValidFragment")
    public TabEventoUbicacion(Long id) {
        this.idEventoActual = id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_ubicacion_evento, container, false);
        if (getActivity() != null)
            ((EventoDetalle) getActivity()).actualizarVisibilidadBotonRegistroParticipantes();
        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();
        mMapView = rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);




        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onResume(); // mostrat mapa inmediatamente

        try {
            if (getActivity() != null)
                MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            Log.e(getString(R.string.log_error), e.getMessage());
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                obtenerUbicacionEvento();

            }
        });
    }


    private void obtenerUbicacionEvento() {


        Call<Ubicacion> peticionRest = apiRest.ubicacionEvento(idEventoActual);
        peticionRest.enqueue(new Callback<Ubicacion>() {
            @Override
            public void onResponse(Call<Ubicacion> call, Response<Ubicacion> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {

                    Ubicacion u = response.body();
                    if (u != null) {
                        LatLng posicion = new LatLng(u.getLatitud(), u.getLongitud());
                        if (u.getDireccion() != null) {
                            googleMap.addMarker(new MarkerOptions().position(posicion).title(u.getMunicipio()).snippet(u.getDireccion()));
                        } else {
                            googleMap.addMarker(new MarkerOptions().position(posicion).title(u.getMunicipio()));
                        }

                        // Realizar zoom automático a la posición indicada en el mapa
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(posicion).zoom(12).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }
            }

            @Override
            public void onFailure(Call<Ubicacion> call, Throwable t) {
                Log.i(getString(R.string.log_error), t.getMessage());
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


}
