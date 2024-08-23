/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.igrafica.labrado.presion;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author luisberna
 */
public class ChangeCheckLabrado implements ItemListener {

    private final JTextField check;

    public ChangeCheckLabrado(JTextField check) {
        this.check = check;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            check.setEnabled(true);
        }else{
            check.setEnabled(false);
        }
    }

}
