package com.soltelec.Inicio;

//TODO: En cada botón enviar de la interfáz gráfica ponerle funcionalidad de envío a la BD.

/**
 * @author Ing. Asdrúbal Martínez
 */



import com.soltelec.igrafica.FrmLogin;
import org.apache.log4j.xml.DOMConfigurator;

public class Main {

    public static void main(String args[]) throws Exception{
        //Thread.setDefaultUncaughtExceptionHandler(new CDAUncaughtExceptionHandler());

//         for(UIManager.LookAndFeelInfo laf:UIManager.getInstalledLookAndFeels()){
//                if("Nimbus".equals(laf.getName()))
//                try {
//                    UIManager.setLookAndFeel(laf.getClassName());
//                } catch (Exception ex) {
//                    }
//                }
         
//        Inicialización de puertos
//        PuertoRS232 prs = new PuertoRS232();
//        prs.AbrirConexion();

        //Abrir la ventana Password, donde inicia el arbol de interfaces gráficas.
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                DOMConfigurator.configure("./log4j.xml");
                //BasicConfigurator.configure();
                new FrmLogin().setVisible(true);
            }
        });
    }    
}