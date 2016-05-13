package model_os;

/**
 * Created by NekoChan on 2016-05-13.
 */
public class MachineInterrupt extends Exception {

    private static final long serialVersionUID = 5200130498662999430L;

    public enum InterruptType {
        HALT,
        PRINT,
        SCAN,
        OUTOFVIRTUALMEMORY,
        BADCOMMAND,
        REQUESTMEM,
        FREEMEM,
        TIMER
    }

    public MachineInterrupt (String string) {
        super(string);
    }
}
