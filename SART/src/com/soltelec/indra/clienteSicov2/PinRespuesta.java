/**
 * PinRespuesta.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.soltelec.indra.clienteSicov2;

public class PinRespuesta  implements java.io.Serializable {
    private int codRespuesta;

    private java.lang.String msjRespuesta;

    private com.soltelec.indra.clienteSicov2.GetInfoPinResult pinRespuesta;

    public PinRespuesta() {
    }

    public PinRespuesta(
           int codRespuesta,
           java.lang.String msjRespuesta,
           com.soltelec.indra.clienteSicov2.GetInfoPinResult pinRespuesta) {
           this.codRespuesta = codRespuesta;
           this.msjRespuesta = msjRespuesta;
           this.pinRespuesta = pinRespuesta;
    }


    /**
     * Gets the codRespuesta value for this PinRespuesta.
     * 
     * @return codRespuesta
     */
    public int getCodRespuesta() {
        return codRespuesta;
    }


    /**
     * Sets the codRespuesta value for this PinRespuesta.
     * 
     * @param codRespuesta
     */
    public void setCodRespuesta(int codRespuesta) {
        this.codRespuesta = codRespuesta;
    }


    /**
     * Gets the msjRespuesta value for this PinRespuesta.
     * 
     * @return msjRespuesta
     */
    public java.lang.String getMsjRespuesta() {
        return msjRespuesta;
    }


    /**
     * Sets the msjRespuesta value for this PinRespuesta.
     * 
     * @param msjRespuesta
     */
    public void setMsjRespuesta(java.lang.String msjRespuesta) {
        this.msjRespuesta = msjRespuesta;
    }


    /**
     * Gets the pinRespuesta value for this PinRespuesta.
     * 
     * @return pinRespuesta
     */
    public com.soltelec.indra.clienteSicov2.GetInfoPinResult getPinRespuesta() {
        return pinRespuesta;
    }


    /**
     * Sets the pinRespuesta value for this PinRespuesta.
     * 
     * @param pinRespuesta
     */
    public void setPinRespuesta(com.soltelec.indra.clienteSicov2.GetInfoPinResult pinRespuesta) {
        this.pinRespuesta = pinRespuesta;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PinRespuesta)) return false;
        PinRespuesta other = (PinRespuesta) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.codRespuesta == other.getCodRespuesta() &&
            ((this.msjRespuesta==null && other.getMsjRespuesta()==null) || 
             (this.msjRespuesta!=null &&
              this.msjRespuesta.equals(other.getMsjRespuesta()))) &&
            ((this.pinRespuesta==null && other.getPinRespuesta()==null) || 
             (this.pinRespuesta!=null &&
              this.pinRespuesta.equals(other.getPinRespuesta())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += getCodRespuesta();
        if (getMsjRespuesta() != null) {
            _hashCode += getMsjRespuesta().hashCode();
        }
        if (getPinRespuesta() != null) {
            _hashCode += getPinRespuesta().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PinRespuesta.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://190.25.205.154:809?/sicov.asmx", "PinRespuesta"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codRespuesta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://190.25.205.154:809?/sicov.asmx", "codRespuesta"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("msjRespuesta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://190.25.205.154:809?/sicov.asmx", "msjRespuesta"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pinRespuesta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://190.25.205.154:809?/sicov.asmx", "pinRespuesta"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://190.25.205.154:809?/sicov.asmx", "getInfoPinResult"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

    @Override
    public String toString() {
        return "PinRespuesta{" + "codRespuesta=" + codRespuesta + ", msjRespuesta=" + msjRespuesta + ", pinRespuesta=" + pinRespuesta + ", __equalsCalc=" + __equalsCalc + ", __hashCodeCalc=" + __hashCodeCalc + '}';
    }

    
    
}
