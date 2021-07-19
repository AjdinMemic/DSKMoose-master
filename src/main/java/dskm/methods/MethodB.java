package dskm.methods;

import dskm.Config;
import dskm.Constants;
import dskm.experiment.Constellation;
import dskm.experiment.LogChecker;
import dskm.experiment.TrialInfo;
import dskm.gui.*;
import io.reactivex.rxjava3.subjects.PublishSubject;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MethodB extends Method {
    private final java.util.List<Point.Double> radDistList = new ArrayList<>();
    private int nTrials;
    private int trialNumInTest = 0;

    private final PublishSubject<String> expSubject;
    private final Constellation testConstellation = Constellation.FITTS_CURSORSIZE_1;
    private final String participantID = LogChecker.retNumbLog();

    private int blockNumber = 1;
    private final ArrayList<TrialInfo> trials;
    private final ArrayList<ArrayList<TrialInfo>> blocks;

    double pixelSizeMM;

    private final int n;

    private int pos=0;

    private int countOfCirclesClicked=0;

    public static int radius=0;

    public int distBetCircle;

    public static int distBetCirclemm;

    public boolean flag;

    public int[]radList={10,25,5};
    public static int[]distList={125,250,62};

    public MethodB(int n,int radius,int distBetCircle,boolean flag) throws IOException {
        expSubject = PublishSubject.create();
        int monitorPPI = Toolkit.getDefaultToolkit().getScreenResolution();
        //System.out.println(Toolkit.getDefaultToolkit().getScreenSize());
        pixelSizeMM = 25.4 / monitorPPI;
        trials = new ArrayList<>();
        blocks = new ArrayList<>();
        this.n=n;
        MethodB.radius =radius;
        this.distBetCircle=distBetCircle/2;
        distBetCirclemm=convertMMtoPIX(this.distBetCircle);
        this.flag=flag;
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
            DrawingPanel exPanel = new DrawingPanel(getN(),"MethodB",flag);
            TrialInfo trialInfo = blocks.get(0).remove(0);

            //Find out where to put target and the start.
            Circle targetCircle;

            targetCircle = determineTargetPositionFitts(trialInfo);

            Circle previousTarget = new Circle(targetCircle.getCenterX(),
                    targetCircle.getCenterY(),
                    trialInfo.getWidthPix() / 2);

            System.out.println("trialInfo.getWidthPix(): "+trialInfo.getWidthPix());

            trialInfo.setTarget(new Circle(targetCircle.getCenterX(),
                    targetCircle.getCenterY(),
                    convertMMtoPIX(trialInfo.getWidthPix())));

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


    static int genIndex=0;

    public void generateRadiusDistancePairs() {

        radDistList.clear();

        int a = MainFrame.getFrame().getWidth() / 2;
        int b = MainFrame.getFrame().getHeight() / 2;
        if(genIndex==distList.length){genIndex=0;}
        int r = convertMMtoPIX(distList[genIndex]/2);
        distBetCirclemm=r;

         for (int i = 0; i < getN()*distList.length; i++) {
            double t = 2 * Math.PI * i / getN();
            int x = (int) Math.round(a + r * Math.cos(t));
            int y = (int) Math.round(b + r * Math.sin(t));

            radDistList.add(new Point2D.Double(x,y));
        }
        genIndex++;
    }

    @Override
    public void generateTrialList() {

        StartRectangle start=new StartRectangle(0,0,0,0);

        Circle startAsCircle;
        Circle target;
        double cursorSize=1.0;

        int widthPix;
        int distancePix = 0;

        for (int k : radList) {
            setRadius(k);
            for (int i = 0; i < radDistList.size(); i++) {
                widthPix = getRadius();
                System.out.println(1 + i + "." + " widthPix: " + widthPix);
                int posX = (int) radDistList.get(i).getX();
                int posY = (int) radDistList.get(i).getY();
                startAsCircle = new Circle(posX, posY, convertMMtoPIX(getRadius()));
                int targetIndex = 0;

                targetIndex++;
                target = new Circle((int) radDistList.get(targetIndex).getX(), (int) radDistList.get(targetIndex).getY(), convertMMtoPIX(getRadius()));

                //Fake a CustomCursor for the default cursor!
                //cursors.add(new CustomCursor(51, this.pixelSizeMM));

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

    int i=0;
    int j=0;
    int getOldX;
    int getOldY;
    int countIndex=0;

    private Circle determineTargetPositionFitts(TrialInfo trialInfo) {

        if(countOfCirclesClicked==getN()){
            i = 0;
            pos = 0;
            countOfCirclesClicked=0;
            generateRadiusDistancePairs();
            countIndex++;
        }

        if(countIndex==radList.length){
            j++;
            countIndex=0;
        }

        if(j==radList.length){
            j=0;
        }
            trialInfo.setWidthPix(radList[j]/2);

        countOfCirclesClicked++;

        System.out.println("j=="+j);
        System.out.println("Sta bi trebo dobit: "+trialInfo.getWidthPix());
        //System.out.println("i."+i);
        if(pos+radDistList.size()/2+1==radDistList.size()){   pos=pos-radDistList.size()/2+2;}
        if(i%2==0){

            if(pos+radDistList.size()/2+1<=radDistList.size()){
                if(pos+radDistList.size()/2+1==radDistList.size()){
                    pos=pos+radDistList.size()/2;
                }else {
                pos=pos+radDistList.size()/2+1;}// z.B. i=0 size=9, pos=4
            }else{
                pos=(pos+radDistList.size()/2)-radDistList.size();
            }
        }else {
            pos=pos-radDistList.size()/2;
        }

        if(i==0) {
            trialInfo.setStartAsCircle(new Circle((int) radDistList.get(0).getX(), (int) radDistList.get(0).getY(),
                    convertMMtoPIX(trialInfo.getWidthPix())));
        }else {
            trialInfo.setStartAsCircle(new Circle(getOldX, getOldY,
                    convertMMtoPIX(trialInfo.getWidthPix())));
        }

        getOldX= (int) radDistList.get(pos).getX();
        getOldY= (int) radDistList.get(pos).getY();

        i++;
        return new Circle((int) radDistList.get(pos).getX(), (int) radDistList.get(pos).getY(), convertMMtoPIX(trialInfo.getWidthPix()));

    }

    public int getN() {
        return n;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        MethodB.radius = radius;
    }

    private int convertMMtoPIX(double dim) {
        return (int) (Math.rint(dim / this.pixelSizeMM));
    }
}
