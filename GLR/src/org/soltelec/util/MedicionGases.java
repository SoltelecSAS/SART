/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.util;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Scanner;
import org.soltelec.medicionrpm.MedidorRevTemp;

/**
 *
 *
 * @author Gerencia Desarrollo de Soluciones Tecnologica
 */


public class MedicionGases {//clase para agrupar los valores
    boolean[] banderas = new boolean[16];
    int valorHC,valorCO,valorCO2,valorNox;
    double valCO,valCO2,valHC,valRPM;
    double valorO2;
    boolean lect5seg=false;
    int valorRPM,valorTAceite,valorTAmbiente,valorPres,valorRH;
    int valorTExt,valorRef, valorTLamp, valorDetectorTemp;
    //medidas adicionales agregadas para el banco capelec
    int valorLambda,pef,voltajeO2,porcentajeCalentamiento,presionVacio;
    
    String cadenaHC,cadenaCO,cadencaCO2,cadenaNox,cadenaO2;
    String cadenaRPM,cadenaTAceite,cadenaTAmbiente,cadenaPress;
    String cadenaRH,cadenaText,cadenaRef,cadenaTLamp,cadenaTDetect;
    boolean bajoFlujo,noExacto,cero,hiHC,condensacion,Calentamiento,HCrange,dilucion;
    boolean COrange,CO2range,O2range,NOxRange,Interna;
    //banderas adicionales agragadas para el banco capelec
    boolean ceroEnProgreso, calibracionEnProgreso,calibracion,presionRange;
    boolean tempAmbienRange,tempDetectorRange,tempAceiteRange,rpmRange,vacioRange;
    boolean CO3digitos, HCpropano, ChanelError,vacuumSwitch;
    boolean errorEEPROM,badO2Sensor,BajaSenialDetector,badNOxSensor;
    boolean ceroInicialEnProgreso,nuevoDatoGas, nuevoDatoRpm,ErrorLampara;
    boolean bombaEncendida,pruebaFugasAprobada;
    private boolean convertidorCatalitico;
    //las demas banderas se dejan para una actualizacion o al pedido del cliente
    DecimalFormat df2;
    private static double x[] = new double[21];
    private static double y[] = new double[21];
    static {
        try {
        Scanner input = new Scanner(new File("temp.txt"));
        
           for(int i = 0; i< 21; i++){
               x[i] = input.nextDouble();
               System.err.println(x[i]);
               y[i] = input.nextDouble();
               System.err.println(y[i]);
           }
           input.close();
        }catch (Exception exc){
        x[0] =  100; y[0] = 120.45;
        x[1] = 906; y[1]= 90.0;
        x[2] = 917; y[2] = 86.5 ;
        x[3] = 928; y[3] = 80.0 ;
        x[4] = 943; y[4] = 75.0 ;
        x[5] = 974; y[5] = 65.0 ;
        x[6] = 980; y[6] = 60.0 ;
        x[7] = 988; y[7] = 57.0 ;
        x[8] = 1032; y[8]= 55.0 ;
        x[9] = 1066; y[9]= 54.2 ;
        x[10] = 1122; y[10]= 52.2 ;
        x[11] = 1162; y[11]= 50.5 ;
        x[12] = 1200; y[12]= 48.2 ;
        x[13] = 1252; y[13]= 43.4 ;
        x[14] = 1291; y[14]= 38.3 ;
        x[15] = 1319; y[15]= 34.2 ;
        x[16] = 1336; y[16]= 36.4 ;
        x[17] = 1506; y[17]= 32.4 ;
        x[18] = 1579; y[18]= 30.2 ;
        x[19] = 1593; y[19]= 27.4 ;
        x[20] = 2500; y[20]= 0;
        }
    }

    public boolean isCO2range() {
        return CO2range;
    }

    public boolean isCOrange() {
        return COrange;
    }

    public boolean isCalentamiento() {
        return Calentamiento;
    }

    public boolean isHCrange() {
        return HCrange;
    }

    public boolean isInterna() {
        return Interna;
    }
    public boolean isLect5Seg() {
        return lect5seg;
    }
    public void setLect5Seg(boolean lect5seg ) {
        this.lect5seg = lect5seg;
    }
    

    public boolean isNOxRange() {
        return NOxRange;
    }

    public boolean isO2range() {
        return O2range;
    }

    public boolean isBajoFlujo() {
        return bajoFlujo;
    }

    public boolean isDilucion() {
        return dilucion;
    }

    public boolean isCero() {
        return cero;
    }

    public boolean isCondensacion() {
        return condensacion;
    }

    public boolean isHiHC() {
        return hiHC;
    }

    public boolean isNoExacto() {
        return noExacto;
    }

    public boolean isCalibracion() {
        return calibracion;
    }

    public boolean isCalibracionEnProgreso() {
        return calibracionEnProgreso;
    }

    public boolean isCeroEnProgreso() {
        return ceroEnProgreso;
    }

    public boolean isPresionRange() {
        return presionRange;
    }

    public boolean isRpmRange() {
        return rpmRange;
    }

    public boolean isTempAceiteRange() {
        return tempAceiteRange;
    }

    public boolean isTempAmbienRange() {
        return tempAmbienRange;
    }

    public boolean isTempDetectorRange() {
        return tempDetectorRange;
    }

    public boolean isVacioRange() {
        return vacioRange;
    }

    public boolean isCO3digitos() {
        return CO3digitos;
    }

    public boolean isChanelError() {
        return ChanelError;
    }

    public boolean isHCpropano() {
        return HCpropano;
    }

    public boolean isBajaSenialDetector() {
        return BajaSenialDetector;
    }

    public boolean isErrorLampara() {
        return ErrorLampara;
    }

    public boolean isBadNOxSensor() {
        return badNOxSensor;
    }

    public boolean isBadO2Sensor() {
        return badO2Sensor;
    }

    public boolean isCeroInicialEnProgreso() {
        return ceroInicialEnProgreso;
    }

    public boolean isErrorEEPROM() {
        return errorEEPROM;
    }

    public boolean isNuevoDatoGas() {
        return nuevoDatoGas;
    }

    public boolean isNuevoDatoRpm() {
        return nuevoDatoRpm;
    }

    public boolean isBombaEncendida() {
        return bombaEncendida;
    }

    public void setBombaEncendida(boolean bombaEncendida) {
        this.bombaEncendida = bombaEncendida;
    }

    public boolean isPruebaFugasAprobada() {
        return pruebaFugasAprobada;
    }

    public void setPruebaFugasAprobada(boolean pruebaFugasAprobada) {
        this.pruebaFugasAprobada = pruebaFugasAprobada;
    }

    
    public boolean[] getBanderas() {
        return banderas;
    }

    public void setBanderas(boolean[] banderas) {
        this.banderas = banderas;
    }

    public int getValorDetectorTemp() {
        return valorDetectorTemp;
    }

    public void setValorDetectorTemp(int valorDetectorTemp) {
        this.valorDetectorTemp = valorDetectorTemp;
    }

    public int getValorRH() {
        return valorRH;
    }

    public void setValorRH(int valorRH) {
        this.valorRH = valorRH;
    }

    public int getValorRef() {
        return valorRef;
    }

    public void setValorRef(int valorRef) {
        this.valorRef = valorRef;
    }

    public int getValorTAmbiente() {
        return valorTAmbiente;
    }

    public void setValorTAmbiente(int valorTAmbiente) {
        this.valorTAmbiente = valorTAmbiente;
    }

    public int getValorTExt() {
        return valorTExt;
    }

    public void setValorTExt(int valorTExt) {
        this.valorTExt = valorTExt;
    }

    public int getValorTLamp() {
        return valorTLamp;
    }

    public void setValorTLamp(int valorTLamp) {
        this.valorTLamp = valorTLamp;
    }

    public void setValorLambda(int valorLambda) {
        this.valorLambda = valorLambda;
    }

    public int getPef() {
        return pef;
    }

    public void setPef(int pef) {
        this.pef = pef;
    }

    public int getPorcentajeCalentamiento() {
        return porcentajeCalentamiento;
    }

    public void setPorcentajeCalentamiento(int porcentajeCalentamiento) {
        this.porcentajeCalentamiento = porcentajeCalentamiento;
    }

    public int getPresionVacio() {
        return presionVacio;
    }

    public void setPresionVacio(int presionVacio) {
        this.presionVacio = presionVacio;
    }

    public int getVoltajeO2() {
        return voltajeO2;
    }

    public void setVoltajeO2(int voltajeO2) {
        this.voltajeO2 = voltajeO2;
    }

    
    public MedicionGases(){
       Locale locale = new Locale("US");//para que sea con punto separador de decimales
       df2 = (DecimalFormat) NumberFormat.getInstance(locale);
        if (df2 instanceof DecimalFormat) {
            ((DecimalFormat) df2).setDecimalSeparatorAlwaysShown(true);
        }
        df2.setMaximumFractionDigits(2);

    }
    public MedicionGases(int valorHC, int valorCO, int valorCO2, int valorO2) {
        this();
        this.valorHC = valorHC;
        this.valorCO = valorCO;
        this.valorCO2 = valorCO2;
        this.valorO2 = valorO2;
    }
    
    public MedicionGases(int valorHC, int valorCO, double valorCO2, int valorO2) {
        this();
        this.valorHC = valorHC;
        this.valorCO = valorCO;
        this.valCO2 = valorCO2;
        this.valorO2 = valorO2;
    }
    
    
     public MedicionGases(double valorHC, double valorCO, double valorCO2, double valorO2) {
        this();        
        this.valHC = valorHC;
        this.valCO = valorCO;
        this.valCO2 = valorCO2;
        this.valorO2 = valorO2;
    }

    public double getValCO() {
        return valCO;
    }

    public void setValCO(double valCO) {
        this.valCO = valCO;
    }

    public double getValCO2() {
        return valCO2;
    }

    public void setValCO2(double valCO2) {
        this.valCO2 = valCO2;
    }

    public double getValHC() {
        return valHC;
    }

    public void setValHC(double valHC) {
        this.valHC = valHC;
    }

    public int getValorCO() {
        
        return (valorCO < 0? 0: valorCO);
    }

    public void setValorCO(int valorCO) {
        this.valorCO = valorCO;
    }

    public int getValorCO2() {
        return (valorCO2 < 0  ?0 : valorCO2);
    }
    
    public void setValCO(Double valorCO) {
        this.valCO = valorCO;
    }
    public void setValCO2(Double valorCO2) {
        this.valCO2 = valorCO2;
    }
    
    
    public void setValorCO2(int valorCO2) {
        this.valorCO2 = valorCO2;
    }

    public int getValorHC() {
        return  (valorHC < 0? 0 : valorHC);//las unidades depen estar en propano
    }

    public void setValorHC(int valorHC) {
        this.valorHC = valorHC;
    }

    public int getValorNox() {
        return valorNox;
    }

    public void setValorNox(int valorNox) {
        this.valorNox = valorNox;
    }

    public double getValorO2() {
        return  (valorO2 < 0? 0 : valorO2);//si existen unidades menores a 0 se corrigen
    }

    public void setValorO2(double valorO2) {
        this.valorO2 = valorO2;
    }

    public int getValorPres() {
        return valorPres;
    }

    public void setValorPres(int valorPres) {
        this.valorPres = valorPres/10;
    }

    public int getValorRPM() {
        return valorRPM;
    }

    public void setValorRPM(int valorRPM) {
        this.valorRPM = valorRPM;
    }
    
     public double getValRPM() {
        return valRPM;
    }

    public void setValRPM(double valRPM) {
        this.valRPM = valRPM;
    }

    public int getValorTAceiteSinTabla() {//segun na table sugerida por fabriante de sensores
        return this.valorTAceite;
    }

    public int getValorTAceite(){//temperatura con tabla solamente usada por el banco Sensors ojo refactoirizar cuando se pueda
        double tempCalc = obtenerTempTabla(this.valorTAceite);
        return (int)tempCalc;//redondeando
    }//cambiar del programa de servicio el archivo temp.txt

    public void setValorTAceite(int valorTAceite) {
        this.valorTAceite = valorTAceite;
    }

    public String getCadenaCO() {
        //poner el valor de CO en 0.01%
        if(this.getValorCO() <= 0){
            return "0.00%";
        } else {
        String formateado = df2.format(this.getValorCO());
        return formateado +"%";
        }//return cadenaCO;
    }

    public void setCadenaCO(String cadenaCO) {
        this.cadenaCO = cadenaCO;
    }

    public void setCO2range(boolean CO2range) {
        this.CO2range = CO2range;
    }

    public void setCOrange(boolean COrange) {
        this.COrange = COrange;
    }

    public void setCalentamiento(boolean Calentamiento) {
        this.Calentamiento = Calentamiento;
    }

    public void setHCrange(boolean HCrange) {
        this.HCrange = HCrange;
    }

    public void setInterna(boolean Interna) {
        this.Interna = Interna;
    }

    public void setNOxRange(boolean NOxRange) {
        this.NOxRange = NOxRange;
    }

    public void setO2range(boolean O2range) {
        this.O2range = O2range;
    }

    public void setBajoFlujo(boolean bajoFlujo) {
        this.bajoFlujo = bajoFlujo;
    }

    public void setDilucion(boolean dilucion) {
        this.dilucion = dilucion;
    } 

    public void setCero(boolean cero) {
        this.cero = cero;
    }

    public void setCondensacion(boolean condensacion) {
        this.condensacion = condensacion;
    }

    public void setHiHC(boolean hiHC) {
        this.hiHC = hiHC;
    }

    public void setNoExacto(boolean noExacto) {
        this.noExacto = noExacto;
    }

    public void setCalibracion(boolean calibracion) {
        this.calibracion = calibracion;
    }

    public void setCalibracionEnProgreso(boolean calibracionEnProgreso) {
        this.calibracionEnProgreso = calibracionEnProgreso;
    }

    public void setCeroEnProgreso(boolean ceroEnProgreso) {
        this.ceroEnProgreso = ceroEnProgreso;
    }

    public void setPresionRange(boolean presionRange) {
        this.presionRange = presionRange;
    }

    public void setRpmRange(boolean rpmRange) {
        this.rpmRange = rpmRange;
    }

    public void setTempAceiteRange(boolean tempAceiteRange) {
        this.tempAceiteRange = tempAceiteRange;
    }

    public void setTempAmbienRange(boolean tempAmbienRange) {
        this.tempAmbienRange = tempAmbienRange;
    }

    public void setTempDetectorRange(boolean tempDetectorRange) {
        this.tempDetectorRange = tempDetectorRange;
    }

    public void setVacioRange(boolean vacioRange) {
        this.vacioRange = vacioRange;
    }

    public void setCO3digitos(boolean CO3digitos) {
        this.CO3digitos = CO3digitos;
    }

    public void setChanelError(boolean ChanelError) {
        this.ChanelError = ChanelError;
    }

    public void setHCpropano(boolean HCpropano) {
        this.HCpropano = HCpropano;
    }

    public void setBajaSenialDetector(boolean BajaSenialDetector) {
        this.BajaSenialDetector = BajaSenialDetector;
    }

    public void setErrorLampara(boolean ErrorLampara) {
        this.ErrorLampara = ErrorLampara;
    }

    public void setBadNOxSensor(boolean badNOxSensor) {
        this.badNOxSensor = badNOxSensor;
    }

    public void setBadO2Sensor(boolean badO2Sensor) {
        this.badO2Sensor = badO2Sensor;
    }

    public void setCeroInicialEnProgreso(boolean ceroInicialEnProgreso) {
        this.ceroInicialEnProgreso = ceroInicialEnProgreso;
    }

    public void setErrorEEPROM(boolean errorEEPROM) {
        this.errorEEPROM = errorEEPROM;
    }

    public void setNuevoDatoGas(boolean nuevoDatoGas) {
        this.nuevoDatoGas = nuevoDatoGas;
    }

    public void setNuevoDatoRpm(boolean nuevoDatoRpm) {
        this.nuevoDatoRpm = nuevoDatoRpm;
    }
    
    
    
//Retornar el valor de la cadena en ppm
    public String getCadenaHC() {
        if(this.getValorHC() <= 0){
            return "0 ppm";
        } else {
        return String.valueOf(this.getValorHC()) + "ppm";
        }//end else
    }//end method

    public String getCadenaNox() {
        return String.valueOf(this.getValorNox())+"ppm";
    }

    //ojo esto puede cambiar segun configuración del sensor
    public String getCadenaPress() {
        return String.valueOf(this.getValorPres()+"mBar");
    }

    public String getCadenaRH() {
        return String.valueOf(this.getValorRH())+"%";
    }

    public String getCadenaRPM() {
        return String.valueOf(this.getValorRPM()) + "rpm";
    }

    public String getCadenaRef() {
        return String.valueOf(this.getValorRef());
    }

    public String getCadenaTAceite() {
        return String.valueOf(this.getValorTAceite()) + "ºC";
    }   

    public String getCadenaTAmbiente() {
        return String.valueOf(this.getValorTAmbiente()) +"ºC";
    }

    public String getCadenaTDetect() {
        return String.valueOf(this.getCadenaTDetect()) +"ºC";
    }

    public String getCadenaTLamp() {
        return String.valueOf(this.getCadenaTLamp()) + "ºC";
    }

    

    public String getCadenaText() {
        return String.valueOf(this.getValorTExt()) + "ºC";
    }

    

    public String getCadencaCO2() {
        if(this.getValorCO2() <= 0){
            return "0.00%";
        }else {
        double resultado = this.getValorCO2();
        String formateado = df2.format(resultado);
        return formateado + "%";
        }
        //return cadencaCO2;
    }

    public String getCadenaO2() {
        if(this.getValorO2() <= 0){
         return "0.00%";
        }else {
        String formateado = df2.format(this.getValorO2());
        return formateado + "%";
        }
    }

    public int getValorLambda() {
        return valorLambda;
    }

    

    public String toString(){
        String cadena ="Medición de HC:" + valorHC;
         cadena += "\nMedición de CO:" + valorCO;
         cadena += "\nMedición de CO2:" + valorCO2;
         cadena += "\nMedición de 02:" + valorO2;
         cadena += "\nMedición de NoX:" + valorNox;
         cadena += "\nMedición de RPM:" + valorRPM;
         cadena += "\nMedición de TempAceite:" + valorTAceite;
         cadena += "\nMedición de Temperatura Ambiente:" + valorTAmbiente;
         cadena += "\nMedición de Presion:" + valorPres;
         cadena += "\nMedición de RH:"+ valorRH;
         cadena += "\nMedición de External Temp:" + valorTExt;
         cadena += "\nMedición de Reference:" + valorRef;
         cadena += "\nMedicion de Lamp Temp:" + valorTLamp;
         cadena += "\nMedicion de Deterctor Temp" +valorDetectorTemp;
         return cadena;

    }//end of toString

    private double obtenerTempTabla(int temp) {        
         double x1 = 0; double x2 = 0; double y1 = 0; double y2 = 0;
         double temperatura = 0;
         for (int i = 0; i < 19; i++){
            if(estaRango(temp,x[i],x[i+1])){
                x1 = x[i]; x2 =  x[i+1];
                y1 = y[i]; y2 = y[i+1];
                double pendiente = (y2 - y1)/(x2 -x1);
                double intersecto = y1 - pendiente*x1;
                temperatura = pendiente*temp + intersecto ;
                return temperatura;
          }//end if
       }//end for
     return 0;
   }

    private boolean estaRango(double t,double menor, double mayor){
        if (t >= menor && t <= mayor )
         return true;
       else
         return false;
    }

    /**
     * @return the vacuumSwitch
     */
    public boolean isVacuumSwitch() {
        return vacuumSwitch;
    }

    /**
     * @param vacuumSwitch the vacuumSwitch to set
     */
    public void setVacuumSwitch(boolean vacuumSwitch) {
        this.vacuumSwitch = vacuumSwitch;
    }

    /**
     * @return the convertidorCatalitico
     */
    public boolean isConvertidorCatalitico() {
        return convertidorCatalitico;
}

    /**
     * @param convertidorCatalitico the convertidorCatalitico to set
     */
    public void setConvertidorCatalitico(boolean convertidorCatalitico) {
        this.convertidorCatalitico = convertidorCatalitico;
    }
}
