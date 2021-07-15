package dskm.gui;

import dskm.Config;
import dskm.experiment.Experimenter;
import dskm.experiment.Mologger;
import dskm.experiment.TrialInfo;
import io.reactivex.rxjava3.subjects.PublishSubject;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import static java.awt.Color.BLACK;
import static java.awt.Color.MAGENTA;

public class DrawingPanel extends JPanel implements MouseInputListener {

    // Two circles to draw
    private StartRectangle stCircle;
    private Circle stCircle2;
    private Color stCircleColor = Config.STACLE_COLOR;
    private Circle tgtCircle;

    // Text to draw
    private String blockInfoToDraw = "";
    private String textToDraw = "";

    private Graphics2D graphics2D;

    // Logging vars
    //private boolean startClicked = false;
    private boolean pressInStart = false;
    private boolean pressInTarget = false;
    private boolean drawCircles = false;

    TrialInfo currentTrialInfo = null;
    boolean trialIsRunning = false;
    String method = "";

    // Publishing all the movements
    private static PublishSubject<MouseEvent> mouseSubject;

    private int n; //n Circles

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    /***
     * Constructor
     */
    public DrawingPanel(int n, String method,boolean drawCircles) {
        addMouseListener(this);
        addMouseMotionListener(this);
        mouseSubject = PublishSubject.create();
        this.method = method;
        setN(n);
        this.drawCircles=drawCircles;
    }

    /**
     * Return the MotionSubject to any class interested!
     *
     * @return PublishSubject motionSubject
     */
    public static PublishSubject<MouseEvent> getMouseSubject() {
        return mouseSubject;
    }

    /***
     * Main printing function
     * @param graphics
     */
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        int winW = this.getWidth();
        int winH = this.getHeight();
        graphics2D = (Graphics2D) graphics;

        this.setBackground(Config.TASK_BACKGROUND_COLOR);

        // Set  anti-alias!
        graphics2D.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        if (Experimenter.methodType.equals("MethodB")) {
            if (drawCircles) {
                int a = MainFrame.getFrame().getWidth() / 2;
                int b = MainFrame.getFrame().getHeight() / 2;
                int m = Math.min(a, b);
                int r = 4 * m / 5;
                int r2 = Math.abs(m - r) / 2;
                System.out.println("****************");
                for (int i = 0; i < getN(); i++) {
                    double t = 2 * Math.PI * i / getN();
                    int x = (int) Math.round(a + r * Math.cos(t));
                    int y = (int) Math.round(b + r * Math.sin(t));
                    System.out.println(i + 1 + " posX: " + x);
                    System.out.println(i + 1 + " posY: " + y);
                    graphics2D.setColor(MAGENTA);
                    graphics2D.drawOval(x - stCircle2.getRadius(), y  - stCircle2.getRadius(),
                            stCircle2.getRadius(), stCircle2.getRadius());
                }
                System.out.println("****************");
            }
        }

        if (Experimenter.methodType.equals("MethodB") || Experimenter.methodType.equals("MethodA")) {

            //-- Draw circles
            if (currentTrialInfo.getTestType().equals(Config.TEST_TYPE_FITTS)) {
                // Start circle
                if (!trialIsRunning) {
                    graphics2D.setColor(Config.STACLE_COLOR);
                } else {
                    graphics2D.setColor(Config.STACLE_COLOR_CLICKED);
                }
                String startType = "";

                if (Experimenter.methodType.equals("MethodB")) {
                    startType = Config.START_BUTTON_SHAPE_CIRCLE;
                }
                // stCircle2.setRadius(MethodB.radius*2);
                if (startType.equals(Config.START_BUTTON_SHAPE_CIRCLE)) {
                    graphics2D.fillOval(stCircle2.getX(), stCircle2.getY(),
                            stCircle2.getRadius(), stCircle2.getRadius());
                    graphics2D.setColor(Color.cyan);
                    graphics2D.drawOval(stCircle2.getX(), stCircle2.getY(),
                            stCircle2.getRadius(), stCircle2.getRadius());
                } else {
                    //Draw a rectangle as start button
                    graphics2D.fillRect(stCircle.getX(),
                            stCircle.getY(),
                            stCircle.getWidth(),
                            stCircle.getHeight());
                    //graphics2D.setColor(Color.cyan);
                    graphics2D.drawRect(stCircle.getX(),
                            stCircle.getY(),
                            stCircle.getWidth(),
                            stCircle.getHeight());
                    //Draw start label in start button
                    if (!trialIsRunning) {
                        graphics2D.setColor(BLACK);
                    } else {
                        graphics2D.setColor(Config.STACLE_COLOR_CLICKED);
                    }
                    graphics2D.setFont(new Font(Config.FONT_STYLE, Font.PLAIN, 14));
                    graphics2D.drawString("Start", stCircle.getX() + 3, stCircle.getCenterY() + 5);
                }
            }
            //  Target circle
            if (!trialIsRunning) {
                graphics2D.setColor(Config.TARCLE_COLOR);
            } else {
                graphics2D.setColor(Config.TARCLE_COLOR_FREE);
            }
            if (Experimenter.methodType.equals("MethodA")) {
                graphics2D.fillOval(tgtCircle.getX(), tgtCircle.getY(),
                        tgtCircle.getSide(), tgtCircle.getSide());
            } else {
                graphics2D.fillOval(tgtCircle.getX(), tgtCircle.getY(),
                        tgtCircle.getRadius(), tgtCircle.getRadius());
            }
        }
        //System.out.println("Target position Draw: " + tgtCircle.getCenterX() + ", " + tgtCircle.getCenterY());

        //-- Draw text
        graphics2D.setColor(Config.TEXT_COLOR);
        graphics2D.setFont(new Font(Config.FONT_STYLE, Font.PLAIN, Config.FONT_SIZE));
        graphics2D.drawString(blockInfoToDraw, winW - Config.TEXT_X, Config.TEXT_Y);
        graphics2D.drawString(textToDraw, winW - Config.TEXT_X, Config.TEXT_Y + 30);
    }

    public void setCurrentTrialInfo(TrialInfo currentTrialInfo) {
        this.currentTrialInfo = currentTrialInfo;
        if (method.equals("MethodA")) {
            this.setCircles(currentTrialInfo.getStart(),
                    currentTrialInfo.getTarget());
        } else if (method.equals("MethodB")) {
            this.setCircles2(currentTrialInfo.getStartAsCircle(),
                    currentTrialInfo.getTarget());
        }
    }

    /***
     * Set the circles
     * @param c1 Circle1
     * @param c2 Circle2
     */
    public void setCircles(StartRectangle c1, Circle c2) {
        stCircle = c1;
        tgtCircle = c2;
    }

    public void setCircles2(Circle c1, Circle c2) {
        stCircle2 = c1;
        tgtCircle = c2;
    }

    /***
     * Set the text to draw
     * @param text Text
     */
    public void setTextToDraw(String text) {
        textToDraw = text;
    }

    public void setBlockInfoToDraw(String text) {
        this.blockInfoToDraw = text;
    }

    /**
     * Overriding the mouse click
     *
     * @param e MouseEvent
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (this.currentTrialInfo.getTestType().equals(Config.TEST_TYPE_FITTS)) {
            mousePressedFitts(e);
        } else {
            // calibration
            mousePressedCalib(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (this.currentTrialInfo.getTestType().equals(Config.TEST_TYPE_FITTS)) {
            try {
                mouseReleasedFitts(e);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } else {
            // calibration
            try {
                mouseReleasedCalib(e);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void mousePressedFitts(MouseEvent e) {
        //System.out.println("mouse pressed: " + e.getX() + ", " + e.getY());
        boolean isInStart;

        if (method.equals("MethodA")) {
            isInStart = stCircle.isInside(e.getX(), e.getY());
        } else {
            isInStart = stCircle2.isInside(e.getX(), e.getY());
        }

        boolean isInTarget = tgtCircle.isInside(e.getX(), e.getY());

        if (trialIsRunning) {
            //Interested in a press anywhere
            if (isInStart) {
                //Press in start
                pressInStart = true;
                pressInTarget = false;
                //System.out.println("Press in Start – Running PAY ATTENTION");
                currentTrialInfo.setPressPointXStart(e.getX());
                currentTrialInfo.setPressPointYStart(e.getY());
            } else if (isInTarget) {
                //Press in target
                pressInTarget = true;
                pressInStart = false;
                //System.out.println("Press in Target - Running PAY ATTENTION");
                currentTrialInfo.setPressPointXTarget(e.getX());
                currentTrialInfo.setPressPointYTarget(e.getY());
            } else {
                //A press elsewhere
                pressInStart = false;
                pressInTarget = false;
                //System.out.println("Press Elsewhere – Running PAY ATTENTION");
                currentTrialInfo.setPressPointXTarget(e.getX());
                currentTrialInfo.setPressPointYTarget(e.getY());
            }
        } else {
            //Only interested in a press inside the start button
            if (isInStart) {
                //Press in start
                pressInStart = true;
                pressInTarget = false;
                //System.out.println("Press in Start – Not running PAY ATTENTION");
                currentTrialInfo.setPressPointXStart(e.getX());
                currentTrialInfo.setPressPointYStart(e.getY());
            } else if (isInTarget) {
                //Press in target
                pressInTarget = true;
                pressInStart = false;
                //System.out.println("Press in Target - Not running IGNORE");
                Toolkit.getDefaultToolkit().beep();
            } else {
                //A press elsewhere
                pressInStart = false;
                pressInTarget = false;
                //System.out.println("Press Elsewhere – Not running IGNORE");
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }

    private void mousePressedCalib(MouseEvent e) {
        trialIsRunning = true; //always true in calibration
        boolean isInStart = stCircle.isInside(e.getX(), e.getY());
        boolean isInTarget = tgtCircle.isInside(e.getX(), e.getY());
        if (trialIsRunning) {
            //Interested in a press anywhere
            if (isInStart) {
                //Press in start
                pressInStart = true;
                pressInTarget = false;
                //System.out.println("Calib, should not happen Press in Start – Running PAY ATTENTION");
            } else if (isInTarget) {
                //Press in target
                pressInTarget = true;
                pressInStart = false;
                //System.out.println("Calib Press in Target - Running PAY ATTENTION");
            } else {
                //A press elsewhere
                pressInStart = false;
                pressInTarget = false;
                //System.out.println("Calib Press Elsewhere – Running PAY ATTENTION");
            }
            currentTrialInfo.setPressPointXStart(0);
            currentTrialInfo.setPressPointYStart(0);
            currentTrialInfo.setPressPointXTarget(e.getX());
            currentTrialInfo.setPressPointYTarget(e.getY());
        }
    }

    public void mouseReleasedFitts(MouseEvent e) throws IOException {
        boolean isInStart;
        if (method.equals("MethodA")) {
            isInStart = stCircle.isInside(e.getX(), e.getY());
        } else {
            isInStart = stCircle2.isInside(e.getX(), e.getY());
        }

        boolean isInTarget = tgtCircle.isInside(e.getX(), e.getY());
        if (!trialIsRunning) {
            if (pressInStart && isInStart) {
                //A match with the previous press in the start
                trialIsRunning = true;
                //System.out.println("Release in Start after press in start – START TRIAL");
                pressInStart = false;
                trialIsRunning = true;
                this.currentTrialInfo.setTrialStartTime(System.currentTimeMillis());
                Mologger.get().log(e);
                // Publish the event
                mouseSubject.onNext(e);

                // change the color of the start circle
                if (method.equals("MethodA")) {
                    stCircle.setColor(Config.STACLE_COLOR_CLICKED);
                } else {
                    stCircle2.setColor(Config.STACLE_COLOR_CLICKED);
                }
                tgtCircle.setColor(Config.TARCLE_COLOR_FREE);
                this.currentTrialInfo.setReleasePointXStart(e.getX());
                this.currentTrialInfo.setReleasePointYStart(e.getY());
                this.repaint();
            } else if (pressInStart && !isInStart) {
                //A mismatch with the previous press in start
                //System.out.println("Release outside Start after press in start – IGNORE");
                pressInStart = false;
                try {
                    Clip clip = AudioSystem.getClip();
                    clip.open(AudioSystem.getAudioInputStream(new File(Config.SOUND_PATH_ERROR)));
                    clip.start();
                } catch (Exception exc) {
                    exc.printStackTrace(System.out);
                }
            } else {
                //The previous press was not in start, ignore this release
                //System.out.println("Release outside Start after press outside start – IGNORE");
                pressInStart = false;
                pressInTarget = false;
            }
        } else {
            int hit = 0;
            if (pressInTarget && isInTarget) {
                //A match with the previous press in the target
                try {
                    Clip clip = AudioSystem.getClip();
                    clip.open(AudioSystem.getAudioInputStream(new File(Config.SOUND_PATH_SUCCESS)));
                    clip.start();
                } catch (Exception exc) {
                    exc.printStackTrace(System.out);
                }
                //System.out.println("Release in Target after press in target – END TRIAL IN TARGET");
                pressInTarget = false;
                hit = 1;
            } else if (pressInTarget && !isInTarget) {
                //The previous press was in target, this release outside target, log as miss
                //System.out.println("Release outside Target after press in target – END TRIAL OUTSIDE TARGET");
                pressInTarget = false;
                try {
                    Clip clip = AudioSystem.getClip();
                    clip.open(AudioSystem.getAudioInputStream(new File(Config.SOUND_PATH_ERROR)));
                    clip.start();
                } catch (Exception exc) {
                    exc.printStackTrace(System.out);
                }
            } else {
                //The previous press was outside target, this release also outside target, log as miss
                //System.out.println("Release outside Target after press outside target – END TRIAL OUTSIDE TARGET");
                try {
                    Clip clip = AudioSystem.getClip();
                    clip.open(AudioSystem.getAudioInputStream(new File(Config.SOUND_PATH_ERROR)));
                    clip.start();
                } catch (Exception exc) {
                    exc.printStackTrace(System.out);
                }
            }
            trialIsRunning = false;
            // Publish the click
            mouseSubject.onNext(e);
            Mologger.get().log(e);
            this.currentTrialInfo.setTrialEndTime(System.currentTimeMillis());
            this.currentTrialInfo.setReleasePointXTarget(e.getX());
            this.currentTrialInfo.setReleasePointYTarget(e.getY());
            this.currentTrialInfo.setHit(hit);

            Mologger.get().writeLogToFile(this.currentTrialInfo);
            // Go to the next experiment
            Experimenter.method.createTrial();

        }
    }

    private void mouseReleasedCalib(MouseEvent e) throws IOException {
        int hit = 0;
        boolean isInStart = stCircle.isInside(e.getX(), e.getY());
        boolean isInTarget = tgtCircle.isInside(e.getX(), e.getY());
        if (pressInTarget && isInTarget) {
            //A match with the previous press in the target
            try {
                Clip clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(new File(Config.SOUND_PATH_SUCCESS)));
                clip.start();
            } catch (Exception exc) {
                exc.printStackTrace(System.out);
            }
            //System.out.println("Calib Release in Target after press in target – END TRIAL IN TARGET");
            pressInTarget = false;
            hit = 1;
        } else if (pressInTarget && !isInTarget) {
            //The previous press was in target, this release outside target, log as miss
            //System.out.println("Calib Release outside Target after press in target – END TRIAL OUTSIDE TARGET");
            pressInTarget = false;
            try {
                Clip clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(new File(Config.SOUND_PATH_ERROR)));
                clip.start();
            } catch (Exception exc) {
                exc.printStackTrace(System.out);
            }
        } else {
            //The previous press was outside target, this release also outside target, log as miss
            //System.out.println("Release outside Target after press outside target – END TRIAL OUTSIDE TARGET");
            try {
                Clip clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(new File(Config.SOUND_PATH_ERROR)));
                clip.start();
            } catch (Exception exc) {
                exc.printStackTrace(System.out);
            }
        }
        trialIsRunning = false;
        // Publish the click
        mouseSubject.onNext(e);
        Mologger.get().log(e);
        this.currentTrialInfo.setTrialStartTime(System.currentTimeMillis());
        this.currentTrialInfo.setTrialEndTime(System.currentTimeMillis());
        this.currentTrialInfo.setReleasePointXTarget(e.getX());
        this.currentTrialInfo.setReleasePointYTarget(e.getY());
        this.currentTrialInfo.setHit(hit);

        Mologger.get().writeLogToFile(this.currentTrialInfo);
        // Go to the next experiment
        Experimenter.method.createTrial();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (trialIsRunning) {
            // Publish the movement
//            mouseSubject.onNext(e);
            try {
                Mologger.get().log(e);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

}
