package dskm.gui;

import java.awt.*;

import static java.lang.Math.sqrt;

public class Circle {

    int radius;
    int tlX, tlY; // Top-left (X,Y)
    int side;
    int centerX, centerY; // Center (X,Y)

    Color color;

    /***
     * Constructor
     * @param centerX Center X
     * @param centerY Center Y
     * @param radius Radius
     */
    public Circle(int centerX, int centerY, int radius) {
        this.radius = radius;
        tlX = centerX - radius;
        tlY = centerY - radius;
        this.centerX = centerX;
        this.centerY = centerY;
        side = radius * 2;
    }

    /***
     * Check if a point is inside the circle or not (border is counted inside)
     * @param x X
     * @param y Y
     * @return True -> inside
     */
    public boolean isInside(int x, int y) {
        //System.out.println("center = " + centerX + " , " + centerY);
        //System.out.println("radius = " + radius);
        //System.out.println("x, y = " + x + ", " + y);
        double distToCenter = sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        //System.out.println("Dist = " + distToCenter + " radius: " + radius);
        if (distToCenter <= radius) return true;
        else return false;
    }

    public void setColor(Color clr) {
        color = clr;
    }
    
    public Color getColor() { return color; }
    public int getX(){ return tlX; }
    public int getY(){ return tlY; }
    public int getRadius() { return radius; }
    public int getSide(){ return side; }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    @Override
    public String toString() {
        return "Circle{" +
                "radius=" + radius +
                ", tlX=" + tlX +
                ", tlY=" + tlY +
                ", side=" + side +
                ", cx=" + centerX +
                ", cy=" + centerY +
                '}';
    }
}
