/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.sonometro;

/**
 *
 * @author GerenciaDesarrollo
 */
public interface Sonometro {
    
    String XTECH = "XTECH";
    String PCE322A = "PCE322A";
    String PCE322ASerial = "PCE322ASerial";
    String H55633T = "H55633T";

    double getDecibeles();
    void stop();
    
}
