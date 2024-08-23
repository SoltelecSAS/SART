/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.medicionrpm;

import gnu.io.SerialPort;

/**
 *Interface para proporcionar una solucion polimorfica a
 * la medicion de Revoluciones y temperatura usando
 * distintos dispositivos.
 * @author Usuario
 */
public interface MedidorRevTemp {

    
    public abstract int getRpm() throws ArrayIndexOutOfBoundsException;
    public abstract int getTemp() throws ArrayIndexOutOfBoundsException;
    public SerialPort getPuertoSerial();
    public void setPuertoSerial(SerialPort port);
    public void stop();
    public abstract void setRpm(Integer rpm);

}
