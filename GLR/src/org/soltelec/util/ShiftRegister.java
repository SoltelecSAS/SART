/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.util;

import java.util.ArrayList;
import java.util.Random;
/**
 *Clase par implementar un registro de desplazamiento para 
 * detección de cambios en señales del sonometro y aceleración gobernada *
 * @author Usuario
 */

public class ShiftRegister {
    private double[] arrayList;//un arraylist que funcione como backup para la lista o un arreglo ??
    private int tamanio;//tamaño del shift Register
    private int ultimoIndice = 0;

    public ShiftRegister(){
        arrayList = new double[10];
        tamanio = 10;
    }

    public ShiftRegister(int size,double valorInicial){
        tamanio = size;
        arrayList = new double[size];
        for(int i = 0; i < arrayList.length;i++){
            arrayList[i] = valorInicial;
        }
    }

    public void ponerElemento(double d){//pone el elemento en la ultima posicion debo llevar registro de la ultima posición
     //quitar el elemento de la posicion 0
         if(ultimoIndice == 0){//es el primer elemento que se posiciona en la lista
            arrayList[ultimoIndice] = d;
            ultimoIndice++;
            //no hay corrimiento hacia la derecha
        }//end if e
        else if(ultimoIndice < tamanio  ){
            arrayList[ultimoIndice] = d;
            ultimoIndice++;
        } else if(ultimoIndice == tamanio ){
            correrHaciaIzquierda();
            arrayList[ultimoIndice - 1] = d;
        }
    }//end of addElement method

    /**
     * Metodo que corre hacia la izquierda todos los elementos y saca el
     * elemento que este en la primera posicion     *
     */
    private void correrHaciaIzquierda() {
        if(ultimoIndice < tamanio -1)
        return;

        else if(ultimoIndice == tamanio ){
            for(int l = 0; l < tamanio- 1; l++){
                arrayList[l] = arrayList[l+1];
        }
        }//end else if
    }//end of method correrHaciaIzquierda


    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int i= 0 ; i < ultimoIndice  ; i++ )
            sb.append(arrayList[i]).append("\t");
        return sb.toString();
    }

    public int getSize() {
        return tamanio;
    }

    public double media(){
        if(ultimoIndice == 0){
            return 0;
        }else {
        double suma = 0;
        for(double d: arrayList)
            suma += d;
        return suma/ultimoIndice;
        }
    }

    public double desviacionEstandar(){
        double sumaCuadrados = 0;
        double media = media();
        for(int q = 0; q < ultimoIndice ; q++)
            sumaCuadrados +=Math.pow(( media - arrayList[q]),2);
        double temporal = sumaCuadrados/ultimoIndice;
        return Math.sqrt(temporal);
    }

    public boolean isStable(){
        //double media = media();
        double desviacionEstandar = desviacionEstandar();
        if(desviacionEstandar < 0.5){
            return true;
        }
        else
            return false;
    }


    public boolean isAcelerando(){
        double mediaMitadInferior = mediaMitadInferior();
        System.out.println("MEDIA INFERIOR: " +mediaMitadInferior );
        double mediaMitadSuperior = mediaMitadSuperior();
        System.out.println("MEDIA SUPERIOR: " + mediaMitadSuperior);


        if(mediaMitadSuperior > mediaMitadInferior)
            if(!this.isStable())
                return true;
            else
                return false;
        else
            return false;
    }

    public static void main(String args[]){
        ShiftRegister sr = new ShiftRegister(15,100);
        Random r = new Random();
        int temp = 0;
        for(int w = 0; w < sr.getSize()*2 ; w++){
            temp = 120 + (10 - r.nextInt(20));
            
            System.out.println("Añadiendo : " + w);
            sr.ponerElemento(temp);
            System.out.println("Lista " + sr);
             System.out.println("Media:" + sr.media());
             System.out.println("Desviacion Estandar:" + sr.desviacionEstandar() );
            System.out.println("Estable: " + sr.isStable());

        }
        System.out.println("Media:" + sr.media());
        System.out.println("Desviacion Estandar:" + sr.desviacionEstandar() );

    }

    private double mediaMitadInferior() {
       if(ultimoIndice > 4){
        double suma = 0;
        for( int m = 0; m < ultimoIndice / 2; m++){
            suma+= arrayList[m];
        }
        return suma/((int)(ultimoIndice / 2));
        }
        else
            return -100;
    }//


    private double mediaMitadSuperior() {
    if(ultimoIndice > 4){
        double suma = 0;
            for( int m = ultimoIndice/2; m < ultimoIndice ; m++){
                suma+= arrayList[m];
            }
            return suma/((int)(ultimoIndice * 0.5));
    }//end if
    else {
        return -100;
    }
    }//end mehod


public double obtenerUltimo(){
    if(ultimoIndice < tamanio)
    return arrayList[ultimoIndice];
    else
        return arrayList[tamanio-1];
}

}//end of class ShiftRegister
