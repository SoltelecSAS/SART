package org.soltelec.componenteluces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.Timer;

/**
 *
 * @author hansolo
 */
public class LuzExploradora extends javax.swing.JComponent implements java.awt.event.ComponentListener,ActionListener
{
    private final java.awt.GraphicsConfiguration GFX_CONF;
    private java.awt.image.BufferedImage backgroundImage;
    private java.awt.image.BufferedImage contentImage;
    private java.awt.image.BufferedImage redContentImage;
    private java.awt.image.BufferedImage foregroundImage;
    private double value;
    private Timer timer;
    private Color color;
    private boolean cambiar = false;

    // Constructor
    public LuzExploradora()
    {
        super();
        GFX_CONF = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        backgroundImage = GFX_CONF.createCompatibleImage(200, 200, java.awt.Transparency.TRANSLUCENT);
        contentImage = GFX_CONF.createCompatibleImage(200, 200, java.awt.Transparency.TRANSLUCENT);
        redContentImage = GFX_CONF.createCompatibleImage(200, 200, java.awt.Transparency.TRANSLUCENT);
        foregroundImage = GFX_CONF.createCompatibleImage(200, 200, java.awt.Transparency.TRANSLUCENT);
        value = 0;
        addComponentListener(this);
        setPreferredSize(new java.awt.Dimension(200, 200));
        //color = Color.ORANGE;
        color = Color.LIGHT_GRAY;
        init(getWidth(),getHeight());
    }

    // Initialization
    private void init(final int WIDTH, final int HEIGHT)
    {
        if(backgroundImage != null)
    	backgroundImage.flush();
        backgroundImage = create_BACKGROUND_Image(WIDTH, HEIGHT);
        if(contentImage != null)
        contentImage.flush();
        contentImage = create_CONTENT_Image(WIDTH, HEIGHT);
        if(foregroundImage != null)
		foregroundImage.flush();
        foregroundImage = create_FOREGROUND_Image(WIDTH, HEIGHT);
    }

    // Custom painting code
    @Override
    protected void paintComponent(final java.awt.Graphics G)
    {
        super.paintComponent(G);

        final java.awt.Graphics2D G2 = (java.awt.Graphics2D) G.create();
        G2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_DITHERING, java.awt.RenderingHints.VALUE_DITHER_ENABLE);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION, java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_COLOR_RENDERING, java.awt.RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_STROKE_CONTROL, java.awt.RenderingHints.VALUE_STROKE_PURE);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_FRACTIONALMETRICS, java.awt.RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw background image
        G2.drawImage(backgroundImage, 0, 0, null);

        // Draw content image
        G2.drawImage(contentImage, 0, 0, null);

        // Draw foreground image
        G2.drawImage(foregroundImage, 0, 0, null);

        // Free memory
        G2.dispose();
    }

    // <editor-fold defaultstate="collapsed" desc="Getters and Setters">
    public double getValue()
    {
        return this.value;
    }

    public void setValue(final double VALUE)
    {
        this.value = VALUE;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Image creation methods">
    private java.awt.image.BufferedImage create_BACKGROUND_Image(final int WIDTH, final int HEIGHT)
    {
        if (WIDTH <= 0 || HEIGHT <= 0)
        {
            return null;
        }

        final java.awt.image.BufferedImage IMAGE = GFX_CONF.createCompatibleImage(WIDTH, HEIGHT, java.awt.Transparency.TRANSLUCENT);
        final java.awt.Graphics2D G2 = IMAGE.createGraphics();
        G2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_DITHERING, java.awt.RenderingHints.VALUE_DITHER_ENABLE);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION, java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_COLOR_RENDERING, java.awt.RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_STROKE_CONTROL, java.awt.RenderingHints.VALUE_STROKE_PURE);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_FRACTIONALMETRICS, java.awt.RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        final int IMAGE_WIDTH = IMAGE.getWidth();
        final int IMAGE_HEIGHT = IMAGE.getHeight();

		// **************** DRAWING CODE **********************

        if(IMAGE_WIDTH < IMAGE_HEIGHT)
            dibujarBackGround(G2, IMAGE_WIDTH-10);
        else if(IMAGE_HEIGHT < IMAGE_WIDTH)
            dibujarBackGround(G2,IMAGE_HEIGHT-10);// ****************************************************
        else if (IMAGE_WIDTH == IMAGE_HEIGHT)
            dibujarBackGround(G2, IMAGE_HEIGHT);

        G2.dispose();

        return IMAGE;
    }


    private void dibujarBackGround(Graphics2D g2, int diametro){
        RadialGradientPaint r = new RadialGradientPaint(50, 50, 50, new float[]{0.5f,1.0f},new Color[]{Color.white,Color.black});
        g2.setStroke(new BasicStroke(10));
        g2.setPaint(r);
        //g2.setColor(Color.red);
        //g2.fillOval(0, 0, 100, 100);
        GradientPaint gradient = new GradientPaint(0, 0, Color.GRAY, diametro-10, diametro-10, Color.LIGHT_GRAY);
        g2.setPaint(gradient);
        g2.drawOval(5,5,diametro-10,diametro-10);
        gradient = new GradientPaint( (diametro - 10)/2 ,0,new Color(0x33,0x33,0x33),(diametro-10)/2, diametro-10, new Color(0xA0,0xA0,0xA0));
        g2.setPaint(gradient);
        g2.fillOval(10,10,diametro-20,diametro-20);
    }


    private java.awt.image.BufferedImage create_CONTENT_Image(final int WIDTH, final int HEIGHT)
    {
        if (WIDTH <= 0 || HEIGHT <= 0)
        {
            return null;
        }

        final java.awt.image.BufferedImage IMAGE = GFX_CONF.createCompatibleImage(WIDTH, HEIGHT, java.awt.Transparency.TRANSLUCENT);
        final java.awt.Graphics2D G2 = IMAGE.createGraphics();
        G2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_DITHERING, java.awt.RenderingHints.VALUE_DITHER_ENABLE);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION, java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_COLOR_RENDERING, java.awt.RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_STROKE_CONTROL, java.awt.RenderingHints.VALUE_STROKE_PURE);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_FRACTIONALMETRICS, java.awt.RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        final int IMAGE_WIDTH = IMAGE.getWidth();
        final int IMAGE_HEIGHT = IMAGE.getHeight();

		// **************** DRAWING CODE **********************
        if(IMAGE_WIDTH < IMAGE_HEIGHT)
            dibujarContenido(G2, IMAGE_WIDTH-10);
        else if(IMAGE_HEIGHT < IMAGE_WIDTH)
            dibujarContenido(G2,IMAGE_HEIGHT-10);// ****************************************************
        else if (IMAGE_WIDTH == IMAGE_HEIGHT)
            dibujarContenido(G2, IMAGE_HEIGHT);

        G2.dispose();


	    // ****************************************************

        G2.dispose();

        return IMAGE;
    }


    private void dibujarContenido(Graphics2D g2, int d){
        g2.setStroke(new BasicStroke(d/18));
        //g2.setColor(new Color(0x00,0x77,0xd6));
        g2.setColor(color);
        int diametro = d;
        g2.drawArc(diametro/4, diametro/3,diametro/2,diametro/3,90,180) ;
        g2.drawLine(diametro/2, diametro/3,diametro/2, diametro*2/3);

        g2.drawLine(diametro*3/5, diametro*3/8, diametro*7/10,diametro*3/8);
        g2.drawLine(diametro*3/5, diametro/2, diametro*7/10,diametro/2);
        g2.drawLine(diametro*3/5, diametro*5/8, diametro*7/10,diametro*5/8);

        g2.setStroke(new BasicStroke(d/36));
        g2.drawArc(diametro*6/10, diametro/3, diametro/12,diametro/6, 90, -180);
        g2.drawArc(diametro*6/10, diametro/2, diametro/12,diametro/6, 90, 180);


    }
    private java.awt.image.BufferedImage create_FOREGROUND_Image(final int WIDTH, final int HEIGHT)
    {
        if (WIDTH <= 0 || HEIGHT <= 0)
        {
            return null;
        }

        final java.awt.image.BufferedImage IMAGE = GFX_CONF.createCompatibleImage(WIDTH, HEIGHT, java.awt.Transparency.TRANSLUCENT);
        final java.awt.Graphics2D G2 = IMAGE.createGraphics();
        G2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_DITHERING, java.awt.RenderingHints.VALUE_DITHER_ENABLE);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION, java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_COLOR_RENDERING, java.awt.RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_STROKE_CONTROL, java.awt.RenderingHints.VALUE_STROKE_PURE);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_FRACTIONALMETRICS, java.awt.RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        final int IMAGE_WIDTH = IMAGE.getWidth();
        final int IMAGE_HEIGHT = IMAGE.getHeight();

		// **************** DRAWING CODE **********************



	    // ****************************************************

        G2.dispose();

        return IMAGE;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ComponentListener methods">
    @Override
    public void componentResized(final java.awt.event.ComponentEvent EVENT)
    {
        setPreferredSize( new Dimension (getWidth(),getHeight()));
        init(getWidth(), getHeight());
        revalidate();
        repaint();
    }

	@Override
    public void componentMoved(final java.awt.event.ComponentEvent EVENT)
    {

    }

	@Override
    public void componentShown(final java.awt.event.ComponentEvent EVENT)
    {

    }

    @Override
    public void componentHidden(final java.awt.event.ComponentEvent EVENT)
    {

    }
    //</editor-fold>

    @Override
    public String toString()
    {
        return "ComponentTemplate";
    }

    public void mouseClicked(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mousePressed(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseReleased(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseEntered(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseExited(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseDragged(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseMoved(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public void setBlinking(boolean parpadear){
        if(parpadear){
            if(timer != null)
                timer.start();
                else{
                timer = new Timer(500,this);
                timer.start();
            }
        } else {
            if(timer != null)
                timer.stop();
            color = Color.ORANGE;
            contentImage = create_CONTENT_Image(this.getWidth(), this.getHeight());
            repaint();
        }

    }

    @Override
    protected void finalize() throws Throwable{
        super.finalize();
        if(timer != null)
                timer.stop();
    }
    public void actionPerformed(ActionEvent e) {
        //System.out.println("Camibanco");
                    if(cambiar){
                    color = Color.white;
                    cambiar = !cambiar;
                    }else{
                       color = Color.LIGHT_GRAY;
                       cambiar = !cambiar;
                    }
                   contentImage = create_CONTENT_Image(this.getWidth(),this.getHeight());
                   repaint();
                }

    public void setOnOff(boolean b){
        if(b)
            color = Color.ORANGE;
        else
            color = Color.LIGHT_GRAY;
        contentImage = create_CONTENT_Image(this.getWidth(),this.getHeight());
        repaint();

    }
    
}
