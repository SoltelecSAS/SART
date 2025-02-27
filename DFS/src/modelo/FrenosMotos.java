/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.util.ArrayList;
import java.util.List;
import Utilidades.Utilidades2;

/**
 *
 * @author GerenciaDesarrollo
 */
public class FrenosMotos implements PruebaDefault {

    private double pesoDelantero;
    private double pesoTrasero;
    private double fuerzaDelantera;
    private double fuerzaTrasera;
    private double eficacia;

    private List<Double> valorMedidas;
    private List<Integer> tipoMedidas;
    private List<Integer> defectos;
    private String aprobada;

    //Permisible para eficacia de frenado
    //private int minimoEficaciaA = 30;
    private boolean repetirPrueba;

    public double getPesoDelantero() {
        return pesoDelantero;
    }

    public void setPesoDelantero(double pesoDelantero) {
        this.pesoDelantero = pesoDelantero;
    }

    public double getPesoTrasero() {
        return pesoTrasero;
    }

    public void setPesoTrasero(double pesoTrasero) {
        this.pesoTrasero = pesoTrasero;
    }

    public double getFuerzaDelantera() {
        return fuerzaDelantera;
    }

    public void setFuerzaDelantera(double fuerzaDelantera) {
        this.fuerzaDelantera = fuerzaDelantera;
    }

    public double getFuerzaTrasera() {
        return fuerzaTrasera;
    }

    public void setFuerzaTrasera(double fuerzaTrasera) {
        this.fuerzaTrasera = fuerzaTrasera;
    }

    private void calcularEficacia() {
        double sumaPeso = pesoDelantero + pesoTrasero;
        double sumaFuerza = fuerzaDelantera + fuerzaTrasera;        
        eficacia = (sumaFuerza / sumaPeso) * 100;        
        System.out.println("entro a calcular eficacia: suma peso: "+ sumaPeso+ "sumaFUERZA: " + sumaFuerza+ " eficacia: "+ eficacia);
    }
    @Override
    public Double verifPesoVacioXPista() {
     return 0.0;
    }
    public void llenarValorMedidas() {
        valorMedidas = new ArrayList<>();      
        valorMedidas.add(pesoDelantero);
        valorMedidas.add(pesoTrasero);
        valorMedidas.add(fuerzaDelantera);
        valorMedidas.add(fuerzaTrasera);
        valorMedidas.add(eficacia);
    }

    public void llenarTipoMedidas() {
        tipoMedidas = new ArrayList<>();
        tipoMedidas.add(5000);
        tipoMedidas.add(5001);
        tipoMedidas.add(5008);
        tipoMedidas.add(5009);
        tipoMedidas.add(5024);
    }

    @Override
    public void verificarDefectos(String placa) {

        System.out.println("-------------placa vehiculo: "+placa);

        String tipoVehiculo = Utilidades2.obtenerTipoVehiculoPorPlaca(placa);

        

        aprobada = "Y";
        defectos = new ArrayList<>();

        if (tipoVehiculo.equalsIgnoreCase("4")) {
            System.out.println("-------------tipo vehiculo: "+tipoVehiculo+" Osea moto");
            defectos = getDefectosMoto();
        } else {
            defectos = getDefectosCicloMotor();
            System.out.println("-------------tipo vehiculo: "+tipoVehiculo+" Osea ciclomotor");
        }
    }

    private List<Integer> getDefectosMoto(){
        List<Integer> defects = new ArrayList<>();
        //minimoEficaciaA = 30;
        if (eficacia < 30) {
            aprobada = "N";
            defects.add(54010);
            //CMensajes.mensajeAdvertencia("LA PRUEBA SERA RECHAZADA POR eficacia de :"+eficacia);
        }
        return defects;
    }

    private List<Integer> getDefectosCicloMotor(){
        List<Integer> defects = new ArrayList<>();
        //minimoEficaciaA = 40;
        if (eficacia < 40) {
            defects.add(56000);//cambiar permisible = 54010
            aprobada = "N";
            //CMensajes.mensajeAdvertencia("LA PRUEBA SERA RECHAZADA POR eficacia de :"+eficacia);
        } 
        return defects;
    }

    @Override
    public List<Integer> getDefectos(String tipoVehiculo) {
        if (defectos == null) {
            calcularEficacia();
            verificarDefectos(tipoVehiculo);
        }
        return defectos;
    }

    @Override
    public String getAprobada() {
        return aprobada;
    }

    @Override
    public List<Integer> getTiposMedida() {
        if (tipoMedidas == null) {
            llenarTipoMedidas();
        }
        return tipoMedidas;
    }

    @Override
    public List<Double> getValoresMedida() {
        if (valorMedidas == null) {
             calcularEficacia();
            llenarValorMedidas();
        }
        return valorMedidas;
    }

    @Override
    public void imprimirValores(String ubicacion) {
        System.out.println("-------ubicacion FrenosMotos: "+ ubicacion);
    }

    @Override
    public boolean isRepetirPrueba() {
        
        return repetirPrueba;
    }

    @Override
    public Boolean verifResolMedDesv(Double medida, Integer resolMin, Integer resolMax) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
