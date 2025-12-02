/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.servidor.utils;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.util.concurrent.CountDownLatch;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

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
        final CountDownLatch latch = new CountDownLatch(1);

        SwingUtilities.invokeLater(() -> {
            final JDialog dialogo = new JDialog();
            dialogo.setTitle("Mensaje");
            dialogo.setModal(true);
            dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            JLabel label = new JLabel(mensaje, SwingConstants.CENTER);
            label.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
            dialogo.getContentPane().add(label);
            dialogo.pack();
            dialogo.setLocationRelativeTo(null);

            // Usar javax.swing.Timer para asegurar ejecución en EDT
            new javax.swing.Timer(segundos * 1000, e -> {
                dialogo.dispose();
                latch.countDown();
            }) {{
                setRepeats(false);
                start();
            }};

            dialogo.setVisible(true);
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void mensajeTemporal(Component parent, String mensaje, int segundos) {
        // Crear el diálogo
        final JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(parent),
                "Mensaje",
                Dialog.ModalityType.MODELESS
        );

        // Configurar el contenido
        JLabel label = new JLabel(mensaje, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        dialog.add(label);

        // Apariencia
        dialog.setUndecorated(true); // sin bordes ni botones
        dialog.pack();
        dialog.setLocationRelativeTo(parent); // centrar sobre la ventana principal
        dialog.setAlwaysOnTop(true);

        // Mostrar
        dialog.setVisible(true);

        // Crear un temporizador para cerrarlo después de X segundos
        Timer timer = new Timer(segundos * 1000, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();
    }
}
