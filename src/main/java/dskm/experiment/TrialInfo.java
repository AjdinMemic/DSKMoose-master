package dskm.experiment;

import dskm.gui.Circle;

import java.awt.*;
import java.awt.geom.Point2D;

import static java.lang.Math.sqrt;
/********************************
 *  Created by David Ahlstroem  *
 ********************************/
public class TrialInfo {

    private static boolean headerLineIsWritten = false;

    private int blockNumber = 0;
    private int trialInBlock = 0;

    private int distancePix = 0;
    private int widthPix = 0;
    private int widthMethodB=0;

    private double distanceMM = 0;
    private double widthMM = 0;

    private String movementDirection = "Calib";

    private double pixelSizeMM = 0;

    private Circle startAsCircle=null;
    private Circle target = null;
    private Circle start = null;

    private double cursorSizeMM = 0;
    private double cursorSizePix = 0;

    private String participantID = "";

    private int pressPointXTarget = 0;
    private int pressPointYTarget = 0;

    private int releasePointXTarget = 0;
    private int releasePointYTarget = 0;

    private int pressPointXStart = 0;
    private int pressPointYStart = 0;

    private int releasePointXStart = 0;
    private int releasePointYStart = 0;

    private long trialStartTime = 0;
    private long trialEndTime = 0;
    private int trialTime = 0;

    private int hit = 0;
    private int trialNumInTest = 0;

    private String testType;
    private String methodType;
    private Boolean AllCirclesFlag;
    private int numberOfCircles;
    private int  distBetCirclemm;

    private String quartile;
    private Point2D.Double radiusFromTo;
    private double randomRadNum;
    private int section;

    public Circle getStartAsCircle() {
        return startAsCircle;
    }

    public void setStartAsCircle(Circle startAsCircle) {
        this.startAsCircle = startAsCircle;
    }

    public TrialInfo(String method,
                     String quartile,
                     Boolean AllCircleFlag,
                     int numberOfCircles,
                     int distBetCirclemm,
                     int blockNumber,
                     int trialInBlock,
                     int distancePix,
                     int widthPix,
                     double pixelSizeMM,
                     Circle startAsCircle,
                     Circle target,
                     Circle start,
                     double cursorSizeMM,
                     String participantID,
                     String testType,
                     String movementDirection,
                     Point2D.Double radiusFromTo,
                     double randomRadNum,
                     int section) {
        this.quartile = quartile;
        this.methodType =method;
        this.AllCirclesFlag=AllCircleFlag;
        this.numberOfCircles=numberOfCircles;
        this.distBetCirclemm=distBetCirclemm;
        this.blockNumber = blockNumber;
        this.trialInBlock = trialInBlock;
        this.distancePix = distancePix;
        this.widthPix = widthPix;
        this.pixelSizeMM = pixelSizeMM;
        this.distanceMM = distancePix * pixelSizeMM;
        this.widthMM = widthPix * pixelSizeMM;
        this.startAsCircle=startAsCircle;
        this.target = target;
        this.start = start;
        this.cursorSizeMM = cursorSizeMM;
        this.cursorSizePix = cursorSizeMM/pixelSizeMM;
        this.participantID = participantID;
        this.testType = testType;
        this.movementDirection = movementDirection;
        this.radiusFromTo=radiusFromTo;
        this.randomRadNum=randomRadNum;
        this.section=section;
    }

    public TrialInfo copyTrialInfo(){
        TrialInfo copy = new TrialInfo(
                this.methodType,
                this.quartile,
                this.AllCirclesFlag,
                this.numberOfCircles,
                this.distBetCirclemm,
                this.blockNumber,
                this.trialInBlock,
                this.distancePix,
                this.widthPix,
                this.pixelSizeMM,
                this.startAsCircle,
                this.target,
                this.start,
                this.cursorSizeMM,
                this.participantID,
                this.testType,
                this.movementDirection,
                this.radiusFromTo,
                this.randomRadNum,
                this.section
        );
        copy.setBlockNumber(this.getBlockNumber());
        copy.setTrialInBlock(this.getTrialInBlock());
        return copy;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public int getTrialInBlock() {
        return trialInBlock;
    }

    public int getDistancePix() {
        return distancePix;
    }

    public int getWidthPix() {
        return widthPix;
    }

    public double getDistanceMM() {
        return distanceMM;
    }

    public double getWidthMM() {
        return widthMM;
    }

    public Circle getTarget() {
        return target;
    }

    public Circle getStart() {
        return start;
    }

    public double getCursorSizeMM() {
        return cursorSizeMM;
    }
    public double getCursorSizePix() {
        return cursorSizePix;
    }

    public String getParticipantID() {
        return participantID;
    }

    public String getMovementDirection() {
        return movementDirection;
    }

    public int getPressPointXTarget() {
        return pressPointXTarget;
    }

    public int getPressPointYTarget() {
        return pressPointYTarget;
    }

    public int getPressPointXStart() { return pressPointXStart; }

    public int getPressPointYStart() {
        return pressPointYStart;
    }

    public int getReleasePointXTarget() {
        return releasePointXTarget;
    }

    public int getReleasePointYTarget() {
        return releasePointYTarget;
    }

    public int getReleasePointXStart() {
        return releasePointXStart;
    }

    public int getReleasePointYStart() {
        return releasePointYStart;
    }

    public long getTrialStartTime() {
        return trialStartTime;
    }

    public long getTrialEndTime() {
        return trialEndTime;
    }

    public long getTrialTime(){
        return this.trialTime;
    }

    public String getTestType(){
        return this.testType;
    }

    public void setPressPointXTarget(int pressPointXTarget) {
        this.pressPointXTarget = pressPointXTarget;
    }

    public void setPressPointYTarget(int pressPointYTarget) {
        this.pressPointYTarget = pressPointYTarget;
    }

    public void setPressPointXStart(int pressPointXStart) {
        this.pressPointXStart = pressPointXStart;
    }

    public void setPressPointYStart(int pressPointYStart) {
        this.pressPointYStart = pressPointYStart;
    }

    public void setReleasePointXTarget(int releasePointXTarget) {
        this.releasePointXTarget = releasePointXTarget;
    }

    public void setReleasePointYTarget(int releasePointYTarget) {
        this.releasePointYTarget = releasePointYTarget;
    }

    public void setReleasePointXStart(int releasePointXStart) {
        this.releasePointXStart = releasePointXStart;
    }

    public void setReleasePointYStart(int releasePointYStart) {
        this.releasePointYStart = releasePointYStart;
    }

    public void setTrialStartTime(long trialStartTime) {
        this.trialStartTime = trialStartTime;
    }

    public void setTrialEndTime(long trialEndTime) {
        this.trialEndTime = trialEndTime;
        this.trialTime = (int)(trialEndTime - trialStartTime);
    }

    public void setHit(int hit){
        this.hit = hit;
    }

    public void setTrialNumInTest(int trialNumInTest){
        this.trialNumInTest = trialNumInTest;
    }

    public void setBlockNumber(int blockNumber) {
        this.blockNumber = blockNumber;
    }

    public void setTrialInBlock(int trialInBlock) {
        this.trialInBlock = trialInBlock;
    }

    public void setTarget(Circle target) {
        this.target = target;
    }

    public void setMovementDirection(String movementDirection) {
        this.movementDirection = movementDirection;
    }

    public void setStart(Circle start) {
        this.start = start;
    }

    public String toLogString(){
        if(methodType.equals("Circles")){
            this.widthMM=widthMethodB;//widthMethodB;
            this.widthPix=convertMMtoPIX(widthMM);
            this.start=startAsCircle;
        }

        return "" +
                methodType + ";"+
                pixelSizeMM + ";" +
                testType + ";" +
                participantID + ";" +
                trialNumInTest + ";" +
                blockNumber + ";" +
                trialInBlock + ";" +
                distanceMM + ";" +
                distancePix + ";" +
                widthMM + ";" +
                widthPix + ";" +
                cursorSizeMM + ";" +
                cursorSizePix + ";" +
                movementDirection + ";" +
                radiusFromTo.x + ";"+
                radiusFromTo.y + ";"+
                randomRadNum   + ";"+
                section        + ";"+
                start.getX() + ";" +
                start.getY() + ";" +
                start.getCenterX() + ";" +
                start.getCenterY() + ";" +
                start.getRadius() + ";" +
                numberOfCircles + ";" +
                distBetCirclemm + ";" +
                AllCirclesFlag + ";" +
                target.getX() + ";" +
                target.getY() + ";" +
                target.getCenterX() + ";" +
                target.getCenterY() + ";" +
                target.getSide() + ";" +

                pressPointXStart + ";" +
                pressPointYStart + ";" +
                calculateEucDistance("pix",
                        new Point(start.getCenterX(), start.getCenterY()),
                        new Point(pressPointXStart, pressPointYStart)) + ";" +
                calculateEucDistance("mm",
                        new Point(start.getCenterX(), start.getCenterY()),
                        new Point(pressPointXStart, pressPointYStart)) + ";" +
                releasePointXStart + ";" +
                releasePointYStart + ";" +
                calculateEucDistance("pix",
                        new Point(start.getCenterX(), start.getCenterY()),
                        new Point(releasePointXStart, releasePointYStart)) + ";" +
                calculateEucDistance("mm",
                        new Point(start.getCenterX(), start.getCenterY()),
                        new Point(releasePointXStart, releasePointYStart)) + ";" +


                pressPointXTarget + ";" +
                pressPointYTarget + ";" +
                calculateEucDistance("pix",
                        new Point(target.getCenterX(), target.getCenterY()),
                        new Point(pressPointXTarget, pressPointYTarget)) + ";" +
                calculateEucDistance("mm",
                        new Point(target.getCenterX(), target.getCenterY()),
                        new Point(pressPointXTarget, pressPointYTarget)) + ";" +
                releasePointXTarget + ";" +
                releasePointYTarget + ";" +
                calculateEucDistance("pix",
                        new Point(target.getCenterX(), target.getCenterY()),
                        new Point(releasePointXTarget, releasePointYTarget)) + ";" +
                calculateEucDistance("mm",
                        new Point(target.getCenterX(), target.getCenterY()),
                        new Point(releasePointXTarget, releasePointYTarget)) + ";" +
                hit + ";" +
                trialStartTime + ";" +
                trialEndTime + ";" +
                trialTime +
                "";
    }

    public double calculateEucDistance(String type, Point center, Point click){
        double retValue = sqrt(Math.pow(center.getX() - click.getX(), 2) +
                Math.pow(center.getY() - click.getY(), 2));
        if(type.equals("mm")){
            retValue = retValue * this.pixelSizeMM;
        }
        double roundDigits = retValue * 1000000d;
        int temp = (int)roundDigits;
        retValue = temp/1000000d;
        return retValue;
    }

    public static String getHeaderLine(){
        String headerLine =
                "method" + ";" +
                "pixelSizeMM" + ";" +
                        "testType" + ";" +
                        "part" + ";" +
                        "trialNumInTest" + ";" +
                        "blockNumber" + ";" +
                        "trialInBlock" + ";" +
                        "distanceMM" + ";" +
                        "distancePix" + ";" +
                        "widthMM" + ";" +
                        "widthPix" + ";" +
                        "cursorSizeMM" + ";" +
                        "cursorSizePix" + ";" +
                        "movementDirection" + ";" +
                        "degree lower bound" + ";"+
                        "degree upper bound" + ";"+
                        "random degree"   + ";"+
                        "section"           +";"+
                        "startPosX" + ";" +
                        "startPosY" + ";" +
                        "startCenterX" + ";" +
                        "startCenterY" + ";" +
                        "startRadius" + ";" +
                        "numberOfCircles"+ ";" +
                        "distBetCircleMM"+ ";" +
                        "allCircleFlag"  + ";" +
                        "targetPosX" + ";" +
                        "targetPosY" + ";" +
                        "targetCenterX" + ";" +
                        "targetCenterY" + ";" +
                        "targetSideLengthPix" + ";" +
                        "pressPointStartX" + ";" +
                        "pressPointStartY" + ";" +
                        "EucDistPressStartPix" + ";" +
                        "EucDistPressStartMM" + ";" +
                        "releasePointStartX" + ";" +
                        "releasePointStartY" + ";" +
                        "EucDistReleaseStartPix" + ";" +
                        "EucDistReleaseStartMM" + ";" +
                        "pressPointTargetX" + ";" +
                        "pressPointTargetY" + ";" +
                        "EucDistPressTargetPix" + ";" +
                        "EucDistPressTargetMM" + ";" +
                        "releasePointTargetX" + ";" +
                        "releasePointTargetY" + ";" +
                        "EucDistReleaseTargetPix" + ";" +
                        "EucDistReleaseTargetMM" + ";" +
                        "hit" + ";" +
                        "trialStartTime" + ";" +
                        "trialEndTime" + ";" +
                        "trialTime" +
                        "\n";
        TrialInfo.headerLineIsWritten = true;
        return headerLine;
    }

    public static boolean isHeaderLineIsWritten() {
        return headerLineIsWritten;
    }

    @Override
    public String toString() {
        return "TrialInfo{" +
                "participantID='" + participantID + '\'' +
                ", blockNumber=" + blockNumber +
                ", trialInBlock=" + trialInBlock +
                ", distancePix=" + distancePix +
                ", widthPix=" + widthPix +
                ", cursorSizeMM=" + cursorSizeMM +
                ", distanceMM=" + distanceMM +
                ", widthMM=" + widthMM +
                ", pixelSizeMM=" + pixelSizeMM +
                ", movementDirection=" + movementDirection +
                ", target=" + target +
                ", start=" + start +
                ", pressPointXTarget=" + pressPointXTarget +
                ", pressPointYTarget=" + pressPointYTarget +
                ", clickPointXStart=" + pressPointXStart +
                ", clickPointYStart=" + pressPointYStart +
                ", hit=" + hit +
                ", trialStartTime=" + trialStartTime +
                ", trialEndTime=" + trialEndTime +
                ", trialTime=" + trialTime +
                '}';
    }

    public void setWidthPix(int widthPix) {
        this.widthPix = widthPix;
    }

    public void setDistancePix(int distancePix) {
        this.distancePix = distancePix;
    }

    public void setDistanceMM(double distanceMM) {
        this.distanceMM = distanceMM;
    }

    private int convertMMtoPIX(double dim) {
        return (int) (Math.rint(dim / this.pixelSizeMM));
    }

    public void setRealWidthPix(int widthPix){
        this.widthMethodB=widthPix;
    }

    public String getQuartile() {
        return quartile;
    }

    public void setQuartile(String quartile) {
        this.quartile = quartile;
    }

    public Point2D.Double getRadiusFromTo() {
        return radiusFromTo;
    }

    public void setRadiusFromTo(Point2D.Double radiusFromTo) {
        this.radiusFromTo = radiusFromTo;
    }

    public double getRandomRadNum() {
        return randomRadNum;
    }

    public void setRandomRadNum(double randomRadNum) {
        this.randomRadNum = randomRadNum;
    }
}
