/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import com.soltelec.modulopuc.persistencia.conexion.DBUtil;
import excepciones.NoPersistException;
import java.sql.SQLException;
import java.util.List;
import modelo.Desviacion;
import modelo.Medidas;
import modelo.PruebaDefault;

/**
 *
 * @author Gerencia TIC
 */
public class MedidasDAO {

    /*
     * Metodo para guarda una medida
     * 
     * @param idTipoPrueba id del tipo de prueba al que corresponde la medida
     * @param valorMedida el valor de la medida a guardar
     * @param idPrueba el id de la prueba a la cual hace referencia
     */
     public boolean guardarMedidas(PruebaDefault prueba, int idPrueba, String lugarTomaDatos) throws NoPersistException 
     {


        System.out.println("---------------------------------------------------");
        System.out.println("------------------Guardar Medidas desde "+lugarTomaDatos+"------------------");
        System.out.println("---------------------------------------------------");

        String campos = "(" + Medidas.ID_TIPO_MEDIDA + "," + Medidas.VALOR_MEDIDA + "," + Medidas.ID_PRUEBA + ")";
        String valores = "";
        List<Integer> tiposMedida = prueba.getTiposMedida();
        List<Double> valoresMedida = prueba.getValoresMedida();
        System.out.println("Lonfirud List tipoMedida : " + tiposMedida.size());
        System.out.println("Lonfirud List valoresMedida : " + valoresMedida.size());
        
        if (prueba.isRepetirPrueba()) 
        {
            return true;
        }
        if (prueba instanceof Desviacion)
        {
             for (int i = 0; i < valoresMedida.size(); i++) {
                Boolean verifica = prueba.verifResolMedDesv(valoresMedida.get(i),0 ,0);
//                if(valoresMedida.get(i) >= 43.0 && valoresMedida.get(i) <= 45.0){
//                    valoresMedida.set(i, 0.0);
//                    System.out.println("-----------------------------------------------prueba desviacion cero: " + valoresMedida.get(i));
//                }
                if(verifica==false){
                     return true;
                }
             }
         }
        System.out.println("--------------------------------------");
        System.out.println("--PRUEBA SUSPENSION 12/04/2021--------");
        System.out.println("--------------------------------------");
        
        System.out.println("Numero tipos de medida: "+tiposMedida.size());
        for (int i = 0; i < valoresMedida.size(); i++) 
        {        
            System.out.println("--------------------------------------------------------------");    
            System.out.println("ciclo "+i);
            System.out.println("tipo de medida "+i+" "+tiposMedida.get(i));
            System.out.println("valor de medida "+i+" "+valoresMedida.get(i));
            System.out.println("--------------------------------------------------------------");

            valores = "('" + tiposMedida.get(i) + "','" + 
            valoresMedida.get(i) + "','" + 
            idPrueba + "')";
            String consulta="SELECT * FROM "+ Medidas.TABLA+ " WHERE MEASURETYPE=" + tiposMedida.get(i) + " AND  TEST="+ idPrueba;
            System.out.println("Consulta de medidas : " +consulta);
            String actualizacion="UPDATE "+ Medidas.TABLA + " SET Valor_medida= "+ valoresMedida.get(i) +"  WHERE MEASURETYPE="+ tiposMedida.get(i)+ " AND TEST="+idPrueba;
            System.out.println("Actualizacion de medidas : " +actualizacion);
            try 
            {
                if (DBUtil.executeQuery(consulta).next()) 
                {   
                    System.out.println(" ************* ACTUALIZA MEDIDAS *****************");
                    DBUtil.executeUpdate(actualizacion);
                }else{
                    System.out.println(" ************* INSERTA MEDIDAS *****************");
                    DBUtil.insert(Medidas.TABLA, campos, valores);   
                }
            } catch (SQLException ex) 
            {
                System.out.println("Error: guardarMedidas - (MedidasDAO)");
                ex.printStackTrace(System.err);
                //throw new NoPersistException();
            } catch (ClassNotFoundException ex) {
                System.out.println("Error: guardarMedidas - (MedidasDAO)");
                ex.printStackTrace(System.err);
                throw new NoPersistException();
            }
        }

        prueba.imprimirValores();
        return false;
    }
}
