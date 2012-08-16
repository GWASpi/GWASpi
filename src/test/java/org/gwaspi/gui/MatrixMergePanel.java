
/*
 * MatrixTrafoPanel.java
 *
 * Created on Nov 13, 2009, 2:08:31 PM
 */
package org.gwaspi.gui;

/**
 *
 * @author u56124
 */
public class MatrixMergePanel extends javax.swing.JPanel {

	/**
	 * Creates new form MatrixTrafoPanel
	 */
	public MatrixMergePanel() {
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        pnl_ParentMatrixDesc = new javax.swing.JPanel();
        scrl_ParentMatrixDesc = new javax.swing.JScrollPane();
        txtA_ParentMatrixDesc = new javax.swing.JTextArea();
        pnl_addedMatrix = new javax.swing.JPanel();
        cmb_SelectMatrix = new javax.swing.JComboBox();
        lbl_SelectMatrix = new javax.swing.JLabel();
        rdio_MergeMarkers = new javax.swing.JRadioButton();
        rdio_MergeSamples = new javax.swing.JRadioButton();
        rdio_MergeAll = new javax.swing.JRadioButton();
        scrl_Notes = new javax.swing.JScrollPane();
        txtA_Notes = new javax.swing.JTextArea();
        pnl_TrafoMatrixDesc = new javax.swing.JPanel();
        lbl_NewMatrixName = new javax.swing.JLabel();
        txt_NewMatrixName = new javax.swing.JTextField();
        scroll_TrafoMatrixDescription = new javax.swing.JScrollPane();
        textArea_TrafoMatrixDescription = new javax.swing.JTextArea();
        btn_Merge = new javax.swing.JButton();
        pnl_Footer = new javax.swing.JPanel();
        btn_Back = new javax.swing.JButton();
        btn_Help = new javax.swing.JButton();

        pnl_ParentMatrixDesc.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Parent Matrix: ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("DejaVu Sans", 1, 13))); // NOI18N

        txtA_ParentMatrixDesc.setColumns(20);
        txtA_ParentMatrixDesc.setRows(5);
        txtA_ParentMatrixDesc.setBorder(javax.swing.BorderFactory.createTitledBorder("Description"));
        scrl_ParentMatrixDesc.setViewportView(txtA_ParentMatrixDesc);

        javax.swing.GroupLayout pnl_ParentMatrixDescLayout = new javax.swing.GroupLayout(pnl_ParentMatrixDesc);
        pnl_ParentMatrixDesc.setLayout(pnl_ParentMatrixDescLayout);
        pnl_ParentMatrixDescLayout.setHorizontalGroup(
            pnl_ParentMatrixDescLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_ParentMatrixDescLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrl_ParentMatrixDesc, javax.swing.GroupLayout.DEFAULT_SIZE, 796, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnl_ParentMatrixDescLayout.setVerticalGroup(
            pnl_ParentMatrixDescLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_ParentMatrixDescLayout.createSequentialGroup()
                .addComponent(scrl_ParentMatrixDesc, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnl_addedMatrix.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Matrix to be added", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        cmb_SelectMatrix.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lbl_SelectMatrix.setText("Select Matrix: ");

        rdio_MergeMarkers.setText("Merge new Markers only");

        rdio_MergeSamples.setText("Merge new Samples only");

        rdio_MergeAll.setText("Merge Markers & Samples");

        txtA_Notes.setColumns(20);
        txtA_Notes.setRows(5);
        txtA_Notes.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        scrl_Notes.setViewportView(txtA_Notes);

        javax.swing.GroupLayout pnl_addedMatrixLayout = new javax.swing.GroupLayout(pnl_addedMatrix);
        pnl_addedMatrix.setLayout(pnl_addedMatrixLayout);
        pnl_addedMatrixLayout.setHorizontalGroup(
            pnl_addedMatrixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_addedMatrixLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_addedMatrixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_addedMatrixLayout.createSequentialGroup()
                        .addComponent(lbl_SelectMatrix)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmb_SelectMatrix, 0, 690, Short.MAX_VALUE))
                    .addGroup(pnl_addedMatrixLayout.createSequentialGroup()
                        .addGroup(pnl_addedMatrixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rdio_MergeMarkers)
                            .addComponent(rdio_MergeSamples)
                            .addComponent(rdio_MergeAll))
                        .addGap(21, 21, 21)
                        .addComponent(scrl_Notes, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnl_addedMatrixLayout.setVerticalGroup(
            pnl_addedMatrixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_addedMatrixLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_addedMatrixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmb_SelectMatrix, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_SelectMatrix))
                .addGroup(pnl_addedMatrixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_addedMatrixLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(rdio_MergeMarkers, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rdio_MergeSamples)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rdio_MergeAll))
                    .addGroup(pnl_addedMatrixLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrl_Notes)))
                .addGap(15, 15, 15))
        );

        pnl_TrafoMatrixDesc.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "New Matrix Details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("DejaVu Sans", 1, 13))); // NOI18N

        lbl_NewMatrixName.setText("New Matrix Name:");

        txt_NewMatrixName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_NewMatrixNameActionPerformed(evt);
            }
        });

        textArea_TrafoMatrixDescription.setColumns(20);
        textArea_TrafoMatrixDescription.setLineWrap(true);
        textArea_TrafoMatrixDescription.setRows(5);
        textArea_TrafoMatrixDescription.setBorder(javax.swing.BorderFactory.createTitledBorder("Description"));
        scroll_TrafoMatrixDescription.setViewportView(textArea_TrafoMatrixDescription);

        btn_Merge.setText("Merge");

        javax.swing.GroupLayout pnl_TrafoMatrixDescLayout = new javax.swing.GroupLayout(pnl_TrafoMatrixDesc);
        pnl_TrafoMatrixDesc.setLayout(pnl_TrafoMatrixDescLayout);
        pnl_TrafoMatrixDescLayout.setHorizontalGroup(
            pnl_TrafoMatrixDescLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_TrafoMatrixDescLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_TrafoMatrixDescLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_TrafoMatrixDescLayout.createSequentialGroup()
                        .addComponent(lbl_NewMatrixName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_NewMatrixName, javax.swing.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE))
                    .addComponent(scroll_TrafoMatrixDescription, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 798, Short.MAX_VALUE)
                    .addComponent(btn_Merge, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        pnl_TrafoMatrixDescLayout.setVerticalGroup(
            pnl_TrafoMatrixDescLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_TrafoMatrixDescLayout.createSequentialGroup()
                .addGroup(pnl_TrafoMatrixDescLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_NewMatrixName)
                    .addComponent(txt_NewMatrixName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scroll_TrafoMatrixDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_Merge, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btn_Back.setText("Back");

        btn_Help.setText("Help");
        btn_Help.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_HelpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_FooterLayout = new javax.swing.GroupLayout(pnl_Footer);
        pnl_Footer.setLayout(pnl_FooterLayout);
        pnl_FooterLayout.setHorizontalGroup(
            pnl_FooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_FooterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_Back, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 652, Short.MAX_VALUE)
                .addComponent(btn_Help)
                .addContainerGap())
        );

        pnl_FooterLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_Back, btn_Help});

        pnl_FooterLayout.setVerticalGroup(
            pnl_FooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_FooterLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnl_FooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_Back)
                    .addComponent(btn_Help)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnl_TrafoMatrixDesc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(pnl_ParentMatrixDesc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(14, 14, 14))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnl_addedMatrix, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(14, 14, 14))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(pnl_Footer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnl_ParentMatrixDesc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_addedMatrix, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_TrafoMatrixDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_Footer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_NewMatrixNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_NewMatrixNameActionPerformed
		// TODO add your handling code here:
}//GEN-LAST:event_txt_NewMatrixNameActionPerformed

    private void btn_HelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_HelpActionPerformed
		// TODO add your handling code here:
}//GEN-LAST:event_btn_HelpActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Back;
    private javax.swing.JButton btn_Help;
    private javax.swing.JButton btn_Merge;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmb_SelectMatrix;
    private javax.swing.JLabel lbl_NewMatrixName;
    private javax.swing.JLabel lbl_SelectMatrix;
    private javax.swing.JPanel pnl_Footer;
    private javax.swing.JPanel pnl_ParentMatrixDesc;
    private javax.swing.JPanel pnl_TrafoMatrixDesc;
    private javax.swing.JPanel pnl_addedMatrix;
    private javax.swing.JRadioButton rdio_MergeAll;
    private javax.swing.JRadioButton rdio_MergeMarkers;
    private javax.swing.JRadioButton rdio_MergeSamples;
    private javax.swing.JScrollPane scrl_Notes;
    private javax.swing.JScrollPane scrl_ParentMatrixDesc;
    private javax.swing.JScrollPane scroll_TrafoMatrixDescription;
    private javax.swing.JTextArea textArea_TrafoMatrixDescription;
    private javax.swing.JTextArea txtA_Notes;
    private javax.swing.JTextArea txtA_ParentMatrixDesc;
    private javax.swing.JTextField txt_NewMatrixName;
    // End of variables declaration//GEN-END:variables
}
