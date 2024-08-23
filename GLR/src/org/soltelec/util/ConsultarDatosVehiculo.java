/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.soltelec.test.Test;

/**
 * Clase para guardar medidas en la base de datos usando jdbc Esta la realice
 * para registrar las medidas de la prueba de luces usando el sonometro gemini
 *
 * @author GerenciaDST
 */
public class ConsultarDatosVehiculo {

    public ConsultarDatosVehiculo() {
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {

        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        Connection conexion = Conex.getConnection();
        return conexion;
    }

    public void closeConnection(Connection con) throws SQLException {
        con.close();
    }

    /**
     * Metodo para consultar el tipo de vehiculo al cual se le hace la prueba
     *
     * @param idHojaPrueba identificador de la hoja de pruebas
     * @return
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     */
    public Integer buscarTipoVehiculo(long idHojaPrueba) throws SQLException, ClassNotFoundException {
        Connection conn = getConnection();
        String str2 = "select v.CARTYPE from vehiculos v inner join hoja_pruebas hp on hp.Vehiculo_for = v.CAR where hp.TESTSHEET = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(str2);
        preparedStatement.setLong(1, idHojaPrueba);
        ResultSet data = preparedStatement.executeQuery();
        int tipoVehiculos = 1;
        while (data.next()) {
            tipoVehiculos = data.getInt(1);
        }
        return tipoVehiculos;
    }

    /**
     * Consulta el serial de una prueba
     *
     * @param idPrueba identificador de la prueba
     * @return
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     */
    public static String buscarSerialEquipo(long idPrueba) throws SQLException, ClassNotFoundException, IOException 
    {
        Connection conn = Conex.getConnection();
        Long idHojaPrueba = 0L;
        Long idTipoCombustible = 0L;
        Long idTipoPrueba = 0L;
        Long tiemposMotor = 0L;
        Long tipoVehiculo = 0L;
        String nombreCombustible = "";
        String nombreTipoPrueba = "";
        String codigoEquipo = "";
        String serialEquipo = "";
        String diseno = "";
        String catalizador = "";
        String str2 = "SELECT p.hoja_pruebas_for, v.FUELTYPE, tg.Nombre_gasolina,p.Tipo_prueba_for, tp.Nombre_tipo_prueba,v.Tiempos_motor,v.CARTYPE, v.diseño, v.catalizador\n"
                + "FROM pruebas p\n"
                + "INNER JOIN hoja_pruebas hp ON p.hoja_pruebas_for = hp.TESTSHEET\n"
                + "INNER JOIN vehiculos v ON hp.Vehiculo_for = v.CAR\n"
                + "INNER JOIN tipo_prueba tp ON p.Tipo_prueba_for = tp.TESTTYPE\n"
                + "INNER JOIN tipos_gasolina tg ON v.`FUELTYPE` = tg.FUELTYPE\n"
                + "WHERE p.Id_Pruebas = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(str2);
        preparedStatement.setLong(1, idPrueba);
        ResultSet data = preparedStatement.executeQuery();
        while (data.next()) {
            idHojaPrueba = data.getLong(1);
            idTipoCombustible = data.getLong(2);
            nombreCombustible = data.getString(3);
            idTipoPrueba = data.getLong(4);
            nombreTipoPrueba = data.getString(5);
            tiemposMotor = data.getLong(6);
            tipoVehiculo = data.getLong(7);
            diseno = data.getString(8);
            catalizador = data.getString(9);
        }
 
        if (idTipoPrueba == 8) {
            if (nombreCombustible.equals("DIESEL")) {
                codigoEquipo = UtilPropiedades.cargarPropiedad("OPACIMETRO", "equipos.properties");
            } else if (tipoVehiculo == 4 && tiemposMotor == 2) {
                codigoEquipo = UtilPropiedades.cargarPropiedad("GASES2T", "equipos.properties");
                System.out.println("Levanto el id del equipo (gases 2t) es " + codigoEquipo);
            } else {
                codigoEquipo = UtilPropiedades.cargarPropiedad("GASES", "equipos.properties");
                System.out.println("Levanto el id del equipo (Gases) es " + codigoEquipo);
            }
            str2 = "select e.serialresolucion,resolucionambiental from equipos e where e.id_equipo = ?";
            preparedStatement = conn.prepareStatement(str2);
            preparedStatement.setLong(1, Long.parseLong(codigoEquipo));
            data = preparedStatement.executeQuery();
            while (data.next()) {
                serialEquipo = data.getString(1);
//                String[] otherEquipo = data.getString(2).split(";");
                System.out.println(" SERIAL RECUPERADO BD " + serialEquipo);
//                serialEquipo = serialEquipo.concat(otherEquipo[1]);
            }
//            if(diseno.equalsIgnoreCase("Scooter")){
//                String[] equipos = serialEquipo.split(";");
//                String[] bancoGases = equipos[1].split("_");
//                serialEquipo = equipos[0] + ";" + equipos[1];
//                System.out.println("*********serial cuando el vehiculo es Scooter o tiene Catalizado" + serialEquipo + "*********");
//            }
        } else {
            if (idTipoPrueba == 1) { //1 = Inspeccion visual
                codigoEquipo = UtilPropiedades.cargarPropiedad("PROFUNDIMETRO", "equipos.properties");
                 System.out.println("COD EQUIPO DEL FILE IS " + codigoEquipo);
            } else if (idTipoPrueba == 3) { //3 = Foto
                codigoEquipo = UtilPropiedades.cargarPropiedad("FOTO", "equipos.properties");
            } else {
                codigoEquipo = UtilPropiedades.cargarPropiedad(nombreTipoPrueba.toUpperCase(), "equipos.properties");
            }
            str2 = "select e.serialresolucion from equipos e where e.id_equipo = ?";
            preparedStatement = conn.prepareStatement(str2);
            preparedStatement.setLong(1, Long.parseLong(codigoEquipo));
            data = preparedStatement.executeQuery();
            while (data.next()) {
                serialEquipo = data.getString(1);
                 System.out.println(" COD EQUIPO DEL BD IS " + serialEquipo);
            }         
        }
        return serialEquipo;
    }

    public static void main(String[] args) throws Exception 
    {
        String serial = buscarSerialEquipo(2859);
        System.out.println(serial);
    }

}
