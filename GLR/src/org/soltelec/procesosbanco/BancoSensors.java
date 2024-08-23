/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosbanco;

import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.soltelec.medicionrpm.MedidorRevTemp;
import org.soltelec.util.*;
import org.soltelec.util.capelec.BancoCapelec.TipoCalibracion;
import org.soltelec.models.controllers.EquipoController;

/**
 *
 * @author GerenciaDesarrollo
 */
public class BancoSensors implements BancoGasolina, MedidorRevTemp {

    private SerialPort puertoSerial;
    MedicionGases medicion;

    OutputStream out;//Salida hacia el puerto serial
    InputStream in;//Entrada del puerto serial
    private boolean tiempoTerminado = false;//inicia por defecto en falso
    Timer timer;
    private int factorRPM = 2;
    //El propósito de este mapa es imprimir todos los mensajes de advertencia en una pasada de el comando de obtener estatus, posible mejora con transcripción del software
    static Map<IndiceAdvertencias, String> mapaAdvertencias = new HashMap<IndiceAdvertencias, String>();

    static {
        inicializarMapa();
    }

    static void inicializarMapa() {//fila indica numero de palabra de status
        //status word cero
        mapaAdvertencias.put(new IndiceAdvertencias(0, 0), "Banco esta en calentamiento");
        mapaAdvertencias.put(new IndiceAdvertencias(0, 1), "Banco pide cero");
        mapaAdvertencias.put(new IndiceAdvertencias(0, 2), "Nuevo Transductor de O2 instalado");
        mapaAdvertencias.put(new IndiceAdvertencias(0, 3), "CalZero = 1:(SW 1!= 0)||(SW 2!= 0)||(SW 3!= 0)");
        mapaAdvertencias.put(new IndiceAdvertencias(0, 4), "Advertencia de Calibración o de 0 para el O2");
        mapaAdvertencias.put(new IndiceAdvertencias(0, 5), "Advertencia de Bajo Flujo");
        mapaAdvertencias.put(new IndiceAdvertencias(0, 6), "Nuevo Transductor de O2 instalado");
        mapaAdvertencias.put(new IndiceAdvertencias(0, 7), "Advertencia de Calibración o de Zero para el NOx");
        mapaAdvertencias.put(new IndiceAdvertencias(0, 8), "Advertencia de Condensación");
        mapaAdvertencias.put(new IndiceAdvertencias(0, 14), "ADrailed");
        mapaAdvertencias.put(new IndiceAdvertencias(0, 15), "Interna revisar status word 6");

        //status word uno
        mapaAdvertencias.put(new IndiceAdvertencias(1, 0), "Alarma de Zero para HC");
        mapaAdvertencias.put(new IndiceAdvertencias(1, 1), "Alarma de Zero para CO");
        mapaAdvertencias.put(new IndiceAdvertencias(1, 2), "Alarma de Zero para CO2");
        mapaAdvertencias.put(new IndiceAdvertencias(1, 4), "Alarma de Zero para Ref");
        mapaAdvertencias.put(new IndiceAdvertencias(1, 5), "Alarma de Zero para O2");
        mapaAdvertencias.put(new IndiceAdvertencias(1, 6), "Alarma de Zero para NOx");
        //status word dos calibración de un solo punto
        mapaAdvertencias.put(new IndiceAdvertencias(2, 0), "Alarma de calibracion de un solo punto para HC");
        mapaAdvertencias.put(new IndiceAdvertencias(2, 1), "Alarma de calibracion de un solo punto para CO");
        mapaAdvertencias.put(new IndiceAdvertencias(2, 2), "Alarma de calibracion de un solo punto para CO2");
        mapaAdvertencias.put(new IndiceAdvertencias(2, 5), "Alarma de calibracion de un solo punto para O2");
        mapaAdvertencias.put(new IndiceAdvertencias(2, 6), "Alarma de calibracion de un solo punto para NOx");
        //status word tres calibración de dos puntos
        mapaAdvertencias.put(new IndiceAdvertencias(3, 0), "Alarma de calibracion de DOS PUNTOS para HC");
        mapaAdvertencias.put(new IndiceAdvertencias(3, 1), "Alarma de calibracion de DOS PUNTOS para CO");
        mapaAdvertencias.put(new IndiceAdvertencias(3, 2), "Alarma de calibracion de DOS PUNTOS para CO2");
        mapaAdvertencias.put(new IndiceAdvertencias(3, 6), "Alarma de calibracion de DOS PUNTOS para NOx");
        //status word 4 y 5 reservadas para uso futuro
        //status word 6 para alarmas internas
        mapaAdvertencias.put(new IndiceAdvertencias(6, 1), "Block Heater out of control LED 10 Hz");
        mapaAdvertencias.put(new IndiceAdvertencias(6, 2), "02 offset Warning");
        mapaAdvertencias.put(new IndiceAdvertencias(6, 13), "NDIR beam strength error if the value of HC or CO is less than 66% of the initial value during production");
        mapaAdvertencias.put(new IndiceAdvertencias(6, 14), "Iteration error");
        mapaAdvertencias.put(new IndiceAdvertencias(6, 15), "EEPROM incompatibe o corrupta, LED 5Hz");
        //status word 7 para AD channel railed
        mapaAdvertencias.put(new IndiceAdvertencias(7, 0), "HC channel railed (AD counts == 0)|(AD counts == MAX_AD)");
        mapaAdvertencias.put(new IndiceAdvertencias(7, 1), "CO channel railed (AD counts == 0)|(AD counts == MAX_AD)");
        mapaAdvertencias.put(new IndiceAdvertencias(7, 2), "CO2 channel railed (AD counts == 0)|(AD counts == MAX_AD)");
        mapaAdvertencias.put(new IndiceAdvertencias(7, 3), "O2 channel railed (AD counts == 0)|(AD counts == MAX_AD)");
        mapaAdvertencias.put(new IndiceAdvertencias(7, 4), "NOx channel railed (AD counts == 0)|(AD counts == MAX_AD)");
        mapaAdvertencias.put(new IndiceAdvertencias(7, 5), "RPM channel railed (AD counts == 0)|(AD counts == MAX_AD)");
        mapaAdvertencias.put(new IndiceAdvertencias(7, 6), "Oil channel railed (AD counts == 0)|(AD counts == MAX_AD)");
        mapaAdvertencias.put(new IndiceAdvertencias(7, 7), "Temp Ambient channel railed (AD counts == 0)|(AD counts == MAX_AD)");
        mapaAdvertencias.put(new IndiceAdvertencias(7, 8), "Pressure channel railed (AD counts == 0)|(AD counts == MAX_AD)");
        mapaAdvertencias.put(new IndiceAdvertencias(7, 9), "Reference channel railed (AD counts == 0)|(AD counts == MAX_AD)");
        mapaAdvertencias.put(new IndiceAdvertencias(7, 10), "Temp Externa channel railed (AD counts == 0)|(AD counts == MAX_AD)");
        mapaAdvertencias.put(new IndiceAdvertencias(7, 11), "Temp Block Heater channel railed (AD counts == 0)|(AD counts == MAX_AD)");
        mapaAdvertencias.put(new IndiceAdvertencias(7, 12), "Relative humidity channel railed (AD counts == 0)|(AD counts == MAX_AD)");
        mapaAdvertencias.put(new IndiceAdvertencias(7, 13), "Lamp temperature channel railed (AD counts == 0)|(AD counts == MAX_AD)");
        //Status word 8 calibración necesaria
        mapaAdvertencias.put(new IndiceAdvertencias(8, 8), "If new 02 transducer is installed");
        mapaAdvertencias.put(new IndiceAdvertencias(8, 9), "Nox zero If new NO transducer is installed");
        mapaAdvertencias.put(new IndiceAdvertencias(8, 10), "NO calibracio un punto If new NO transducer is installed");
        //Status word 9 estado de calentamiento
        mapaAdvertencias.put(new IndiceAdvertencias(9, 0), "Deterctor de temperatura de bloque fuera de rango");
        mapaAdvertencias.put(new IndiceAdvertencias(9, 1), "Detector de temperatura de bloque inestable");
        mapaAdvertencias.put(new IndiceAdvertencias(9, 2), "Temperatura de lampara < temperatura ambiente");
        mapaAdvertencias.put(new IndiceAdvertencias(9, 3), "Analog digital converter is railed");
        mapaAdvertencias.put(new IndiceAdvertencias(9, 4), "Temperatura de lampara inestable");
        mapaAdvertencias.put(new IndiceAdvertencias(9, 5), "Canal de referencia inestable");
        mapaAdvertencias.put(new IndiceAdvertencias(9, 6), "Low IR channel(s) voltaje");
        mapaAdvertencias.put(new IndiceAdvertencias(9, 7), "DAC innestable");
        mapaAdvertencias.put(new IndiceAdvertencias(9, 15), "Banco esta inestable");
    }//end of static inicialization
    private ShiftRegister shiftRegister;

    public BancoSensors() {
        shiftRegister = new ShiftRegister(4, 0);
    }

    public BancoSensors(OutputStream out, InputStream in) {
        this.out = out;
        this.in = in;
    }

    /**
     * Este metodo envia un comando y espera una respuesta.
     *
     * @param comando
     * @return
     */
    public synchronized Mensaje enviarMensaje(Mensaje comando) throws RuntimeException {
        tiempoTerminado = false;//el tiempo siempre se cumple
        int contador = 0;
        Mensaje msj;
        boolean respuestaRecibida = false;
        byte b[] = UtilGasesModelo.armarBytes(comando);//armar los bytes para enviar
        try {

            for (byte item : b) {
                System.out.println("-_-__-_-_-_-_-_-_-_-_" + item);
            }
            out.write(b);//Enviar el comando por el puerto serial           

        } catch (IOException ioe) {
            System.out.println("Error enviando el comando: " + comando.toString());
            ioe.printStackTrace(System.err);
        }
        while (respuestaRecibida == false) {
            byte[] buffer = new byte[1024];
            byte[] bleer = new byte[1];
            int data;
            try {
                int len = 0;

                while ((data = in.read()) > -1) {
                    buffer[len++] = (byte) data;
                    //data = in.read(bleer);//modificacion para que funcione en linux
                    //System.out.println("Bloqueado aqui");
                }//end while
                if (len > 0) {//si hay datos
                    System.out.println("Longitud del mensaje:" + len);
                    respuestaRecibida = true;
                    //copiar a un arreglo para armar el mensaje
                    byte arreglo[] = new byte[len];
                    //armar el mensaje
                    System.arraycopy(buffer, 0, arreglo, 0, len);
                    //Thread.sleep(25);//sincronia

                    return msj = UtilGasesModelo.armarMensaje(arreglo);
                } else {
                    System.out.println("Len: " + len);
                    respuestaRecibida = true;
                }
            } catch (IOException e) {
                if (e.getMessage().equals("Input/output error in readByte")) {
                    throw new RuntimeException(e);
                }
                //System.exit(-1);
            }//end catch
        }//end while

        //no termino en el tiempo esperado entonces retornar null indicando error
        System.out.println("La máquina no envió respuesta en el tiempo intermensaje");

        return null;
    }//end of method

    /**
     *
     */
    /**
     * Objetivo es determinar si el comando ha sido terminado, si ha terminado
     * la respuesta tiene los datos, sino entonces preguntar hasta que los datos
     * esten disponibles y devolver la respuesta
     */
    public Mensaje manejarRespuesta(Mensaje respuesta, Mensaje comandoOrig) {
        Mensaje msjDatos = null;

        switch (respuesta.getNumModoComando()) {
            case 06:
                System.out.println("Comando Recibido OK...");//el comando termino inmediatamente
                msjDatos = respuesta;
                System.out.println(msjDatos.toString());
                break;
            case 05:
                System.out.println("Maquina ocupada procesando el comando");//
                 {
                    try {
                        msjDatos = manejarModoLargoComando(comandoOrig);
                    } catch (Exception ex) {
                        Logger.getLogger(BancoSensors.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                System.out.println(msjDatos.toString());
                break;

            case 0x15:
                System.out.println("Error en la transmisión o error del comando");
                //msjDatos retorna null, se generará alguna excepción
                System.out.println(respuesta);
                break;
            default:
                System.out.println("Situación no esperada respuesta de la máquina mala ERROR GRAVE");
                System.out.println(respuesta);
                return null;

        }//end switch

        return msjDatos;
    }//end method

    /**
     * Metodo para determinar si la maquina termino de procesar el comando
     */
    private Mensaje manejarModoLargoComando(Mensaje comandoOriginal) throws Exception {
        Mensaje respuestaProcess;
        Mensaje respuestaModoLargo = null;
        Mensaje processStatus = new Mensaje((short) 11, (short) 0, (short) 0, null);//no datos
        short[] datosDeProcess;
        boolean end = false;
        while (!end) {
            //enviar el comando PROCESS_STATUS
            respuestaProcess = enviarMensaje(processStatus);
            //mirar la respuesta hasta q el primer dato de la respuesta sea 0
            System.out.println("Respuesta a process status: " + respuestaProcess.toString());
            datosDeProcess = respuestaProcess.getDatos();
            if (datosDeProcess[0] == 0) {//no hay procesos pendientes la maquina no esta busy
                short numComando = comandoOriginal.getCodigoComando();

                Mensaje bug = new Mensaje(numComando, (short) 1, (short) 0, null);
                System.out.println("Enviando comando original:" + bug.toString());
                respuestaModoLargo = enviarMensaje(bug);//modo largo esperando que
                System.out.println("Respuesta a comando modo largo:" + respuestaModoLargo.toString());
                end = true;
            } else {//un proceso esta pendiente
                end = false;  //seguir pregundando
            }//end else
        }//end while

//        comandoOriginal.setNumModoComando((short)1);//mandar el comando en modo largo
//        comandoOriginal.setNumeroDeDatos((short)0);        
        return respuestaModoLargo;

    }//end manejarModoLargoComando

    @Override
    public MedicionGases obtenerDatos() {//comando para obtener datos de sensado de la máquina
        //String retorno;
        MedicionGases datos = null;
        Mensaje respuestaComando, respuestaDatos;
        short[] datosObtener = {0x00, 0x140};
        Mensaje msjObtenerDatos = new Mensaje((short) 3, (short) 0, (short) 2, datosObtener);
        //msjObtenerDatos.setDatos(datosObtener);//??enviando datos correctamente??constructor usando arreglos de bytes
        int cntEspera = 0;
        //while (true) {
        System.out.println("obtener datos");
        respuestaComando = enviarMensaje(msjObtenerDatos);//enviar el mensaje para obtener los datos 

        if (respuestaComando != null) {
            respuestaDatos = manejarRespuesta(respuestaComando, msjObtenerDatos);//obtener los datos de respuesta?
            if (respuestaDatos == null) {
                datos = null;
            } else {
                // suponiendo que todo va bien hasta aquí, entonces procesar los datos
                datos = armarMedicion(respuestaDatos.getDatos());
                //retorno = "Datos obtenidos:" + respuestaDatos.toString();
                System.out.println("Datos obtenidos:" + respuestaDatos.toString());
                //break;
            }
        }
        /*cntEspera++;
            if (cntEspera == 5) {
                break;
            }*/
        try {
            Thread.sleep(250);
        } catch (InterruptedException ex) {
        }
        //}

        return datos;

    }//end of method obtenerDatos

    private MedicionGases armarMedicion(short[] datos) {
        //tediosa y larga rutina solamente para ver las mediciones:
        //meter un mapa
//        System.out.println("Medición de HC:" + datos[1]);
//        System.out.println("Medición de CO:" + datos[2]);
//        System.out.println("Medición de CO2:" + datos[3]);
//        System.out.println("Medición de 02:" + datos[4]);
//        System.out.println("Medición de NoX:" + datos[5]);
//        System.out.println("Medición de RPM:" + datos[6]);
//        System.out.println("Medición de TempAceite:" + datos[7]);
//        System.out.println("Medición de Temperatura Ambiente:" + datos[8]);
//        System.out.println("Medición de Presion:" + datos[9]);
//        System.out.println("Medición de RH:" + datos[10]);
//        System.out.println("Medición de External Temp:" + datos[11]);
//        System.out.println("Medición de Reference:" + datos[12]);
//        System.out.println("Medicion de Lamp Temp:" + datos[13]);
//        System.out.println("Medicion de Deterctor Temp" + datos[14]);
        MedicionGases medida = new MedicionGases();
        boolean[] banderas = UtilGasesModelo.shortToFlags(datos[0]);
        //medida.setBanderas());//poner las banderas en la medición;por polimorfismo no es posible
        medida.setBajoFlujo(banderas[0]);
        medida.setCO2range(banderas[10]);
        medida.setCOrange(banderas[9]);
        medida.setCalentamiento(banderas[7]);
        medida.setCero(banderas[4]);
        medida.setCondensacion(banderas[6]);
        medida.setHCrange(banderas[8]);
        medida.setHiHC(banderas[5]);
        medida.setInterna(banderas[15]);
        medida.setNOxRange(banderas[12]);
        medida.setNoExacto(banderas[3]);
        medida.setO2range(banderas[11]);
        medida.setValorHC(datos[1]);
        medida.setValorCO(datos[2]);
        medida.setValorCO2(datos[3]);
        medida.setValorO2(datos[4]);
        medida.setValorNox(datos[5]);
        medida.setValorRPM(datos[6] * factorRPM);
        medida.setValorTAceite(datos[7]);
        medida.setValorTAmbiente(datos[8]);
        medida.setValorPres(datos[9]);
        medida.setValorRH(datos[10]);
        medida.setValorTExt(datos[11]);
        if (datos.length > 12) {
            medida.setValorRef(datos[12]);
            medida.setValorTLamp(datos[13]);
            medida.setValorDetectorTemp(datos[14]);
        } else {
            medida.setValorRef(0);
            medida.setValorTLamp(0);
            medida.setValorDetectorTemp(0);
        }
        return medida;
    }//end imprimirDatos

    public EstadoMaquina statusMaquina() {
        Mensaje msjEstatus = new Mensaje((short) 4, (short) 0, (short) 0);
        Mensaje respuestaEstatus = null, respuestaComando;
        //String retorno;
        EstadoMaquina estado;

        respuestaEstatus = enviarMensaje(msjEstatus);
        if (respuestaEstatus != null) {
            respuestaComando = manejarRespuesta(respuestaEstatus, msjEstatus);
            //retorno = "Respuesta a obtener estatus" + respuestaComando;
            System.out.println("Respuesta a obtener estatus" + respuestaComando);

            estado = identificarEstado(respuestaComando.getDatos());
        } else {
            //retorno = "Respuesta null";
            System.out.println("Respuesta null");
            estado = null;
        }
        return estado;
    }//end method statusMaquina

    /**
     * Método que retorna el número serial de la máquina
     */
    public String numeroSerial() {
        String cadena;
        short[] temp = {2};
        Mensaje msjNumeroSerial = new Mensaje((short) 13, (short) 0, (short) 1, temp);
        Mensaje recibido = null, respuesta;
        short[] datosNumero = null;

        recibido = enviarMensaje(msjNumeroSerial);

        if (recibido != null) {
            respuesta = manejarRespuesta(recibido, msjNumeroSerial);
            try {
                datosNumero = respuesta.getDatos();
            } catch (NullPointerException ne) {
                return null;
            }

            int numeroSerial = (datosNumero[0] & 0xFFFF) + (datosNumero[1] & 0xFFFF0000);
            cadena = String.valueOf(numeroSerial);
            System.out.println("El numero serial es:" + numeroSerial);
        } else {
            cadena = null;
            System.out.println("La maquina no envia respuesta");
        }//end else
        return cadena;

    }//end of method

    /**
     * Metodo para encender el solenoide de aire 1
     */
    public String encenderSolenoideUno(boolean on) {
        String cadena;
        Mensaje comando, recibido = null, respuestaDatos;
        short[] datosOn = {1, 4, 128};
        short[] datosOff = {1, 4, 0};
        if (on == true) {
            comando = new Mensaje((short) 8, (short) 0, (short) 3, datosOn);
            cadena = "Prender";
            System.out.println("Prender");
        } else {
            comando = new Mensaje((short) 8, (short) 0, (short) 3, datosOff);
            //comando.setDatos(datosOff);
            System.out.println("Apagar");
            cadena = "Apagar";
        }
        System.out.println("encender solenoide: " + comando.toString());
        recibido = enviarMensaje(comando);//enviar el mensaje para prender la bomba

        if (recibido != null) {
            cadena += "Respuesta:" + recibido.toString();
            System.out.println("Respuesta:" + recibido.toString());
            respuestaDatos = manejarRespuesta(recibido, comando);//obtener los datos de respuesta?
            //TODO leer el mapa de las entradas salidas
        } else {
            System.out.println("Maquina no responde");
            cadena += "Maquina no responde";
        }
        return cadena;
    }//end of method

    public String encenderSolenoide2(boolean on) {
        String retorno;
        Mensaje comando, recibido = null, respuestaDatos;
        short[] datosOn = {1, 8, 8};
        short[] datosOff = {1, 8, 0};
        if (on == true) {
            comando = new Mensaje((short) 8, (short) 0, (short) 3, datosOn);
        } else {
            comando = new Mensaje((short) 8, (short) 0, (short) 3, datosOff);
            //comando.setDatos(datosOff);
        }

        recibido = enviarMensaje(comando);//enviar el mensaje para prender la bomba

        if (recibido != null) {
            retorno = "Respuesta:" + recibido.toString();
            System.out.println("Respuesta:" + recibido.toString());
            respuestaDatos = manejarRespuesta(recibido, comando);//obtener los datos de respuesta?
            //TODO leer el mapa de las entradas salidas
        } else {
            retorno = "Maquina no responde";
            System.out.println("Maquina no responde");
        }
        return retorno;
    }//end of method

    public String encenderDrainPump(boolean on) {
        String retorno;
        Mensaje comando, recibido = null, respuestaDatos;
        short[] datosOn = {1, 32, 32};
        short[] datosOff = {1, 32, 0};
        if (on == true) {
            comando = new Mensaje((short) 8, (short) 0, (short) 3, datosOn);
        } else {
            comando = new Mensaje((short) 8, (short) 0, (short) 3, datosOff);
            //comando.setDatos(datosOff);
        }

        recibido = enviarMensaje(comando);//enviar el mensaje para prender la bomba

        if (recibido != null) {
            retorno = "Respuesta:" + recibido.toString();
            System.out.println("Respuesta:" + recibido.toString());
            respuestaDatos = manejarRespuesta(recibido, comando);//obtener los datos de respuesta?
            //TODO leer el mapa de las entradas salidas
        } else {
            retorno = "Maquina no responde";
            System.out.println("Maquina no responde");
        }
        return retorno;
    }//end of method

    public String encenderBombaMuestras(boolean on) {
        String retorno;
        Mensaje comando, recibido = null, respuestaDatos;
        short[] datosOn = {1, 16, 16};
        short[] datosOff = {1, 16, 0};
        if (on == true) {
            comando = new Mensaje((short) 8, (short) 0, (short) 3, datosOn);
        } else {
            comando = new Mensaje((short) 8, (short) 0, (short) 3, datosOff);
            //comando.setDatos(datosOff);
        }

        recibido = enviarMensaje(comando);//enviar el mensaje para prender la bomba

        if (recibido != null) {
            retorno = "Respuesta:" + recibido.toString();
            System.out.println("Respuesta:" + recibido.toString());
            respuestaDatos = manejarRespuesta(recibido, comando);//obtener los datos de respuesta?
            //TODO leer el mapa de las entradas salidas
        } else {
            retorno = "Maquina no responde";
            System.out.println("Maquina no responde");
        }
        return retorno;
    }//end of method

    public String encenderCalSol1(boolean on) {
        String retorno;
        Mensaje comando, recibido = null, respuestaDatos;
        short[] datosOn = {1, 1, 1};
        short[] datosOff = {1, 1, 0};
        if (on == true) {
            comando = new Mensaje((short) 8, (short) 0, (short) 3, datosOn);
        } else {
            comando = new Mensaje((short) 8, (short) 0, (short) 3, datosOff);
            //comando.setDatos(datosOff);
        }

        recibido = enviarMensaje(comando);//enviar el mensaje para prender la bomba

        if (recibido != null) {
            retorno = "Respuesta:" + recibido.toString();
            System.out.println("Respuesta:" + recibido.toString());
            respuestaDatos = manejarRespuesta(recibido, comando);//obtener los datos de respuesta?
            //TODO leer el mapa de las entradas salidas
        } else {
            retorno = "Maquina no responde";
            System.out.println("Maquina no responde");
        }
        return retorno;
    }//end of method

    public String encenderCalSol2(boolean on) {
        String retorno;
        Mensaje comando, recibido = null, respuestaDatos;
        short[] datosOn = {1, 2, 2};
        short[] datosOff = {1, 2, 0};
        if (on == true) {
            comando = new Mensaje((short) 8, (short) 0, (short) 3, datosOn);
        } else {
            comando = new Mensaje((short) 8, (short) 0, (short) 3, datosOff);
            //comando.setDatos(datosOff);
        }

        recibido = enviarMensaje(comando);//enviar el mensaje para prender la bomba

        if (recibido != null) {
            retorno = "Respuesta:" + recibido.toString();
            System.out.println("Respuesta:" + recibido.toString());
            respuestaDatos = manejarRespuesta(recibido, comando);//obtener los datos de respuesta?
            //TODO leer el mapa de las entradas salidas
        } else {
            retorno = "Maquina no responde";
            System.out.println("Maquina no responde");
        }
        return retorno;
    }//end of method

    public String obtenerPEF() {
        String PEF = "Error";
        // try {
        Mensaje recibido = null, respuestaDatos;
        Mensaje comando;
        short[] datosComando = {0, 42, 1};
        comando = new Mensaje(5, 0, 3, datosComando);

        recibido = enviarMensaje(comando);

        if (recibido != null) {
            System.out.println("Respuesta: " + recibido.toString());
            respuestaDatos = manejarRespuesta(recibido, comando);
            //sin sino, hacer un manejador
            short[] datos = respuestaDatos.getDatos();
            int valorPEF = (datos[0] > 0 ? datos[0] : datos[0] + 655369);
            PEF = String.valueOf(valorPEF);
            //int valorPEF = datos[0];
            System.out.println("El valor de PEF es:" + valorPEF);
        } else {
            System.out.println("Maquina no responde");
        }
        /* EquipoController eqpCont = new EquipoController();
            PEF = eqpCont.findPEFBySerial(numeroSerial());           
        } catch (Exception ex) {
            Logger.getLogger(BancoSensors.class.getName()).log(Level.SEVERE, null, ex);
        } */
        return PEF;
    }

//TODO comando de escribir serial ¿si se puede?
//Para el timer
//TODO comando de polaridad alta o baja
//TODO calibración de rpm : ¿Cuando la van a hacer? ¿Con que sensor?
//Comandos para todas las salidas y demás solenoides
//TODO detectar maquina
//TODO desconectar, conectar
    /**
     * procedimiento para identificar el estado de la máquina !!!puede
     * pertenecer a otra clase
     */
    private EstadoMaquina identificarEstado(short[] datos) {
        EstadoMaquina estado = new EstadoMaquina();
        String cadena = "";
        List<IndiceAdvertencias> listaIndices = new ArrayList<IndiceAdvertencias>();
        if (datos == null) {
            System.out.println("Error en los datos para la identificacion");
        } else {
            //construir una matriz con los arreglos booleanos
            //para cada palabra en los datos del estatus
            boolean[] arregloStatus = UtilGasesModelo.shortToFlags(datos[0]);
            //tediosa parte de armar el Estado
            estado.setCalentamiento(arregloStatus[0]);
            estado.setZero(arregloStatus[1]);
            estado.setNewO2(arregloStatus[2]);
            estado.setCalZero(arregloStatus[3]);
            estado.setBadO2(arregloStatus[4]);
            estado.setBajoFlujo(arregloStatus[5]);
            estado.setNewNox(arregloStatus[6]);
            estado.setBadNOx(arregloStatus[7]);
            estado.setCondensacion(arregloStatus[8]);
            estado.setADrailed(arregloStatus[14]);
            estado.setFlagInterna(arregloStatus[15]);
            boolean[] arregloFlags;
            for (int i = 0; i < datos.length - 1; i++) {//el ultimo dato no tiene mensajes
                //imprimir cadena
                System.out.println(UtilGasesModelo.shortToString(datos[i]));
                //convertir a arreglo booleano
                arregloFlags = UtilGasesModelo.shortToFlags(datos[i]);
                //recorrer el arreglo
                for (int j = 0; j < arregloFlags.length; j++) {
                    //si el valor del arreglo es true, entonces insertar en la lista de mensajes
                    if (arregloFlags[j]) {
                        listaIndices.add(new IndiceAdvertencias(i, j));
                    }
                }//end inner for
                //
            }//end outer for
            estado.setContadorCalentamiento(datos[10]);
            //imprimir los mensajes del diagnostico
            //sacar los mensajes del mapa e imprimirlos o meterlos en una lista
            for (IndiceAdvertencias indice : listaIndices) {
                cadena += indice + "\n";
                System.out.println(indice);
                cadena += mapaAdvertencias.get(indice) + "\n";
                System.out.println(mapaAdvertencias.get(indice));

            }//end for

            //Que hago, retorno una lista con los flags, imprimo una cadena, retorno el arreglo?????
            //retorno una matriz, lo hago por palabras, un map de numero de palabras y el arreglo????
            //mapear cada advertencia con un mensaje e imprimir los mensajes, usar una matriz o
            //una lista, o un mapa con el indice del flag;
            cadena = cadena + datos[10];
            estado.setMensajes(cadena);
        }//end else

        return estado;
    }//end of method identificarEstado

    /**
     * Metodo para ordenar al banco hacer la calibración con los datos
     * almacenados en memoria interna del banco, debe retornar mensajes o status
     * modoCalibracion = 0, entonces calibracion de Cero modoCalibracion = 1,
     * calibracion de Cero para el oxigeno modoCalibracion = 2, calibracion de
     * dos puntos, punto numero 1 o concentracion baja modo Calibracion = 3,
     * calibracion de dos puntos numero 2, punto numero 2 o calibracion alta
     */
    public void ordenarCalibracion(int modoCalibracion) {
        Mensaje comando;
        Mensaje respuesta = null, respuestaModoUno;

        switch (modoCalibracion) {
            case 0:///calibración de cero
                short[] datosCero = {0, 0x07};
                comando = new Mensaje(2, 0, 2, datosCero);
                break;
            case 1://Calibracion de oxigeno
                short[] datosOxigeno = {1, 0x08};
                comando = new Mensaje(2, 0, 2, datosOxigeno);
                break;
            case 2://calibracion de dos puntos, numero 1
                short[] datosPrimPunto = {2, 0x07};
                comando = new Mensaje(2, 0, 2, datosPrimPunto);
                break;
            case 3://calibracion de dos puntos numero 2
                short[] datosSegPunto = {3, 0x07};
                comando = new Mensaje(2, 0, 2, datosSegPunto);
                break;
            default:
                System.out.println("Error de modo de calibracion");
                comando = null;
                return;

        }//end switch

        respuesta = enviarMensaje(comando);

        if (respuesta != null) {
            System.out.println("Respuesta: " + respuesta.toString());
            respuestaModoUno = manejarRespuesta(respuesta, comando);
            //sin sino, hacer un manejador
            short[] datos = respuestaModoUno.getDatos();
            //manejador de respuesta de calibración                                                                                                                       

        } else {
            System.out.println("Maquina no responde");
        }

    }//end of method ordenarCAlibracion

    /**
     * Comando para leer o escribir el mapa fisico de las entradas salidas segun
     * la polaridad ***--------no implementada, polaridad alta por defecto
     * palabra indica que entradas o salidas, comando 8, modo de comando 0,
     * numero de datos :3 , leer/escribir parametro leer false, modo = 128,
     * palabra para escribirlo
     */
    public short mapaFisicoES(short palabra, boolean leerEscribir) {//retornar una cadena o un arreglo de las entradas
        Mensaje comando;
        short[] datos = null;
        if (!leerEscribir) {//LEER
            short[] datosLeer = {0, 0, 0};//ojo
            comando = new Mensaje(8, 0, 3, datosLeer);
        } else {//ESCRIBIR
            short[] datosEscribir = {1, 0, palabra};
            comando = new Mensaje(8, 0, 3, datosEscribir);
        }//end else
        Mensaje respuesta = null;

        respuesta = enviarMensaje(comando);

        if (respuesta != null) {
            System.out.println("Respuesta: " + respuesta.toString());
            Mensaje respuestaModoUno = manejarRespuesta(respuesta, comando);
            //sin sino, hacer un manejador
            datos = respuestaModoUno.getDatos();
            //manejador de respuesta de calibración

        } else {
            System.out.println("Maquina no responde");
        }

        return (leerEscribir ? 0 : datos[0]);

    }//end of method mapaFisicoES

    /**
     * Metodo para escribir/leer el punto de calibración depende de dataset que
     * punto se lee o escribe
     *
     * @param leerEscribir leer false, escribir true
     * @param ptoCalibracion
     * @param dataset si es 2, es para la calibración de oxigeno de un solo
     * punto si es 11 es el punto numero uno de calibracion usando dos puntos si
     * es 21 es el punto numero dos de calibracion usando dos puntos
     */
    public void leerEscribirDatosCal(boolean leerEscribir, PuntoCalibracion ptoCalibracion, int dataset) {
        Mensaje comando;
        short[] datos;
        if (!leerEscribir)//leer significa 0 o falso
        {
            switch (dataset) {
                case 2://calibrar el oxigeno un solo punto
                    datos = new short[3];
                    datos[0] = 0;
                    datos[1] = 2;
                    datos[2] = (short) (ptoCalibracion.getValorOxigeno());//no hay problema para valores positivos
                    comando = new Mensaje(5, 0, 3, datos);
                    break;
                case 11://punto numero uno para calibracion usando dos puntos,gases HC,CO,CO2,HiHC
                    datos = new short[6];
                    datos[0] = 0;
                    datos[1] = 11;
                    datos[2] = (short) (ptoCalibracion.getValorHC() * 1);
                    datos[3] = (short) (ptoCalibracion.getValorCO() * 0.01);
                    datos[4] = (short) (ptoCalibracion.getValorCO2() * 0.1);
                    datos[5] = ptoCalibracion.getValorHiHC();
                    comando = new Mensaje(5, 0, 6, datos);
                    break;
                case 21:
                    datos = new short[6];
                    datos[0] = 0;
                    datos[1] = 21;
                    datos[2] = (short) (ptoCalibracion.getValorHC() * 1);
                    datos[3] = (short) (ptoCalibracion.getValorCO() * 0.01);
                    datos[4] = (short) (ptoCalibracion.getValorCO2() * 0.1);
                    datos[5] = ptoCalibracion.getValorHiHC();
                    comando = new Mensaje(5, 0, 6, datos);
                    break;
                default:
                    System.out.println("Mal argumento datset" + dataset);
                    return;
            }//end switch
            //enviar el mensaje y manejar la respuesta

        } else {//escribir
            //obtener el valor de HiHC guardarlo en el punto de Calibracion
            switch (dataset) {
                case 2://calibrar el oxigeno un solo punto
                    System.out.println("Escribiendo valor del oxigeno");
                    datos = new short[3];
                    datos[0] = 1;
                    datos[1] = 2;
                    datos[2] = (short) (ptoCalibracion.getValorOxigeno());//no hay problema para valores positivos
                    comando = new Mensaje(5, 0, 3, datos);
                    break;
                case 11://punto numero uno para calibracion usando dos puntos,gases HC,CO,CO2,HiHC
                    datos = new short[6];
                    datos[0] = 1;
                    datos[1] = 11;
                    datos[2] = (short) (ptoCalibracion.getValorHC());
                    datos[3] = (short) (ptoCalibracion.getValorCO());
                    datos[4] = (short) (ptoCalibracion.getValorCO2());
                    datos[5] = (short) 20000;
                    comando = new Mensaje(5, 0, 6, datos);

                    break;
                case 21:
                    System.out.println("Entra a escribir el segundo punto de calibracion");
                    datos = new short[6];
                    datos[0] = 1;
                    datos[1] = 21;
                    datos[2] = (short) (ptoCalibracion.getValorHC());
                    datos[3] = (short) (ptoCalibracion.getValorCO());
                    datos[4] = (short) (ptoCalibracion.getValorCO2());
                    datos[5] = (short) (ptoCalibracion.getValorHiHC());
                    comando = new Mensaje(5, 0, 6, datos);
                    //comando.setChecksum((short)583);
                    System.out.println("Escribiendo comando" + comando.toString());
                    break;
                default:
                    System.out.println("Mal argumento datset" + dataset);
                    return;

            }//end switch

        }//end else
        Mensaje respuesta = null;

        respuesta = enviarMensaje(comando);

        if (respuesta != null) {
            System.out.println("Respuesta: " + respuesta.toString());
            Mensaje respuestaModoUno = manejarRespuesta(respuesta, comando);
            //sin sino, hacer un manejador
            System.out.println("calibracion escribir leer enviado");
            short[] datosRecibidos = respuestaModoUno.getDatos();
            //manejador de respuesta de calibración

        } else {
            System.out.println("Maquina no responde");
        }
    }//end of method leerEscribirDatosCal

    /**
     * Obtener informacion del punto de calibracion
     *
     * @param puntoCal si es 1 entonces es primer punto de calibracion o muestra
     * baja si es 2 entonces es segundo punto de calibracion o muestra alta
     * @return
     */
    public PuntoCalibracion leerDatosCalibracionPto1(int puntoCal) {
        short[] datos;
        Mensaje comando;
        if (puntoCal == 1) {
            System.out.println("Leyendo valors para el primer punto de calibracion cuando la calibracion es de dos puntos");
            datos = new short[6];
            datos[0] = 0;
            datos[1] = 11;
            datos[2] = 0;
            datos[3] = 0;
            datos[4] = 0;
            datos[5] = 0;
            comando = new Mensaje(5, 0, 6, datos);
        } else if (puntoCal == 2) {
            System.out.println("Leyendo valors para el primer punto de calibracion cuando la calibracion es de dos puntos");
            datos = new short[6];
            datos[0] = 0;
            datos[1] = 21;
            datos[2] = 0;
            datos[3] = 0;
            datos[4] = 0;
            datos[5] = 0;
            comando = new Mensaje(5, 0, 6, datos);
        } else {
            System.out.println("Error en el argumento pto de calibracion: " + puntoCal);
            return null;
        }
        Mensaje recibido = null;

        recibido = enviarMensaje(comando);

        PuntoCalibracion ptoCalibracion = new PuntoCalibracion();
        if (recibido != null) {
            System.out.println("Respuesta: " + recibido.toString());
            Mensaje respuestaDatos = manejarRespuesta(recibido, comando);
            //sin sino, hacer un manejador
            try {
                short[] datosRecibidos = respuestaDatos.getDatos();
                ptoCalibracion.setValorHC(datosRecibidos[0]);
                ptoCalibracion.setValorCO(datosRecibidos[1]);
                ptoCalibracion.setValorCO2(datosRecibidos[2]);
                ptoCalibracion.setValorHiHC(datosRecibidos[3]);
                //int valorPEF = datos[0];

            } catch (NullPointerException ne) {
                System.out.println("Datos del punto de calibracion no recibidos");
                ne.printStackTrace();
            }
        } else {
            System.out.println("Maquina no responde, no se pueden leer puntos de calibracion");
        }
        return ptoCalibracion;
    }//end of  

    @Override
    /**
     * Calibracion de un punto para el banco sensors, segun requerimento se va a
     * implementar exclusivamente para el programa de servicio.
     */
    public void calibracion(TipoCalibracion tipoCalibracion, double valorCO, double valorCO2, int valorHC) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void stop() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setRpm(Integer rpm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    class DelayTerminado extends TimerTask {

        @Override
        public void run() {
            setTiempoTerminado(true);
            System.out.println("Timer terminado");
            timer.cancel();
        }

    }

    public InputStream getIn() {
        return in;
    }

    public void setIn(InputStream in) {
        this.in = in;
    }

    public OutputStream getOut() {
        return out;
    }

    public void setOut(OutputStream out) {
        this.out = out;
    }

    public synchronized void setTiempoTerminado(boolean tiempoTerminado) {
        this.tiempoTerminado = tiempoTerminado;
    }

    public boolean isTiempoTerminado() {
        return tiempoTerminado;
    }

    public SerialPort getPuertoSerial() {
        return puertoSerial;
    }

    @Override
    public void setPuertoSerial(SerialPort puertoSerial) {
        this.puertoSerial = puertoSerial;
        try {
            this.out = puertoSerial.getOutputStream();
            this.in = puertoSerial.getInputStream();
        } catch (IOException ioexc) {
            System.out.println("Los flujos no se pueden establecer");
            ioexc.printStackTrace();
        }
    }

    /**
     * Con esta implementacion se toma el valor de la media de las ultimas
     * cuatro mediciones
     *
     * @param valorRPM
     * @return
     */
    private double filtroRPM(double valorRPM) {
        System.out.println("filtrando:" + valorRPM);
        double ultimaMedicion = shiftRegister.obtenerUltimo();
        if (Math.abs(valorRPM - ultimaMedicion) < 600) {
            shiftRegister.ponerElemento(valorRPM);
        } else if ((valorRPM - ultimaMedicion) > 0) {//subiendo
            shiftRegister.ponerElemento(ultimaMedicion + 200);
        } else {
            shiftRegister.ponerElemento(ultimaMedicion - 100);
        }
        double media = shiftRegister.media();
        System.out.println("media: " + media);
        return media;
    }

    @Override
    public int getRpm() {
        if (medicion != null) {
            MedicionGases medicionAnterior = medicion;
            try {
                medicion = obtenerDatos();
            } catch (Exception ex) {
                Logger.getLogger(BancoSensors.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                if (medicionAnterior != null && medicion != null) {

                    if (Math.abs(medicionAnterior.getValorRPM() - medicion.getValorRPM()) > 1000) {
                        return (int) filtroRPM(medicion.getValorRPM());
                    }
                }
            } catch (Exception exc) {

                try {
                    medicion = obtenerDatos();
                } catch (Exception ex) {
                    Logger.getLogger(BancoSensors.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            try {
                medicion = obtenerDatos();
            } catch (Exception ex) {
                Logger.getLogger(BancoSensors.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        int rpms = (int) filtroRPM(medicion.getValorRPM());
        rpms = rpms - rpms % 10;
        return rpms;//para que salga con las revoluciones adecuadas
    }

    @Override
    public int getTemp() {
        if (medicion != null) {
            return medicion.getValorTAceite();
        } else {
            try {
                medicion = obtenerDatos();
            } catch (Exception ex) {
                Logger.getLogger(BancoSensors.class.getName()).log(Level.SEVERE, null, ex);
            }
            return medicion.getValorTAceite();
        }

    }

    public int getFactorRPM() {
        return factorRPM;
    }

    @Override
    public void setFactorRPM(int factorRPM) {
        this.factorRPM = factorRPM;
    }

}//end of class

class IndiceAdvertencias {

    int fila, columna;

    public IndiceAdvertencias() {
        this.fila = -1;
        this.columna = -1;
    }//end of class

    public IndiceAdvertencias(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IndiceAdvertencias other = (IndiceAdvertencias) obj;
        if (this.fila != other.fila) {
            return false;
        }
        if (this.columna != other.columna) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.fila;
        hash = 67 * hash + this.columna;
        return hash;
    }

    @Override
    public String toString() {
        return "IndiceAdvertencias{" + "fila=" + fila + "columna=" + columna + '}';
    }

}
