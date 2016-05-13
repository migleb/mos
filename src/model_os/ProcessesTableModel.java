package model_os;

import javax.swing.table.AbstractTableModel;

/**
 * Created by NekoChan on 2016-05-13.
 */
public class ProcessesTableModel extends AbstractTableModel {

    private static final long serialVersionUID =  -5565391086322521610L;

    TProcess [] processes = new TProcess[]{};

    public void setProcesses (TProcess[] processes) {
        this.processes = processes;
    }
    @Override
    public int getRowCount () {
        return this.processes.length;
    }

    @Override
    public int getColumnCount () {
        return 3;
    }

    @Override
    public String getColumnName (int colIndex) {
        String[] columnNames = new String [] {"Name", "ID", "Status"};
        return columnNames[colIndex];
    }

    @Override
    public Object getValueAt (int rowIndex, int colIndex) {
        TProcess process = processes[rowIndex];
        switch (colIndex){
            case 0: return process.getExternalName();
            case 1: return process.getpID();
            case 2: return process.getpState().toString();
        }
        return null;
    }

}
