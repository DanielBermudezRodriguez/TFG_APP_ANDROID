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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONObject;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Deporte;
import org.udg.pds.todoandroid.entity.GenericId;
import org.udg.pds.todoandroid.entity.Imagen;
import org.udg.pds.todoandroid.entity.Municipio;
import org.udg.pds.todoandroid.entity.Pais;
import org.udg.pds.todoandroid.entity.Provincia;
import org.udg.pds.todoandroid.entity.Usuario;
import org.udg.pds.todoandroid.entity.UsuarioActual;
import org.udg.pds.todoandroid.entity.UsuarioModificarPerfil;
import org.udg.pds.todoandroid.fragment.DialogConfirmActionFragment;
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

public class ModificarPerfil extends AppCompatActivity implements View.OnClickListener, SeleccionarProvinciasDialog.SeleccionarProvinciasDialogListener,DialogConfirmActionFragment.DialogConfirmActionFragmentListener, SeleccionarMunicipioDialog.SeleccionarMunicipioDialogListener, SeleccionarDeporteDialog.SeleccionarDeporteDialogListener {

    // Interficie de llamadas a la APIRest gestionada por Retrofit
    private ApiRest apiRest;
    // Paises
    private List<Pais> paises = new ArrayList<>();
    private int paisActual = Global.DEFAULT_COUNTRY;// Pais por defecto España
    // Provincias
    private List<Provincia> provincias = new ArrayList<>();
    private int provinciaActual;
    // Municipios
    private List<Municipio> municipios = new ArrayList<>();
    private int municipioActual = 0;
    // Determina si se ha seleccionado una nueva ubicación para el usuario
    private boolean nuevoMunicipio = false;
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

    // Private Usuario actual
    private Usuario usuario;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtenemos los datos del usuario actual pasador por parámetro
        if (getIntent().getExtras() != null) {
            usuario = (Usuario) getIntent().getExtras().getSerializable(Global.KEY_ACTUAL_USER);
        }

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
        // Obtener nombre imagen usuario actual para completar la URL
        final Call<String> nombreImagen = apiRest.nombreImagenUsuario(usuario.getId());
        nombreImagen.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    String imagenNombre = response.body();
                    RequestOptions options = new RequestOptions().centerCrop();
                    Glide.with(getApplicationContext()).load(Global.BASE_URL + Global.IMAGE_USER + usuario.getId().toString() + "/" + imagenNombre).apply(options).into(imagenPerfil);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(getString(R.string.log_error), t.getMessage());
            }
        });
        imagenPerfil.setOnClickListener(this);


        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        // Nombre usuario actual
        tilNombre = findViewById(R.id.registro_til_nombre);
        etNombre = findViewById(R.id.registro_et_nombre);
        etNombre.setText(usuario.getNombre());
        // Apellidos Usuario Actual
        tilApellidos = findViewById(R.id.registro_til_apellidos);
        etApellidos = findViewById(R.id.registro_et_apellidos);
        etApellidos.setText(usuario.getApellidos());
        // NickName usuario actual
        tilUsername = findViewById(R.id.registro_til_username);
        etUsername = findViewById(R.id.registro_et_username);
        etUsername.setText(usuario.getUsername());

        // Oculatar campos no modificables
        tilCorreo = findViewById(R.id.registro_til_correo);
        etCorreo = findViewById(R.id.registro_et_correo);
        tilCorreo.setVisibility(View.GONE);
        tilPassword1 = findViewById(R.id.registro_til_password1);
        etPassword1 = findViewById(R.id.registro_et_password1);
        tilPassword1.setVisibility(View.GONE);
        tilPassword2 = findViewById(R.id.registro_til_password2);
        etPassword2 = findViewById(R.id.registro_et_password2);
        tilPassword2.setVisibility(View.GONE);

        tilDeportes = findViewById(R.id.registro_til_deportes);
        etDeportes = findViewById(R.id.registro_et_deportes);
        obtenerDeportesActuales();
        etProvincia = findViewById(R.id.registro_et_provincias);
        provinciaActual = usuario.getProvincia().getId().intValue() - 1;
        etProvincia.setText(usuario.getProvincia().getProvincia());
        etMunicipio = findViewById(R.id.registro_et_municipios);

        etDeportes.setOnClickListener(this);
        etProvincia.setOnClickListener(this);
        etMunicipio.setOnClickListener(this);

        obtenerPaises();
        obtenerDeportes();
        addTextListeners();
    }

    // Insertar deportes favoritos actuales del usuario actual
    private void obtenerDeportesActuales() {
        StringBuilder deportesFavoritos = new StringBuilder();

        for (Deporte deporte : usuario.getDeportesFavoritos()) {
            deportesFavoritos.append(deporte.getDeporte()).append(", ");
            deportesSeleccionado.add(deporte.getId() - 1);
        }
        deportesFavoritos = new StringBuilder(deportesFavoritos.substring(0, deportesFavoritos.length() - 2));
        etDeportes.setText(deportesFavoritos.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_modificar_perfil, menu);
        return true;
    }

    @Override
    public void accionSeleccionada(boolean accion) {
        if (accion) modificarPerfil();
    }

    private void modificarPerfil(){
        List<Long> idDeportesFavoritos = obtenerIdDeportesSeleccionados();
        UsuarioModificarPerfil datosModificarPerfil;
        if (nuevoMunicipio)
            datosModificarPerfil = new UsuarioModificarPerfil(etUsername.getText().toString(), etNombre.getText().toString(), etApellidos.getText().toString(), municipios.get(municipioActual).getId(), idDeportesFavoritos);
        else
            datosModificarPerfil = new UsuarioModificarPerfil(etUsername.getText().toString(), etNombre.getText().toString(), etApellidos.getText().toString(), -1L, idDeportesFavoritos);

        Call<GenericId> peticionRest = apiRest.modificarPerfil(datosModificarPerfil, UsuarioActual.getInstance().getId());
        peticionRest.enqueue(new Callback<GenericId>() {
            @Override
            public void onResponse(Call<GenericId> call, Response<GenericId> response) {
                if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
                    GenericId id = response.body();
                    // Guardamos el nuevo nombre de usuario
                    UsuarioActual.getInstance().setUsername(etUsername.getText().toString());
                    // Si al realizar el registro se ha seleccionado una imagen de la cámara o galería
                    if (esNuevaImagen) {
                        try {
                            // Obtenemos la imagen del dispositivo
                            File imageFile = ImageUtil.decodeImage(getRealPathFromURIPath(pathImagenPerfil, ModificarPerfil.this), getApplicationContext());
                            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), Objects.requireNonNull(imageFile));
                            MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), reqFile);
                            Call<org.udg.pds.todoandroid.entity.Imagen> peticionRest = apiRest.subirImagenUsuario(body);
                            peticionRest.enqueue(new Callback<org.udg.pds.todoandroid.entity.Imagen>() {
                                @Override
                                public void onResponse(Call<org.udg.pds.todoandroid.entity.Imagen> call, Response<org.udg.pds.todoandroid.entity.Imagen> response) {
                                    if (response.raw().code() != Global.CODE_ERROR_RESPONSE_SERVER && response.isSuccessful()) {
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
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        } catch (Exception e) {
                            Log.e(getString(R.string.log_error), e.getMessage());
                        }
                    } else {
                        finish();
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

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.toolbar_modificar_perfil) {
            if (validarFormularioRegistro()) {
                DialogConfirmActionFragment accion = new DialogConfirmActionFragment(getString(R.string.registro_dialog_modificar_perfil_titulo), getString(R.string.registro_dialog_modificar_perfil_contenido));
                accion.show(ModificarPerfil.this.getFragmentManager(), "");
            }
        }
        return super.onOptionsItemSelected(item);
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

    private boolean validarFormularioRegistro() {
        boolean esCorrecto = true;

        String nombre = Objects.requireNonNull(tilNombre.getEditText()).getText().toString();
        String apellidos = Objects.requireNonNull(tilApellidos.getEditText()).getText().toString();
        String username = Objects.requireNonNull(tilUsername.getEditText()).getText().toString();

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

        // Validar que se ha seleccionado almenos una categoría deportiva
        if (deportesSeleccionado == null || deportesSeleccionado.isEmpty()) {
            esCorrecto = false;
            tilDeportes.setError(getString(R.string.registro_deportes_obligatorio));
        } else tilDeportes.setError(null);

        return esCorrecto;
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
        seleccionarDeportes.show(ModificarPerfil.this.getFragmentManager(), Global.TAG_DEPORTES_DIALOG);
    }

    private void seleccionarMunicipio() {
        SeleccionarMunicipioDialog seleccionarMunicipio = new SeleccionarMunicipioDialog(municipios, municipioActual);
        seleccionarMunicipio.show(ModificarPerfil.this.getFragmentManager(), Global.TAG_MUNICIPIOS_DIALOG);
    }

    private void seleccionarProvincia() {
        SeleccionarProvinciasDialog seleccionarProvincias = new SeleccionarProvinciasDialog(provincias, provinciaActual);
        seleccionarProvincias.show(ModificarPerfil.this.getFragmentManager(), Global.TAG_PROVINCIAS_DIALOG);
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
            nuevoMunicipio = true;
            obtenerMunicipios();
        }
    }

    @Override
    public void municipioSeleccionado(int municipioSeleccionado) {
        municipioActual = municipioSeleccionado;
        nuevoMunicipio = true;
        etMunicipio.setText(municipios.get(municipioActual).getMunicipio());
        obtenerMunicipios();
    }

    // Eliminamos el mensaje de error al introducir texto en un campo vacio
    private void addTextListeners() {
        etNombre.addTextChangedListener(new CustomTextWatcher(tilNombre));
        etApellidos.addTextChangedListener(new CustomTextWatcher(tilApellidos));
        etUsername.addTextChangedListener(new CustomTextWatcher(tilUsername));
        etDeportes.addTextChangedListener(new CustomTextWatcher(tilDeportes));
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
                        if (nuevoMunicipio)
                            etMunicipio.setText(municipios.get(municipioActual).getMunicipio());
                        else
                            etMunicipio.setText(usuario.getMunicipio().getMunicipio());
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

    // Se obtienen los identificadores de los deportes seleccionados
    private List<Long> obtenerIdDeportesSeleccionados() {
        List<Long> idDeportesFavoritos = new ArrayList<>();
        for (Long posDeporte : deportesSeleccionado) {
            idDeportesFavoritos.add(deportes.get(posDeporte.intValue()).getId());
        }
        return idDeportesFavoritos;
    }

    // Función que define comportamiento del botón "Atras"
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

}
