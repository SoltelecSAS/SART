/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.indra;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author user
 */
public class EventoDTO  implements Serializable{
  private String codigoProveedor = "";
  private String fecha = "";
  private Integer idPrueba = Integer.valueOf(0);
  private String placa = "";
  private String serialEquipo = "";
  private EstadoEventosSicov codigoMensaje = EstadoEventosSicov.INICIADO;
  private String mensajeEvento = "";
  private String nombrePrueba = "";
  private Integer idEvento = Integer.valueOf(0);
  private String idRunt = "";
  

  public String getCodigoProveedor()
  {
    return this.codigoProveedor;
  }

  public void setCodigoProveedor(String codigoProveedor)
  {
    this.codigoProveedor = codigoProveedor;
  }

  public String getFecha()
  {
    if ("".equals(this.fecha)) {
      this.fecha = new SimpleDateFormat("yyy-MM-dd hh:mm:ss").format(new Date());
    }
    return this.fecha;
  }

  public void setFecha(String fecha)
  {
    this.fecha = fecha;
  }

  public String getPlaca()
  {
    return this.placa;
  }

  public void setPlaca(String placa)
  {
    this.placa = placa;
  }

  public String getSerialEquipo()
  {
    return this.serialEquipo;
  }

  public void setSerialEquipo(String serialEquipo)
  {
    this.serialEquipo = serialEquipo;
  }

  public String getMensajeEvento()
  {
    return this.mensajeEvento;
  }

  public void setMensajeEvento(String mensajeEvento)
  {
    this.mensajeEvento = mensajeEvento;
  }

  public Integer getIdPrueba()
  {
    return this.idPrueba;
  }

  public void setIdPrueba(Integer idPrueba)
  {
    this.idPrueba = idPrueba;
  }

    public Integer getIdEvento() {
        return idEvento;
    }

    public String getIdRunt() {
        return idRunt;
    }

    public void setIdRunt(String idRunt) {
        this.idRunt = idRunt;
    }

    public void setIdEvento(Integer idEvento) {
        this.idEvento = idEvento;
    }

  
  @Override
  public String toString()
  {
    return 
            this.codigoProveedor + "|" + 
            getFecha() + "|" + 
            this.nombrePrueba + "|" + 
            this.placa + "|" + 
            this.serialEquipo + "|" + 
            this.idEvento.toString() + "|" + 
            this.mensajeEvento + "|" + 
            this.idRunt;
  }

  public String getNombrePrueba()
  {
    return this.nombrePrueba;
  }

  public void setNombrePrueba(String nombrePrueba)
  {
    this.nombrePrueba = nombrePrueba;
  }

  public EstadoEventosSicov getCodigoMensaje()
  {
    return this.codigoMensaje;
  }

  public void setCodigoMensaje(EstadoEventosSicov codigoMensaje)
  {
    this.codigoMensaje = codigoMensaje;
  }
}
