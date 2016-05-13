package model_os;

import javax.swing.*;

/**
 * Created by NekoChan on 2016-05-13.
 */
public class MemoryTable extends JTable {

    private static final long serialVersionUID = -1526885160213490445L;

    public MemoryTable (OperativeMemoryTable tableModel){
        super (tableModel);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setRowSelectionInterval(0, 0);
    }

    @Override
    public OperativeMemoryTable getModel() {
        return (OperativeMemoryTable) super.getModel();
    }
}
