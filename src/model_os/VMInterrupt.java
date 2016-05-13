package model_os;

import java.util.List;

/**
 * Created by NekoChan on 2016-05-13.
 */
public class VMInterrupt extends TProcess {

    public VMInterrupt (TKernel kernel, TPState pState, TProcess pParent,
                        int pPriority, List<TElement> pORElements) {

        super(kernel, pState, pParent, pPriority, pORElements);
    }

    public void phase1 () {
        phase = 2;
        kernel.askForResource(this, TResource.ResourceClass.INTERRUPT, 0);
    }

    public void phase2 () throws Exception {

        phase = 1;
        TElement interrupt = getElement(TResource.ResourceClass.INTERRUPT);
        String [] interruptInfo = interrupt.getInfo().split(":");
        String mr = interruptInfo[3];

        switch (Integer.valueOf(interruptInfo[0])) {
            case 1: {
                releaseInfo(interrupt.getCreator().getpParent(), MachineInterrupt.InterruptType.HALT, null);
                return;
            }
            case 2: {
                releaseInfo(interrupt.getCreator().getpParent(), MachineInterrupt.InterruptType.PRINT, null);
                return;
            }
            case 3 : {
                releaseInfo(interrupt.getCreator().getpParent(), MachineInterrupt.InterruptType.SCAN, null);
                return;
            }
        }

        switch (Integer.valueOf(interruptInfo[1])) {
            case 1: {
                releaseInfo(interrupt.getCreator().getpParent(), MachineInterrupt.InterruptType.OUTOFVIRTUALMEMORY, null);
                return;
            }
            case 2: {
                releaseInfo(interrupt.getCreator().getpParent(), MachineInterrupt.InterruptType.BADCOMMAND, null);
                return;
            }
            case 3: {
                releaseInfo(interrupt.getCreator().getpParent(), MachineInterrupt.InterruptType.REQUESTMEM, mr);
                return;
            }
            case 4: {
                releaseInfo(interrupt.getCreator().getpParent(), MachineInterrupt.InterruptType.FREEMEM, mr);
                return;
            }
        }

        if (Integer.valueOf(interruptInfo[2]) == 0) {
            releaseInfo (interrupt.getCreator(), MachineInterrupt.InterruptType.TIMER, null);
        }
        throw new Exception("Unexpected Interrupt Type");
    }

    public void releaseInfo (TProcess proc, MachineInterrupt.InterruptType type, String mr) {
        String info = type.toString();
        if (mr != null) {
            info += ":" + mr;
        }
        kernel.freeResource(TResource.ResourceClass.INTERRUPTINFO, new TElement(proc, this, info));
    }
}
