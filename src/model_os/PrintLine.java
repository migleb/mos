package model_os;

/**
 * Created by NekoChan on 2016-05-13.
 */
public class PrintLine extends TProcess {

    public PrintLine (TKernel kernel, TPState pState, TPRocess pParent,
                      int pPriority, List<Telements> pORElements) {
        super(kernel, pState, pParent, pPriority, pORElements);
    }

    public void phase1 () {
        phase = 2;
        kernel.requestResource(this, ResourceClass.LINETOPRINT, 0);
    }

    public void phase2 () {
        phase = 3;
        kernel.requestResource(this, resourceClass.CHANNELDEVICE, 0);
    }

    public void phase3 () throws Exception {
        phase = 1;
        TElement lineToPrint = getElement (ResourceClass.LINETOPRINT);
        kernel.print(lineToPrint.getInfo());
        TElement channelDevice = getElement (ResourceClass.CHANNELDEVICE);
        kernel.releaseResource (ResourceClass.CHANNELDEVICE, channelDevice);
    }
}
