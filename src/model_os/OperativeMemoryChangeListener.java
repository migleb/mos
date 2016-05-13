package model_os;

/**
 * Created by NekoChan on 2016-05-13.
 */
public interface OperativeMemoryChangeListener {

    public void memoryChanged (int block, int idx, String value);

    public void memoryExecuted (int block, int idx);


}
