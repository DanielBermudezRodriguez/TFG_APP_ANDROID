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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
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
    // Lista de eventos
    private RecyclerView recycler;
    // Linear layout manager del recyclerview
    private LinearLayoutManager lManager;
    // Adaptador de eventos
    private EventoAdapter adapter;
    // Lista de eventos actuales
    private List<Evento> eventos = new ArrayList<>();
    // Variable que controla si se hace scroll en la lista de eventos
    private Boolean isScrolling = false;
    // Variables de control de la lista de eventos i el scroll en la lista
    private int currentItems, totalItems, scrollOutItems;
    // variable de control desde la posición a obtener los siguientes eventos del servidor al hacer scroll
    private int offset = 0;
    // URL de búsqueda de eventos actual
    private String url = "";
    // Texto al no obtener resultados de la búsqueda por defecto o personalizada a través del buscador
    private TextView noResultadosPorDefecto;
    // Indica si se está realizando una petición al servidor
    private Boolean requestServer = false;
    // Variable de control busqueda por defecto o a través del buscador
    private Boolean esBusquedaPorDefecto = true;
    // Refrescar recyclerview scroll hacia arriba
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Miramos si el usuario actual está logeadoo
        if (UsuarioActual.getInstance().getId() == -1L) {
            Intent main = new Intent(this, Login.class);
            // Si no está logeado eliminamos de la pila la actividad Principal
            main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(main);
            finish();
        }
        setContentView(R.layout.principal_layout);

        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();


        // Ponemos el toolbar
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

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
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);
        // creamos adaptador con el evento click
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
        // Scroll de la lista de eventos segun el valor de la variable offset
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = lManager.getChildCount();
                totalItems = lManager.getItemCount();
                scrollOutItems = lManager.findFirstVisibleItemPosition();

                if (isScrolling && dy > 0 && !requestServer && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    requestServer = true;
                    // Obtener siguiente eventos
                    obtenerEventos();
                }
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (eventos != null)
                            eventos.removeAll(eventos);
                        offset = 0;
                        obtenerEventos();
                    }
                });

        // Busqueda inicial de eventos segun preferencias del usuario por defecto
        if (esBusquedaPorDefecto)
            busquedaInicialEventos();
    }


    // RESULTADOS DEVUELTOS BUSCADOR ------------------------------------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Resultados buscador:
        if (requestCode == Global.REQUEST_CODE_BUSCADOR) {
            if (resultCode == RESULT_OK) {
                if (data.getExtras() != null) {
                    eventos.clear();
                    url = data.getStringExtra(Global.PARAMETER_RESULTADOS_BUSCADOR);
                    offset = 0;
                    esBusquedaPorDefecto = false;
                    obtenerEventos();
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
                    if (usuario != null) {
                        StringBuilder auxURL = new StringBuilder(Global.BASE_URL + "evento?limite=" + String.valueOf(Global.LIMITE_EVENTOS_REQUEST) + "&municipio=" + usuario.getMunicipio().getId());
                        for (Deporte d : usuario.getDeportesFavoritos()) {
                            auxURL.append("&deportes=").append(d.getId());
                        }
                        url = auxURL.toString();
                        obtenerEventos();
                    }

                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e(getString(R.string.log_error), t.getMessage());
            }
        });
    }

    private void obtenerEventos() {
        if (offset == -1) return;
        String urlOffset = url + "&offset=" + String.valueOf(offset);
        Call<List<Evento>> peticionEventos = apiRest.buscadorEventos(urlOffset);
        peticionEventos.enqueue(new Callback<List<Evento>>() {
            @Override
            public void onResponse(Call<List<Evento>> call, Response<List<Evento>> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    List<Evento> eventosRespuesta = response.body();
                    // Búsqueda inicial de eventos vacia
                    if ((eventosRespuesta == null || eventosRespuesta.isEmpty()) && (offset == 0)) {
                        adapter.notifyDataSetChanged();
                        if (esBusquedaPorDefecto) {
                            noResultadosPorDefecto.setText(R.string.no_resultados_busqueda_inicial);
                        } else
                            noResultadosPorDefecto.setText(getString(R.string.no_resultados_busqueda));
                        noResultadosPorDefecto.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    // No hay mas eventos a devolver del servidor
                    else if ((eventosRespuesta == null || eventosRespuesta.isEmpty()) && (offset > 0)) {
                        offset = -1;
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    // Hay resultados de eventos en la búsqueda por defecto
                    else if (eventosRespuesta != null) {
                        // offset + limite
                        offset += Global.LIMITE_EVENTOS_REQUEST;
                        noResultadosPorDefecto.setVisibility(View.GONE);
                        // Cargamos los eventos resultantes en el adaptador del recyclerview
                        eventos.addAll(eventosRespuesta);
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        //inicializarAdaptador(eventos);
                    }
                    requestServer = false;
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    // Obtenemos mensaje de error del servidor
                    requestServer = false;
                    try {
                        JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                        Log.e(getString(R.string.log_error), jObjError.getString(getString(R.string.error_server_message)));
                    } catch (Exception e) {
                        Log.e(getString(R.string.log_error), e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Evento>> call, Throwable t) {
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
            final Call<String> nombreImagen = apiRest.nombreImagenUsuario(UsuarioActual.getInstance().getId());
            nombreImagen.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                        String imagenNombre = response.body();
                        Intent imagenIntent = new Intent(getApplicationContext(), Imagen.class);
                        imagenIntent.putExtra(Global.URL_IMAGEN, Global.BASE_URL + Global.IMAGE_USER + UsuarioActual.getInstance().getId().toString() + "/" + imagenNombre);
                        startActivity(imagenIntent);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e(getString(R.string.log_error), t.getMessage());
                }
            });

        }
        // Ver información perfil
        if (position == 1) {
            Intent perfilUsuario = new Intent(getApplicationContext(), PerfilUsuario.class);
            perfilUsuario.putExtra(Global.ID_USUARIO_PERFIL, UsuarioActual.getInstance().getId());
            startActivity(perfilUsuario);
        }
        // Modificar perfil
        if (position == 2) {
            Call<Usuario> call = apiRest.perfilUsuario(UsuarioActual.getInstance().getId());
            call.enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                        Usuario usuario = response.body();
                        Intent modificarPerfil = new Intent(getApplicationContext(), ModificarPerfil.class);
                        modificarPerfil.putExtra(Global.KEY_ACTUAL_USER, usuario);
                        startActivity(modificarPerfil);
                    }
                }

                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    Log.e(getString(R.string.log_error), t.getMessage());
                }
            });
        }
        // Notificaciones usuario
        if (position == 3) {
            Intent i = new Intent(getApplicationContext(), Notificacion.class);
            startActivity(i);
        }
        // Eventos creados y apuntados del usuario actual
        if (position == 4) {
            Intent i = new Intent(getApplicationContext(), MisEventos.class);
            startActivity(i);
        }
        // buscador evento
        if (position == 5) {
            onSearchItemSelected();

        }
        // crear nuevo evento
        if (position == 6) {
            crearEvento();

        }
        // Cerrar Sesión
        if (position == 7) {
            cerrarSesion();

        }

    }


    private void cerrarSesion() {
        Call<GenericId> peticionRest = apiRest.logout(UsuarioActual.getInstance().getId());
        peticionRest.enqueue(new Callback<GenericId>() {
            @Override
            public void onResponse(Call<GenericId> call, Response<GenericId> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    UsuarioActual.getInstance().setId(-1L);
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
