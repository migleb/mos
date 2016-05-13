package model_os;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NekoChan on 2016-05-13.
 */
public abstract class MemoryListable {

    protected int blockCount;
    protected int blockSize;
    protected String[] memory;
    protected List<OperativeMemoryChangeListener> memChangeListeners;

    public MemoryListable (int blockCount, int blockSize) {
        this.blockCount = blockCount;
        this.blockSize = blockSize;
        this.memory = new String[blockCount * blockSize];
        this.memChangeListeners = new ArrayList<OperativeMemoryChangeListener>();

        for (int i = 0; i < blockCount; i++) {
            for (int j = 0; j < blockSize; j++) {
                occupyMemory(i,j,"0");
            }
        }
    }

    public void occupyMemory (int block, int idx, String value) {
        this.memory[block * this.blockSize + idx] = value;
        for (OperativeMemoryChangeListener l : memChangeListeners) {
            l.memoryChanged(block, idx, value);
        }
    }

    public String getMemory (int block, int idx) {
        return memory[block * this.blockSize + idx];
    }

    public int getBlockCount() {
        return blockCount;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public abstract String getTitle ();

    public void addOperativeMemoryChangeListener(OperativeMemoryChangeListener l) {
        memChangeListeners.add(l);
    }

    public void removeOperativeMemoryChangeListener(OperativeMemoryChangeListener l) {
        memChangeListeners.remove(l);
    }
}