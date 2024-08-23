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
public class CondicionesAnormalesException extends ExecutionException {

    @Override
    public String getMessage() {
        return "Condiciones Anormales encontradas durante la prueba";
    }

}
