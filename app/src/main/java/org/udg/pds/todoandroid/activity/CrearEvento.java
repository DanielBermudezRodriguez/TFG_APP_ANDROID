package org.udg.pds.todoandroid.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.tooltip.Tooltip;

import org.json.JSONObject;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Deporte;
import org.udg.pds.todoandroid.entity.Municipio;
import org.udg.pds.todoandroid.entity.Pais;
import org.udg.pds.todoandroid.entity.Provincia;
import org.udg.pds.todoandroid.fragment.DatePickerFragment;
import org.udg.pds.todoandroid.fragment.SeleccionarDeporteEventoDialog;
import org.udg.pds.todoandroid.fragment.SeleccionarMunicipioDialog;
import org.udg.pds.todoandroid.fragment.SeleccionarProvinciasDialog;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;
import org.udg.pds.todoandroid.util.InputFilterMinMax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearEvento extends AppCompatActivity implements View.OnClickListener, SeleccionarDeporteEventoDialog.SeleccionarDeporteEventoDialogListener, SeleccionarProvinciasDialog.SeleccionarProvinciasDialogListener, SeleccionarMunicipioDialog.SeleccionarMunicipioDialogListener {

    private ApiRest apiRest;

    // Categoría deportiva
    private List<Deporte> deportes = new ArrayList<Deporte>();
    private int deporteSeleccionado = -1;

    // Fecha evento
    private int year = -1;
    private int month = -1;
    private int day = -1;

    // Hora evento
    private int hora = -1;
    private int minutos = -1;

    // Ubicación Evento escojiendo provincia y municipio
    private List<Pais> paises = new ArrayList<Pais>();
    private int paisActual = -1;
    private List<Provincia> provincias = new ArrayList<Provincia>();
    private int provinciaActual = -1;
    private List<Municipio> municipios = new ArrayList<Municipio>();
    private int municipioActual = -1;
    private boolean esMunicipio = false;

    // Ubicación evento seleccionando en el mapa de google
    public double latitud;
    public double longitud;
    public String direccion;
    public String municipioDireccion;
    private boolean esUbicacionGPS = false;

    // Campos formulario
    private EditText tituloEvento;
    private EditText descripcionEvento;
    private TextView deporteEvento;
    private TextView deporteEventoText;
    private EditText participantesEvento;
    private ImageView tooltipParticipantes;
    private EditText duracionEvento;
    private Switch privacidadForo;
    private ImageView tooltipPrivacidadForo;
    private ConstraintLayout seleccionarFecha;
    private TextView mostrarFecha;
    private ConstraintLayout seleccionarHora;
    private TextView mostrarHora;
    private Button ubicacionEvento;
    private Button municipioEvento;
    private ConstraintLayout seleccionarProvincia;
    private TextView provincia;
    private ConstraintLayout seleccionarMunicipio;
    private TextView municipio;
    private ConstraintLayout seleccionarUbicacion;
    private TextView ubicacion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_evento);

        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();

        //Obtenemos las categorías deportivas disponibles:
        obtenerDeportes();

        // Ponemos el toolbar
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Ocultar layout ubicación
        findViewById(R.id.crear_evento_seleccionar_ubicacion).setVisibility(View.GONE);

        // Obtener campos formulario
        tituloEvento = findViewById(R.id.crear_evento_titulo);
        descripcionEvento = findViewById(R.id.crear_evento_descripcion);
        deporteEvento = findViewById(R.id.crear_evento_deporte);
        deporteEvento.setOnClickListener(this);
        deporteEventoText = findViewById(R.id.crear_evento_deporte_texto);
        participantesEvento = findViewById(R.id.crear_evento_participantes);
        participantesEvento.setFilters(new InputFilter[]{new InputFilterMinMax("0", "9999")});
        tooltipParticipantes = findViewById(R.id.crear_evento_tooltip_participantes);
        tooltipParticipantes.setOnClickListener(this);
        duracionEvento = findViewById(R.id.crear_evento_duracion);
        duracionEvento.setFilters(new InputFilter[]{new InputFilterMinMax("0", "9999")});
        privacidadForo = findViewById(R.id.crear_evento_privacidad_foro);
        privacidadForo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (privacidadForo.isChecked()) privacidadForo.setText("Público");
                else privacidadForo.setText("Privado");
            }
        });
        tooltipPrivacidadForo = findViewById(R.id.crear_evento_tooltip_privacidad_foro);
        tooltipPrivacidadForo.setOnClickListener(this);
        seleccionarFecha = findViewById(R.id.crear_evento_seleccionar_fecha);
        seleccionarFecha.setOnClickListener(this);
        mostrarFecha = findViewById(R.id.crear_evento_fecha);
        seleccionarHora = findViewById(R.id.crear_evento_seleccionar_hora);
        seleccionarHora.setOnClickListener(this);
        mostrarHora = findViewById(R.id.crear_evento_hora);
        ubicacionEvento = findViewById(R.id.crear_evento_boton_ubicacion);
        ubicacionEvento.setOnClickListener(this);
        municipioEvento = findViewById(R.id.crear_evento__boton_municipio);
        municipioEvento.setOnClickListener(this);
        seleccionarProvincia = findViewById(R.id.crear_evento_seleccionar_provincia);
        seleccionarProvincia.setOnClickListener(this);
        provincia = findViewById(R.id.crear_evento_provincia);
        seleccionarMunicipio = findViewById(R.id.crear_evento_seleccionar_municipio);
        seleccionarMunicipio.setOnClickListener(this);
        municipio = findViewById(R.id.crear_evento_municipio);
        seleccionarUbicacion = findViewById(R.id.crear_evento_ubicacion_layout);
        ubicacion = findViewById(R.id.crear_evento_ubicacion);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.crear_evento_deporte:
                seleccionarDeporte();
                break;
            case R.id.crear_evento_tooltip_participantes:
                Tooltip.Builder tooltipParticipantes = new Tooltip.Builder(v, R.style.Tooltip2)
                        .setCancelable(true)
                        .setDismissOnClick(true)
                        .setCornerRadius(20f)
                        .setGravity(Gravity.TOP)
                        .setText("Indique 0 para una cantidad ilimitada de participantes");
                tooltipParticipantes.show();
                break;
            case R.id.crear_evento_tooltip_privacidad_foro:
                Tooltip.Builder tooltipForo = new Tooltip.Builder(v, R.style.Tooltip2)
                        .setCancelable(true)
                        .setDismissOnClick(true)
                        .setCornerRadius(20f)
                        .setGravity(Gravity.TOP)
                        .setText("Seleccione privado para que solamente los usuarios registrados al evento puedan visualizar y participar en el foro. Si selecciona público, cualquier usuario registrado podrá visualizar y participar en el foro.");
                tooltipForo.show();
                break;
            case R.id.crear_evento_seleccionar_fecha:
                showDatePickerDialog();
                break;
            case R.id.crear_evento_seleccionar_hora:
                showTimePickerDialog();
                break;
            case R.id.crear_evento__boton_municipio:
                activarBoton(v.getId());
                desactivarBoton(R.id.crear_evento_boton_ubicacion);
                findViewById(R.id.crear_evento_seleccionar_ubicacion).setVisibility(View.VISIBLE);
                seleccionarUbicacion.setVisibility(View.GONE);
                seleccionarProvincia.setVisibility(View.VISIBLE);
                seleccionarMunicipio.setVisibility(View.VISIBLE);
                esUbicacionGPS = false;
                esMunicipio = true;
                if (emptyUbicacion()) {
                    paisActual = Global.DEFAULT_COUNTRY;
                    provinciaActual = Global.DEFAULT_PROVINCE;
                    municipioActual = Global.DEFAULT_LOCALITY;
                    obtenerUbicacion();

                }
                ubicacion.setText("");
                break;
            case R.id.crear_evento_boton_ubicacion:
                activarBoton(v.getId());
                desactivarBoton(R.id.crear_evento__boton_municipio);
                findViewById(R.id.crear_evento_seleccionar_ubicacion).setVisibility(View.VISIBLE);
                seleccionarUbicacion.setVisibility(View.VISIBLE);
                seleccionarProvincia.setVisibility(View.GONE);
                seleccionarMunicipio.setVisibility(View.GONE);
                if (!emptyUbicacion()) resetUbicacion();
                esUbicacionGPS = true;
                esMunicipio = false;
                showPlacePicker();
                break;
            case R.id.crear_evento_seleccionar_provincia:
                seleccionarProvincia();
                break;
            case R.id.crear_evento_seleccionar_municipio:
                seleccionarMunicipio();
                break;
        }

    }

    private void showPlacePicker() {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), Global.REQUEST_CODE_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Global.REQUEST_CODE_PLACE_PICKER) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                if (place != null) {
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    try {
                        List<Address> list = geocoder.getFromLocation(
                                place.getLatLng().latitude, place.getLatLng().longitude, 1);
                        if (!list.isEmpty()) {
                            Address datosUbicacion = list.get(0);
                            direccion = place.getAddress().toString();
                            latitud = place.getLatLng().latitude;
                            longitud = place.getLatLng().longitude;
                            municipioDireccion = datosUbicacion.getLocality();
                            ubicacion.setText(direccion);
                        }
                        else {
                            Snackbar.make(findViewById(android.R.id.content), "No se ha podido registrar la ubicación", Snackbar.LENGTH_SHORT).show();
                            esUbicacionGPS = false;
                        }
                    } catch (IOException e) {
                        Snackbar.make(findViewById(android.R.id.content), "No se ha podido registrar la ubicación", Snackbar.LENGTH_SHORT).show();
                        esUbicacionGPS = false;
                        e.printStackTrace();
                    }
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "No se ha registrado ninguna ubicación", Snackbar.LENGTH_SHORT).show();
                    esUbicacionGPS = false;
                }


            }
        }
    }

    private void seleccionarMunicipio() {
        SeleccionarMunicipioDialog seleccionarMunicipio = new SeleccionarMunicipioDialog(municipios, municipioActual);
        seleccionarMunicipio.show(CrearEvento.this.getFragmentManager(), "seleccionarMunicio");
    }

    private void seleccionarProvincia() {
        SeleccionarProvinciasDialog seleccionarProvincias = new SeleccionarProvinciasDialog(provincias, provinciaActual);
        seleccionarProvincias.show(CrearEvento.this.getFragmentManager(), "seleccionarProvincias");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void provinciaSeleccionada(int provinciaSeleccionada) {
        // Comparar provincia actual con la seleccionada
        if (provinciaActual != provinciaSeleccionada) {
            provinciaActual = provinciaSeleccionada;
            provincia.setText(provincias.get(provinciaActual).getProvincia());
            municipioActual = 0;
            obtenerMunicipios();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void municipioSeleccionado(int municipioSeleccionado) {
        // Comparar provincia actual con la seleccionada
        if (municipioActual != municipioSeleccionado) {
            municipioActual = municipioSeleccionado;
            municipio.setText(municipios.get(municipioActual).getMunicipio());
            obtenerMunicipios();
        }
    }

    private void obtenerUbicacion() {
        Call<List<Pais>> peticionRestPaises = apiRest.paises();

        peticionRestPaises.enqueue(new Callback<List<Pais>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<List<Pais>> call, Response<List<Pais>> response) {
                if (response.raw().code() != 500 && response.isSuccessful()) {
                    paises = response.body();
                    paisActual = Global.DEFAULT_COUNTRY;
                    obtenerProvincias();

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
            public void onFailure(Call<List<Pais>> call, Throwable t) {
                Log.i("ERROR:", t.getMessage());
            }
        });
    }

    public void obtenerProvincias() {

        Call<List<Provincia>> peticionRestProvincias = apiRest.provincias(paises.get(paisActual).getId());

        peticionRestProvincias.enqueue(new Callback<List<Provincia>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<List<Provincia>> call, Response<List<Provincia>> response) {
                if (response.raw().code() != 500 && response.isSuccessful()) {
                    provincias = response.body();
                    provincia.setText(provincias.get(provinciaActual).getProvincia());
                    obtenerMunicipios();
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
            public void onFailure(Call<List<Provincia>> call, Throwable t) {
                Log.i("ERROR:", t.getMessage());
            }
        });
    }

    public void obtenerMunicipios() {

        Call<List<Municipio>> peticionRestMunicipios = apiRest.municipios(provincias.get(provinciaActual).getId());

        peticionRestMunicipios.enqueue(new Callback<List<Municipio>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<List<Municipio>> call, Response<List<Municipio>> response) {
                if (response.raw().code() != 500 && response.isSuccessful()) {
                    municipios = response.body();
                    municipio.setText(municipios.get(municipioActual).getMunicipio());
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
            public void onFailure(Call<List<Municipio>> call, Throwable t) {
                Log.i("ERROR:", t.getMessage());
            }
        });
    }


    private Boolean emptyUbicacion() {
        return paisActual == -1 && provinciaActual == -1 && municipioActual == -1;
    }

    private void resetUbicacion() {
        paisActual = -1;
        provinciaActual = -1;
        municipioActual = -1;
    }

    private void desactivarBoton(int id) {
        Button boton = findViewById(id);
        boton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.negro));
        boton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.border_button));
        if (id == R.id.crear_evento_boton_ubicacion) {
            boton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_cerca_de_mi_no_select), null, null);
        } else if (id == R.id.crear_evento__boton_municipio) {
            boton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_ubicacion_no_select), null, null);
        }

    }

    private void activarBoton(int id) {
        Button boton = findViewById(id);
        boton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blanco));
        boton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.border_button_select));
        if (id == R.id.crear_evento_boton_ubicacion) {
            boton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_cerca_de_mi_select), null, null);
        } else if (id == R.id.crear_evento__boton_municipio) {
            boton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_ubicacion_select), null, null);
        }
    }

    private void showTimePickerDialog() {

        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int horaActual = c.get(Calendar.HOUR_OF_DAY);
        int minutoActual = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        hora = hourOfDay;
                        minutos = minute;
                        mostrarHora.setText(hora + ":" + minutos + " h");
                    }
                }, horaActual, minutoActual, true);
        timePickerDialog.show();
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                // +1 porque enero es 0
                year = y;
                month = m;
                day = d;
                final String selectedDate = dosDigitos(day) + " / " + dosDigitos(month + 1) + " / " + year;
                mostrarFecha.setText(selectedDate);
            }
        });
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private String dosDigitos(int n) {
        return (n <= 9) ? ("0" + n) : String.valueOf(n);
    }

    private void seleccionarDeporte() {
        SeleccionarDeporteEventoDialog seleccionarDeporte = new SeleccionarDeporteEventoDialog(deportes, deporteSeleccionado);
        seleccionarDeporte.show(CrearEvento.this.getFragmentManager(), "seleccionarDeporte");
    }


    @Override
    public void deporteSeleccionado(int deporteSeleccionado) {
        this.deporteSeleccionado = deporteSeleccionado;
        if (this.deporteSeleccionado >= 0) {
            deporteEventoText.setText(deportes.get(this.deporteSeleccionado).getDeporte());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.toolbar_crear_evento) {
            // Crear foro al crear evento MIRAR TABFOROEVENTO
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_crear_evento, menu);
        return true;
    }

    private void obtenerDeportes() {
        Call<List<Deporte>> peticionRestDeportes = apiRest.getDeportes();

        peticionRestDeportes.enqueue(new Callback<List<Deporte>>() {
            @Override
            public void onResponse(Call<List<Deporte>> call, Response<List<Deporte>> response) {
                if (response.raw().code() != 500 && response.isSuccessful()) {
                    deportes = response.body();
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
            public void onFailure(Call<List<Deporte>> call, Throwable t) {
                Log.i("ERROR:", t.getMessage());
            }
        });
    }

    // Función que define comportamiento del botón "Atras"
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }


}
