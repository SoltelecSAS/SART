package org.soltelec.util;

public class VariablesOpacidad {
    private static double temperaturaMotor;

    private VariablesOpacidad() {
        throw new IllegalStateException("Utility class");
    }

    public static double getTemperaturaMotor() {
        return temperaturaMotor;
    }

    public static void setTemperaturaMotor(double temperaturaMotor) {
        VariablesOpacidad.temperaturaMotor = temperaturaMotor;
    }

}
