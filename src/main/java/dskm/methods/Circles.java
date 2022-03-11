package dskm.methods;

import dskm.Constants;
import dskm.experiment.Constellation;
import dskm.experiment.LogChecker;
import dskm.experiment.TrialInfo;
import dskm.gui.*;
import io.reactivex.rxjava3.subjects.PublishSubject;
import org.javatuples.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

public class Circles extends Method {
    private final java.util.List<Point.Double> radDistList = new ArrayList<>();
    private int nTrials;
    private int trialNumInTest = 0;

    private final PublishSubject<String> expSubject;
    private final Constellation testConstellation = Constellation.FITTS_CURSORSIZE_2;
    private final String participantID = LogChecker.retNumbLog();

    private int blockNumber = 1;
    private final ArrayList<TrialInfo> trials;
    private final ArrayList<ArrayList<TrialInfo>> blocks;

    double pixelSizeMM;

    private final int n;

    private int pos = 0;

    private int countOfCirclesClicked = 0;

    public static double radius = 0;

    public int distBetCircle;

    public static int distBetCirclemm;

    public static double distGlobal;

    public boolean flag;

    private double firstDistVal;

    Pair<Integer,Integer> pair;

    static LinkedList<Double> radListL=new LinkedList<Double>();
    public static LinkedList <Double>distListL=new LinkedList<>();

    public Circles(int n, boolean flag) throws IOException {
        expSubject = PublishSubject.create();
        int monitorPPI = Toolkit.getDefaultToolkit().getScreenResolution();
        pixelSizeMM = 25.4 / monitorPPI;
        trials = new ArrayList<>();
        blocks = new ArrayList<>();
        this.n = n;
        Circles.radius = radius;
        this.distBetCircle = distBetCircle / 2;
        distBetCirclemm = convertMMtoPIX(this.distBetCircle);
        this.flag = flag;
    }

    public String getParticipantID() {
        return participantID;
    }

    public void fillDistList(){
        distListL.add(25.0);
        distListL.add(50.0);
    }

    public void fillRadList(){
        radListL.add(5.0);
        radListL.add(10.0);
        radListL.add(20.0);

        radListL.add(6.0);
        radListL.add(11.0);
        radListL.add(21.0);
    }

    public void methodSetup() {
        fillDistList();
        fillRadList();

        int len = distListL.size();
        Random random = new Random();
        int randomInt = random.nextInt(len);
        Double distance = distListL.get(randomInt);

        setFirstDistVal(distance);

        this.generateRadiusDistancePairs(distance);

        this.generateTrialList();

        this.addBlocks();

        this.blockNrLoop();

        setnTrials(blocks.size() * trials.size()); // Num. of trails = all the combinations (n x n)
    }

    private void setFirstDistVal(double distance) {
        this.firstDistVal=distance;
    }

    public double getFirstDistVal(){
        return firstDistVal;
    }

    @Override
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
            DrawingPanel exPanel = new DrawingPanel(getN(), "MethodB", flag);
            TrialInfo trialInfo = blocks.get(0).remove(0);

            //Find out where to put target and the start.
            Circle targetCircle;

            targetCircle = determineTargetPositionFitts(trialInfo);

            Circle previousTarget = new Circle(targetCircle.getCenterX(),
                    targetCircle.getCenterY(),
                    trialInfo.getWidthPix() / 2);

            System.out.println("trialInfo.getWidthPix(): " + trialInfo.getWidthPix());

            trialInfo.setTarget(new Circle(targetCircle.getCenterX(),
                    targetCircle.getCenterY(),
                    convertMMtoPIX(trialInfo.getWidthPix())));

            trialNumInTest++;
            trialInfo.setTrialNumInTest(trialNumInTest);

            exPanel.setCurrentTrialInfo(trialInfo);
            exPanel.setBlockInfoToDraw("Block: " + this.blockNumber +
                    " of " + (radListL.size()*distListL.size()));
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


    static int genIndex = 0;
    int localIndex = 0;

    public void generateRadiusDistancePairs(double distance) {

        radDistList.clear();

        int a = MainFrame.getFrame().getWidth() / 2;
        int b = MainFrame.getFrame().getHeight() / 2;
        if (genIndex == distListL.size()) {
            genIndex = 0;
        }
        if (localIndex == distListL.size()) {
            localIndex = 0;
        }
        int r = convertMMtoPIX(distance / 2);
        distGlobal = distListL.get(localIndex) / 2;
        distBetCirclemm = r;
        System.out.println("***---***");
        for (int i = 0; i < getN() * distListL.size(); i++) {
            double t = 2 * Math.PI * i / getN();
            int x = (int) Math.round(a + r * Math.cos(t));
            int y = (int) Math.round(b + r * Math.sin(t));
            System.out.println(i + ". " + "x:" + x + " " + "y:" + y);
            radDistList.add(new Point2D.Double(x, y));
        }
        System.out.println("***---***");
        genIndex++;
        localIndex++;
    }

    @Override
    public void generateTrialList() {

        Circle start = new Circle(0, 0, 0);

        Circle startAsCircle;
        Circle target;
        double cursorSize = 1.0;

        int widthPix;
        int distancePix = 0;
        int numbOfTrials = 0;

        for (double k : radListL) {
            if (numbOfTrials < getN()) {
                setRadius(k);
                for (int i = 0; i < radDistList.size(); i++) {
                    widthPix = (int) getRadius();
                    System.out.println(1 + i + "." + " widthPix: " + widthPix);
                    int posX = (int) radDistList.get(i).getX();
                    int posY = (int) radDistList.get(i).getY();
                    startAsCircle = new Circle(posX, posY, convertMMtoPIX(getRadius()));
                    int targetIndex = 0;

                    targetIndex++;
                    target = new Circle((int) radDistList.get(targetIndex).getX(), (int) radDistList.get(targetIndex).getY(), convertMMtoPIX(getRadius()));

                    //Fake a CustomCursor for the default cursor!
                    //cursors.add(new CustomCursor(51, this.pixelSizeMM));

                    TrialInfo trial = new TrialInfo("Circles", null, flag, getN(), distBetCirclemm,
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
                            "fakeMovementDirection", new Point2D.Double(0, 0), 0.0, 0
                    );

                    trials.add(trial);
                }
            }
        }
    }

    public void addBlocks() {
        for (int i = 0; i < testConstellation.getNrBlocks(); i++) {
            blocks.add(new ArrayList<>());
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

    int i = 0;
    int j = 0;
    int getOldX;
    int getOldY;
    int countIndex = 0;
    int countDistance = 0;
    double radiusL = 0;
    double distance=0;
    static int arrayIndex=0;
    static double[][] distAndRadArray=new double[12][2];
    boolean isNotDuplicate=false;
    int circleCount1=0;
    int circleCount2=1;

    private Circle determineTargetPositionFitts(TrialInfo trialInfo) {
        if(distance==0){distance=getFirstDistVal();}
        if(radiusL==0){
            int len2 = radListL.size();
            Random random2 = new Random();
            int randomInt2 = random2.nextInt(len2);
            radiusL= radListL.get(randomInt2);

            distAndRadArray[arrayIndex][0]=distance;
                    distAndRadArray[arrayIndex++][1]=radiusL;
        }

        if (countOfCirclesClicked == getN()) {
            circleCount1=0;
            circleCount2=1;
            JLabel label = new JLabel("Block " + this.blockNumber +
                    " out of " + (radListL.size()*distListL.size()) + " is finished!");
            label.setFont(new Font("Arial", Font.PLAIN, 18));
            JOptionPane.showMessageDialog(
                    MainFrame.getFrame(),
                    label,
                    "",
                    JOptionPane.INFORMATION_MESSAGE,
                    new ImageIcon(new BufferedImage(1, 1,
                            BufferedImage.TYPE_INT_ARGB)));
            this.blockNumber++;

            i = 0;
            pos = 0;
            countOfCirclesClicked = 0;

            if(distListL.size()==0){
                fillDistList();
            }

            if(radListL.size()==0){
                fillRadList();
            }

            while (isNotDuplicate==false) {
                if(distListL.size()==0){
                    fillDistList();
                }

                int len = distListL.size();
                Random random = new Random();
                int randomInt = random.nextInt(len);
                distance = distListL.get(randomInt);

                distAndRadArray[arrayIndex][0]=distance;
                System.out.println("DISTANCE:"+distance);
                if(radListL.size()==0){
                    fillRadList();
                }

                int len2 = radListL.size();
                Random random2 = new Random();
                int randomInt2 = random2.nextInt(len2);
                radiusL = radListL.get(randomInt2);

                distAndRadArray[arrayIndex][1]=radiusL;
                System.out.println("RADIUS:"+radiusL);
                if(containsDuplicate(distance,radiusL)==true){
                    isNotDuplicate=false;
                }else{
                    isNotDuplicate=true;
                    arrayIndex++;
                }
            }

            isNotDuplicate=false;

            generateRadiusDistancePairs(distance);

            System.out.println("****-------****");
            countIndex++;
            countDistance++;
            if (countDistance == distListL.size()) {
                countDistance = 0;
            }
        }

        trialInfo.setDistanceMM(distance);
        trialInfo.setDistancePix(convertMMtoPIX(distance));

        if (countIndex == distListL.size()) {
            j++;
            countIndex = 0;
        }

        if (j == radListL.size()) {
            j = 0;
        }


        trialInfo.setRealWidthPix((int) radiusL);
        trialInfo.setWidthPix((int) (radiusL / 2));

        countOfCirclesClicked++;

        System.out.println("j==" + j);
        System.out.println("Sta bi trebo dobit: " + trialInfo.getWidthPix());
        //System.out.println("i."+i);
        if (pos + radDistList.size() / 2 + 1 == radDistList.size()) {
            System.out.println("radDistList:"+radDistList.size());
            System.out.println("1POS="+pos);
            pos = pos - radDistList.size() / 2 + 2;
        }

        if(i%2==0){pos=5+circleCount1++;}
        else {
            pos=circleCount2++;
        }

        if (i == 0) {
            trialInfo.setStartAsCircle(new Circle((int) radDistList.get(0).getX(), (int) radDistList.get(0).getY(),
                    convertMMtoPIX(trialInfo.getWidthPix())));
        } else {
            trialInfo.setStartAsCircle(new Circle(getOldX, getOldY,
                    convertMMtoPIX(trialInfo.getWidthPix())));
        }

        getOldX = (int) radDistList.get(pos).getX();
        getOldY = (int) radDistList.get(pos).getY();

        i++;
        return new Circle((int) radDistList.get(pos).getX(), (int) radDistList.get(pos).getY(), convertMMtoPIX(trialInfo.getWidthPix()));

    }

    private boolean containsDuplicate(double distance, double radiusL) {
        boolean retVal=false;

        for(int i=0;i<arrayIndex;i++){
                if(distAndRadArray[i][0]==distance && distAndRadArray[i][1]==radiusL){
                    retVal=true;
                }
            System.out.println("false");
        }

        return retVal;
    }

    public int getN() {
        return n;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        Circles.radius = radius;
    }

    private int convertMMtoPIX(double dim) {
        return (int) (Math.rint(dim / this.pixelSizeMM));
    }

    private double convertPIXtoMM(int dim) {
        return Math.rint(dim * this.pixelSizeMM);
    }
}
