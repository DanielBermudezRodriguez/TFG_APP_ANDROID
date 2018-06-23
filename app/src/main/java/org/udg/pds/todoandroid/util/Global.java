package org.udg.pds.todoandroid.util;


public class Global {

    public static final String BASE_URL = "http://192.168.42.132:8080/rest/";
    public static final String DEFAULT_IMAGE_USER = "imagen/usuario/0";
    public static final String IMAGE_USER = "imagen/usuario/";
    public static final String DEFAULT_IMAGE_EVENT = "imagen/evento/0";
    public static final String IMAGE_EVENT = "imagen/evento/";
    public static final String NO_IMAGEN_PERFIL = "no-image.png";
    public static final String DEFAULT_FORO_NAME = "default";

    public static final String TAG_PROVINCIAS_DIALOG = "seleccionarProvincias";
    public static final String TAG_MUNICIPIOS_DIALOG = "seleccionarMunicio";
    public static final String TAG_DEPORTES_DIALOG = "seleccionarDeportes";
    public static final String TAG_DATE_PICKER = "datePicker";

    public static final int REQUEST_CODE_GPS_LOCATION = 1000; // Código para peticiones GPS
    public static final int REQUEST_CODE_BUSCADOR = 2000; // Código para peticion buscador
    public static final long MINIMUM_TIME_UPDATE_LOCATION = 60000; // Cada minuto se mira si hay actualizaciones GPS
    public static final float MINIMUM_DISTANCE_UPDATE_LOCATION = 1000; // Mínimo de 1 km respecto posición anterior para actualizar coordenadas GPS
    public static final int REQUEST_CODE_PLACE_PICKER = 3000; // Código petición para seleccionar ubicación en mapa

    public static final int REQUEST_CODE_GALLERY = 100;
    public static final int REQUEST_CODE_CAMERA = 200;

    public static final int DEFAULT_COUNTRY = 0;
    public static final int DEFAULT_PROVINCE = 16;
    public static final int DEFAULT_LOCALITY = 74;

    public static final String ID_USUARIO_PERFIL = "ID_USUARIO_PERFIL";

    public static final String URL_IMAGEN = "URL_IMAGEN";
    public static final String KEY_SELECTED_EVENT = "SELECTED_EVENT";
    public static final String KEY_SELECTED_EVENT_IS_ADMIN = "SELECTED_EVENT_IS_ADMIN";
    public static final String PREFIJO_SALA_FORO_EVENTO = "observable-salaevento";
    public static final String KEY_SELECTED_EVENT_POSITION = "SELECTED_EVENT_POSITION";
    public static final int REQUEST_CODE_EVENTO_DETALLE = 4000;

    public static final String KEY_ACTUAL_USER = "ACTUAL_USER";

    public static final int CODE_EVENTOS_CREADOS = 0;
    public static final int CODE_EVENTOS_REGISTRADO = 1;

    public static final Long EVENTO_ABIERTO = 1L;
    public static final Long EVENTO_COMPLETO = 2L;
    public static final Long EVENTO_SUSPENDIDO = 3L;
    public static final Long EVENTO_FINALIZADO = 4L;

    // Nombre parámetros onActivityResult
    public static final String PARAMETER_RESULTADOS_BUSCADOR = "RESULTADOS_BUSCADOR";
    public static final String PARAMETER_POSICION_EVENTO_ADAPTER = "POSICION_EVENTO_ADAPTER";
    public static final String PARAMETER_EVENTO_ADAPTER = "EVENTO_ADAPTER";

    public static final int CODE_ERROR_RESPONSE_SERVER = 500;

    // Límite petición servidor eventos
    public static final int LIMITE_EVENTOS_REQUEST = 5;

}
