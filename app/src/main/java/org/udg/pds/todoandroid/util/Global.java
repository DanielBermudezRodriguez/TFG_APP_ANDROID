package org.udg.pds.todoandroid.util;


public class Global {

    public static final String BASE_URL = "http://192.168.42.132:8080/rest/";
    public static final int REQUEST_CODE_GPS_LOCATION = 1000; // Código para peticiones GPS
    public static final long MINIMUM_TIME_UPDATE_LOCATION = 60000; // Cada minuto se mira si hay actualizaciones GPS
    public static final float MINIMUM_DISTANCE_UPDATE_LOCATION = 1000; // Mínimo de 1 km respecto posición anterior para actualizar coordenadas GPS


}
