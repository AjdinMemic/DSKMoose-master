package dskm.experiment;

import dskm.Constants;
import dskm.gui.*;
import dskm.Config;
import io.reactivex.rxjava3.subjects.PublishSubject;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Experimenter {
    private Constellation testConstellation = Constellation.FITTS_CURSORSIZE_1;
    private String participantID = "P100"; // Participant's ID (Pa1, Pa2, ...)

    private static Experimenter self = null; // for singleton

    // General vars
    private int minX, maxX, minY, maxY;

    private List<Point.Double> radDistList = new ArrayList<>();
    private int nTrials;
    private int trialNum;
    private int trialNumInTest = 0;
    private Circle previousTarget = new Circle(0,0,0);

    private int blockNumber = 1;
    private ArrayList<TrialInfo> trials;
    private ArrayList<ArrayList<TrialInfo>> blocks;

    double pixelSizeMM;;
    ArrayList<CustomCursor> cursors = new ArrayList<CustomCursor>();

    private List<Double> radList;
    private List<Double> distList;
    private List<Double> cursorList;

    /**
     * Constructor
     */
    private Experimenter() {
        expSubject = PublishSubject.create();

        int monitorPPI = Toolkit.getDefaultToolkit().getScreenResolution();
        //System.out.println(Toolkit.getDefaultToolkit().getScreenSize());
        pixelSizeMM = 25.4/monitorPPI;
        trials = new ArrayList<TrialInfo>();
        blocks = new ArrayList<ArrayList<TrialInfo>>();
        radList = testConstellation.getRadList();
        distList = testConstellation.getDistList();
        cursorList = testConstellation.getCursorList();
    }

    /***
     * Start the experiment
     */
    public void startExperiment() {
        System.out.println("Experiment started.");

        // Generate all the pairs of radius/distance (using Point for int,int)
        for (double rad: radList) {
            for (double dist: distList) {
                radDistList.add(new Point.Double(rad, dist));
            }
        }

        for(int i = 0; i < testConstellation.getNrRepetitions(); i++) {
            for (double cursorSize : cursorList) {
                for (Point.Double p : radDistList) {
                    // Generate the trial list
                    int widthPix = convertMMtoPIX(p.x);
                    int distancePix = convertMMtoPIX(p.y);
                    StartRectangle start = null;
                    Circle target = null;
                    if(testConstellation.getTestType().equals(Config.TEST_TYPE_FITTS)){
                        start = new StartRectangle(Config.STACLE_X,
                                Config.STACLE_Y,
                                Config.STAREC_WIDTH,
                                (int)cursorSize + 20);
                        start.setColor(Config.STACLE_COLOR);
                        target = new Circle(Config.STACLE_X + distancePix,
                                Config.STACLE_Y,
                                widthPix/2);
                        target.setColor(Config.TARCLE_COLOR);
                    }else{
                        //Calibration
                        //Set start circle to a fake one...
                        start = new StartRectangle(0,
                                0,
                                0,
                                0);

                        target = new Circle(0,0,0);
                    }

                    TrialInfo trial = new TrialInfo(
                            1, //block number, will be updated later
                            1, //trial in block, will be updated later
                            distancePix, //distance pix
                            widthPix, //width pix
                            this.pixelSizeMM,
                            target,
                            start,
                            cursorSize,
                            this.participantID,
                            testConstellation.getTestType(),
                            "fakeMovementDirection"
                    );

                    //For Fitts, we need to duplicate each trial,
                    //so that we have one trial to the right, one
                    //to the right.
                    if(testConstellation.getTestType().equals(Config.TEST_TYPE_FITTS)){
                        trial.setMovementDirection(
                                Config.MOVEMENT_DIRECTION_RIGTH
                        );
                        TrialInfo directionCopy = trial.copyTrialInfo();
                        directionCopy.setMovementDirection(
                                Config.MOVEMENT_DIRECTION_LEFT
                        );
                        trials.add(directionCopy);
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

        for(int i = 0; i < testConstellation.getNrBlocks(); i++){
            blocks.add(new ArrayList<TrialInfo>());
        }

        int blockNr = 1;
        for(ArrayList<TrialInfo> trialArray : blocks){
            Collections.shuffle(trials);
            int trialInBlockNr = 1;
            for(TrialInfo ti: trials){
                ti.setBlockNumber(blockNr);
                ti.setTrialInBlock(trialInBlockNr);
                trialInBlockNr++;
                trialArray.add(ti.copyTrialInfo());
            }
            blockNr++;
        }

        nTrials = blocks.size() * trials.size(); // Num. of trails = all the combinations (n x n)

        // Create the first trial
        trialNum = 0;
        createTrial();

        // Set up the Mologger
        Mologger.get().setup(expSubject, nTrials);

        // Publish the start of the experiment (to every subscriber)
        System.out.println("Should emit " + Constants.MSSG_BEGIN_LOG);
        expSubject.onNext(Constants.MSSG_BEGIN_LOG);
    }

    /***
     * Create the drawing panel for the trial
     */
    public void createTrial() {
        if(blocks.get(0).size() == 0){
            //The running block was just finished
            blocks.remove(0);
            //Show Block-End Message for all blocks except for the
            //last block, then show Test-End Message.
            if(blocks.size() > 0){
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
        if(blocks.size() == 0){
            //The last block was just finished
            expSubject.onNext(Constants.MSSG_END_LOG);
            finishTestAndEnd();
        }else {// Create and send the panel to be drawn
            DrawingPanel exPanel = new DrawingPanel();
            trialNum++;
            TrialInfo trialInfo = blocks.get(0).remove(0);
            for(CustomCursor cc: cursors){
                if(cc.getSizeMM() == trialInfo.getCursorSizeMM()){
                    exPanel.setCursor(cc.getCursor());
                    break;
                }
            }

            //Find out where to put target and the start.
            Circle startCircle = null;
            Circle targetCircle;
            if(testConstellation.getTestType().equals(Config.TEST_TYPE_FITTS)){
                //For the Fitts task we need to consider both the
                //position of the target and the position of the start.
                targetCircle = determineTargetPositionFitts(trialInfo);
            }else {
                targetCircle = determineTargetPositionCalibration(trialInfo);
            }

            //Target position and start position are determined.
            //In case it is a calibration task, the start is fake.

            //Update previousTarget to the newly set new target.
            previousTarget = new Circle(targetCircle.getCenterX(),
                    targetCircle.getCenterY(),
                    trialInfo.getWidthPix()/2);
            trialInfo.setTarget(new Circle(targetCircle.getCenterX(),
                    targetCircle.getCenterY(),
                    trialInfo.getWidthPix()/2));
            trialNumInTest++;
            trialInfo.setTrialNumInTest(trialNumInTest);

            exPanel.setCurrentTrialInfo(trialInfo);
            exPanel.setBlockInfoToDraw("Block: " + this.blockNumber +
                    " of " + testConstellation.getNrBlocks());
            exPanel.setTextToDraw("Trial: " + trialInfo.getTrialInBlock() +
                    " of " + nTrials/testConstellation.getNrBlocks());

            MainFrame.getFrame().drawPanel(exPanel);
        }
    }

    private Circle determineTargetPositionFitts(TrialInfo trialInfo){
        //In case the window title bar is showing
        int windowTitleBarHeight = MainFrame.getFrame().getInsets().top;
        int min = 0;
        int max = 0;
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
                if(trialInfo.getMovementDirection().equals(Config.MOVEMENT_DIRECTION_RIGTH)){
                    if(xPos + trialInfo.getDistancePix() < max){
                        posOK = true;
                    }
                }else if(trialInfo.getMovementDirection().equals(Config.MOVEMENT_DIRECTION_LEFT)){
                    if(xPos - trialInfo.getDistancePix() > min){
                        posOK = true;
                    }
                }
            }
        }

        //Now find a suitable y-Position for the start.
        min = windowRec.y + Config.TEXT_Y + Config.TEXT_PAN_H + 20 +
                convertMMtoPIX(trialInfo.getCursorSizeMM() / 2) +
                trialInfo.getWidthPix() / 2;
        //System.out.println("Inset top: " + windowTitleBarHeight);
        max = windowRec.height - windowRec.y - 20 -
                windowTitleBarHeight -
                convertMMtoPIX(trialInfo.getCursorSizeMM() / 2) -
                trialInfo.getWidthPix() / 2;
        //System.out.println("max: " + max + " win: " + windowRec.getBounds());

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

        //Now we have a suitable x and y for the start circle.
        //Set the start for the trial.
        trialInfo.setStart(new StartRectangle(xPos, yPos,
                Config.STAREC_WIDTH,
                (int)trialInfo.getCursorSizePix() + 20));
        //fitta

        //Now we need to calculate the corresponding target position
        //based on the start position.
        if(trialInfo.getMovementDirection().equals(Config.MOVEMENT_DIRECTION_RIGTH)){
            xPos = xPos + trialInfo.getDistancePix();
        }else if(trialInfo.getMovementDirection().equals(Config.MOVEMENT_DIRECTION_LEFT)){
            xPos = xPos - trialInfo.getDistancePix();
        }
        //The yPos should be the same as for the start.
        //Accordingly, no need to find a new yPos.

        return new Circle(xPos, yPos,
                trialInfo.getWidthPix() / 2);
    }

    private Circle determineTargetPositionCalibration(TrialInfo trialInfo){
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
        //System.out.println("Inset top: " + windowTitleBarHeight);
        max = windowRec.height - windowRec.y - 20 -
                windowTitleBarHeight -
                convertMMtoPIX(trialInfo.getCursorSizeMM() / 2) -
                trialInfo.getWidthPix() / 2;
        //System.out.println("max: " + max + " win: " + windowRec.getBounds());

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

    private void finishTestAndEnd(){
        JLabel label =
                new JLabel("The test is finished. Many thanks " +
                        "for your participation!");
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        int input = JOptionPane.showOptionDialog(
                MainFrame.getFrame(),
                label,
                "",
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.INFORMATION_MESSAGE,
                new ImageIcon(new BufferedImage(1, 1,
                        BufferedImage.TYPE_INT_ARGB)),
                null,
                null);
        if(input == JOptionPane.OK_OPTION){
            System.exit(0);
        }
    }

    private int convertMMtoPIX(double dim){
        return (int)(Math.rint(dim/this.pixelSizeMM));
    }

    private double convertPIXtoMM(int dim){
        return Math.rint(dim*this.pixelSizeMM);
    }

    // For publishing the state of the experiment
    private PublishSubject<String> expSubject;

    /**
     * Get the instance
     * @return the singleton instance
     */
    public static Experimenter get() {
        if (self == null) self = new Experimenter();
        return self;
    }

    public PublishSubject<String> getExpSubject() {
        expSubject.subscribe(s -> System.out.println(s));
        return expSubject;
    }

    /***
     * Get the number of trials
     * @return Number of trials
     */
    public int getNTrials() {
        return nTrials;
    }

    /**
     * Get the participant ID
     * @return participant's ID
     */
    public String getParticipantID() {
        return participantID;
    }

    private int generateRandomPosition(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public String getTestType(){
        return testConstellation.getTestType();
    }

}
