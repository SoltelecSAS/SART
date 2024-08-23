/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.util;

 public enum TipoDefecto {
        DEFECTO_A('A'),DEFECTO_B('A');

        private char tipo;

        private TipoDefecto(char tipo) {
            this.tipo = tipo;
        }

        public char getTipo() {
            return tipo;
        }
    }