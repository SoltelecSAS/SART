package org.soltelec.login;

import org.soltelec.principal.PanelBotonesPrincipal;
import java.util.Calendar;
import java.sql.SQLException;
import javax.swing.JOptionPane;
//import org.soltelec.entidades.Usuarios;
import javax.swing.SwingUtilities;
import javax.swing.JDialog;
import java.util.Arrays;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
//import javax.persistence.NoResultException;
//import javax.persistence.PersistenceException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
//import org.eclipse.persistence.exceptions.DatabaseException;
//import org.soltelec.controladores.UsuariosJpaController;
import static java.awt.GridBagConstraints.*;

public class DialogoUsuario extends JPanel implements ActionListener{
    JDialog dlg;
    private JPanel panel;
    private JPasswordField pass;
    private JTextField campoUsuario;
    public JButton botonOK;
    private JButton botonCancelar;
    private static String OK = "ok";
    private boolean inicio = true;
    private boolean loginCorrecto = false;
    //private Usuarios usuario;
    public DialogoUsuario(){
        
        GridBagLayout bag = new GridBagLayout();
        this.setLayout(bag);
        GridBagConstraints c = new GridBagConstraints();
        //Primera Fila para una bonita imagen
        //llenar horizontalmente toda la ventana
        c.weightx = 1.0;
        //los insets
       c.insets = new Insets(10,5,10,5);
       //poner un titulo
       filaColumna(1,0,c);
       JLabel titulo = new JLabel("Ingrese Usuario y Contraseña");
       ocuparHorizontalmente(c,true);
       c.anchor = CENTER;
       this.add(titulo,c);
       c.fill = HORIZONTAL;
       filaColumna(2,0,c);
       ocuparHorizontalmente(c,false);
       c.weightx = 0.25;
       JLabel etiquetaUsuario = new JLabel("Usuario: ");
       this.add(etiquetaUsuario,c);
       c.weightx = 0.75;
       filaColumna(2,1,c);
       campoUsuario = new JTextField();
       this.add(campoUsuario,c);
       c.weightx = 0.25;
       filaColumna(3,0,c);
       JLabel etiquetaContraseña = new JLabel("Contraseña: ");
       this.add(etiquetaContraseña,c);
       filaColumna(3,1,c);
       c.weightx = 0.75;
       pass = new JPasswordField();
       pass.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                botonOK.doClick();
            }
        });
       this.add(pass,c);
       c.weightx = 0;
       filaColumna(4,1,c);
       ocuparHorizontalmente(c,false);
       c.fill = NONE;
       c.anchor = LINE_END;
       botonCancelar = new JButton("Cancelar");
       botonCancelar.addActionListener(this);
       botonOK = new JButton("OK");
       botonOK.addActionListener(this);
       JPanel panelEnvoltor = new JPanel();
       panelEnvoltor.add(botonCancelar);
       panelEnvoltor.add(botonOK);
       this.add(panelEnvoltor,c);       
    }//end of constructor

    private void filaColumna(int fila, int columna, GridBagConstraints co){
        co.gridx = columna;
        co.gridy = fila;
    }

    private void ocuparHorizontalmente(GridBagConstraints co,boolean todoNada){
        if(todoNada)
        co.gridwidth = REMAINDER;
        else
         co.gridwidth = 1;
    }//end of method

    public JPanel getPanel() {
        return panel;
    }

    public void setPanel(JPanel panel) {
        this.panel = panel;
    }




    
    public static void main(String args[]){
            JFrame app  = new JFrame();
            DialogoUsuario du= new DialogoUsuario();
            app.add(du.getPanel());
            app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            app.setVisible(true);
            app.setSize(250,250);

        }

    private static boolean isPasswordCorrect(char[] input,String contraseña) {
        boolean isCorrect = true;
        char[] correctPassword = contraseña.toCharArray();

        if (input.length != correctPassword.length) {
            isCorrect = false;
        } else {
            isCorrect = Arrays.equals (input, correctPassword);
        }

        //Zero out the password.
        Arrays.fill(correctPassword,'0');
        return isCorrect;
    }   

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == botonOK){
        //obtener la entrada del usuario
        String nombreUsuario = campoUsuario.getText();
        char[] password = pass.getPassword();
        //Usuarios u = null;
        String strContraseña = new String(password);
        String contraseña = "STLC";
        Calendar c = Calendar.getInstance();
        String diaMes = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        String nuevaContraseña = "STLC"+diaMes;
        if(nombreUsuario.equals("ADMIN")){
            if(strContraseña.equals(nuevaContraseña)){
                SwingUtilities.invokeLater( new Runnable(){
            public void run(){
                PanelBotonesPrincipal panel = new PanelBotonesPrincipal();
                JFrame app = new JFrame();
                app.getContentPane().add(panel);
                app.setExtendedState(JFrame.MAXIMIZED_BOTH);
                app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                app.setVisible(true);
            }
        });
            }
        else {
             JOptionPane.showMessageDialog(null,"Usuario o contraseña incorrectos");
            }
        } else {
            JOptionPane.showMessageDialog(null,"Usuario o contraseña incorrectos");
            }
   }
 else if(e.getSource()== botonCancelar){
     System.exit(0);
    }
  }//end of actionPerformed
protected void resetFocus() {
        pass.requestFocusInWindow();
    }

public JButton getBotonOK() {
        return botonOK;
}

    public JButton getBotonCancelar() {
        return botonCancelar;
    }

    public JTextField getCampoUsuario() {
        return campoUsuario;
    }

    public JPasswordField getPass() {
        return pass;
    }

    public boolean isLoginCorrecto() {
        return loginCorrecto;
    }
    public void setLoginCorrecto(boolean loginCorrecto) {
        this.loginCorrecto = loginCorrecto;
    }
}//end of class