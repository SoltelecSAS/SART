/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosbanco;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Fabian
 */
public class SerialLogic {


     public static class SerialReader implements SerialPortEventListener
    {
        private JTextArea paraEscribir;
        private InputStream in;
        private byte[] buffer = new byte[1024];

        public SerialReader ( InputStream in, JTextArea paramJTextArea )
        {
            this.in = in;
            this.paraEscribir = paramJTextArea;
        }

        public void serialEvent(SerialPortEvent arg0) {
            int data;

            try
            {
                int len = 0;
                while ( ( data = in.read()) > -1 )
                {
                    if ( data == '\n' ) {
                        break;
                    }
                    buffer[len++] = (byte) data;
                }
                System.out.println("La longitud de lo leído es:" + len);
                paraEscribir.append(new String(buffer,0,len));
            }
            catch ( IOException e )
            {
                e.printStackTrace();
                System.exit(-1);
            }
        }

    }



    /** */
    public static class SerialWriter implements Runnable,ActionListener
    {
        OutputStream out;
        JTextField textField;
        public SerialWriter ( OutputStream out, JTextField atextField )
        {
            this.out = out;
            this.textField = atextField;
        }

        public void run ()
        {
            try
            {
                int c = 0;
                while ( ( c = System.in.read()) > -1 )
                {
                    this.out.write(c);
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }

        public void actionPerformed(ActionEvent e) {
            try {
                String paraEscribir = textField.getText();
                byte[] b = paraEscribir.getBytes(Charset.forName("US-ASCII"));
                this.out.write(b);
            } catch (IOException ex) {
                System.out.println("Exception escribiendo al puerto");
            }
        }


    }




}
