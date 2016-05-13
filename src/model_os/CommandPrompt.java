package model_os;

import java.security.InvalidParameterException;

/**
 * Created by NekoChan on 2016-05-13.
 */
public class CommandPrompt extends TProcess {

    int start = -1;

    public CommandPrompt (TKernel kernel, TPState pState, TProcess pParent, int pPriority, List<TElement> pORElements) {
        super (kernel, pState, pParent, pPriority, pORElements);
    }

    public void phase1() {
        phase = 2;
        kernel.requestResource (this, ResourceClass.SAVEDLINE, 0);
    }

    public void phase2 () throw Exception {
        TElement savedLine = getElement (ResourceClass.SAVEDLINE);
        phase = 1;
        String info = savedLine.getInfo();
        try {
            if (info.equalsIgnoreCase("SHTDW")) {
                kernel.releaseResource(ResourceClass.SHUTDOWN, new TElement (null, this, null));
            } else if (info.startsWith("LS")){
                String address = info.substring(2, Math.min(info.length(), 5));
                start = Integer.valueOf(address);
                kernel.releaseResource (ResourceClass.LINETOPRINT, new TElement(null, this, "Start := " + start));
            } else if (start >= 0 && info.startsWith("LE")) {
                String address = info.substring(2, Math.min(info.length(), 5));
                int end = Integer.valueOf(address);
                if (end > 1000 || (end - start) > 100 || (end - start) <= 0) {
                    throw new InvalidParameterException("End address is invalid. Settings reset");
                }
                kernel.releaseResource(ResourceClass.LOADPROGRAM, new TElement (null, this, start + ":" + end));
            }
        }
        catch (NumberFormatException e) {
            kernel.releaseResource(ResourceClass.LINETOPRINT, new TElement(null, this, start + ":" + end) );
            start = -1;
        }
        catch (InvalidParameterException e) {
            kernel.releaseResource (ResourceClass.LINETOPRINT, new TElement(null, this, e.getMessage()));
            start = -1;
        }
    }
}
