/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.util;


/**
 *Clase que representa un mensaje general de la máquina
 * alto nivel usando enums en vez de constantes
 * @author Usuario
 */
public class Mensaje {
    private static final short PROCESS_STATUS = 11;
    private static final short MODO_NORMAL = 0;
    private ComandosGases comando;
    private ModosComando modoDelComando;
    private short codigoComando, numModoComando, numeroDeDatos,checksum;
    private short datos[];//Decisión de diseño: hacerlo como una lista?

    //Constructor por defecto mensaje PROCESS_STATUS
    public Mensaje() {
        this.comando = ComandosGases.PROCESS_STATUS;//compatibilidad con uso de enums
        this.codigoComando = PROCESS_STATUS;
        this.modoDelComando =  ModosComando.NORMAL;//compatibilidad con uso de enums
        this.numModoComando = MODO_NORMAL;
        this.numeroDeDatos = 0;
        this.datos = null;
        this.checksum = 11;
    }




    public Mensaje(short codigoComando, short modoDeComando, short numeroDeDatos, short[] datos) {
        this.codigoComando = codigoComando;
        this.numModoComando = modoDeComando;
        this.numeroDeDatos = numeroDeDatos;
        this.datos = datos;
        this.checksum = Mensaje.obtenerCheckSum(codigoComando,modoDeComando,
                                            numeroDeDatos,datos);
    }//end of constructor

    public Mensaje(int codigoCom,int modoCom, int numDatos){
        this.codigoComando = (short)codigoCom;
        this.numModoComando = (short)modoCom;
        this.numeroDeDatos = (short)numDatos;
        this.datos = null;
        this.checksum = obtenerCheckSum(codigoComando, numModoComando, numeroDeDatos, datos);
    }

    public Mensaje(int codigoComando, int numModoComando, int numeroDeDatos, short[] datos) {
        this.codigoComando = (short)codigoComando;
        this.numModoComando = (short)numModoComando;
        this.numeroDeDatos = (short)numeroDeDatos;
        this.datos = datos;
        this.checksum = obtenerCheckSum(this.codigoComando, this.numModoComando, this.numeroDeDatos,datos);
    }

        public static short obtenerCheckSum(short codComando,short numModo, short numDatos, short[] datos){
        //lo pongo en int y despues lo meto en un short
        int resultado = 0;
        int intermedio = 0;
        resultado = (codComando + numModo);
        resultado += numDatos&0xFF + numDatos/0x100;

         if(numDatos > 0 && datos != null){
         for (short dato : datos){

             //pasar correctamente de short a int
             String shortBinario = Integer.toString(dato&0xFFFF, 2);
             int shortInt = Integer.parseInt(shortBinario,2);
            intermedio += (shortInt & 0xFF) + (shortInt/0x100);

                }//end for
         }//end if
        resultado += intermedio%0x10000;
        return (short)resultado;
    }//end obtenerCheckSum

    public short getChecksum() {
        return checksum;
    }

    public void setChecksum(short checksum) {
        this.checksum = checksum;
    }

    public short getCodigoComando() {
        return codigoComando;
    }

    public void setCodigoComando(short codigoComando) {
        this.codigoComando = codigoComando;
        this.checksum = obtenerCheckSum(getCodigoComando(), getNumModoComando(), getNumeroDeDatos(), getDatos());
    }

    public ComandosGases getComando() {
        return comando;
    }

    public void setComando(ComandosGases comando) {
        this.comando = comando;
        this.checksum = obtenerCheckSum(getCodigoComando(), getNumModoComando(), getNumeroDeDatos(), getDatos());
    }

    public short[] getDatos() {
        return datos;
    }

    public void setDatos(short[] datos) {
        this.datos = datos;
        this.checksum = obtenerCheckSum(getCodigoComando(), getNumModoComando(), getNumeroDeDatos(), getDatos());
    }

    public ModosComando getModoDelComando() {
        return modoDelComando;
    }

    public void setModoDelComando(ModosComando modoDelComando) {
        this.modoDelComando = modoDelComando;
        this.checksum = obtenerCheckSum(getCodigoComando(), getNumModoComando(), getNumeroDeDatos(), getDatos());
    }

    public short getNumModoComando() {
        return numModoComando;
    }

    public void setNumModoComando(short numModoComando) {
        this.numModoComando = numModoComando;
        this.checksum = obtenerCheckSum(getCodigoComando(), getNumModoComando(), getNumeroDeDatos(), getDatos());
    }

    public short getNumeroDeDatos() {
        return numeroDeDatos;
    }

    public void setNumeroDeDatos(short numeroDeDatos) {
        this.numeroDeDatos = numeroDeDatos;
        this.checksum = obtenerCheckSum(getCodigoComando(), getNumModoComando(), getNumeroDeDatos(), getDatos());
    }

    @Override
    public String toString(){

        /*String cadenaDatos = "" ;
        if(getDatos() != null){
            for (short dato : this.getDatos()){
                cadenaDatos = cadenaDatos + dato +'\t';
            }//end for
        }//end if
        return String.format("codComando: %d \n modoComand: %d \n numDatos:"
                + "%d" + "\ndatos:" + cadenaDatos + "\nchksum : %d\n" ,
                getCodigoComando(),getNumModoComando(),getNumeroDeDatos(),getChecksum());*/
        return "";
    }
}//end of class Mensaje
