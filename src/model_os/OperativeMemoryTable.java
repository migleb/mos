package model_os;

import javax.swing.table.DefaultTableModel;

/**
 * Created by NekoChan on 2016-05-13.
 */
public class OperativeMemoryTable extends DefaultTableModel {

    public static final long serialVersionUID = -8052788013480634187L;

    final MemoryListable memory;

    public OperativeMemoryTable(String[] columnNames, MemoryListable memory) {
        super (columnNames, 0);
        this.memory = memory;

        for (int i = 0; i < memory.getBlockCount(); i++) {
            for (int j = 0; j < memory.getBlockSize(); j++) {
                this.addRow(new Object[]{i*memory.getBlockSize() + j, memory.getMemory(i, j)});
            }
        }
    }

    @Override
    public boolean isCellEditable (int row, int col) {
        return false;
    }

    public MemoryListable getMemory() {
        return memory;
    }
}
