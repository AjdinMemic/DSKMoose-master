package dskm.gui;

import java.awt.*;

public class StartRectangle {
    int width;
    int height;
    int tlX, tlY; // Top-left (X,Y)
    int centerX, centerY; // Center (X,Y)

    Color color;

    public StartRectangle(int centerX, int centerY,
                     int width, int height) {
        this.width = width;
        this.height = height;
        tlX = centerX - width/2;
        tlY = centerY - height/2;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    /***
     * Check if a point is inside the circle or not (border is counted inside)
     * @param x X
     * @param y Y
     * @return True -> inside
     */
    public boolean isInside(int x, int y) {
        boolean xOK = false;
        boolean yOK = false;

        if(x >= this.getX() && x <= this.getX() + this.getWidth()){
            xOK = true;
        }
        if(y >= this.getY() && y <= this.getY() + this.getHeight()){
            yOK = true;
        }

        return xOK && yOK;
    }

    public void setColor(Color clr) {
        color = clr;
    }

    public Color getColor() { return color; }
    public int getX(){ return tlX; }
    public int getY(){ return tlY; }
    public int getWidth() { return width; }
    public int getHeight(){ return height; }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }
}
