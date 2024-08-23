/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases;

import java.util.concurrent.ExecutionException;

/**
 *
 * @author GerenciaDesarrollo
 */
public class HumoAzulException extends ExecutionException {

    @Override
    public String getMessage() {
        return "Se ha encontrado humo Azul durante la prueba";
    }

}
