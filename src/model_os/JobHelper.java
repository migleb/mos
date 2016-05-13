package model_os;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by NekoChan on 2016-05-13.
 */
public class JobHelper extends TProcess {

    int needPages;
    List<TElement> vmMemory;
    int requestedPageAddr;

    public JobHelper (TKernel kernel, TPState pState, TProcess pParent,
                      int pPriority, List<TElement> pORElements) {

        super(kernel, pState, pParent, pPriority, pORElements);
    }

    public void phase1 () throws Exception {

        phase = 2;
        TElement validProgram = getElement(TResource.ResourceClass.VALIDPROGRAM);
        needPages = Integer.parseInt(validProgram.getInfo());
        int available = kernel.availableResourceElementsFor(this, TResource.ResourceClass.PAGES);
        if (available >= needPages + 1) {
            kernel.askForResource(this, TResource.ResourceClass.PAGES, 0, needPages + 1);
        } else {
            phase = 10;
            kernel.freeResource(TResource.ResourceClass.LINETOPRINT, new TElement (null, this, "Not enough memory to run progran" ));
        }
    }

    // Initialize VM
    public void phase2 () throws Exception {

        phase = 5;
        vmMemory = new LinkedList<TElement>(Arrays.asList(getElements(TResource.ResourceClass.PAGES, needPages + 1)));
        TElement pageTable = vmMemory.get(0);
        int pageTableBlock = Integer.parseInt(pageTable.getInfo());
        // CLEAN PAGE TABLE BLOCK
        for (int i = 0; i < kernel.getRam().getBlockSize(); i++) {
            kernel.getRam().occupyMemory(pageTableBlock, i, String.valueOf(0));
        }
        // FILL PAGE TABLE
        for (int i = 0; i < needPages; i++) {
            kernel.getRam().occupyMemory(pageTableBlock, i, vmMemory.get(i+1).getInfo());
        }
        // CREATE VM FROM PROGRAM IN GENERAL MEMORY
        String [] generalMemory = kernel.getGeneralMemory();
        int i = 1;
        int page = 1;
        int blockIdx = 0;
        int idxInBlock = 10;
        int pc = 0;
        while (!generalMemory[i].equalsIgnoreCase("$END")) {
            if (idxInBlock > 9) {
                blockIdx = Integer.parseInt(vmMemory.get(page++).getInfo());
                idxInBlock = 0;
            }
            if (i == 1) {
                pc = blockIdx * kernel.getRam().getBlockSize() + idxInBlock;
            }
            kernel.getRam().occupyMemory(blockIdx, idxInBlock, generalMemory[i]);
            idxInBlock++;
            i++;
        }

        kernel.freeResource(TResource.ResourceClass.GENERALMEMORY, new TElement(null, this, null));
        // kernel.print("Page Table Info: " + pageTable.getInfo() + "; start pc = " + pc);

        kernel.getProcessor().setPtr(Integer.valueOf(vmMemory.get(0).getInfo()));
        kernel.getProcessor().setPc(pc);
        kernel.getProcessor().clearInterruptFlags();

        kernel.createProcess(new VMRunner (kernel, TPState.READY, this, 0, new LinkedList<TElement>()));
        pCPUState = kernel.getProcessor().getTProcessCPU();
    }

    public void phase5 () {

        phase = 8;
        resumeVMRunner();
        kernel.askForResource(this, TResource.ResourceClass.INTERRUPTINFO, 0);
    }

    private void askForMemory (int mr) throws Exception {

        phase = 5;
        requestedPageAddr = mr;
        String val = kernel.getRam().getMemory(requestedPageAddr / 10, requestedPageAddr % 10);
        if (val.equalsIgnoreCase("0")) {
            if (kernel.availableResourceElementsFor(this, TResource.ResourceClass.PAGES) > 0) {
                phase = 7;
                kernel.askForResource(this, TResource.ResourceClass.PAGES, 0, 1);
            } else {
                phase = 4;
                kernel.freeResource(TResource.ResourceClass.LINETOPRINT, new TElement(null, this, "Not enough memory to allocate"));
            }
        }
    }

    private void freeMemory (int mr) throws InterruptedException {

        phase = 5;
        String val = kernel.getRam().getMemory(mr / 10, mr % 10);
        if (!val.equalsIgnoreCase("0")) {
            for (TElement page : vmMemory) {
                //System.out.println(val + ":" + page.getInfo()));
                if (page.getInfo().equalsIgnoreCase(val)) {
                    vmMemory.remove(page);
                    kernel.getRam().occupyMemory(mr / 10, mr % 10, "0");
                    kernel.freeResource(TResource.ResourceClass.PAGES, page);
                    break;
                }
            }
        }
    }

    public void phase7 () throws Exception {

        phase = 5;
        TElement requestedPage = getElement(TResource.ResourceClass.PAGES);
        kernel.getRam().occupyMemory(requestedPageAddr / 10, requestedPageAddr % 10, requestedPage.getInfo());
        vmMemory.add(requestedPage);
    }

    public void phase6() throws Exception {

        phase = 5;
        TElement savedLine = getElement(TResource.ResourceClass.SAVEDLINE);
        int addr = savedLine.getTarget();
        String info = savedLine.getInfo().substring(0, Math.min(5, savedLine.getInfo().length()));
        kernel.getRam().occupyMemory(addr / 10, addr % 10, info);
    }

    public void phase8 () throws Exception {

        phase = 4;
        TElement interruptInfo = getElement(TResource.ResourceClass.INTERRUPTINFO);
        kernel.stopProcess(getpChProcesses().element());

        String [] infoParts = interruptInfo.getInfo().split(":");
        switch (MachineInterrupt.InterruptType.valueOf(infoParts[0])){
            case FREEMEM: {
                freeMemory(Integer.valueOf(infoParts[1]));
                break;
            }
            case REQUESTMEM: {
                askForMemory(Integer.valueOf(infoParts[1]));
                break;
            }
            case PRINT: {
                phase = 5;
                kernel.freeResource(TResource.ResourceClass.LINETOPRINT, new TElement(null, this, kernel.getProcessor().getValueInAddress(Integer.valueOf(infoParts[1]))));
                break;
            }
            case SCAN: {
                phase = 6;
                kernel.askForResource(this, TResource.ResourceClass.SAVEDLINE, Integer.valueOf(infoParts[1]));
                break;
            }
            case TIMER: {
                phase = 5;
                break;
            }
            case BADCOMMAND: {
                kernel.freeResource(TResource.ResourceClass.LINETOPRINT, new TElement(null, this, "Invalid command"));
                break;
            }
            case HALT: {
                kernel.freeResource(TResource.ResourceClass.LINETOPRINT, new TElement(null, this, "Task successfully finished"));
                break;
            }
            case OUTOFVIRTUALMEMORY: {
                kernel.freeResource(TResource.ResourceClass.LINETOPRINT, new TElement (null, this, "Invalid address, out of memory") );
                break;
            }
        }
    }

    public void resumeVMRunner () {
        kernel.activateProcess(getpChProcesses().element());
    }

    public void phase3 () {
        phase = 10;
        kernel.freeResource(TResource.ResourceClass.GENERALMEMORY, new TElement (null, this, null ));
    }

    // DESTROY VM
    public void phase4 () throws Exception {
        phase = 10;
        kernel.freeResource(TResource.ResourceClass.PAGES, vmMemory.toArray(new TElement[vmMemory.size()]));
    }

    public void phase10 () {
        phase = 1;
        kernel.freeResource(TResource.ResourceClass.VALIDPROGRAM, new TElement(null, this, null));
    }
}
