package com.soltelec.conexion_seriales;

public class Consultas {
    public static String getTipoPruebas(){
        return 
        "SELECT * FROM db_cda.tipo_prueba order by TESTTYPE";
    }

    public static String getSerialesByPrueba(String condicion){
        return
        "SELECT tp.TESTTYPE, tp.Nombre_tipo_prueba, e.*, p.* FROM equipos e\n"+
        "LEFT JOIN pruebas p ON p.serialEquipo = e.serialresolucion\n"+
        "LEFT JOIN tipo_prueba tp ON tp.TESTTYPE = p.Tipo_prueba_for\n"+
        "WHERE tp.TESTTYPE "+ condicion +"\n"+
        "GROUP BY serialresolucion";
    }
}
