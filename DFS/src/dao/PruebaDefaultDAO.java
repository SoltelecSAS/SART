/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import com.soltelec.modulopuc.utilidades.Mensajes;
import excepciones.NoPersistException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                String ctxPista = null;
                if (pesoVacio > 3500) 
                {
                   /* if (tipoVehiculo.equalsIgnoreCase("Liviano") || tipoVehiculo.equalsIgnoreCase("Taxis_AplTaximetro") || tipoVehiculo.equalsIgnoreCase("Taxis") || tipoVehiculo.equalsIgnoreCase("4x4")) {
                        Mensajes.messageDoneTime("Se le informa que este vehiculo fue autorizado como " + tipoVehiculo + " y su peso vacio fue: " + Math.round(pesoVacio), 7);
                        String campos = "observaciones";
                        String valores = "ABORTADA; este Vehiculo FUE AUTORIZADO como " + tipoVehiculo + " Y su peso vacio fue " + Math.round(pesoVacio) + " Kg. por lo tanto la prueba ha sido ANULADA por incongruencia entre el peso vacio Registrado y el Tipo Vehiculo ..!";
                        String condicion = "Id_Pruebas =" + idPrueba;
                        try {
                            DBUtil.updateSingle("db_cda.pruebas", campos, valores, condicion);
                        } catch (SQLException ex) {
                            Logger.getLogger(PruebaDefaultDAO.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        Mensajes.messageDoneTime(" SE LE INFORMA QUE LA PRUEBA HA SIDO ANULADA POR INCONGRUENCIA ENTRE EL PESO VACIO Y EL TIPO VEHICULO ", 7);
                        return true;
                    }*/
                } else {
                    
                }
            }
        }
       
        System.out.println("VOY A GUARDAR MEDIDAS ..");
        repetirPrueba = medidasDAO.guardarMedidas(prueba, idPrueba, lugarTomaDatos);
         System.out.println("YA DEBI  GUARDAR MEDIDAS ..!");
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
        
        Mensajes.messageDoneTime("Se ha Registrado la Prueba de " + tipoPrueba + " de una manera Exitosa ..¡", 3);
        return false;
    }
}