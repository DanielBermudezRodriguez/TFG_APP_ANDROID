package org.udg.pds.todoandroid.util;


import org.udg.pds.todoandroid.R;

public class DateUtil {

    public static String diaSemana(int dia) {
        String diaSemana = "";
        switch (dia) {
            case 1:
                diaSemana = "domingo";
                break;
            case 2:
                diaSemana = "lunes";
                break;
            case 3:
                diaSemana = "martes";
                break;
            case 4:
                diaSemana = "miércoles";
                break;
            case 5:
                diaSemana = "jueves";
                break;
            case 6:
                diaSemana = "viernes";
                break;
            case 7:
                diaSemana = "sábado";
                break;
        }
        return diaSemana;
    }

    public static String mes(int m) {
        String mes = "";
        switch (m) {
            case 0:
                mes = "enero";
                break;
            case 1:
                mes = "febrero";
                break;
            case 2:
                mes = "marzo";
                break;
            case 3:
                mes = "abril";
                break;
            case 4:
                mes = "mayo";
                break;
            case 5:
                mes = "junio";
                break;
            case 6:
                mes = "julio";
                break;
            case 7:
                mes = "agosto";
                break;
            case 8:
                mes = "septiembre";
                break;
            case 9:
                mes = "octubre";
                break;
            case 10:
                mes = "noviembre";
                break;
            case 11:
                mes = "diciembre";
                break;
        }
        return mes;
    }
}
