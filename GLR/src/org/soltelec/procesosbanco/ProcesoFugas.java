/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosbanco;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.soltelec.util.MedicionGases;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import javax.swing.*;
import org.soltelec.pruebasgases.EscribirFechas;
import org.soltelec.util.Conex;

/**
 * Clase que representa el proceso de Fugas, considero bastante esencial
 * interrumpir el hilo que obtiene datos 10 segundos de vacio es suficiente
 *
 * @author Usuario
 */
public class ProcesoFugas implements ActionListener, Runnable {

    private PanelProgreso panelProgreso;
    private Timer timer;
    private BancoGasolina banco;
    private JButton botonHabilitar;
    private WorkerDatos worker;///cuestión de sincronizacion
    private EscribirFechas escribirFechas = new EscribirFechas();
    int contadorTimer = 0;
    PuntoCalibracion ptoCalibracion;
    int muestra = 1;
    private String urljdbc;
    private boolean aprobada = false;
    private int idUsuario = 1;//voya necesitar un usuario 

    public ProcesoFugas() {

    }

    public ProcesoFugas(PanelProgreso panelProgreso) {
        this.panelProgreso = panelProgreso;
    }

    public ProcesoFugas(PanelProgreso thePanelProgreso, JButton boton) {//propositos de depuracion
        this.panelProgreso = thePanelProgreso;
        this.botonHabilitar = boton;
    }
    //constructor para interfaz grafica

    public ProcesoFugas(PanelProgreso thePanelProgreso, BancoGasolina theBanco, WorkerDatos theWorker) {
        this.panelProgreso = thePanelProgreso;
        this.banco = theBanco;
        this.worker = theWorker;
    }

    public void run() {
        System.out.print("Entro a verificar");
        if(!banco.statusMaquina().isCalentamiento()){
            iniciarProceso();
        }else{
        JOptionPane.showMessageDialog(null, "Banco en calentamiento. Por favor espere");
        
        }
        
    }

    public void iniciarProceso() {
        if (worker != null) {
            worker.setInterrumpido(true);
        }
        cargarUrl();
        MedicionGases medicion;
        panelProgreso.getLabelMensaje().setText("Generando Vacio.....");
        //Obtiene y guarda el Estado del mapa fisico
        short mapa = banco.mapaFisicoES((short) 128, false);
        panelProgreso.getBarraTiempo().setMaximum(20);
        timer = new Timer(1000, (ActionListener) this);
        timer.start();
        //Obtener el mapa actual de bombas y guardarlo
        /**
         * short mapa = banco.mapaFisicoES((short)128, false);
         */
        //Apagar el CalSol1 y el CalSol2, apagar el Solenoide 1 y el solenoide 2
        //prende la bomba de muestra y apaga la bomba de Drenaje palabra : 00010000
        banco.mapaFisicoES((short) 16, true);
        //banco.encenderSolenoideUno(true);
        while (contadorTimer < 11) { //10 segundos de vacio
            try {
                //System.out.println("Contando .." + contadorTimer);
                panelProgreso.getBarraTiempo().setValue(contadorTimer);
                Thread.sleep(120);
            }//end try
            catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }//end while
        //apagar la bomba de muestras y mantener cerrado todo los demas
        banco.mapaFisicoES((short) 0, true);

        //Vacio Generado verificar la bandera de bajoFlujo
        //Interrumpir el hilo de datos
        //???????????????????????????????????????????????
        medicion = banco.obtenerDatos();
        while (contadorTimer < 21 && (medicion.isBajoFlujo() || medicion.isVacuumSwitch())) { //repetir durante 20 segundos
            panelProgreso.getLabelMensaje().setText("El vacio esta manteniendose");
            panelProgreso.getBarraTiempo().setValue(contadorTimer);
            medicion = banco.obtenerDatos();
        }
        //no cuenta mas
        if (contadorTimer < 21) {
            panelProgreso.getLabelMensaje().setText("Fallo en la prueba no hay vacio en el banco");
            timer.stop();
            aprobada = false;
        } else {
            panelProgreso.getLabelMensaje().setText("Prueba OK");
            aprobada = true;
        }
        timer.stop();
        banco.mapaFisicoES(mapa, true);
        escribirFechas.establecerFechasPruebas("fugas");

        if (worker != null) {
            try {
                mostrarUltimaFecha();
                registrarResultado();

            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(panelProgreso, "No se encuentra el driver de Mysql");
                ex.printStackTrace(System.err);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panelProgreso, "Error insertando los datos");
                ex.printStackTrace(System.err);
            }
        }
        worker.setInterrumpido(false);
    }//end of method proceso

    public void actionPerformed(ActionEvent e) {//timer se cumple, incrementar el contador
        contadorTimer++;
    }//

    public static void main(String args[]) {
        PanelProgreso progreso = new PanelProgreso();
        ProcesoFugas proceso = new ProcesoFugas(progreso);
        JFrame app = new JFrame();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setSize(650, 650);
        app.add(progreso);
        (new Thread(proceso)).start();
        app.setVisible(true);
    }

    private void mostrarUltimaFecha() throws ClassNotFoundException, SQLException {

        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        Connection conexion = Conex.getConnection();
        //Insertar las medidas dependiendo del numero de tiempos del motor
        String statement = "SELECT calibraciones.CURDATE  FROM calibraciones WHERE calibraciones.DESCRIPTION LIKE '%gas Aprobada' ORDER BY CURDATE desc LIMIT 1";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        //Date d = new Date();
        //Calendar c = new GregorianCalendar();
        //c.setTime(d);
        //instruccion.setDate(3,new java.sql.Date(c.getTimeInMillis()));
        ResultSet executeQuery = instruccion.executeQuery();
        boolean existe = executeQuery.first();
        if (!existe) {
            System.out.println("No hay ultima fecha");
            return;
        }
        Timestamp timestamp = executeQuery.getTimestamp("CURDATE");
        //
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp.getTime());
        Date d1 = new Date();
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        c1.add(Calendar.DATE, -1);

        if (c.compareTo(c1) < 1) {
            JOptionPane.showMessageDialog(null, "NecesitaFugas");
        }
        System.out.println(executeQuery.getTimestamp(1));
        if (executeQuery.wasNull()) {
            System.out.println("No hay ultima fecha");
        } else {
            conexion.close();
        }
    }//end of method mostrarUltima Fecha

    private void registrarResultado() throws ClassNotFoundException, SQLException {

        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        Connection conexion = Conex.getConnection();
        //Insertar las medidas dependiendo del numero de tiempos del motor
        String statement = "INSERT INTO calibraciones(TESTTYPE,GEUSER,DESCRIPTION,CURDATE) VALUES(8,?,?,?) ";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        instruccion.setInt(1, idUsuario);
        if (aprobada) {
            instruccion.setString(2, "Prueba Fugas Aprobada");
        } else {
            instruccion.setString(2, "Pruega Fugas Reprobada");
        }
        Date d = new Date();
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        instruccion.setDate(3, new java.sql.Date(c.getTimeInMillis()));
        instruccion.executeUpdate();
        conexion.close();
    }//End of method registrarResultado

    private void cargarUrl() {
        Properties props = new Properties();
        //try retrieve data from file
        try {
            props.load(new FileInputStream("./propiedades.properties"));//TODO warining
            urljdbc = props.getProperty("urljdbc");
            System.out.println(urljdbc);
        }//catch exception in case properties file does not exist
        catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error cargando la url desde el archivo");
            e.printStackTrace();
        }
    }//end of method cargarUrl
}
