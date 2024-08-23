/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.util;

import org.apache.log4j.Logger;

/**
 *
 * @author User
 */
public class CDAUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Logger.getRootLogger().error("Excepcion causada por: " + t,e);
    }
    
}
