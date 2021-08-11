package dskm.experiment;

import com.sun.jdi.IntegerValue;
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

    public static PrintWriter logFile;

    /***
     * Constructor
     */
    private Mologger() throws IOException {
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
    public static Mologger get() throws IOException {
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

        Mologger.logFile.println("MOUSE MOVEMENT;X;Y;trialNumInTest;mouse button;time (System.currentTimeMillis());current time long");
        logFile.flush();
    }

    /**
     * Log a MouseEvent
     *
     * @param e MouseEvent
     */

    static long oldTime=0;
    static long newTime=0;

    static double timeStopWatchStart=0;
    static double timeStopWatchEnd=0;

    public void log(MouseEvent e,String s,int trialNr,String time) {
        if(timeStopWatchStart==0){
            timeStopWatchStart=splitStopWatch(time);
        }
        timeStopWatchEnd=splitStopWatch(time);
        double time1=timeStopWatchEnd-timeStopWatchStart;
        timeStopWatchStart=timeStopWatchEnd;

        if(oldTime==0 || s.equals("PRESSED IN START")){
            oldTime=System.currentTimeMillis();
        }
        newTime=System.currentTimeMillis();
        int time2= (int) (newTime-oldTime);
        oldTime=newTime;


        String button="";
        int leftAndRight = MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK;
        int leftAndMiddle= MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON2_DOWN_MASK;
        int rigthAndMiddle= MouseEvent.BUTTON3_DOWN_MASK | MouseEvent.BUTTON2_DOWN_MASK;

            if((e.getModifiersEx() & leftAndRight)==leftAndRight){
                button="Left + Right";
            }else if((e.getModifiersEx()&leftAndMiddle)==leftAndMiddle){
                button="Left + Middle";
            }else if((e.getModifiersEx()&rigthAndMiddle)==rigthAndMiddle){
                button="Right + Middle";
            }else if((e.getModifiersEx()&MouseEvent.BUTTON1_DOWN_MASK)==MouseEvent.BUTTON1_DOWN_MASK){
                button="Left";
            }else if((e.getModifiersEx()&MouseEvent.BUTTON2_DOWN_MASK)==MouseEvent.BUTTON2_DOWN_MASK){
                button="Middle";
            }else if((e.getModifiersEx()&MouseEvent.BUTTON3_DOWN_MASK)==MouseEvent.BUTTON3_DOWN_MASK){
                button="Right";
            }

            if(s.equals("MOVED")){
                button="M";
            }
            if(s.equals("RELEASED")){
            button="R";
            }

        StringBuilder str = new StringBuilder(200);
        str.append(s).append(";").append(e.getX()).append(";").append(e.getY()).append(";").append(trialNr).append(";").append(button).append(";").append(time2).append(";").append(newTime);
        if(logFile != null) logFile.println(str.toString());
        logFile.flush();


    }

   // public void log(String s){
        /*StringBuilder str = new StringBuilder(400);
        str.append(s).append(";").append(s).append(";").append(s).append(";");
        if(logFile != null) logFile.println(str);*/
 //   }

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

    public double splitStopWatch(String s){
        String[] arrSplit = s.split(" ");
        String numb=arrSplit[0];
        double retVal=Double.valueOf(numb);

        return retVal;
    }
}
