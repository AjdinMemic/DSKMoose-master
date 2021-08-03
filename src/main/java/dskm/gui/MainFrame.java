package dskm.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import dskm.connection.*;
import dskm.experiment.Experimenter;


public class MainFrame extends JFrame {

    private static MainFrame frame;

    private Container container;

    private static Rectangle windowSize;

    private DrawingPanel drawingPanel;
    private StartPanel startPanel;

    private BufferedImage graphicsContext;


    public MainFrame() {
        this.setUndecorated(true);

        final AbstractAction escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent ae) {
                dispose();
            }
        };

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ESCAPE_KEY");
        getRootPane().getActionMap().put("ESCAPE_KEY", escapeAction);

        container = getContentPane();

        // Add the start panel
        startPanel = new StartPanel();
        container.add(startPanel, BorderLayout.CENTER);

        this.setTitle("Experiment!");
        this.pack();
    }

    public static MainFrame getFrame() {
        if (frame == null) frame = new MainFrame();
        return frame;
    }

    /***
     * Draw the passed panel
     * @param jp JPanel to draw
     */
    public void drawPanel(JPanel jp) {
        container.removeAll();
        container.add(jp, BorderLayout.CENTER);
        this.revalidate();
    }


    /***
     * Main method
     * @param args
     */
    public static void main(String[] args) {

        JFrame windowFrame = MainFrame.getFrame();
//        JPanel mainPanel = mainForm.panel1;

//        GraphicsDevice gd =
//                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

//        frame.setContentPane(mainPanel);
        windowFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

//        frame.setUndecorated(true);
//        frame.setPreferredSize(new Dimension(300, 200));
        windowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        windowFrame.setLocation(80, -1000);
//        windowFrame.pack();

//        gd.setFullScreenWindow(mainForm);
        windowFrame.setVisible(true);
        windowFrame.setResizable(false);


        // Start the server
        //MooseServer.get().start();

    }

}
