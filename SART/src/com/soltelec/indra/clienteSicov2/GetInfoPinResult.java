/**
 * GetInfoPinResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.soltelec.indra.clienteSicov2;

public class GetInfoPinResult  extends com.soltelec.indra.clienteSicov2.EntityBase  implements java.io.Serializable {
    private java.lang.String codigoRunt;

    private java.lang.String placa;

    private java.lang.String categoriaServicio;

    private java.util.Calendar fechaRevision;

    private java.util.Calendar fechaReinspeccion;

    private java.lang.Integer recaudadorId;

    private java.lang.String numeroPin;

    private java.lang.String tipoDocumento;

    private java.lang.String numeroIdentificacion;

    private java.math.BigDecimal valorPin;

    private int pruebaActiva;

    public GetInfoPinResult() {
    }

    public GetInfoPinResult(
           java.lang.String codigoRunt,
           java.lang.String placa,
           java.lang.String categoriaServicio,
           java.util.Calendar fechaRevision,
           java.util.Calendar fechaReinspeccion,
           java.lang.Integer recaudadorId,
           java.lang.String numeroPin,
           java.lang.String tipoDocumento,
           java.lang.String numeroIdentificacion,
           java.math.BigDecimal valorPin,
           int pruebaActiva) {
        this.codigoRunt = codigoRunt;
        this.placa = placa;
        this.categoriaServicio = categoriaServicio;
        this.fechaRevision = fechaRevision;
        this.fechaReinspeccion = fechaReinspeccion;
        this.recaudadorId = recaudadorId;
        this.numeroPin = numeroPin;
        this.tipoDocumento = tipoDocumento;
        this.numeroIdentificacion = numeroIdentificacion;
        this.valorPin = valorPin;
        this.pruebaActiva = pruebaActiva;
    }


    /**
     * Gets the codigoRunt value for this GetInfoPinResult.
     * 
     * @return codigoRunt
     */
    public java.lang.String getCodigoRunt() {
        return codigoRunt;
    }


    /**
     * Sets the codigoRunt value for this GetInfoPinResult.
     * 
     * @param codigoRunt
     */
    public void setCodigoRunt(java.lang.String codigoRunt) {
        this.codigoRunt = codigoRunt;
    }


    /**
     * Gets the placa value for this GetInfoPinResult.
     * 
     * @return placa
     */
    public java.lang.String getPlaca() {
        return placa;
    }


    /**
     * Sets the placa value for this GetInfoPinResult.
     * 
     * @param placa
     */
    public void setPlaca(java.lang.String placa) {
        this.placa = placa;
    }


    /**
     * Gets the categoriaServicio value for this GetInfoPinResult.
     * 
     * @return categoriaServicio
     */
    public java.lang.String getCategoriaServicio() {
        return categoriaServicio;
    }


    /**
     * Sets the categoriaServicio value for this GetInfoPinResult.
     * 
     * @param categoriaServicio
     */
    public void setCategoriaServicio(java.lang.String categoriaServicio) {
        this.categoriaServicio = categoriaServicio;
    }


    /**
     * Gets the fechaRevision value for this GetInfoPinResult.
     * 
     * @return fechaRevision
     */
    public java.util.Calendar getFechaRevision() {
        return fechaRevision;
    }


    /**
     * Sets the fechaRevision value for this GetInfoPinResult.
     * 
     * @param fechaRevision
     */
    public void setFechaRevision(java.util.Calendar fechaRevision) {
        this.fechaRevision = fechaRevision;
    }


    /**
     * Gets the fechaReinspeccion value for this GetInfoPinResult.
     * 
     * @return fechaReinspeccion
     */
    public java.util.Calendar getFechaReinspeccion() {
        return fechaReinspeccion;
    }


    /**
     * Sets the fechaReinspeccion value for this GetInfoPinResult.
     * 
     * @param fechaReinspeccion
     */
    public void setFechaReinspeccion(java.util.Calendar fechaReinspeccion) {
        this.fechaReinspeccion = fechaReinspeccion;
    }


    /**
     * Gets the recaudadorId value for this GetInfoPinResult.
     * 
     * @return recaudadorId
     */
    public java.lang.Integer getRecaudadorId() {
        return recaudadorId;
    }


    /**
     * Sets the recaudadorId value for this GetInfoPinResult.
     * 
     * @param recaudadorId
     */
    public void setRecaudadorId(java.lang.Integer recaudadorId) {
        this.recaudadorId = recaudadorId;
    }


    /**
     * Gets the numeroPin value for this GetInfoPinResult.
     * 
     * @return numeroPin
     */
    public java.lang.String getNumeroPin() {
        return numeroPin;
    }


    /**
     * Sets the numeroPin value for this GetInfoPinResult.
     * 
     * @param numeroPin
     */
    public void setNumeroPin(java.lang.String numeroPin) {
        this.numeroPin = numeroPin;
    }


    /**
     * Gets the tipoDocumento value for this GetInfoPinResult.
     * 
     * @return tipoDocumento
     */
    public java.lang.String getTipoDocumento() {
        return tipoDocumento;
    }


    /**
     * Sets the tipoDocumento value for this GetInfoPinResult.
     * 
     * @param tipoDocumento
     */
    public void setTipoDocumento(java.lang.String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }


    /**
     * Gets the numeroIdentificacion value for this GetInfoPinResult.
     * 
     * @return numeroIdentificacion
     */
    public java.lang.String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }


    /**
     * Sets the numeroIdentificacion value for this GetInfoPinResult.
     * 
     * @param numeroIdentificacion
     */
    public void setNumeroIdentificacion(java.lang.String numeroIdentificacion) {
        this.numeroIdentificacion = numeroIdentificacion;
    }


    /**
     * Gets the valorPin value for this GetInfoPinResult.
     * 
     * @return valorPin
     */
    public java.math.BigDecimal getValorPin() {
        return valorPin;
    }


    /**
     * Sets the valorPin value for this GetInfoPinResult.
     * 
     * @param valorPin
     */
    public void setValorPin(java.math.BigDecimal valorPin) {
        this.valorPin = valorPin;
    }


    /**
     * Gets the pruebaActiva value for this GetInfoPinResult.
     * 
     * @return pruebaActiva
     */
    public int getPruebaActiva() {
        return pruebaActiva;
    }


    /**
     * Sets the pruebaActiva value for this GetInfoPinResult.
     * 
     * @param pruebaActiva
     */
    public void setPruebaActiva(int pruebaActiva) {
        this.pruebaActiva = pruebaActiva;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetInfoPinResult)) return false;
        GetInfoPinResult other = (GetInfoPinResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.codigoRunt==null && other.getCodigoRunt()==null) || 
             (this.codigoRunt!=null &&
              this.codigoRunt.equals(other.getCodigoRunt()))) &&
            ((this.placa==null && other.getPlaca()==null) || 
             (this.placa!=null &&
              this.placa.equals(other.getPlaca()))) &&
            ((this.categoriaServicio==null && other.getCategoriaServicio()==null) || 
             (this.categoriaServicio!=null &&
              this.categoriaServicio.equals(other.getCategoriaServicio()))) &&
            ((this.fechaRevision==null && other.getFechaRevision()==null) || 
             (this.fechaRevision!=null &&
              this.fechaRevision.equals(other.getFechaRevision()))) &&
            ((this.fechaReinspeccion==null && other.getFechaReinspeccion()==null) || 
             (this.fechaReinspeccion!=null &&
              this.fechaReinspeccion.equals(other.getFechaReinspeccion()))) &&
            ((this.recaudadorId==null && other.getRecaudadorId()==null) || 
             (this.recaudadorId!=null &&
              this.recaudadorId.equals(other.getRecaudadorId()))) &&
            ((this.numeroPin==null && other.getNumeroPin()==null) || 
             (this.numeroPin!=null &&
              this.numeroPin.equals(other.getNumeroPin()))) &&
            ((this.tipoDocumento==null && other.getTipoDocumento()==null) || 
             (this.tipoDocumento!=null &&
              this.tipoDocumento.equals(other.getTipoDocumento()))) &&
            ((this.numeroIdentificacion==null && other.getNumeroIdentificacion()==null) || 
             (this.numeroIdentificacion!=null &&
              this.numeroIdentificacion.equals(other.getNumeroIdentificacion()))) &&
            ((this.valorPin==null && other.getValorPin()==null) || 
             (this.valorPin!=null &&
              this.valorPin.equals(other.getValorPin()))) &&
            this.pruebaActiva == other.getPruebaActiva();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getCodigoRunt() != null) {
            _hashCode += getCodigoRunt().hashCode();
        }
        if (getPlaca() != null) {
            _hashCode += getPlaca().hashCode();
        }
        if (getCategoriaServicio() != null) {
            _hashCode += getCategoriaServicio().hashCode();
        }
        if (getFechaRevision() != null) {
            _hashCode += getFechaRevision().hashCode();
        }
        if (getFechaReinspeccion() != null) {
            _hashCode += getFechaReinspeccion().hashCode();
        }
        if (getRecaudadorId() != null) {
            _hashCode += getRecaudadorId().hashCode();
        }
        if (getNumeroPin() != null) {
            _hashCode += getNumeroPin().hashCode();
        }
        if (getTipoDocumento() != null) {
            _hashCode += getTipoDocumento().hashCode();
        }
        if (getNumeroIdentificacion() != null) {
            _hashCode += getNumeroIdentificacion().hashCode();
        }
        if (getValorPin() != null) {
            _hashCode += getValorPin().hashCode();
        }
        _hashCode += getPruebaActiva();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetInfoPinResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://190.25.205.154:809?/sicov.asmx", "getInfoPinResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codigoRunt");
        elemField.setXmlName(new javax.xml.namespace.QName("http://190.25.205.154:809?/sicov.asmx", "codigoRunt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("placa");
        elemField.setXmlName(new javax.xml.namespace.QName("http://190.25.205.154:809?/sicov.asmx", "placa"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("categoriaServicio");
        elemField.setXmlName(new javax.xml.namespace.QName("http://190.25.205.154:809?/sicov.asmx", "categoriaServicio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fechaRevision");
        elemField.setXmlName(new javax.xml.namespace.QName("http://190.25.205.154:809?/sicov.asmx", "fechaRevision"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fechaReinspeccion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://190.25.205.154:809?/sicov.asmx", "fechaReinspeccion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recaudadorId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://190.25.205.154:809?/sicov.asmx", "recaudadorId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numeroPin");
        elemField.setXmlName(new javax.xml.namespace.QName("http://190.25.205.154:809?/sicov.asmx", "numeroPin"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tipoDocumento");
        elemField.setXmlName(new javax.xml.namespace.QName("http://190.25.205.154:809?/sicov.asmx", "tipoDocumento"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numeroIdentificacion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://190.25.205.154:809?/sicov.asmx", "numeroIdentificacion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("valorPin");
        elemField.setXmlName(new javax.xml.namespace.QName("http://190.25.205.154:809?/sicov.asmx", "valorPin"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pruebaActiva");
        elemField.setXmlName(new javax.xml.namespace.QName("http://190.25.205.154:809?/sicov.asmx", "pruebaActiva"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
        return "GetInfoPinResult{" + "codigoRunt=" + codigoRunt + ", placa=" + placa + ", categoriaServicio=" + categoriaServicio + ", fechaRevision=" + fechaRevision + ", fechaReinspeccion=" + fechaReinspeccion + ", recaudadorId=" + recaudadorId + ", numeroPin=" + numeroPin + ", tipoDocumento=" + tipoDocumento + ", numeroIdentificacion=" + numeroIdentificacion + ", valorPin=" + valorPin + ", pruebaActiva=" + pruebaActiva + ", __equalsCalc=" + __equalsCalc + ", __hashCodeCalc=" + __hashCodeCalc + '}';
    }

    
    
}
