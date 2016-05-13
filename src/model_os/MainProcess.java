package model_os;

import java.util.List;

/**
 * Created by NekoChan on 2016-05-13.
 */
public class MainProcess extends TProcess {

    public MainProcess (TKernel kernel, TPState pState, TProcess pParent,
                        int pPriority, List<TElement> pORElements) {
        super(kernel, pState, pParent, pPriority, pORElements);
    }

    public void phase1() {
        phase = 2;
        kernel.requestResource(this, ResourceClass.VALIDPROGRAM, 0);
    }

    public phase2 () throws Exception {
        phase = 1;
        TElement validProgram = getElement(ResourceClass.VALIDPROGRAM);
        if (validProgram.getInfo() != null) {
            List<TElements> jobHelperElements = new ArrayList<TElements>();
            jobHelperElements.add(validProgram);
            kernel.createProcess (new JobHElper, kernel, TPState.READY, this, 1, jobHelperElements);
        } else {
            TProcess proc = validProgram.getCreator();
            kernel.destroyProcess(proc);
        }
    }

}
