/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.andros;

import java.util.ArrayList;
import java.util.List;

/**
 *Clase para encapsular la logica de un mensaje Andros y abstraerla
 * de las tramas de bytes.
 * @author User
 */
public class MensajeAndros {
    
    public  int  DIDNACK = 0x02;//mejor dicho el primer byte de la trama del comando o de la trama de la respuesta
    
    private int numeroBytes;
    
    private int comando;
    
    private int checksum;
    
    private List<Byte> listaDatos;

    public MensajeAndros(int comando) {
        this.comando = comando;
        this.listaDatos = new ArrayList<Byte>();
        this.numeroBytes = 1 + listaDatos.size();
        this.checksum = obtenerCheckSum();
    }

    public MensajeAndros(int comando, List<Byte> listaDatos) {
        this.comando = comando;
        this.listaDatos = listaDatos;
        this.checksum = obtenerCheckSum();
    }
    
    public final  int obtenerCheckSum(){
        
        int sumaLista = 0;
        
        
        for(int dato: listaDatos){
            
            sumaLista = sumaLista + dato;
            
        }
        
        int sumaTodo = DIDNACK + numeroBytes + comando + sumaLista;
        sumaTodo = -sumaTodo;
        sumaTodo = sumaTodo +1;
        
        return sumaTodo;
        
    }

    public int getComando() {
        return comando;
    }

    public void setComando(int comando) {
        this.comando = comando;
    }

    public List<Byte> getListaDatos() {
        if(listaDatos == null)
            listaDatos = new ArrayList<Byte>();
        return listaDatos;
    }

    public void setListaDatos(List<Byte> listaDatos) {
        this.listaDatos = listaDatos;
    }

    public int getNumeroBytes() {
        return numeroBytes;
    }

    public void setNumeroBytes(int numeroBytes) {
        this.numeroBytes = numeroBytes;
    }

    public int getDIDNACK() {
        return DIDNACK;
    }

    public void setDIDNACK(int DIDNACK) {
        this.DIDNACK = DIDNACK;
    }
    
    public void recalcularChecksum(){
        
        this.checksum = obtenerCheckSum();
        
    }
}
