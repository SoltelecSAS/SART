/**
 * DatosSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */
package com.soltelec.ci2;

public interface DatosSoap extends java.rmi.Remote {

   

    public com.soltelec.ci2.Resultado utilizarPin(com.soltelec.ci2.Pin PIN) throws java.rmi.RemoteException;

    public com.soltelec.ci2.Resultado consultaPin(com.soltelec.ci2.Pin PIN) throws java.rmi.RemoteException;

    public com.soltelec.ci2.Resultado consultaPinPlaca(com.soltelec.ci2.Pin PIN) throws java.rmi.RemoteException;

    public java.lang.String echo(java.lang.String dato) throws java.rmi.RemoteException;

    public java.lang.String generico(java.lang.String array_parametros) throws java.rmi.RemoteException;

    
}
