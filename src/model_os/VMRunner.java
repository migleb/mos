package model_os;

import java.util.List;

/**
 * Created by NekoChan on 2016-05-13.
 */
public class VMRunner extends TProcess {

    public VMRunner (TKernel kernel, TPState pState, TProcess pParent,
                     int pPriority, List<TElement> pORElements) {

        super(kernel, pState, pParent, pPriority, pORElements);
    }

    public void phase1 () throws Exception {
        kernel.getProcessor().setTProcessCPU(pCPUState);
        kernel.getProcessor().setMode(1);

        try {
            while (true){
                kernel.getProcessor().step();
            }
        } catch (MachineInterrupt interrupt) {
            Processor proc = kernel.getProcessor();
            String info = proc.getValue(ProcessorRegister.SI) + ":" + proc.getValue(ProcessorRegister.PI) + ":" + proc.getValue(ProcessorRegister.TI) + ":" + proc.getValue(ProcessorRegister.MR);
            kernel.getProcessor().clearInterruptFlags();
            pCPUState = kernel.getProcessor().getTProcessCPU();
            kernel.freeResource(TResource.ResourceClass.INTERRUPT, new TElement (null, this, info));
        }
    }
}
