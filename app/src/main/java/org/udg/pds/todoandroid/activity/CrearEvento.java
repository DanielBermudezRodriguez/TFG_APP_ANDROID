package org.udg.pds.todoandroid.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.tooltip.Tooltip;

import org.json.JSONObject;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Deporte;
import org.udg.pds.todoandroid.entity.UsuarioActual;
import org.udg.pds.todoandroid.entity.UsuarioLoginPeticion;
import org.udg.pds.todoandroid.entity.UsuarioLoginRespuesta;
import org.udg.pds.todoandroid.fragment.SeleccionarDeporteEventoDialog;
import org.udg.pds.todoandroid.fragment.SeleccionarMunicipioDialog;
import org.udg.pds.todoandroid.service.ApiRest;
import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;
import org.udg.pds.todoandroid.util.InputFilterMinMax;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearEvento extends AppCompatActivity implements View.OnClickListener, SeleccionarDeporteEventoDialog.SeleccionarDeporteEventoDialogListener {

    private ApiRest apiRest;

    // Categoría deportiva
    private List<Deporte> deportes = new ArrayList<Deporte>();
    private int deporteSeleccionado = -1;

    // Campos formulario
    private EditText tituloEvento;
    private EditText descripcionEvento;
    private TextView deporteEvento;
    private TextView deporteEventoText;
    private EditText participantesEvento;
    private ImageView tooltipParticipantes;

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


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.crear_evento_deporte:
                seleccionarDeporte();
                break;
            case R.id.crear_evento_tooltip_participantes:
                Tooltip.Builder builder = new Tooltip.Builder(v, R.style.Tooltip2)
                        .setCancelable(true)
                        .setDismissOnClick(true)
                        .setCornerRadius(20f)
                        .setGravity(Gravity.BOTTOM)
                        .setText("Indique 0 para una cantidad ilimitada de participantes");
                builder.show();
                break;
        }

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
