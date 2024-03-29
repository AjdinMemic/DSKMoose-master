package dskm.experiment;

import dskm.Constants;
import dskm.gui.CustomCursor;
import dskm.gui.MainFrame;
import dskm.methods.*;
import io.reactivex.rxjava3.subjects.PublishSubject;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Experimenter {
    private Constellation testConstellation = Constellation.FITTS_CURSORSIZE_1;

    private static Experimenter self = null; // for singleton

    private ArrayList<TrialInfo> trials;
    private ArrayList<ArrayList<TrialInfo>> blocks;

    double pixelSizeMM;
    ;
    ArrayList<CustomCursor> cursors = new ArrayList<CustomCursor>();

    public static Method method;
    public static String methodType;

    /**
     * Constructor
     */
    private Experimenter() throws IOException {
        expSubject = PublishSubject.create();

        int monitorPPI = Toolkit.getDefaultToolkit().getScreenResolution();
        //System.out.println(Toolkit.getDefaultToolkit().getScreenSize());
        pixelSizeMM = 25.4 / monitorPPI;
        System.out.println("monitorPPI: " + monitorPPI);
        System.out.println("pixelSizeMM: " + pixelSizeMM);
        trials = new ArrayList<TrialInfo>();
        blocks = new ArrayList<ArrayList<TrialInfo>>();
        System.out.println(MainFrame.getFrame().getWidth());
    }

    /***
     * Start the experiment
     * @param methodType
     */
    public void startExperiment(String methodType) throws IOException {
        this.methodType = methodType;
        System.out.println("Experiment started.");

        if (methodType.equals("MethodA")) {
            method = new Horizontal(false);
            System.out.println("Method A");
        } else if (methodType.equals("MethodB")) {
            method = new Circles(8 , true); // radius and distBetCircle in mm! // distBetCirle = distance from center of c[N] to center c[N+length/2]
            System.out.println("Method B");
        } else if (methodType.equals("MethodC")) {
            method = new FixedSlices(true);
            System.out.println("Method C");
        } else if (methodType.equals("MethodC2")) {
            method = new Slices(8); // 2,4,6,8,10...
            System.out.println("Method C2");
        } else if (methodType.equals("MethodD")) {
            method = new Dropdown();
            System.out.println("Method D");
        } else if (methodType.equals("MethodE")) {
            method = new SlicesInMiddle(9,true );
            System.out.println("Method E");
        }

        method.methodSetup();

        // Create the first trial
        method.createTrial();

        // Set up the Mologger
        Mologger.get().setup(expSubject, method.getnTrials());

        // Publish the start of the experiment (to every subscriber)
        System.out.println("Should emit " + Constants.MSSG_BEGIN_LOG);
        expSubject.onNext(Constants.MSSG_BEGIN_LOG);
    }

    // For publishing the state of the experiment
    private PublishSubject<String> expSubject;

    /**
     * Get the instance
     *
     * @return the singleton instance
     */
    public static Experimenter get() throws IOException {
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
        return method.getnTrials();
    }

    /**
     * Get the participant ID
     *
     * @return participant's ID
     */
    public String getParticipantID() {
        return method.getParticipantID();
    }

    public String getTestType() {
        return testConstellation.getTestType();
    }

}
