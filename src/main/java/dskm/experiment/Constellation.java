package dskm.experiment;

import com.google.common.collect.ImmutableList;
import dskm.Config;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public enum Constellation {
    FITTS_CURSORSIZE_1 (Config.TEST_TYPE_FITTS, 1.0, 3, 1),
    FITTS_CURSORSIZE_2 (Config.TEST_TYPE_FITTS, 1.0, 1, 1),
    FITTS_CURSORSIZE_25 (Config.TEST_TYPE_FITTS, 25.0, 3, 1),
    FITTS_CURSORSIZE_30 (Config.TEST_TYPE_FITTS, 30.0, 3, 1),
    FITTS_CURSORSIZE_35 (Config.TEST_TYPE_FITTS, 35.0, 3, 1),

    CALIB_CURSORSIZE_1 (Config.TEST_TYPE_CALIB, 1.0, 2, 8),
    CALIB_CURSORSIZE_25 (Config.TEST_TYPE_CALIB, 25.0, 2, 8),
    CALIB_CURSORSIZE_30 (Config.TEST_TYPE_CALIB, 30.0, 2, 8),
    CALIB_CURSORSIZE_35 (Config.TEST_TYPE_CALIB, 35.0, 2, 8);

    private String testType;
    private double cursorSizeMM;
    private int nrBlocks;
    private int nrRepetitions;

    private List<Double> radList;
    private List<Double> distList;
    private List<Double> cursorList;
    private ArrayList<Point2D.Double> tupels=new ArrayList<>();
    private Constellation(String testType,
                          double cursorSizeMM,
                          int nrBlocks,
                          int nrRepetitions){
        this.testType = testType;
        this.cursorSizeMM = cursorSizeMM;
        this.nrBlocks = nrBlocks;
        this.nrRepetitions = nrRepetitions;

        if(this.testType.equals("Fitts")){
            radList = ImmutableList.of(2.4, 4.9, 7.2, 10.0, 15.0, 20.0, 25.0);
            distList = ImmutableList.of(50.0, 110.0, 150.0);
            cursorList = ImmutableList.of(this.cursorSizeMM);

            for(int i=0;i<4;i++) {

                tupels.add(new Point2D.Double(2.4, 50.0));
                tupels.add(new Point2D.Double(2.4, 150.0));

                tupels.add(new Point2D.Double(4.8, 50.0));
                tupels.add(new Point2D.Double(4.9, 150.0));

                tupels.add(new Point2D.Double(7.2, 50.0));
                tupels.add(new Point2D.Double(7.2, 150.0));

                tupels.add(new Point2D.Double(15.0, 50.0));
                tupels.add(new Point2D.Double(15.0, 150.0));

                tupels.add(new Point2D.Double(20.0, 50.0));
                tupels.add(new Point2D.Double(20.0, 150.0));

                tupels.add(new Point2D.Double(25.0, 50.0));
                tupels.add(new Point2D.Double(25.0, 150.0));

            }


        }else{
            radList = ImmutableList.of(2.4);
            distList = ImmutableList.of(0.0);
            cursorList = ImmutableList.of(this.cursorSizeMM);
        }
    }

    public String getTestType() {
        return testType;
    }

    public double getCursorSizeMM() {
        return cursorSizeMM;
    }

    public int getNrBlocks() {
        return nrBlocks;
    }

    public int getNrRepetitions() {
        return nrRepetitions;
    }

    public List<Double> getRadList() {
        return radList;
    }

    public List<Double> getDistList() {
        return distList;
    }

    public List<Double> getCursorList() {
        return cursorList;
    }

    public ArrayList<Point2D.Double> getTupels() {
        return tupels;
    }
}
