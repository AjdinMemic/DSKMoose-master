package dskm.methods;

import dskm.Config;
import dskm.Constants;
import dskm.experiment.Constellation;
import dskm.experiment.LogChecker;
import dskm.experiment.TrialInfo;
import dskm.gui.Circle;
import dskm.gui.CustomCursor;
import dskm.gui.DrawingPanel;
import dskm.gui.MainFrame;
import io.reactivex.rxjava3.subjects.PublishSubject;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SlicesInMiddle extends Method {

    private List<Point.Double> radDistList = new ArrayList<>();
    private int nTrials;
    private int trialNum;
    private int trialNumInTest = 0;
    private Circle previousTarget = new Circle(0, 0, 0);
    private int n;
    private static Point2D.Double[] points;

    private PublishSubject<String> expSubject;
    private Constellation testConstellation = Constellation.FITTS_CURSORSIZE_1;
    private String participantID = LogChecker.retNumbLog();

    private int blockNumber = 1;
    private ArrayList<TrialInfo> trials;
    private ArrayList<ArrayList<TrialInfo>> blocks;

    double pixelSizeMM;
    ;
    ArrayList<CustomCursor> cursors = new ArrayList<CustomCursor>();

    private List<Double> radList;
    private List<Double> distList;
    private ArrayList<Point2D.Double> tupels;
    private List<Double> cursorList;
    private boolean allCombinations;
    private String[] quartiles = {"NO"};

    public SlicesInMiddle(int n, boolean allCombinations) throws IOException {
        this.n = n;
        points = new Point2D.Double[n];
        expSubject = PublishSubject.create();
        int monitorPPI = Toolkit.getDefaultToolkit().getScreenResolution();

        pixelSizeMM = 25.4 / monitorPPI;
        trials = new ArrayList<TrialInfo>();
        blocks = new ArrayList<ArrayList<TrialInfo>>();
        radList = testConstellation.getRadList();
        distList = testConstellation.getDistList();
        tupels = testConstellation.getTupels();
        cursorList = testConstellation.getCursorList();
        this.allCombinations=allCombinations;
    }

    public String getParticipantID() {
        return participantID;
    }

    public void methodSetup() {
        this.generatePointRadius(n);
        for (int i = 0; i < points.length; i++) {
            System.out.println("p" + (i + 1) + " (" + points[i].x + " , " + points[i].y + " )");
        }
        this.generateRadiusDistancePairs();

        this.generateTrialList();

        this.addBlocks();

        this.blockNrLoop();

        setnTrials(blocks.size() * trials.size()); // Num. of trails = all the combinations (n x n)
    }

    public int getnTrials() {
        return nTrials;
    }

    public void setnTrials(int nTrials) {
        this.nTrials = nTrials;
    }

    public void generateRadiusDistancePairs() {
        // Generate all the pairs of radius/distance (using Point for int,int)
        for (double rad : radList) {
            for (double dist : distList) {
                radDistList.add(new Point.Double(rad, dist));
            }
        }
    }

    public void generateTrialList() {
      if(allCombinations){
          generateTrialListAllCombinations();
      }else {
          generateTrialListSetOfTupels();
      }
    }

    public void addBlocks() {
        for (int i = 0; i < testConstellation.getNrBlocks(); i++) {
            blocks.add(new ArrayList<TrialInfo>());
        }
    }

    public void blockNrLoop() {
        int blockNr = 1;
        for (ArrayList<TrialInfo> trialArray : blocks) {
            Collections.shuffle(trials);
            int trialInBlockNr = 1;
            for (TrialInfo ti : trials) {
                ti.setBlockNumber(blockNr);
                ti.setTrialInBlock(trialInBlockNr);
                trialInBlockNr++;
                trialArray.add(ti.copyTrialInfo());
            }
            blockNr++;
        }
    }

    private int convertMMtoPIX(double dim) {
        return (int) (Math.rint(dim / this.pixelSizeMM));
    }

    public void createTrial() {
        if (blocks.get(0).size() == 0) {
            //The running block was just finished
            blocks.remove(0);
            //Show Block-End Message for all blocks except for the
            //last block, then show Test-End Message.
            if (blocks.size() > 0) {
                JLabel label = new JLabel("Block " + this.blockNumber +
                        " out of " + testConstellation.getNrBlocks() + " is finished!");
                label.setFont(new Font("Arial", Font.PLAIN, 18));
                JOptionPane.showMessageDialog(
                        MainFrame.getFrame(),
                        label,
                        "",
                        JOptionPane.INFORMATION_MESSAGE,
                        new ImageIcon(new BufferedImage(1, 1,
                                BufferedImage.TYPE_INT_ARGB)));
                this.blockNumber++;
            }
        }
        if (blocks.size() == 0) {
            //The last block was just finished
            expSubject.onNext(Constants.MSSG_END_LOG);
            finishTestAndEnd();
        } else {// Create and send the panel to be drawn
            DrawingPanel exPanel = new DrawingPanel(0, "MethodC2", false);
            trialNum++;
            TrialInfo trialInfo = blocks.get(0).remove(0);
            for (CustomCursor cc : cursors) {
                if (cc.getSizeMM() == trialInfo.getCursorSizeMM()) {
                    exPanel.setCursor(cc.getCursor());
                    break;
                }
            }

            //Find out where to put target and the start.
            Circle startCircle = null;
            Circle targetCircle;
            if (testConstellation.getTestType().equals(Config.TEST_TYPE_FITTS)) {
                //For the Fitts task we need to consider both the
                //position of the target and the position of the start.
                targetCircle = determineTargetPositionFitts(trialInfo);
            } else {
                targetCircle = determineTargetPositionCalibration(trialInfo);
            }

            //Target position and start position are determined.
            //In case it is a calibration task, the start is fake.

            //Update previousTarget to the newly set new target.
            previousTarget = new Circle(targetCircle.getCenterX(),
                    targetCircle.getCenterY(),
                    trialInfo.getWidthPix() / 2);
            trialInfo.setTarget(new Circle(targetCircle.getCenterX(),
                    targetCircle.getCenterY(),
                    trialInfo.getWidthPix() / 2));
            trialNumInTest++;
            trialInfo.setTrialNumInTest(trialNumInTest);

            exPanel.setCurrentTrialInfo(trialInfo);
            exPanel.setBlockInfoToDraw("Block: " + this.blockNumber +
                    " of " + testConstellation.getNrBlocks());
            exPanel.setTextToDraw("Trial: " + trialInfo.getTrialInBlock() +
                    " of " + nTrials / testConstellation.getNrBlocks());

            MainFrame.getFrame().drawPanel(exPanel);
        }
    }

    /***
     * Create the drawing panel for the trial
     */
    int count = 0;

    private Circle determineTargetPositionFitts(TrialInfo trialInfo) {
        System.out.println("------");
        System.out.println("MOVEMENT DIRECTION: " + trialInfo.getMovementDirection());

        //In case the window title bar is showing
        int windowTitleBarHeight = MainFrame.getFrame().getInsets().top;
        int min = 0;
        int max = 0;
        int top = 0;
        int bot = 0;
        int xPos = 0;
        int yPos = 0;
        boolean posOK = false;

        //Start to determine the start position.
        Rectangle windowRec = MainFrame.getFrame().getBounds();
        min = windowRec.x + 20 +
                convertMMtoPIX(trialInfo.getCursorSizeMM() / 2) +
                (trialInfo.getWidthPix() / 2);
        max = windowRec.x + windowRec.width - 20 -
                convertMMtoPIX(trialInfo.getCursorSizeMM() / 2) -
                (trialInfo.getWidthPix() / 2);

        top = windowRec.y + windowRec.height - 20 - convertMMtoPIX(trialInfo.getCursorSizeMM() / 2) - (trialInfo.getWidthPix() / 2);
        // System.out.println("windowRec.y: " + windowRec.y);
        System.out.println("top: " + top);
        bot = windowRec.y + 20 + convertMMtoPIX(trialInfo.getCursorSizeMM() / 2) + (trialInfo.getWidthPix() / 2);
        System.out.println("bot: " + bot);
        int xTop = generateRandomPosition(bot, top);
        xPos = 0;

        //Make sure the selected xPos is more than cursor size away
        //from the xPosition of the previous target, otherwise the
        //new start position might be under the cursor position.

        while (!posOK) {
            xPos = generateRandomPosition(min, max);
            int distanceToPrevious = (int) trialInfo.calculateEucDistance("pix",
                    new Point(xPos, previousTarget.getCenterY()),
                    new Point(previousTarget.getCenterX(),
                            previousTarget.getCenterY()));
            if (distanceToPrevious > (20 + trialInfo.getCursorSizePix())) {
                //The xPos for the start is now not in conflict with the
                //previous target. Now see if it is acceptable according to
                //the movement direction and distance of the trial.

                if (trialInfo.getMovementDirection().equals(Config.MOVEMENT_DIRECTION_RIGTH) || trialInfo.getMovementDirection().equals(Config.MOVEMENT_BOT_RIGHT)) {
                    if (xPos + trialInfo.getDistancePix() < max) {
                        posOK = true;
                    }
                } else if (trialInfo.getMovementDirection().equals(Config.MOVEMENT_DIRECTION_LEFT) || trialInfo.getMovementDirection().equals(Config.MOVEMENT_BOT_LEFT)) {
                    if (xPos - trialInfo.getDistancePix() > min) {
                        posOK = true;
                    }
                }
            }
        }
        System.out.println("xPos:" + xPos);
        //Now find a suitable y-Position for the start.
        min = windowRec.y + Config.TEXT_Y + Config.TEXT_PAN_H + 20 +
                convertMMtoPIX(trialInfo.getCursorSizeMM() / 2) +
                trialInfo.getWidthPix() / 2;

        max = windowRec.height - windowRec.y - 20 -
                windowTitleBarHeight -
                convertMMtoPIX(trialInfo.getCursorSizeMM() / 2) -
                trialInfo.getWidthPix() / 2;

        posOK = false;
        yPos = 0;
        //Make sure the selected yPos is more than cursor size away
        //from the yPosition of the previous target, otherwise the
        //new target might be under the cursor position.
        System.out.println(trialInfo.getQuartile());
        while (!posOK) {
            yPos = generateRandomPosition(min, max);
            int distanceToPrevious = (int) trialInfo.calculateEucDistance("pix",
                    new Point(previousTarget.getCenterX(), yPos),
                    new Point(previousTarget.getCenterX(),
                            previousTarget.getCenterY()));
            if (trialInfo.getQuartile().equals("NO") || trialInfo.getQuartile().equals("NW")) {
                if (distanceToPrevious > (20 + trialInfo.getCursorSizePix()) && yPos + trialInfo.getDistancePix() > top) {
                    posOK = true;
                }
            } else if (trialInfo.getQuartile().equals("SO") || trialInfo.getQuartile().equals("SW")) {
                if (distanceToPrevious > (20 + trialInfo.getCursorSizePix()) && yPos - trialInfo.getDistancePix() - trialInfo.getTarget().getRadius() < bot) {
                    System.out.println("yPos " + yPos + " - distance"+trialInfo.getDistancePix()+ "  smaller then "+ bot);
                    posOK = true;
                }
            }
        }


        //Now we have a suitable x and y for the start circle.
        //Set the start for the trial.
        trialInfo.setStart(new Circle(xPos, yPos,
                Config.STAREC_WIDTH / 2));
        //fitta

        //Now we need to calculate the corresponding target position
        //based on the start position.
        double middle=(trialInfo.getRadiusFromTo().x+ trialInfo.getRadiusFromTo().y)/2;
        int[] posXY = addRadiusToLine(trialInfo.getRadiusFromTo().x, trialInfo.getRadiusFromTo().y, xPos, yPos, trialInfo.getDistancePix(), middle);
        // System.out.println("DIRECTION: "+trialInfo.getMovementDirection());
        xPos = posXY[0];
        yPos = posXY[1];

        //The yPos should be the same as for the start.
        //Accordingly, no need to find a new yPos.
        count++;
        return new Circle(xPos, yPos,
                trialInfo.getWidthPix() / 2);
    }

    private Circle determineTargetPositionCalibration(TrialInfo trialInfo) {

        //In case the window title bar is showing
        int windowTitleBarHeight = MainFrame.getFrame().getInsets().top;
        int min = 0;
        int max = 0;
        int xPos = 0;
        int yPos = 0;
        boolean posOK = false;

        //For the calibration task, we only need to consider the
        //position of the target.
        Rectangle windowRec = MainFrame.getFrame().getBounds();
        min = windowRec.x + 20 +
                convertMMtoPIX(trialInfo.getCursorSizeMM() / 2) +
                trialInfo.getWidthPix() / 2;

        max = windowRec.x + windowRec.width - 20 -
                convertMMtoPIX(trialInfo.getCursorSizeMM() / 2) -
                trialInfo.getWidthPix() / 2;
        xPos = 0;

        //Make sure the selected xPos is more than cursor size away
        //from the xPosition of the previous target, otherwise the
        //new target might be under the cursor position.
        while (!posOK) {
            xPos = generateRandomPosition(min, max);
            int distanceToPrevious = (int) trialInfo.calculateEucDistance("pix",
                    new Point(xPos, previousTarget.getCenterY()),
                    new Point(previousTarget.getCenterX(),
                            previousTarget.getCenterY()));
            if (distanceToPrevious > (20 + trialInfo.getCursorSizePix())) {
                posOK = true;
            }
        }

        //Now find a suitable y-Position
        min = windowRec.y + Config.TEXT_Y + Config.TEXT_PAN_H + 20 +
                convertMMtoPIX(trialInfo.getCursorSizeMM() / 2) +
                trialInfo.getWidthPix() / 2;
        max = windowRec.height - windowRec.y - 20 -
                windowTitleBarHeight -
                convertMMtoPIX(trialInfo.getCursorSizeMM() / 2) -
                trialInfo.getWidthPix() / 2;

        posOK = false;
        yPos = 0;
        //Make sure the selected yPos is more than cursor size away
        //from the yPosition of the previous target, otherwise the
        //new target might be under the cursor position.
        while (!posOK) {
            yPos = generateRandomPosition(min, max);
            int distanceToPrevious = (int) trialInfo.calculateEucDistance("pix",
                    new Point(previousTarget.getCenterX(), yPos),
                    new Point(previousTarget.getCenterX(),
                            previousTarget.getCenterY()));
            if (distanceToPrevious > (20 + trialInfo.getCursorSizePix())) {
                posOK = true;
            }
        }

        return new Circle(xPos, yPos,
                trialInfo.getWidthPix() / 2);
    }

    private int generateRandomPosition(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private int[] addRadiusToLine(double origin, double bound, int xPos, int yPos, int distance, double randomNumb) {
        int[] retVal = new int[2];
        double randomNum = randomNumb;
        System.out.println("----------");
        System.out.println("RADIUS from-to: (" + origin + " , " + bound + " )");
        System.out.println("RANDOM RADIUS: " + randomNum);
        System.out.println("");

        double angle = randomNum;

        int startX = xPos;
        int startY = yPos;
        int length = distance;

        double endX = startX + Math.cos(Math.toRadians(
                angle
        )) * length;

        double endY = (startY + Math.sin(Math.toRadians(angle)) * length);

        retVal[0] = (int) endX;
        retVal[1] = (int) endY;

        return retVal;
    }

    private void generatePointRadius(int n) {
        int count = 0;
        double from = 0;
        double divideRadius = 360 / n;
        double subtract = divideRadius / 2;
        for (int i = 0; i < n; i++) {
            double to = divideRadius + count;

            points[i] = new Point2D.Double(from - subtract, to - subtract);
            count += divideRadius;
            from = to;
        }

    }

    public void generateTrialListAllCombinations(){
        int section = -1;
        for (int i = 0; i < testConstellation.getNrRepetitions(); i++) {
            for (double cursorSize : cursorList) {
                for (Point2D.Double rad : points) {
                    section++;
                    for (Point.Double p : radDistList) {

                        double lowBound = rad.x;
                        double uppBound = rad.y;

                        double randomRadNum = ThreadLocalRandom.current().nextDouble(lowBound, uppBound);

                        // Generate the trial list
                        int widthPix = convertMMtoPIX(p.x);
                        int distancePix = convertMMtoPIX(p.y);
                        Circle start = null;
                        Circle target = null;
                        if (testConstellation.getTestType().equals(Config.TEST_TYPE_FITTS)) {
                            start = new Circle(Config.STACLE_X,
                                    Config.STACLE_Y,
                                    Config.STAREC_WIDTH / 2);
                            start.setColor(Config.STACLE_COLOR);
                            target = new Circle(Config.STACLE_X + distancePix,
                                    Config.STACLE_Y,
                                    widthPix / 2);
                            target.setColor(Config.TARCLE_COLOR);
                        } else {
                            //Calibration
                            //Set start circle to a fake one...
                            start = new Circle(0,
                                    0,
                                    0);

                            target = new Circle(0, 0, 0);
                        }

                        TrialInfo trial = new TrialInfo("SlicesInMiddle", null, null, 1, distancePix,
                                1, //block number, will be updated later
                                1, //trial in block, will be updated later
                                distancePix, //distance pix
                                widthPix, //width pix
                                this.pixelSizeMM,
                                new Circle(0, 0, 0),
                                target,
                                start,
                                cursorSize,
                                this.participantID,
                                testConstellation.getTestType(),
                                "fakeMovementDirection", rad, (rad.x+rad.y)/2, section
                        );

                        //sets watch boundaries to test, example: "SuedOst" we test if the start + targetDistance are not below "bot", and not greater than "max"

                        if (randomRadNum >= 0 && randomRadNum <= 90) {
                            trial.setMovementDirection(Config.MOVEMENT_BOT_RIGHT);
                            trial.setQuartile("SO");
                        } else if (randomRadNum >= 90 && randomRadNum <= 180) {
                            trial.setMovementDirection(Config.MOVEMENT_BOT_LEFT);
                            trial.setQuartile("SW");
                        } else if (randomRadNum >= 180 && randomRadNum <= 270) {
                            trial.setMovementDirection(Config.MOVEMENT_DIRECTION_LEFT);
                            trial.setQuartile("NW");
                        } else if (randomRadNum >= 270 && randomRadNum <= 360) {
                            trial.setMovementDirection(Config.MOVEMENT_DIRECTION_RIGTH);
                            trial.setQuartile("NO");
                        } else if (randomRadNum < 0) {
                            trial.setMovementDirection(Config.MOVEMENT_BOT_RIGHT);
                            trial.setQuartile("SO");
                        }
                        trials.add(trial);
                    }

                    if (cursorSize == 1.0) {
                        //Fake a CustomCursor for the default cursor!
                        //cursors.add(new CustomCursor(51, this.pixelSizeMM));
                    } else {
                        cursors.add(new CustomCursor(cursorSize, this.pixelSizeMM));
                    }
                }
            }
        }
    }

    public void generateTrialListSetOfTupels(){
        int section = -1;
        for (int i = 0; i < testConstellation.getNrRepetitions(); i++) {
            for (double cursorSize : cursorList) {
                for (Point2D.Double rad : points) {
                    section++;
                    for (Point.Double p : tupels) {

                        double lowBound = rad.x;
                        double uppBound = rad.y;

                        double randomRadNum = ThreadLocalRandom.current().nextDouble(lowBound, uppBound);

                        // Generate the trial list
                        int widthPix = convertMMtoPIX(p.x);
                        int distancePix = convertMMtoPIX(p.y);
                        Circle start = null;
                        Circle target = null;
                        if (testConstellation.getTestType().equals(Config.TEST_TYPE_FITTS)) {
                            start = new Circle(Config.STACLE_X,
                                    Config.STACLE_Y,
                                    Config.STAREC_WIDTH / 2);
                            start.setColor(Config.STACLE_COLOR);
                            target = new Circle(Config.STACLE_X + distancePix,
                                    Config.STACLE_Y,
                                    widthPix / 2);
                            target.setColor(Config.TARCLE_COLOR);
                        } else {
                            //Calibration
                            //Set start circle to a fake one...
                            start = new Circle(0,
                                    0,
                                    0);

                            target = new Circle(0, 0, 0);
                        }

                        TrialInfo trial = new TrialInfo("SlicesInMiddle", null, null, 1, distancePix,
                                1, //block number, will be updated later
                                1, //trial in block, will be updated later
                                distancePix, //distance pix
                                widthPix, //width pix
                                this.pixelSizeMM,
                                new Circle(0, 0, 0),
                                target,
                                start,
                                cursorSize,
                                this.participantID,
                                testConstellation.getTestType(),
                                "fakeMovementDirection", rad, (rad.x+rad.y)/2, section
                        );

                        //sets watch boundaries to test, example: "SuedOst" we test if the start + targetDistance are not below "bot", and not greater than "max"

                        if (randomRadNum >= 0 && randomRadNum <= 90) {
                            trial.setMovementDirection(Config.MOVEMENT_BOT_RIGHT);
                            trial.setQuartile("SO");
                        } else if (randomRadNum >= 90 && randomRadNum <= 180) {
                            trial.setMovementDirection(Config.MOVEMENT_BOT_LEFT);
                            trial.setQuartile("SW");
                        } else if (randomRadNum >= 180 && randomRadNum <= 270) {
                            trial.setMovementDirection(Config.MOVEMENT_DIRECTION_LEFT);
                            trial.setQuartile("NW");
                        } else if (randomRadNum >= 270 && randomRadNum <= 360) {
                            trial.setMovementDirection(Config.MOVEMENT_DIRECTION_RIGTH);
                            trial.setQuartile("NO");
                        } else if (randomRadNum < 0) {
                            trial.setMovementDirection(Config.MOVEMENT_BOT_RIGHT);
                            trial.setQuartile("SO");
                        }
                        trials.add(trial);
                    }

                    if (cursorSize == 1.0) {
                        //Fake a CustomCursor for the default cursor!
                        //cursors.add(new CustomCursor(51, this.pixelSizeMM));
                    } else {
                        cursors.add(new CustomCursor(cursorSize, this.pixelSizeMM));
                    }
                }
            }
        }
    }
}
