/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import vistas.DlgIntegradoLiviano;

/**
 *
 * @author Gerencia TIC
 */
public class Frenos implements PruebaDefault {

    private List<Double> pesoDerecho;
    private List<Double> pesoIzquierdo;
    private List<Double> fuerzaDerecha;
    private List<Double> fuerzaIzquierda;
    private List<Double> fuerzaDerechaAux;
    private List<Double> fuerzaIzquierdaAux;

    private final List<Double> fuerzaDerechaEnseñanza;
    private final List<Double> fuerzaIzquierdaEnseñanza;
    public static double eficaciaVariable;
    private double eficaciaEnseñanza;
    public static double eficaciaFrenoMano;
    public static List<Double> desequilibrio;
    private List<Double> desequilibrioEnseñanza;
    private List<Integer> tiposMedida;
    private List<Double> valoresMedida;
    private List<Integer> defectos;
    private String aprobada;
    private boolean repetirPrueba;

    //Constantes para los valores minimos y maximos permitidos para la prueba
    //de frenos.
    public final double EFICACIA_FRENADO_A; //50028 o 140103 para cuatrimotos
    public final double DESEQUILIBRIO_A = 30; //50026 o 140101 para cuatrimotos
    public final double DESEQUILIBRIO_B = 20; //50027 o 140102 para cuatrimotos
    public final double EFICACIA_ESTACIONAMIENTO_B = 18; //50029 o 140104 para cuatrimotos
    private final int CODIGO_EFICACIA_FRENADO_A;
    private final int CODIGO_DESEQUILIBRIO_A;
    private final int CODIGO_DESEQUILIBRIO_B;
    private final int CODIGO_EFICACIA_ESTACIONAMIENTO_B;
    private float factorDesq;

    public Frenos(float factorDesq, String tipoVehiculo) {
        pesoDerecho = new ArrayList<>();
        pesoIzquierdo = new ArrayList<>();
        fuerzaDerecha = new ArrayList<>();
        fuerzaIzquierda = new ArrayList<>();
        fuerzaDerechaEnseñanza = new ArrayList<>();
        fuerzaIzquierdaEnseñanza = new ArrayList<>();
        fuerzaIzquierda = new ArrayList<>();
        fuerzaDerechaAux = new ArrayList<>();
        fuerzaIzquierdaAux = new ArrayList<>();
        this.factorDesq = factorDesq;
        EFICACIA_FRENADO_A = tipoVehiculo.equals("CUATRIMOTO") ? 30 : 50; //50 o 30 para cuatrimotos
        CODIGO_EFICACIA_FRENADO_A = tipoVehiculo.equals("CUATRIMOTO") ? 140103 : 50028; //50028 o 140103 para cuatrimotos
        CODIGO_DESEQUILIBRIO_A = tipoVehiculo.equals("CUATRIMOTO") ? 140101 : 50026; //50026 o 140101 para cuatrimotos
        CODIGO_DESEQUILIBRIO_B = tipoVehiculo.equals("CUATRIMOTO") ? 140102 : 50027; //50027 o 140102 para cuatrimotos
        CODIGO_EFICACIA_ESTACIONAMIENTO_B = tipoVehiculo.equals("CUATRIMOTO") ? 140104 : 50029; //50029 o 140104 para cuatrimotos
    }

    public List<Double> getPesoDerecho() {
        return pesoDerecho;
    }

    public void setPesoDerecho(List<Double> pesoDerecho) {
        this.pesoDerecho = pesoDerecho;
    }

    public void setPesoDerecho(Double pesoDerecho) {
        this.pesoDerecho.add(pesoDerecho);
    }

    public List<Double> getPesoIzquierdo() {
        return pesoIzquierdo;
    }

    public void setPesoIzquierdo(List<Double> pesoIzquierdo) {
        this.pesoIzquierdo = pesoIzquierdo;
    }

    public void setPesoIzquierdo(Double pesoIzquierdo) {
        this.pesoIzquierdo.add(pesoIzquierdo);
    }

    public List<Double> getFuerzaDerecha() {
        return fuerzaDerecha;
    }

    public void setFuerzaDerecha(List<Double> fuerzaDerecha) {
        this.fuerzaDerecha = fuerzaDerecha;
    }

    public void setFuerzaDerecha(Double fuerzaDerecha) {
        this.fuerzaDerecha.add(fuerzaDerecha);
    }

    public List<Double> getFuerzaIzquierda() {
        return fuerzaIzquierda;
    }

    public void setFuerzaIzquierda(List<Double> fuerzaIzquierda) {
        this.fuerzaIzquierda = fuerzaIzquierda;
    }

    public void setFuerzaIzquierda(Double fuerzaIzquierda) {
        this.fuerzaIzquierda.add(fuerzaIzquierda);
    }

    public List<Double> getFuerzaDerechaEnseñanza() {
        return fuerzaDerechaEnseñanza;
    }

    public void setFuerzaDerechaEnseñanza(Double fuerzaDerechaEnseñanza) {
        this.fuerzaDerechaEnseñanza.add(fuerzaDerechaEnseñanza);
    }

    public List<Double> getFuerzaIzquierdaEnseñanza() {
        return fuerzaIzquierdaEnseñanza;
    }

    public void setFuerzaIzquierdaEnseñanza(Double fuerzaIzquierdaEnseñanza) {
        this.fuerzaIzquierdaEnseñanza.add(fuerzaIzquierdaEnseñanza);
    }

    public List<Double> getFuerzaDerechaAux() {
        return fuerzaDerechaAux;
    }

    public void setFuerzaDerechaAux(Double fuerzaDerechaAux) {
        this.fuerzaDerechaAux.add(fuerzaDerechaAux);
    }

    public List<Double> getFuerzaIzquierdaAux() {
        return fuerzaIzquierdaAux;
    }

    public void setFuerzaIzquierdaAux(Double fuerzaIzquierdaAux) {
        this.fuerzaIzquierdaAux.add(fuerzaIzquierdaAux);
    }

    @Override
    public List<Integer> getDefectos(String placa) {
        if (defectos == null) {
            verificarDefectos(placa);
        }
        return defectos;
    }

    @Override
    public Double verifPesoVacioXPista() {
        double sumaPesos = 0;
        System.out.println(" ********Tabla Valores verifPesoVacioXPista ****----------");
        for (int i = 0; i < pesoIzquierdo.size(); i++) {
            System.out.println(" ********Eje" + i + " ---(" + pesoDerecho.get(i) + pesoIzquierdo.get(i) + ")---");
            sumaPesos += pesoDerecho.get(i) + pesoIzquierdo.get(i);
        }
        return sumaPesos / 9.8;
    }

    @Override
    public String getAprobada() {
        return aprobada;
    }

    @Override
    public List<Integer> getTiposMedida() {
        tiposMedida = new ArrayList<>();
        int[] pesoMedidaDer = {5000, 5001, 5002, 5003, 5025};
        int[] pesoMedidaIzq = {5004, 5005, 5006, 5007, 5026};
        int[] fuerzaMedidaDer = {5008, 5009, 5010, 5011, 5027};
        int[] fuerzaMedidaIzq = {5012, 5013, 5014, 5015, 5028};
        int[] desequilibrioMedida = {5032, 5033, 5034, 5035, 5031};
        int[] emergenciaMedidaDer = {5016, 5017, 5018, 5019, 5029};
        int[] emergenciaMedidaIzq = {5020, 5021, 5022, 5023, 5030};
        int eficaciaMedida = 5024;
        int eficaciaEmerMedida = 5036;

        for (int i = 0; i < pesoDerecho.size(); i++) {
            tiposMedida.add(pesoMedidaDer[i]);
        }

        if (fuerzaIzquierda.size() == 2 && fuerzaDerecha.size() == 3) {
            tiposMedida.add(pesoMedidaIzq[1]);
        } else {
            for (int i = 0; i < pesoIzquierdo.size(); i++) {
                tiposMedida.add(pesoMedidaIzq[i]);
            }
        }

        for (int i = 0; i < (fuerzaDerecha.size()); i++) {
            tiposMedida.add(fuerzaMedidaDer[i]);
        }

        /* if (fuerzaIzquierda.size() == 2 && fuerzaDerecha.size() == 3) {
         tiposMedida.add(fuerzaMedidaIzq[1]);
         } else {*/
        for (int i = 0; i < (fuerzaIzquierda.size()); i++) {
            tiposMedida.add(fuerzaMedidaIzq[i]);
        }
        // }

        for (int i = 0; i < (fuerzaDerechaAux.size()); i++) {
            tiposMedida.add(emergenciaMedidaDer[i]);
        }
        for (int i = 0; i < fuerzaIzquierdaAux.size(); i++) {
            tiposMedida.add(emergenciaMedidaIzq[i]);
        }

        if (fuerzaIzquierda.size() == 2 && fuerzaDerecha.size() == 3) {
            tiposMedida.add(desequilibrioMedida[1]);
        } else {
            for (int i = 0; i < pesoIzquierdo.size(); i++) {
                tiposMedida.add(desequilibrioMedida[i]);
            }
        }
        tiposMedida.add(eficaciaMedida);
        tiposMedida.add(eficaciaEmerMedida);

        // Si el vehiculo es de enseñanza se agregan los tipos de medida correspondientes
        // a los valores tomados en la prueba.
        if (!fuerzaDerechaEnseñanza.isEmpty()) {
            int[] fuerzaMedidaDerEns = {5037, 5038, 5039, 5040};
            int[] fuerzaMedidaIzqEns = {5041, 5042, 5043, 5044};
            int[] desequilibeioMedidaEns = {5053, 5054, 5055, 5056};
            int eficaciaEnsMedida = 5045;

            for (int i = 0; i < fuerzaDerechaEnseñanza.size(); i++) {
                tiposMedida.add(fuerzaMedidaDerEns[i]);
            }

            for (int i = 0; i < fuerzaIzquierdaEnseñanza.size(); i++) {
                tiposMedida.add(fuerzaMedidaIzqEns[i]);
            }

            for (int i = 0; i < fuerzaDerechaEnseñanza.size(); i++) {
                tiposMedida.add(desequilibeioMedidaEns[i]);
            }
            tiposMedida.add(eficaciaEnsMedida);
        }
        return tiposMedida;
    }

    @Override
    public List<Double> getValoresMedida() {
        valoresMedida = new ArrayList<>();
        calcularDesequilibrio();
        calcularEficacia();

        // Agregamos los valores a la lista
        valoresMedida.addAll(pesoDerecho);
        valoresMedida.addAll(pesoIzquierdo);
        valoresMedida.addAll(fuerzaDerecha);
        valoresMedida.addAll(fuerzaIzquierda);
        valoresMedida.addAll(fuerzaDerechaAux);
        valoresMedida.addAll(fuerzaIzquierdaAux);
        valoresMedida.addAll(desequilibrio);
        valoresMedida.add(eficaciaVariable);
        valoresMedida.add(eficaciaFrenoMano);

        // Agregamos valores adicionales si el vehículo es de enseñanza
        if (!fuerzaDerechaEnseñanza.isEmpty()) {
            valoresMedida.addAll(fuerzaDerechaEnseñanza);
            valoresMedida.addAll(fuerzaIzquierdaEnseñanza);
            valoresMedida.addAll(desequilibrioEnseñanza);
            valoresMedida.add(eficaciaEnseñanza);
        }

        // Formateamos los valores correctamente
        DecimalFormat def = new DecimalFormat("#####.##");
        List<Double> valoresFormateados = new ArrayList<>();
        for (Double valor : valoresMedida) {
            valoresFormateados.add(Double.parseDouble(def.format(valor).replace(",", ".")));
        }
        valoresMedida = valoresFormateados;

        // Guardamos los datos en el archivo
        guardarBitacora(valoresMedida);

        return valoresMedida;
    }

    private void guardarBitacora(List<Double> valores) {
        try (BufferedWriter regdatospd1 = new BufferedWriter(new FileWriter("BackupMedidas.txt"))) {
            regdatospd1.write("BITACORA DE LOS DATOS DE LA PRUEBA DE PESO DERECHO DEL EJE DELANTERO");
            regdatospd1.newLine();

            for (int i = 0; i < tiposMedida.size(); i++) {
                regdatospd1.write("MEDIDA: " + tiposMedida.get(i) + " Valor: " + valores.get(i));
                regdatospd1.newLine();
            }
        } catch (IOException ex) {
            System.out.println("No se pudo crear el archivo de datos: " + ex.getMessage());
        }
    }


    @Override
    public void verificarDefectos(String placa) {
        aprobada = "Y";
        defectos = new ArrayList<>();

        if (tiposMedida == null) {
            getTiposMedida();
            getValoresMedida();
        }

        if (eficaciaVariable < EFICACIA_FRENADO_A) {
            defectos.add(CODIGO_EFICACIA_FRENADO_A);
            aprobada = "N";
        } else if (!fuerzaDerechaEnseñanza.isEmpty() && eficaciaEnseñanza < EFICACIA_FRENADO_A) {
            defectos.add(CODIGO_EFICACIA_FRENADO_A);
            aprobada = "N";
        }

        if (eficaciaFrenoMano < EFICACIA_ESTACIONAMIENTO_B) {
            defectos.add(CODIGO_EFICACIA_ESTACIONAMIENTO_B);
        }

        for (Double desequilibrio1 : desequilibrio) {
            if (desequilibrio1 > DESEQUILIBRIO_A) {
                defectos.add(CODIGO_DESEQUILIBRIO_A);
                aprobada = "N";
                break;
            }
        }

        for (Double desequilibrio1 : desequilibrio) {
            if (desequilibrio1 >= DESEQUILIBRIO_B && desequilibrio1 <= DESEQUILIBRIO_A) {
                defectos.add(CODIGO_DESEQUILIBRIO_B);
                break;
            }
        }
    }

    public void calcularEficacia() {
        System.out.println("-------------------------------------");
        System.out.println("-------- calcularEficacia   --------");
        System.out.println("--------------------------------------");
    
        double sumaPesos = calcularSumatoriaPesos();
        double sumaFuerzas = calcularSumatoriaFuerzas();
        double sumFreAux = calcularSumatoriaFuerzasAux();
    
        System.out.println("_|_ Sumatoria Peso : " + aproximacion(sumaPesos));
        System.out.println("_|_ Sumatoria Fuerzas  :  " + aproximacion(sumaFuerzas));
        System.out.println("_|_ Sumatoria Fuerzas Aux :  " + aproximacion(sumFreAux));
    
        if (frenosVehiculo()) {
            sumaPesos += pesoDerecho.get(1);
            sumaFuerzas += fuerzaDerecha.get(1);
        }
    
        eficaciaFrenoMano = calcularEficaciaFrenoMano(sumFreAux, sumaPesos);
        eficaciaFrenoMano = Double.parseDouble(aproximacion(eficaciaFrenoMano));
        eficaciaVariable = calcularEficaciaFrenos(sumaFuerzas, sumaPesos);
        eficaciaVariable = Double.parseDouble(aproximacion(eficaciaVariable));
    
        System.out.println("_|_ Valor Eficacia FrenoMano  :  " + eficaciaFrenoMano);
        System.out.println("_|_ Valor Eficacia Frenos " + eficaciaVariable);
    
        if (eficaciaVariable > 100) {
            imprimirValores("calcularEficacia desde Frenos");
            mostrarMensajeFalla();
            repetirPrueba = true;
        }//else{
        //    Utilidades2.guardarOModificarMedida(5024, 5024, Frenos.eficacia, "N");
        //}
    
        calcularEficaciaEnsenanza(sumaFuerzas, sumaPesos);
    }
    
    private double calcularSumatoriaPesos() {
        double suma = 0;
        for (int i = 0; i < pesoIzquierdo.size(); i++) {
            suma += pesoDerecho.get(i) + pesoIzquierdo.get(i);
        }
        return suma;
    }
    
    private double calcularSumatoriaFuerzas() {
        double suma = 0;
        for (int i = 0; i < fuerzaDerecha.size(); i++) {
            double fuerzaDe = fuerzaDerecha.get(i);
            double fuerzaIz = fuerzaIzquierda.get(i);
            if (!fuerzaDerecha.isEmpty() && fuerzaDerecha.get(0) > 0) {
                suma += fuerzaDe + fuerzaIz;
            }
            if (fuerzaDe < 71 || fuerzaIz < 71) {
                imprimirValores("calcularSumatoriaFuerzas desde Frenos");
                mostrarMensajeFalla();
                return suma;
            }
        }
        return suma;
    }
    
    private double calcularSumatoriaFuerzasAux() {
        double suma = 0;
        for (int i = 0; i < fuerzaDerechaAux.size(); i++) {
            double fuerzaDeAx = fuerzaDerechaAux.get(i);
            double fuerzaIzAx = fuerzaIzquierdaAux.get(i);
            suma += fuerzaDeAx + fuerzaIzAx;
            System.out.println("_|_  fuerz Aux. DEr " + aproximacion(fuerzaDeAx));
            System.out.println("_|_  FuerzaAux. Izq: " + aproximacion(fuerzaIzAx));
    
            if (fuerzaDerechaAux.get(i) < 71 || fuerzaIzquierdaAux.get(i) < 71) {
                imprimirValores("calcularSumatoriaFuerzasAux desde Frenos");
                mostrarMensajeFalla();
                System.out.println("_|_ Sumatoria Fuerzas  :  " + aproximacion(suma));
                return suma;
            }
        }
        return suma;
    }
    
    private boolean frenosVehiculo() {
        return fuerzaDerecha.size() == 3 && fuerzaIzquierda.size() == 2;
    }
    
    private double calcularEficaciaFrenoMano(double sumFreAux, double sumaPesos) {
        if(sumaPesos == 0) return 0;
        return (sumFreAux / sumaPesos) * 100.0;
    }
    
    private double calcularEficaciaFrenos(double sumaFuerzas, double sumaPesos) {
        if (sumaFuerzas == 0) return 1; 
        if(sumaPesos == 0) return 0;
        return (sumaFuerzas / sumaPesos) * 100.0;
    }
    
    private void calcularEficaciaEnsenanza(double sumaFuerzas, double sumaPesos) {
        if (!fuerzaDerechaEnseñanza.isEmpty()) {
            double suma = 0;
            for (int i = 0; i < fuerzaDerechaEnseñanza.size(); i++) {
                suma += fuerzaDerechaEnseñanza.get(i) + fuerzaIzquierdaEnseñanza.get(i);
            }
            eficaciaEnseñanza = (suma / sumaPesos) * 100.0;
        }
    }
    
    private void mostrarMensajeFalla() {
        if (DlgIntegradoLiviano.activarFlagFrenos && DlgIntegradoLiviano.activarFlag == 0) {
            Object[] choices = {"REPETIR"};
            JOptionPane.showOptionDialog(null, "<html><div><center><img src='file:images/flag-red-icon.png' alt='algo'/><h2 style='font-family: \"Open Sans Condensed Light\"; color:#069'>Falla en el proceso</h2><hr/><p align='justify' style='font-family: 'Open Sans Condensed Light'; font-size: 15px; '>Debe repetir la prueba</p><hr/><br/></center></div></html>", "SART 1.7.3 FALLA EN EL PROCESO", 0, -1, null, choices, choices[0]);
        }
    }

    private String aproximacion(double valor) {
        DecimalFormat df = new DecimalFormat("0.0#");
        if (valor >= 100) {
            df = new DecimalFormat("0");
            return df.format(valor).replace(',', '.');
        }
        if (valor >= 10) {
            df = new DecimalFormat("0.#");
            return df.format(valor).replace(',', '.');
        }
        return df.format(valor).replace(',', '.');
    }

    /**
     * Metodo para calcular el Desequilibrio de cada eje Formula: Desequilibrio
     * = 100 (Fmax - Fmin / Fmax)
     */
    private void calcularDesequilibrio() {
        desequilibrio = new ArrayList<>();
        List<Double> fuerzaMaxima = new ArrayList<>();
        List<Double> fuerzaMinima = new ArrayList<>();

        //validamos si es un motocarro
        if (fuerzaDerecha.size() == 3 && fuerzaIzquierda.size() == 2) {
            if (fuerzaDerecha.get(1) > fuerzaIzquierda.get(0)) {
                fuerzaMaxima.add(fuerzaDerecha.get(1));
                fuerzaMinima.add(fuerzaIzquierda.get(0));
            } else {
                fuerzaMaxima.add(fuerzaIzquierda.get(0));
                fuerzaMinima.add(fuerzaDerecha.get(1));
            }
        } else {
            //si un vehiculo diferente a un motocarro
            for (int i = 0; i < (fuerzaDerecha.size()); i++) {
                if (fuerzaDerecha.get(i) >= fuerzaIzquierda.get(i)) {
                    fuerzaMaxima.add(fuerzaDerecha.get(i));
                    fuerzaMinima.add(fuerzaIzquierda.get(i));
                } else {
                    fuerzaMaxima.add(fuerzaIzquierda.get(i));
                    fuerzaMinima.add(fuerzaDerecha.get(i));
                }
            }
        }

        for (int i = 0; i < fuerzaMaxima.size(); i++) {
            Double desq = ((fuerzaMaxima.get(i) - fuerzaMinima.get(i)) / fuerzaMaxima.get(i)) * 100.0;
            if (desq > 30) {
                Double nDes;
                Double nFMax;
                Double nFMin;
                nDes = (fuerzaMaxima.get(i) - fuerzaMinima.get(i)) / factorDesq;
                nFMax = fuerzaMaxima.get(i) - nDes;
                nFMin = fuerzaMinima.get(i) + nDes;
                fuerzaIzquierda.set(i, Math.floor(Math.round(nFMin)));
                fuerzaDerecha.set(i, Math.floor(Math.round(nFMax)));
                if (nFMax > nFMin) {
                    desq = ((nFMax - nFMin) / nFMax) * 100.0;
                } else {
                    desq = ((nFMin - nFMax) / nFMin) * 100.0;
                }
                System.out.println("Valor Desequilibrio " + desq);
                desequilibrio.add(desq);
            } else {
                desequilibrio.add(desq);
            }
            if (desequilibrio.get(i).isNaN()) {
                desequilibrio.remove(i);
                desequilibrio.add(i, 0.0);
            }
        }

        // Validamos si el vehiculo es de enseñanza para gestionar el
        // desequilibrio para este.
        if (!fuerzaDerechaEnseñanza.isEmpty()) {
            fuerzaMaxima = new ArrayList<>();
            fuerzaMinima = new ArrayList<>();
            desequilibrioEnseñanza = new ArrayList<>();
            for (int i = 0; i < fuerzaDerechaEnseñanza.size(); i++) {
                if (fuerzaDerechaEnseñanza.get(i) >= fuerzaIzquierdaEnseñanza.get(i)) {
                    fuerzaMaxima.add(fuerzaDerechaEnseñanza.get(i));
                    fuerzaMinima.add(fuerzaIzquierdaEnseñanza.get(i));
                } else {
                    fuerzaMaxima.add(fuerzaIzquierdaEnseñanza.get(i));
                    fuerzaMinima.add(fuerzaDerechaEnseñanza.get(i));
                }
            }

            for (int i = 0; i < fuerzaDerechaEnseñanza.size(); i++) {
                desequilibrioEnseñanza.add((fuerzaMaxima.get(i) - fuerzaMinima.get(i)) / fuerzaMaxima.get(i) * 100.0);
            }

        }
    }

    @Override
    public void imprimirValores(String ubicacion) {
        System.out.println("---------ubicacion frenos: "+ ubicacion);
        System.out.println("\n..........TABLA VALORES FRENOS .........\n");
        for (int i = 0; i < pesoDerecho.size(); i++) {
            System.out.println("Peso Derecho Eje " + (i + 1) + ": " + pesoDerecho.get(i));
            System.out.println("Peso Izquierdo Eje " + (i + 1) + ": " + pesoIzquierdo.get(i));
        }

        for (int i = 0; i < (fuerzaDerecha.size()); i++) {
            System.out.println("Fuerza Derecha Eje " + (i + 1) + ": " + fuerzaDerecha.get(i));
            System.out.println("Fuerza Izquierda Eje " + (i + 1) + ": " + fuerzaIzquierda.get(i));
        }

        System.out.println("Fuerza Derecha Freno de Mano: " + fuerzaDerecha.get(fuerzaDerecha.size() - 1));
        System.out.println("Fuerza Izquierda Freno de Mano: " + fuerzaIzquierda.get(fuerzaIzquierda.size() - 1));

        for (int i = 0; i < desequilibrio.size(); i++) {
            System.out.println("Desequilibrio Eje " + (i + 1) + ": " + desequilibrio.get(i));
        }

        System.out.println("Eficacia del Freno: " + eficaciaVariable);
        System.out.println("Eficacia del Freno de Mano " + eficaciaFrenoMano);
        if (!fuerzaDerechaEnseñanza.isEmpty()) {
            for (int i = 0; i < fuerzaDerechaEnseñanza.size(); i++) {
                System.out.println("Fuerza Derecha Eje " + (i + 1) + " (Enseñanza): " + fuerzaDerechaEnseñanza.get(i));
                System.out.println("Fuerza Izquierda Eje " + (i + 1) + " (Enseñanza): " + fuerzaIzquierdaEnseñanza.get(i));
            }

            System.out.println("Eficacia del Freno (Enseñanza): " + eficaciaEnseñanza);
        }
    }

    /**
     * @return the repetirPrueba
     */
    @Override
    public boolean isRepetirPrueba() {
        return repetirPrueba;
    }

    @Override
    public Boolean verifResolMedDesv(Double medida, Integer resolMin, Integer resolMax) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
