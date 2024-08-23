/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.models.controllers;

import com.soltelec.modulopuc.persistencia.conexion.DBUtil;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.JOptionPane;
import org.soltelec.models.entities.Calibracion;
import org.soltelec.models.entities.CalibracionDosPuntos;

/**
 *
 * @author Gerencia Desarrollo de Soluciones Tecnologicas
 */
public class CalibracionDosPuntosController {

    private CalibracionDosPuntos fillData(ResultSet rs) throws SQLException {
        CalibracionDosPuntos pruebaDosPuntos = new CalibracionDosPuntos();
        String hora = new SimpleDateFormat("hh:mm").format(rs.getTime(CalibracionDosPuntos.FECHA));
        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate(CalibracionDosPuntos.FECHA));
        try {
            Date fecha2 = new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(String.format("%s %s", fecha, hora));
            pruebaDosPuntos.setFecha(fecha2);
        } catch (Exception e) {
            pruebaDosPuntos.setFecha(rs.getDate(CalibracionDosPuntos.FECHA));
        }

        pruebaDosPuntos.setIdCalibracion(rs.getInt(CalibracionDosPuntos.ID_CALIBRACION));
        pruebaDosPuntos.setUsuario(rs.getInt(CalibracionDosPuntos.USUARIO));
        pruebaDosPuntos.setAprobada(rs.getBoolean(CalibracionDosPuntos.APROBADA));
        pruebaDosPuntos.setIdEquipo(rs.getInt(CalibracionDosPuntos.ID_EQUIPO));
        pruebaDosPuntos.setIdTipoCalibracion(rs.getInt(CalibracionDosPuntos.ID_TIPO_CALIBRACION));

        pruebaDosPuntos.setBmCo(rs.getDouble(CalibracionDosPuntos.BM_CO));
        pruebaDosPuntos.setBmCo2(rs.getDouble(CalibracionDosPuntos.BM_CO2));
        pruebaDosPuntos.setBmHc(rs.getDouble(CalibracionDosPuntos.BM_HC));
        pruebaDosPuntos.setAltaCo(rs.getDouble(CalibracionDosPuntos.ALTA_CO));
        pruebaDosPuntos.setAltaCo2(rs.getDouble(CalibracionDosPuntos.ALTA_CO2));
        pruebaDosPuntos.setAltaHc(rs.getDouble(CalibracionDosPuntos.ALTA_HC));

        pruebaDosPuntos.setBancoBmCo(rs.getDouble(CalibracionDosPuntos.BANCO_BM_CO));
        pruebaDosPuntos.setBancoBmCo2(rs.getDouble(CalibracionDosPuntos.BANCO_BM_CO2));
        pruebaDosPuntos.setBancoBmHc(rs.getDouble(CalibracionDosPuntos.BANCO_BM_HC));
        pruebaDosPuntos.setBancoAltaCo(rs.getDouble(CalibracionDosPuntos.BANCO_ALTA_CO));
        pruebaDosPuntos.setBancoAltaCo2(rs.getDouble(CalibracionDosPuntos.BANCO_ALTA_CO2));
        pruebaDosPuntos.setBancoAltaHc(rs.getDouble(CalibracionDosPuntos.BANCO_ALTA_HC));
        
        return pruebaDosPuntos;
    }

    public void registrarCalibracion(CalibracionDosPuntos calibracionDosPuntos) throws SQLException, ClassNotFoundException, IOException {
        CalibracionController calibracionController = new CalibracionController();
        calibracionDosPuntos.setIdCalibracion(calibracionController.registrarCalibracion(calibracionDosPuntos));
        String campos = " (";
        List<String> camposTabla = calibracionDosPuntos.getCamposTabla();
        for (int i = 0; i < camposTabla.size(); i++) {
            if (i != 0) {
                campos += ",";
            }
            campos += camposTabla.get(i);
        }
        campos += ")";
            System.out.println(" VOY A INYECTAR VALORES AUTOMATICAMENTE");
        String valores = "('";
        List valoresTabla = calibracionDosPuntos.getValores();
        for (int i = 0; i < valoresTabla.size(); i++) {
            if (i != 0) {
                valores += "','";
            }
            valores += valoresTabla.get(i);
        }
        valores += "')";
        DBUtil.insert(CalibracionDosPuntos.TABLA_DOS_PUNTOS, campos, valores);
        System.out.println("ACABO DE INSERTAR LAS CALIBRACIONES EN LA BD ");
    }

    //opcion 1
    public void Calibracion2ptsRep(int idUsuario, int idEquipo) throws ClassNotFoundException, SQLException {

        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        Connection conexion = org.soltelec.util.Conex.getConnection();
        //Insertar las medidas dependiendo del numero de tiempos del motor
        String statement = "INSERT INTO calibraciones(CURDATE,GEUSER,aprobada,id_equipo,id_tipo_calibracion) VALUES(?,?,0,?,2) ";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        Date d = new Date();
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        instruccion.setDate(1, new java.sql.Date(c.getTimeInMillis()));
        instruccion.setInt(2, idUsuario);
        instruccion.setInt(3, idEquipo);
//        double diferencia = (Integer.parseInt(CalibracionDosPuntos.BM_HC)) - (Integer.parseInt(CalibracionDosPuntos.BANCO_ALTA_HC));
//        
//        if ((Integer.parseInt(CalibracionDosPuntos.ALTA_HC) > 3000)) { // verificamos si es 2T o 4T
//            if ((Integer.parseInt(CalibracionDosPuntos.BM_HC))>=0 && (Integer.parseInt(CalibracionDosPuntos.BANCO_ALTA_HC) <= 2001)) {
//                if (diferencia <= 100) {
//                    instruccion.setInt(3, 1);
//                } else {
//                    instruccion.setInt(3, 0);
//                }           
//            }else if ((Integer.parseInt(CalibracionDosPuntos.BM_HC)) >=2001 && (Integer.parseInt(CalibracionDosPuntos.BANCO_ALTA_HC) <= 4000)) {
//                if (diferencia <= 200) {
//                    instruccion.setInt(3, 1);
//                } else {
//                    instruccion.setInt(3, 0);
//                }           
//            }else if ((Integer.parseInt(CalibracionDosPuntos.BM_HC))>=4001 && (Integer.parseInt(CalibracionDosPuntos.BANCO_ALTA_HC) <= 8000)) {
//                if (diferencia <= 400) {
//                    instruccion.setInt(3, 1);
//                } else {
//                    instruccion.setInt(3, 0);
//                }           
//            }else if ((Integer.parseInt(CalibracionDosPuntos.BM_HC))>=8001 && (Integer.parseInt(CalibracionDosPuntos.BANCO_ALTA_HC) <= 20000)) {
//                if (diferencia <= 800) {
//                    instruccion.setInt(3, 1);
//                } else {
//                    instruccion.setInt(3, 0);
//                }           
//            }
//        }else{   
//            if ((Integer.parseInt(CalibracionDosPuntos.BM_HC))>=0 && (Integer.parseInt(CalibracionDosPuntos.BANCO_ALTA_HC) <= 1000)) {
//                if (diferencia <= 50) {
//                    instruccion.setInt(3, 1);
//                } else {
//                    instruccion.setInt(3, 0);
//                }           
//            }else if ((Integer.parseInt(CalibracionDosPuntos.BM_HC))>=1001 && (Integer.parseInt(CalibracionDosPuntos.BANCO_ALTA_HC) <= 2000)) {
//                if (diferencia <= 100) {
//                    instruccion.setInt(3, 1);
//                } else {
//                    instruccion.setInt(3, 0);
//                }           
//            }else if ((Integer.parseInt(CalibracionDosPuntos.BM_HC))>=2001 && (Integer.parseInt(CalibracionDosPuntos.BANCO_ALTA_HC) <= 4000)) {
//                if (diferencia <= 200) {
//                    instruccion.setInt(3, 1);
//                } else {
//                    instruccion.setInt(3, 0);
//                }           
//            }else if ((Integer.parseInt(CalibracionDosPuntos.BM_HC))>=4001 && (Integer.parseInt(CalibracionDosPuntos.BANCO_ALTA_HC) <= 10000)) {
//                if (diferencia <= 500) {
//                    instruccion.setInt(3, 1);
//                } else {
//                    instruccion.setInt(3, 0);
//                }           
//            }           
//        }

        instruccion.executeUpdate();

        conexion.close();
    }//End of method registrarResultado

//opcion2
    public CalibracionDosPuntos findCalibracionDosPuntos(int idEquipo) throws ClassNotFoundException, SQLException {
        String sql = "SELECT * FROM " + CalibracionDosPuntos.TABLA
                + " INNER JOIN " + CalibracionDosPuntos.TABLA_DOS_PUNTOS + " USING (CALIBRATION) "
                + "WHERE " + CalibracionDosPuntos.ID_EQUIPO + " = " + idEquipo + " AND " + CalibracionDosPuntos.ID_TIPO_CALIBRACION + "= 2"
                + " ORDER BY " + CalibracionDosPuntos.FECHA + " DESC LIMIT 1";

        System.out.println("..... findCalibracionDosPuntos .....");
        // System.out.println(sql);
        ResultSet rs = DBUtil.executeQuery(sql);
        CalibracionDosPuntos calibracionDosPuntos = null;
        try {
            if (rs.next()) {
                calibracionDosPuntos = fillData(rs);// a si el regresa la fecha 01-jun-2015 0:00:00
            }else{
                throw new SQLException("No Existe Equipo con Serial: " + idEquipo);
            }
        }finally {
            rs.close();
            DBUtil.close();
        }
        return calibracionDosPuntos;
    }
    public CalibracionDosPuntos findCalibracionDosPuntos(int idEquipo,Connection cn) throws ClassNotFoundException, SQLException {
        String sql = "SELECT * FROM " + CalibracionDosPuntos.TABLA
                + " INNER JOIN " + CalibracionDosPuntos.TABLA_DOS_PUNTOS + " USING (CALIBRATION) "
                + "WHERE " + CalibracionDosPuntos.ID_EQUIPO + " = " + idEquipo
                + " ORDER BY " + CalibracionDosPuntos.FECHA + " DESC LIMIT 1";
        System.out.println("..... findCalibracionDosPuntos .....");
        // System.out.println(sql);
        ResultSet rs = DBUtil.executeQuery(sql,cn);
        CalibracionDosPuntos calibracionDosPuntos = null;
        try {
            if (rs.next()) {
                calibracionDosPuntos = fillData(rs);// a si el regresa la fecha 01-jun-2015 0:00:00
            }else{
                JOptionPane.showMessageDialog(null, "no se encontro el equipo: " + idEquipo +""
                                                + "o el equipo no tiene calibraciones", "ERROR", JOptionPane.ERROR_MESSAGE);
                throw new SQLException("No Existe Equipo con Serial: " + idEquipo);
            }
        } finally {
            rs.close();            
        }
        return calibracionDosPuntos;
    }

}
