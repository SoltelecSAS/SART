/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.indra;

import static com.soltelec.indra.ClienteSicov.logger;
import com.soltelec.indra.clienteSicov.FURRespuesta;
import com.soltelec.indra.clienteSicov.SicovLocator;
import com.soltelec.indra.clienteSicov.SicovSoap;
import com.soltelec.indra.clienteSicov.Encript;
//import com.soltelec.clientes.util.UtilPropiedades;
import com.soltelec.indra.RespuestaDTO;
import com.soltelec.indra.clienteSicov.SartComunicadorException;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import javax.xml.rpc.ServiceException;
import org.apache.log4j.Logger;

public class ClienteIndra
{
  static final Logger logger = Logger.getLogger(ClienteIndra.class);
  private String urlSicov;
  private String urlSicov2;
  private String urlSicovEncript;
  private boolean test;
  private boolean testerror;

    public ClienteIndra() {
    }

  
  
    public ClienteIndra(String urlSicov,String urlSicovEncript)  
    {
        System.out.println("---------------------------------------------");
        System.out.println("-------------------ClienteIndra--------------");
        System.out.println("---------------------------------------------");
        try  
        {
          this.test =false;// Boolean.parseBoolean(UtilPropiedades.cargarPropiedad("test", "propiedades.properties"));
          this.testerror =false;// (!Boolean.parseBoolean(UtilPropiedades.cargarPropiedad("testerror", "propiedades.properties")));
          this.urlSicov2="http://186.116.14.44:8056/sicov.asmx";
        } catch (Exception e) {
            
        }
  }

    
    /**}
     * Metodo que consume el servicio web y envia eventos 
     * 
     * @param trama
     * @return 
     */
    public RespuestaDTO setEvento(String trama)
    {
        logger.info("-----------------------------------------------------------");
        logger.info("------------------ Enviando Evento-------------------------");
        logger.info("------------------ ----------------------------------------");
        try
        {
            if (this.test) 
            {
                if (this.testerror) 
                {
                    return new RespuestaDTO("1", "Exitoso");
                }
                return new RespuestaDTO("0", "Error");
            }   
            SicovSoap sicovSoap = new SicovLocator(this.urlSicov,this.urlSicov2).getSicovSoap();
            
            logger.info("--Enviado el eveto------------------------------------");
            logger.info("------------------------------------------------------");
            logger.info("--Trama a enviar : " + trama );
            logger.info("------------------------------------------------------");
            
            String tramaEncript = Encript.encript(trama, this.urlSicovEncript);
            FURRespuesta fURRespuesta = sicovSoap.setEventsRTMEC(tramaEncript);
            return new RespuestaDTO(String.valueOf(fURRespuesta.getCodRespuesta()), fURRespuesta.getMsjRespuesta());
        } catch (ServiceException | RemoteException e)
        {
            logger.error("Error en el metodo : setEvento()"+e.getMessage());
            throw new SartComunicadorException(500, e.getMessage());
        }
    }
    
    
//    public static void main(String[] args) 
//    {
//        ClienteIndra aa=new ClienteIndra();
//        aa.setEvento("858|2020-11-07 11:06:14|VISUAL|EFK57E|PDC-01|1||");
//    }
//    
    
    

  public RespuestaDTO setFur(String trama) {
    try {
      if (this.test) {
        if (this.testerror) {
          return new RespuestaDTO("1", "Exitoso");
        }
        return new RespuestaDTO("0", "Error");
      }

      SicovSoap sicovSoap = new SicovLocator(this.urlSicov, this.urlSicov2).getSicovSoap();

      String tramaEncript = Encript.encript(trama, this.urlSicovEncript);

      FURRespuesta fURRespuesta = sicovSoap.setFUR(tramaEncript);
      return new RespuestaDTO(String.valueOf(fURRespuesta.getCodRespuesta()), fURRespuesta.getMsjRespuesta());
    } catch (ServiceException|RemoteException e) {
      throw new SartComunicadorException(500, e.getMessage());
    }
  }

      public RespuestaDTO getInfoPin(String placa, String codigoRunt)
    {
        logger.info("-----------------------------------------------------------");
        logger.info("------------------ Enviando getInfoPin-------------------------");
        logger.info("------------------ ----------------------------------------");
        System.out.println("Voy a entrar al try de GETINFOPIN");
        try
        {
            if (this.test) 
            {
                if (this.testerror) 
                {
                    return new RespuestaDTO("1", "Exitoso");
                }
                return new RespuestaDTO("0", "Error");
            }   

            com.soltelec.indra.clienteSicov2.SicovLocator sicovLocator = new com.soltelec.indra.clienteSicov2.SicovLocator(this.urlSicov,this.urlSicov2);

            com.soltelec.indra.clienteSicov2.SicovSoap sicovSoap = sicovLocator.getSicovSoap();
            
            logger.info("------------------------------------------------------");
            logger.info("-getInfoPin : " + placa );
            logger.info("------------------------------------------------------");

            logger.info("------------------------------------------------------");
            logger.info("-codigo Runt : " + codigoRunt );
            logger.info("------------------------------------------------------");

            logger.info("------------------------------------------------------");
            logger.info("-codigo Runt : " + codigoRunt );
            logger.info("------------------------------------------------------");

            logger.info("------------------------------------------------------");
            logger.info("-endpoint : " + sicovLocator.getEndpoint() );
            logger.info("------------------------------------------------------");
            
            String fechaActual = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            com.soltelec.indra.clienteSicov2.PinRespuesta pinRespuesta = sicovSoap.getInfoPin(placa, codigoRunt, fechaActual);
            
            logger.info("------------------------------------------------------");
            logger.info("-Respuesta : " + pinRespuesta );
            logger.info("-Respuesta pin : " + pinRespuesta.getPinRespuesta() );
            logger.info("------------------------------------------------------");
            
            if(Objects.isNull(pinRespuesta) || Objects.isNull(pinRespuesta.getPinRespuesta())){
                return new RespuestaDTO(String.valueOf(-1), "");
            }
            
            if(pinRespuesta.getCodRespuesta() == 0){
                return new RespuestaDTO(String.valueOf(-1), "");
            }
            return new RespuestaDTO(String.valueOf(pinRespuesta.getPinRespuesta().getPruebaActiva()), pinRespuesta.getMsjRespuesta());
        } catch (ServiceException | RemoteException e)
        {
            System.out.println("ERROR EN GETINFOPIN");
            logger.error("Error en el metodo : setEvento()"+e.getMessage());
            throw new SartComunicadorException(500, e.getMessage());
        }
    }
  
  public String getUrlSicov()
  {
    return this.urlSicov;
  }

  public void setUrlSicov(String urlSicov)
  {
    this.urlSicov = urlSicov;
  }

  public String getUrlSicov2()
  {
    return this.urlSicov2;
  }

  public void setUrlSicov2(String urlSicov2)
  {
    this.urlSicov2 = urlSicov2;
  }

  public String getUrlSicovEncript()
  {
    return this.urlSicovEncript;
  }

  public void setUrlSicovEncript(String urlSicovEncript)
  {
    this.urlSicovEncript = urlSicovEncript;
  }
}