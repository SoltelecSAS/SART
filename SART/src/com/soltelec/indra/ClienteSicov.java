/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.indra;

/**
 *
 * @author user
 */
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.soltelec.indra.*;
import com.soltelec.model.Cda;
import com.soltelec.indra.clienteSicov.SartComunicadorException;
        ;

public class ClienteSicov {
  
  static final Logger logger = Logger.getLogger(ClienteSicov.class);

  public ClienteSicov()  {
    
  }

  
  /**
   * Metodo que crea evento 
   * 
   * @param evento
   * @param cda
   * @return 
   */
  public RespuestaDTO crearEvento(EventoDTO evento,Cda cda)
  {
    logger.info("------------------------------------------------------------");
    logger.info("------------Creando Evento para la prueba-------------------");
    logger.info("------------------------------------------------------------");
    try  
    {
        System.out.println("-- Url Servicio Sicov : " + cda.getUrlServicioSicov());
        System.out.println("-- Url Servicio Encript : " +  cda.getUrlServicioEncript());
        ClienteIndra indra = new ClienteIndra(cda.getUrlServicioSicov(), cda.getUrlServicioEncript());
        
        System.out.println("-- Setiando parametros a ws ");
        indra.setUrlSicov(cda.getUrlServicioSicov());
        indra.setUrlSicovEncript(cda.getUrlServicioEncript());

        RespuestaDTO respuesta = null;
        System.out.println("----Invocando el metodo : setEvento()");
        respuesta = indra.setEvento(evento.toString());// String trama
        
        System.out.println("----Respuesta del metodo setEvento():  " + respuesta);
        System.out.println("----Finaliza el envio del evento para la prueba------------");
        
        return respuesta;
        
      } catch (SartComunicadorException e) 
      {
          logger.error("Error en el metodo:crearEvento(), Error al enviado el  Evento ", e);
          logger.error("Error 500 Comunicacion con Sicov por favor llamar mesa de ayuda", e);
          throw new SartComunicadorException(500, e.getMensaje());
      }
  }

 
}
