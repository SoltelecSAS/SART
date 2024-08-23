/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.util.List;

/**
 * Interfaz para toda prueba requerida como Frenos, Suspension o Desviacion
 * entre otras
 * @author GerenciaDesarrollo
 */
public interface PruebaDefault {
    
    /**
     * Metodo para obtener la lista de los defectos encontrados
     * en la preuba.
     * @return
     */
    public List<Integer> getDefectos();
    
    /**
     * Metodo para obtener el estado de la prueba Y = Aprobada, N = No Aprobada
     * @return 
     */
    public String getAprobada();
    
    /**
     * Metodo para llenar los tipos de las medidas correspondientes
     * a los valores medidos en la prueba, mantener el mismo orden con el que se llena
     * getValoresMedida()
     * @return una lista con los tipod de medida
     */
    public List<Integer> getTiposMedida();
    
    /**
     * Metodod para llenar los valores de los campos de la prueba,
     * cada valor tiene un tipo de medida el cual lo identifica en 
     * la base de datos, mantener el mismo orden con el que se llena
     * getTiposMedida()
     * @return
     */
    public List<Double> getValoresMedida();
    
    /**
     * Metodo para verificar los defectos y determina el estado de la prueba. 
     */
    public void verificarDefectos();
    
    /**
     * Metodo para imprimir los valores en la consola de ejecucion
     */
    public Double verifPesoVacioXPista();
    
    Boolean verifResolMedDesv(Double medida,Integer resolMin,Integer resolMax);
    public void imprimirValores();
    
    /**
     * Indica si se debe repetir la prueba
     * @return 
     */
    
    public boolean isRepetirPrueba();
    
    
}
