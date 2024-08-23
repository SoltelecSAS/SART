/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.models.entities;

import java.util.Date;

/**
 *
 * @author GerenciaDesarrollo
 */
public class Calibracion {

    public static final String TABLA = "calibraciones";
    public static final String ID_CALIBRACION = "CALIBRATION";
    public static final String TIPO_PRUEBA = "TESTTYPE";
    public static final String FECHA = "CURDATE";
    public static final String USUARIO = "GEUSER";
    public static final String VALOR0 = "VALOR0";
    public static final String VALOR1 = "VALOR1";
    public static final String VALOR2 = "VALOR2";
    public static final String VALOR3 = "VALOR3";
    public static final String VALOR4 = "VALOR4";
    public static final String VALOR5 = "VALOR5";
    public static final String VALOR6 = "VALOR6";
    public static final String VALOR7 = "VALOR7";
    public static final String APROBADA = "aprobada";
    public static final String ID_EQUIPO = "id_equipo";
    public static final String ID_TIPO_CALIBRACION = "id_tipo_calibracion";
    public static final String LAB_FILTRO_ALTO = "lab_filtro_alto";
    public static final String SERIAL_FILTRO_ALTO = "serial_filtro_alto";
    public static final String CERTIFICADO_FILTRO_ALTO = "certificado_filtro_alto";
    public static final String LAB_FILTRO_BAJO = "lab_filtro_bajo";
    public static final String SERIAL_FILTRO_BAJO = "serial_filtro_bajo";
    public static final String CERTIFICADO_FILTRO_BAJO = "certificado_filtro_bajo";

    private Integer idCalibracion;
    private Integer tipoPrueba;
    private Date fecha;
    private Integer usuario;
    private String valor0;
    private String valor1;
    private String valor2;
    private String valor3;
    private String valor4;
    private String valor5;
    private String valor6;
    private String valor7;
    private Integer aprobada;
    private Integer idEquipo;
    private Integer idTipoCalibracion;
    private String labFiltroAlto;
    private String serialFiltroAlto;
    private String certificadoFiltroAlto;
    private String labFiltroBajo;
    private String serialFiltroBajo;
    private String CertificadoFiltroBajo;

    public String getValor0() {
        return valor0;
    }

    public void setValor0(String valor0) {
        this.valor0 = valor0;
    }

    public String getValor7() {
        return valor7;
    }

    public void setValor7(String valor7) {
        this.valor7 = valor7;
    }

    public Integer getIdCalibracion() {
        return idCalibracion;
    }

    public void setIdCalibracion(Integer idCalibracion) {
        this.idCalibracion = idCalibracion;
    }

    public Integer getTipoPrueba() {
        return tipoPrueba;
    }

    public void setTipoPrueba(Integer tipoPrueba) {
        this.tipoPrueba = tipoPrueba;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getUsuario() {
        return usuario;
    }

    public void setUsuario(Integer usuario) {
        this.usuario = usuario;
    }

    public String getValor1() {
        return valor1;
    }

    public void setValor1(String valor1) {
        this.valor1 = valor1;
    }

    public String getValor2() {
        return valor2;
    }

    public void setValor2(String valor2) {
        this.valor2 = valor2;
    }

    public String getValor3() {
        return valor3;
    }

    public void setValor3(String valor3) {
        this.valor3 = valor3;
    }

    public String getValor4() {
        return valor4;
    }

    public void setValor4(String valor4) {
        this.valor4 = valor4;
    }

    public String getValor5() {
        return valor5;
    }

    public void setValor5(String valor5) {
        this.valor5 = valor5;
    }

    public String getValor6() {
        return valor6;
    }

    public void setValor6(String valor6) {
        this.valor6 = valor6;
    }

    public Boolean isAprobada() {
        return (aprobada == 1);
    }

    public Integer getAprobada() {
        return aprobada;
    }

    public void setAprobada(Boolean aprobada) {
        this.aprobada = (aprobada) ? 1 : 0;
    }

    public Integer getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(Integer idEquipo) {
        this.idEquipo = idEquipo;
    }

    public Integer getIdTipoCalibracion() {
        return idTipoCalibracion;
    }

    public void setIdTipoCalibracion(Integer idTipoCalibracion) {
        this.idTipoCalibracion = idTipoCalibracion;
    }

    public String getLabFiltroAlto() {
        return labFiltroAlto;
    }

    public void setLabFiltroAlto(String labFiltroAlto) {
        this.labFiltroAlto = labFiltroAlto;
    }

    public String getSerialFiltroAlto() {
        return serialFiltroAlto;
    }

    public void setSerialFiltroAlto(String serialFiltroAlto) {
        this.serialFiltroAlto = serialFiltroAlto;
    }

    public String getCertificadoFiltroAlto() {
        return certificadoFiltroAlto;
    }

    public void setCertificadoFiltroAlto(String certificadoFiltroAlto) {
        this.certificadoFiltroAlto = certificadoFiltroAlto;
    }

    public String getLabFiltroBajo() {
        return labFiltroBajo;
    }

    public void setLabFiltroBajo(String labFiltroBajo) {
        this.labFiltroBajo = labFiltroBajo;
    }

    public String getSerialFiltroBajo() {
        return serialFiltroBajo;
    }

    public void setSerialFiltroBajo(String serialFiltroBajo) {
        this.serialFiltroBajo = serialFiltroBajo;
    }

    public String getCertificadoFiltroBajo() {
        return CertificadoFiltroBajo;
    }

    public void setCertificadoFiltroBajo(String CertificadoFiltroBajo) {
        this.CertificadoFiltroBajo = CertificadoFiltroBajo;
    }

    
}
