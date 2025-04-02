/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author GerenciaDesarrollo
 */
public class Suspension implements PruebaDefault {

    private final List<Double> fuerzaDerecha;
    private final List<Double> fuerzaIzquierda;
    private List<Double> pesoDerecho;
    private List<Double> pesoIzquierdo;
    public static List<Double> suspension;
    private List<Integer> tiposMedida;
    private List<Double> valoresMedida;
    List<Integer> defectos;
    private String aprobada;    
    List<Double> fuerzasvd;
    List<Double> fuerzasvi;
    

    public static double ADHERENCIA_MINIMA = 40; //46009
    private boolean repetirPrueba;

    public Suspension( ) {
        fuerzaDerecha = new ArrayList<>();
        fuerzaIzquierda = new ArrayList<>();
        pesoDerecho = new ArrayList<>();
        pesoIzquierdo = new ArrayList<>();   
    }

    public List<Double> getFuerzaDerecha() {
        return fuerzaDerecha;
    }

    public void setFuerzaDerecha(Double fuerzaDerecha, String ubicacion) {
        System.out.println("---ubicacion de seteo fuerza derecha double: "+ubicacion+ " fuerzaDerecha.size() = " +this.fuerzaDerecha.size());
        System.out.println("fuerzaIzquierda.size() = " +fuerzaIzquierda.size());
        this.fuerzaDerecha.add(fuerzaDerecha);
    }

    public List<Double> getFuerzaIzquierda() {
        return fuerzaIzquierda;
    }

    public void setFuerzaIzquierda(Double fuerzaIzquierda, String ubicacion) {
        System.out.println("---ubicacion de seteo fuerza izquierda double: "+ubicacion+ "fuerzaIzquierda.size() = " +this.fuerzaIzquierda.size());
        this.fuerzaIzquierda.add(fuerzaIzquierda);
    }

    public List<Double> getPesoDerecho() {
        return pesoDerecho;
    }

    public void setPesoDerecho(List<Double> pesoDerecho, String ubicacion) {
        System.out.println("---ubicacion de seteo peso derecho List: "+ubicacion);
        this.pesoDerecho = pesoDerecho;
    }

    public void setPesoDerecho(Double pesoDerecho, String ubicacion) {
        System.out.println("---ubicacion de seteo peso derecho double: "+ubicacion);
        this.pesoDerecho.add(pesoDerecho);
    }

    public List<Double> getPesoIzquierdo() {
        return pesoIzquierdo;
    }

    public void setPesoIzquierdo(List<Double> pesoIzquierdo, String ubicacion) {
        System.out.println("---ubicacion de seteo peso derecho List: "+ubicacion);
        this.pesoIzquierdo = pesoIzquierdo;
    }

    public void setPesoIzquierdo(Double pesoIzquierdo, String ubicacion) {
        System.out.println("---ubicacion de seteo peso izquierdo double: "+ubicacion);
        this.pesoIzquierdo.add(pesoIzquierdo);
    }

    @Override
    public List<Integer> getDefectos(String placa) {
        if (defectos == null) {
           verificarDefectos(placa);
        }
        return defectos;
    }

    @Override
    public String getAprobada() {
        return aprobada;
    }

    @Override
    public List<Integer> getTiposMedida() 
    {

        System.out.println("--------------------------------------------------- ");
        System.out.println("- Estoy modelo suspension implemntCalculoSuspension-");
        System.out.println("--------------------------------------------------- ");

        tiposMedida = new ArrayList<>();
        int[] idCampoPeso = {6000, 6004, 6001, 6005, 6002, 6006, 6003, 6007};
        int[] idCampoFuerza = {6008, 6012, 6009, 6013, 6010, 6014, 6011, 6015};
        int[] idCampoSuspension = {6016, 6017, 6020, 6021, 6022, 6018, 6023, 6019};

        for (int i = 0; i < (pesoDerecho.size() * 2); i++) {
            tiposMedida.add(idCampoPeso[i]);
        }

        for (int i = 0; i < (fuerzaDerecha.size() * 2); i++) {
            tiposMedida.add(idCampoFuerza[i]);
        }

        for (int i = 0; i < (pesoDerecho.size() * 2); i++) {
            tiposMedida.add(idCampoSuspension[i]);
        }

        return tiposMedida;
    }

    @Override
    public List<Double> getValoresMedida() 
    {
        System.out.println("--------------------------------------------------- ");
        System.out.println("- Estoy modelo suspension implemntCalculoSuspension-");
        System.out.println("--------------------------------------------------- ");
        
        
        valoresMedida = new ArrayList<>();
        calcularSuspension();

        for (int i = 0; i < pesoDerecho.size(); i++) 
        {
            valoresMedida.add(pesoDerecho.get(i));
            valoresMedida.add(pesoIzquierdo.get(i));
        }

        for (int i = 0; i < fuerzaDerecha.size(); i++) 
        {
            valoresMedida.add(fuerzaDerecha.get(i));
            valoresMedida.add(fuerzaIzquierda.get(i));
        }
        for (int i = 0; i < suspension.size(); i++) {
            valoresMedida.add(suspension.get(i));
        }
        return valoresMedida;
    }
    
    @Override
    public Double verifPesoVacioXPista() {
         double sumaPesos = 0;
       for (int i = 0; i < pesoIzquierdo.size(); i++) {
            sumaPesos += pesoDerecho.get(i) + pesoIzquierdo.get(i);
             System.out.println("Pesos Derecho "+pesoDerecho.get(i)+"Pesos Izquierdo "+ pesoIzquierdo.get(i));
        }
       System.out.println("Sum Pesos  "+sumaPesos);
        System.out.println("lo divide "+String.valueOf(sumaPesos/9.8));
       return sumaPesos/9.8;
       
    }
    
    @Override
    public void verificarDefectos(String placa) {
        aprobada = "Y";
        defectos = new ArrayList<>();
       

        if (tiposMedida == null) {
            getTiposMedida();
            getValoresMedida();
        }

        for (Double suspension1 : suspension) {
            System.out.println("valor que evaluo para poner defecto suspension: "+ suspension1);
            if (suspension1 < ADHERENCIA_MINIMA) {
                System.out.println("VALOR : "+ suspension1 + " menor al permisible se inserta defecto 46009 ");
                defectos.add(46009);
                aprobada = "N";
                break;
            }
        }
    }

    //calculosss
    public void calcularSuspension() 
    {
        System.out.println("---------------------------------------------------");
        System.out.println("-------------- Calcular Suspension ----------------");
        System.out.println("---------------------------------------------------");

            suspension = new ArrayList<>();
        

        System.out.println("----------------------------------------------------");
        System.out.println("---------- INICIO RECALCULO    -------------");
        System.out.println("----------------------------------------------------");

        int numeroDeDatos = 0;
        Double sumaDatos = 0.0;
        
        System.out.println("fuerzaDerecha.size() = " +fuerzaDerecha.size());
        System.out.println("fuerzaIzquierda.size() = " +fuerzaIzquierda.size());
        
        System.out.println("Datos:");
        for (int i = 0; i < (fuerzaDerecha.size()); i++) 
        {
            if (fuerzaDerecha.get(i) != 0){
                numeroDeDatos++;
                sumaDatos+=fuerzaDerecha.get(i);
                System.out.println("fuerza derecha "+i+"="+fuerzaDerecha.get(i));
            }else System.out.println("fuerza derecha "+i+"= 0");
            if (fuerzaIzquierda.get(i) != 0) {
                numeroDeDatos++;
                sumaDatos+=fuerzaIzquierda.get(i);
                System.out.println("fuerza izquierda "+i+"="+fuerzaIzquierda.get(i));
            }else System.out.println("fuerza izquierda "+i+"= 0");
        }

        System.out.println("\n\nnumero de elementos: "+numeroDeDatos);
        System.out.println("sumaTotal: "+numeroDeDatos);
        Double promedio = sumaDatos / numeroDeDatos;
        System.out.println("Promedio recalculo: "+promedio);

        for (int i = 0; i < (fuerzaDerecha.size()); i++) 
        {
            if ((fuerzaDerecha.get(i) >=0 && fuerzaDerecha.get(i) <1)){
                fuerzaDerecha.set(i, promedio);
                System.out.println("Derecha C " + (i + 1) + ": " + fuerzaDerecha.get(i));
            }
            if (fuerzaIzquierda.get(i) >=0 && fuerzaIzquierda.get(i) <1) {
                fuerzaIzquierda.set(i, promedio);
                System.out.println("Izquierda C " + (i + 1) + ": " + fuerzaIzquierda.get(i));
            }
        }

        
        System.out.println("----------------------------------------------------");
        System.out.println("---------- INICIO PRUEBA SUSPENSION    -------------");
        System.out.println("----------------------------------------------------");
        
        System.out.println("---   VALORES TOMADOS FUERZA DERECHA  -------------");
        for (int i = 0; i < fuerzaDerecha.size(); i++) 
        {    
            double FuerzaD = fuerzaDerecha.get(i);
            double pesoD = pesoDerecho.get(i);
            
            double adherencia = (FuerzaD / pesoD) * 100;
            
            System.out.println("---  FuerzaD : " + FuerzaD);
            System.out.println("---  pesoD : " + pesoD);   
            System.out.println("---Original  adherenciaD : " + adherencia );
            System.out.println("---Recalculado  adherencia : " + adherencia );
            
            fuerzaDerecha.remove(i);
            fuerzaDerecha.add(i,FuerzaD);
            suspension.add(adherencia);
        }
        

        System.out.println("---   VALORES TOMADOS FUERZA IZQUIERDA  ------------");
        
        for (int i = 0; i < fuerzaIzquierda.size(); i++) 
        {
            double FuerzaIz = fuerzaIzquierda.get(i);
            double pesoIz = pesoIzquierdo.get(i);
            
            double adherenciaIz = (FuerzaIz / pesoIz) * 100;
            
            System.out.println("---  FuerzaIz : " + FuerzaIz);
            System.out.println("---  pesoIz : " + pesoIz);
            System.out.println("---Original  adherenciaIz : " + adherenciaIz );

            adherenciaIz = (FuerzaIz / pesoIz) * 100;
            System.out.println("---Reclaculada  adherenciaIz : " + adherenciaIz );
            fuerzaIzquierda.remove(i);
            fuerzaIzquierda.add(i, FuerzaIz);
            suspension.add(adherenciaIz);
        }
        
    }
    

    //impresioens
    @Override
    public void imprimirValores(String ubicacion)
    {
        System.out.println("------ubicacion suspension: "+ubicacion);
        System.out.println("\n..........VALORES SUSPENSION..........\n");

        for (int i = 0; i < pesoDerecho.size(); i++) 
        {
            System.out.println("Peso Izquierdo Eje " + (i + 1) + ": " + pesoIzquierdo.get(i));
            System.out.println("Peso Derecho Eje " + (i + 1) + ": " + pesoDerecho.get(i));
        }

        for (int i = 0; i < (fuerzaDerecha.size()); i++) 
        {
            System.out.println("Fuerza Izquierda Eje " + (i + 1) + ": " + fuerzaIzquierda.get(i));
            System.out.println("Fuerza Derecha Eje " + (i + 1) + ": " + fuerzaDerecha.get(i));
        }

        System.out.println("Suspension DD " + suspension.get(0));
        System.out.println("Suspension TD : " + suspension.get(1));
        System.out.println("Suspension DI " + suspension.get(2));
        System.out.println("Suspension TI " + suspension.get(3));
    }

    @Override
    public boolean isRepetirPrueba() 
    {
        return repetirPrueba;
    }

    @Override
    public Boolean verifResolMedDesv(Double medida, Integer resolMin, Integer resolMax) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}