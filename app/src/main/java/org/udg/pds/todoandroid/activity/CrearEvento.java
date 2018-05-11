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
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
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
import org.udg.pds.todoandroid.entity.UsuarioActual;
import org.udg.pds.todoandroid.entity.UsuarioRegistroPeticion;
import org.udg.pds.todoandroid.entity.UsuarioRegistroRespuesta;
import org.udg.pds.todoandroid.fragment.DatePickerFragment;
import org.udg.pds.todoandroid.fragment.SeleccionarDeporteEventoDialog;
import org.udg.pds.todoandroid.fragment.SeleccionarMunicipioDialog;
import org.udg.pds.todoandroid.fragment.SeleccionarProvinciasDialog;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;
import org.udg.pds.todoandroid.util.InputFilterMinMax;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

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
    private double latitud;
    private double longitud;
    private String direccion;
    private String municipioDireccion;
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
    private ImageView imagenEvento;
    private FloatingActionButton tomarImagenEvento;
    private Uri pathImagenPerfil;
    private boolean esNuevaImagen = false;

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
        imagenEvento = findViewById(R.id.crear_evento_imagen_evento);
        Picasso.with(getApplicationContext()).load(Global.BASE_URL + "imagen/evento/0").fit().centerCrop().into(imagenEvento);
        tomarImagenEvento = findViewById(R.id.crear_evento_tomar_imagen_evento);
        tomarImagenEvento.setOnClickListener(this);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.toolbar_crear_evento) {
            // VALIDAR FORMULARIO
            if (validarFormulario()) {
                String fechaEvento = dosDigitos(day) + "/" + dosDigitos(month + 1) + "/" + year + " " + dosDigitos(hora) + ":" + dosDigitos(minutos);
                EventoCrearPeticion datosEvento = new EventoCrearPeticion();
                if (!esMunicipio) {
                    datosEvento = new EventoCrearPeticion(tituloEvento.getText().toString(), descripcionEvento.getText().toString(), Integer.parseInt(duracionEvento.getText().toString()),
                            Integer.parseInt(participantesEvento.getText().toString()), fechaEvento, privacidadForo.isChecked(), null, deportes.get(deporteSeleccionado).getId(), new Ubicacion(latitud, longitud, direccion, municipioDireccion), "tituloForo");
                } else {
                    datosEvento = new EventoCrearPeticion(tituloEvento.getText().toString(), descripcionEvento.getText().toString(), Integer.parseInt(duracionEvento.getText().toString()),
                            Integer.parseInt(participantesEvento.getText().toString()), fechaEvento, privacidadForo.isChecked(), municipios.get(municipioActual).getId(), deportes.get(deporteSeleccionado).getId(), null, "tituloForo");
                }

                Call<GenericId> peticionRest = apiRest.crearEvento(datosEvento);
                peticionRest.enqueue(new Callback<GenericId>() {
                    @Override
                    public void onResponse(Call<GenericId> call, Response<GenericId> response) {
                        if (response.raw().code() != 500 && response.isSuccessful()) {

                            GenericId idEvento = response.body();

                            // Creamos el foro en la base de datos de FireBase
                            DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put(Global.PREFIJO_SALA_FORO_EVENTO + idEvento.getId().toString(), "");
                            root.updateChildren(map);


                            if (esNuevaImagen) {
                                try {
                                    File file = new File(getRealPathFromURIPath(pathImagenPerfil, CrearEvento.this));
                                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
                                    Call<Imagen> peticionRest = apiRest.subirImagenEvento(body, idEvento.getId());
                                    peticionRest.enqueue(new Callback<Imagen>() {
                                        @Override
                                        public void onResponse(Call<Imagen> call, Response<Imagen> response) {
                                            if (response.raw().code() != 500 && response.isSuccessful()) {
                                                Imagen imagen = response.body();
                                                Intent principal = new Intent(getApplicationContext(), Principal.class);
                                                // Eliminamos de la pila todas las actividades
                                                principal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(principal);
                                                finish();
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
                                        public void onFailure(Call<Imagen> call, Throwable t) {
                                            Log.i("ERROR:", t.getMessage());
                                        }
                                    });
                                } catch (Exception e) {
                                    System.out.println(e);
                                }
                            } else {
                                Intent principal = new Intent(getApplicationContext(), Principal.class);
                                // Eliminamos de la pila todas las actividades
                                principal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(principal);
                            }


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
                    public void onFailure(Call<GenericId> call, Throwable t) {
                        Log.i("ERROR:", t.getMessage());
                    }
                });
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean validarFormulario() {

        boolean esCorrecto = true;

        if (tituloEvento == null || tituloEvento.getText().toString().isEmpty()){
            tituloEvento.setError("Debe introducir un título para el evento");
            tituloEvento.requestFocus();
            esCorrecto = false;
        }

        if (descripcionEvento == null || descripcionEvento.getText().toString().isEmpty()){
            descripcionEvento.setError("Debe introducir una descripción para el evento");
            descripcionEvento.requestFocus();
            esCorrecto = false;
        }

        if (deporteSeleccionado < 0){
            deporteEvento.setError("Debe seleccionar una categoría deportiva");
            deporteEvento.requestFocus();
            esCorrecto = false;
        }

        if (  participantesEvento == null || participantesEvento.getText().toString().isEmpty() || Integer.parseInt(participantesEvento.getText().toString()) < 0 || Integer.parseInt(participantesEvento.getText().toString()) > 9999){
            participantesEvento.setError("Debe introducir el número de participantes");
            participantesEvento.requestFocus();
            esCorrecto = false;
        }

        if (  duracionEvento == null || duracionEvento.getText().toString().isEmpty() || Integer.parseInt(duracionEvento.getText().toString()) < 0 || Integer.parseInt(duracionEvento.getText().toString()) > 9999){
            duracionEvento.setError("Debe introducir la duración estimada del evento");
            duracionEvento.requestFocus();
            esCorrecto = false;
        }

        if (hora < 0 || minutos < 0 ){
            mostrarHora.setError("Debe seleccionar una hora");
            mostrarHora.requestFocus();
            esCorrecto = false;
        }

        if (year < 0 || month < 0 || day < 0){
            mostrarFecha.setError("Debe seleccionar una fecha");
            mostrarFecha.requestFocus();
            esCorrecto = false;
        }

        if ( (esUbicacionGPS && (ubicacion == null || ubicacion.getText().toString().isEmpty() ))  || (!esUbicacionGPS && !esMunicipio && (ubicacion == null || ubicacion.getText().toString().isEmpty() )) ){
            ubicacion.setError("Debe seleccionar una ubicación para el evento");
            ubicacion.requestFocus();
            esCorrecto = false;
        }


        return esCorrecto;

    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
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
            case R.id.crear_evento_tomar_imagen_evento:
                cargarImagenEvento();
                break;
        }

    }

    private void cargarImagenEvento() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Seleccionar opción:");
        String[] pictureDialogItems = {
                "Foto de la galería",
                "Foto de la cámara"};
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == Global.REQUEST_CODE_GALLERY) {
            if (data != null) {

                pathImagenPerfil = data.getData();
                try {
                    Picasso.with(getApplicationContext()).load(pathImagenPerfil).fit().centerCrop().into(imagenEvento);
                    esNuevaImagen = true;
                    Toast.makeText(CrearEvento.this, "Imagen guardada!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Picasso.with(getApplicationContext()).load(Global.BASE_URL + "imagen/evento/0").into(imagenEvento);
                    esNuevaImagen = false;
                    e.printStackTrace();
                    Toast.makeText(CrearEvento.this, "Error al guardar imagen!", Toast.LENGTH_SHORT).show();

                }
            }

        } else if (requestCode == Global.REQUEST_CODE_CAMERA) {
            try {

                pathImagenPerfil = data.getData();
                Picasso.with(getApplicationContext()).load(pathImagenPerfil).fit().centerCrop().into(imagenEvento);
                esNuevaImagen = true;
                Toast.makeText(CrearEvento.this, "Imagen guardada!", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                esNuevaImagen = false;
                e.printStackTrace();
                Picasso.with(getApplicationContext()).load(Global.BASE_URL + "imagen/evento/0").into(imagenEvento);
                Toast.makeText(CrearEvento.this, "Error al guardar imagen!", Toast.LENGTH_SHORT).show();


            }
        } else if (requestCode == Global.REQUEST_CODE_PLACE_PICKER) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
            e.printStackTrace();
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
                        mostrarHora.setText(dosDigitos(hora) + ":" + dosDigitos(minutos) + " h");
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
