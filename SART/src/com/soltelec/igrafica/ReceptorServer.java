package com.soltelec.igrafica;

/**
 * @author Ing. Asdrubal Martínez
 */

//TODO: Armar una tabla aquí que contenga el grupo del problema, grupo del problema,
//TODO: cantidad maxima de defectos tipo B que causan rechado, columna: Tipo A, columna: Tipo B

//TODO: Acumular la cadena para obtener los problemas no solo de una categoría sino de varias.

//TODO: Recibir los datos de todas las clases que tienen los CheckBox para sacar datos en general. O pensar
//si almacenar todo en una base de datos y extraer información, posteriormente.
public class ReceptorServer {
    
    //private int matriz[][] = new int[2][50];
    //private int defecto;
    private int problemasA, problemasB;
    private boolean aprobado = true;
    private String cadenaPrueba = "";
    private String grupo = "";
    //private String cadena, tipoVehiculo, tipoPrueba;
    //private String problemas = "Los problemas marcados fueron: \n\n";
    

    public ReceptorServer(){

    }

    public void establecerNombreProblema(String cadenaPrueba){
        this.cadenaPrueba += cadenaPrueba + "\n";
    }

    public String obtenerNombreProblema(){
        return cadenaPrueba;
    }

    public void establecerAprobado(int defecto){
        if(defecto >= 100){
            aprobado = false;
        }
        //TODO: Mirar si es un camion, una moto, un cuatriciclo, pq cada uno tiene un número máximo de rechazos
        //diferente, para carros normales es 10.
        if(defecto >= 10){
            aprobado = false;
        }
    }

    public boolean obtenberAprobado(){
        return aprobado;
    }

    public void establecerGrupo(String grupo){
        this.grupo = grupo;
    }

    public String obtenerGrupo(){
        return grupo;
    }

    public void establecerCantidadProblemas(int problemasA, int problemasB){
        this.problemasA = problemasA;
        this.problemasB = problemasB;
    }

    public int obtenerCantidadProblemasA(){
        return problemasA;
    }

    public int obtenerCantidadProblemasB(){
        return problemasB;
    }
}