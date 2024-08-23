/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.ci2;

import com.soltelec.ci2.*;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;


import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.rpc.ServiceException;

/**
 *
 * @author GerenciaDesarrollo
 */
public class ClienteCi2 {

   private String urlSicov2;
  private String urlSicovEncript;
    private String urlSicov;
    final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ClienteCi2.class);

    public ClienteCi2(String urlSicov) {
        try {
            this.urlSicov = urlSicov;
        } catch (Exception e) {
        }
    }

    public String getUrlSicov2() {
        return urlSicov2;
    }

    public void setUrlSicov2(String urlSicov2) {
        this.urlSicov2 = urlSicov2;
    }

    public String getUrlSicovEncript() {
        return urlSicovEncript;
    }

    public void setUrlSicovEncript(String urlSicovEncript) {
        this.urlSicovEncript = urlSicovEncript;
    }
    
    public RespuestaDTO utilizarPin(Pin pin) {
        try {         
            logger.info("Metodo utilizar pin url de servicio: " + urlSicov);          
            DatosSoap stub = new DatosLocator(urlSicov).getdatosSoap(); 
            Resultado resultado = stub.utilizarPin(pin);
            logger.info("Resultado de ejecutar pin: " + resultado);
            return new RespuestaDTO(resultado.getCodigoRespuesta(), resultado.getMensajeRespuesta());
        } catch (Exception  e) {
            logger.error("Error al iniciar el pin ", e);
           // throw new SartComunicadorException(500, e.getMessage());
        }
        return null;
    }

    public RespuestaDTO consultarPinPlaca(Pin pin) {
        try {            
            DatosSoap stub = new DatosLocator(this.urlSicov).getdatosSoap();
            Resultado resultado = stub.consultaPinPlaca(pin);
            return new RespuestaDTO( resultado.getCodigoRespuesta(), resultado.getMensajeRespuesta());
        } catch (Exception  e ) {
            logger.error("Error al consultar el pin", e);
            //throw new SartComunicadorException(500, e.getMessage());
        }
        
        return null;
    }

    public static void main(String[] args) {
        try {           
            URL endpoint = new java.net.URL("http://192.168.248.90/ci2_cda_ws/sincrofur.asmx");
        } catch (MalformedURLException ex) {
            Logger.getLogger(ClienteCi2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    public String getUrlSicov() {
        return urlSicov;
    }   

}
