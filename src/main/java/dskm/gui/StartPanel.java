package dskm.gui;

import dskm.experiment.Experimenter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartPanel extends JPanel {

    StartPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // Start button
        JButton startButton = new JButton("Start Experiment");
        startButton.setFont(new Font("Sans", Font.PLAIN, 14));
        startButton.setBounds(50, 50, 100, 100);
        startButton.setMaximumSize(new Dimension(250, 50));
        startButton.setAlignmentX(CENTER_ALIGNMENT);

        startButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // Pass the control to the Experimenter
                Experimenter.get().startExperiment();
            }
        });

        // Hint label
        JLabel textLabel = new JLabel("Click to start the experiment");
        textLabel.setAlignmentX(CENTER_ALIGNMENT);
        textLabel.setFont(new Font("Sans", Font.PLAIN, 14));

        this.add(Box.createRigidArea(new Dimension(0, 200)));
        this.add(textLabel);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
        this.add(startButton);
    }

}
