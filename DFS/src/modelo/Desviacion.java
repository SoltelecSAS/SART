/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Gerencia TIC
 */
public class Desviacion implements PruebaDefault {

    private final List<Double> desviacion;
    private double desviacionMedia;
    private List<Integer> tiposMedida;
    private List<Double> valoresMedida;
    private List<Integer> defectos;
    private String aprobada;
    private int resolMin = 1;
    private int resolMax = 1;

    public static double DESVIACION_EJE = 10; //90009 - 90010

    public Desviacion() {
        desviacion = new ArrayList<>();
    }

    public List<Double> getDesviacion() {
        return desviacion;
    }

     public void setResolMin(Integer resMin) {
        this.resolMin=resMin;
    }  
     
     public void setResolMax(Integer resMax) {
        this.resolMax=resMax;
    }  
    public void setDesviacion(Double desviacion) {
        this.desviacion.add(desviacion);
    }

    @Override
    public List<Integer> getDefectos() {
        if (defectos == null) {
            verificarDefectos();
        }
        return defectos;
    }

    @Override
    public String getAprobada() {
        return aprobada;
    }

    @Override
    public List<Integer> getTiposMedida() {
        tiposMedida = new ArrayList<>();
//        int[] idCampo = {4000, 4001, 4002, 4003, 4005};
        int[] idCampo = {4000, 4001, 4002, 4003};
        for (int i = 0; i < desviacion.size(); i++) {
            tiposMedida.add(idCampo[i]);
        }
        return tiposMedida;
    }

    @Override
    public List<Double> getValoresMedida() {
        valoresMedida = new ArrayList<>();
        for (Double desviacion1 : desviacion) {
            valoresMedida.add(desviacion1);
        }
        return valoresMedida;
    }

    @Override
    public void verificarDefectos() {
        aprobada = "Y";
        defectos = new ArrayList<>();

        if (tiposMedida == null) {
            getTiposMedida();
            getValoresMedida();
        }

        for (int i = 0; i < desviacion.size(); i++) {
            if (Math.abs(desviacion.get(i)) > DESVIACION_EJE) {
                if (i == 0) {
                    defectos.add(90009);
                    aprobada = "N";
                } else {
                    defectos.add(90010);
                    break;
                }
            }
        }
    }
    @Override
    public Double verifPesoVacioXPista() {        
       return 0.0;
    }

    /**
     * Metodo para calcular la desviacion media de los datos obtenidos en la
     * prueba Formula: desviacionMedia = sumaDesviacion / numeroDatos;
     */
    private void calcularDesviacionMedia() {
        double suma = 0;
        for (int i = 0; i < desviacion.size(); i++) {
            suma += desviacion.get(i);
        }
        desviacionMedia = (suma / desviacion.size()) / 1000;
    }

    @Override
    public void imprimirValores() {
        System.out.println("\n..........VALORES DESVIACION..........\n");
        for (int i = 0; i < desviacion.size(); i++) {
            System.out.println("Desviacion del Eje " + (i + 1) + ": " + desviacion.get(i));
        }
    }

    @Override
    public boolean isRepetirPrueba() {
        return false;
    }

    @Override
    public Boolean verifResolMedDesv(Double medida,Integer resolMin,Integer resolMax) {
        boolean verifica= true;
         System.out.println("\n..........VALORES medido DESV..........\n"+medida);        
        if( this.resolMin > medida ){
            verifica= false;            
        }
        if( this.resolMax < medida ){
            verifica= false;            
        }
        if(verifica==false){
          imprimirValores();
          Object[] choices = {"REPETIR"};         
          int dato = JOptionPane.showOptionDialog(null, "<html><div><center><img src='file:images/flag-red-icon.png' alt='algo'/><h2 style='font-family: \"Open Sans Condensed Light\"; color:#069'>Falla en el proceso</h2><hr/><p align='justify' style='font-family: 'Open Sans Condensed Light'; font-size: 15px; '>LA MEDIDA ESTA POR FUERA DE LA RESOLUCION DE LA MAQUINA</p><hr/><br/></center></div></html>", "SART 1.7.3 DESVIACION FUERA DE RESOLUCION", 0, -1, null, choices, choices[0]);                   
        }        
        return verifica;        
    }
}
