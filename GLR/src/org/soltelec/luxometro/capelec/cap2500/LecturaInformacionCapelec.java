/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.luxometro.capelec.cap2500;

import java.util.List;

/**
 *
 * @author User
 */
public class LecturaInformacionCapelec {

    public MedidasVehiculos buscarDatosAutos(List<String> datos) {
        MedidasVehiculos medidasVehiculos = new MedidasVehiculos();
        medidasVehiculos.setInclinacionLuzBajaDerecha(BusquedaNumericos.buscarDecimal("Optico Derecho", datos));
        medidasVehiculos.setIntencidadLuzBajaDerecha(BusquedaNumericos.buscarEntero("Optico Derecho", datos, Coincidencia.DOS_COINCIDENCIAS));
        medidasVehiculos.setExploradoraDerecha(BusquedaNumericos.buscarEntero("Optico Derecho", datos, Coincidencia.TRES_COINCIDENCIAS));
        
        medidasVehiculos.setInclinacionLuzBajaIzquierda(BusquedaNumericos.buscarDecimal("Optico Izq.", datos));
        medidasVehiculos.setIntencidadLuzBajaIzquierda(BusquedaNumericos.buscarEntero("Optico Izq.", datos, Coincidencia.DOS_COINCIDENCIAS));
        medidasVehiculos.setExploradoraIzquierda(BusquedaNumericos.buscarEntero("Optico Izq.", datos, Coincidencia.TRES_COINCIDENCIAS));
        medidasVehiculos.setIntencidadLuzAltaIzquierda(BusquedaNumericos.buscarEntero("Luz A. Izq.", datos));
        medidasVehiculos.setIntencidadLuzAltaDerecha(BusquedaNumericos.buscarEntero("Luz A. Der.", datos, Coincidencia.TRES_COINCIDENCIAS));
        System.out.println(medidasVehiculos);
        return medidasVehiculos;
    }

    public MedidasVehiculos buscarDatosMotos(List<String> datos) {        
        MedidasVehiculos medidasVehiculos = new MedidasVehiculos();
        medidasVehiculos.setInclinacionLuzBajaDerecha(BusquedaNumericos.buscarDecimal("Optico Derecho", datos));
        medidasVehiculos.setIntencidadLuzBajaDerecha(BusquedaNumericos.buscarEntero("Optico Derecho", datos, Coincidencia.DOS_COINCIDENCIAS));
        medidasVehiculos.setIntencidadLuzAltaDerecha(BusquedaNumericos.buscarEntero("Luz A. Der.", datos, Coincidencia.TRES_COINCIDENCIAS));
        System.out.println(medidasVehiculos);
        return medidasVehiculos;

    }

}
