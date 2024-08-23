/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.eventos;

import com.soltelec.eventos.igrafica.EventoCDA;
import com.soltelec.eventos.igrafica.Evento_Datos;
import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDesktopPane;

/**
 *
 * @author SOLTELEC
 */
public class Eventos {

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       
        String usuario="Soltelec";
        Evento_Datos eventDatos=new Evento_Datos(usuario);
        eventDatos.setVisible(true);    
        
    }
    
}
