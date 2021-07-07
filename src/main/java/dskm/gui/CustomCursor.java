package dskm.gui;

import dskm.Config;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/********************************
 *  Created by David Ahlstroem  *
 ********************************/
public class CustomCursor{
    Cursor cursor = null;
    int sizeMM = 0;
    boolean isStandardCursor = false;
    int diameter;
    double pixelSizeMM;
    Image cursorImage;
    String imagePath;

    public CustomCursor(double sizeMM, double pixelSizeMM){
        this.pixelSizeMM = pixelSizeMM;
        this.diameter = convertMMtoPIX(sizeMM);
        //make sure we have a odd pixel width to
        //have a center hotspot
        if(this.diameter % 2 == 0){
            this.diameter = 1 + this.diameter;
        }
        //System.out.println("cursorPix: " + this.diameter);
        this.sizeMM = (int)sizeMM;
        createCursorImage();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        cursorImage = toolkit.getImage(imagePath);

        int half = (this.diameter-1)/2;
        //System.out.println("cursor mid pix: " + half);
        cursor = toolkit.createCustomCursor(cursorImage,
                new Point(half, half),
                "cursor img");
    }

    private void createCursorImage(){
        BufferedImage bi = new BufferedImage(diameter, diameter,
                BufferedImage.TYPE_INT_ARGB);
        imagePath = "cursor_img/DDcursor_" +
                getSizeMM() + "mm" + ".png";
        Graphics2D g2D = bi.createGraphics();
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setColor(Config.CURSOR_COLOR);
        g2D.fillOval(0,0,diameter,diameter);

        int midX = (diameter-1)/2;
        int midY = (diameter-1)/2;
        //System.out.println("cursorMid: " + midX);
        if(Config.CURSOR_CROSSHAIR){
            g2D.setColor(Config.CURSOR_CROSSHAIR_COLOR);

            g2D.setStroke(new BasicStroke(1));
            g2D.drawLine(midX,
                    midY - 4,
                    midX,
                    midY + 4);
            g2D.drawLine(midX - 4,
                    midY,
                    midX + 4,
                    midY);
        }
        //g2D.setColor(Color.RED);
        //g2D.setStroke(new BasicStroke(1));
        //g2D.drawRect(0,0,diameter-1,diameter-1);

        //g2D.setColor(Config.CURSOR_BORDER_COLOR);
        //g2D.setStroke(new BasicStroke(1));
        //g2D.drawOval(0,0,diameter-1,diameter-1);
        //g2D.setColor(Color.magenta);
        //g2D.fillOval(midX,midY,1,1);
        try {
            ImageIO.write(bi, "png", new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int convertMMtoPIX(double dim){
        return (int)(Math.rint(dim/this.pixelSizeMM));
    }

    public Cursor getCursor(){
        return cursor;
    }
    public int getSizeMM(){
        return sizeMM;
    }
}
