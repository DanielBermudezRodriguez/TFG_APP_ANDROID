package org.udg.pds.todoandroid.service;


import org.udg.pds.todoandroid.entity.UsuarioLoginPeticion;
import org.udg.pds.todoandroid.entity.UsuarioLoginRespuesta;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiRest {

    @POST("users/auth")
    Call<UsuarioLoginRespuesta> iniciarSesion(@Body UsuarioLoginPeticion datosPeticion);

}
