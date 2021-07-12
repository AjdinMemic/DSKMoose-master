package dskm.methods;

import com.google.common.collect.ImmutableList;
import dskm.Config;
import dskm.Constants;
import dskm.experiment.Constellation;
import dskm.experiment.LogChecker;
import dskm.experiment.TrialInfo;
import dskm.gui.*;
import io.reactivex.rxjava3.subjects.PublishSubject;
import org.checkerframework.checker.units.qual.C;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MethodB extends Method {
    private java.util.List<Point.Double> radDistList = new ArrayList<>();
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

    private List<Double> cursorList;

    private int n;

    private int count=0;

    private Point point;

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public MethodB(int n) throws IOException {
        expSubject = PublishSubject.create();
        int monitorPPI = Toolkit.getDefaultToolkit().getScreenResolution();
        //System.out.println(Toolkit.getDefaultToolkit().getScreenSize());
        pixelSizeMM = 25.4 / monitorPPI;
        trials = new ArrayList<TrialInfo>();
        blocks = new ArrayList<ArrayList<TrialInfo>>();
        cursorList = testConstellation.getCursorList();
        this.n=n;
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

    @Override
    public void createTrial() {
        for(int i=0;i<radDistList.size();i++) {
            System.out.println((i+1)+" x: "+radDistList.get(i).x);
            System.out.println((i+1)+" y: "+radDistList.get(i).y);
        }
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
            DrawingPanel exPanel = new DrawingPanel(getN(),"MethodB");
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

    public int getnTrials() {
        return nTrials;
    }

    public void setnTrials(int nTrials) {
        this.nTrials = nTrials;
    }



    public void generateRadiusDistancePairs() {
        // Generate all the pairs of radius/distance (using Point for int,int)
        int a = MainFrame.getFrame().getWidth() / 2;
        int b = MainFrame.getFrame().getHeight() / 2;
        int m = Math.min(a, b);
        int r = 4 * m / 5;
        int r2 = Math.abs(m - r) / 2;

         for (int i = 0; i < getN(); i++) {
            double t = 2 * Math.PI * i / getN();
            int x = (int) Math.round(a + r * Math.cos(t));
            int y = (int) Math.round(b + r * Math.sin(t));

            radDistList.add(new Point2D.Double(x-r2,y-r2));
        }
    }

    @Override
    public void generateTrialList() {

        StartRectangle start=new StartRectangle(0,0,0,0);

        Circle startAsCircle  = null;
        Circle target = null;
        Double cursorSize=1.0;

        for(int i=0;i<radDistList.size();i++) {
            int posX = (int) radDistList.get(i).getX();
            System.out.println(i+1+" posX: "+posX);
            int posY = (int) radDistList.get(i).getY();
            System.out.println(i+1+" posY: "+posY);
            startAsCircle = new Circle(posX, posY, 20);
            int targetIndex=0;

            if((i+radDistList.size()/2+1)>radDistList.size()){
                targetIndex=(i+radDistList.size()/2+1)-radDistList.size(); // z.B. size=8, i=4, targetIndex = 4 + 5 > 8 true -> targetIndex= 9 - 8 = 1
            }
            target = new Circle((int) radDistList.get(targetIndex).getX(), (int) radDistList.get(targetIndex).getY(), 20);

            if (cursorSize == 1.0) {
                //Fake a CustomCursor for the default cursor!
                //cursors.add(new CustomCursor(51, this.pixelSizeMM));
            } else {
                cursors.add(new CustomCursor(cursorSize, this.pixelSizeMM));
            }

            int widthPix = 100;
            int distancePix = 0;

            TrialInfo trial = new TrialInfo(
                    1, //block number, will be updated later
                    1, //trial in block, will be updated later
                    distancePix, //distance pix
                    widthPix, //width pix
                    this.pixelSizeMM,
                    startAsCircle,
                    target,
                    start,
                    cursorSize,
                    this.participantID,
                    testConstellation.getTestType(),
                    "fakeMovementDirection"
            );

            trials.add(trial);
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

    private Circle determineTargetPositionFitts(TrialInfo trialInfo) {

        trialInfo.setStartAsCircle(new Circle((int) radDistList.get(0 + count).getX(), (int) radDistList.get(0 + count).getY(),
                (int) trialInfo.getCursorSizePix() + 10));
        return new Circle((int) radDistList.get(count++).getX(), (int) radDistList.get(count).getY(), 5);

    }

}
