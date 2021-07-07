package dskm.connection;

import dskm.Constants;
import dskm.action.*;
import dskm.experiment.Experimenter;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class MooseServer {

    private static MooseServer self; // for singleton

    private ServerSocket serverSocket;
    private BufferedReader inBR;
    private PrintWriter outPW;
    private boolean isConnected = false;

    private PublishSubject<String> actionSubject; // For publishing the actions

    private @NonNull Observable<String> listenerObserver;

    /**
     * Get instance
     * @return
     */
    public static MooseServer get() {
        if (self == null) self = new MooseServer();
        return self;
    }

    /**
     * Constructor
     */
    private MooseServer() {
        actionSubject = PublishSubject.create();
        listenerObserver = Observable.fromAction(new Action() {

            @Override
            public void run() throws Throwable {
                // Continously read lines from the Moose until get disconnected
                String line;
                do {
                    line = inBR.readLine();
                    // publish the action
                    actionSubject.onNext(line);
                } while(isConnected);
            }
        });
    }

    /**
     * Start the server
     */
    public void start() {

        try {

            // Open socket
            System.out.println("Starting server...");
            serverSocket = new ServerSocket(Constants.CONN_PORT);
            Socket socket = serverSocket.accept();
            System.out.println("Server started!");

            // Create streams
            inBR = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outPW = new PrintWriter(socket.getOutputStream());

            // Get connection request from the Moose
            String line = inBR.readLine();
            System.out.println("First Moose Message: " + line);

            if (Objects.equals(line, Constants.MSSG_MOOSE)) { // Correct message
                // Confirm
                outPW.println(Constants.MSSG_CONFIRM);
                outPW.flush();
                System.out.println("Moose connected! Receiving actions...");

                isConnected = true;

                // Pass the PublishSubject to the Bot for listening
                MooseBot.get().startBot(actionSubject);
                System.out.println("Here!");
                // Start listening to incoming messages from the Moose
                listenerObservable().subscribe();
                System.out.println("There!");
                // Start listening to Experimenter
                System.out.println("Subscribing to Experimenter");
                Experimenter.get().getExpSubject().subscribe(state -> {
                    System.out.println(state);
                    outPW.println(state);
                    outPW.flush();
                });

            }

        } catch (IOException ioException) {
            System.out.println("Problem in starting the server!" + ioException);
            ioException.printStackTrace();
        }
    }

    private @NonNull Observable<Object> listenerObservable() {
        return Observable.fromAction(new Action() {

            @Override
            public void run() throws Throwable {
                // Continously read lines from the Moose until get disconnected
                String line;
                do {
                    line = inBR.readLine();
                    // publish the action
                    actionSubject.onNext(line);
                } while(isConnected);
            }
        }).subscribeOn(Schedulers.io());
    }

}
