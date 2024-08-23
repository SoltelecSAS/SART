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

/*     
    public void iniciarTomaMedidaLuxometroO(String placa, int idHojaPrueba, int idPrueba, int idUsuario, int tipoVehiculo) {
        LOG.info("--------------------------------------------");
        LOG.info("------  iniciarTomaMedidaLuxometro  --------");
        LOG.info("--------------------------------------------");

        try {
            if (tipoVehiculo == 1) //SI EL TIPO DE VEHICULO ES UNA MOTO
            {
                Object[] colores = {1, 2, 3};
                Object opcion = JOptionPane.showInputDialog(null, "Bienvenido a la prueba de luces! \n Selecciona Cantidad Farolas", "Elegir", JOptionPane.QUESTION_MESSAGE, null, colores, 1);

                if (opcion == null) {
                    Mensajes.messageDoneTime("Prueba cancelada, sera redirigido al menu principal", 5);
                    return;
                }
                int cantidadFarolas = (int) opcion;
                System.out.println("-------------------CANTIDAD DE FAROLAS----------------------- " + cantidadFarolas);
                int contador = 0;
                medidasVehiculos = new MedidasVehiculosLuxometro();
                luces = new TipoLucesMoto();

                while (contador < cantidadFarolas) {
                    Mensajes.messageDoneTime("Se inicia la toma de datos para la farola : " + (contador + 1), 5);
                    if (cantidadFarolas == 1)//SI SOLO TIENE UNA FAROLA
                    {
                        System.out.println("entre a luces 1------------------------------------------------------------------------------------------");
                        String msg = "Seleccione el tipo de luces que posee la farola " + (contador + 1); //MENSAJE
                        Object[] msgContent = {msg, altas, bajas}; //CHECK BOX PARA SABER QUE TIPO DE LUCES CONTIENE LA FAROLA SI ALTAS O BAJAS O LAS DOS AL TIEMPO
                        bajas.setSelected(true);
                        do {
                            int n = JOptionPane.showConfirmDialog(null, msgContent, "Tipos de luces", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);  //CREO EL JOPTION PANE SIN EL BOTON CANCELAR  

                            lucesAltas = altas.isSelected();//VERIFICA QUE CHECK BOX ESTA SELECCIONADO
                            lucesBajas = bajas.isSelected();//VERIFICA QUE CHECK BOX ESTA SELECCIONADO
                        } while (!lucesAltas && !lucesBajas || !lucesBajas); //VERIFICA QUE ESTE SELECCIONADO
                        luces.setLuzAltaDerecha(lucesAltas);
                        luces.setLuzBajaDerecha(lucesBajas);
                        System.out.println("farola 1, tiene luces altas : " + lucesAltas + ", tiene luces bajas : " + lucesBajas);

                        crearPlacaFile(placa, altas.isSelected());//ESCRIBE LA PLACA EN EL ARCHIVO ENTRADA.DAT
                        final Process process = Runtime.getRuntime().exec(String.format("cmd /c start /wait %s/luxometro/luxometro.bat", System.getProperty("user.dir")));//EJECUTA EL PROGRAMA DEL LUXOMETRO
                        final int exitVal = process.waitFor();
                        if (exitVal == 0)//MIENTRAS EL PROGRAMA SE ESTA EJECUTANDO 
                        {
                            if (contador == 0)//LEE LOS DATOS DE LA PRIMERA FAROLA 1
                            {
                                copiaArchivoDatos(System.getProperty("user.dir") + "/luxometro/SALIDA.DAT", System.getProperty("user.dir") + "/luxometro/DatosFarola1.DAT"); //COPIA LOS DATOS DEL ARCHIVO SALIDA.DAT A FAROLA1.DAT
                                if (lucesBajas) {//SI EL CHECK BOX DE LUCES BAJAS ESTA SELECCIONADO
                                    medidasVehiculos.setIntensidadLuzBajaDerecha(obtenerMedida(21));//OBTIENE LA INTENSIDAD DE LA LUZ BAJA DERECHA
                                    medidasVehiculos.setInclinacionLuzBajaDerecha(obtenerMedida(22));//OBTIENE LA INCLINACION DE LA LUZ BAJA DERECHA
                                    lucesBajas = false;
                                    bajas.setSelected(false);//DESMARCA CHECKBOX
                                    System.out.println("si tiene luces bajas en la farola 1");

                                }
                                if (lucesAltas) {//SI EL CHECK BOX DE LUCES ALTAS ESTA SELECCIONADO
                                    medidasVehiculos.setIntensidadLuzAltaDerecha(obtenerMedida(31));//LEE INTENSIDAD LUZ ALTA DERECHA
                                    lucesAltas = false;
                                    altas.setSelected(false);//DESMARCA CHECKBOX
                                    System.out.println("si tiene luces altas en la farola 1, VALOR:" + obtenerMedida(31));
                                }
                            }

                        }
                        process.destroy();//CIERRA EL PROGRAMA DELLUXOMETRO
                        contador++;
                        //  registrarPrueba(idPrueba, idUsuario,contador);//REGISTRA LA PRUEBA

                    }

                    if (cantidadFarolas == 2)//SI LA CANTIDAD DE FAROLAS ES IGUAL A 2
                    {
                        System.out.println("entre a luces 2------------------------------------------------------------------------------------------");
                        System.out.println("hay 2 farolas");

                        String msg = "Seleccione el tipo de luces que posee la farola " + (contador + 1); //MENSAJE
                        Object[] msgContent = {msg, altas, bajas}; //CHECK BOX PARA SABER QUE TIPO DE LUCES CONTIENE LA FAROLA SI ALTAS O BAJAS O LAS DOS AL TIEMPO
                        if (contador == 1)//SI ESTA EN LA FAROLA NUMERO DOS MUESTRA OPCIONES SIN EL BOTON CANCELAR 
                        {
                            System.out.println(" contador =" + contador + " aqui deberia ser 1");
                            do {
                                int n = JOptionPane.showConfirmDialog(null, msgContent, "Tipos de luces", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);  //CREO EL JOPTION PANE SIN EL BOTON CANCELAR                      
                                lucesAltas = altas.isSelected();//VERIFICA QUE CHECK BOX ESTA SELECCIONADO
                                lucesBajas = bajas.isSelected();//VERIFICA QUE CHECK BOX ESTA SELECCIONADO
                            } while (!lucesAltas && !lucesBajas); //VERIFICA QUE POR LO MENOS UN CHECK BOX ESTE SELECCIONADO
                            luces.setLuzAltaIzquierda(lucesAltas);
                            luces.setLuzBajaIzquierda(lucesBajas);
                            System.out.println("farola 2, tiene luces altas : " + lucesAltas + ", tiene luces bajas : " + lucesBajas);

                        } else {//SI ESTA EN LA FAROLA NUMERO UNO PERMITE CANCELAR EL PROCESO
                            System.out.println(" contador =" + contador + " aqui deberia ser diferente de 1");

                            do {
                                int n = JOptionPane.showConfirmDialog(null, msgContent, "Tipos de luces", JOptionPane.OK_CANCEL_OPTION);
                                if (n == JOptionPane.CANCEL_OPTION) {
                                    Mensajes.messageDoneTime("Prueba cancelada, sera redirigido al menu principal", 5);//DEVUELVE AL MENU PRINCIPAL SI SE DA CLIC EN CANCELAR
                                    return;
                                }
                                lucesAltas = altas.isSelected();//VERIFICA QUE CHECK BOX ESTA SELECCIONADO
                                lucesBajas = bajas.isSelected();//VERIFICA QUE CHECK BOX ESTA SELECCIONADO
                            } while (!lucesAltas && !lucesBajas); //VERIFICA QUE POR LO MENOS UN CHECK BOX ESTE SELECCIONADO
                            luces.setLuzAltaDerecha(lucesAltas);
                            luces.setLuzBajaDerecha(lucesBajas);
                            System.out.println("farola 1, tiene luces altas : " + lucesAltas + ", tiene luces bajas : " + lucesBajas);
                        }

                        crearPlacaFile(placa, altas.isSelected());//GRABA LA PLACA EN EL ARCHIVO ENTRADA.DAT

                        final Process process = Runtime.getRuntime().exec(String.format("cmd /c start /wait %s/luxometro/luxometro.bat", System.getProperty("user.dir")));//INICIA PROGRAMA LUXOMETRO
                        final int exitVal = process.waitFor();
                        if (exitVal == 0)//MIENTRAS EL PROGRAMA NO SE CIERRE
                        {
                            if (contador == 0)//LEE LOS DATOS DE LA PRIMERA FAROLA 1
                            {
                                copiaArchivoDatos(System.getProperty("user.dir") + "/luxometro/SALIDA.DAT", System.getProperty("user.dir") + "/luxometro/DatosFarola1.DAT"); //COPIA LOS DATOS DEL ARCHIVO SALIDA.DAT A FAROLA1.DAT
                                if (lucesBajas) {//SI EL CHECK BOX DE LUCES BAJAS ESTA SELECCIONADO
                                    medidasVehiculos.setIntensidadLuzBajaDerecha(obtenerMedida(21));//OBTIENE LA INTENSIDAD DE LA LUZ BAJA DERECHA
                                    medidasVehiculos.setInclinacionLuzBajaDerecha(obtenerMedida(22));//OBTIENE LA INCLINACION DE LA LUZ BAJA DERECHA
                                    lucesBajas = false;
                                    bajas.setSelected(false);//DESMARCA CHECKBOX
                                    System.out.println("si tiene luces bajas en la farola 1");

                                }
                                if (lucesAltas) {//SI EL CHECK BOX DE LUCES ALTAS ESTA SELECCIONADO
                                    medidasVehiculos.setIntensidadLuzAltaDerecha(obtenerMedida(31));//LEE INTENSIDAD LUZ ALTA DERECHA
                                    lucesAltas = false;
                                    altas.setSelected(false);//DESMARCA CHECKBOX
                                    System.out.println("si tiene luces altas en la farola 1, VALOR:" + obtenerMedida(31));
                                }

                            } else { //LEE LOS DATOS DE LA SEGUNDA FAROLA 

                                copiaArchivoDatos(System.getProperty("user.dir") + "/luxometro/SALIDA.DAT", System.getProperty("user.dir") + "/luxometro/DatosFarola2.DAT"); //COPIA LOS DATOS DEL ARCHIVO SALIDA.DAT A FAROLA2.DAT
                                if (lucesBajas) {//SI EL CHECK BOX DE LUCES BAJAS ESTA SELECCIONADO
                                    medidasVehiculos.setIntensidadLuzBajaIzquierda(obtenerMedida(21));//OBTIENE INTENSIDAD LUZ BAJAIZQUIERDA
                                    medidasVehiculos.setInclinacionLuzBajaIzquierda(obtenerMedida(22));//OBTIENE INCLINACION LUZ BAJA IZQUIERDA
                                    lucesBajas = false;
                                    bajas.setSelected(false);//DESMARCA CHECKBOX  
                                    System.out.println("si tiene luces bajas en la farola 2");
                                }
                                if (lucesAltas) {//SI EL CHECK BOX DE LUCES ALTAS ESTA SELECCIONADO
                                    medidasVehiculos.setIntensidadLuzAltaIzquierda(obtenerMedida(31));//LEE INTENSIDAD LUZ ALTAIZQUIERDA
                                    lucesAltas = false;
                                    altas.setSelected(false);//DESMARCA CHECKBOX
                                    System.out.println("si tiene luces altas en la farola 2");
                                }
                            }

                        }
                        process.destroy();//CIERRA PROGRAMA LUXOMETRO
                        contador++;

                    }
//////////////////////////////////////////////////////SI TIENE 3 FAROLAS
                    if (cantidadFarolas == 3)//SI LA CANTIDAD DE FAROLAS ES IGUAL A 2
                    {
                        System.out.println("hay 3 farolas");

                        String msg = "Seleccione el tipo de luces que posee la farola " + (contador + 1); //MENSAJE
                        Object[] msgContent = {msg, altas, bajas}; //CHECK BOX PARA SABER QUE TIPO DE LUCES CONTIENE LA FAROLA SI ALTAS O BAJAS O LAS DOS AL TIEMPO

                        if (contador == 0) {//SI ESTA EN LA FAROLA NUMERO UNO PERMITE CANCELAR EL PROCESO
                            System.out.println(" contador =" + contador + " aqui deberia ser diferente de 1");

                            do {
                                int n = JOptionPane.showConfirmDialog(null, msgContent, "Tipos de luces", JOptionPane.OK_CANCEL_OPTION);
                                if (n == JOptionPane.CANCEL_OPTION) {
                                    Mensajes.messageDoneTime("Prueba cancelada, sera redirigido al menu principal", 5);//DEVUELVE AL MENU PRINCIPAL SI SE DA CLIC EN CANCELAR
                                    return;
                                }
                                lucesAltas = altas.isSelected();//VERIFICA QUE CHECK BOX ESTA SELECCIONADO
                                lucesBajas = bajas.isSelected();//VERIFICA QUE CHECK BOX ESTA SELECCIONADO
                            } while (!lucesAltas && !lucesBajas); //VERIFICA QUE POR LO MENOS UN CHECK BOX ESTE SELECCIONADO
                            luces.setLuzAltaDerecha(lucesAltas);
                            luces.setLuzBajaDerecha(lucesBajas);
                            System.out.println("farola 1, tiene luces altas : " + lucesAltas + ", tiene luces bajas : " + lucesBajas);
                        } else//SI ESTA EN LA FAROLA NUMERO DOS O TRES MUESTRA OPCIONES SIN EL BOTON CANCELAR 
                        {
                            System.out.println(" contador =" + contador + " aqui deberia ser 1 o 2");
                            do {
                                int n = JOptionPane.showConfirmDialog(null, msgContent, "Tipos de luces", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);  //CREO EL JOPTION PANE SIN EL BOTON CANCELAR                      
                                lucesAltas = altas.isSelected();//VERIFICA QUE CHECK BOX ESTA SELECCIONADO
                                lucesBajas = bajas.isSelected();//VERIFICA QUE CHECK BOX ESTA SELECCIONADO
                            } while (!lucesAltas && !lucesBajas); //VERIFICA QUE POR LO MENOS UN CHECK BOX ESTE SELECCIONADO
                            if (contador == 1) {
                                luces.setLuzAltaIzquierda(lucesAltas);
                                luces.setLuzBajaIzquierda(lucesBajas);
                                System.out.println("farola 3, tiene luces altas : " + lucesAltas + ", tiene luces bajas : " + lucesBajas);
                            }
                            if (contador == 2) {
                                luces.setLuzAlta3(lucesAltas);
                                luces.setLuzBaja3(lucesBajas);
                                System.out.println("farola 3, tiene luces altas : " + lucesAltas + ", tiene luces bajas : " + lucesBajas);
                            }

                        }

                        crearPlacaFile(placa, altas.isSelected());//GRABA LA PLACA EN EL ARCHIVO ENTRADA.DAT

                        final Process process = Runtime.getRuntime().exec(String.format("cmd /c start /wait %s/luxometro/luxometro.bat", System.getProperty("user.dir")));//INICIA PROGRAMA LUXOMETRO
                        final int exitVal = process.waitFor();
                        if (exitVal == 0)//MIENTRAS EL PROGRAMA NO SE CIERRE
                        {
                            if (contador == 0)//LEE LOS DATOS DE LA PRIMERA FAROLA 1
                            {
                                copiaArchivoDatos(System.getProperty("user.dir") + "/luxometro/SALIDA.DAT", System.getProperty("user.dir") + "/luxometro/DatosFarola1.DAT"); //COPIA LOS DATOS DEL ARCHIVO SALIDA.DAT A FAROLA1.DAT
                                if (lucesBajas) {//SI EL CHECK BOX DE LUCES BAJAS ESTA SELECCIONADO
                                    medidasVehiculos.setIntensidadLuzBajaDerecha(obtenerMedida(21));//OBTIENE LA INTENSIDAD DE LA LUZ BAJA DERECHA
                                    medidasVehiculos.setInclinacionLuzBajaDerecha(obtenerMedida(22));//OBTIENE LA INCLINACION DE LA LUZ BAJA DERECHA
                                    lucesBajas = false;
                                    bajas.setSelected(false);//DESMARCA CHECKBOX
                                    System.out.println("si tiene luces bajas en la farola 1");

                                }
                                if (lucesAltas) {//SI EL CHECK BOX DE LUCES ALTAS ESTA SELECCIONADO
                                    medidasVehiculos.setIntensidadLuzAltaDerecha(obtenerMedida(31));//LEE INTENSIDAD LUZ ALTA DERECHA
                                    lucesAltas = false;
                                    altas.setSelected(false);//DESMARCA CHECKBOX
                                    System.out.println("si tiene luces altas en la farola 1, VALOR:" + obtenerMedida(31));
                                }

                            } else if (contador == 1) { //LEE LOS DATOS DE LA SEGUNDA FAROLA 

                                copiaArchivoDatos(System.getProperty("user.dir") + "/luxometro/SALIDA.DAT", System.getProperty("user.dir") + "/luxometro/DatosFarola2.DAT"); //COPIA LOS DATOS DEL ARCHIVO SALIDA.DAT A FAROLA2.DAT
                                if (lucesBajas) {//SI EL CHECK BOX DE LUCES BAJAS ESTA SELECCIONADO
                                    medidasVehiculos.setIntensidadLuzBajaIzquierda(obtenerMedida(21));//OBTIENE INTENSIDAD LUZ BAJAIZQUIERDA
                                    medidasVehiculos.setInclinacionLuzBajaIzquierda(obtenerMedida(22));//OBTIENE INCLINACION LUZ BAJA IZQUIERDA
                                    lucesBajas = false;
                                    bajas.setSelected(false);//DESMARCA CHECKBOX  
                                    System.out.println("si tiene luces bajas en la farola 2");
                                }
                                if (lucesAltas) {//SI EL CHECK BOX DE LUCES ALTAS ESTA SELECCIONADO
                                    medidasVehiculos.setIntensidadLuzAltaIzquierda(obtenerMedida(31));//LEE INTENSIDAD LUZ ALTAIZQUIERDA
                                    lucesAltas = false;
                                    altas.setSelected(false);//DESMARCA CHECKBOX
                                    System.out.println("si tiene luces altas en la farola 2");
                                }
                            } else if (contador == 2) { //LEE LOS DATOS DE LA TERCERA FAROLA 

                                copiaArchivoDatos(System.getProperty("user.dir") + "/luxometro/SALIDA.DAT", System.getProperty("user.dir") + "/luxometro/DatosFarola3.DAT"); //COPIA LOS DATOS DEL ARCHIVO SALIDA.DAT A FAROLA2.DAT
                                if (lucesBajas) {//SI EL CHECK BOX DE LUCES BAJAS ESTA SELECCIONADO
                                    medidasVehiculos.setIntensidadLuzBaja3(obtenerMedida(21));//OBTIENE INTENSIDAD LUZ BAJAIZQUIERDA
                                    medidasVehiculos.setInclinacionLuzBaja3(obtenerMedida(22));//OBTIENE INCLINACION LUZ BAJA IZQUIERDA
                                    lucesBajas = false;
                                    bajas.setSelected(false);//DESMARCA CHECKBOX  
                                    System.out.println("si tiene luces bajas en la farola 3");
                                }
                                if (lucesAltas) {//SI EL CHECK BOX DE LUCES ALTAS ESTA SELECCIONADO
                                    medidasVehiculos.setIntensidadLuzAlta3(obtenerMedida(31));//LEE INTENSIDAD LUZ ALTAIZQUIERDA
                                    lucesAltas = false;
                                    altas.setSelected(false);//DESMARCA CHECKBOX
                                    System.out.println("si tiene luces altas en la farola 3");
                                }
                            }

                        }
                        process.destroy();//CIERRA PROGRAMA LUXOMETRO
                        contador++;

                    }
//////////////////////////////////////////////////////////////////////
                    if (org.soltelec.util.Mensajes.mensajePregunta("¿Desea Agregar un Comentario a la Prueba?")) {
                        org.soltelec.luxometro.FrmComentariov2 frm = new org.soltelec.luxometro.FrmComentariov2(null, idPrueba, JDialog.DEFAULT_MODALITY_TYPE, " ");
                        frm.setVisible(true);
                        frm.setModal(true);
                    }
                }
                //registrarPrueba(idPrueba, idUsuario, contador);//REGISTRA LA PRUEBA

                conexion.setAutoCommit(true);
                conexion.close();
                Mensajes.messageDoneTime("Se ha Registrado la Prueba de Luces de una manera Exitosa ..¡", 5);

                //--------------------------------------------------------------SI EL VEHICULO ES DIFERENTE DE MOTO----------------------------------------------------------------------------------------                  
            } else if (tipoVehiculo == 2) {
                int contador = 0;
                JOptionPane.showMessageDialog(null, "Bienvenido a la prueba de luces!");
                String msg = "Seleccione el tipo de luces " + (contador + 1); //MENSAJE
                Object[] msgContent = {msg, altas, bajas}; //CHECK BOX PARA SABER QUE TIPO DE LUCES CONTIENE LA FAROLA SI ALTAS O BAJAS O LAS DOS AL TIEMPO
                do {
                    int n = JOptionPane.showConfirmDialog(null, msgContent, "Tipos de luces", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);  //CREO EL JOPTION PANE SIN EL BOTON CANCELAR  

                    lucesAltas = altas.isSelected();//VERIFICA QUE CHECK BOX ESTA SELECCIONADO
                    lucesBajas = bajas.isSelected();//VERIFICA QUE CHECK BOX ESTA SELECCIONADO
                } while (!lucesAltas && !lucesBajas || !lucesBajas); //VERIFICA QUE ESTE SELECCIONADO
                int opcion = JOptionPane.showConfirmDialog(null, "¿El vehiculo tiene exploradoras?", "Pregunta", JOptionPane.YES_NO_OPTION);
                if (opcion == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(null, "Procede lectura de farolas CON Exploradoras");
                } else if (opcion == JOptionPane.NO_OPTION) {
                    JOptionPane.showMessageDialog(null, "Procede lectura de farolas SIN Exploradoras");
                }

                int cantidadFarolas = 1;
                medidasVehiculos = new MedidasVehiculosLuxometro();
                TiposLuces = new TipoLuces();

                while (contador < cantidadFarolas) {
                    Mensajes.messageDoneTime("Se inicia la toma de datos " + (contador + 1), 5);

                    //createSalidaDat(placa);//ESCRIBE LA PLACA EN EL ARCHIVO ENTRADA.DAT
                    final Process process = Runtime.getRuntime().exec(String.format("cmd /c start /wait %s/luxometro/luxometro.bat", System.getProperty("user.dir")));//EJECUTA EL PROGRAMA DEL LUXOMETRO
                    final int exitVal = process.waitFor();
                    if (exitVal == 0)//MIENTRAS EL PROGRAMA SE ESTA EJECUTANDO 
                    {
                        if (contador == 0)//LEE LOS DATOS DE LA PRIMERA FAROLA 1
                        {
                            copiaArchivoDatos(System.getProperty("user.dir") + "/luxometro/SALIDA.DAT", System.getProperty("user.dir") + "/luxometro/DatosFarola1.DAT"); //COPIA LOS DATOS DEL ARCHIVO SALIDA.DAT A FAROLA1.DAT
                            if (lucesBajas) {//SI EL CHECK BOX DE LUCES BAJAS ESTA SELECCIONADO
                                medidasVehiculos.setIntensidadLuzBajaDerecha(obtenerMedida(21));//OBTIENE LA INTENSIDAD DE LA LUZ BAJA DERECHA
                                medidasVehiculos.setInclinacionLuzBajaDerecha(obtenerMedida(22));//OBTIENE LA INCLINACION DE LA LUZ BAJA DERECHA
                                lucesBajas = false;
                                bajas.setSelected(false);//DESMARCA CHECKBOX
                                System.out.println("si tiene luces bajas en la farola 1");

                            }
                            if (lucesAltas) {//SI EL CHECK BOX DE LUCES ALTAS ESTA SELECCIONADO
                                medidasVehiculos.setIntensidadLuzAltaDerecha(obtenerMedida(31));//LEE INTENSIDAD LUZ ALTA DERECHA
                                lucesAltas = false;
                                altas.setSelected(false);//DESMARCA CHECKBOX
                                System.out.println("si tiene luces altas en la farola 1, VALOR:" + obtenerMedida(31));
                            }
                            if (opcion == JOptionPane.YES_OPTION) {
                                medidasVehiculos.setIntensidadLuzAltaDerecha(obtenerMedida(31));//verificar cual es la luz exploradora
                                lucesExploradoras = false;
                                altas.setSelected(false);//DESMARCA CHECKBOX
                                System.out.println("si tiene luces exp en la farola 1, VALOR:" + obtenerMedida(31));
                            }

                            do {
                                int respuesta = JOptionPane.showConfirmDialog(null, "¿Deseas agregar otra luz?", "Agregar Luz", JOptionPane.YES_NO_OPTION);

                                if (respuesta == JOptionPane.YES_OPTION) {
                                    String[] opcionesLuz = {"Luz Alta", "Luz Baja", "Luz Exploradora"};
                                    int tipoLuz = JOptionPane.showOptionDialog(null, "Selecciona el tipo de luz a registrar:", "Tipo de Luz",
                                            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                                            null, opcionesLuz, opcionesLuz[0]);
                                    String tipoSeleccionado = opcionesLuz[tipoLuz];
                                    if (tipoLuz != JOptionPane.CLOSED_OPTION) {

                                        if (tipoSeleccionado.equals("Luz Alta")) {
                                            JOptionPane.showMessageDialog(null, "Agregando luz alta...");
                                            System.out.println("Agregando luz alta...");
                                            // Aquí puedes poner el código para agregar una luz alta
                                        } else if (tipoSeleccionado.equals("Luz Baja")) {
                                            JOptionPane.showMessageDialog(null, "Agregando luz baja...");
                                            System.out.println("Agregando luz baja...");
                                            // Aquí puedes poner el código para agregar una luz baja
                                        } else if (tipoSeleccionado.equals("Luz Exploradora")) {
                                            JOptionPane.showMessageDialog(null, "Agregando luz exploradora...");
                                            System.out.println("Agregando luz exploradora...");
                                            // Aquí puedes poner el código para agregar una luz exploradora
                                        }
                                    }
                                    //createSalidaDat(placa);//ESCRIBE LA PLACA EN EL ARCHIVO ENTRADA.DAT
                                    Runtime.getRuntime().exec(String.format("cmd /c start /wait %s/luxometro/luxometro.bat", System.getProperty("user.dir")));//EJECUTA EL PROGRAMA DEL LUXOMETRO
                                    process.waitFor();
                                    if (exitVal == 0)//MIENTRAS EL PROGRAMA SE ESTA EJECUTANDO 
                                    {
                                        copiaArchivoDatos(System.getProperty("user.dir") + "/luxometro/SALIDA.DAT", System.getProperty("user.dir") + "/luxometro/DatosFarola1.DAT"); //COPIA LOS DATOS DEL ARCHIVO SALIDA.DAT A FAROLA1.DAT
                                        if (tipoSeleccionado.equals("Luz Alta")) {
                                            medidasVehiculos.setIntensidadLuzBajaDerecha(obtenerMedida(21));//OBTIENE LA INTENSIDAD DE LA LUZ BAJA DERECHA
                                            medidasVehiculos.setInclinacionLuzBajaDerecha(obtenerMedida(22));//OBTIENE LA INCLINACION DE LA LUZ BAJA DERECHA
                                            lucesBajas = false;
                                            bajas.setSelected(false);//DESMARCA CHECKBOX

                                        }
                                        if (tipoSeleccionado.equals("Luz Baja")) {
                                            medidasVehiculos.setIntensidadLuzBajaDerecha(obtenerMedida(21));//OBTIENE LA INTENSIDAD DE LA LUZ BAJA DERECHA
                                            medidasVehiculos.setInclinacionLuzBajaDerecha(obtenerMedida(22));//OBTIENE LA INCLINACION DE LA LUZ BAJA DERECHA
                                            lucesBajas = false;
                                            bajas.setSelected(false);//DESMARCA CHECKBOX

                                        }
                                        if (tipoSeleccionado.equals("Luz Exploradora")) {
                                            medidasVehiculos.setIntensidadLuzBajaDerecha(obtenerMedida(21));//OBTIENE LA INTENSIDAD DE LA LUZ BAJA DERECHA
                                            medidasVehiculos.setInclinacionLuzBajaDerecha(obtenerMedida(22));//OBTIENE LA INCLINACION DE LA LUZ BAJA DERECHA
                                            lucesBajas = false;
                                            bajas.setSelected(false);//DESMARCA CHECKBOX 

                                        }

                                    }

                                } else {
                                    JOptionPane.showMessageDialog(null, "No se agregará otra luz.");
                                    break;
                                }
                            } while (true);
                        }

                    }
                    process.destroy();//CIERRA EL PROGRAMA DELLUXOMETRO
                    contador++;
                    //  registrarPrueba(idPrueba, idUsuario,contador);//REGISTRA LA PRUEBA

                }

            } //--------------------------------------------------------------SI EL VEHICULO ES DIFERENTE DE MOTO Y DE CARRO----------------------------------------------------------------------------------------                  
            else {
                Mensajes.messageDoneTime("::::El Tipo de vehiculo No aplica :: \n  Solo motos con 1 o 2 farolas", 5);
            }
//        } catch (IOException | InterruptedException e) 
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Problemas al iniciar el programa {0}", e);
        }
    }

    private static void crearPlacaFile(String placa, boolean highBeam) {
        try {
            File file = new File(System.getProperty("user.dir") + "/luxometro/ENTRADA.DAT");

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

            LOG.log(Level.INFO, "Se logro crear el archivo-placa ENTRADA.DAT");
            File file2 = new File(System.getProperty("user.dir") + "/luxometro/SALIDA.DAT");
            if (!file2.exists()) {
                file2.createNewFile();
            }
            LOG.log(Level.INFO, "Se logro crear el archivo-placa SALIDA.DAT");
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Problemas al crear el archivo de entrada {0}", e);
        }
    }
 */
    /* 
    private static double obtenerMedida(int index) {
        BufferedReader br = null;
        String medida = "";
        int contador = 1;
        try {
            br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/luxometro/SALIDA.DAT"));
            while (true) {
                if (contador == index) {
                    try {
                        medida = br.readLine().split(",")[1];
                    } catch (Exception e) {
                        medida = "0";
                    }
                    break;
                }
                br.readLine();
                contador++;
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Problemas al buscar el archivo {0}", e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Problemas al intentar cerrar el BufferedReader {0}", ex);
            }
        }
        LOG.log(Level.INFO, "Lectura de medida satisfactoria");

        double medidad = Double.parseDouble(medida);
        if (medidad < 0) {
            medidad = (medidad * -1);
        }
        int totalDeMedidas = 0;

        for (int i = 1; i <= index; i++) {
            
            System.out.println("Medida " + i + ": " + medidad);
        }

        return medidad;
    }
 */

    /* private static void copiaArchivoDatos(String rutaOrigen, String rutaFinal) {
        LOG.info("Ruta Original : " + rutaOrigen);
        LOG.info("Ruta Copia " + rutaFinal);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            File archivoOriginal = new File(rutaOrigen);
            File archivoCopia = new File(rutaFinal);
            inputStream = new FileInputStream(archivoOriginal);
            outputStream = new FileOutputStream(archivoCopia);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();
            System.out.println("Archivo copiado.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    } */


/* //JFM---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private void registrarPrueba(Integer idPrueba, long idUsuario, int contador) {
        String queryInsertMedidas = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        String queryInsertDefectos = "INSERT INTO defxprueba(id_defecto,id_prueba,Tipo_defecto) VALUES (?,?,'A')";
//        String queryDefectoExistente="SELECT * FROM defxprueba df where df.id_prueba="+idPrueba+"";
        double IntensidadLuzBaja = -1;
        double InclinacionLuzBaja = -1;
        double IntensidadLuzAlta = -1;
        double permisibleBaja = 2.5;
        double permisibleAnguloAlto = 3.5;
        double permisibleAnguloBajo = 0.5;
        boolean DefectoInclinacion = false, DefectoIntensidad = false;

        boolean aprobada = true;

        try {
            if (contador == 1) {
                boolean AltaDerecha = luces.getLuzAltaDerecha();
                boolean BajaDerecha = luces.getLuzBajaDerecha();
                //se registran las medidas
                IntensidadLuzBaja = medidasVehiculos.getIntensidadLuzBajaDerecha();
                InclinacionLuzBaja = medidasVehiculos.getInclinacionLuzBajaDerecha();
                IntensidadLuzAlta = medidasVehiculos.getIntensidadLuzAltaDerecha();
                if (BajaDerecha) {//SI  TIENE FAROLA BAJA DERECHA GUARDA MEDIDAS
                    registrarMedidas(queryInsertMedidas, 2014, IntensidadLuzBaja, idPrueba);
                    System.out.println("registra 2014 solo una farola");
                    registrarMedidas(queryInsertMedidas, 2013, InclinacionLuzBaja, idPrueba);
                    System.out.println("registra 2013 solo una farola");
                    if (IntensidadLuzBaja < permisibleBaja) {//INSERTA DEFECTO DE INTENSIDAD SI ES MENOR AL PERMISIBLE
                        System.out.println("entro al if de intensidad menor a permisible");

                        registrarDefectosxPrueba(queryInsertDefectos, 24005, idPrueba);
                        System.out.println("registro el defecto 24005, medida de la luz " + InclinacionLuzBaja);
                        DefectoIntensidad = true;
                        aprobada = false;
                    }

                    if (InclinacionLuzBaja < permisibleAnguloBajo || InclinacionLuzBaja > permisibleAnguloAlto) {//INSERTA DEFECTO DE ANGULO SI ENO ESTA ENTRE EL PERMISIBLE
                        System.out.println("entro al if de defecto por angulo");

                        registrarDefectosxPrueba(queryInsertDefectos, 24006, idPrueba);
                        System.out.println("Registro defecto 24006, angulo de la luz :" + InclinacionLuzBaja);
                        DefectoInclinacion = true;
                        aprobada = false;
                    }
                }
                if (AltaDerecha) {//GUARDA LA MEDIDA DE LA FAROLA ALTA DERECHA SI EXISTE
                    registrarMedidas(queryInsertMedidas, 2056, IntensidadLuzAlta, idPrueba);
                    System.out.println("registra 2056 solo una farola");
                }

                finalizarPrueba(aprobada, idUsuario, idPrueba, contador);
                // conexion.commit();            
            } else if (contador == 2 || contador == 3) {//SI LA MOTO TIENE DOS O TRES  FAROLAS
                //SE ASIGNAN LOS VALORES DE LAS FAROLAS, LA FAROLA 1 ES LA FAROLA DERECHA, LA FAROLA 2 ES LA IZQUIERA
                System.out.println(" valor de defectoInclinacion:" + DefectoInclinacion);
                System.out.println(" valor de defectoIntensidad:" + DefectoIntensidad);
                double IntensidadLuzBaja2 = -1;
                double InclinacionLuzBaja2 = -1;
                double IntensidadLuzAlta2 = -1;
                boolean AltaDerecha = luces.getLuzAltaDerecha();
                boolean BajaDerecha = luces.getLuzBajaDerecha();
                boolean AltaIzquierda = luces.getLuzAltaIzquierda();
                boolean BajaIzquierda = luces.getLuzBajaIzquierda();

                if (BajaDerecha)//SI LA LUZ BAJA DERECHA EXISTE GUARDA Y EVALUA ANGULO Y INTENSIDAD
                {
                    System.out.println("registra medidas baja derecha");
                    IntensidadLuzBaja = medidasVehiculos.getIntensidadLuzBajaDerecha();
                    InclinacionLuzBaja = medidasVehiculos.getInclinacionLuzBajaDerecha();
                    registrarMedidas(queryInsertMedidas, 2014, IntensidadLuzBaja, idPrueba);
                    registrarMedidas(queryInsertMedidas, 2013, InclinacionLuzBaja, idPrueba);
                    if (DefectoIntensidad == false) {
                        if (IntensidadLuzBaja < permisibleBaja)//SI LA INTENSIDAD DE LA LUZ BAJA DERECHA ES MENOR AL PERMISIBLE GUARDA EL DEFECTO
                        {
                            System.out.println("registra defecto de intensidad farola 1 24005");
                            registrarDefectosxPrueba(queryInsertDefectos, 24005, idPrueba);
                            DefectoIntensidad = true;
                            aprobada = false;
                        }
                    }
                    if (DefectoInclinacion == false) {
                        if (InclinacionLuzBaja < permisibleAnguloBajo || InclinacionLuzBaja > permisibleAnguloAlto)//SI EL ANGULO DE INCLINACION DE LA LUZ BAJA DERECHA ES <0.5 Ó >3.5 GUARDA EL DEFECTO
                        {
                            System.out.println("registra defecto de angulo farola 1 24006");
                            registrarDefectosxPrueba(queryInsertDefectos, 24006, idPrueba);
                            aprobada = false;
                            DefectoInclinacion = true;
                        }
                    }

                }

                if (AltaDerecha)//SI LA LUZ ALTA DERECHA EXISTE GUARDA EL VALOR
                {
                    IntensidadLuzAlta = medidasVehiculos.getIntensidadLuzAltaDerecha();
                    registrarMedidas(queryInsertMedidas, 2056, IntensidadLuzAlta, idPrueba);

                }

                if (BajaIzquierda)//SI LA LUZ BAJA IZQUIERDA EXISTE GUARDA Y EVALUA ANGULO Y INTENSIDAD
                {
                    IntensidadLuzBaja2 = medidasVehiculos.getIntensidadLuzBajaIzquierda();
                    InclinacionLuzBaja2 = medidasVehiculos.getInclinacionLuzBajaIzquierda();
                    registrarMedidas(queryInsertMedidas, 2015, IntensidadLuzBaja2, idPrueba);
                    registrarMedidas(queryInsertMedidas, 2002, InclinacionLuzBaja2, idPrueba);

                    if (DefectoIntensidad == false) {
                        if (IntensidadLuzBaja2 < permisibleBaja)//SI LA INTENSIDAD DE LA LUZ BAJA IZQUIERDA ES MENOR AL PERMISIBLE GUARDA EL DEFECTO
                        {
                            System.out.println("registra defecto de intensidad farola 2 24005");
                            registrarDefectosxPrueba(queryInsertDefectos, 24005, idPrueba);
                            aprobada = false;
                            DefectoIntensidad = true;
                        }
                    }
                    if (DefectoInclinacion == false) {
                        if (InclinacionLuzBaja2 < permisibleAnguloBajo || InclinacionLuzBaja2 > permisibleAnguloAlto)//SI EL ANGULO DE INCLINACION DE LA LUZ BAJA IZQUIERDA ES <0.5 Ó >3.5 GUARDA EL DEFECTO
                        {
                            System.out.println("registra defecto de intensidad farola 2 24006");
                            registrarDefectosxPrueba(queryInsertDefectos, 24006, idPrueba);
                            aprobada = false;
                            DefectoInclinacion = true;
                        }
                    }
                }

                if (AltaIzquierda)//SI LA LUZ ALTA IZQUIERDA EXISTE GUARDA EL VALOR
                {
                    IntensidadLuzAlta2 = medidasVehiculos.getIntensidadLuzAltaIzquierda();
                    registrarMedidas(queryInsertMedidas, 2057, IntensidadLuzAlta2, idPrueba);
                }
                if (contador == 3)//llega a la tercera farola
                {
                    System.out.println(" valor de defectoInclinacion 3:" + DefectoInclinacion);
                    System.out.println(" valor de defectoIntensidad 3:" + DefectoIntensidad);
                    double IntensidadLuzBaja3 = -1;
                    double InclinacionLuzBaja3 = -1;
                    double IntensidadLuzAlta3 = -1;
                    boolean Alta3 = luces.getLuzAlta3();
                    boolean Baja3 = luces.getLuzBaja3();
                    if (Baja3)//SI LA LUZ BAJA DERECHA EXISTE GUARDA Y EVALUA ANGULO Y INTENSIDAD
                    {
                        System.out.println("registra medidas baja 3");
                        IntensidadLuzBaja3 = medidasVehiculos.getIntensidadLuzBaja3();
                        InclinacionLuzBaja3 = medidasVehiculos.getInclinacionLuzBaja3();

                        registrarMedidas(queryInsertMedidas, 2000, IntensidadLuzBaja3, idPrueba);
                        registrarMedidas(queryInsertMedidas, 2022, InclinacionLuzBaja3, idPrueba);
                        if (!DefectoIntensidad) {
                            if (IntensidadLuzBaja3 < permisibleBaja)//SI LA INTENSIDAD DE LA LUZ BAJA DERECHA ES MENOR AL PERMISIBLE GUARDA EL DEFECTO
                            {
                                System.out.println("registra defecto de intensidad farola 3 24005");
                                registrarDefectosxPrueba(queryInsertDefectos, 24005, idPrueba);
                                aprobada = false;
                                DefectoIntensidad = true;
                            }
                        }
                        if (!DefectoInclinacion) {
                            if (InclinacionLuzBaja3 < permisibleAnguloBajo || InclinacionLuzBaja3 > permisibleAnguloAlto)//SI EL ANGULO DE INCLINACION DE LA LUZ BAJA DERECHA ES <0.5 Ó >3.5 GUARDA EL DEFECTO
                            {
                                System.out.println("registra defecto de angulo farola 3 24006");
                                registrarDefectosxPrueba(queryInsertDefectos, 24006, idPrueba);
                                aprobada = false;
                                DefectoInclinacion = true;
                            }
                        }

                    }

                    if (Alta3)//SI LA LUZ ALTA DERECHA EXISTE GUARDA EL VALOR
                    {
                        System.out.println("registra luz alta 3");
                        IntensidadLuzAlta3 = medidasVehiculos.getIntensidadLuzAlta3();
                        registrarMedidas(queryInsertMedidas, 2061, IntensidadLuzAlta3, idPrueba);

                    }

                }
                finalizarPrueba(aprobada, idUsuario, idPrueba, contador);
            }

            //se registra los defectos si los hay
        } catch (Exception e) {
            Mensajes.messageErrorTime("Error al registrar la prueba, Consulte a soporte Soltelec" + e, 3);
            System.out.println("Error al registrar la prueba" + e);
            return;
        }

    }
    //JFM----------------------------------------------------------------------------------------------------------------------------------------------------------

    private void registrarMedidas(String query, int codigo, double valorMedida, Integer idPrueba) {
        System.out.println("---------------------------------------------------");
        System.out.println("-------------  registrarMedidas      --------------");
        System.out.println("---------------------------------------------------");
        try {
            System.out.println("queryInsertMedidas : " + query);
            PreparedStatement instruccion = conexion.prepareStatement(query);
            instruccion.setInt(1, codigo);
            System.out.println("codigo : " + codigo);
            instruccion.setDouble(2, valorMedida);
            System.out.println("valorMedida : " + valorMedida);
            instruccion.setLong(3, idPrueba);
            System.out.println("idPrueba : " + idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
        } catch (SQLException e) {
            Mensajes.messageErrorTime("Error al registrar las medidas, Consulte a soporte Soltelec" + e, 3);
            System.out.println(e.getErrorCode());
            System.out.println("Error en el metodo :registrarMedidas() " + e);
            System.out.println("Error : " + e.getSQLState());
            System.out.println("Error : " + e.getMessage());
            return;
        }

    }


    private void registrarDefectosxPrueba(String query, int codigo, Integer idPrueba) {
        System.out.println("---------------------------------------------------");
        System.out.println("------  registrarDefectosxPrueba      ---- --------");
        System.out.println("---------------------------------------------------");
        try {
            conexion.setAutoCommit(false);
            PreparedStatement instruccion = conexion.prepareStatement(query);
            System.out.println("queryInsertMedidas : " + query);
            instruccion.setInt(1, codigo);
            System.out.println("codigo : " + codigo);
            instruccion.setLong(2, idPrueba);
            System.out.println("idPrueba : " + idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
        } catch (SQLException e) {
            Mensajes.messageErrorTime("Error al registrar el  defecto " + codigo + " , Consulte a soporte Soltelec" + e, 3);
            System.out.println("Error en el metodo :registrarDefectosxPrueba() " + e);
            System.out.println("Error : " + e.getSQLState());
            System.out.println("Error : " + e.getMessage());
            return;
        }
    }


    private String cargarSerialEquipo(int idPrueba) {
        System.out.println("---------------------------------------------------");
        System.out.println("------Cargando Serial Del Equipo De Luces----------");
        System.out.println("---------------------------------------------------");

        String serialEquipo = "";
        try {
            serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
        } catch (Exception e) {
            System.out.println("Error en elo metodo :cargarSerialEquipo() " + e);
            serialEquipo = "Serial no encontrado";
            System.out.println(serialEquipo);
        }
        return serialEquipo;
    }

    private void finalizarPrueba(boolean aprobada, long idUsuario, Integer idPrueba, int contador) {
        String query = "";
        if (this.aplicaModificacion == 0) {
            if (aprobada) {
                query = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='Y',Abortada='N',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
            } else {
                query = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='N',Abortada='N',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
            }
        } else {
            if (aprobada) {
                query = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='Y',Abortada='N',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
            } else {
                query = "UPDATE pruebas SET Autorizada = 'A',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
            }
        }

        registrarResultadoPrueba(query, idUsuario, idPrueba, contador);
    }

    private void registrarResultadoPrueba(String query, long idUsuario, Integer idPrueba, int contador) {
        System.out.println("---------------------------------------------------");
        System.out.println("------    registrarResultadoPrueba      -- --------");
        System.out.println("---------------------------------------------------");
        try {
            PreparedStatement instruccion = conexion.prepareStatement(query);
            instruccion.setLong(1, idUsuario);
            instruccion.setString(2, cargarSerialEquipo(idPrueba));
            instruccion.setLong(3, idPrueba);
            int executeUpdate = instruccion.executeUpdate();
            Mensajes.messageDoneTime("Se ha Registrado los valores de la Farola " + (contador) + " Correctamente", 10);
        } catch (SQLException e) {
            System.out.println("Error en el metodo :registrarResultadoPrueba() " + e);
            System.out.println("Error : " + e.getSQLState());
            System.out.println("Error : " + e.getMessage());
        }
    }
 */
    /**
     *
     */
    /* private void cargarMadidasLuzProperties()
    {
        System.out.println("---------------------------------------------------");
        System.out.println("------    cargarMadidasLuzProperties    -- --------");
        System.out.println("---------------------------------------------------");
        try 
        {
            Luz2014 = Integer.parseInt(UtilPropiedades.cargarPropiedad("Luz2014", "ConfigMedidas.properties"));
            Luz2013 = Integer.parseInt(UtilPropiedades.cargarPropiedad("Luz2013", "ConfigMedidas.properties"));
            Luz2015 = Integer.parseInt(UtilPropiedades.cargarPropiedad("Luz2015", "ConfigMedidas.properties"));
            Luz2022 = Integer.parseInt(UtilPropiedades.cargarPropiedad("Luz2022", "ConfigMedidas.properties"));
        } catch (IOException e) 
        {
            System.out.println("Error"+ e.getMessage());
            System.out.println("Error en el metodo : cargarMadidasLuzProperties()" + e);
        }
    }
     *//* 
    private boolean validarDefectosExistentes(String query) throws ClassNotFoundException {
        System.out.println("---------------------------------------------------");
        System.out.println("------    registrarResultadoPrueba      -- --------");
        System.out.println("---------------------------------------------------");
        try {
            ResultSet rs = DBUtil.executeQuery(query);
            while (rs.next()) {
                return false;
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error en el metodo :validarDefectosExistentes() " + e);
            System.out.println("Error : " + e.toString());
            System.out.println("Error : " + e.getMessage());
        }
        return true;
    }

     */


}
