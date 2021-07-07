package dskm.experiment;

/***
 * Class for each recorded log
 */
public class Molog {

    private Mevent mevent;
    private Long timestamp; // in milliseconds

    /***
     *  Create a log
     * @param me Mouse event
     * @param t Timestamp of the event (in ms)
     */
    public Molog(Mevent me, Long t) {
        mevent = me;
        timestamp = t;
    }


}
