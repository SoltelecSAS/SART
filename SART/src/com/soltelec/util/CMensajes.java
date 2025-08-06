/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.util;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

/**
 *
 * @author Soltelec Ltda
 */
public class CMensajes {
    
    public static void mensajeError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void mensajeAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }
    
    public static void mensajeCorrecto(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Exito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void mostrarExcepcion(Exception excepcion) {
        JOptionPane.showMessageDialog(null, "Error: " + excepcion.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        excepcion.printStackTrace(System.err);
    }
    
    public static boolean mensajePregunta(String mensaje) {
        boolean estado = true;
        int i = JOptionPane.showOptionDialog(null, mensaje, "Advertencia", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
        
        if (i != JOptionPane.YES_OPTION) {
            estado = false;
        }
        
        return estado;
    }

    public static void mensajeTemporal(String mensaje, int segundos) {
        final JDialog dialogo = new JDialog();
        dialogo.setTitle("Mensaje");
        dialogo.setModal(false);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JLabel label = new JLabel(mensaje, SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        dialogo.getContentPane().add(label);
        dialogo.pack();
        dialogo.setLocationRelativeTo(null); // Centrar en pantalla

        // Crear un temporizador para cerrarlo después de 'segundos'
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                dialogo.dispose();
            }
        }, segundos * 1000); // convertir a milisegundos

        dialogo.setVisible(true);
    }
}
