/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.models.controllers;

import com.soltelec.modulopuc.persistencia.conexion.DBUtil;
import com.sun.rowset.CachedRowSetImpl;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.soltelec.models.entities.Calibracion;
import org.soltelec.models.entities.CalibracionDosPuntos;

/**
 *
 * @author GerenciaDesarrollo
 */
public class CalibracionController {

    private Calibracion fillData(ResultSet rs) throws SQLException {
        Calibracion calibracion = new Calibracion();

        CalibracionDosPuntos pruebaDosPuntos = new CalibracionDosPuntos();
        String hora = new SimpleDateFormat("hh:mm").format(rs.getTime(calibracion.FECHA));
        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate(calibracion.FECHA));
        try {
           
            Date fecha2 = new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(String.format("%s %s", fecha, hora));
            calibracion.setFecha(fecha2);
        } catch (Exception e) {
            calibracion.setFecha(rs.getDate(Calibracion.FECHA));

        }

        calibracion.setIdCalibracion(rs.getInt(Calibracion.ID_CALIBRACION));
        calibracion.setUsuario(rs.getInt(Calibracion.USUARIO));
        calibracion.setAprobada(rs.getBoolean(Calibracion.APROBADA));
        calibracion.setIdEquipo(rs.getInt(Calibracion.ID_EQUIPO));
        calibracion.setIdTipoCalibracion(rs.getInt(Calibracion.ID_TIPO_CALIBRACION));

        return calibracion;
    }

    public int registrarCalibracion(Calibracion calibracion) throws SQLException, ClassNotFoundException, IOException {
        String campos = " (" + Calibracion.USUARIO + "," + Calibracion.APROBADA + "," + Calibracion.ID_EQUIPO + "," + Calibracion.ID_TIPO_CALIBRACION + "," + Calibracion.VALOR0 +  "," + Calibracion.VALOR1 +  "," + Calibracion.VALOR2 +  "," + Calibracion.VALOR3 +  "," + Calibracion.VALOR4 +  "," + Calibracion.VALOR5 +  "," + Calibracion.VALOR6 + "," + Calibracion.VALOR7 +"," + Calibracion.LAB_FILTRO_ALTO +"," + Calibracion.SERIAL_FILTRO_ALTO +"," + Calibracion.CERTIFICADO_FILTRO_ALTO +"," + Calibracion.LAB_FILTRO_BAJO +"," + Calibracion.SERIAL_FILTRO_BAJO +"," + Calibracion.CERTIFICADO_FILTRO_BAJO +  ")";
        String valores = "('" + calibracion.getUsuario() + "','" + calibracion.getAprobada() + "','" + calibracion.getIdEquipo() + "','" + calibracion.getIdTipoCalibracion() + "','" + calibracion.getValor0()+ "','" + calibracion.getValor1()+ "','" + calibracion.getValor2()+ "','" + calibracion.getValor3()+ "','" + calibracion.getValor4()+ "','" + calibracion.getValor5()+ "','" + calibracion.getValor6()+ "','" + calibracion.getValor7() +"','" + calibracion.getLabFiltroAlto() +"','" + calibracion.getSerialFiltroAlto() +"','" + calibracion.getCertificadoFiltroAlto() +"','" + calibracion.getLabFiltroBajo() +"','" + calibracion.getSerialFiltroBajo() +"','" + calibracion.getCertificadoFiltroBajo() + "')";
        return DBUtil.insertAndGetId(Calibracion.TABLA, campos, valores);//aca, bien
    }

    public int registrarCalibracionReprobada(Calibracion calibracion) throws SQLException, ClassNotFoundException, IOException {
        String campos = " (" + Calibracion.USUARIO + "," + Calibracion.APROBADA + "," + Calibracion.ID_EQUIPO + "," + Calibracion.ID_TIPO_CALIBRACION + ")";
        String valores = "('" + calibracion.getUsuario() + "','" + 0 + "','" + calibracion.getIdEquipo() + "','" + calibracion.getIdTipoCalibracion() + "')";

        return DBUtil.insertAndGetId(Calibracion.TABLA, campos, valores);
    }

    public Calibracion findPruebaFugas(int idEquipo) throws SQLException, ClassNotFoundException, IOException {
        String sql = "SELECT * FROM " + Calibracion.TABLA + " WHERE (" + Calibracion.ID_TIPO_CALIBRACION + " = 3"
                + " AND " + Calibracion.ID_EQUIPO + " = " + idEquipo + ") "
                + " ORDER BY " + Calibracion.FECHA + " DESC LIMIT 1";

        System.out.println(".....findCalibracionByDescipcion....");
        //System.out.println("SQL: " + sql);          
        ResultSet rs = DBUtil.executeQuery(sql);
        Calibracion calibracion = null;
        try {
            if (rs.next()) {
                calibracion = fillData(rs);
            }
        } finally {
            rs.close();
            DBUtil.close();
        }

        return calibracion;
    }
    public Calibracion findPruebaFugas(int idEquipo,Connection cn) throws SQLException, ClassNotFoundException, IOException {
        String sql = "SELECT * FROM " + Calibracion.TABLA + " WHERE (" + Calibracion.ID_TIPO_CALIBRACION + " = 3"
                + " AND " + Calibracion.ID_EQUIPO + " = " + idEquipo + ") "
                + " ORDER BY " + Calibracion.FECHA + " DESC LIMIT 1";

        System.out.println(".....findCalibracionByDescipcion....");
        System.out.println("SQL: CONSULTA CALIBRACION " + sql);          
        ResultSet rs = DBUtil.executeQuery(sql,cn);
        Calibracion calibracion = null;
        try {
            if (rs.next()) {
                calibracion = fillData(rs);
            }
        } finally {
            rs.close();            
        }
        return calibracion;
    }

    public Calibracion findLinealidad(Long  serialBanco) throws SQLException, ClassNotFoundException, IOException {     
        String sql = "SELECT * FROM " + Calibracion.TABLA + ", equipos " +" WHERE calibraciones.id_equipo = equipos.id_equipo AND  " + Calibracion.ID_TIPO_CALIBRACION + " = 1 AND equipos.serial="+serialBanco
              + " ORDER BY " + Calibracion.FECHA + " DESC LIMIT 1";
          System.out.println("SQL IS  " + sql);
        ResultSet rs = DBUtil.executeQuery(sql);
         System.out.println("SQL IS  " + sql);
        Calibracion calibracion = null;
        try {
            if (rs.next()) {
                calibracion = fillData(rs);
            }
        } finally {
            rs.close();
            DBUtil.close();
        }
        return calibracion;
    }
    public Calibracion findCalibracionGasolina(int idEquipo) throws SQLException, ClassNotFoundException, IOException {
        String sql = "SELECT * FROM " + Calibracion.TABLA + " WHERE (" + Calibracion.ID_TIPO_CALIBRACION + " = 2"
                + " AND " + Calibracion.ID_EQUIPO + " = " + idEquipo + ") "
                + " ORDER BY " + Calibracion.FECHA + " DESC LIMIT 1";

        ResultSet rs = DBUtil.executeQuery(sql);

        Calibracion calibracion = null;

        try {
            if (rs.next()) {
                calibracion = fillData(rs);
            }
        } finally {
            rs.close();
            DBUtil.close();
        }

        return calibracion;
    }
    
    public Calibracion findCalibraciontipotres(int idEquipo) throws SQLException, ClassNotFoundException, IOException {
        String sql = "SELECT * FROM " + Calibracion.TABLA + " WHERE (" + Calibracion.ID_TIPO_CALIBRACION + " = 3"
                + " AND " + Calibracion.ID_EQUIPO + " = " + idEquipo + ") "
                + " ORDER BY " + Calibracion.FECHA + " DESC LIMIT 1";

        ResultSet rs = DBUtil.executeQuery(sql);

        Calibracion calibracion = null;

        try {
            if (rs.next()) {
                calibracion = fillData(rs);
            }
        } finally {
            rs.close();
            DBUtil.close();
        }

        return calibracion;
    }
    public Calibracion findLinealidadgas(int idEquipo) throws SQLException, ClassNotFoundException, IOException {
        String sql = "SELECT * FROM " + Calibracion.TABLA + " WHERE (" + Calibracion.ID_TIPO_CALIBRACION + " = 1"
                + " AND " + Calibracion.ID_EQUIPO + " = " + idEquipo + ") "
                + " ORDER BY " + Calibracion.FECHA + " DESC LIMIT 1";

        ResultSet rs = DBUtil.executeQuery(sql);

        Calibracion calibracion = null;

        try {
            if (rs.next()) {
                calibracion = fillData(rs);
            }
        } finally {
            rs.close();
            DBUtil.close();
        }

        return calibracion;
    }
    
}
