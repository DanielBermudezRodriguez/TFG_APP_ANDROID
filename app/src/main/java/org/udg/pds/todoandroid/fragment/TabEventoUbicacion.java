package org.udg.pds.todoandroid.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Ubicacion;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.InitRetrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class TabEventoUbicacion extends Fragment {

    private GoogleMap googleMap;
    private MapView mMapView;
    private ApiRest apiRest;
    private Long idEventoActual;

    public TabEventoUbicacion(){}

    @SuppressLint("ValidFragment")
    public TabEventoUbicacion(Long id) {
        this.idEventoActual = id;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_ubicacion_evento, container, false);
        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                obtenerUbicacionEvento();

            }
        });


        return rootView;
    }


    private void obtenerUbicacionEvento() {


        Call<Ubicacion> peticionRest = apiRest.ubicacionEvento(idEventoActual);
        peticionRest.enqueue(new Callback<Ubicacion>() {
            @Override
            public void onResponse(Call<Ubicacion> call, Response<Ubicacion> response) {
                if (response.raw().code()!=500 && response.isSuccessful()) {

                    Ubicacion u = response.body();
                    // For dropping a marker at a point on the Map
                    LatLng posicion = new LatLng(u.getLatitud(), u.getLongitud());
                    googleMap.addMarker(new MarkerOptions().position(posicion).title("Marker Title").snippet("Marker Description"));

                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(posicion).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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
            public void onFailure (Call <Ubicacion> call, Throwable t){
                Log.i("ERROR:", t.getMessage());
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
