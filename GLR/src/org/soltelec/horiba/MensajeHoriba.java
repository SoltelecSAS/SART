/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.horiba;

/**
 *Como no tenemos manual.. vamos a hacer esto como un conjunto de constantes
 * @author User
 */
public class MensajeHoriba {
    
    
    public static final byte[] tramaSerial = new byte[]{ 0x10,0x01,0x04, -21};
    public static final byte[] tramaPEF = new byte[]{ 0x01,0x03,24,0,0,-28 };
    
    public static final byte[] tramaBombaOn = new byte[]{-128,03,-91,0x40,0x40,0x58};
    public static final byte[] tramaBombaOff = {-128,03,-91,0x40,0x00,-104};
    
    public static final byte[] tramaSolenoideUnoOn = {-128,03,-92,1,1,-41};
    public static final byte[] tramaSolenoideUnoOff = {-128,03,-92,1,0,-40};
    
    public static final byte[] tramaSonenoideDosOn = {-128,03,-92,2,2,-43};
    public static final byte[] tramaSolenoideDosOff = {-128,03,-92,2,0,-41};
    
    public static final byte[] tramaCalSol1On = {-128,3,-92,04,04,-47};
    public static final byte[] tramaCalSolOff = {-128,3,-92,4,0,-43};
    
    public static final byte[] tramaCalSol2On = {-128,3,-92,8,8,-55};
    public static final byte[] tramaCalSol2Off = {-128,3,-92,8,0,-47};
    
    
    public static final byte[] tramaDrainPumpOn = {-128,3,-91,-128,-128,-40};
    public static final byte[] tramaDrainPumpOff = {-128,3,-91,-128,0,88};
    
    public static final byte[] tramaDatos = { 0x10,0x01,0x40,-81};
    
    public static final byte[] tramaStatusAnalizador = {0x10,0x01,0x01,-18};
    
    public static final byte[] tramaStatusSistema = {-128,1,-86,-43};
    
    public static final byte[] tramaCero = {-128,1,(byte)161,(byte)222};
    
}
