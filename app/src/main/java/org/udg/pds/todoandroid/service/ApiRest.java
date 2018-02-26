package org.udg.pds.todoandroid.service;


import org.udg.pds.todoandroid.entity.UsuarioLoginPeticion;
import org.udg.pds.todoandroid.entity.UsuarioLoginRespuesta;
import org.udg.pds.todoandroid.entity.UsuarioRegistroPeticion;
import org.udg.pds.todoandroid.entity.UsuarioRegistroRespuesta;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiRest {

    @POST("usuarios/login")
    Call<UsuarioLoginRespuesta> iniciarSesion(@Body UsuarioLoginPeticion datosPeticion);

    @POST("usuarios/registro")
    Call<UsuarioRegistroRespuesta> registrar(@Body UsuarioRegistroPeticion datosRegistro);
}
