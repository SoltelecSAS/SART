/**
 * SicovLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.soltelec.indra.clienteSicov2;

import java.util.logging.Logger;

public class SicovLocator extends org.apache.axis.client.Service implements Sicov {

//    private static String urlServicio1;
//    private static String urlSicov2;
    private static final Logger LOG = Logger.getLogger(SicovLocator.class.getName());

//    static {
//        try {
//            urlServicio1 = UtilPropiedades.cargarPropiedad("urlServicio1", "propiedades.properties");
//            urlSicov2 = UtilPropiedades.cargarPropiedad("urlSicov2", "propiedades.properties");
//        } catch (Exception e) {
//            LOG.log(Level.SEVERE, "Error al cargar la propiedad urlSicov2 {0}", e);
//        }
//    }
    public SicovLocator() {

    }

    public SicovLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SicovLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SicovSoap12
//    private java.lang.String SicovSoap12_address = "http://190.25.205.154:8193/Sicov.asmx";
    private java.lang.String SicovSoap12_address;

    public SicovLocator(String urlSicov, String urlSicov2) {
        this.SicovSoap_address = urlSicov;
        this.SicovSoap12_address = urlSicov2;
    }

    public java.lang.String getSicovSoap12Address() {
        return SicovSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SicovSoap12WSDDServiceName = "SicovSoap12";

    public java.lang.String getSicovSoap12WSDDServiceName() {
        return SicovSoap12WSDDServiceName;
    }

    public void setSicovSoap12WSDDServiceName(java.lang.String name) {
        SicovSoap12WSDDServiceName = name;
    }

    public SicovSoap getSicovSoap12() throws javax.xml.rpc.ServiceException {
        java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SicovSoap12_address);
        } catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSicovSoap12(endpoint);
    }

    public SicovSoap getSicovSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            SicovSoap12Stub _stub = new SicovSoap12Stub(portAddress, this);
            _stub.setPortName(getSicovSoap12WSDDServiceName());
            return _stub;
        } catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSicovSoap12EndpointAddress(java.lang.String address) {
        SicovSoap12_address = address;
    }

    // Use to get a proxy class for SicovSoap
//    private java.lang.String SicovSoap_address = "http://190.25.205.154:8193/Sicov.asmx";
    private java.lang.String SicovSoap_address ;

    public java.lang.String getSicovSoapAddress() {
        return SicovSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SicovSoapWSDDServiceName = "SicovSoap";

    public java.lang.String getSicovSoapWSDDServiceName() {
        return SicovSoapWSDDServiceName;
    }

    public void setSicovSoapWSDDServiceName(java.lang.String name) {
        SicovSoapWSDDServiceName = name;
    }

    public SicovSoap getSicovSoap() throws javax.xml.rpc.ServiceException {
        java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SicovSoap_address);
        } catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSicovSoap(endpoint);
    }

    public String getEndpoint() throws javax.xml.rpc.ServiceException {
        java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SicovSoap_address);
        } catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return endpoint.toString();
    }


    public SicovSoap getSicovSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            SicovSoapStub _stub = new SicovSoapStub(portAddress, this);
            _stub.setPortName(getSicovSoapWSDDServiceName());
            return _stub;
        } catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSicovSoapEndpointAddress(java.lang.String address) {
        SicovSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation. If this service has
     * no port for the given interface, then ServiceException is thrown. This
     * service has multiple ports for a given interface; the proxy
     * implementation returned may be indeterminate.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (SicovSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                SicovSoap12Stub _stub = new SicovSoap12Stub(new java.net.URL(SicovSoap12_address), this);
                _stub.setPortName(getSicovSoap12WSDDServiceName());
                return _stub;
            }
            if (SicovSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                SicovSoapStub _stub = new SicovSoapStub(new java.net.URL(SicovSoap_address), this);
                _stub.setPortName(getSicovSoapWSDDServiceName());
                return _stub;
            }
        } catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation. If this service has
     * no port for the given interface, then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("SicovSoap12".equals(inputPortName)) {
            return getSicovSoap12();
        } else if ("SicovSoap".equals(inputPortName)) {
            return getSicovSoap();
        } else {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName(SicovSoap12_address, "Sicov");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName(SicovSoap12_address, "SicovSoap12"));
            ports.add(new javax.xml.namespace.QName(SicovSoap12_address, "SicovSoap"));
        }
        return ports.iterator();
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {

        if ("SicovSoap12".equals(portName)) {
            setSicovSoap12EndpointAddress(address);
        } else if ("SicovSoap".equals(portName)) {
            setSicovSoapEndpointAddress(address);
        } else { // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }
    
}