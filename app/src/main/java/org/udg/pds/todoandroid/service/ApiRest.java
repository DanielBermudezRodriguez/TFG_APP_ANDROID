package org.udg.pds.todoandroid.service;


import org.udg.pds.todoandroid.entity.Deporte;
import org.udg.pds.todoandroid.entity.Evento;
import org.udg.pds.todoandroid.entity.Imagen;
import org.udg.pds.todoandroid.entity.Municipio;
import org.udg.pds.todoandroid.entity.Pais;
import org.udg.pds.todoandroid.entity.ParticipanteEvento;
import org.udg.pds.todoandroid.entity.Provincia;
import org.udg.pds.todoandroid.entity.Ubicacion;
import org.udg.pds.todoandroid.entity.Usuario;
import org.udg.pds.todoandroid.entity.UsuarioLoginPeticion;
import org.udg.pds.todoandroid.entity.UsuarioLoginRespuesta;
import org.udg.pds.todoandroid.entity.UsuarioRegistroPeticion;
import org.udg.pds.todoandroid.entity.UsuarioRegistroRespuesta;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;

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

    @POST("ubicacion/usuario")
    Call<Long> guardarUbicacionActualUsuario(@Body Ubicacion ubicacion);

    @Multipart
    @POST("imagen/usuario")
    Call<Imagen> subirImagenUsuario(@Part MultipartBody.Part file);

    @GET
    Call<List<Evento>> buscadorEventos(@Url String url);

    @GET("usuario/{idUsuario}")
    Call<Usuario> perfilUsuario(@Path("idUsuario") Long idUsuario);

    @GET("ubicacion/{idEvento}")
    Call<Ubicacion> ubicacionEvento(@Path("idEvento") Long idEvento);

    @GET("participante/{idEvento}")
    Call<List<ParticipanteEvento>> obtenerParticipantesEvento(@Path("idEvento") Long idEvento);
}
