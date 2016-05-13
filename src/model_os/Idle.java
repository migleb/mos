package model_os;

/**
 * Created by NekoChan on 2016-05-13.
 */
public class Idle extends TProcess {

    public Idle (TKernel kernel, TPState pState, TProcess pParent,
                 int pPriority, List<TElement> pORElements) {
        super (kernel, pState, pParent, pPriority, pORElements);
    }

    public void phase1 () {
        kernel.requestResource (this, ResourceClass.IDLE, 0);
    }

}
