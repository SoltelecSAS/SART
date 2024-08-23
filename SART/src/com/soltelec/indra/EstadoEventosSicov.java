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
public enum EstadoEventosSicov{
  INICIADO(Integer.valueOf(1)), CANCELADO(Integer.valueOf(3)), ABORTADO(Integer.valueOf(3)), FINALIZADO(Integer.valueOf(2)), PENDIENTE(Integer.valueOf(5)), FALLIDO(Integer.valueOf(6)), SINCRONIZADO(Integer.valueOf(7));

  private Integer idEvento;

  private EstadoEventosSicov(Integer idEvento) { this.idEvento = idEvento; }


  public Integer getIdEvento()
  {
    return this.idEvento;
  }
}