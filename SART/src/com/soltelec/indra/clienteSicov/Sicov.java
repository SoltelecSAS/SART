/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.indra.clienteSicov;
/**
 * Sicov.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */


public interface Sicov extends javax.xml.rpc.Service {

    public java.lang.String getSicovSoap12Address();

    public com.soltelec.indra.clienteSicov.SicovSoap getSicovSoap12() throws javax.xml.rpc.ServiceException;

    public com.soltelec.indra.clienteSicov.SicovSoap getSicovSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;

    public java.lang.String getSicovSoapAddress();

    public com.soltelec.indra.clienteSicov.SicovSoap getSicovSoap() throws javax.xml.rpc.ServiceException;

    public com.soltelec.indra.clienteSicov.SicovSoap getSicovSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
