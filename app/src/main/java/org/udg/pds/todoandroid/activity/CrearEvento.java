package org.udg.pds.todoandroid.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
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


import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.tooltip.Tooltip;

import org.json.JSONObject;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Deporte;
import org.udg.pds.todoandroid.entity.EventoCrearPeticion;
import org.udg.pds.todoandroid.entity.GenericId;
import org.udg.pds.todoandroid.entity.Imagen;
import org.udg.pds.todoandroid.entity.Municipio;
import org.udg.pds.todoandroid.entity.Pais;
import org.udg.pds.todoandroid.entity.Provincia;
import org.udg.pds.todoandroid.entity.Ubicacion;
import org.udg.pds.todoandroid.fragment.DatePickerFragment;
import org.udg.pds.todoandroid.fragment.SeleccionarDeporteEventoDialog;
import org.udg.pds.todoandroid.fragment.SeleccionarMunicipioDialog;
import org.udg.pds.todoandroid.fragment.SeleccionarProvinciasDialog;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.CustomTextWatcher;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.ImageUtil;
import org.udg.pds.todoandroid.util.InitRetrofit;
import org.udg.pds.todoandroid.util.InputFilterMinMax;
import org.udg.pds.todoandroid.util.SnackbarUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearEvento extends AppCompatActivity implements View.OnClickListener, SeleccionarDeporteEventoDialog.SeleccionarDeporteEventoDialogListener, SeleccionarProvinciasDialog.SeleccionarProvinciasDialogListener, SeleccionarMunicipioDialog.SeleccionarMunicipioDialogListener {

    private ApiRest apiRest;
    // Categoría deportiva
    private List<Deporte> deportes = new ArrayList<>();
    private int deporteSeleccionado = -1;
    // Fecha evento
    private int year = -1;
    private int month = -1;
    private int day = -1;
    // Hora evento
    private int hora = -1;
    private int minutos = -1;
    // Ubicación Evento escojiendo provincia y municipio
    private List<Pais> paises = new ArrayList<>();
    private int paisActual = -1;
    private List<Provincia> provincias = new ArrayList<>();
    private int provinciaActual = -1;
    private List<Municipio> municipios = new ArrayList<>();
    private int municipioActual = -1;
    private boolean esMunicipio = false;
    // Ubicación evento seleccionando en el mapa de google
    private double latitud;
    private double longitud;
    private String direccion;
    private String municipioDireccion;
    private boolean esUbicacionGPS = false;
    // Campos formulario
    private TextInputLayout tilTitulo;
    private TextInputLayout tilDescripcion;
    private TextInputLayout tilDeporte;
    private TextInputLayout tilParticipantes;
    private TextInputLayout tilDuracion;
    private TextInputLayout tilFecha;
    private TextInputLayout tilHora;
    private TextInputLayout tilProvincia;
    private TextInputLayout tilMunicipio;
    private EditText etTitulo;
    private EditText etDescripcion;
    private EditText etDeporte;
    private EditText etParticipantes;
    private EditText etDuracion;
    private EditText etFecha;
    private EditText etHora;
    private EditText etProvincia;
    private EditText etMunicipio;
    // Privacidad Foro
    private Switch privacidadForo;
    // Ubicación evento
    private ConstraintLayout seleccionarUbicacion;
    private TextView ubicacion;
    // Imagen evento
    private ImageView imagenEvento;
    private Uri pathImagenPerfil;
    private boolean esNuevaImagen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_evento_layout);
        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();
        //Obtenemos las categorías deportivas disponibles
        obtenerDeportes();
        // Ponemos el toolbar
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        // Imagen evento por defecto
        imagenEvento = findViewById(R.id.crear_evento_imagen_evento);
        Picasso.with(getApplicationContext()).load(Global.BASE_URL + Global.DEFAULT_IMAGE_EVENT).into(imagenEvento);
        imagenEvento.setOnClickListener(this);
        // Ocultar layout ubicación
        findViewById(R.id.crear_evento_seleccionar_ubicacion).setVisibility(View.GONE);
        // Obtener campos formulario
        tilTitulo = findViewById(R.id.crear_evento_til_titulo);
        tilDescripcion = findViewById(R.id.crear_evento_til_descripcion);
        tilDeporte = findViewById(R.id.crear_evento_til_deporte);
        tilParticipantes = findViewById(R.id.crear_evento_til_participantes);
        tilDuracion = findViewById(R.id.crear_evento_til_duracion);
        tilFecha = findViewById(R.id.crear_evento_til_dia);
        tilHora = findViewById(R.id.crear_evento_til_hora);
        tilProvincia = findViewById(R.id.crear_evento_til_provincias);
        tilMunicipio = findViewById(R.id.crear_evento_til_municipio);

        etTitulo = findViewById(R.id.crear_evento_et_titulo);
        etDescripcion = findViewById(R.id.crear_evento_et_descripcion);
        etDeporte = findViewById(R.id.crear_evento_et_deporte);
        etParticipantes = findViewById(R.id.crear_evento_et_participantes);
        etParticipantes.setFilters(new InputFilter[]{new InputFilterMinMax("0", "9999")});
        etDuracion = findViewById(R.id.crear_evento_et_duracion);
        etDuracion.setFilters(new InputFilter[]{new InputFilterMinMax("0", "9999")});
        etDeporte.setOnClickListener(this);
        etFecha = findViewById(R.id.crear_evento_et_dia);
        etFecha.setOnClickListener(this);
        etHora = findViewById(R.id.crear_evento_et_hora);
        etHora.setOnClickListener(this);
        etProvincia = findViewById(R.id.crear_evento_et_provincias);
        etProvincia.setOnClickListener(this);
        etMunicipio = findViewById(R.id.crear_evento_et_municipio);
        etMunicipio.setOnClickListener(this);

        // tooltips
        ImageView tooltipParticipantes = findViewById(R.id.crear_evento_tooltip_participantes);
        tooltipParticipantes.setOnClickListener(this);
        ImageView tooltipPrivacidadForo = findViewById(R.id.crear_evento_tooltip_privacidad_foro);
        tooltipPrivacidadForo.setOnClickListener(this);
        // Listeners para los campos del formulario al setear el error
        addTextListeners();
        // Privacidad foro
        privacidadForo = findViewById(R.id.crear_evento_privacidad_foro);
        privacidadForo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (privacidadForo.isChecked())
                    privacidadForo.setText(getString(R.string.crear_evento_foro_publico));
                else privacidadForo.setText(getString(R.string.crear_evento_foro_Privado));
            }
        });
        // Ubicación evento
        Button ubicacionEvento = findViewById(R.id.crear_evento_boton_ubicacion);
        ubicacionEvento.setOnClickListener(this);
        Button municipioEvento = findViewById(R.id.crear_evento__boton_municipio);
        municipioEvento.setOnClickListener(this);
        seleccionarUbicacion = findViewById(R.id.crear_evento_ubicacion_layout);
        ubicacion = findViewById(R.id.crear_evento_ubicacion);

    }

    // Eliminamos el mensaje de error al introducir texto en un campo vacio
    private void addTextListeners() {
        etTitulo.addTextChangedListener(new CustomTextWatcher(tilTitulo));
        etDescripcion.addTextChangedListener(new CustomTextWatcher(tilDescripcion));
        etDeporte.addTextChangedListener(new CustomTextWatcher(tilDeporte));
        etParticipantes.addTextChangedListener(new CustomTextWatcher(tilParticipantes));
        etDuracion.addTextChangedListener(new CustomTextWatcher(tilDuracion));
        etFecha.addTextChangedListener(new CustomTextWatcher(tilFecha));
        etHora.addTextChangedListener(new CustomTextWatcher(tilHora));

    }

    // Gestión de los eventos 'click' en diferentes partes del formulario
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.crear_evento_et_deporte:
                seleccionarDeporte();
                break;
            case R.id.crear_evento_tooltip_participantes:
                Tooltip.Builder tooltipParticipantes = new Tooltip.Builder(v, R.style.Tooltip2)
                        .setCancelable(true)
                        .setDismissOnClick(true)
                        .setCornerRadius(20f)
                        .setGravity(Gravity.TOP)
                        .setText(getString(R.string.crear_evento_tooltip_participantes));
                tooltipParticipantes.show();
                break;
            case R.id.crear_evento_tooltip_privacidad_foro:
                Tooltip.Builder tooltipForo = new Tooltip.Builder(v, R.style.Tooltip2)
                        .setCancelable(true)
                        .setDismissOnClick(true)
                        .setCornerRadius(20f)
                        .setGravity(Gravity.TOP)
                        .setText(getString(R.string.crear_evento_tooltip_privacidad_foro));
                tooltipForo.show();
                break;
            case R.id.crear_evento_et_dia:
                showDatePickerDialog();
                break;
            case R.id.crear_evento_et_hora:
                showTimePickerDialog();
                break;
            case R.id.crear_evento__boton_municipio:
                activarBoton(v.getId());
                desactivarBoton(R.id.crear_evento_boton_ubicacion);
                findViewById(R.id.crear_evento_seleccionar_ubicacion).setVisibility(View.VISIBLE);
                seleccionarUbicacion.setVisibility(View.GONE);
                tilProvincia.setVisibility(View.VISIBLE);
                tilMunicipio.setVisibility(View.VISIBLE);
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
                tilProvincia.setVisibility(View.GONE);
                tilMunicipio.setVisibility(View.GONE);
                if (!emptyUbicacion()) resetUbicacion();
                esUbicacionGPS = true;
                esMunicipio = false;
                showPlacePicker();
                break;
            case R.id.crear_evento_et_provincias:
                seleccionarProvincia();
                break;
            case R.id.crear_evento_et_municipio:
                seleccionarMunicipio();
                break;
            case R.id.crear_evento_imagen_evento:
                cargarImagenEvento();
                break;
        }
    }

    // Comportamiento del botón crear evento
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.toolbar_crear_evento) {
            // Validar datos del formulario
            if (validarFormulario()) {
                String fechaEvento = dosDigitos(day) + "/" + dosDigitos(month + 1) + "/" + year + " " + dosDigitos(hora) + ":" + dosDigitos(minutos);
                EventoCrearPeticion datosEvento;
                // creamos el evento con la ubicación seleccionada a través de una provincia y municipio
                if (!esMunicipio) {
                    datosEvento = new EventoCrearPeticion(etTitulo.getText().toString(), etDescripcion.getText().toString(), Integer.parseInt(etDuracion.getText().toString()),
                            Integer.parseInt(etParticipantes.getText().toString()), fechaEvento, privacidadForo.isChecked(), null, deportes.get(deporteSeleccionado).getId(), new Ubicacion(latitud, longitud, direccion, municipioDireccion), Global.DEFAULT_FORO_NAME);
                }
                // Creamos el evento con la ubicación seleccionada en el mapa
                else {
                    datosEvento = new EventoCrearPeticion(etTitulo.getText().toString(), etDescripcion.getText().toString(), Integer.parseInt(etDuracion.getText().toString()),
                            Integer.parseInt(etParticipantes.getText().toString()), fechaEvento, privacidadForo.isChecked(), municipios.get(municipioActual).getId(), deportes.get(deporteSeleccionado).getId(), null, Global.DEFAULT_FORO_NAME);
                }

                Call<GenericId> peticionRest = apiRest.crearEvento(datosEvento);
                peticionRest.enqueue(new Callback<GenericId>() {
                    @Override
                    public void onResponse(Call<GenericId> call, Response<GenericId> response) {
                        if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                            GenericId idEvento = response.body();
                            // Creamos el foro en la base de datos de FireBase
                            if (idEvento != null) {
                                DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
                                Map<String, Object> map = new HashMap<>();
                                map.put(Global.PREFIJO_SALA_FORO_EVENTO + idEvento.getId().toString(), "");
                                root.updateChildren(map);
                            }
                            // Si se ha cargado una imagen para el evento
                            if (esNuevaImagen) {
                                try {
                                    File imageFile = ImageUtil.decodeImage(getRealPathFromURIPath(pathImagenPerfil, CrearEvento.this), getApplicationContext());
                                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), Objects.requireNonNull(imageFile));
                                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), reqFile);
                                    Call<Imagen> peticionRest = apiRest.subirImagenEvento(body, Objects.requireNonNull(idEvento).getId());
                                    peticionRest.enqueue(new Callback<Imagen>() {
                                        @Override
                                        public void onResponse(Call<Imagen> call, Response<Imagen> response) {
                                            if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                                                Intent principal = new Intent(getApplicationContext(), Principal.class);
                                                // Eliminamos de la pila todas las actividades
                                                principal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(principal);
                                                finish();
                                            } else {
                                                try {
                                                    JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                                                    SnackbarUtil.showSnackBar(findViewById(R.id.login_snackbar), jObjError.getString(getString(R.string.error_server_message)), Snackbar.LENGTH_LONG, true);
                                                } catch (Exception e) {
                                                    Log.e(getString(R.string.log_error), e.getMessage());
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Imagen> call, Throwable t) {
                                            Log.e(getString(R.string.log_error), t.getMessage());
                                        }
                                    });
                                } catch (Exception e) {
                                    Log.e(getString(R.string.log_error), e.getMessage());
                                }
                            } else {
                                Intent principal = new Intent(getApplicationContext(), Principal.class);
                                // Eliminamos de la pila todas las actividades
                                principal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(principal);
                            }
                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                                SnackbarUtil.showSnackBar(findViewById(R.id.login_snackbar), jObjError.getString(getString(R.string.error_server_message)), Snackbar.LENGTH_LONG, true);
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
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean validarFormulario() {

        boolean esCorrecto = true;

        String titulo = Objects.requireNonNull(tilTitulo.getEditText()).getText().toString();
        String descripcion = Objects.requireNonNull(tilDescripcion.getEditText()).getText().toString();
        String deporte = Objects.requireNonNull(tilDeporte.getEditText()).getText().toString();
        String participantes = Objects.requireNonNull(tilParticipantes.getEditText()).getText().toString();
        String duracion = Objects.requireNonNull(tilDuracion.getEditText()).getText().toString();
        String fecha = Objects.requireNonNull(tilFecha.getEditText()).getText().toString();
        String hora = Objects.requireNonNull(tilHora.getEditText()).getText().toString();

        // Validar título no vacio
        if (titulo.isEmpty()) {
            esCorrecto = false;
            tilTitulo.setError(getString(R.string.crear_evento_titulo_obligatorio));
        } else tilTitulo.setError(null);

        // Validar descripción no vacía
        if (descripcion.isEmpty()) {
            esCorrecto = false;
            tilDescripcion.setError(getString(R.string.crear_evento_descripcion_obligatoria));
        } else tilDescripcion.setError(null);

        // Validar categoría deportiva no vacía
        if (deporte.isEmpty()) {
            esCorrecto = false;
            tilDeporte.setError(getString(R.string.crear_evento_deporte_obligatorio));
        } else tilDeporte.setError(null);

        // participantes
        if (participantes.isEmpty()) {
            esCorrecto = false;
            tilParticipantes.setError(getString(R.string.crear_evento_participantes_obligatorio));
        } else tilParticipantes.setError(null);

        // duracion
        if (duracion.isEmpty()) {
            esCorrecto = false;
            tilDuracion.setError(getString(R.string.crear_evento_duracion_obligatoria));
        } else tilDuracion.setError(null);

        // fecha
        if (fecha.isEmpty()) {
            esCorrecto = false;
            tilFecha.setError(getString(R.string.crear_evento_fecha_obligatoria));
        } else tilFecha.setError(null);

        // fecha
        if (hora.isEmpty()) {
            esCorrecto = false;
            tilHora.setError(getString(R.string.crear_evento_hora_obligatoria));
        } else tilHora.setError(null);

        if ((esUbicacionGPS && (ubicacion == null || ubicacion.getText().toString().isEmpty())) || (!esUbicacionGPS && !esMunicipio && (ubicacion == null || ubicacion.getText().toString().isEmpty()))) {
            if (esCorrecto) {
                SnackbarUtil.showSnackBar(findViewById(R.id.login_snackbar), "Introduzca la ubicación del evento", Snackbar.LENGTH_LONG, true);
            }
            esCorrecto = false;
        }

        return esCorrecto;

    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        @SuppressLint("Recycle") Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_crear_evento, menu);
        return true;
    }


    private void cargarImagenEvento() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle(getString(R.string.titulo_seleccionar_foto));
        String[] pictureDialogItems = {
                getString(R.string.titulo_seleccionar_foto_galeria),
                getString(R.string.titulo_seleccionar_foto_camara)};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        // Foto de la galería del dispositivo
        if (requestCode == Global.REQUEST_CODE_GALLERY) {
            if (data != null) {
                pathImagenPerfil = data.getData();
                try {
                    // Interfície para recortar la imagen
                    CropImage.activity(pathImagenPerfil)
                            .setAspectRatio(10, 10)
                            .start(this);
                } catch (Exception e) {
                    Picasso.with(getApplicationContext()).load(Global.BASE_URL + Global.DEFAULT_IMAGE_EVENT).into(imagenEvento);
                    esNuevaImagen = false;
                    Log.e(getString(R.string.log_error), e.getMessage());
                    SnackbarUtil.showSnackBar(findViewById(R.id.login_snackbar), getString(R.string.imagen_cargada_error), Snackbar.LENGTH_LONG, true);
                }
            }

        }
        // Foto de la cámara del dispositivo
        else if (requestCode == Global.REQUEST_CODE_CAMERA) {
            try {
                pathImagenPerfil = data.getData();
                // Interfície para recortar la imagen
                CropImage.activity(pathImagenPerfil)
                        .setAspectRatio(10, 10)
                        .start(this);

            } catch (Exception e) {
                esNuevaImagen = false;
                Picasso.with(getApplicationContext()).load(Global.BASE_URL + Global.DEFAULT_IMAGE_EVENT).into(imagenEvento);
                SnackbarUtil.showSnackBar(findViewById(R.id.login_snackbar), getString(R.string.imagen_cargada_error), Snackbar.LENGTH_LONG, true);
                Log.e(getString(R.string.log_error), e.getMessage());
            }
        }
        // Ubicación del mapa seleccionada
        else if (requestCode == Global.REQUEST_CODE_PLACE_PICKER) {
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
                        } else {
                            SnackbarUtil.showSnackBar(findViewById(R.id.login_snackbar), getString(R.string.ubicacion_registrada_error), Snackbar.LENGTH_LONG, true);
                            esUbicacionGPS = false;
                        }
                    } catch (IOException e) {
                        SnackbarUtil.showSnackBar(findViewById(R.id.login_snackbar), getString(R.string.ubicacion_registrada_error), Snackbar.LENGTH_LONG, true);
                        Log.e(getString(R.string.log_error), e.getMessage());
                        esUbicacionGPS = false;
                    }
                } else {
                    SnackbarUtil.showSnackBar(findViewById(R.id.login_snackbar), getString(R.string.ubicacion_registrada_error), Snackbar.LENGTH_LONG, true);
                    esUbicacionGPS = false;
                }
            }
        }
        // Imagen de galería o cámara recortada
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                pathImagenPerfil = result.getUri();
                Picasso.with(getApplicationContext()).load(pathImagenPerfil).into(imagenEvento);
                esNuevaImagen = true;
                SnackbarUtil.showSnackBar(findViewById(R.id.login_snackbar), getString(R.string.imagen_cargada_correctamente), Snackbar.LENGTH_LONG, false);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                esNuevaImagen = false;
                Picasso.with(getApplicationContext()).load(Global.BASE_URL + Global.DEFAULT_IMAGE_EVENT).into(imagenEvento);
                SnackbarUtil.showSnackBar(findViewById(R.id.login_snackbar), getString(R.string.imagen_cargada_error), Snackbar.LENGTH_LONG, true);
            }
        }
    }

    // Pedir permisos al usuario para utilizar la cámara del dispositivo o acceder a las imagenes de la galería
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Global.REQUEST_CODE_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, Global.REQUEST_CODE_CAMERA);
            }
        } else if (requestCode == Global.REQUEST_CODE_GALLERY) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, Global.REQUEST_CODE_GALLERY);
            }
        }
    }

    private void takePhotoFromCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,}, Global.REQUEST_CODE_CAMERA);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,}, Global.REQUEST_CODE_CAMERA);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, Global.REQUEST_CODE_CAMERA);
        }

    }

    private void choosePhotoFromGallary() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,}, Global.REQUEST_CODE_GALLERY);
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(galleryIntent, Global.REQUEST_CODE_GALLERY);
        }

    }

    private void showPlacePicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), Global.REQUEST_CODE_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.e(getString(R.string.log_error), e.getMessage());
        }
    }

    private void seleccionarMunicipio() {
        SeleccionarMunicipioDialog seleccionarMunicipio = new SeleccionarMunicipioDialog(municipios, municipioActual);
        seleccionarMunicipio.show(CrearEvento.this.getFragmentManager(), Global.TAG_MUNICIPIOS_DIALOG);
    }

    private void seleccionarProvincia() {
        SeleccionarProvinciasDialog seleccionarProvincias = new SeleccionarProvinciasDialog(provincias, provinciaActual);
        seleccionarProvincias.show(CrearEvento.this.getFragmentManager(), Global.TAG_PROVINCIAS_DIALOG);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void provinciaSeleccionada(int provinciaSeleccionada) {
        // Comparar provincia actual con la seleccionada
        if (provinciaActual != provinciaSeleccionada) {
            provinciaActual = provinciaSeleccionada;
            etProvincia.setText(provincias.get(provinciaActual).getProvincia());
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
            etMunicipio.setText(municipios.get(municipioActual).getMunicipio());
            obtenerMunicipios();
        }
    }

    private void obtenerUbicacion() {
        Call<List<Pais>> peticionRestPaises = apiRest.paises();
        peticionRestPaises.enqueue(new Callback<List<Pais>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<List<Pais>> call, Response<List<Pais>> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    paises = response.body();
                    paisActual = Global.DEFAULT_COUNTRY;
                    obtenerProvincias();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                        SnackbarUtil.showSnackBar(findViewById(R.id.login_snackbar), jObjError.getString(getString(R.string.error_server_message)), Snackbar.LENGTH_LONG, true);
                    } catch (Exception e) {
                        Log.e(getString(R.string.log_error), e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Pais>> call, Throwable t) {
                Log.e(getString(R.string.log_error), t.getMessage());
            }
        });
    }

    public void obtenerProvincias() {

        Call<List<Provincia>> peticionRestProvincias = apiRest.provincias(paises.get(paisActual).getId());

        peticionRestProvincias.enqueue(new Callback<List<Provincia>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<List<Provincia>> call, Response<List<Provincia>> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    provincias = response.body();
                    if (provincias != null && !provincias.isEmpty())
                        etProvincia.setText(provincias.get(provinciaActual).getProvincia());
                    obtenerMunicipios();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                        SnackbarUtil.showSnackBar(findViewById(R.id.login_snackbar), jObjError.getString(getString(R.string.error_server_message)), Snackbar.LENGTH_LONG, true);
                    } catch (Exception e) {
                        Log.e(getString(R.string.log_error), e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Provincia>> call, Throwable t) {
                Log.e(getString(R.string.log_error), t.getMessage());
            }
        });
    }

    public void obtenerMunicipios() {

        Call<List<Municipio>> peticionRestMunicipios = apiRest.municipios(provincias.get(provinciaActual).getId());

        peticionRestMunicipios.enqueue(new Callback<List<Municipio>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<List<Municipio>> call, Response<List<Municipio>> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    municipios = response.body();
                    if (municipios != null && !municipios.isEmpty())
                        etMunicipio.setText(municipios.get(municipioActual).getMunicipio());
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                        SnackbarUtil.showSnackBar(findViewById(R.id.login_snackbar), jObjError.getString(getString(R.string.error_server_message)), Snackbar.LENGTH_LONG, true);
                    } catch (Exception e) {
                        Log.e(getString(R.string.log_error), e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Municipio>> call, Throwable t) {
                Log.e(getString(R.string.log_error), t.getMessage());
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
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        hora = hourOfDay;
                        minutos = minute;
                        String h = dosDigitos(hora) + ":" + dosDigitos(minutos) + " h";
                        etHora.setText(h);
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
                etFecha.setText(selectedDate);
            }
        });
        newFragment.show(getFragmentManager(), Global.TAG_DATE_PICKER);
    }

    private String dosDigitos(int n) {
        return (n <= 9) ? ("0" + n) : String.valueOf(n);
    }

    private void seleccionarDeporte() {
        SeleccionarDeporteEventoDialog seleccionarDeporte = new SeleccionarDeporteEventoDialog(deportes, deporteSeleccionado);
        seleccionarDeporte.show(CrearEvento.this.getFragmentManager(), Global.TAG_DEPORTES_DIALOG);
    }


    @Override
    public void deporteSeleccionado(int deporteSeleccionado) {
        this.deporteSeleccionado = deporteSeleccionado;
        if (this.deporteSeleccionado >= 0) {
            etDeporte.setText(deportes.get(this.deporteSeleccionado).getDeporte());
        } else etDeporte.getText().clear();
    }


    private void obtenerDeportes() {
        Call<List<Deporte>> peticionRestDeportes = apiRest.getDeportes();

        peticionRestDeportes.enqueue(new Callback<List<Deporte>>() {
            @Override
            public void onResponse(Call<List<Deporte>> call, Response<List<Deporte>> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    deportes = response.body();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                        SnackbarUtil.showSnackBar(findViewById(R.id.login_snackbar), jObjError.getString(getString(R.string.error_server_message)), Snackbar.LENGTH_LONG, true);
                    } catch (Exception e) {
                        Log.e(getString(R.string.log_error), e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Deporte>> call, Throwable t) {
                Log.e(getString(R.string.log_error), t.getMessage());
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
