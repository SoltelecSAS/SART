/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosbanco;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.imageio.ImageIO;

/**
 *
 * @author Usuario
 */
public class PanelTextura extends JPanel{
    Image imagen = null;

    PanelTextura(){
        super();
        try {
            imagen = ImageIO.read(this.getClass().getResourceAsStream("/imagenes/fondo.jpg"));
        } catch (IOException ex) {
            Logger.getLogger(PanelTextura.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void paintComponent(Graphics g){
            TexturePaint texturePaint = new TexturePaint(toBufferedImage(imagen), new Rectangle( this.getWidth(),this.getHeight()));
            Rectangle rect = new Rectangle(this.getWidth(),this.getHeight());
            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(texturePaint);
            g2.fill(rect);
    }//end of paintComponent


    // This method returns a buffered image with the contents of an image
public static BufferedImage toBufferedImage(Image image) {
    if (image instanceof BufferedImage) {
        return (BufferedImage)image;
    }

    // This code ensures that all the pixels in the image are loaded
    image = new ImageIcon(image).getImage();

    // Determine if the image has transparent pixels; for this method's
    // implementation, see Determining If an Image Has Transparent Pixels
    boolean hasAlpha = hasAlpha(image);

    // Create a buffered image with a format that's compatible with the screen
    BufferedImage bimage = null;
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    try {
        // Determine the type of transparency of the new buffered image
        int transparency = Transparency.OPAQUE;
        if (hasAlpha) {
            transparency = Transparency.BITMASK;
        }

        // Create the buffered image
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gs.getDefaultConfiguration();
        bimage = gc.createCompatibleImage(
            image.getWidth(null), image.getHeight(null), transparency);
    } catch (HeadlessException e) {
        // The system does not have a screen
    }

    if (bimage == null) {
        // Create a buffered image using the default color model
        int type = BufferedImage.TYPE_INT_RGB;
        if (hasAlpha) {
            type = BufferedImage.TYPE_INT_ARGB;
        }
        bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
    }

    // Copy image to buffered image
    Graphics g = bimage.createGraphics();

    // Paint the image onto the buffered image
    g.drawImage(image, 0, 0, null);
    g.dispose();

    return bimage;
}

// This method returns true if the specified image has transparent pixels
public static boolean hasAlpha(Image image) {
    // If buffered image, the color model is readily available
    if (image instanceof BufferedImage) {
        BufferedImage bimage = (BufferedImage)image;
        return bimage.getColorModel().hasAlpha();
    }

    // Use a pixel grabber to retrieve the image's color model;
    // grabbing a single pixel is usually sufficient
     PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
    try {
        pg.grabPixels();
    } catch (InterruptedException e) {
    }

    // Get the image's color model
    ColorModel cm = pg.getColorModel();
    return cm.hasAlpha();
}



}//end of class PanelTextura
