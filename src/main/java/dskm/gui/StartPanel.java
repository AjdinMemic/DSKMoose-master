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

        JRadioButton RBmethodA = new JRadioButton("Horizontal");
        RBmethodA.setActionCommand("Method A");
        RBmethodA.setSelected(true);

        JRadioButton RBmethodB = new JRadioButton("Circles");
        RBmethodB.setMnemonic(KeyEvent.VK_C);
        RBmethodB.setActionCommand("Method B");

        JRadioButton RBmethodC = new JRadioButton("Fixed slices");
        RBmethodC.setMnemonic(KeyEvent.VK_C);
        RBmethodC.setActionCommand("Method C");

        JRadioButton RBmethodC2 = new JRadioButton("Slices");
        RBmethodC2.setMnemonic(KeyEvent.VK_C);
        RBmethodC2.setActionCommand("Method C2");

        JRadioButton RBmethodD = new JRadioButton("Dropdown");
        RBmethodD.setMnemonic(KeyEvent.VK_C);
        RBmethodD.setActionCommand("Method D");

        JRadioButton RBmethodE = new JRadioButton("SlicesInMiddle");
        RBmethodE.setMnemonic(KeyEvent.VK_C);
        RBmethodE.setActionCommand("Method E");


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
        RBmethodC2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                methodType[0] ="MethodC2";
                startButton.setEnabled(true);
            }
        });
        RBmethodD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                methodType[0] ="MethodD";
                startButton.setEnabled(true);
            }
        });
        RBmethodE.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                methodType[0] ="MethodE";
                startButton.setEnabled(true);
            }
        });
        //Group the radio buttons.
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(RBmethodA);
        buttonGroup.add(RBmethodB);
        buttonGroup.add(RBmethodC);
        buttonGroup.add(RBmethodC2);
        buttonGroup.add(RBmethodD);
        buttonGroup.add(RBmethodE);
        // Hint label
        JLabel textLabel = new JLabel("Click to start the experiment");
        textLabel.setAlignmentX(CENTER_ALIGNMENT);
        textLabel.setFont(new Font("Sans", Font.PLAIN, 14));

        this.add(Box.createRigidArea(new Dimension(0, 200)));
        this.add(textLabel);
        this.add(RBmethodA);
        this.add(RBmethodB);
        this.add(RBmethodC);
        this.add(RBmethodC2);
        this.add(RBmethodD);
        this.add(RBmethodE);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
        this.add(startButton);
    }

}
