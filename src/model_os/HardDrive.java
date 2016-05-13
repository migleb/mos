package model_os;

/**
 * Created by NekoChan on 2016-05-13.
 */
public class HardDrive extends MemoryListable{

    public HardDrive (int blockCount, int blockSize) {
        super(blockCount, blockSize);
    }

    @Override
    public String getTitle() {
        return "Hard Drive";
    }

}