package org.udg.pds.todoandroid.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import org.json.JSONObject;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.adapter.EventoAdapter;
import org.udg.pds.todoandroid.entity.Deporte;
import org.udg.pds.todoandroid.entity.Evento;
import org.udg.pds.todoandroid.entity.GenericId;
import org.udg.pds.todoandroid.entity.Ubicacion;
import org.udg.pds.todoandroid.entity.Usuario;
import org.udg.pds.todoandroid.entity.UsuarioActual;
import org.udg.pds.todoandroid.fragment.MenuLateralFragment;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;
import org.udg.pds.todoandroid.util.Localizacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Principal extends AppCompatActivity implements MenuLateralFragment.NavigationDrawerCallbacks {

    private ApiRest apiRest;
    private RecyclerView recycler;
    private EventoAdapter adapter;
    private TextView noResultadosPorDefecto;
    private List<Evento> eventos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Miramos si el usuario actual está logeadoo
        if (UsuarioActual.getInstance().getId() == -1L) {
            Intent main = new Intent(this, Login.class);
            // Si no está logeado eliminamos de la pila la actividad Principal
            main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(main);
        }
        setContentView(R.layout.principal_layout);

        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();

        // Ponemos el toolbar
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        // Logo de la aplicación
        toolbar.setLogo(R.mipmap.ic_logo_prueba2);

        // Preparar el menú lateral
        MenuLateralFragment mNavigationDrawerFragment = (MenuLateralFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        // Pedir permisos para utilizar GPS del dispositivo
        obtenerUbicacionGPSActual();

        // Texto si no hay resultados por defecto
        noResultadosPorDefecto = findViewById(R.id.no_resultados_eventos_por_defecto);
        noResultadosPorDefecto.setVisibility(View.GONE);

        // Obtener el Recycler
        recycler = findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);
        recycler.setItemViewCacheSize(20);
        recycler.setDrawingCacheEnabled(true);
        recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        LinearLayoutManager lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Busqueda inicial de eventos segun preferencias del usuario por defecto
        busquedaInicialEventos();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }


    // RESULTADOS DEVUELTOS BUSCADOR ------------------------------------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Resultados buscador:
        if (requestCode == Global.REQUEST_CODE_BUSCADOR) {
            if (resultCode == RESULT_OK) {
                if (data.getExtras() != null) {
                    eventos = (List<Evento>) data.getExtras().getSerializable(Global.PARAMETER_RESULTADOS_BUSCADOR);
                }
                if (eventos == null || eventos.isEmpty()) {
                    inicializarAdaptador(eventos);
                    noResultadosPorDefecto.setText(getString(R.string.no_resultados_busqueda));
                    noResultadosPorDefecto.setVisibility(View.VISIBLE);
                } else {
                    noResultadosPorDefecto.setVisibility(View.GONE);
                    inicializarAdaptador(eventos);
                }

            }
        }
        // Evento al visualizar su detalle
        if (requestCode == Global.REQUEST_CODE_EVENTO_DETALLE) {
            int position = Objects.requireNonNull(data.getExtras()).getInt(Global.PARAMETER_POSICION_EVENTO_ADAPTER);
            Evento evento = (Evento) data.getExtras().getSerializable(Global.PARAMETER_EVENTO_ADAPTER);
            // Actualizamos el elemento en la lista del recyclerview
            if (position >= 0) {
                adapter.setItem(position, evento);
                adapter.notifyItemChanged(position);
            }
        }
    }

    // Inicializamos la lista de eventos asignandoles el evento onClick para acceder a su detalle
    private void inicializarAdaptador(List<Evento> eventos) {
        adapter = new EventoAdapter(getApplicationContext(), eventos, new EventoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Evento e, int position) {
                Intent i = new Intent(getApplicationContext(), EventoDetalle.class);
                i.putExtra(Global.KEY_SELECTED_EVENT, e);
                i.putExtra(Global.KEY_SELECTED_EVENT_IS_ADMIN, e.getAdministrador().getId().equals(UsuarioActual.getInstance().getId()));
                i.putExtra(Global.KEY_SELECTED_EVENT_POSITION, position);
                startActivityForResult(i, Global.REQUEST_CODE_EVENTO_DETALLE);
            }
        });
        recycler.setAdapter(adapter);
    }

    // LOCALIZACIÓN GPS -------------------------------------------------------------------------------------------------------------

    // Pedir permisos para poder utilizar el GPS del dispositivo
    private void obtenerUbicacionGPSActual() {
        // Pedir permisos para utilizar ubicación del dispositivo
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si no hay permisos concedidos se piden:
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, Global.REQUEST_CODE_GPS_LOCATION);
        } else {
            // Si la aplicación está autorizada a la detección de localicazión:
            determinarUbicacion();
        }
    }

    // Activamos el servicio para obtener la ubicación actual del dispositivo mediante GPS
    private void determinarUbicacion() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        // Assignamos actividad actual a la clase localización
        Local.setPrincipal(this);
        boolean gpsEnabled = false;
        if (mlocManager != null) {
            gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
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
        if (mlocManager != null) {
            mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Global.MINIMUM_TIME_UPDATE_LOCATION, Global.MINIMUM_DISTANCE_UPDATE_LOCATION, Local);
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Global.MINIMUM_TIME_UPDATE_LOCATION, Global.MINIMUM_DISTANCE_UPDATE_LOCATION, Local);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Global.REQUEST_CODE_GPS_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                determinarUbicacion();
            }
        }
    }

    // Se recibe una nueva actualización de posicionamiento GPS
    public void setLocation(Location localizacion) {
        if (localizacion != null && localizacion.getLatitude() != 0.0 && localizacion.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                // Obtenemos una lista con las direcciones aproximadas. Nos quedamos solo la primera
                List<Address> list = geocoder.getFromLocation(
                        localizacion.getLatitude(), localizacion.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address datosUbicacion = list.get(0);
                    Ubicacion ubicacionActual = new Ubicacion(datosUbicacion.getLatitude(), datosUbicacion.getLongitude(), datosUbicacion.getAddressLine(0), datosUbicacion.getLocality());
                    Call<GenericId> peticionRest = apiRest.guardarUbicacionActualUsuario(ubicacionActual);
                    peticionRest.enqueue(new Callback<GenericId>() {
                        @Override
                        public void onResponse(Call<GenericId> call, Response<GenericId> response) {
                            if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                                Log.i(getString(R.string.log_info), "Ubicación registrada correctamente");
                            } else {
                                // Obtenemos mensaje de error del servidor
                                try {
                                    JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                                    Log.e(getString(R.string.log_error), jObjError.getString(getString(R.string.error_server_message)));
                                } catch (Exception e) {
                                    Log.e(getString(R.string.log_error), e.getMessage());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<GenericId> call, Throwable t) {
                            Log.e(getString(R.string.log_error), t.getMessage());
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(getString(R.string.log_error), "Error al guardar la ubicación actual");
            }
        }
    }

    // BÚSQUEDA INICIAL PRODUCTOS -------------------------------------------------------------------------------------------------------------

    // Búsqueda por defecto segun preferencias del usuario registrado
    private void busquedaInicialEventos() {
        Call<Usuario> call = apiRest.perfilUsuario(UsuarioActual.getInstance().getId());
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    Usuario usuario = response.body();
                    StringBuilder url = new StringBuilder(Global.BASE_URL + "evento?distancia=100");
                    for (Deporte d : Objects.requireNonNull(usuario).getDeportesFavoritos()) {
                        url.append("&deportes=").append(d.getId());
                    }
                    Call<List<Evento>> peticionEventos = apiRest.buscadorEventos(url.toString());
                    peticionEventos.enqueue(new Callback<List<Evento>>() {
                        @Override
                        public void onResponse(Call<List<Evento>> call, Response<List<Evento>> response) {
                            if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                                eventos = response.body();
                                if (eventos == null || eventos.isEmpty()) {
                                    inicializarAdaptador(eventos);
                                    noResultadosPorDefecto.setText(R.string.no_resultados_busqueda_inicial);
                                    noResultadosPorDefecto.setVisibility(View.VISIBLE);
                                } else {
                                    noResultadosPorDefecto.setVisibility(View.GONE);
                                    // Cargamos los eventos resultantes en el adaptador del recyclerview
                                    inicializarAdaptador(eventos);
                                }
                            } else
                                // Obtenemos mensaje de error del servidor
                                try {
                                    JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                                    Log.e(getString(R.string.log_error), jObjError.getString(getString(R.string.error_server_message)));
                                } catch (Exception e) {
                                    Log.e(getString(R.string.log_error), e.getMessage());
                                }
                        }

                        @Override
                        public void onFailure(Call<List<Evento>> call, Throwable t) {
                            Log.e(getString(R.string.log_error), t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e(getString(R.string.log_error), t.getMessage());
            }
        });
    }


    // MENÚ LATERAL ----------------------------------------------------------------------------------------------------------------------------

    // Acciones de los items del menú lateral
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // visualizar en pantalla completa imagen perfil usuario actual
        if (position == 0) {
            Intent imagenIntent = new Intent(this, Imagen.class);
            imagenIntent.putExtra(Global.URL_IMAGEN, Global.BASE_URL + Global.IMAGE_USER + UsuarioActual.getInstance().getId().toString());
            startActivity(imagenIntent);
        }
        // Ver información perfil
        if (position == 1) {
            Intent perfilUsuario = new Intent(getApplicationContext(), PerfilUsuario.class);
            startActivity(perfilUsuario);
        }
        // Modificar perfil
        if (position == 2) {
        System.out.print("");
        }
        // Cerrar Sesión
        if (position == 3) {
            cerrarSesion();
        }
        // Eventos creados y apuntados del usuario actual
        if (position == 4) {
            Intent i = new Intent(getApplicationContext(), MisEventos.class);
            startActivity(i);
        }

    }


    private void cerrarSesion() {
        Call<GenericId> peticionRest = apiRest.logout(UsuarioActual.getInstance().getId());
        peticionRest.enqueue(new Callback<GenericId>() {
            @Override
            public void onResponse(Call<GenericId> call, Response<GenericId> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    Intent principal = new Intent(getApplicationContext(), Login.class);
                    // Eliminamos de la pila todas las actividades
                    principal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(principal);
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                        Log.i(getString(R.string.log_info), jObjError.toString());
                    } catch (Exception e) {
                        Log.e(getString(R.string.log_error), e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<GenericId> call, Throwable t) {
                Log.e(getString(R.string.log_error), t.getMessage());
            }
        });
    }

    @Override
    public void onSearchItemSelected() {
        Intent buscador = new Intent(getApplicationContext(), Buscador.class);
        startActivityForResult(buscador, Global.REQUEST_CODE_BUSCADOR);
    }

    @Override
    public void crearEvento() {
        Intent crearEvento = new Intent(getApplicationContext(), CrearEvento.class);
        startActivity(crearEvento);
    }


    // RECYCLERVIEW AND CARDVIEW ---------------------------------------------------------------------------------------------

    // Comportamiento del botón 'Atrás' del sistema operativo
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
