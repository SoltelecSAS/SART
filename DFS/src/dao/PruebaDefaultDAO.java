/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import com.soltelec.modulopuc.utilidades.Mensajes;

import Utilidades.Utilidades2;
import excepciones.NoPersistException;
import java.util.List;

import modelo.*;

/**
 *
 * @author GerenciaDesarrollo
 */
public class PruebaDefaultDAO {

    private MedidasDAO medidasDAO;
    private PruebasDAO pruebasDAO;
    public static String escrTrans = "*";
    public static String serialEquipo = "";

    public boolean persist(PruebaDefault prueba, int idPrueba, int idUsuario, int aplicTrans, String ipEquipo,String tipoPista, String tipoVehiculo, String placa, String lugarTomaDatos) throws NoPersistException, ClassNotFoundException 
    {
        medidasDAO = new MedidasDAO();
        pruebasDAO = new PruebasDAO();
        boolean repetirPrueba;
        if (prueba instanceof Desviacion) { }
        
        if (prueba instanceof Frenos || prueba instanceof Suspension || prueba instanceof FrenoMotoCarro) 
        {
            Double pesoVacio = prueba.verifPesoVacioXPista();
            System.out.println("SUM PESO VACIO.." + pesoVacio);
            if (tipoPista.equalsIgnoreCase("MIXTA"))
            {
                if (pesoVacio > 3500) 
                {
                } else {
                    
                }
            }
        }
       
        System.out.println("VOY A GUARDAR MEDIDAS ..");
        repetirPrueba = medidasDAO.guardarMedidas(prueba, idPrueba, lugarTomaDatos);
         System.out.println("YA DEBI  GUARDAR MEDIDAS para la placa:  "+placa+"  ..! CON el idPrueba: "+idPrueba);
         List<Integer> lstDef = prueba.getDefectos(placa);
        boolean escTran = true;
        if (aplicTrans == 1 && prueba.getAprobada().equalsIgnoreCase("N")) 
        {
            escTran = false;
        }
        if (repetirPrueba) 
        {
            return true;
        }
        if (repetirPrueba) 
        {
            return true;
        }
        if (escTran == true)
        {
            System.out.println("Entro en la Condic Busq Defectos dado que voy a escribir trans");                   
                if(lstDef.size()>0){
                System.out.println("lista de defectos size>0-----> "+lstDef.size() );               
                    System.out.println("get defectos es diferente de null, entro a insertar defectos");
                pruebasDAO.guardarDefectos(prueba.getDefectos(tipoVehiculo), idPrueba); 
                }           
        }
        pruebasDAO.finalizarPrueba(idPrueba, idUsuario, prueba.getAprobada(), escTran, ipEquipo);
        if (escTran == true) {
            PruebaDefaultDAO.escrTrans = "E";
        } else {
            PruebaDefaultDAO.escrTrans = "A";
        }
        System.out.println("tome  co de transaccion es " + PruebaDefaultDAO.escrTrans);
        String tipoPrueba = null;
        if (prueba instanceof Frenos) {
            tipoPrueba = "Frenos";
        }
        if (prueba instanceof Desviacion) {
            tipoPrueba = "Desviacion";
        }
        if (prueba instanceof Suspension) {
            tipoPrueba = "Suspension";
        }

        System.out.println("Tipo de vehiculo: "+tipoVehiculo);

        
        Mensajes.messageDoneTime("Se ha Registrado la Prueba de " + tipoPrueba + " de una manera Exitosa ..¡", 3);
        return false;
    }
}