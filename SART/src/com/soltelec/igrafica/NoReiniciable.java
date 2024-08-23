/*Clase utilizada para guardar el valor de oprimido de los botones de la interfaz gráfica Frm_IVisual
 *una vez enviados los datos al servidor de alguna prueba perteneciente a inspección visual, debe bloquear
 * este boton para que no pueda volver a acceder a esta prueba nuevamente.
 * Se usa un booleano que determinara si el los datos ya fueron enviados o no, después de verificarlo deberá
 * reiniciar la variable para que los otros botones no tengan dificiltad en hacer la comprobación.
 */

package com.soltelec.igrafica;

//import javax.swing.*;

/**
 * @author Ing. Asdrubal Martinez
 */

public class NoReiniciable {

    boolean oprimido = false;
    String placas_bd = ""; //Guardar las placas del vehiculo que esta "ON"

    public NoReiniciable(){
    }  

    public void establecer_oprimido(boolean oprimido){
        this.oprimido = oprimido = true;
        //JOptionPane.showMessageDialog(null, "El valor se setablecio en: " +oprimido);
    }
    public boolean obtener_oprimido(){
        //JOptionPane.showMessageDialog(null, "Alguien entro aca y la variable oprimido esta en: " +oprimido);
        return oprimido;
    }

    public void reiniciarVariable(){
        oprimido = false;
    }

    public void establecerPlacasBd(String placas_bd){
        this.placas_bd = placas_bd;
    }

    public String obtenerPlacasBd(){
        return placas_bd;
    }
}