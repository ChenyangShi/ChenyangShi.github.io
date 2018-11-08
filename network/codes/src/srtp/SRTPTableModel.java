/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package srtp;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author traitor
 */
public class SRTPTableModel extends DefaultTableModel {
    public SRTPTableModel(Object[][] data, Object[] columnHeaders) {
        super(data, columnHeaders);
    }

    public SRTPTableModel() {
        super();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
