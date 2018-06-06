package org.udg.pds.todoandroid.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;

import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
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
import org.udg.pds.todoandroid.fragment.SeleccionarMunicipioDialog;
import org.udg.pds.todoandroid.fragment.SeleccionarProvinciasDialog;
import org.udg.pds.todoandroid.service.ApiRest;

import org.udg.pds.todoandroid.util.Global;
import org.udg.pds.todoandroid.util.InitRetrofit;

import java.io.Serializable;
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

public class Buscador extends AppCompatActivity implements View.OnClickListener, SeleccionarDeporteDialog.SeleccionarDeporteDialogListener, SeleccionarProvinciasDialog.SeleccionarProvinciasDialogListener, SeleccionarMunicipioDialog.SeleccionarMunicipioDialogListener {

    private ApiRest apiRest;
    private SearchView tituloBuscador;
    private List<Deporte> deportes = new ArrayList<>();
    private List<Long> deportesSeleccionados = new ArrayList<>();
    private TextView deporte;
    private EditText fechaEvento;
    private List<Pais> paises = new ArrayList<>();
    private int paisActual = -1;
    private TextView provincia;
    private List<Provincia> provincias = new ArrayList<>();
    private int provinciaActual = -1;
    private TextView municipio;
    private List<Municipio> municipios = new ArrayList<>();
    private int municipioActual = -1;
    private TextView mostrarDistancia;
    private SeekBar seekBarDistancia;
    private int distancia = -1;
    private int year = -1;
    private int month = -1;
    private int day = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.buscador_layout);

        // Inicializamos el servicio de APIRest de retrofit
        apiRest = InitRetrofit.getInstance().getApiRest();

        // Ponemos el toolbar
        Toolbar toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        // Mostrar botón "atras" en action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

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

        findViewById(R.id.constraintLayout2).setVisibility(View.GONE);

        provincia = findViewById(R.id.texto_buscador_provincia);
        provincia.setOnClickListener(this);
        provincia.setVisibility(View.GONE);
        municipio = findViewById(R.id.texto_buscador_municipio);
        municipio.setOnClickListener(this);
        municipio.setVisibility(View.GONE);

        mostrarDistancia = findViewById(R.id.buscador_cerca_de_mi_distancia);
        mostrarDistancia.setVisibility(View.GONE);

        seekBarDistancia = findViewById(R.id.buscador_seekbar_distancia);
        seekBarDistancia.setVisibility(View.GONE);
        seekBarDistancia.setProgress(0);
        seekBarDistancia.setMax(100);
        seekBarDistancia.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    //hace un llamado a la perilla cuando se arrastra
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progress, boolean fromUser) {
                        if (progress == 0) mostrarDistancia.setText("Cualquier distancia");
                        else
                            mostrarDistancia.setText("Distancia: " + String.valueOf(progress) + " Km");

                        distancia = progress;
                    }

                    //hace un llamado  cuando se toca la perilla
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    //hace un llamado  cuando se detiene la perilla
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

        obtenerDeportes();

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.boton_buscador_aplicar_filtros:
                String url = Global.BASE_URL + "evento?limite="+String.valueOf(Global.LIMITE_EVENTOS_REQUEST)+"&";
                if (tituloBuscador != null && tituloBuscador.getQuery() != null && !tituloBuscador.getQuery().toString().isEmpty()) {
                    try {
                        url += "titulo=" + URLEncoder.encode(tituloBuscador.getQuery().toString(), "UTF-8") + "&";
                    } catch (UnsupportedEncodingException e) {
                        Log.e("ERROR", "Error al codificar el título en el buscador", e);
                    }
                }
                // Si no se ha seleccionado ninguna categoria deportiva se consideran todas
                if (deportesSeleccionados == null || deportesSeleccionados.isEmpty()) {
                    for (Deporte d : deportes) {
                        url += "deportes=" + d.getId().toString() + "&";
                    }
                } else {
                    // Categorias deportivas seleccionadas
                    for (Long d : deportesSeleccionados) {
                        url += "deportes=" + (d + 1) + "&";
                    }
                }
                // Miramos si se ha escojido una fecha
                if (year != -1 && month != -1 && day != -1) {
                    Date date = new GregorianCalendar(year, month, day).getTime();
                    @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    url += "fechaEvento=" + df.format(date) + "&";
                }
                if (distancia > 0) {
                    url += "distancia=" + distancia + "&";
                } else if (municipioActual != -1) {
                    url += "municipio=" + municipios.get(municipioActual).getId().toString() + "&";
                }
                url = url.substring(0, url.length() - 1);

                Intent returnIntent = new Intent();
                returnIntent.putExtra(Global.PARAMETER_RESULTADOS_BUSCADOR, url);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                System.out.println(url);

                break;
            case R.id.boton_buscador_reiniciar_filtros:
                distancia = -1;
                tituloBuscador.setQuery("", false);
                tituloBuscador.setIconified(false);
                String deportesFavoritos = "Todos los deportes";
                deporte.setText(deportesFavoritos);
                this.deportesSeleccionados = new ArrayList<Long>();
                fechaEvento.setText("");
                year = -1;
                month = -1;
                day = -1;
                // Si hay escojida alguna ubicacion la reseteamos.
                desactivarBoton(R.id.buscador_boton_cerca_de_mi);
                desactivarBoton(R.id.buscador_boton_ubicacion);
                if (!emptyUbicacion()) {
                    provincia.setVisibility(View.GONE);
                    municipio.setVisibility(View.GONE);
                    resetUbicacion();
                }
                seekBarDistancia.setVisibility(View.GONE);
                seekBarDistancia.setProgress(0);
                mostrarDistancia.setText("Cualquier distancia");
                mostrarDistancia.setVisibility(View.GONE);
                findViewById(R.id.constraintLayout2).setVisibility(View.GONE);

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
                // Si hay escojida alguna ubicacion la reseteamos.
                if (!emptyUbicacion()) resetUbicacion();
                findViewById(R.id.constraintLayout2).setVisibility(View.VISIBLE);
                seekBarDistancia.setVisibility(View.VISIBLE);
                mostrarDistancia.setVisibility(View.VISIBLE);
                break;
            case R.id.buscador_boton_ubicacion:
                activarBoton(v.getId());
                desactivarBoton(R.id.buscador_boton_cerca_de_mi);
                distancia = -1;
                findViewById(R.id.constraintLayout2).setVisibility(View.VISIBLE);
                provincia.setVisibility(View.VISIBLE);
                municipio.setVisibility(View.VISIBLE);

                if (emptyUbicacion()) {
                    paisActual = Global.DEFAULT_COUNTRY;
                    provinciaActual = Global.DEFAULT_PROVINCE;
                    municipioActual = Global.DEFAULT_LOCALITY;
                    obtenerUbicacion();

                }

                seekBarDistancia.setProgress(0);
                mostrarDistancia.setText("Cualquier distancia");
                seekBarDistancia.setVisibility(View.GONE);
                mostrarDistancia.setVisibility(View.GONE);
                break;
            case R.id.texto_buscador_provincia:
                seleccionarProvincia();
                break;
            case R.id.texto_buscador_municipio:
                seleccionarMunicipio();
                break;
        }


    }


    private void seleccionarMunicipio() {
        SeleccionarMunicipioDialog seleccionarMunicipio = new SeleccionarMunicipioDialog(municipios, municipioActual);
        seleccionarMunicipio.show(Buscador.this.getFragmentManager(), "seleccionarMunicio");
    }

    private void seleccionarProvincia() {
        SeleccionarProvinciasDialog seleccionarProvincias = new SeleccionarProvinciasDialog(provincias, provinciaActual);
        seleccionarProvincias.show(Buscador.this.getFragmentManager(), "seleccionarProvincias");
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
        if (id == R.id.buscador_boton_cerca_de_mi) {
            boton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_cerca_de_mi_no_select), null, null);
        } else if (id == R.id.buscador_boton_ubicacion) {
            boton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_ubicacion_no_select), null, null);
        }

    }

    private void activarBoton(int id) {
        Button boton = findViewById(id);
        boton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blanco));
        boton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.border_button_select));
        if (id == R.id.buscador_boton_cerca_de_mi) {
            boton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_cerca_de_mi_select), null, null);
        } else if (id == R.id.buscador_boton_ubicacion) {
            boton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_ubicacion_select), null, null);
        }
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
                fechaEvento.setText(selectedDate);
            }
        });
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private String dosDigitos(int n) {
        return (n <= 9) ? ("0" + n) : String.valueOf(n);
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

    // Función que define comportamiento del botón "Atras"
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }


}
