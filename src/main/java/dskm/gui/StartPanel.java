package dskm.gui;

import dskm.experiment.Experimenter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class StartPanel extends JPanel {

    StartPanel() {
        final String[] methodType = {""};

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // Start button
        JButton startButton = new JButton("Start Experiment");
        startButton.setFont(new Font("Sans", Font.PLAIN, 14));
        startButton.setBounds(50, 50, 100, 100);
        startButton.setMaximumSize(new Dimension(250, 50));
        startButton.setAlignmentX(CENTER_ALIGNMENT);

        startButton.setEnabled(false); // Button is disabled until one of the Method is chosen

        startButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // Pass the control to the Experimenter
                try {
                    Experimenter.get().startExperiment(methodType[0]);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        // Radio buttons

        JRadioButton RBmethodA = new JRadioButton("Method A");
        RBmethodA.setActionCommand("Method A");
        RBmethodA.setSelected(true);

        JRadioButton RBmethodB = new JRadioButton("Method B");
        RBmethodB.setMnemonic(KeyEvent.VK_C);
        RBmethodB.setActionCommand("Method B");

        JRadioButton RBmethodC = new JRadioButton("Method C");
        RBmethodB.setMnemonic(KeyEvent.VK_C);
        RBmethodB.setActionCommand("Method C");

        //Register a listener for the radio buttons.
        RBmethodA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                methodType[0] ="MethodA";
                startButton.setEnabled(true);
            }
        });
        RBmethodB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                methodType[0] ="MethodB";
                startButton.setEnabled(true);
            }
        });
        RBmethodC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                methodType[0] ="MethodC";
                startButton.setEnabled(true);
            }
        });

        //Group the radio buttons.
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(RBmethodA);
        buttonGroup.add(RBmethodB);
        buttonGroup.add(RBmethodC);
        // Hint label
        JLabel textLabel = new JLabel("Click to start the experiment");
        textLabel.setAlignmentX(CENTER_ALIGNMENT);
        textLabel.setFont(new Font("Sans", Font.PLAIN, 14));

        this.add(Box.createRigidArea(new Dimension(0, 200)));
        this.add(textLabel);
        this.add(RBmethodA);
        this.add(RBmethodB);
        this.add(RBmethodC);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
        this.add(startButton);
    }

}
