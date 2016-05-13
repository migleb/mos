package model_os;

import java.util.List;

/**
 * Created by NekoChan on 2016-05-13.
 */
public class PrintLine extends TProcess {

    public PrintLine (TKernel kernel, TPState pState, TProcess pParent,
                      int pPriority, List<TElement> pORElements) {
        super(kernel, pState, pParent, pPriority, pORElements);
    }

    public void phase1 () {
        phase = 2;
        kernel.askForResource(this, TResource.ResourceClass.LINETOPRINT, 0);
    }

    public void phase2 () {
        phase = 3;
        kernel.askForResource(this, TResource.ResourceClass.CHANNELDEVICE, 0);
    }

    public void phase3 () throws Exception {
        phase = 1;
        TElement lineToPrint = getElement (TResource.ResourceClass.LINETOPRINT);
        kernel.print(lineToPrint.getInfo());
        TElement channelDevice = getElement (TResource.ResourceClass.CHANNELDEVICE);
        kernel.freeResource (TResource.ResourceClass.CHANNELDEVICE, channelDevice);
    }
}
