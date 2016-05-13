package model_os;

import java.util.List;

/**
 * Created by NekoChan on 2016-05-13.
 */
public class Idle extends TProcess {

    public Idle (TKernel kernel, TPState pState, TProcess pParent,
                 int pPriority, List<TElement> pORElements) {
        super (kernel, pState, pParent, pPriority, pORElements);
    }

    public void phase1 () {
        kernel.askForResource (this, TResource.ResourceClass.IDLE, 0);
    }

}
