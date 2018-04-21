package org.udg.pds.todoandroid.activity;


import android.app.DatePickerDialog;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Deporte;

import org.udg.pds.todoandroid.entity.Municipio;
import org.udg.pds.todoandroid.entity.Pais;
import org.udg.pds.todoandroid.entity.Provincia;
import org.udg.pds.todoandroid.fragment.DatePickerFragment;

import org.udg.pds.todoandroid.fragment.SeleccionarDeporteDialog;
import org.udg.pds.todoandroid.service.ApiRest;

import org.udg.pds.todoandroid.util.InitRetrofit;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Buscador extends AppCompatActivity implements View.OnClickListener, SeleccionarDeporteDialog.SeleccionarDeporteDialogListener {

    private ApiRest apiRest;

    private SearchView tituloBuscador;

    private List<Deporte> deportes = new ArrayList<Deporte>();
    private List<Long> deportesSeleccionados = new ArrayList<Long>();

    private TextView deporte;

    EditText fechaEvento;

    private List<Pais> paises = new ArrayList<Pais>();
    private int paisActual = 0;
    private TextView provincia;
    private List<Provincia> provincias = new ArrayList<Provincia>();
    // Provincia por defecto Girona
    private int provinciaActual = 16;
    // Municipios registro
    private TextView municipio;
    private List<Municipio> municipios = new ArrayList<Municipio>();
    // Municipio por defecto Girona
    private int municipioActual = 74;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.buscador);

        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();

        tituloBuscador = findViewById(R.id.buscador_titulo);
        tituloBuscador.setOnClickListener(this);

        Button aplicarFiltros = findViewById(R.id.boton_buscador_aplicar_filtros);
        aplicarFiltros.setOnClickListener(this);

        Button reiniciarFiltros = findViewById(R.id.boton_buscador_reiniciar_filtros);
        reiniciarFiltros.setOnClickListener(this);

        deporte = findViewById(R.id.texto_buscador_deportes);
        deporte.setOnClickListener(this);

        ImageView iconoDeportesFavoritos = findViewById(R.id.imagen_buscador_deportes);
        iconoDeportesFavoritos.setOnClickListener(this);

        fechaEvento = findViewById(R.id.etPlannedDate);
        fechaEvento.setOnClickListener(this);

        Button cercaDemi = findViewById(R.id.buscador_boton_cerca_de_mi);
        cercaDemi.setOnClickListener(this);

        Button ubicacion = findViewById(R.id.buscador_boton_ubicacion);
        ubicacion.setOnClickListener(this);

        provincia = findViewById(R.id.texto_buscador_provincia);
        provincia.setOnClickListener(this);
        provincia.setVisibility(View.GONE);
        municipio = findViewById(R.id.texto_buscador_municipio);
        municipio.setOnClickListener(this);
        municipio.setVisibility(View.GONE);

        obtenerDeportes();

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.boton_buscador_aplicar_filtros:
                //Toast.makeText(getApplicationContext(), tituloBuscador.getQuery(), Toast.LENGTH_LONG).show();
                break;
            case R.id.boton_buscador_reiniciar_filtros:
                tituloBuscador.setQuery("", false);
                tituloBuscador.setIconified(false);
                String deportesFavoritos = "Todos los deportes";
                deporte.setText(deportesFavoritos);
                this.deportesSeleccionados = new ArrayList<Long>();
                fechaEvento.setText("");
                break;
            case R.id.texto_buscador_deportes:
            case R.id.imagen_buscador_deportes:
                seleccionarDeportesFavoritos();
                break;
            case R.id.etPlannedDate:
                showDatePickerDialog();
                break;
            case R.id.buscador_boton_cerca_de_mi:
                activarBoton(v.getId());
                desactivarBoton(R.id.buscador_boton_ubicacion);
                provincia.setVisibility(View.GONE);
                municipio.setVisibility(View.GONE);
                break;
            case R.id.buscador_boton_ubicacion:
                activarBoton(v.getId());
                desactivarBoton(R.id.buscador_boton_cerca_de_mi);
                provincia.setVisibility(View.VISIBLE);
                municipio.setVisibility(View.VISIBLE);
                break;
        }


    }

    private void desactivarBoton(int id) {
        Button boton = findViewById(id);
        boton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.negro));
        boton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.border_button));
        if (id == R.id.buscador_boton_cerca_de_mi){
            boton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_cerca_de_mi_no_select) , null, null);
        }
        else if (id == R.id.buscador_boton_ubicacion){
            boton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_ubicacion_no_select) , null, null);
        }

    }

    private void activarBoton(int id) {
        Button boton = findViewById(id);
        boton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blanco));
        boton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.border_button_select));
        if (id == R.id.buscador_boton_cerca_de_mi){
            boton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_cerca_de_mi_select) , null, null);
        }
        else if (id == R.id.buscador_boton_ubicacion){
            boton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_ubicacion_select) , null, null);
        }
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 porque enero es 0
                final String selectedDate = dosDigitos(day) + " / " + dosDigitos(month + 1) + " / " + year;
                fechaEvento.setText(selectedDate);
            }
        });
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private String dosDigitos(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
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

    private void seleccionarDeportesFavoritos() {
        SeleccionarDeporteDialog seleccionarDeportes = new SeleccionarDeporteDialog(deportes, deportesSeleccionados);
        seleccionarDeportes.show(Buscador.this.getFragmentManager(), "seleccionarDeportes");
    }

    @Override
    public void deportesSeleccionados(List<Long> deportesSeleccionados) {

        if (deportesSeleccionados != null && !deportesSeleccionados.isEmpty()) {
            String deportesFavoritos = "Deportes: ";
            this.deportesSeleccionados = deportesSeleccionados;
            for (Long deporte : deportesSeleccionados) {
                deportesFavoritos += deportes.get(deporte.intValue()).getDeporte() + ", ";
            }
            deportesFavoritos = deportesFavoritos.substring(0, deportesFavoritos.length() - 2) + ".";
            deporte.setText(deportesFavoritos);
        } else {
            String deportesFavoritos = "Todos los deportes";
            deporte.setText(deportesFavoritos);
            this.deportesSeleccionados = new ArrayList<Long>();
        }
    }


}
