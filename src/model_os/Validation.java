package model_os;

import java.util.List;

/**
 * Created by NekoChan on 2016-05-13.
 */
public class Validation extends TProcess {

    public Validation (TKernel kernel, TPState pState, TProcess pParent,
                       int pPriority, List<TElement> pORElements) {
        super(kernel, pState, pParent, pPriority, pORElements);
    }

    public void phase1 () {
        phase = 2;
        kernel.requestResource(this, ResourceClass.LOADEDPROGRAM, 0);
    }

    public void phase2 () {
        phase = 3;
        String [] generalMemory = kernel.getGeneralMemory();
        getElement(ResourceClass.LOADEDPROGRAM);
        for (int i = 0; i < generalMemory.length; i++) {
            if (i == 0 && !generalMemory[i].equalsIgnoreCase("$JOB")) {
                kernel.releaseResource(ResourceClass.LINETOPRINT, new TElement(null, this, "Program is invalid format ($JOB missing)"));
                return;
            }
            if (i > 111) {
                kernel.releaseResource(ResourceClass.LINETOPRINT, new TElement (null, this, "Program is invalid format (too large)"));
                return;
            }
            if (generalMemory[i].equalsIgnoreCase("$END")) {
                phase = 1;
                int requiredBlocks = (int)Math.ceil((i-1)/10.);
                kernel.releaseResource(ResourceClass.VALIDPROGRAM, new TElement (null, this, String.valueOf(requiredBlocks)));
                return;
            }
        }
        kernel.releaseResource(ResourceClass.LINETOPRINT, new TElement (null, this, "Program is not in valid format ($END missing)"));
    }

    public void phase3 () {
        phase = 1;
        kernel.releaseResouce (ResourceClass.GENERALMEMORY, new TElement (null, this, null));
    }
}
