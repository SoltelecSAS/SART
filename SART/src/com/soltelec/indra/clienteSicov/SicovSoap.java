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

public interface SicovSoap extends java.rmi.Remote 
{
   public com.soltelec.indra.clienteSicov.FURRespuesta setFUR(java.lang.String cadena) throws java.rmi.RemoteException;
   public com.soltelec.indra.clienteSicov.FURRespuesta setEventsRTMEC(java.lang.String cadena) throws java.rmi.RemoteException;
   public java.lang.String setConexion(java.lang.String cadena) throws java.rmi.RemoteException;
}
