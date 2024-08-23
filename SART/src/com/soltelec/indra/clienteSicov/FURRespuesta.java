/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.indra.clienteSicov;

/**
 *
 * @author user
 */

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.BeanDeserializer;
import org.apache.axis.encoding.ser.BeanSerializer;

public class FURRespuesta  implements Serializable
{
  private String furData;
  private static final Logger LOG = Logger.getLogger(FURRespuesta.class.getName());
  private int codRespuesta;
  private static String urlServicio2;
  private String msjRespuesta;
  private Object __equalsCalc = null;

  private boolean __hashCodeCalc = false;
  private static TypeDesc typeDesc;

  public FURRespuesta()
  {
  }

  public FURRespuesta(String furData, int codRespuesta, String msjRespuesta)
  {
    this.furData = furData;
    this.codRespuesta = codRespuesta;
    this.msjRespuesta = msjRespuesta;
  }

  public String getFurData()
  {
    return this.furData;
  }

  public void setFurData(String furData)
  {
    this.furData = furData;
  }

  public int getCodRespuesta()
  {
    return this.codRespuesta;
  }

  public void setCodRespuesta(int codRespuesta)
  {
    this.codRespuesta = codRespuesta;
  }

  public String getMsjRespuesta()
  {
    return this.msjRespuesta;
  }

  public void setMsjRespuesta(String msjRespuesta)
  {
    this.msjRespuesta = msjRespuesta;
  }

 public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FURRespuesta)) {
            return false;
        }
        FURRespuesta other = (FURRespuesta) obj;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true
                && ((this.furData == null && other.getFurData() == null)
                || (this.furData != null
                && this.furData.equals(other.getFurData())))
                && this.codRespuesta == other.getCodRespuesta()
                && ((this.msjRespuesta == null && other.getMsjRespuesta() == null)
                || (this.msjRespuesta != null
                && this.msjRespuesta.equals(other.getMsjRespuesta())));
        __equalsCalc = null;
        return _equals;
    }
  

  public synchronized int hashCode()
  {
    if (this.__hashCodeCalc) {
      return 0;
    }
    this.__hashCodeCalc = true;
    int _hashCode = 1;
    if (getFurData() != null) {
      _hashCode += getFurData().hashCode();
    }
    _hashCode += getCodRespuesta();
    if (getMsjRespuesta() != null) {
      _hashCode += getMsjRespuesta().hashCode();
    }
    this.__hashCodeCalc = false;
    return _hashCode;
  }

  public static TypeDesc getTypeDesc()
  {
    return typeDesc;
  }

  public static Serializer getSerializer(String mechType, Class _javaType, QName _xmlType)
  {
    return new BeanSerializer(_javaType, _xmlType, typeDesc);
  }

  public static Deserializer getDeserializer(String mechType, Class _javaType, QName _xmlType)
  {
    return new BeanDeserializer(_javaType, _xmlType, typeDesc);
  }

  static
  {
    try
    {
      urlServicio2 = "http://192.168.0.113:8056/sicov.asmx";//UtilPropiedades.cargarPropiedad("urlServicio2", "propiedades.properties");
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Error al cargar la propiedad urlServicio2 {0}", e);
    }

    typeDesc = new TypeDesc(FURRespuesta.class, true);

    typeDesc.setXmlType(new QName(urlServicio2, "FURRespuesta"));
    ElementDesc elemField = new ElementDesc();
    elemField.setFieldName("furData");
    elemField.setXmlName(new QName(urlServicio2, "furData"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("codRespuesta");
    elemField.setXmlName(new QName(urlServicio2, "codRespuesta"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("msjRespuesta");
    elemField.setXmlName(new QName(urlServicio2, "msjRespuesta"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setMinOccurs(0);
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
  }
}

