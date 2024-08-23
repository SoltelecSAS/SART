package com.soltelec.opacimetro.brianbee;

import java.util.ArrayList;
import java.util.List;
import org.soltelec.pruebasgases.diesel.HiloTomaDatosDiesel;
import org.soltelec.util.MedicionOpacidad;
import org.soltelec.util.UtilGasesModelo;

public class HiloTomaDatosDieselBrianBee extends HiloTomaDatosDiesel
{
  private List<Integer> listaOpacidadInteger;
  private MedicionOpacidad medicion;
 
  public HiloTomaDatosDieselBrianBee()
  {
    this.listaOpacidadInteger = new ArrayList();
  }

  public void run() {
    OpacimetroBrianBee opa = (OpacimetroBrianBee)this.opacimetro;
    while (!this.cicloTerminado) {
      this.medicion = opa.obtenerOpacidad();
      this.listaOpacidadInteger.add(Integer.valueOf(this.medicion.getOpacidad()));
      try {
        Thread.sleep(10L);
      } catch (InterruptedException ex) {
      }
    }
  }

  public List<Byte> getListaOpacidad() {
    this.listaOpacidad = procesarLista(this.listaOpacidadInteger);
    return super.getListaOpacidad();
  }

  private List<Byte> procesarLista(List<Integer> listaOpacidadInteger) {
    List lista = new ArrayList();

    byte[] opacidadBytes = new byte[2];
    for (int i = 0; i < listaOpacidadInteger.size(); i++) {
      int opacidad = ((Integer)listaOpacidadInteger.get(i)).intValue();
      opacidadBytes = UtilGasesModelo.toBytes((short)opacidad);
      lista.add(2 * i, Byte.valueOf(opacidadBytes[1]));
      lista.add(2 * i + 1, Byte.valueOf(opacidadBytes[0]));
    }
    return lista;
  }
}