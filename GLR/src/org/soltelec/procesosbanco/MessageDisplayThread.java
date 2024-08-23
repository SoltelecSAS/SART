/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosbanco;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author user
 */
public class MessageDisplayThread extends Thread {
    @Override
    public void run() {
        while (true) {
            // Display the message every second
            try {
                Thread.sleep(1000); // Wait for 1 second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SwingUtilities.invokeLater(() -> {
                JTextField textField = new JTextField("Message");
                textField.setEditable(false);
                JOptionPane.showMessageDialog(null, textField, "Mensaje", JOptionPane.INFORMATION_MESSAGE);
            });
        }
    }
}
