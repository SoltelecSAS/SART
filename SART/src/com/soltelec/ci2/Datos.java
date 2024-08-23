/**
 * Datos.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */
package com.soltelec.ci2;

public interface Datos extends javax.xml.rpc.Service {

    /**
     *
     * @return
     */
    public java.lang.String getdatosSoapAddress();

    public com.soltelec.ci2.DatosSoap getdatosSoap() throws javax.xml.rpc.ServiceException;

    public com.soltelec.ci2.DatosSoap getdatosSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;

    public java.lang.String getdatosSoap12Address();

    public com.soltelec.ci2.DatosSoap getdatosSoap12() throws javax.xml.rpc.ServiceException;

    public com.soltelec.ci2.DatosSoap getdatosSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
