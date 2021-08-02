package dskm.methods;

import dskm.gui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Method {
    /**
     * This 6 methods are called by all sub-classes in the Experimenter class
     */
    public abstract void generateRadiusDistancePairs();

    public abstract void generateTrialList();

    public abstract void blockNrLoop();

    public abstract void addBlocks();

    public abstract void methodSetup();

    public abstract void createTrial();

    void finishTestAndEnd() {
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
        if (input == JOptionPane.OK_OPTION) {
            System.exit(0);
        }
    }

    public abstract int getnTrials();

    public String getParticipantID() {
        return "dummyId";
    }
}
