/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.indra;

import java.io.Serializable;
/**
 *
 * @author user
 */
public class RespuestaDTO implements Serializable {

    private String codigoRespuesta;
    private String mensajeRespuesta;

    public RespuestaDTO(String codigoRespuesta, String mensajeRespuesta) {
        this.codigoRespuesta = codigoRespuesta;
        this.mensajeRespuesta = mensajeRespuesta;
    }

    public RespuestaDTO() {
    }

    public String getCodigoRespuesta() {
        return this.codigoRespuesta;
    }

    public void setCodigoRespuesta(String codigoRespuesta) {
        this.codigoRespuesta = codigoRespuesta;
    }

    public String getMensajeRespuesta() {
        return this.mensajeRespuesta;
    }

    public void setMensajeRespuesta(String mensajeRespuesta) {
        this.mensajeRespuesta = mensajeRespuesta;
    }

    public String toString() {
        return "RespuestaDTO{codigoRespuesta=" + this.codigoRespuesta + ", mensajeRespuesta=" + this.mensajeRespuesta + '}';
    }
}
