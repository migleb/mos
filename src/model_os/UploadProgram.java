package model_os;

import java.util.List;

/**
 * Created by NekoChan on 2016-05-13.
 */
public class UploadProgram extends TProcess {

    public UploadProgram (TKernel kernel, TPState pState, TProcess pParent,
                          int pPriority, List<TElement> pORElements) {
        super (kernel, pState, pParent, pPriority, pORElements);
    }

    public void phase1 () {
        phase = 2;
        kernel.askForResource(this, TResource.ResourceClass.LOADPROGRAM, 0);
    }

    public void phase2 () {
        phase = 3;
        kernel.askForResource(this, TResource.ResourceClass.GENERALMEMORY, 0);
    }

    public void phase3 () {
        phase = 4;
        kernel.askForResource(this, TResource.ResourceClass.CHANNELDEVICE, 0);
    }

    public void phase4 () throws Exception {
        phase = 5;
        TElement loadProgram = getElement (TResource.ResourceClass.LOADPROGRAM);
        String [] addresses = loadProgram.getInfo().split(":");
        int start = Integer.valueOf(addresses[0]);
        int end = Integer.valueOf(addresses[1]);
        int gmIdx = 0;
        for (int i = start; i < end; i++) {
            kernel.getGeneralMemory()[gmIdx++] = kernel.getHdd().getMemory(i / 10, i % 10);
        }
        TElement channelDevice = getElement (TResource.ResourceClass.CHANNELDEVICE);
        kernel.freeResource (TResource.ResourceClass.CHANNELDEVICE, channelDevice);
    }

    public void phase5 () {
        phase = 1;
        kernel.freeResource(TResource.ResourceClass.LOADEDPROGRAM, new TElement (null, this, null));
    }
}
