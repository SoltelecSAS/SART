package com.soltelec.opacimetro.brianbeeold;

/*    */
 /*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.soltelec.pruebasgases.diesel.HiloTomaDatosDiesel;
/*    */ import org.soltelec.util.MedicionOpacidad;
/*    */ import org.soltelec.util.UtilGasesModelo;

/*    */
 /*    */ public class HiloTomaDatosDieselBrianBee extends HiloTomaDatosDiesel /*    */ {

    /*    */ private final List<Integer> listaOpacidadInteger;
    /*    */    private MedicionOpacidad medicion;

    /*    */
 /*    */ public HiloTomaDatosDieselBrianBee() /*    */ {
        /* 16 */ this.listaOpacidadInteger = new ArrayList();
        /*    */    }

    /*    */
 /*    */ public void run() {
        /* 20 */ OpacimetroBrianBee opa = (OpacimetroBrianBee) this.opacimetro;
        /* 21 */ while (!this.cicloTerminado) {
            /* 22 */ this.medicion = opa.obtenerOpacidad();
            /* 23 */ this.listaOpacidadInteger.add(Integer.valueOf(this.medicion.getOpacidad()));
            /*    */ try {
                /* 25 */ Thread.sleep(10L);
                /*    */            } /*    */ catch (InterruptedException ex) {
            }
            /*    */        }
        /*    */    }

    /*    */
 /*    */ public List<Byte> getListaOpacidad() {
        /* 32 */ this.listaOpacidad = procesarLista(this.listaOpacidadInteger);
        /* 33 */ return super.getListaOpacidad();
        /*    */    }

    /*    */
 /*    */ private List<Byte> procesarLista(List<Integer> listaOpacidadInteger) {
        /* 37 */ List<Byte> lista = new ArrayList();
        /*    */
 /* 39 */ byte[] opacidadBytes = new byte[2];
        /* 40 */ for (int i = 0; i < listaOpacidadInteger.size(); i++) {
            /* 41 */ int opacidad = ((Integer) listaOpacidadInteger.get(i)).intValue();
            /* 42 */ opacidadBytes = UtilGasesModelo.toBytes((short) opacidad);
            /* 43 */ lista.add(2 * i, Byte.valueOf(opacidadBytes[1]));
            /* 44 */ lista.add(2 * i + 1, Byte.valueOf(opacidadBytes[0]));
            /*    */        }
        /* 46 */ return lista;
        /*    */    }
    /*    */ }


/* Location:              /home/luisberna/Dropbox/maven-soltelec/Sart-git-3/modulos/ModuloGLR.jar!/com/soltelec/opacimetro/brianbee/HiloTomaDatosDieselBrianBee.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
