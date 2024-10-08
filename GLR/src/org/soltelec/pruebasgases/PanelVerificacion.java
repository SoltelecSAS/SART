/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author GerenciaDesarrollo
 */
public class PanelVerificacion extends javax.swing.JPanel implements java.awt.event.ActionListener {

    private final Set<Integer> conjuntoDefectos = new LinkedHashSet<>();
    private StringBuilder mensaje;
    private String mensaje2 = "";
     private StringBuilder detalleRechazo;
    private  Map<Integer, String> mapStrRechazos = new HashMap<Integer, String>(); 
    private boolean defectoEncontrado;
    String str;

    /**
     * Creates new form PanelVerificacionV2
     */
    public PanelVerificacion() {
        initComponents();
       
    }

    public String getMensaje2(){
        return mensaje2;
    }

    public boolean isDefectoEncontrado() {
        return defectoEncontrado;
    }

    public StringBuilder getMensaje() {
        return mensaje;
    }

    public Set<Integer> getConjuntoDefectos() {
        return conjuntoDefectos;
    }
     public StringBuilder getDetallesRechazos() {
         detalleRechazo = new StringBuilder();
         Set<Integer> lst=mapStrRechazos.keySet();         
         for (Integer posDef : lst) {
                detalleRechazo.append(mapStrRechazos.get(posDef));
            }
        return detalleRechazo;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chb84002 = new javax.swing.JCheckBox();
        chb84002.addActionListener(this);
        chb84003 = new javax.swing.JCheckBox();
        chb84003.addActionListener(this);
        chb84001 = new javax.swing.JCheckBox();
        chb84001.addActionListener(this);
        chb84000 = new javax.swing.JCheckBox();
        chb84000.addActionListener(this);
        chb84009 = new javax.swing.JCheckBox();
        chb84009.addActionListener(this);
        chb84010 = new javax.swing.JCheckBox();
        chb84010.addActionListener(this);
        chb84011 = new javax.swing.JCheckBox();
        chb84011.addActionListener(this);
        chb84012 = new javax.swing.JCheckBox();
        chb84012.addActionListener(this);
        jLabel1 = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(600, 500));
        setPreferredSize(new java.awt.Dimension(600, 500));

        chb84002.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chb84002.setText("<html>Existencia de fugas en el tubo, uniones del múltiple y silenciador del sistema de escape del vehículo</html>");

        chb84003.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chb84003.setText("<html>Salidas adicionales en el sistema de escape diferentes a las de diseño original del vehículo</html>");

        chb84001.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chb84001.setText("<html>Ausencia de tapones de aceite o fugas en el mismo</html>");

        chb84000.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chb84000.setText("<html>Ausencia de tapas o tapones de combustible o fugas en el mismo</html>");

        chb84009.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chb84009.setText("<html>Sistema de admisión de aire en mal estado (filtro roto, o deformado) o ausencia del filtro de aire</html>");

        chb84010.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chb84010.setText("<html>Desconexión de sistemas de recirculación de gases provenientes del Carter del motor. <br>(Por ejemplo válvula de ventilación positiva del Carter)</html>");

        chb84011.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chb84011.setText("<html>Instalación de accesorios o deformaciones en el tubo de escape que no permitan la introducción de la sonda</html>");

        chb84012.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        chb84012.setText("<html>Incorrecta operación del sistema de refrigeración, cuya verificación se hará por medio de inspección</html>");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 0, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("VERIFICACION DE CONDICIONES ANORMALES");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 105, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(0, 104, Short.MAX_VALUE))
                    .addComponent(chb84003)
                    .addComponent(chb84001)
                    .addComponent(chb84002, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(chb84000)
                    .addComponent(chb84009, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(chb84011, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(chb84010)
                    .addComponent(chb84012, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(chb84002, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(chb84003, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(chb84001, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(chb84000, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(chb84009, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(chb84010, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(chb84011, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(chb84012, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(33, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chb84000;
    private javax.swing.JCheckBox chb84001;
    private javax.swing.JCheckBox chb84002;
    private javax.swing.JCheckBox chb84003;
    private javax.swing.JCheckBox chb84009;
    private javax.swing.JCheckBox chb84010;
    private javax.swing.JCheckBox chb84011;
    private javax.swing.JCheckBox chb84012;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) 
    {        
        defectoEncontrado = ( chb84000.isSelected() || chb84001.isSelected() || chb84002.isSelected() 
                              || chb84003.isSelected() || chb84009.isSelected() || chb84010.isSelected()
                              || chb84011.isSelected() || chb84012.isSelected());
       
        if (defectoEncontrado) 
        {
            mensaje = new StringBuilder("<html>Prueba Rechazada por:</html>");
//            conjuntoDefectos.add(80000);
            if (chb84000.isSelected()) {
                mensaje.append("<html>Ausencia de tapas o tapones de combustible o fugas en el mismo</html>");
               // str=" 40103074 ".concat(chb84000.getText().replaceFirst("<html>", ""));
                str=" 3.1.1.1.4 ".concat(chb84000.getText().replaceFirst("<html>", ""));
                System.out.println("Lo que voy a rechazar: "+ str);
                mapStrRechazos.put(84000,str.replaceAll("</html>", "; "));   
                mensaje2 += str.replaceAll("</html>", "; ");
                conjuntoDefectos.add(84000);
            } else {
                conjuntoDefectos.remove(84000);
                mapStrRechazos.remove(84000);                
            }

            if (chb84001.isSelected()) {
                mensaje.append(chb84001.getText());
               // str=" 40103073 ".concat(chb84001.getText().replaceFirst("<html>", ""));
                 str=" 3.1.1.1.3 ".concat(chb84001.getText().replaceFirst("<html>", ""));
                 System.out.println("Lo que voy a rechazar: "+ str);
                mapStrRechazos.put(84001,str.replaceAll("</html>", "; "));    
                mensaje2 += str.replaceAll("</html>", "; ");           
                conjuntoDefectos.add(84001);
            } else {
                conjuntoDefectos.remove(84001);
                mapStrRechazos.remove(84001) ;
            }

            if (chb84002.isSelected()) {
                mensaje.append(chb84002.getText());
              //  str=" 40103071 ".concat(chb84002.getText().replaceFirst("<html>", ""));
                str="3.1.1.1.1 ".concat(chb84002.getText().replaceFirst("<html>", ""));
                System.out.println("Lo que voy a rechazar: "+ str);
                mapStrRechazos.put(84002,str.replaceAll("</html>", "; "));     
                mensaje2 += str.replaceAll("</html>", "; ");           
                conjuntoDefectos.add(84002);
            } else {
                conjuntoDefectos.remove(84002);
                 mapStrRechazos.remove(84002) ;
            }

            if (chb84003.isSelected()) {
                mensaje.append(chb84003.getText());
//                str=" 40103072 ".concat(chb84003.getText().replaceFirst("<html>", ""));
                 str=" 3.1.1.1.2 ".concat(chb84003.getText().replaceFirst("<html>", ""));
                 System.out.println("Lo que voy a rechazar: "+ str);
                mapStrRechazos.put(84003,str.replaceAll("</html>", "; "));
                mensaje2 += str.replaceAll("</html>", "; ");
                conjuntoDefectos.add(84003);
            } else {
                conjuntoDefectos.remove(84003);
                 mapStrRechazos.remove(84003) ;
            }

            if (chb84009.isSelected()) {
                mensaje.append(chb84009.getText());
               // str=" 40103075 ".concat(chb84009.getText().replaceFirst("<html>", ""));
                 str=" 3.1.1.1.5  ".concat(chb84009.getText().replaceFirst("<html>", ""));
                 System.out.println("Lo que voy a rechazar: "+ str);
                mapStrRechazos.put(84009,str.replaceAll("</html>", "; "));
                mensaje2 += str.replaceAll("</html>", "; ");
                conjuntoDefectos.add(84009);
            } else {
                conjuntoDefectos.remove(84009);
                 mapStrRechazos.remove(84000) ;
            }

            if (chb84010.isSelected()) {
                mensaje.append(chb84010.getText());
                //str=" 40103076 ".concat(chb84010.getText().replaceFirst("<html>", ""));
                str=" 3.1.1.1.6  ".concat(chb84010.getText().replaceFirst("<html>", ""));
                System.out.println("Lo que voy a rechazar: "+ str);
                mapStrRechazos.put(84010,str.replaceAll("</html>", "; "));
                mensaje2 += str.replaceAll("</html>", "; ");
                conjuntoDefectos.add(84010);
            } else {
                conjuntoDefectos.remove(84010);
                 mapStrRechazos.remove(84010) ;
            }

            if (chb84011.isSelected()) {
                mensaje.append(chb84011.getText());
                //str=" 40103077 ".concat(chb84011.getText().replaceFirst("<html>", ""));
                  str=" 3.1.1.1.7  ".concat(chb84011.getText().replaceFirst("<html>", ""));
                  System.out.println("Lo que voy a rechazar: "+ str);
                mapStrRechazos.put(84011,str.replaceAll("</html>", "; "));
                mensaje2 += str.replaceAll("</html>", "; ");
                conjuntoDefectos.add(84011);
            } else {
                conjuntoDefectos.remove(84011);
                 mapStrRechazos.remove(84011) ;
            }

            if (chb84012.isSelected()) {
                mensaje.append(chb84012.getText());
                //str=" 40103078 ".concat(chb84012.getText().replaceFirst("<html>", ""));
                str=" 3.1.1.1.8 ".concat(chb84012.getText().replaceFirst("<html>", ""));
                System.out.println("Lo que voy a rechazar: "+ str);
                mapStrRechazos.put(84012,str.replaceAll("</html>", "; "));
                mensaje2 += str.replaceAll("</html>", "; ");
                conjuntoDefectos.add(84012);
            } else {
                conjuntoDefectos.remove(84012);
                mapStrRechazos.remove(84012) ;
            }

            for (Integer i : conjuntoDefectos) {
                System.out.println(i);
            }
//            AddDefGases();
            System.out.println("------------");
        }
    }

    public static void main(String args[])
    {
        JFrame app = new JFrame();
        PanelVerificacion panelC = new PanelVerificacion();
        JOptionPane.showMessageDialog(null, panelC, "Inspeccion Sensorial", JOptionPane.PLAIN_MESSAGE);
    }

    //Agrega el defecto 80000 si alguno de los siguientes defectos es seleccionado - "Popayan"
    private void AddDefGases()
    {
        if (chb84000.isSelected() || chb84001.isSelected() || chb84002.isSelected() || chb84003.isSelected() 
            || chb84009.isSelected() || chb84010.isSelected() || chb84011.isSelected() || chb84012.isSelected()) 
        {
            conjuntoDefectos.add(80000);
            System.out.println("Defecto 80000 agregado");
        } else {
            conjuntoDefectos.remove(80000);
            System.out.println("No se selecciono ningun defecto");
        }
    }
}
