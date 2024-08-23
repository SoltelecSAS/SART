/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosbanco;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import javax.swing.JOptionPane;
import org.soltelec.models.controllers.CalibracionController;
import org.soltelec.models.controllers.CalibracionDosPuntosController;
import org.soltelec.models.controllers.EquipoController;
import org.soltelec.models.entities.Calibracion;
import org.soltelec.models.entities.CalibracionDosPuntos;
import org.soltelec.util.Mensajes;
import org.soltelec.util.capelec.BancoCapelec;
import org.soltelec.models.controllers.Conexion;
import org.soltelec.util.UtilPropiedades;

/**
 *
 * @author GerenciaDesarrollo
 */
public class RegVefCalibraciones {

    ProcesoCalibracion procesocalibracion;
    BancoGasolina banco;
    private Integer idEquipo;
    

    public Connection obtenerConexion() {
        Connection con = null;
        try {
            con = Conexion.conectar();
            System.out.println("obtengo obj conexion " + con);
        } catch (SQLException | ClassNotFoundException | IOException ex) {
            System.out.println("TROUBLE OBJ CONEXION " + ex.getMessage());
        }
        return con;
    }

    public void registrarResultadoCal2Ptos(boolean aprobada, PuntoCalibracion pto1, PuntoCalibracion pto2, String serial, int pef, BancoGasolina banco, int idUsuario) throws SQLException, ClassNotFoundException, IOException {
        String labPalta = UtilPropiedades.cargarPropiedad("LABPIPETAALTA", "equipos.properties");
        String cilPalta = UtilPropiedades.cargarPropiedad("CILPIPETAALTA", "equipos.properties");
        String cerPalta = UtilPropiedades.cargarPropiedad("CERPIPETAALTA", "equipos.properties");
        String labPbaja = UtilPropiedades.cargarPropiedad("LABPIPETABAJA", "equipos.properties");
        String cilPbaja = UtilPropiedades.cargarPropiedad("CILPIPETABAJA", "equipos.properties");
        String cerPbaja = UtilPropiedades.cargarPropiedad("CERPIPETABAJA", "equipos.properties");

        System.out.println("\n--->> Registrando verificacion Dos Puntos <<---\n");
        System.out.println(pto1);
        System.out.println(pto2);
        CalibracionDosPuntos calibracion = new CalibracionDosPuntos();
        calibracion.setUsuario(idUsuario);
        this.banco = banco;
        calibracion.setBmHc((pto1.getValorHC()) * Double.parseDouble(this.banco.obtenerPEF()) / 1000.0D);
        calibracion.setBmCo(pto1.getValorCO() * 0.01);
        calibracion.setAltaHc(pto2.getValorHC() * Double.parseDouble(this.banco.obtenerPEF()) / 1000.0D);
        calibracion.setAltaCo(pto2.getValorCO() * 0.01);
        calibracion.setAltaCo2(pto2.getValorCO2() * 0.1);
        if (banco instanceof BancoCapelec) {
            calibracion.setBmCo2((pto1.getValorCO2() * 0.1) / 10);
            calibracion.setAltaCo2((pto2.getValorCO2() * 0.1) / 10);
        } else {
            calibracion.setBmCo2(pto1.getValorCO2() * 0.1);
            calibracion.setAltaCo2(pto2.getValorCO2() * 0.1);
        }
        calibracion.setBancoBmHc(pto1.getBancoHc());
        calibracion.setBancoBmCo(pto1.getBancoCo());
        calibracion.setBancoBmCo2(pto1.getBancoCo2());
        calibracion.setBancoAltaHc(pto2.getBancoHc());
        calibracion.setBancoAltaCo(pto2.getBancoCo());
        calibracion.setBancoAltaCo2(pto2.getBancoCo2());

        calibracion.setAprobada(aprobada);

        calibracion.setLabPipetaAlta(labPalta);
        calibracion.setCilPipetaAlta(cilPalta);
        calibracion.setCerPipetasAlta(cerPalta);
        calibracion.setLabPipetaBaja(labPbaja);
        calibracion.setCilPipetaBaja(cilPbaja);
        calibracion.setCerPipetaBaja(cerPbaja);

        calibracion.setIdEquipo(new EquipoController().findIdEquipoBySerial(serial));
        System.out.println("VOY A LLAMAR METODO METODO registrarCalibracion ");
        new CalibracionDosPuntosController().registrarCalibracion(calibracion);
    }

    public boolean necesitaCalibracionDosPuntos(String serialEquipo) throws SQLException, ClassNotFoundException, IOException, InterruptedException {
        boolean necesitaCalibracion = false;
        Calibracion calibracion = null;
        Calendar fechaCalibracion = null;
        Calendar fechaActual = null;

        try {
            calibracion = new CalibracionDosPuntosController().findCalibracionDosPuntos(idEquipo);
            fechaCalibracion = Calendar.getInstance();
            fechaCalibracion.setTimeInMillis(calibracion.getFecha().getTime());
            fechaActual = Calendar.getInstance();
            fechaActual.add(Calendar.DATE, -3);
        } catch (Exception e) {
            necesitaCalibracion = true;
            return necesitaCalibracion;
        }

        if (calibracion.getAprobada() == 0) {

            Mensajes.mensajeAdvertencia("Prueba REPROBADA, Fecha:" + calibracion.getFecha() + " Necesita Prueba de Dos Puntos");
            return true;
        } else {
            if (fechaCalibracion.compareTo(fechaActual) < 1) {
                Mensajes.mensajeAdvertencia("Necesita Calibracion Dos puntos");
                Thread.sleep(1000);
                necesitaCalibracion = true;
            } else {
                Mensajes.messageDoneTime(" No NECESITA calibracion, Ultima Fecha de Calibracion: " + calibracion.getFecha(), 2);
                Thread.sleep(1000);
                // Mensajes.mensajeCorrecto("No necesita calibracion, Ultima Fecha de Calibracion: " + calibracion.getFecha());
            }

        }
        return necesitaCalibracion;
    }

    public boolean necesitaCalibracionDosPuntos(String serialEquipo, Connection cn) throws SQLException, ClassNotFoundException, IOException {
        boolean necesitaCalibracion = false;
        Calibracion calibracion = null;
        Calendar fechaCalibracion = null;
        Calendar fechaActual = null;
        try {
            calibracion = new CalibracionDosPuntosController().findCalibracionDosPuntos(idEquipo, cn);
            fechaCalibracion = Calendar.getInstance();
            fechaCalibracion.setTimeInMillis(calibracion.getFecha().getTime());
            fechaActual = Calendar.getInstance();
            fechaActual.add(Calendar.DATE, -3);
        } catch (Exception e) {
            necesitaCalibracion = true;
            return necesitaCalibracion;
        }

        if (calibracion.getAprobada() == 0) {
            Mensajes.mensajeAdvertencia("Prueba REPROBADA, Fecha:" + calibracion.getFecha() + " Necesita Prueba de Dos Puntos");
            return true;
        } else {
            if (fechaCalibracion.compareTo(fechaActual) < 1) {
                Mensajes.mensajeAdvertencia("Necesita Calibracion Dos puntos");
                necesitaCalibracion = true;
            } else {
                Mensajes.mensajeCorrecto("No necesita calibracion, Ultima Fecha de Calibracion: " + calibracion.getFecha());
            }
        }

        return necesitaCalibracion;
    }

    public boolean validAnalizador(String serialEquipo, int tMotor) throws SQLException, ClassNotFoundException, IOException {

        boolean res = new EquipoController().validadorAnalizador(serialEquipo, tMotor);
        return res;
    }

    public String naturalezaBco(String serialEquipo) throws SQLException, ClassNotFoundException, IOException {
        String res = new EquipoController().naturalezaBco(serialEquipo);
        return res;
    }

    public boolean necesitaFugas(String serialEquipo) throws SQLException, ClassNotFoundException, IOException, NullPointerException, Exception {
        System.out.println("------------------PRUEBA DE GASES : necesitaFugas --------------");
        boolean necesitaFugas = false;
        Calibracion calibracion = null;

        //--------Cuando no existe la calibración o el equipo no esta registrado en la tabla equipos se cierra el programa y no informa la razon
        idEquipo = new EquipoController().findIdEquipoBySerial(serialEquipo);
        System.out.println("-Equipo---" + idEquipo);
        if (idEquipo == null) {
            return true;
        }

        calibracion = new CalibracionController().findPruebaFugas(idEquipo);
        if (calibracion == null) {
            return true;
        }
        System.out.println("-Calibracion--" + calibracion);

        //-----------------------------------------------------------------------------------------------------------------
        Calendar fechaCalibracion = Calendar.getInstance();
        fechaCalibracion.setTimeInMillis(calibracion.getFecha().getTime());//ddd
        Calendar fechaActual = Calendar.getInstance();
        fechaActual.add(Calendar.DATE, -1);
        System.out.println(" entre necesita fugas  ");
        if (calibracion.getAprobada() == 0) {
            System.out.println("PRUEBAGASES");
            System.out.println("Prueba reprobada necesita fugas  PRUEBA : GASES");
            Mensajes.mensajeAdvertencia("Prueba de Estanqueidad NO fue EXITOSA, Fecha:" + calibracion.getFecha() + " Necesita realizar Prueba de Fugas para poder iniciar la medición de Gases Contaminantes");
            necesitaFugas = true;

        } else {
            if (fechaCalibracion.compareTo(fechaActual) > 0) {
                System.out.println(" aprobada bloque fugas ");
                Mensajes.messageDoneTime(" Prueba de Fugas APROBADA, Fecha: " + calibracion.getFecha(), 2);
                Thread.sleep(1000);
            } else {
                System.out.println(" NO  aprobada bloque fugas ");
                Mensajes.mensajeAdvertencia("Fecha: " + calibracion.getFecha() + " Necesita Prueba de Fugas ");
                Thread.sleep(1000);
                necesitaFugas = true;
            }
        }

        System.out.println("idEquipo " + idEquipo);
        System.out.println("fechaActual: " + fechaActual);
        System.out.println("fechaCalibracion: " + fechaCalibracion);

        return necesitaFugas;
    }

    public boolean necesitaFugas(String serialEquipo, Connection cn) throws SQLException, ClassNotFoundException, IOException, Exception {
        boolean necesitaFugas = false;
        System.out.println("voy a consultar el serial del equipo " + serialEquipo);
        Calibracion calibracion = null;

        idEquipo = new EquipoController().findIdEquipoBySerial(serialEquipo, cn);
        System.out.println("ID DEL EQUIPO ES " + idEquipo);
        if (idEquipo == null) {
            return true;
        }

        calibracion = new CalibracionController().findPruebaFugas(idEquipo, cn);
        System.out.println("-Calibracion--" + calibracion);
        if (calibracion == null) {
            return true;
        }

        Calendar fechaCalibracion = Calendar.getInstance();
        fechaCalibracion.setTimeInMillis(calibracion.getFecha().getTime());//ddd
        Calendar fechaActual = Calendar.getInstance();
        fechaActual.add(Calendar.DATE, -1);

        if (calibracion.getAprobada() == 0) {
            Mensajes.mensajeAdvertencia("Prueba REPROBADA, Fecha:" + calibracion.getFecha() + " Necesita Prueba de Fugas ");
            necesitaFugas = true;
        } else {
            if (fechaCalibracion.compareTo(fechaActual) > 0) {
                Mensajes.mensajeCorrecto("Prueba de Fugas APROBADA, Fecha: " + calibracion.getFecha());
            } else {
                Mensajes.mensajeAdvertencia("Fecha: " + calibracion.getFecha() + " Necesita Prueba de Fugas ");
                necesitaFugas = true;
            }
        }

        System.out.println("idEquipo " + idEquipo);
        System.out.println("fechaActual: " + fechaActual);
        System.out.println("fechaCalibracion: " + fechaCalibracion);

        return necesitaFugas;
    }

    public void registrarPruebaLinelidad(String serial, boolean aprobada, double valorFiltro1, double valorFiltro2, double valorFiltro3, double valorFiltro4, double promedioParaFiltro1, double promedioParaFiltro2, double promedioParaFiltro3, double promedioParaFiltro4, int idUsuario) throws SQLException, ClassNotFoundException, IOException {
        String laboratorioFiltroAlta = UtilPropiedades.cargarPropiedad("LABFALTO", "equipos.properties");
        String serialFiltroAlta = UtilPropiedades.cargarPropiedad("SERFALTO", "equipos.properties");
        String cerFiltroAlta = UtilPropiedades.cargarPropiedad("CERFALTO", "equipos.properties");
        String valFiltroAlta = UtilPropiedades.cargarPropiedad("VFALTO", "equipos.properties");
        String labFiltroBaja = UtilPropiedades.cargarPropiedad("LABFBAJO", "equipos.properties");
        String serialFiltroBaja = UtilPropiedades.cargarPropiedad("SERFBAJO", "equipos.properties");
        String cerFiltroaja = UtilPropiedades.cargarPropiedad("CERFBAJO", "equipos.properties");
        String valFiltroBaja = UtilPropiedades.cargarPropiedad("VFBAJO", "equipos.properties");
        Calibracion calibracion = new Calibracion();

        calibracion.setUsuario(idUsuario);
        DecimalFormat df = new DecimalFormat("#0.0#");
        calibracion.setAprobada(aprobada);
        calibracion.setIdEquipo(new EquipoController().findIdEquipoBySerial(serial));
        calibracion.setIdTipoCalibracion(1);
        calibracion.setValor0(String.valueOf(df.format(valorFiltro1)));
        calibracion.setValor1(String.valueOf(df.format(valorFiltro2)));
        calibracion.setValor2(String.valueOf(df.format(valorFiltro3)));
        calibracion.setValor3(String.valueOf(df.format(valorFiltro4)));
        calibracion.setValor7(String.valueOf(df.format(promedioParaFiltro1)));
        calibracion.setValor4(String.valueOf(df.format(promedioParaFiltro2)));
        calibracion.setValor5(String.valueOf(df.format(promedioParaFiltro3)));
        calibracion.setValor6(String.valueOf(df.format(promedioParaFiltro4)));

        //nuevo
        calibracion.setLabFiltroAlto(laboratorioFiltroAlta);
        calibracion.setSerialFiltroAlto(serialFiltroAlta);
        calibracion.setCertificadoFiltroAlto(cerFiltroAlta);
        calibracion.setLabFiltroBajo(labFiltroBaja);
        calibracion.setSerialFiltroBajo(serialFiltroBaja);
        calibracion.setCertificadoFiltroBajo(cerFiltroaja);

        new CalibracionController().registrarCalibracion(calibracion);
    }

    /**
     * Selecciona la ultima fecha de calibracion, es decir el ultimo registro de
     * calibracion de diesel
     *
     * @insidencia SART-14 SOLICITUD EN LA PARTE DE OPACIDAD - SE REQUIERE QUE
     * LA LINEALIDAD SE SOLICTE A DIARIO.
     *
     * @return
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     * @throws java.io.IOException
     */
    public boolean necesitaLinealidad(Long serialBanco) throws SQLException, ClassNotFoundException, IOException {
        boolean necesitaCalibracion = true;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calibracion calibracion = null;
        Calendar fechaCalibracion = null;
        Calendar fechaActual = null;

        try {
            calibracion = new CalibracionController().findLinealidad(serialBanco);
            fechaCalibracion = Calendar.getInstance();
            fechaCalibracion.setTime(calibracion.getFecha());
            fechaActual = Calendar.getInstance();
        } catch (Exception e) {
            System.out.println("----------serial no encontrado-------");
            return true;
        }
        if (calibracion.getAprobada() == 0) {
            Mensajes.mensajeAdvertencia("Verificacion de Linealidad Vencida ..! " + calibracion.getFecha());
            return true;
        }

        fechaActual.add(Calendar.DATE, -1);
        if (fechaCalibracion.compareTo(fechaActual) < 1) {
            Mensajes.mensajeAdvertencia("Verificacion de Linealidad Vencida ..! " + calibracion.getFecha());
            necesitaCalibracion = true;
        } else {
            Mensajes.mensajeCorrecto("NO NECESITA Verificacion de Linealidad :" + calibracion.getFecha());
            necesitaCalibracion = false;
        }

        return necesitaCalibracion;
    }

    public static void maina(String[] args) throws Exception {
        try {
            RegVefCalibraciones r = new RegVefCalibraciones();
            PuntoCalibracion pto1 = new PuntoCalibracion((short) 0, (short) 404, (short) 60, (short) 303);//por perezoso y no castear en el constructor
            PuntoCalibracion pto2 = new PuntoCalibracion((short) 0, (short) 1199, (short) 162, (short) 1196);//por perezoso y no castear en el constructor
            BancoGasolina banco = null;
            r.registrarResultadoCal2Ptos(false, pto1, pto2, "62824", 500, banco, 0);
            r.registrarPruebaLinelidad("62824", false, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            r.necesitaLinealidad(0L);
            r.registrarPruebaLinelidad("62824", true, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            r.necesitaCalibracionDosPuntos("62824");
            r.necesitaFugas("62824", null);
            r.registrarResultadoCal2Ptos(true, pto1, pto2, "62824", 500, banco, 0);
            r.necesitaCalibracionDosPuntos("62824");
        } catch (SQLException | ClassNotFoundException | IOException ex) {
            Mensajes.mostrarExcepcion(ex);
        }
    }
}
