package org.udg.pds.todoandroid.service;


import org.udg.pds.todoandroid.entity.Deporte;
import org.udg.pds.todoandroid.entity.Evento;
import org.udg.pds.todoandroid.entity.EventoCrearPeticion;
import org.udg.pds.todoandroid.entity.GenericId;
import org.udg.pds.todoandroid.entity.Imagen;
import org.udg.pds.todoandroid.entity.Municipio;
import org.udg.pds.todoandroid.entity.Notificaciones;
import org.udg.pds.todoandroid.entity.Pais;
import org.udg.pds.todoandroid.entity.ParticipanteEvento;
import org.udg.pds.todoandroid.entity.Provincia;
import org.udg.pds.todoandroid.entity.Ubicacion;
import org.udg.pds.todoandroid.entity.Usuario;
import org.udg.pds.todoandroid.entity.UsuarioLoginPeticion;
import org.udg.pds.todoandroid.entity.UsuarioLoginRespuesta;
import org.udg.pds.todoandroid.entity.UsuarioModificarPerfil;
import org.udg.pds.todoandroid.entity.UsuarioRegistroPeticion;
import org.udg.pds.todoandroid.entity.UsuarioRegistroRespuesta;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ApiRest {

    @POST("usuario/login")
    Call<UsuarioLoginRespuesta> iniciarSesion(@Body UsuarioLoginPeticion datosPeticion);

    @GET("pais")
    Call<List<Pais>> paises();

    @Multipart
    @POST("imagen/usuario")
    Call<Imagen> subirImagenUsuario(@Part MultipartBody.Part file);

    @DELETE("usuario/logout/{id}")
    Call<GenericId> logout(@Path("id") Long id);

    @PUT("usuario/{idUsuario}")
    Call<GenericId> modificarPerfil(@Body UsuarioModificarPerfil datosModificarPerfil, @Path("idUsuario") Long idUsuario);

    @POST("usuario")
    Call<UsuarioRegistroRespuesta> registrar(@Body UsuarioRegistroPeticion datosRegistro);



    @POST("evento")
    Call<GenericId> crearEvento(@Body EventoCrearPeticion datosEvento);

    @PUT("evento/{idUsuario}/{idEvento}")
    Call<GenericId> modificarEvento(@Body EventoCrearPeticion datosEvento, @Path("idUsuario") Long idUsuario, @Path("idEvento") Long idEvento);


    @GET("provincia/{idPais}")
    Call<List<Provincia>> provincias(@Path("idPais") Long idPais);

    @GET("municipio/{idProvincia}")
    Call<List<Municipio>> municipios(@Path("idProvincia") Long idProvincia);

    @GET("deporte")
    Call<List<Deporte>> getDeportes();

    @POST("ubicacion/usuario")
    Call<GenericId> guardarUbicacionActualUsuario(@Body Ubicacion ubicacion);


    @Multipart
    @POST("imagen/evento/{idEvento}")
    Call<Imagen> subirImagenEvento(@Part MultipartBody.Part file, @Path("idEvento") Long idEvento);

    @GET
    Call<List<Evento>> buscadorEventos(@Url String url);

    @GET("usuario/{idUsuario}")
    Call<Usuario> perfilUsuario(@Path("idUsuario") Long idUsuario);

    @GET("ubicacion/{idEvento}")
    Call<Ubicacion> ubicacionEvento(@Path("idEvento") Long idEvento);

    @GET("participante/{idEvento}/{tipoParticipantes}")
    Call<List<ParticipanteEvento>> obtenerParticipantesEvento(@Path("idEvento") Long idEvento, @Path("tipoParticipantes") int tipoParticipantes);

    @POST("participante/{idEvento}")
    Call<GenericId> addParticipanteEvento(@Path("idEvento") Long idEvento);

    @DELETE("participante/{idEvento}/{idUsuario}")
    Call<GenericId> eliminarParticipanteEvento(@Path("idEvento") Long idEvento, @Path("idUsuario") Long idUsuario);

    @GET("evento/{idEvento}")
    Call<Evento> obtenerInformacionEvento(@Path("idEvento") Long idEvento);



    @GET("usuario/evento/{idUsuario}/{tipoEventos}")
    Call<List<Evento>> eventosUsuario(@Path("idUsuario") Long idUsuario, @Path("tipoEventos") int tipoEventos);

    @DELETE("evento/{idEvento}")
    Call<GenericId> suspenderEvento(@Path("idEvento") Long idEvento);

    @GET("imagen/usuario/nombre/{idUsuario}")
    Call<String> nombreImagenUsuario(@Path("idUsuario") Long idUsuario);

    @GET("imagen/evento/nombre/{idEvento}")
    Call<String> nombreImagenEvento(@Path("idEvento") Long idEvento);

    @GET("notificacion")
    Call<Notificaciones> obtenerConfiguracionNotificaciones();

    @PUT("notificacion")
    Call<Notificaciones> guardarConfiguracionNotificaciones(@Body Notificaciones configuracionNotificaciones);
}
