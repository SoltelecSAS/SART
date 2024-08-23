/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.util.List;

/**
 *
 * @author Dany
 */
public class Medidas {
    
    private int idMedidas;
    private List<Integer> tiposMedida;
    private List<Double> valoresMedida;
    private int idPrueba;
    private String[] condicion;
    
    //constantes que guardaran la informacion de los nombres de las tablas
    //cada constante tendra el nombre de la variable a la cual va ligada
    //la tabla.
    public static final String TABLA = "medidas";
    public static final String ID_MEDIDAS = "MEASURE";
    public static final String ID_TIPO_MEDIDA = "MEASURETYPE";
    public static final String VALOR_MEDIDA = "valor_medida";
    public static final String ID_PRUEBA = "TEST";
    public static final String CONDICION = "Condicion";

    public int getIdMedidas() {
        return idMedidas;
    }

    public void setIdMedidas(int idMedidas) {
        this.idMedidas = idMedidas;
    }

    public List<Integer> getTiposMedida() {
        return tiposMedida;
    }

    public void setTiposMedida(List<Integer> tiposMedida) {
        this.tiposMedida = tiposMedida;
    }

    public List<Double> getValoresMedida() {
        return valoresMedida;
    }

    public void setValoresMedida(List<Double> valoresMedida) {
        this.valoresMedida = valoresMedida;
    }

    public int getIdPrueba() {
        return idPrueba;
    }

    public void setIdPrueba(int idPrueba) {
        this.idPrueba = idPrueba;
    }
}
