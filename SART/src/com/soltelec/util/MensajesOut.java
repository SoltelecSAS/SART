/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.util;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;

/**
 *
 * @author  Gerencia Desarrollo de Soluciones Tecnologicas
 */
public class MensajesOut {
    
    private static boolean isRun;
    private static Timer timer;
    private static TimerTask task;
    
    public static void mensajeError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void mensajeAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "SART 1.7.3", JOptionPane.WARNING_MESSAGE);
    }
    
    public static void mensajeCorrecto(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "SART 1.7.3", JOptionPane.INFORMATION_MESSAGE);
    }
    public static void mensajeInformacion(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "SART 1.7.3", JOptionPane.YES_OPTION);       
    }
    
    
    public static void mostrarExcepcion(Exception excepcion) {
        excepcion.printStackTrace(System.err);
        org.apache.log4j.Logger.getRootLogger().error(excepcion);
        //JOptionPane.showMessageDialog(null, "Error: " + excepcion.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }
    
    public static boolean mensajePregunta(String mensaje) {
        boolean estado = true;
        int i = JOptionPane.showOptionDialog(null, mensaje, "SART 1.7.3", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
        
        if (i != JOptionPane.YES_OPTION) {
            estado = false;
        }
        
        return estado;
    }
    
    /**
     * Metodo para mostrar un mensaje de Advertencia visible solo por un tiempo determinado
     * @param message mensaje que se mostrara en pantalla.
     * @param segundos tiempo en segundos que durara el mensaje en pantalla.
     */
    public static void messageWarningTime(String message, int segundos) {
        startTimer(segundos);
        mensajeAdvertencia(message);
        
        if (isRun) {
            stopTimer();
        }
    }
    
    /**
     * Metodo para mostrar un mensaje de Advertencia visible solo por un tiempo determinado
     * @param message mensaje que se mostrara en pantalla.
     * @param segundos tiempo en segundos que durara el mensaje en pantalla.
     */
    public static void messageErrorTime(String message, int segundos) {
        startTimer(segundos);
        mensajeError(message);
        
        if (isRun) {
            stopTimer();
        }
    }
    
    /**
     * Metodo para mostrar un mensaje de Advertencia visible solo por un tiempo determinado
     * @param message mensaje que se mostrara en pantalla.
     * @param segundos tiempo en segundos que durara el mensaje en pantalla.
     */
    public static void messageDoneTime(String message, int segundos) {
        startTimer(segundos);
        mensajeCorrecto(message);
        
        if (isRun) {
            stopTimer();
        }
    }
    
    /**
     * Metodo para administrar el tiempo que durara un Mensaje
     * @param duracion parametro para asignar el tiempo de duracion en segundos.
     */
    private static void startTimer(final int duracion) {
        isRun = true;
        timer = new Timer();
        
        task = new TimerTask() {
            int contador = 0;
                    
            @Override
            public void run() {
                contador++;
                
                if (contador == duracion) {
                    try {
                        Robot robot = new Robot();
                        robot.keyPress(KeyEvent.VK_ESCAPE);
                    } catch (AWTException ex) {
                        ex.printStackTrace(System.err);
                    }
                    stopTimer();
                }
            }
        };
        timer.schedule(task, 100, 1000);
    }
    
    /**
     * Metodo para terminar el temporizador.
     */
    private static void stopTimer() {
        isRun = false;
        timer.cancel();
        task.cancel();
    }
}
