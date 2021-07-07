package dskm.action;

import dskm.Constants;
import dskm.connection.MooseServer;
import io.reactivex.rxjava3.subjects.PublishSubject;

import java.awt.*;
import java.awt.event.InputEvent;

public class MooseBot {

    private static MooseBot self; // for singleton

    private Robot bot;

    /**
     * Constructor
     */
    public MooseBot() throws AWTException {
        bot = new Robot();
    }

    /**
     * Start observing actions from the PublishSubject and performing them
     * @param actionSubject PublishSubject of actions (from Constants)
     */
    public void startBot(PublishSubject<String> actionSubject) {
        // Subscribe to the actionSubject to get the actions
        actionSubject.subscribe(action -> {
            switch (action) {
                case Constants.ACT_CLICK:
                    click();
                    break;
            }
        });
    }

    /***
     * Singleton
     * @return Singleton instance
     * @throws AWTException
     */
    public static MooseBot get() {
        try {
            if (self == null) self = new MooseBot();
        } catch (AWTException e) {
            System.out.println(e);
        }

        return self;
    }

    /**
     * Simulate clicking
     * @throws AWTException
     */
    public void click() throws AWTException {
        System.out.println("Bot click");
        Point cursorPos = MouseInfo.getPointerInfo().getLocation();
        bot.mouseMove(cursorPos.x, cursorPos.y);
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public void moveCursor(int dx, int dy) {
        Point cursorPos = MouseInfo.getPointerInfo().getLocation();
        bot.mouseMove(cursorPos.x + dx, cursorPos.y + dy);
    }

}
