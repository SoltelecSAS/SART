/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosopacimetro;

import gnu.io.SerialPort;
import org.soltelec.util.MedicionOpacidad;

/**
 * Interface para proporcionar un comportamiento polimorfico
 * para el opacimetro, la idea es reutilizar el codigo o en un
 * futuro usar la inyección de dependencias para seleccionar
 * la marca del opacimetro
 * @author Usuario
 */
public interface Opacimetro {

    int VALOR_ESCALA_MAXIMA = 99;//Valor para considerar que el opacimetro alcanza la escala maxima
//    int TIEMPO_ESCALA = 60;//tiempo de espera para que el opacimetro alcanze la escala maxima
    int TIEMPO_ESCALA = 5;//tiempo de espera para que el opacimetro alcanze la escala maxima
    int VALOR_VERIFICACION = 10;
    int LIMITE_FILTROS = 10; // En la verificacion de linealidad los filtros deben coincidir +- LIMITEFILTROS
    double LIMITE_RESIDUOS = 0.15;// Limite de la opacidad de 2% despues de realizar los ciclos.
    int LIMITE_DIFERENCIA = 10; //Diferencia entre el ciclo mayor y el menor
    int LIMITE_RANGO_CICLOS = 200;
    /**;
     * Este metodo se supone que alista
     * la operación de tomar medidas por 10 segundos
     * ascii '97'
     */
    void armarLCS();

    /**
     * Metodo para linealizar no se debe pedir datos durante este
     * periodo, bit se activa
     */
    void calibrar();

    /**
     * Permite subida de datos en tiempo real durante el evento de disparo del
     * trigger. Depues de mandar el comando de trigger(‘t’) 0x8a puede ser secuenciado
     * para subir un conjunto de datos de puntos de opacidad del buffer de disparo. El comando asegurará rápida y completa transmisión de datos sin tener que esperar el final de un ciclo de disparo
     */
    byte[] datosRTTrigger(int indiceInicial, int indiceFinal);

    /**
     * Instruye al LCS a desarmar el disparador y dejar d escribir datos de opacidad en la memoria del buffer. El LCS aborta el estado armado y resetea el bit trigger del status.
     * Nota: Desarmer el tigger o del detonador o el disparador terminara el modo de
     * trigger antes que  diez segundos de  datos sean adquiridos y guardados en
     * el buffer de memoria. ( 1 segundo pre-trigger y 9 segundos buffer trigger).
     * ascii de 'q' es 113
     * Esto permite al operador del software acortar el tiempo de recolección de datos.
     */
    void desarmarTrigger();

    /**
     * Instruye al LCS a dispararse o detonarse, resetea el bit de estatus
     * arm-trigger  y activa el bit de estado trigger. Si el LCS estuvo armado
     * por más de un segundo, el buffer de datos retiene el último segundo de
     * información. Si el LCS estuvo armado por un tiempo menor a un segundo,
     * el buffer de datos retenerá esta información y empezará a almacenar datos
     * desde el disparo en el buffer de datos. El buffer de datos puede almacenar
     * hasta 10 segundos de datos armados (pre – disparado) y datos disparados.
     * codigo ascii de 't' 116
     */
    void dispararTrigger();

    SerialPort getPort();

    /**
     * Reporta el número de puntos adquiridos de datos durante el disparo.
     * Puede ser ejecutado solamente después de enviar el comando de quitar – gatillo (‘q’).
     * Donde puntos de datos disparados es el número de datos mestreados de puntos disparados en el buffe de datos del triger
     * 50<= triggered data points <= 500 *
     *
     * @return el numero de datos adquiridos
     */
    int numeroDatosAdquiridos();

    /**
     * Instruye a la LCS a subir 10 segundos del buffer de datos de opacidad desde un evento ARM&TRIGGER.
     * La velocidad de muestreo de 50 muestras/segundo resulta en 500 palabras de datos
     * (1000 bytes) ascci de 'o' = 111 chksum -111
     * @return retorna el arreglo de datos
     */
    byte[] obtenerArregloDatosOpacidad();

    /**
     * Pide datos desde el LCS, los datos son actualizados cada 20 ms
     * @return
     */
    MedicionOpacidad obtenerDatos();

    /**
     * Este comando solamente se puede usar para realizar pruebas de linealidad
     * con atenuadores
     *
     * @return un dato en bruto ( es decir sin filtrar, no tiene corrección
     * de temperatura de gas, no esta EOPL corregida)
     */
    int obtenerDatosBruto();

    /**
     *
     * Este comando solamente se puede usar para realizar pruebas de linealidad
     * con atenuadores
     * @return una medicion que consiste en dato y estatus
     */
    MedicionOpacidad obtenerDatosEstatusBruto();

    /**
     * Comando para obtener la frecuencia de Muestreo del
     * Opacimetro
     * @return entero frecuencia de muestreo
     */
    int obtenerFrecuenciaMuestreo();

    /**
     * A)	Unidades LCS con version de software hasta 1.42
     * Peticion  de numero de revisión de software
     * B)	La siguiente estructura de comando esta disponible en versiones de software 1.43 y superiores:
     * Peticion de numero de revisión de software y su checksum
     * @return 
     */
    int[] obtenerInfoSoftware();

    void setPort(SerialPort port);
    
    long obtenerSerial();

}
