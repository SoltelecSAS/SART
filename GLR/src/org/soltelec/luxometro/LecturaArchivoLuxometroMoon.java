package org.soltelec.luxometro;

import com.soltelec.modulopuc.persistencia.conexion.DBUtil;
import com.soltelec.modulopuc.utilidades.Mensajes;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.soltelec.luxometro.capelec.cap2500.MedidasVehiculosLuxometro;
import org.soltelec.luxometro.capelec.cap2500.TipoLuces;
import org.soltelec.luxometro.capelec.cap2500.TipoLucesMoto;
import org.soltelec.procesosbanco.ProcesoRuido;
import org.soltelec.util.Conex;
import org.soltelec.util.ConsultarDatosVehiculo;
import org.soltelec.util.UtilPropiedades;
import sun.font.SunFontManager;

/**
 *
 * @author SOLTELEC
 */
public class LecturaArchivoLuxometroMoon {

    private static final Logger LOG = Logger.getLogger(LecturaArchivoLuxometroMoon.class.getName());
    private MedidasVehiculosLuxometro medidasVehiculos;
    private TipoLucesMoto luces;
    private TipoLuces TiposLuces;
    public static String tramaAuditoria;
    private Connection conexion;
    JCheckBox altas = new JCheckBox("Altas");
    JCheckBox bajas = new JCheckBox("Bajas");
    JCheckBox exploradoras = new JCheckBox("Exploradoras");
    public int aplicaModificacion;
    /* private int Luz2014;
    private int Luz2013;
    private int Luz2015;
    private int Luz2022;
    private boolean defectoExisteIntencidadLuzBaja=true;
    private boolean defectoExisteInclinacionLuzBaja=true;
    public int aplicTrans;    public int aplicTrans;*/
    public int cantFarolas;
    boolean lucesAltas = false, lucesBajas = false;
    boolean lucesExploradoras = false;
    private String location = "";

    public LecturaArchivoLuxometroMoon(int aplicTrans) {
        LOG.info("--------------------------------------------");
        LOG.info("--------------  Contructor   ---------------");
        LOG.info("--------------------------------------------");
        try {
            conexion = Conex.getConnection();
        } catch (ClassNotFoundException ex) {
            LOG.info("Error al conectar con la db " + ex);
            JOptionPane.showMessageDialog(null, "Error al conectar con la db : " + ex);
            Logger.getLogger(LecturaArchivoLuxometroMoon.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.aplicaModificacion = aplicTrans;

    }

    private Double[][] medidas = {
        /*bajas altas*/
        {null, null, null}, //{intBaja, inclinacionBaja, intAlta} farola 1
        {null, null, null}, //{intBaja, inclinacionBaja, intAlta} farola 2
        {null, null, null}  //{intBaja, inclinacionBaja, intAlta} farola 3
    };

    private Integer[][] codigosMedidas = {
        /*bajas altas*/
        {2014, 2013, 2056}, //{intBaja, inclinacionBaja, intAlta} measureTypes farola 1
        {2015, 2002, 2057}, //{intBaja, inclinacionBaja, intAlta} measureTypes farola 2
        {2000, 2022, 2061}  //{intBaja, inclinacionBaja, intAlta} measureTypes farola 3
    };

    public void iniciarTomaMedidaLuxometro(String placa, int idHojaPrueba, int idPrueba, int idUsuario, int tipoVehiculo, String location) throws IOException, InterruptedException {
        this.location = location;
        Object[] opciones = {1, 2, 3};
        Object opcion = JOptionPane.showInputDialog(null, "Bienvenido a la prueba de luces! \n Selecciona Cantidad Farolas", "Elegir", JOptionPane.QUESTION_MESSAGE, null, opciones, 1);

        if (opcion == null) {
            Mensajes.messageDoneTime("Prueba cancelada, sera redirigido al menu principal", 5);
            return;
        }
        int cantidadFarolas = (int) opcion;

        for (int i = 0; i < cantidadFarolas; i++) {
            Mensajes.messageDoneTime("Se inicia la toma de datos para la farola : " + (i + 1), 5);

            String msg = "Seleccione el tipo de luces que posee la farola " + (i + 1); //MENSAJE
            Object[] msgContent = {msg, altas, bajas}; //CHECK BOX PARA SABER QUE TIPO DE LUCES CONTIENE LA FAROLA SI ALTAS O BAJAS O LAS DOS AL TIEMPO
            bajas.setSelected(true);
            bajas.setEnabled(false);

            JOptionPane.showConfirmDialog(null, msgContent, "Tipos de luces", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);

            boolean medirAltas = altas.isSelected() == true ? true : false;

            createSalidaDat(placa, medirAltas);

            String cmdLocation = location.equals(".") ? System.getProperty("user.dir") : location;
            final Process process = Runtime.getRuntime().exec(String.format("cmd /c start /wait %s/moon/luxometro.bat", cmdLocation));//INICIA PROGRAMA LUXOMETRO
            final int exitVal = process.waitFor();
            if (exitVal == 0){//MIENTRAS EL PROGRAMA NO SE CIERRE
                String ruta = location + "/moon/SALIDA.DAT";
                BufferedReader config = null;
                try {
                    config = new BufferedReader(new FileReader(new File(ruta)));
                    String linea;
                    int contadorLineas = 0;
                    int contador2 = 0; //indica cuantas pruebas no se pudieron leer
                    while (true) {
                        contadorLineas++;

                        linea = config.readLine();

                        if (contadorLineas == 32) break;

                        System.out.println("Linea: "+contadorLineas);
                        if (linea.equals("") || linea == null || linea.equals(",")) continue;

                        if (contadorLineas == 21){
                            if(linea == null || linea.equals("")) contador2++;
                            medidas[i][0] = Double.parseDouble(linea.split(",")[1]) / 1000;
                        }
                        if (contadorLineas == 22) {
                            if(linea == null || linea.equals("")) contador2++;
                            medidas[i][1] = Double.parseDouble(linea.split(",")[1]) * -1;
                        }
                        if (contadorLineas == 31) {
                            if(linea == null || linea.equals("")) contador2++;
                            if(medirAltas) medidas[i][2] = Double.parseDouble(linea.split(",")[1]) / 1000;
                            if (contador2 >= 3) {
                                System.out.println("Error contador2");
                                JOptionPane.showMessageDialog(null, "Se salio del programa sin cargar las medidas. Repita la prueba");
                                throw new RuntimeException("No se encontraron medidas en SALIDA.DAT");
                            }
                        }
                    }

                    // Crear un objeto File
                    File archivo = new File(ruta);

                    // Verificar si el archivo existe
                    if (archivo.exists()) {
                        // Intentar vaciar el contenido del archivo
                        try (FileWriter fw = new FileWriter(archivo, false)) {
                            // Al abrir el archivo en modo de escritura sin append, se trunca el contenido
                            System.out.println("El archivo fue vaciado con éxito.");
                        } catch (IOException e) {
                            System.out.println( "No se pudo vaciar el archivo: "+ e.getMessage());
                        }
                    } else {
                        System.out.println("El archivo no existe: "+ ruta);
                    }
                }catch (IOException ex) {
                    System.out.println("Error1: ");
                    ex.printStackTrace(System.err);
                    JOptionPane.showMessageDialog(null, "Error al tratar de leer el archivo de medicion. Revise la carpeta: " + ruta);
                } finally {
                    if (config != null) {
                        try {
                            config.close();
                        } catch (IOException e) {
                            System.out.println("Error2: ");
                            e.printStackTrace(System.err);
                        }
                    }
                }
            }
            Mensajes.messageDoneTime("Se ha terminado la toma de datos de la falora : " + (i + 1), 5);
        }

        for (int i = 0; i < medidas.length; i++) {
            for (int j = 0; j < medidas[i].length; j++) {
                System.out.println("Farola " + (i + 1) + ", Medida " + (j + 1) + ":");
                System.out.println("  Código de Medida: " + codigosMedidas[i][j]);
                System.out.println("  Valor de Medida: " + medidas[i][j]);
            }
        }
    }

    private void createSalidaDat(String placa, boolean highBeam) {
        try {
            File file = new File(location + "/moon/ENTRADA.DAT");

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(placa);
            bw.newLine();
            bw.write("L3e");
            bw.newLine();
            bw.newLine();
            bw.newLine();
            bw.newLine();
            bw.newLine();
            bw.write(highBeam ? "Y" : "N");
            bw.close();
            System.out.println("Se logro crear el archivo-placa ENTRADA.DAT");
            File file2 = new File(location + "/moon/SALIDA.DAT");
            if (!file2.exists()) {
                file2.createNewFile();
            }
            System.out.println("Se logro crear el archivo-placa SALIDA.DAT");
        } catch (IOException e) {
            System.out.println( "Problemas al crear el archivo de entrada: "+ e.getMessage());
        }
    }
}
