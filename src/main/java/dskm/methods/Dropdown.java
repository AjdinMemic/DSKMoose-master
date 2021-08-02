package dskm.methods;
//https://www3.ntu.edu.sg/home/ehchua/programming/index.html

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

public class Dropdown extends Method {

    private List<Point.Double> radDistList = new ArrayList<>();
    private int nTrials;
    private int trialNum;
    private int trialNumInTest = 0;
    private Circle previousTarget = new Circle(0, 0, 0);

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
    private List<Double> cursorList;

    private String[] quartiles = {"VER"};

    public Dropdown() throws IOException {
        expSubject = PublishSubject.create();
        int monitorPPI = Toolkit.getDefaultToolkit().getScreenResolution();

        pixelSizeMM = 25.4 / monitorPPI;
        trials = new ArrayList<TrialInfo>();
        blocks = new ArrayList<ArrayList<TrialInfo>>();
        radList = testConstellation.getRadList();
        distList = testConstellation.getDistList();
        cursorList = testConstellation.getCursorList();

    }

    public String getParticipantID() {
        return participantID;
    }

    public void methodSetup() {
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
        for (int i = 0; i < testConstellation.getNrRepetitions(); i++) {
            for (double cursorSize : cursorList) {
                for (String q : quartiles) {
                    for (Point.Double p : radDistList) {
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

                        TrialInfo trial = new TrialInfo("Method D", q, null, 1, distancePix,
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
                                "fakeMovementDirection", new Point2D.Double(0, 0), 0.0, 0
                        );

                        //For Fitts, we need to duplicate each trial,
                        //so that we have one trial to the right, one
                        //to the right.
                        if (trial.getQuartile().equals("NO") || trial.getQuartile().equals("SO")) {
                            trial.setMovementDirection(Config.MOVEMENT_DIRECTION_RIGTH);
                        } else if (trial.getQuartile().equals("NW") || trial.getQuartile().equals("SW")) {
                            trial.setMovementDirection(Config.MOVEMENT_DIRECTION_LEFT);
                        }
                        if (trial.getQuartile().equals("HOR") || trial.getQuartile().equals("VER")) {
                            if (testConstellation.getTestType().equals(Config.TEST_TYPE_FITTS)) {
                                trial.setMovementDirection(
                                        Config.MOVEMENT_DIRECTION_RIGTH
                                );
                                TrialInfo directionCopy = trial.copyTrialInfo();
                                directionCopy.setMovementDirection(
                                        Config.MOVEMENT_DIRECTION_LEFT
                                );
                                // trials.add(directionCopy);
                            }
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
            DrawingPanel exPanel = new DrawingPanel(0, "Dropdown", false);
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
                targetCircle = determineTargetPositionFitts(trialInfo);
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
    int horCount = 0;
    int verCount = 0;

    private Circle determineTargetPositionFitts(TrialInfo trialInfo) {
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
        System.out.println("windowRec.y: " + windowRec.y);
        System.out.println("top: " + top);
        bot = windowRec.y + 20 + convertMMtoPIX(trialInfo.getCursorSizeMM() / 2) + (trialInfo.getWidthPix() / 2);
        System.out.println("bot: " + bot);
        int xTop = generateRandomPosition(bot, top);
        xPos = 0;

        //Make sure the selected xPos is more than cursor size away
        //from the xPosition of the previous target, otherwise the
        //new start position might be under the cursor position.

        if (trialInfo.getQuartile().equals("VER")) {
            yPos = trialInfo.getStart().getRadius();
            System.out.println("yPos: " + yPos);

            //Now find a suitable y-Position for the start.
            bot = windowRec.y - Config.TEXT_Y + Config.TEXT_PAN_H + 20 +
                    convertMMtoPIX(trialInfo.getCursorSizeMM() / 2) +
                    trialInfo.getWidthPix() / 2;
            System.out.println("bot2: " + bot);
            top = windowRec.height - windowRec.y - 20 -
                    windowTitleBarHeight -
                    convertMMtoPIX(trialInfo.getCursorSizeMM() / 2) -
                    trialInfo.getWidthPix() / 2;
            System.out.println("top2: " + top);

            posOK = false;
            xPos = 0;
            //Make sure the selected yPos is more than cursor size away
            //from the yPosition of the previous target, otherwise the
            //new target might be under the cursor position.
            while (!posOK) {
                xPos = generateRandomPosition(bot, top);
                int distanceToPrevious = (int) trialInfo.calculateEucDistance("pix",
                        new Point(previousTarget.getCenterX(), xPos),
                        new Point(previousTarget.getCenterX(),
                                previousTarget.getCenterY()));
                if (distanceToPrevious > (20 + trialInfo.getCursorSizePix())) {
                    posOK = true;
                }
            }
            System.out.println("xPos: " + xPos);
        }

        //Now we have a suitable x and y for the start circle.
        //Set the start for the trial.
        trialInfo.setStart(new Circle(xPos, yPos,
                Config.STAREC_WIDTH / 2));
        //fitta

        //Now we need to calculate the corresponding target position
        //based on the start position.
        if (trialInfo.getQuartile().equals("VER")) {
            if (trialInfo.getMovementDirection().equals(Config.MOVEMENT_DIRECTION_RIGTH)) {
                yPos = yPos + trialInfo.getDistancePix();
            } else if (trialInfo.getMovementDirection().equals(Config.MOVEMENT_DIRECTION_LEFT)) {
                yPos = yPos - trialInfo.getDistancePix();
            }
        }

        //The yPos should be the same as for the start.
        //Accordingly, no need to find a new yPos.
        count++;
        return new Circle(xPos, yPos,
                trialInfo.getWidthPix() / 2);
    }

    private int generateRandomPosition(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

}
