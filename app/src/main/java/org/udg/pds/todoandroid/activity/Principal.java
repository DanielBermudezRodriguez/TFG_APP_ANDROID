package org.udg.pds.todoandroid.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONObject;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Deporte;
import org.udg.pds.todoandroid.entity.Evento;
import org.udg.pds.todoandroid.entity.Ubicacion;
import org.udg.pds.todoandroid.entity.Usuario;
import org.udg.pds.todoandroid.entity.UsuarioActual;
import org.udg.pds.todoandroid.fragment.MenuLateralFragment;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;
import org.udg.pds.todoandroid.util.Localizacion;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Principal extends AppCompatActivity implements MenuLateralFragment.NavigationDrawerCallbacks {

    // guarda el último título de pantalla
    private CharSequence mTitle;

    private ApiRest apiRest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Miramos si el usuario está logeado
        if (UsuarioActual.getInstance().getId() == -1L) {
            Intent main = new Intent(this, MainActivity.class);
            // Si no está logeado eliminamos de la pila la actividad Principal
            main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(main);
        }
        setContentView(R.layout.activity_principal);

        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();

        // Ponemos el toolbar
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

        MenuLateralFragment mNavigationDrawerFragment = (MenuLateralFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // Preparar el menú lateral
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));


        // Pedir permisos para utilizar ubicación del dispositivo
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si no hay permisos concedidos se piden:
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, Global.REQUEST_CODE_GPS_LOCATION);
        } else {
            // Si la aplicación está autorizada a la detección de localicazión:
            determinarUbicacion();
        }

        // Busqueda inicial de eventos segun características del usuario logeado
        busquedaInicialEventos();

    }

    // LOCALIZACIÓN GPS -------------------------------------------------------------------------------------------------------------

    private void determinarUbicacion() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        // Assignamos actividad actual a la clase localización
        Local.setPrincipal(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // Si el GPS se encuentra deshabilitado, se pide habilitar-lo
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        // Si no hay permisos de piden
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, Global.REQUEST_CODE_GPS_LOCATION);
            return;
        }

        // Pedir actualizaciones de posición (cada minuto y notificar si distancia es > 1km respecto a la anterior)
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Global.MINIMUM_TIME_UPDATE_LOCATION, Global.MINIMUM_DISTANCE_UPDATE_LOCATION, Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Global.MINIMUM_TIME_UPDATE_LOCATION, Global.MINIMUM_DISTANCE_UPDATE_LOCATION, Local);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Global.REQUEST_CODE_GPS_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                determinarUbicacion();
            }
        }
    }

    public void setLocation(Location localizacion) {
        //Se ha recibido una actualizacion de la ubicación del usuario actual
        if (localizacion != null && localizacion.getLatitude() != 0.0 && localizacion.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        localizacion.getLatitude(), localizacion.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address datosUbicacion = list.get(0);
                    Ubicacion ubicacionActual = new Ubicacion(datosUbicacion.getLatitude(), datosUbicacion.getLongitude(), datosUbicacion.getAddressLine(0), datosUbicacion.getLocality());
                    Call<Long> peticionRest = apiRest.guardarUbicacionActualUsuario(ubicacionActual);
                    peticionRest.enqueue(new Callback<Long>() {
                        @Override
                        public void onResponse(Call<Long> call, Response<Long> response) {
                            if (response.raw().code() != 500 && response.isSuccessful()) {

                                Long idUbicacion = response.body();
                                Log.d("DEBUG", "Ubicación registrada correctamente con identificador: " + idUbicacion + ".");

                            } else {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    Toast.makeText(getApplicationContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Log.i("ERROR:", e.getMessage());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Long> call, Throwable t) {
                            Log.i("ERROR:", t.getMessage());
                        }
                    });


                    Log.d("DEBUG", "Direccion : " + datosUbicacion.getAddressLine(0));
                    Log.d("DEBUG", "Pais : " + datosUbicacion.getCountryName());
                    Log.d("DEBUG", "Comunidad autónoma : " + datosUbicacion.getAdminArea());
                    Log.d("DEBUG", "Provincia : " + datosUbicacion.getSubAdminArea());
                    Log.d("DEBUG", "Municipio : " + datosUbicacion.getLocality());
                    Log.d("DEBUG", "Latitud : " + datosUbicacion.getLatitude());
                    Log.d("DEBUG", "Longitud : " + datosUbicacion.getLongitude());

                }
            } catch (Exception e) {
                Log.d("ERROR", "Error al guardar la ubicación actual");
            }
        }
    }

    // BÚSQUEDA INICIAL PRODUCTOS -------------------------------------------------------------------------------------------------------------

    private void busquedaInicialEventos() {

        Call<Usuario> call = apiRest.perfilUsuario(UsuarioActual.getInstance().getId());
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.raw().code() != 500 && response.isSuccessful()) {
                    Usuario usuario = response.body();
                    String url = Global.BASE_URL + "evento?distancia=100";
                    for (Deporte d : usuario.getDeportesFavoritos()) {
                        url += "&deportes=" + d.getId();
                    }
                    Call<List<Evento>> peticionEventos = apiRest.buscadorEventos(url);
                    peticionEventos.enqueue(new Callback<List<Evento>>() {
                        @Override
                        public void onResponse(Call<List<Evento>> call, Response<List<Evento>> response) {
                            if (response.raw().code() != 500 && response.isSuccessful()) {
                                List<Evento> eventos = response.body();
                                System.out.println("Eventos recibidos: " + eventos.size());

                            } else
                                Toast.makeText(getApplicationContext(), "Error al buscar eventos", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<List<Evento>> call, Throwable t) {
                            Log.e("ERROR", t.getMessage(), t);
                            Toast.makeText(getApplicationContext(), "Error al buscar eventos", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("ERROR", t.getMessage(), t);
            }
        });

    }


    // MENÚ LATERAL ----------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // Actualizar el contenido principal mediante la substitución de fragmentos o actividades segun la posición del menú lateral escojida
        System.out.println("Posición actual: " + position);
        if (position == 0) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                    .commit();
        }
        if (position == 1) {
            Intent perfilUsuario = new Intent(getApplicationContext(), PerfilUsuario.class);
            startActivity(perfilUsuario);
        }
    }

    @Override
    public void onSearchItemSelected(){
        Intent buscador = new Intent(getApplicationContext(), Buscador.class);
        startActivity(buscador);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    // Fragmento de marcador de posición
    public static class PlaceholderFragment extends Fragment {
        // Número de la posición de la sección del menú lateral
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        // Devuelve una nueva instancia del Fragment para el número de sección del menú
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_principal, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((Principal) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
