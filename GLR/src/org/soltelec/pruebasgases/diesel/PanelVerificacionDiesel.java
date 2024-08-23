/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases.diesel;

import java.util.Set;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashSet;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
//import sun.font.Font2D;
import static java.awt.GridBagConstraints.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase para checkear que el sistema de escape funcione correctamente Esta
 * clase tiene una implementacion regular. Cuando se da click en el boton se
 * mira que checkboxes fueron seleccionados dependiendo de cuales fueron
 * seleccionados se añade o remueve el numero del defecto a un conjunto de
 * defectos (al ser un conjunto facilita que solamente se registre una vez inde
 * pendientemente de cuantas veces se de click) finalmente se itera sobre el
 * conjunto registrando los defectos en la base de datos
 *
 * @author GerenciaDesarrollo
 */
public class PanelVerificacionDiesel extends JPanel implements ActionListener {

    private boolean defectoEncontrado;
    private boolean defecto1, defecto2, defecto3, defecto4, defecto5;
    //Para
    private boolean defecto6, defecto7, defecto9, defecto10;
    private JCheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5;
    private JCheckBox checkBox6, checkBox7, checkBox8, checkBox9, checkBox10;
    private StringBuilder mensaje;
    private StringBuilder msgRechazo;
    private JLabel lbl1, lbl2, lbl3, lbl4, lbl5, lbl6, lbl7, lbl8, lbl9;
    private JLabel lblTitulo;
     private Map<Integer, String> mapStrRechazos = new HashMap<Integer, String>();
      String str;
//    private JLabel lbl10;

    private Set<Integer> conjuntoDefectos = new HashSet<Integer>();//conjunto de defectos

    public PanelVerificacionDiesel() {
        inicializarInterfaz();
    }

    private void inicializarInterfaz() {
        Font f = new Font(Font.SERIF, Font.PLAIN, 24);
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        this.setLayout(bag);
        c.insets = new Insets(5, 10, 5, 10);//en contra del reloj
        //toda de la pantalla modo horizontal
        c.weightx = 1.0;
        //un titulo, ocupar toda la fila, centrarse
        c.gridwidth = REMAINDER;
        c.anchor = CENTER;
        //añadir el titulo, fila 0 columna 0
        c.gridx = 0;
        c.gridy = 0;
        c.fill = NONE;//para que se pueda centrar
        lblTitulo = new JLabel(" MARQUE SI SE PRESENTA  ");
        this.add(lblTitulo, c);
        c.gridwidth = 1;
        //noventa por ciento de la pantalla para las etiquetas 5 para los checkbox
        c.weightx = 0.95;
        //Primer mensaje en forma de JLabel
        c.gridx = 0;
        c.gridy = 1;//fila1 columna 0 centro
        c.fill = NONE;
        c.anchor = LINE_START;
        //cinco porciento del tamaño
        c.weightx = 0.05;
        lbl1 = new JLabel("<HTML>Existencia de fugas en el tubo,uniones del multiple y silenciador<BR/> del sistema de escape del vehiculo</HTML>");
        this.add(lbl1, c);
        c.gridx = 1;
        c.gridy = 1;//poner el checkBox1
        //c.fill = HORIZONTAL;
        checkBox1 = new JCheckBox();
        checkBox1.addActionListener(this);
        this.add(checkBox1, c);
        //Segundo mensaje en forma de JLabel
        //95% del tamaño
        c.weightx = 0.95;
        c.gridx = 0;
        c.gridy = 2;
        c.fill = NONE;
        lbl2 = new JLabel("<HTML>Salidas adicionales en el sistema de escape diferentes<BR/> a las del diseño original del vehiculo</HTML>");
        this.add(lbl2, c);
        c.gridx = 1;
        c.gridy = 2;
        //c.fill = HORIZONTAL;//ponerl el checkBox2
        c.weightx = 0.05;
        checkBox2 = new JCheckBox();
        checkBox2.addActionListener(this);
        this.add(checkBox2, c);
        //poner El tercer mensaje en forma de JLabel
        c.gridx = 0;
        c.gridy = 3;
        c.fill = NONE;
        c.weightx = 0.95;
        lbl3 = new JLabel("Ausencia de tapones de aceite o fugas en el mismo");
        this.add(lbl3, c);
        c.gridx = 1;
        c.gridy = 3;
        //c.fill = HORIZONTAL;
        c.weightx = 0.05;
        checkBox3 = new JCheckBox();
        checkBox3.addActionListener(this);
        this.add(checkBox3, c);
        //Poner el cuarto mensaje
        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 0.95;
        lbl4 = new JLabel("Ausencia de tapones de combustible o fugas en el mismo");
        this.add(lbl4, c);
        c.gridx = 1;
        c.gridy = 4;
        c.weightx = 0.05;
        checkBox4 = new JCheckBox();
        checkBox4.addActionListener(this);
        this.add(checkBox4, c);
        //Poner el quinto mensaje
        c.gridx = 0;
        c.gridy = 5;
        c.weightx = 0.95;
        lbl5 = new JLabel("<HTML>Instalacion de accesorios o deformaciones en el tubo<BR/> de escape que no permital la introduccion del acople</HTML>");
        this.add(lbl5, c);
        //Poner el quinto checkBox
        c.gridx = 1;
        c.gridy = 5;
        c.weightx = 0.05;
        checkBox5 = new JCheckBox();
        checkBox5.addActionListener(this);
        this.add(checkBox5, c);
        //Poner el sexto mensaje
        c.gridx = 0;
        c.gridy = 6;
        c.weightx = 0.95;
        lbl6 = new JLabel("Incorrecta operacion del sistema de refrigeracion cuya Verificacion se hara por medio de Inspeccion");
        lbl6.setVisible(true);
        this.add(lbl6, c);
        //Poner el quinto checkBox
        c.gridx = 1;
        c.gridy = 6;
        c.weightx = 0.05;
        checkBox6 = new JCheckBox();
        checkBox6.addActionListener(this);
        checkBox6.setVisible(true);
        this.add(checkBox6, c);
        //Poner el septimo mensaje
        c.gridx = 0;
        c.gridy = 7;
        c.weightx = 0.95;
        lbl7 = new JLabel("Ausencia o incorrecta instalacion del filtro de aire");
        lbl7.setVisible(true);
        this.add(lbl7, c);
        //Poner el septimo checkBox
        c.gridx = 1;
        c.gridy = 7;
        c.weightx = 0.05;
        checkBox7 = new JCheckBox();
        checkBox7.addActionListener(this);
        checkBox7.setVisible(true);
        this.add(checkBox7, c);

        c.gridx = 0;
        c.gridy = 8;
        c.weightx = 0.95;
        lbl9 = new JLabel("<HTML>Activacion de dispositivos instalados en el motor o en el <BR/>"
                + "vehiculo que alteren las caracteristicas normales de velocidad de giro<BR/> "
                + " y modifiquen la prueba de opacidad y su correcta ejecucion </HTML>");
        lbl9.setVisible(true);
        this.add(lbl9, c);
        checkBox9 = new JCheckBox();
        checkBox9.addActionListener(this);
        checkBox9.setVisible(true);
        c.gridx = 1;
        c.gridy = 8;
        this.add(checkBox9, c);

        c.gridx = 0;
        c.gridy = 10;
        c.weightx = 0.95;
//        lbl10 = new JLabel("Incumplimiento del decreto 948/97,1552/00");
//        lbl10.setVisible(true);
//        this.add(lbl10, c);
        c.fill = BOTH;
        msgRechazo = new  StringBuilder();        
//        JLabel[] etiquetas = {lbl1, lbl2, lbl3, lbl4, lbl5, lbl6, lbl7, lbl9, lbl10, lblTitulo};
        JLabel[] etiquetas = {lbl1, lbl2, lbl3, lbl4, lbl5, lbl6, lbl7, lbl9, lblTitulo};
        for (JLabel lbl : etiquetas) {
            lbl.setFont(f);
        }
    }

    public void ocultarDefectosVisual() {
        lblTitulo.setText("SISTEMA DE ACELERACION");
        checkBox1.setVisible(false);
        checkBox2.setVisible(false);
        checkBox3.setVisible(false);
        checkBox4.setVisible(false);
        checkBox5.setVisible(false);
        checkBox6.setVisible(true);
        checkBox7.setVisible(true);
        checkBox8.setVisible(true);
        checkBox9.setVisible(false);
        //checkBox10.setVisible(false);
        lbl1.setVisible(false);
        lbl2.setVisible(false);
        lbl3.setVisible(false);
        lbl4.setVisible(false);
        lbl5.setVisible(false);
        lbl6.setVisible(true);
        lbl7.setVisible(true);
        lbl8.setVisible(true);
        lbl9.setVisible(false);
//        lbl10.setVisible(false);
        defectoEncontrado = false;
        checkBox1.setSelected(false);
        checkBox2.setSelected(false);
        checkBox3.setSelected(false);
        checkBox4.setSelected(false);
        checkBox5.setSelected(false);
    }//end of method ocultarDefectosVisual

    public boolean isDefectoEncontrado() {
        return defectoEncontrado;
    }

    public StringBuilder getMensaje() {
        return mensaje;
    }
     public String getMsgRechazo() {        
        String detalleRechazos = new String();
        Set<Integer> lst = mapStrRechazos.keySet();
        for (Integer posDef : lst) {
            detalleRechazos = detalleRechazos.concat(mapStrRechazos.get(posDef));
        }
        System.out.println(detalleRechazos);
        return detalleRechazos;     
    }

    public static void main(String args[]) {
        JFrame app = new JFrame();
        PanelVerificacionDiesel panelC = new PanelVerificacionDiesel();
        app.add(panelC);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setVisible(true);
        app.setSize(820, 650);
        //panelC.iniciarProceso();
    }

    public void actionPerformed(ActionEvent e) {
        defecto1 = checkBox1.isSelected();
        defecto2 = checkBox2.isSelected();
        defecto3 = checkBox3.isSelected();
        defecto4 = checkBox4.isSelected();
        defecto5 = checkBox5.isSelected();
        defecto6 = checkBox6.isSelected();
        defecto7 = checkBox7.isSelected();
        //defecto8 = checkBox8.isSelected();
        defecto9 = checkBox9.isSelected();    
         Boolean douDefc = false;

        //esta implementacion es ineficiente
        //deberia con cada click del checkbox poner o quitar del conjunto de defectos
        defectoEncontrado = defecto1 || defecto2 || defecto3 || defecto4 || defecto5 || defecto6
                || defecto7 || defecto9 || defecto10;

        if (defectoEncontrado) {
            mensaje = new StringBuilder(" PRUEBA RECHAZADA:");//                 
            System.out.println("***Asignacion de Defecto***");
        }
        Set<Integer> ev = mapStrRechazos.keySet();
        if (defecto1) {
            mensaje.append("Fugas en el tubo, uniones del multiple y silencioador del sistema de escape del vehiculo");
            conjuntoDefectos.add(84027);        
            for (Integer i : ev) {
                if (i == 84027) {
                    douDefc = true;
                }
            }
            if (douDefc == false) {
              //  mapStrRechazos.put(84027,"30103081  Fugas en el tubo, uniones del multiple y silencioador del sistema de escape del vehiculo");
                mapStrRechazos.put(84027,"2.1.1.1.1  Fugas en el tubo, uniones del multiple y silencioador del sistema de escape del vehiculo");
            }            
        } else {
            conjuntoDefectos.remove(84027);
            mapStrRechazos.remove(84027);
        }        
        if (defecto2) {
            douDefc = false;
            mensaje.append("Salidas adicionales en el sistema de escape diferentes a las del diseño original del vehiculo\n");
            conjuntoDefectos.add(84030);            
            for (Integer i : ev) {
                if (i == 84030) {
                    douDefc = true;
                }
            }
            if (douDefc == false) {                
                //mapStrRechazos.put(84030,"30103082 Salidas adicionales en el sistema de escape diferentes a las del diseño original del vehiculo ");
                mapStrRechazos.put(84030,"2.1.1.1.2 Salidas adicionales en el sistema de escape diferentes a las del diseño original del vehiculo ");
            }            
        } else {
            conjuntoDefectos.remove(84030);
             mapStrRechazos.remove(84030); 
        }
        
        if (defecto3) {
            mensaje.append("\nAusencia de tapones de aceite o fugas en el mismo");
            douDefc = false;
            conjuntoDefectos.add(84031);
            for (Integer i : ev) {
                if (i == 84031) {
                    douDefc = true;
                }
            }
            if (douDefc == false) {                
                //mapStrRechazos.put(84031,"30103083 Ausencia de tapones de aceite o fugas en el mismo");
                mapStrRechazos.put(84031,"2.1.1.1.3 Ausencia de tapones de aceite o fugas en el mismo");
                
            }
        } else {
            conjuntoDefectos.remove(84031);
            mapStrRechazos.remove(84031); 
        }
        if (defecto4) {
            mensaje.append("\nAusencia de tapones de combustible o fugas en el mismo");
            douDefc = false;
            conjuntoDefectos.add(84032);
            for (Integer i : ev) {
                if (i == 84032) {
                    douDefc = true;
                }
            }
            if (douDefc == false) {                
               // mapStrRechazos.put(84032,"30103084 Ausencia de tapones de combustible o fugas en el mismo");
                mapStrRechazos.put(84032,"2.1.1.1.4 Ausencia de tapones de combustible o fugas en el mismo");
            }            
        } else {
            conjuntoDefectos.remove(84032);
            mapStrRechazos.remove(84032); 
        }
        if (defecto5) {
            mensaje.append("\n Instalacion de accesorios o deformaciones en el tubo de escape que no permitan la introduccion del acople");
            conjuntoDefectos.add(84020);            
            douDefc = false;
            for (Integer i : ev) {
                if (i == 84020) {
                    douDefc = true;
                }
            }
            if (douDefc == false) {                
                //mapStrRechazos.put(84020,"30103085 Instalacion de accesorios o deformaciones en el tubo de escape que no permitan la introduccion del acople");
                  mapStrRechazos.put(84020,"2.1.1.1.5 Instalacion de accesorios o deformaciones en el tubo de escape que no permitan la introduccion del acople");            }            
        } else {
            conjuntoDefectos.remove(84020);
            mapStrRechazos.remove(84020); 
        }
        if (defecto6) {
            douDefc = false;
            mensaje.append("\nIncorrecta operacion del sistema de refrigeracion");
            conjuntoDefectos.add(84021);
            for (Integer i : ev) {
                if (i == 84021) {
                    douDefc = true;
                }
            }
            if (douDefc == false) {                
               // mapStrRechazos.put(84021,"30103086 Incorrecta operacion del sistema de refrigeracion");
                mapStrRechazos.put(84021,"2.1.1.1.6 Incorrecta operacion del sistema de refrigeracion");
            }            
        } else {
            conjuntoDefectos.remove(84021);
            mapStrRechazos.remove(84021); 
        }
        if (defecto7) {
            mensaje.append("\nAusencia o incorrecta instalacion del filtro de aire");
            conjuntoDefectos.add(84022);
            for (Integer i : ev) {
                if (i == 84022) {
                    douDefc = true;
                }
            }
            if (douDefc == false) {               
              //  mapStrRechazos.put(84022,"30103087 Ausencia o incorrecta instalacion del filtro de aire");
                mapStrRechazos.put(84022,"2.1.1.1.7 Ausencia o incorrecta instalacion del filtro de aire");
            }            
        } else {
            conjuntoDefectos.remove(84022);
            mapStrRechazos.remove(84022);
        } 
        if (defecto9) {
            douDefc = false;
            mensaje.append("\nActivacion de dispositivos instalados en el motor o en le vehiculo"
                    + "\n que alteren las caracteristicas normales de velocidad de giro"
                    + "\n y que tengan como efecto la modificacion de los resultados de la"
                    + "\nprueba de opacidad o que impidan su ejecucion adecuada");
            conjuntoDefectos.add(84023);            
            for (Integer i : ev) {
                if (i == 84023) {
                    douDefc = true;
                }
            }
            if (douDefc == false) {                
               // mapStrRechazos.put(84023,"30103088 Activacion de dispositivos instalados en el motor o en le vehiculo que alteren las caracteristicas normales de velocidad de giro"
                 //   + "y que tengan como efecto la modificacion de los resultados de la prueba de opacidad o que impidan su ejecucion adecuada");
                  mapStrRechazos.put(84023,"2.1.1.1.8 Activacion de dispositivos instalados en el motor o en le vehiculo que alteren las caracteristicas normales de velocidad de giro"
                    + "y que tengan como efecto la modificacion de los resultados de la prueba de opacidad o que impidan su ejecucion adecuada");
            }
        } else {
            conjuntoDefectos.remove(84023);
            mapStrRechazos.remove(84023);
        }       

        System.out.println(mensaje.toString());
        for (Integer def : conjuntoDefectos) {
            System.out.println(def);
        }
        System.out.println("-------------*");
    }//end of method actionPerformed

    public JCheckBox getCheckBox7() {
        return checkBox7;
    }

    public JCheckBox getCheckBox8() {
        return checkBox8;
    }

    public boolean isDefecto1() {
        return defecto1;
    }

//    public JLabel getLbl10() {
//        return lbl10;
//    }
    public JLabel getLbl7() {
        return lbl7;
    }

    public JLabel getLbl8() {
        return lbl8;
    }

    public boolean isDefecto10() {
        return defecto10;
    }

    public JCheckBox getCheckBox10() {
        return checkBox10;
    }

    public JCheckBox getCheckBox4() {
        return checkBox4;
    }

    public JCheckBox getCheckBox6() {
        return checkBox6;
    }

    public JCheckBox getCheckBox9() {
        return checkBox9;
    }

    public JLabel getLbl4() {
        return lbl4;
    }

    public JLabel getLbl6() {
        return lbl6;
    }

    public JLabel getLbl9() {
        return lbl9;
    }

    public Set<Integer> getConjuntoDefectos() {
        return conjuntoDefectos;
    }

    public JCheckBox getCheckBox1() {
        return checkBox1;
    }

    public void setCheckBox1(JCheckBox checkBox1) {
        this.checkBox1 = checkBox1;
    }

    public JCheckBox getCheckBox3() {
        return checkBox3;
    }

    public void setCheckBox3(JCheckBox checkBox3) {
        this.checkBox3 = checkBox3;
    }

    public JCheckBox getCheckBox5() {
        return checkBox5;
    }

    public void setCheckBox5(JCheckBox checkBox5) {
        this.checkBox5 = checkBox5;
    }

    public JLabel getLbl1() {
        return lbl1;
    }

    public void setLbl1(JLabel lbl1) {
        this.lbl1 = lbl1;
    }

    public JLabel getLbl3() {
        return lbl3;
    }

    public void setLbl3(JLabel lbl3) {
        this.lbl3 = lbl3;
    }

    public JLabel getLbl5() {
        return lbl5;
    }

    public void setLbl5(JLabel lbl5) {
        this.lbl5 = lbl5;
    }

}//end of class PanelVerificacionDiesel

