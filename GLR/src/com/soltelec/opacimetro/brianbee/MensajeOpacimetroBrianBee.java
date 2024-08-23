package com.soltelec.opacimetro.brianbee;

import java.util.ArrayList;
import java.util.List;

public class MensajeOpacimetroBrianBee
{
  private String comando;
  private List<String> datos;

  public MensajeOpacimetroBrianBee(String comando){
    this.comando = comando;
    this.datos = new ArrayList();
  }

  public String getComando() {
    return this.comando;
  }

  public void setComando(String comando) {
    this.comando = comando;
  }

  public List<String> getDatos() {
    return this.datos;
  }

  public void setDatos(List<String> datos) {
    this.datos = datos;
  }
}