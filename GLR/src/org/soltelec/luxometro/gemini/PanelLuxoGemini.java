/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.luxometro.gemini;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import org.soltelec.luxometro.LlamarLuxometro;
import org.soltelec.util.DefectoGeneral;
import org.soltelec.util.MedidaGeneral;
import org.soltelec.util.RegistrarMedidas;
import org.soltelec.util.TipoAutomovil;



/**
 *
 * @author GerenciaDesarrollado
 */
public class PanelLuxoGemini extends JPanel implements ActionListener{
    private JButton buttonPaso1,buttonPaso2,buttonPaso3,buttonPaso4,buttonPaso5;
    private JButton botones[];
    private JProgressBar progressBar;
    private JLabel labelMensaje;
    private JLabel labelTitulo;
    private int indiceBoton = 0;
    private LogicaLuxoGemini logicaGemini = new LogicaLuxoGemini();
    private LlamarLuxometro llamarLuxometro = new LlamarLuxometro();
    private RegistrarMedidas regMedidas;
    private final String NOMBRE_ARCHIVO = "propiedades.properties";

    //Información del vehiculo
    private long idHojaPrueba,idPrueba,idUsuario;
    private String placaVehiculo;
    private int numeroTipoVehiculo;

    //
    private MedidaLuxometro med;

    //permisibles

   

    public PanelLuxoGemini(long idHojaPrueba, String placaVehiculo, long idPrueba, long idUsuario) {
        this();
        this.idHojaPrueba = idHojaPrueba;
        this.placaVehiculo = placaVehiculo;
        this.idPrueba = idPrueba;
        this.idUsuario = idUsuario;
    }

    public PanelLuxoGemini() {
        crearComponentes();
        ponerComponentes();
    }

    private void crearComponentes(){
        Font f = new Font(Font.SERIF,Font.BOLD,36);
        buttonPaso1 = new JButton("Archivo Entrada");
        buttonPaso2 = new JButton("Abrir Programa");
        buttonPaso3 = new JButton("Cargar Resultado");
        buttonPaso4 = new JButton("Registrar Medidas");
        buttonPaso5 = new JButton("Finalizar");
        botones = new JButton[]{buttonPaso1,buttonPaso2,buttonPaso3,buttonPaso4,buttonPaso5};
        for(JButton b:botones)
            b.addActionListener(this);
        labelMensaje = new JLabel();
        labelTitulo = new JLabel("LUXOMETRO GEMINI");
        labelTitulo.setFont(f);
        progressBar = new JProgressBar(0,5);
        deshabilitarOtrosBotones();
    }//end of method crearComponentes

    private void ponerComponentes() {
       GridBagLayout bag = new GridBagLayout();
       GridBagConstraints c = new GridBagConstraints();

       this.setLayout(bag);
       c.insets = new Insets(10,5,10,5);

       c.weightx = 0.8;

       c.weighty = 0.25;
       
       c.gridwidth = GridBagConstraints.REMAINDER;
       c.anchor = GridBagConstraints.CENTER;
       filaColumna(1,1,c);
       this.add(labelTitulo,c);
       c.gridwidth = 1;
       c.fill = GridBagConstraints.BOTH;
       filaColumna(2,1,c);
       this.add(buttonPaso1,c);
       filaColumna(2,2,c);
       this.add(buttonPaso2,c);
       filaColumna(2,3,c);
       this.add(buttonPaso3,c);
       filaColumna(2,4,c);
       this.add(buttonPaso4,c);
       filaColumna(2,5,c);
       this.add(buttonPaso5,c);
       filaColumna(3,1,c);
       c.gridwidth = 5;
       this.add(progressBar,c);
       c.gridwidth = 1;
       filaColumna(4,1,c);
       this.add(labelMensaje,c);
    }

    private void filaColumna(int fila, int columna,GridBagConstraints c){
        c.gridx = columna;
        c.gridy = fila;
    }

    public static void main(String args[]){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame app = new JFrame("Prueba Panel Faro");
                //PanelLuxoGemini panel = new PanelLuxoGemini(23,"");
                //app.getContentPane().add(panel);
                app.setExtendedState(JFrame.MAXIMIZED_BOTH);
                app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                app.setVisible(true);
            }
        });

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        deshabilitarOtrosBotones();
        if(e.getActionCommand().equals("Archivo Entrada")){
            try {
                Map<String, String> mapInfo = logicaGemini.traerInfo(idHojaPrueba);
                numeroTipoVehiculo = Integer.parseInt(mapInfo.get("TipoVehiculo"));
                logicaGemini.generarArchivoACC(mapInfo, idHojaPrueba);
                labelMensaje.setText("Archivo generado correctamente");
            } catch (ClassNotFoundException ex) {
                String mensaje = "No se encuentra el driver de MYSQL";
                System.out.println("No se encuentra el driver de MYSQL");
                manejarError(mensaje,ex);
            } catch (FileNotFoundException ex) {
                System.out.println("No se encuentra el archivo de propiedades");
                String mensaje = "No se encuentra el archivo de propiedades";
                manejarError(mensaje, ex);
            } catch (IOException ex) {
                String mensaje = "Error leyendo el archivo de propiedades\n"
                                 +" o generando el archivo .acc";
                System.out.println("Error leyendo el archivo de propiedades\n"
                                 +" o generando el archivo .acc");
                manejarError(mensaje, ex);
            } catch (SQLException ex) {
                String mensaje = "Error leyendo la base de datos";
                ex.printStackTrace(System.err);
                System.out.println("Error leyendo de la base de datos");
                manejarError(mensaje,ex);
            }

        }
        else if(e.getActionCommand().equals("Abrir Programa")){
               
                llamarLuxometro.LlamarProgramaLuxometro();
        }
        else if(e.getActionCommand().equals("Cargar Resultado")){
            try {
                med = logicaGemini.leerArchivo(placaVehiculo, idHojaPrueba);
            } catch (FileNotFoundException ex) {
                String mensaje = "Error leyendo el archivo de la prueba";
                ex.printStackTrace(System.err);
                System.out.println("Error leyendo el archivo generado por el luxometro");
                manejarError(mensaje,ex);
            }
                //generar las medidas y los defectos;
        }
        else if(e.getActionCommand().equals("Registrar Medidas")){
            //Tipo del carro;
            List<DefectoGeneral> listaDefectos = logicaGemini.validarMedida(med, obtenerTipoAutomovil(numeroTipoVehiculo));
            List<MedidaGeneral> listaMedidas = logicaGemini.generarMedidas(med, obtenerTipoAutomovil(numeroTipoVehiculo));
            regMedidas = new RegistrarMedidas();

            Connection con;

            //Registrar el resultado de la prueba;
            try {
                con = regMedidas.getConnection();
                con.setAutoCommit(false);
                regMedidas.registrarMedidas(listaMedidas, idPrueba, con);
                if(!listaDefectos.isEmpty()){
                    regMedidas.registrarDefectos(listaDefectos, idPrueba, con);
                    regMedidas.registrarPruebaFinalizada(con, false, idUsuario, idPrueba,false,"192");
                }else{
                    regMedidas.registrarPruebaFinalizada(con, true, idUsuario, idPrueba,false,"192");
                }
                con.setAutoCommit(true);
                con.close();
            } catch (ClassNotFoundException clexc) {

            } catch(SQLException sqlexc){
                System.out.println("Error registrando resultados");
                sqlexc.printStackTrace();
                manejarError("Error registrando resultados",sqlexc);
            }                
        }
        else if(e.getActionCommand().equals("Finalizar")){
            (SwingUtilities.getWindowAncestor(this)).dispose();
            //BOrrar los dos archivos
            
        }
        else if(e.getActionCommand().endsWith("Error Salir!")){
            (SwingUtilities.getWindowAncestor(this)).dispose();
        }
    }//end of Method ActionEvent

    private void deshabilitarOtrosBotones() {
 
        JButton jButton = botones[indiceBoton];
            for(JButton b:botones){
                if(jButton == b)
                    b.setEnabled(true);
                else
                    b.setEnabled(false);
            }
        if(indiceBoton == 4){
            indiceBoton = 0;
            progressBar.setValue(5);
        }else {
            indiceBoton++;
            progressBar.setValue(indiceBoton);
        }
            
    }

    /**
     * Metodo para manejar los posibles errores de la aplicacion
     * @param mensaje
     * @param ex
     */
    private void manejarError(String mensaje, Exception ex) {
            labelMensaje.setForeground(Color.red);
            labelMensaje.setText(mensaje);
            indiceBoton=4;
            buttonPaso5.setText("Error Salir!");
            deshabilitarOtrosBotones();
    }



    private TipoAutomovil obtenerTipoAutomovil(int numero){
        switch (numero){
            case 1:
                return TipoAutomovil.LIVIANO;
            case 2:
                return TipoAutomovil.CUATROXCUATRO;
            case 3:
                return TipoAutomovil.PESADO;
            case 4:
                return TipoAutomovil.MOTO;
            case 5:
                return TipoAutomovil.MOTOCARRO;
            default:
                return null;

        }
    }
    
    


}
