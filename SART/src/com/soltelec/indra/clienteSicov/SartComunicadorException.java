/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.indra.clienteSicov;

/**
 *
 * @author user
 */
public class SartComunicadorException extends RuntimeException{
  private int errorCode;
  private String mensaje;

  public SartComunicadorException(int errorCode, String mensaje) {
    this.errorCode = errorCode;
    this.mensaje = mensaje;
  }

  public SartComunicadorException(String mensaje) {
    this.errorCode = 500;
    this.mensaje = mensaje;
  }

  public SartComunicadorException()  {
  }

  public int getErrorCode()  {
    return this.errorCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  public String getMensaje()  {
    return this.mensaje;
  }

  public void setMensaje(String mensaje) {
    this.mensaje = mensaje;
  }

  public String toString() {
    return "SartComunicadorException{errorCode=" + this.errorCode + ", mensaje=" + this.mensaje + '}';
  }
  
}
