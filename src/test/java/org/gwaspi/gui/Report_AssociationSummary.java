
/*
 * Report_AssociationSummary.java
 *
 * Created on Nov 16, 2009, 3:42:18 PM
 */
package org.gwaspi.gui;

/**
 *
 * @author u56124
 */
public class Report_AssociationSummary extends javax.swing.JPanel {

	/**
	 * Creates new form Report_AssociationSummary
	 */
	public Report_AssociationSummary() {
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        
        pnl_Summary = new javax.swing.JPanel();
        txt_NRows = new javax.swing.JTextField();
        lbl_suffix1 = new javax.swing.JLabel();
        btn_Get = new javax.swing.JButton();
        pnl_SearchDB = new javax.swing.JPanel();
        cmb_SearchDB = new javax.swing.JComboBox();
        scrl_ReportTable = new javax.swing.JScrollPane();
        tbl_ReportTable = new javax.swing.JTable();
        
        setBorder(javax.swing.BorderFactory.createTitledBorder("Report"));
        
        pnl_Summary.setBorder(javax.swing.BorderFactory.createTitledBorder("Summary"));
        
        txt_NRows.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txt_NRows.setText("100");
        
        lbl_suffix1.setText("most significant p-Values.");
        
        btn_Get.setText("Get");
        
        javax.swing.GroupLayout pnl_SummaryLayout = new javax.swing.GroupLayout(pnl_Summary);
        pnl_Summary.setLayout(pnl_SummaryLayout);
        pnl_SummaryLayout.setHorizontalGroup(
                pnl_SummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnl_SummaryLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txt_NRows, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_suffix1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Get, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
        pnl_SummaryLayout.setVerticalGroup(
                pnl_SummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnl_SummaryLayout.createSequentialGroup()
                .addGroup(pnl_SummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txt_NRows, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lbl_suffix1)
                .addComponent(btn_Get))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
        
        pnl_SearchDB.setBorder(javax.swing.BorderFactory.createTitledBorder("Search Database"));
        
        cmb_SearchDB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        
        javax.swing.GroupLayout pnl_SearchDBLayout = new javax.swing.GroupLayout(pnl_SearchDB);
        pnl_SearchDB.setLayout(pnl_SearchDBLayout);
        pnl_SearchDBLayout.setHorizontalGroup(
                pnl_SearchDBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnl_SearchDBLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cmb_SearchDB, 0, 357, Short.MAX_VALUE)
                .addContainerGap())
                );
        pnl_SearchDBLayout.setVerticalGroup(
                pnl_SearchDBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnl_SearchDBLayout.createSequentialGroup()
                .addComponent(cmb_SearchDB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
                );
        
        tbl_ReportTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                    {null, null, null, null},
                    {null, null, null, null},
                    {null, null, null, null},
                    {null, null, null, null}
        },
                new String [] {
            "Title 1", "Title 2", "Title 3", "Title 4"
        }
        ));
        scrl_ReportTable.setViewportView(tbl_ReportTable);
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(scrl_ReportTable, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 801, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_Summary, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_SearchDB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnl_Summary, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnl_SearchDB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrl_ReportTable, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                .addContainerGap())
                );
    }//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Get;
    private javax.swing.JComboBox cmb_SearchDB;
    private javax.swing.JLabel lbl_suffix1;
    private javax.swing.JPanel pnl_SearchDB;
    private javax.swing.JPanel pnl_Summary;
    private javax.swing.JScrollPane scrl_ReportTable;
    private javax.swing.JTable tbl_ReportTable;
    private javax.swing.JTextField txt_NRows;
    // End of variables declaration//GEN-END:variables
}
