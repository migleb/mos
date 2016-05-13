package model_os;

/**
 * Created by NekoChan on 2016-05-13.
 */
public class Processor extends Registerable {

    int mode; // Machine mode
    int ptr; // Pages table register
    int gr; // General register
    int pc; // Program counter
    int sp; // Stack pointer
    int cf; // Carry flag
    int pi; // Programming interrupt
    int si; // Supervisor interrupt
    int ti; // Timer interrupt
    int mr; // I/O address

    OperativeMemory ram;

    public Processor (OperativeMemory ram) {
        this.ram = ram;
    }

    // Register value setters
    private void setMode (int mode) {
        if (this.mode != mode) {
            changes.firePropertyChange(ProcessorRegister.MODE.name(), this.mode, mode);
            this.mode = mode;
        }
    }
    private void setPtr (int ptr) {
        if (this.ptr != ptr) {
            changes.firePropertyChange(ProcessorRegister.PTR.name(), this.ptr, ptr);
            this.ptr = ptr;
        }
    }
    private void setGr (int gr) {
        if (this.gr != gr){
            changes.firePropertyChange(ProcessorRegister.GR.name(), this.gr, gr);
            this.gr = gr;
        }
    }
    private void setPc (int pc) {
        if (this.pc != pc){
            changes.firePropertyChange(ProcessorRegister.PC.name(), this.pc, pc);
            this.pc = pc;
        }
    }
    private void setSp (int sp) {
        if (this.sp != sp){
            changes.firePropertyChange(ProcessorRegister.SP.name(), this.sp, sp);
            this.sp = sp;
        }
    }
    private void setCf (int cf) {
        if (this.cf != cf){
            changes.firePropertyChange(ProcessorRegister.CF.name(), this.cf, cf);
            this.cf = cf;
        }
    }
    private void setPi (int pi) {
        if (this.pi != pi){
            changes.firePropertyChange(ProcessorRegister.PI.name(), this.pi, pi);
            this.pi = pi;
        }
    }
    private void setSi (int si) {
        if (this.si != si){
            changes.firePropertyChange(ProcessorRegister.SI.name(), this.si, si);
            this.si = si;
        }
    }
    private void setTi (int ti) {
        if (this.ti != ti){
            changes.firePropertyChange(ProcessorRegister.TI.name(), this.ti, ti);
            this.ti = ti;
        }
    }
    private void setMr (int mr) {
        if (this.mr != mr){
            changes.firePropertyChange(ProcessorRegister.MR.name(), this.mr, mr);
            this.mr = mr;
        }
    }
    private void incPc () {
        setPc(this.pc+1);
    }

    // Register value getter
    public int getValue (ProcessorRegister reg){
        switch (reg) {
            case CF: return cf;
            case GR: return gr;
            case MODE: return mode;
            case PC: return pc;
            case PI: return pi;
            case SP: return sp;
            case PTR: return ptr;
            case SI: return si;
            case TI: return ti;
            case MR: return mr;
        }
        return 0;
    }

    public void push (int value) throws OutOfVirtualMemoryException {
        int addr = buildAddress (String.valueOf(sp));
        ram.occupyMemory(addr / 10, addr % 10, String.valueOf(value));
        setSp(sp + 1);
    }

    public int pop () throws OutOfVirtualMemoryException {
        setSp (sp - 1);
        int addr = buildAddress (String.valueOf(sp));
        return Integer.parseInt(ram.getMemory(addr/10, addr%10));
    }

    public int buildAddress (String addr) throws OutOfVirtualMemoryException {
        int blockNumber = Integer.parseInt(addr);
        if (blockNumber > ram.getBlockCount()) {
            throw new OutOfVirtualMemoryException();
        }
        int x = Math.floorDiv(blockNumber, 10);
        if (x >= ram.getBlockSize()) {
            throw new OutOfVirtualMemoryException();
        }
        int y = blockNumber % 10;
        int vmBlockNumber = Integer.valueOf(ram.getMemory(ptr, x));
        if (vmBlockNumber == 0) {
            throw new OutOfVirtualMemoryException();
        }
        return (vmBlockNumber * 10 + y);
    }

    public int realBlockNum (String num) {
        return (ptr * ram.getBlockSize() + Integer.valueOf(num));
    }

    public String getValueInAddress(int addr) {
        int block = addr / 10;
        int idx = addr % 10;
        ram.markMemory(block, idx);
        return ram.getMemory(block, idx);
    }

    private void test() throws MachineInterrupt, Exception {
        if ((si + pi > 0) || (ti == 0)) {
            throw new MachineInterrupt("Virtual machine was interrupted");
        }
    }

    private void doCommand (String cmd) throws MachineInterrupt, Exception {
        incPc();
        int cmdLength = 1;

        try {
            switch (cmd.substring(0, 2)) {
                case "SP" : {
                    int value = Integer.valueOf(cmd.substring(2, 5));
                    setSp(value);
                    return;
                }
                case "GO" : {
                    int addr = buildAddress(cmd.substring(2, 5));
                    setPc(addr);
                    break;
                }
                case "MG" : {
                    int addr = buildAddress(cmd.substring(2, 5));
                    setGr(Integer.parseInt(getValueInAddress(addr)));
                    break;
                }
                case "MM" : {
                    int addr = buildAddress(cmd.substring(2, 5));
                    ram.occupyMemory(addr / 10, addr % 10, String.valueOf(this.gr));
                    break;
                }
                case "GV" : {
                    int val = buildAddress(cmd.substring(2, 5));
                    setGr (val);
                    break;
                }
                case "AD" : {
                    int addr = buildAddress(cmd.substring(2, 5));
                    int val = Integer.parseInt(ram.getMemory(addr / 10, addr % 10));
                    setGr (this.gr + val);
                    break;
                }
                case "CP" : {
                    int addr = buildAddress(cmd.substring(2, 5));
                    int val = Integer.parseInt(ram.getMemory(addr / 10, addr % 10));
                    if (this.gr == val) {
                        setCf(0);
                    } else if (this.gr > val) {
                        setCf(1);
                    } else {
                        setCf(2);
                    }
                    break;
                }
                case "JE" : {
                    int addr = buildAddress(cmd.substring(2, 5));
                    if (this.cf == 0) {
                        setPc(addr);
                    }
                    break;
                }
                case "JL" : {
                    int addr = buildAddress(cmd.substring(2, 5));
                    if (this.cf == 2) {
                        setPc(addr);
                    }
                    break;
                }
                case "JG" : {
                    int addr = buildAddress(cmd.substring(2, 5));
                    if (this.cf == 1) {
                        setPc(addr);
                    }
                    break;
                }
                case "CL" : {
                    int addr = buildAddress(cmd.substring(2, 5));
                    push(pc);
                    setPc(addr);
                    break;
                }
                case "RT" : {
                    setPc(pop());
                    break;
                }
                case "PT" : {
                    cmdLength = 3;
                    setMr (buildAddress(String.valueOf(gr)));
                    setSi (3);
                    break;
                }
                case "SC" : {
                    cmdLength = 3;
                    setMr(buildAddress(String.valueOf(gr)));
                    setSi(3);
                    break;
                }
                case "RM" : {
                    if (mode == 1) {
                        int blockNum = realBlockNum(cmd.substring(2, 3));
                        setMr(blockNum);
                        setPi(3);
                    }
                }
                case "FM" : {
                    if (mode == 1) {
                        int trackNum = realBlockNum(cmd.substring(2, 3));
                        setMr(trackNum);
                        setPi(4);
                        break;
                    }
                }
                case "HT" : {
                    if (mode == 1) {
                        setSi(1);
                        break;
                    }
                }
                default: {
                    throw new Exception("Unknows command");
                }
            }
        }
        catch (OutOfVirtualMemoryException e) {
            setPi (1);
        }
        catch (Exception e) {
            System.out.println(((mode == 0)? "Supervisor" : "User") + ": Invalid command");
            if (mode == 1) {
                setPi (2);
            }
        }

        if (mode == 1) {
            setTi (Math.max(ti - cmdLength, 0));
            test();
        }
    }

    public void step () throws MachineInterrupt, Exception {
        int block = pc / 10;
        int idx = pc % 10;
        String cmd = getValueInAddress(pc);
        System.out.println(block / 10 + "" + block % 10 + ":" + idx + "\t" + cmd);
        doCommand(cmd);
    }

    public void clearInterruptFlags () {
        setSi (0);
        setPi (0);
        setTi (10);
    }

    public TProcessCPU getTProcessCPU () {
        return new TProcessCPU(mode, ptr, gr, pc, sp, cf, pi, si, ti, mr);
    }

    public void setTProcessCPU(TProcessCPU state) {
        setMode(state.mode);
        setPtr(state.ptr);
        setGr(state.gr);
        setPc(state.pc);
        setSp(state.sp);
        setCf(state.cf);
        setPi(state.pi);
        setSi(state.si);
        setTi(state.ti);
        setMr(state.ar);
    }
}
