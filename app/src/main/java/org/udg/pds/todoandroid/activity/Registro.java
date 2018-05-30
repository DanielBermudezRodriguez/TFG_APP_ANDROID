package org.udg.pds.todoandroid.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONObject;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Deporte;
import org.udg.pds.todoandroid.entity.Imagen;
import org.udg.pds.todoandroid.entity.Municipio;
import org.udg.pds.todoandroid.entity.Pais;
import org.udg.pds.todoandroid.entity.Provincia;
import org.udg.pds.todoandroid.entity.UsuarioActual;
import org.udg.pds.todoandroid.entity.UsuarioRegistroPeticion;
import org.udg.pds.todoandroid.entity.UsuarioRegistroRespuesta;
import org.udg.pds.todoandroid.fragment.SeleccionarDeporteDialog;
import org.udg.pds.todoandroid.fragment.SeleccionarMunicipioDialog;
import org.udg.pds.todoandroid.fragment.SeleccionarProvinciasDialog;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.CustomTextWatcher;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.ImageUtil;
import org.udg.pds.todoandroid.util.InitRetrofit;
import org.udg.pds.todoandroid.util.SnackbarUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registro extends AppCompatActivity implements View.OnClickListener, SeleccionarProvinciasDialog.SeleccionarProvinciasDialogListener, SeleccionarMunicipioDialog.SeleccionarMunicipioDialogListener, SeleccionarDeporteDialog.SeleccionarDeporteDialogListener {

    // Interficie de llamadas a la APIRest gestionada por Retrofit
    private ApiRest apiRest;
    // Paises
    private List<Pais> paises = new ArrayList<>();
    private int paisActual = Global.DEFAULT_COUNTRY;// Pais por defecto España
    // Provincias
    private List<Provincia> provincias = new ArrayList<>();
    private int provinciaActual = Global.DEFAULT_PROVINCE; // Provincia por defecto Girona
    // Municipios
    private List<Municipio> municipios = new ArrayList<>();
    private int municipioActual = Global.DEFAULT_LOCALITY;// Municipio por defecto Girona
    // Deportes
    private List<Deporte> deportes = new ArrayList<>();
    private List<Long> deportesSeleccionado = new ArrayList<>();
    // Imagen perfil
    private ImageView imagenPerfil;
    private boolean esNuevaImagen = false;
    private Uri pathImagenPerfil;
    // ProgressBar
    private ProgressBar progressBar;
    // Text input layout
    private TextInputLayout tilNombre;
    private TextInputLayout tilApellidos;
    private TextInputLayout tilUsername;
    private TextInputLayout tilCorreo;
    private TextInputLayout tilPassword1;
    private TextInputLayout tilPassword2;
    private TextInputLayout tilDeportes;
    // Edit text
    private EditText etNombre;
    private EditText etApellidos;
    private EditText etUsername;
    private EditText etCorreo;
    private EditText etPassword1;
    private EditText etPassword2;
    private EditText etDeportes;
    private EditText etProvincia;
    private EditText etMunicipio;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();
        //Cargamos layout formulario de registro
        setContentView(R.layout.registro_layout);
        // Ponemos el toolbar
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        // Mostrar botón "atras" en action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        imagenPerfil = findViewById(R.id.registro_imagen_perfil);
        // Cargar imagen de perfil por defecto
        Picasso.with(getApplicationContext()).load(Global.BASE_URL + Global.DEFAULT_IMAGE_USER).into(imagenPerfil);
        imagenPerfil.setOnClickListener(this);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        tilNombre = findViewById(R.id.registro_til_nombre);
        tilApellidos = findViewById(R.id.registro_til_apellidos);
        tilUsername = findViewById(R.id.registro_til_username);
        tilCorreo = findViewById(R.id.registro_til_correo);
        tilPassword1 = findViewById(R.id.registro_til_password1);
        tilPassword2 = findViewById(R.id.registro_til_password2);
        tilDeportes = findViewById(R.id.registro_til_deportes);

        etNombre = findViewById(R.id.registro_et_nombre);
        etApellidos = findViewById(R.id.registro_et_apellidos);
        etUsername = findViewById(R.id.registro_et_username);
        etCorreo = findViewById(R.id.registro_et_correo);
        etPassword1 = findViewById(R.id.registro_et_password1);
        etPassword2 = findViewById(R.id.registro_et_password2);
        etDeportes = findViewById(R.id.registro_et_deportes);
        etProvincia = findViewById(R.id.registro_et_provincias);
        etMunicipio = findViewById(R.id.registro_et_municipios);

        etDeportes.setOnClickListener(this);
        etProvincia.setOnClickListener(this);
        etMunicipio.setOnClickListener(this);

        obtenerPaises();
        obtenerDeportes();
        addTextListeners();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.registro_et_provincias:
                seleccionarProvincia();
                break;
            case R.id.registro_et_municipios:
                seleccionarMunicipio();
                break;
            case R.id.registro_et_deportes:
                seleccionarDeportesFavoritos();
                break;
            case R.id.registro_imagen_perfil:
                cargarImagenPerfil();
                break;
        }
    }

    private void cargarImagenPerfil() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == Global.REQUEST_CODE_GALLERY) {
            if (data != null) {
                pathImagenPerfil = data.getData();
                try {
                    // Interfície para recortar la imagen
                    CropImage.activity(pathImagenPerfil)
                            .setAspectRatio(10, 10)
                            .start(this);
                } catch (Exception e) {
                    Picasso.with(getApplicationContext()).load(Global.BASE_URL + Global.DEFAULT_IMAGE_USER).into(imagenPerfil);
                    esNuevaImagen = false;
                    SnackbarUtil.showSnackBar(findViewById(R.id.login_snackbar), getString(R.string.imagen_cargada_error), Snackbar.LENGTH_LONG, true);
                }
            }
        } else if (requestCode == Global.REQUEST_CODE_CAMERA) {
            try {
                pathImagenPerfil = data.getData();
                // Interfície para recortar la imagen
                CropImage.activity(pathImagenPerfil)
                        .setAspectRatio(10, 10)
                        .start(this);
            } catch (Exception e) {
                esNuevaImagen = false;
                Picasso.with(getApplicationContext()).load(Global.BASE_URL + Global.DEFAULT_IMAGE_USER).into(imagenPerfil);
                SnackbarUtil.showSnackBar(findViewById(R.id.login_snackbar), getString(R.string.imagen_cargada_error), Snackbar.LENGTH_LONG, true);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                pathImagenPerfil = result.getUri();
                Picasso.with(getApplicationContext()).load(pathImagenPerfil).into(imagenPerfil);
                esNuevaImagen = true;
                SnackbarUtil.showSnackBar(findViewById(R.id.login_snackbar), getString(R.string.imagen_cargada_correctamente), Snackbar.LENGTH_LONG, false);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                esNuevaImagen = false;
                Picasso.with(getApplicationContext()).load(Global.BASE_URL + Global.DEFAULT_IMAGE_USER).into(imagenPerfil);
                SnackbarUtil.showSnackBar(findViewById(R.id.login_snackbar), getString(R.string.imagen_cargada_error), Snackbar.LENGTH_LONG, true);
            }
        }
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

    private void seleccionarDeportesFavoritos() {
        SeleccionarDeporteDialog seleccionarDeportes = new SeleccionarDeporteDialog(deportes, deportesSeleccionado);
        seleccionarDeportes.show(Registro.this.getFragmentManager(), Global.TAG_DEPORTES_DIALOG);
    }

    private void seleccionarMunicipio() {
        SeleccionarMunicipioDialog seleccionarMunicipio = new SeleccionarMunicipioDialog(municipios, municipioActual);
        seleccionarMunicipio.show(Registro.this.getFragmentManager(), Global.TAG_MUNICIPIOS_DIALOG);
    }

    private void seleccionarProvincia() {
        SeleccionarProvinciasDialog seleccionarProvincias = new SeleccionarProvinciasDialog(provincias, provinciaActual);
        seleccionarProvincias.show(Registro.this.getFragmentManager(), Global.TAG_PROVINCIAS_DIALOG);
    }

    @Override
    public void deportesSeleccionados(List<Long> deportesSeleccionados) {
        StringBuilder deportesFavoritos = new StringBuilder();
        if (deportesSeleccionados != null && !deportesSeleccionados.isEmpty()) {
            this.deportesSeleccionado = deportesSeleccionados;
            for (Long deporte : deportesSeleccionados) {
                deportesFavoritos.append(deportes.get(deporte.intValue()).getDeporte()).append(", ");
            }
            deportesFavoritos = new StringBuilder(deportesFavoritos.substring(0, deportesFavoritos.length() - 2));
            etDeportes.setText(deportesFavoritos.toString());
        } else {
            etDeportes.getText().clear();
            this.deportesSeleccionado = new ArrayList<>();
        }
    }


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

    @Override
    public void municipioSeleccionado(int municipioSeleccionado) {
        // Comparar provincia actual con la seleccionada
        if (municipioActual != municipioSeleccionado) {
            municipioActual = municipioSeleccionado;
            etMunicipio.setText(municipios.get(municipioActual).getMunicipio());
            obtenerMunicipios();
        }
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

    private void obtenerPaises() {
        Call<List<Pais>> peticionRestPaises = apiRest.paises();
        peticionRestPaises.enqueue(new Callback<List<Pais>>() {
            @Override
            public void onResponse(Call<List<Pais>> call, Response<List<Pais>> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    paises = response.body();
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
            @Override
            public void onResponse(Call<List<Provincia>> call, Response<List<Provincia>> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    provincias = response.body();
                    if (provincias != null && !provincias.isEmpty()) {
                        etProvincia.setText(provincias.get(provinciaActual).getProvincia());
                    }
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
            @Override
            public void onResponse(Call<List<Municipio>> call, Response<List<Municipio>> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    municipios = response.body();
                    if (municipios != null && !municipios.isEmpty()) {
                        etMunicipio.setText(municipios.get(municipioActual).getMunicipio());
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
            public void onFailure(Call<List<Municipio>> call, Throwable t) {
                Log.e(getString(R.string.log_error), t.getMessage());
            }
        });
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.toolbar_crear_cuenta) {
            if (validarFormularioRegistro()) {
                // Obtenemos token proporcionado por Firebase que identifica el dispositivo a la hora de recibir notificaciones
                String tokenFireBase = FirebaseInstanceId.getInstance().getToken();
                List<Long> idDeportesFavoritos = obtenerIdDeportesSeleccionados();
                progressBar.setVisibility(View.VISIBLE);
                UsuarioRegistroPeticion datosRegistro = new UsuarioRegistroPeticion(etNombre.getText().toString(), etApellidos.getText().toString(), etUsername.getText().toString(), etCorreo.getText().toString(), etPassword1.getText().toString(), tokenFireBase, municipios.get(municipioActual).getId(), idDeportesFavoritos);
                Call<UsuarioRegistroRespuesta> peticionRest = apiRest.registrar(datosRegistro);
                peticionRest.enqueue(new Callback<UsuarioRegistroRespuesta>() {
                    @Override
                    public void onResponse(Call<UsuarioRegistroRespuesta> call, Response<UsuarioRegistroRespuesta> response) {
                        if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                            UsuarioRegistroRespuesta dadesResposta = response.body();
                            // Guardamos datos de la respuesta, que identifican al usuario actual logeado
                            UsuarioActual.getInstance().setId(Objects.requireNonNull(dadesResposta).getId());
                            UsuarioActual.getInstance().setMail(dadesResposta.getEmail());
                            UsuarioActual.getInstance().setUsername(dadesResposta.getUsername());
                            // Si al realizar el registro se ha seleccionado una imagen de la cámara o galería
                            if (esNuevaImagen) {
                                try {
                                    // Obtenemos la imagen del dispositivo
                                    File imageFile = ImageUtil.decodeImage(getRealPathFromURIPath(pathImagenPerfil, Registro.this), getApplicationContext());
                                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), Objects.requireNonNull(imageFile));
                                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), reqFile);
                                    Call<Imagen> peticionRest = apiRest.subirImagenUsuario(body);
                                    peticionRest.enqueue(new Callback<Imagen>() {
                                        @Override
                                        public void onResponse(Call<Imagen> call, Response<Imagen> response) {
                                            if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                                                Intent principal = new Intent(getApplicationContext(), Principal.class);
                                                // Eliminamos de la pila todas las actividades
                                                principal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                progressBar.setVisibility(View.GONE);
                                                startActivity(principal);
                                                finish();
                                            } else {
                                                try {
                                                    JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                                                    SnackbarUtil.showSnackBar(findViewById(R.id.login_snackbar), jObjError.getString(getString(R.string.error_server_message)), Snackbar.LENGTH_LONG, true);
                                                    progressBar.setVisibility(View.GONE);
                                                } catch (Exception e) {
                                                    progressBar.setVisibility(View.GONE);
                                                    Log.e(getString(R.string.log_error), e.getMessage());
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Imagen> call, Throwable t) {
                                            Log.e(getString(R.string.log_error), t.getMessage());
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                                } catch (Exception e) {
                                    Log.e(getString(R.string.log_error), e.getMessage());
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                Intent principal = new Intent(getApplicationContext(), Principal.class);
                                // Eliminamos de la pila todas las actividades
                                principal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                progressBar.setVisibility(View.GONE);
                                startActivity(principal);
                            }
                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                                SnackbarUtil.showSnackBar(findViewById(R.id.login_snackbar), jObjError.getString(getString(R.string.error_server_message)), Snackbar.LENGTH_LONG, true);
                                progressBar.setVisibility(View.GONE);
                            } catch (Exception e) {
                                progressBar.setVisibility(View.GONE);
                                Log.e(getString(R.string.log_error), e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UsuarioRegistroRespuesta> call, Throwable t) {
                        Log.e(getString(R.string.log_error), t.getMessage());
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // Se obtienen los identificadores de los deportes seleccionados
    private List<Long> obtenerIdDeportesSeleccionados() {
        List<Long> idDeportesFavoritos = new ArrayList<>();
        for (Long posDeporte : deportesSeleccionado) {
            idDeportesFavoritos.add(deportes.get(posDeporte.intValue()).getId());
        }
        return idDeportesFavoritos;
    }

    private boolean validarFormularioRegistro() {
        boolean esCorrecto = true;

        String nombre = Objects.requireNonNull(tilNombre.getEditText()).getText().toString();
        String apellidos = Objects.requireNonNull(tilApellidos.getEditText()).getText().toString();
        String username = Objects.requireNonNull(tilUsername.getEditText()).getText().toString();
        String correo = Objects.requireNonNull(tilCorreo.getEditText()).getText().toString();
        String password1 = Objects.requireNonNull(tilPassword1.getEditText()).getText().toString();
        String password2 = Objects.requireNonNull(tilPassword2.getEditText()).getText().toString();
        // Validar nombre no vacio
        if (nombre.isEmpty()) {
            esCorrecto = false;
            tilNombre.setError(getString(R.string.registro_nombre_obligatorio));
        } else tilNombre.setError(null);
        // Validar apellidos no vacio
        if (apellidos.isEmpty()) {
            esCorrecto = false;
            tilApellidos.setError(getString(R.string.registro_apellido_obligatorio));
        } else tilApellidos.setError(null);
        // Validar nombre de usuario no vacio
        if (username.isEmpty()) {
            esCorrecto = false;
            tilUsername.setError(getString(R.string.registro_username_obligatorio));
        } else tilUsername.setError(null);
        // Validar correo electrónico no vacio
        if (correo.isEmpty()) {
            esCorrecto = false;
            tilCorreo.setError(getString(R.string.registro_correo_obligatorio));
        } else tilCorreo.setError(null);
        // Validar password no vacio
        if (password1.isEmpty()) {
            esCorrecto = false;
            tilPassword1.setError(getString(R.string.registro_password_obligatorio));
        } else tilPassword1.setError(null);
        // Validar password no vacio
        if (password2.isEmpty()) {
            esCorrecto = false;
            tilPassword2.setError(getString(R.string.registro_password2_obligatorio));
        } else tilPassword2.setError(null);
        // Validar passwords coinciden
        if (!password1.isEmpty() && !password2.isEmpty() && !password1.equals(password2)) {
            esCorrecto = false;
            Objects.requireNonNull(tilPassword1.getEditText()).getText().clear();
            Objects.requireNonNull(tilPassword2.getEditText()).getText().clear();
            tilPassword1.setError(getString(R.string.registro_passwords_incorrectos));
            tilPassword2.setError(getString(R.string.registro_passwords_incorrectos));
        }
        // Validar que se ha seleccionado almenos una categoría deportiva
        if (deportesSeleccionado == null || deportesSeleccionado.isEmpty()) {
            esCorrecto = false;
            tilDeportes.setError(getString(R.string.registro_deportes_obligatorio));
        } else tilDeportes.setError(null);

        return esCorrecto;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.registrar_usuario, menu);
        return true;
    }

    // Función que define comportamiento del botón "Atras"
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    // Eliminamos el mensaje de error al introducir texto en un campo vacio
    private void addTextListeners() {
        etNombre.addTextChangedListener(new CustomTextWatcher(tilNombre));
        etApellidos.addTextChangedListener(new CustomTextWatcher(tilApellidos));
        etUsername.addTextChangedListener(new CustomTextWatcher(tilUsername));
        etCorreo.addTextChangedListener(new CustomTextWatcher(tilCorreo));
        etPassword1.addTextChangedListener(new CustomTextWatcher(tilPassword1));
        etPassword2.addTextChangedListener(new CustomTextWatcher(tilPassword2));
        etDeportes.addTextChangedListener(new CustomTextWatcher(tilDeportes));
    }

}
