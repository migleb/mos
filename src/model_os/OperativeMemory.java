package model_os;

/**
 * Created by NekoChan on 2016-05-13.
 */
public class OperativeMemory extends MemoryListable {

    public OperativeMemory (int blockCount, int blockSize){
        super(blockCount, blockSize);
    }

    public void markMemory (int block, int idx) {
        for (OperativeMemoryChangeListener l: memChangeListeners) {
            l.memoryExecuted(block, idx);
        }
    }

    @Override
    public String getTitle() {
        return "Operative Memory";
    }
}
