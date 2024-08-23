/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosbanco;

import gnu.io.SerialPort;
import java.io.IOException;
import org.soltelec.util.EstadoMaquina;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.capelec.BancoCapelec.TipoCalibracion;


/**
 *Interface para definir las operaciones basicas
 * de un equipo de medicion de gases para gasolina
 * basado en la implementacion de BancoSensors
 * @author GerenciaDesarrollo
 */
public interface BancoGasolina{


    int LIM_CRUCERO = 2500;//constante general para cambiar en las pruebas el limite de crucero
    int LIM_RALENTI = 1100;//estas constantes se repetian en tres clases por lo que se decidio ponerla en una sola clase
//    int LIMITE_OXIGENO = 35; // Se modifica ya que debe ser 18
    int LIMITE_OXIGENO = 18;
    int RETARDOO2 =60;
    int LIM_TEMP = 60;//ACEITE
    int TIEMPO_HC_DESCIENDA = 270;//tiempo para que el banco se descontamine en Motos
    int LIM_TEMP_MOTOS = 40; //limite de temperatura para motos
    int LIM_RALENTI_MOTOS_SUP = 1800;
    int TIEMPO_FUERA_CONDICIONES = 60;
    int LIM_TEMP_AMB_MIN = 5;
    int LIM_TEMP_AMB_MAX = 55;
    int LIM_HUMEDAD_MIN = 30;
    int LIM_HUMEDAD_MAX = 90;
    int LIM_TEMP_BLOQUE = 45;
    int LIM_TIEMPO_CALIBRACION = 180;
    
    MedicionGases obtenerDatos();
    String obtenerPEF();
    String numeroSerial();
    /**
     * Enciende si onOff es true de lo contrario apaga la bomba de muestra
     * @param onOff
     * @return 
     */
    String encenderBombaMuestras(boolean onOff);
    String encenderSolenoideUno(boolean onOff);
    String encenderSolenoide2(boolean onOff);
    String encenderCalSol1(boolean onOff);
    String encenderCalSol2(boolean onOff);
    String encenderDrainPump(boolean on);
    EstadoMaquina statusMaquina();
    short mapaFisicoES(short palabra, boolean leerEscribir);//leer debe ser false
    void ordenarCalibracion(int modoCalibracion);
    void leerEscribirDatosCal(boolean leerEscribir,PuntoCalibracion ptoCal,int dataset);//leer debe ser false
    PuntoCalibracion leerDatosCalibracionPto1(int dataset);
    SerialPort getPuertoSerial();
    void setPuertoSerial(SerialPort serialPort) throws IOException;

    public void calibracion(TipoCalibracion tipoCalibracion, double valorCO, double valorCO2, int valorHC);

    public void setFactorRPM(int i);
    
}//end of interface BancoGasolinastatu