package org.udg.pds.todoandroid.service;


import org.udg.pds.todoandroid.entity.Deporte;
import org.udg.pds.todoandroid.entity.Municipio;
import org.udg.pds.todoandroid.entity.Pais;
import org.udg.pds.todoandroid.entity.Provincia;
import org.udg.pds.todoandroid.entity.UsuarioLoginPeticion;
import org.udg.pds.todoandroid.entity.UsuarioLoginRespuesta;
import org.udg.pds.todoandroid.entity.UsuarioRegistroPeticion;
import org.udg.pds.todoandroid.entity.UsuarioRegistroRespuesta;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiRest {

    @POST("usuario/login")
    Call<UsuarioLoginRespuesta> iniciarSesion(@Body UsuarioLoginPeticion datosPeticion);

    @POST("usuario")
    Call<UsuarioRegistroRespuesta> registrar(@Body UsuarioRegistroPeticion datosRegistro);

    @GET("pais")
    Call<List<Pais>> paises();

    @GET("provincia/{idPais}")
    Call<List<Provincia>> provincias(@Path("idPais") Long idPais);

    @GET("municipio/{idProvincia}")
    Call<List<Municipio>> municipios(@Path("idProvincia") Long idProvincia);

    @GET("deporte")
    Call<List<Deporte>> getDeportes();
}
