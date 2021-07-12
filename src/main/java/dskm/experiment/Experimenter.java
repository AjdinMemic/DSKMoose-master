package dskm.experiment;

import dskm.Constants;
import dskm.gui.CustomCursor;
import dskm.methods.Method;
import dskm.methods.MethodA;
import dskm.methods.MethodB;
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
        trials = new ArrayList<TrialInfo>();
        blocks = new ArrayList<ArrayList<TrialInfo>>();
    }

    /***
     * Start the experiment
     * @param methodType
     */
    public void startExperiment(String methodType) throws IOException {
        this.methodType=methodType;
        System.out.println("Experiment started.");

        if (methodType.equals("MethodA")) {
            method = new MethodA();
            System.out.println("Method A");
        } else if (methodType.equals("MethodB")) {
            method = new MethodB(8);
            System.out.println("Method B");
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
