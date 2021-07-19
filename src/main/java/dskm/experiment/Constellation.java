package dskm.experiment;

import com.google.common.collect.ImmutableList;
import dskm.Config;

import java.util.List;

public enum Constellation {
    FITTS_CURSORSIZE_1 (Config.TEST_TYPE_FITTS, 1.0, 1, 1),
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

    private Constellation(String testType,
                          double cursorSizeMM,
                          int nrBlocks,
                          int nrRepetitions){
        this.testType = testType;
        this.cursorSizeMM = cursorSizeMM;
        this.nrBlocks = nrBlocks;
        this.nrRepetitions = nrRepetitions;

        if(this.testType.equals("Fitts")){
            radList = ImmutableList.of(2.4, 4.8, 7.2, 10.0, 15.0, 20.0, 25.0);
            distList = ImmutableList.of(50.0, 110.0, 150.0);
            cursorList = ImmutableList.of(this.cursorSizeMM);
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
}
