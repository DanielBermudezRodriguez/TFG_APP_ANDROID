package org.udg.pds.todoandroid.util;


public class Global {

    public static final String BASE_URL = "http://192.168.42.132:8080/rest/";
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

    public static final String KEY_SELECTED_EVENT = "SELECTED_EVENT";

    public static final String PREFIJO_SALA_FORO_EVENTO = "observable-salaevento";

    public static final int CODE_EVENTOS_CREADOS = 0;
    public static final int CODE_EVENTOS_REGISTRADO = 1;

}
