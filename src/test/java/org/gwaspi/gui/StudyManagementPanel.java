
/*
 * CurrentStudyPanel.java
 *
 * Created on Nov 12, 2009, 10:04:17 AM
 */
package org.gwaspi.gui;

/**
 *
 * @author fernando
 */
public class StudyManagementPanel extends javax.swing.JPanel {

	/**
	 * Creates new form CurrentStudyPanel
	 */
	public StudyManagementPanel() {
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        
        pnl_StudiesTable = new javax.swing.JPanel();
        scrl_StudiesTable = new javax.swing.JScrollPane();
        tbl_StudiesTable = new javax.swing.JTable();
        btn_DeleteStudy = new javax.swing.JButton();
        pnl_StudyDesc = new javax.swing.JPanel();
        lbl_NewStudyName = new javax.swing.JLabel();
        txtF_NewStudyName = new javax.swing.JTextField();
        lbl_Desc = new javax.swing.JLabel();
        scrl_Desc = new javax.swing.JScrollPane();
        txtA_Desc = new javax.swing.JTextArea();
        btn_AddStudy = new javax.swing.JButton();
        pnl_Footer = new javax.swing.JPanel();
        
        pnl_StudiesTable.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Available Studies", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("DejaVu Sans", 1, 13))); // NOI18N
        
        tbl_StudiesTable.setModel(new javax.swing.table.DefaultTableModel(
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
        scrl_StudiesTable.setViewportView(tbl_StudiesTable);
        
        btn_DeleteStudy.setText("Delete Study");
        btn_DeleteStudy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_DeleteStudyActionPerformed(evt);
            }
        });
        
        javax.swing.GroupLayout pnl_StudiesTableLayout = new javax.swing.GroupLayout(pnl_StudiesTable);
        pnl_StudiesTable.setLayout(pnl_StudiesTableLayout);
        pnl_StudiesTableLayout.setHorizontalGroup(
                pnl_StudiesTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnl_StudiesTableLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_StudiesTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrl_StudiesTable, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 694, Short.MAX_VALUE)
                .addComponent(btn_DeleteStudy, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
                );
        pnl_StudiesTableLayout.setVerticalGroup(
                pnl_StudiesTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnl_StudiesTableLayout.createSequentialGroup()
                .addComponent(scrl_StudiesTable, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_DeleteStudy)
                .addContainerGap())
                );
        
        pnl_StudyDesc.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Create New Study", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("DejaVu Sans", 1, 13))); // NOI18N
        
        lbl_NewStudyName.setText("Study Name:");
        
        txtF_NewStudyName.setText("Insert Name of new Study...");
        
        lbl_Desc.setText("Description:");
        
        txtA_Desc.setColumns(20);
        txtA_Desc.setRows(5);
        txtA_Desc.setText("Insert Description...");
        scrl_Desc.setViewportView(txtA_Desc);
        
        btn_AddStudy.setText("Add Study");
        
        javax.swing.GroupLayout pnl_StudyDescLayout = new javax.swing.GroupLayout(pnl_StudyDesc);
        pnl_StudyDesc.setLayout(pnl_StudyDescLayout);
        pnl_StudyDescLayout.setHorizontalGroup(
                pnl_StudyDescLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnl_StudyDescLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_StudyDescLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lbl_NewStudyName)
                .addComponent(lbl_Desc))
                .addGap(18, 18, 18)
                .addGroup(pnl_StudyDescLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrl_Desc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE)
                .addComponent(txtF_NewStudyName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE))
                .addContainerGap())
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_StudyDescLayout.createSequentialGroup()
                .addContainerGap(605, Short.MAX_VALUE)
                .addComponent(btn_AddStudy, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
                );
        pnl_StudyDescLayout.setVerticalGroup(
                pnl_StudyDescLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnl_StudyDescLayout.createSequentialGroup()
                .addGroup(pnl_StudyDescLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lbl_NewStudyName)
                .addComponent(txtF_NewStudyName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnl_StudyDescLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lbl_Desc)
                .addGroup(pnl_StudyDescLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(scrl_Desc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_AddStudy))
                );
        
        javax.swing.GroupLayout pnl_FooterLayout = new javax.swing.GroupLayout(pnl_Footer);
        pnl_Footer.setLayout(pnl_FooterLayout);
        pnl_FooterLayout.setHorizontalGroup(
                pnl_FooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 730, Short.MAX_VALUE)
                );
        pnl_FooterLayout.setVerticalGroup(
                pnl_FooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 100, Short.MAX_VALUE)
                );
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnl_StudiesTable, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnl_StudyDesc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnl_Footer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
                );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnl_StudiesTable, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_StudyDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_Footer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
    }//GEN-END:initComponents

    private void btn_DeleteStudyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_DeleteStudyActionPerformed
		// TODO add your handling code here:
    }//GEN-LAST:event_btn_DeleteStudyActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_AddStudy;
    private javax.swing.JButton btn_DeleteStudy;
    private javax.swing.JLabel lbl_Desc;
    private javax.swing.JLabel lbl_NewStudyName;
    private javax.swing.JPanel pnl_Footer;
    private javax.swing.JPanel pnl_StudiesTable;
    private javax.swing.JPanel pnl_StudyDesc;
    private javax.swing.JScrollPane scrl_Desc;
    private javax.swing.JScrollPane scrl_StudiesTable;
    private javax.swing.JTable tbl_StudiesTable;
    private javax.swing.JTextArea txtA_Desc;
    private javax.swing.JTextField txtF_NewStudyName;
    // End of variables declaration//GEN-END:variables
}
