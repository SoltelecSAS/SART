package org.soltelec.luxometro.capelec.cap2500;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase encargada de realizar busquedas en listados de string
 *
 * @author Luis Berna
 */
public class BusquedaNumericos {

    /**
     * Busca numeros enteros en una cadena de String
     *
     *
     * @param criterio Pequeña frase que concuerda con la linea a buscar.
     * @param listadoLineas Listado de lineas donde se quiere buscar.
     * @param coincidencia Numero de las veces que se puede repetir el valor.
     * @return Valor numerico en dicha linea.
     */
    public static int buscarEntero(String criterio, List<String> listadoLineas, Coincidencia coincidencia) {
        Pattern p = Pattern.compile("-?\\d+");
        try {
            return Integer.parseInt(buscar(p, criterio, listadoLineas, coincidencia));
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Busca numeros enteros en una cadena de String
     *
     *
     * @param criterio Pequeña frase que concuerda con la linea a buscar.
     * @param listadoLineas Listado de lineas donde se quiere buscar.
     * @return Valor numerico en dicha linea.
     */
    public static int buscarEntero(String criterio, List<String> listadoLineas) {
        Pattern p = Pattern.compile("-?\\d+");
        try {
            return Integer.parseInt(buscar(p, criterio, listadoLineas, Coincidencia.SIN_COINCIDENCIAS));
        } catch (Exception e) {
            return 0;
        }
        
    }

    /**
     * Busca numeros decimales en una cadena de String
     *
     * @param criterio Pequeña frase que concuerda con la linea a buscar.
     * @param listadoLineas Listado de lineas donde se quiere buscar.
     * @return Valor numerico en dicha linea.
     * @param coincidencia Numero de las veces que se puede repetir el valor.
     */
    public static double buscarDecimal(String criterio, List<String> listadoLineas, Coincidencia coincidencia) {
        Pattern p = Pattern.compile("\\d+(\\,\\d{1,2})?");
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);
        DecimalFormat df = (DecimalFormat) nf;
        Number valor = 0;
        try {
            valor = df.parse(buscar(p, criterio, listadoLineas, Coincidencia.SIN_COINCIDENCIAS));
            return (double) valor;
        } catch (Exception ex) {
            return 0.1 + valor.doubleValue();
        }

    }

    /**
     * Busca numeros decimales en una cadena de String
     *
     * @param criterio Pequeña frase que concuerda con la linea a buscar.
     * @param listadoLineas Listado de lineas donde se quiere buscar.
     * @return Valor numerico en dicha linea.
     */
    public static double buscarDecimal(String criterio, List<String> listadoLineas) {
        Pattern p = Pattern.compile("-?\\d+\\,\\d");
//        Pattern p = Pattern.compile("\\d+(\\,\\d{1,2})?");
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);
        DecimalFormat df = (DecimalFormat) nf;
        Number valor = 0;
        try {
            valor = df.parse(buscar(p, criterio, listadoLineas, Coincidencia.SIN_COINCIDENCIAS));
            return (double) valor;
        } catch (Exception ex) {
            return 0.1 + valor.doubleValue();
        }
    }

    private static String buscar(Pattern p, String criterio, List<String> listadoLineas, Coincidencia coincidencia) {
        Scanner scanner = null;
        String valorEcontrado = "";
        int contadorCoincidencias = 0;
        for (String linea : listadoLineas) {
            scanner = new Scanner(linea);
            if (scanner.findInLine(criterio) != null) {
                if (contadorCoincidencias < coincidencia.getValor()) {
                    contadorCoincidencias++;
                    continue;
                }
                Matcher m = p.matcher(linea);
                //Se itera la cantidad de números que hay en la lista, siempre se toma el ultimo
                while (m.find()) {
                    valorEcontrado = m.group();
                }
                break;
            }
        }
        if (scanner != null) {
            scanner.close();
        }
        return valorEcontrado;
    }

}
