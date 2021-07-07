package dskm.experiment;

import dskm.Constants;
import dskm.Config;
import dskm.gui.DrawingPanel;
import io.reactivex.rxjava3.subjects.PublishSubject;

import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Mologger {

    private static Mologger self; // Singleton
    private List<Molog> logList = new ArrayList<>();
    private List<MouseEvent>[] logDB;

    private PrintWriter logFile;

    /***
     * Constructor
     */
    private Mologger() {
        int nTrials = Experimenter.get().getNTrials();
        logDB = new List[nTrials + 1]; // Start from 1
        for (int i = 0; i < nTrials + 1; i++) {
            logDB[i] = new ArrayList<MouseEvent>();
        }
        //System.out.println("DAHugo");
    }


    /***
     * Singleton get instance
     * @return self
     */
    public static Mologger get() {
        if (self == null) self = new Mologger();
        return self;
    }

    public void setup(PublishSubject<String> expSubject, int nTrials) {
        expSubject.subscribe(state -> {
            switch (state) {
                case Constants.MSSG_BEGIN_LOG: // Start logging
                    System.out.println("Logging started...");
                    // Create the log file
                    createLogFile();
                    // Subscribe to events from DrawinPanel
//                    DrawingPanel.getMouseSubject().subscribe(event -> {
//                        System.out.println(event.paramString());
//                        log(event);
//                    });
                    break;
                case Constants.MSSG_END_LOG: // End logging
                    // Finish the logging and close the file
                    if (logFile != null) {
                        logFile.close();
                        System.out.println("Log file closed.");
                    }
                    break;
            }
        });

    }

    /**
     * Create the log file
     */
    private void createLogFile() {
        try {
            logFile = new PrintWriter(new FileWriter(
                    Config.LOG_PATH +
                            Config.LOG_FILE_NAME_EVENTS +
                            Experimenter.get().getParticipantID() +
                            ".txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Log a MouseEvent
     *
     * @param e MouseEvent
     */
    public void log(MouseEvent e) {
        //System.out.println(e.paramString());
        // Write the info to the file
        if (logFile != null) logFile.println(e.paramString());
    }

    /***
     * Put the log in the list
     * @param mlg Log
     */
    public void log(Molog mlg) {
        logList.add(mlg);
    }

    /***
     * Log an event
     * @param trialNum Trial number
     * @param me MouseEvent
     */
    public void log(int trialNum, MouseEvent me) {
        logDB[trialNum].add(me);
    }

    /***
     * Write the log to the file
     */
    public void writeLogToFile(TrialInfo toLog) {
        try {
            // Create file
            String fileName = Config.LOG_PATH +
                    Experimenter.get().getTestType() + "_" +
                    "Cursor" + (int) toLog.getCursorSizeMM() + "_" +
                    toLog.getParticipantID() +
                    ".txt";
            String s = toLog.toLogString() + "\n";
            Path path = Paths.get(fileName);

            if (!TrialInfo.isHeaderLineIsWritten()) {
                String headerLine = TrialInfo.getHeaderLine();
                Files.write(path, headerLine.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);
            }

            Files.write(path, s.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Problem in Writing log to the file");
        }
    }
}
