package org.udg.pds.todoandroid.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

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
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registro extends AppCompatActivity implements View.OnClickListener, SeleccionarProvinciasDialog.SeleccionarProvinciasDialogListener, SeleccionarMunicipioDialog.SeleccionarMunicipioDialogListener, SeleccionarDeporteDialog.SeleccionarDeporteDialogListener {

    // Interficie de llamadas a la APIRest gestionada por Retrofit
    private ApiRest apiRest;
    // Paises registro
    private List<Pais> paises = new ArrayList<Pais>();
    // Paispor defecto España
    private int paisActual = Global.DEFAULT_COUNTRY;
    // Provincias registro
    private TextView provincia;
    private List<Provincia> provincias = new ArrayList<Provincia>();
    // Provincia por defecto Girona
    private int provinciaActual = Global.DEFAULT_PROVINCE;
    // Municipios registro
    private TextView municipio;
    private List<Municipio> municipios = new ArrayList<Municipio>();
    // Municipio por defecto Girona
    private int municipioActual = Global.DEFAULT_LOCALITY;
    private List<Deporte> deportes = new ArrayList<Deporte>();
    private TextView deporte;
    private List<Long> deportesSeleccionado = new ArrayList<Long>();

    // Imagen perfil
    private ImageView imagenPerfil;
    private boolean esNuevaImagen = false;
    private Uri pathImagenPerfil;

    private ProgressBar progressBar;

    FloatingActionButton cargarImagenPerfil;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Cargamos layout formulario de registro
        setContentView(R.layout.registro_constraint_layout);
        // Ponemos el toolbar
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();
        // Mostrar botón "atras" en action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        provincia = findViewById(R.id.texto_registro_provincia);
        provincia.setOnClickListener(this);
        municipio = findViewById(R.id.texto_registro_municipio);
        municipio.setOnClickListener(this);
        deporte = findViewById(R.id.texto_registro_deportes);
        deporte.setOnClickListener(this);
        //Obtenemos paises
        obtenerPaises();
        obtenerDeportes();

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);


        imagenPerfil = findViewById(R.id.texto_registro_imagen_perfil);
        Picasso.with(getApplicationContext()).load(Global.BASE_URL + "imagen/usuario/0").into(imagenPerfil);
        cargarImagenPerfil = findViewById(R.id.registro_foto_perfil);
        cargarImagenPerfil.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.texto_registro_provincia:
                seleccionarProvincia();
                break;
            case R.id.texto_registro_municipio:
                seleccionarMunicipio();
                break;
            case R.id.texto_registro_deportes:
                seleccionarDeportesFavoritos();
                break;
            case R.id.registro_foto_perfil:
                cargarImagenPerfil();
                break;
        }


    }

    private void cargarImagenPerfil() {
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
                    Picasso.with(getApplicationContext()).load(pathImagenPerfil).into(imagenPerfil);
                    //imagenPerfil.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI));
                    esNuevaImagen = true;
                    Toast.makeText(Registro.this, "Imagen guardada!", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Picasso.with(getApplicationContext()).load(Global.BASE_URL + "imagen/usuario/0").into(imagenPerfil);
                    esNuevaImagen = false;
                    e.printStackTrace();
                    Toast.makeText(Registro.this, "Error al guardar imagen!", Toast.LENGTH_SHORT).show();

                }
            }

        } else if (requestCode == Global.REQUEST_CODE_CAMERA) {
            try {

                pathImagenPerfil = data.getData();
                Picasso.with(getApplicationContext()).load(pathImagenPerfil).into(imagenPerfil);
                esNuevaImagen = true;
                Toast.makeText(Registro.this, "Imagen guardada!", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                esNuevaImagen = false;
                e.printStackTrace();
                Picasso.with(getApplicationContext()).load(Global.BASE_URL + "imagen/usuario/0").into(imagenPerfil);
                Toast.makeText(Registro.this, "Error al guardar imagen!", Toast.LENGTH_SHORT).show();


            }
        }
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

    private void takePhotoFromCamera() {
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

    private void seleccionarDeportesFavoritos() {
        SeleccionarDeporteDialog seleccionarDeportes = new SeleccionarDeporteDialog(deportes, deportesSeleccionado);
        seleccionarDeportes.show(Registro.this.getFragmentManager(), "seleccionarDeportes");
    }

    private void seleccionarMunicipio() {
        SeleccionarMunicipioDialog seleccionarMunicipio = new SeleccionarMunicipioDialog(municipios, municipioActual);
        seleccionarMunicipio.show(Registro.this.getFragmentManager(), "seleccionarMunicio");
    }

    private void seleccionarProvincia() {
        SeleccionarProvinciasDialog seleccionarProvincias = new SeleccionarProvinciasDialog(provincias, provinciaActual);
        seleccionarProvincias.show(Registro.this.getFragmentManager(), "seleccionarProvincias");
    }

    @Override
    public void deportesSeleccionados(List<Long> deportesSeleccionados) {
        String deportesFavoritos = "Deportes: ";
        if (deportesSeleccionados != null && !deportesSeleccionados.isEmpty()) {
            this.deportesSeleccionado = deportesSeleccionados;
            for (Long deporte : deportesSeleccionados) {
                deportesFavoritos += deportes.get(deporte.intValue()).getDeporte() + ", ";
            }
            deportesFavoritos = deportesFavoritos.substring(0, deportesFavoritos.length() - 2) + ".";
            deporte.setText(deportesFavoritos);
        } else {
            deporte.setText(deportesFavoritos);
            this.deportesSeleccionado = new ArrayList<Long>();
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void provinciaSeleccionada(int provinciaSeleccionada) {
        // Comparar provincia actual con la seleccionada
        if (provinciaActual != provinciaSeleccionada) {
            provinciaActual = provinciaSeleccionada;
            provincia.setText("Provincia: " + provincias.get(provinciaActual).getProvincia());
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
            municipio.setText("Municipio: " + municipios.get(municipioActual).getMunicipio());
            obtenerMunicipios();
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

    private void obtenerPaises() {

        Call<List<Pais>> peticionRestPaises = apiRest.paises();

        peticionRestPaises.enqueue(new Callback<List<Pais>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<List<Pais>> call, Response<List<Pais>> response) {
                if (response.raw().code() != 500 && response.isSuccessful()) {
                    paises = response.body();
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
                    provincia.setText("Provincia: " + provincias.get(provinciaActual).getProvincia());
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
                    municipio.setText("Municipio: " + municipios.get(municipioActual).getMunicipio());
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

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.toolbar_crear_cuenta) {
            EditText nombre = findViewById(R.id.texto_registro_nombre);
            EditText apellidos = findViewById(R.id.texto_registro_apellidos);
            EditText username = findViewById(R.id.texto_registro_nick);
            EditText email = findViewById(R.id.texto_registro_email);
            EditText password1 = findViewById(R.id.texto_registro_password_1);
            EditText password2 = findViewById(R.id.texto_registro_password_2);
            if (validarFormularioRegistro(nombre, apellidos, username, email, password1, password2)) {
                String tokenFireBase = FirebaseInstanceId.getInstance().getToken();
                List<Long> idDeportesFavoritos = new ArrayList<Long>();
                for (Long posDeporte : deportesSeleccionado) {
                    idDeportesFavoritos.add(deportes.get(posDeporte.intValue()).getId());
                }

                progressBar.setVisibility(View.VISIBLE);

                UsuarioRegistroPeticion datosRegistro = new UsuarioRegistroPeticion(nombre.getText().toString(), apellidos.getText().toString(), username.getText().toString(), email.getText().toString(), password1.getText().toString(), tokenFireBase, municipios.get(municipioActual).getId(), idDeportesFavoritos);
                Call<UsuarioRegistroRespuesta> peticionRest = apiRest.registrar(datosRegistro);


                peticionRest.enqueue(new Callback<UsuarioRegistroRespuesta>() {
                    @Override
                    public void onResponse(Call<UsuarioRegistroRespuesta> call, Response<UsuarioRegistroRespuesta> response) {
                        if (response.raw().code() != 500 && response.isSuccessful()) {

                            UsuarioRegistroRespuesta dadesResposta = response.body();

                            // Guardamos datos de la respuesta, que identifican al usuario actual logeado
                            UsuarioActual.getInstance().setId(dadesResposta.getId());
                            UsuarioActual.getInstance().setMail(dadesResposta.getEmail());
                            UsuarioActual.getInstance().setUsername(dadesResposta.getUsername());

                            if (esNuevaImagen) {
                                try {
                                    File file = new File(getRealPathFromURIPath(pathImagenPerfil, Registro.this));
                                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
                                    Call<Imagen> peticionRest = apiRest.subirImagenUsuario(body);
                                    peticionRest.enqueue(new Callback<Imagen>() {
                                        @Override
                                        public void onResponse(Call<Imagen> call, Response<Imagen> response) {
                                            if (response.raw().code() != 500 && response.isSuccessful()) {
                                                Imagen imagen = response.body();
                                                Intent principal = new Intent(getApplicationContext(), Principal.class);
                                                // Eliminamos de la pila todas las actividades
                                                principal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                progressBar.setVisibility(View.GONE);
                                                startActivity(principal);
                                            } else {
                                                try {
                                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                                    Toast.makeText(getApplicationContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                                                    progressBar.setVisibility(View.GONE);
                                                } catch (Exception e) {
                                                    progressBar.setVisibility(View.GONE);
                                                    Log.i("ERROR:", e.getMessage());
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Imagen> call, Throwable t) {
                                            Log.i("ERROR:", t.getMessage());
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                                } catch (Exception e) {
                                    System.out.println(e);
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
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                Toast.makeText(getApplicationContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            } catch (Exception e) {
                                progressBar.setVisibility(View.GONE);
                                Log.i("ERROR:", e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UsuarioRegistroRespuesta> call, Throwable t) {
                        Log.i("ERROR:", t.getMessage());
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });

            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validarFormularioRegistro(EditText nombre, EditText apellidos, EditText username, EditText email, EditText password1, EditText password2) {
        boolean esCorrecto = true;

        if (deportesSeleccionado == null || deportesSeleccionado.isEmpty()) {
            deporte.setError("Debe seleccionar almenos una categoría deportiva");
            deporte.requestFocus();
            esCorrecto = false;
        }

        if (nombre == null || TextUtils.isEmpty(nombre.getText().toString())) {
            nombre.setError(getString(R.string.nombre_registro_validacion));
            nombre.requestFocus();
            esCorrecto = false;
        }
        if (apellidos == null || TextUtils.isEmpty(apellidos.getText().toString())) {
            apellidos.setError(getString(R.string.apellidos_registro_validacion));
            apellidos.requestFocus();
            esCorrecto = false;
        }
        if (username == null || TextUtils.isEmpty(username.getText().toString())) {
            username.setError(getString(R.string.nick_registro_validacion));
            username.requestFocus();
            esCorrecto = false;
        }
        if (email == null || TextUtils.isEmpty(email.getText().toString())) {
            email.setError(getString(R.string.email_registro_validacion));
            email.requestFocus();
            esCorrecto = false;
        }
        if (password1 == null || TextUtils.isEmpty(password1.getText().toString())) {
            password1.setError(getString(R.string.password_registro_validacion));
            password1.requestFocus();
            esCorrecto = false;
        }
        if (password2 == null || TextUtils.isEmpty(password2.getText().toString())) {
            password2.setError(getString(R.string.password_registro_validacion));
            password2.requestFocus();
            esCorrecto = false;
        }
        if (password1 != null && password2 != null && !TextUtils.isEmpty(password1.getText().toString()) && !TextUtils.isEmpty(password2.getText().toString())) {
            if (!password1.getText().toString().equals(password2.getText().toString())) {
                password1.setText("");
                password2.setText("");
                password1.setError(getString(R.string.password_diferentes_registro_validacion));
                password1.requestFocus();
                esCorrecto = false;
            }
        }
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

}
