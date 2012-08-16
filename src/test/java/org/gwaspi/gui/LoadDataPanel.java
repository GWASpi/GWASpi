
/*
 * LoadDataPanel_old.java
 *
 * Created on Nov 19, 2009, 12:49:41 PM
 */
package org.gwaspi.gui;

/**
 *
 * @author u56124
 */
public class LoadDataPanel extends javax.swing.JPanel {

	/**
	 * Creates new form LoadDataPanel_old
	 */
	public LoadDataPanel() {
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        
        pnl_NameAndDesc = new javax.swing.JPanel();
        lbl_NewMatrixName = new javax.swing.JLabel();
        txt_NewMatrixName = new javax.swing.JTextField();
        scrl_NewMatrixDescription = new javax.swing.JScrollPane();
        txtA_NewMatrixDescription = new javax.swing.JTextArea();
        pnl_Input = new javax.swing.JPanel();
        lbl_Format = new javax.swing.JLabel();
        cmb_Format = new javax.swing.JComboBox();
        lbl_File1 = new javax.swing.JLabel();
        txt_File1 = new javax.swing.JTextField();
        btn_File1 = new javax.swing.JButton();
        lbl_File2 = new javax.swing.JLabel();
        txt_File2 = new javax.swing.JTextField();
        btn_File2 = new javax.swing.JButton();
        lbl_File3 = new javax.swing.JLabel();
        txt_File3 = new javax.swing.JTextField();
        btn_File3 = new javax.swing.JButton();
        pnl_Footer = new javax.swing.JPanel();
        btn_Back = new javax.swing.JButton();
        btn_Go = new javax.swing.JButton();
        btn_Help = new javax.swing.JButton();
        pnl_Gif = new javax.swing.JPanel();
        pnl_GifLeft = new javax.swing.JPanel();
        pnl_GifCenter = new javax.swing.JPanel();
        scrl_Gif = new javax.swing.JScrollPane();
        pnl_GifRight = new javax.swing.JPanel();
        
        setBorder(javax.swing.BorderFactory.createTitledBorder("Import Genotypes"));
        
        pnl_NameAndDesc.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "New Matrix Details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("DejaVu Sans", 1, 13))); // NOI18N
        
        lbl_NewMatrixName.setText("New Matrix Name:");
        
        txt_NewMatrixName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_NewMatrixNameActionPerformed(evt);
            }
        });
        
        txtA_NewMatrixDescription.setColumns(20);
        txtA_NewMatrixDescription.setLineWrap(true);
        txtA_NewMatrixDescription.setRows(5);
        txtA_NewMatrixDescription.setBorder(javax.swing.BorderFactory.createTitledBorder("Description"));
        scrl_NewMatrixDescription.setViewportView(txtA_NewMatrixDescription);
        
        javax.swing.GroupLayout pnl_NameAndDescLayout = new javax.swing.GroupLayout(pnl_NameAndDesc);
        pnl_NameAndDesc.setLayout(pnl_NameAndDescLayout);
        pnl_NameAndDescLayout.setHorizontalGroup(
                pnl_NameAndDescLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_NameAndDescLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_NameAndDescLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(scrl_NewMatrixDescription, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
                .addGroup(pnl_NameAndDescLayout.createSequentialGroup()
                .addComponent(lbl_NewMatrixName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_NewMatrixName, javax.swing.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE)))
                .addContainerGap())
                );
        pnl_NameAndDescLayout.setVerticalGroup(
                pnl_NameAndDescLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnl_NameAndDescLayout.createSequentialGroup()
                .addGroup(pnl_NameAndDescLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lbl_NewMatrixName)
                .addComponent(txt_NewMatrixName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrl_NewMatrixDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
        
        pnl_Input.setBorder(javax.swing.BorderFactory.createTitledBorder("Input"));
        
        lbl_Format.setText("Format");
        
        cmb_Format.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        
        lbl_File1.setText("File 1");
        
        txt_File1.setText("Input file 1");
        
        btn_File1.setText("Browse");
        
        lbl_File2.setText("File 2");
        
        txt_File2.setText("Input file 2");
        
        btn_File2.setText("Browse");
        
        lbl_File3.setText("File 3");
        
        txt_File3.setText("Input file 3");
        
        btn_File3.setText("Browse");
        
        javax.swing.GroupLayout pnl_InputLayout = new javax.swing.GroupLayout(pnl_Input);
        pnl_Input.setLayout(pnl_InputLayout);
        pnl_InputLayout.setHorizontalGroup(
                pnl_InputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnl_InputLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_InputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lbl_Format)
                .addComponent(lbl_File1)
                .addComponent(lbl_File2)
                .addComponent(lbl_File3))
                .addGap(130, 130, 130)
                .addGroup(pnl_InputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(txt_File2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                .addGroup(pnl_InputLayout.createSequentialGroup()
                .addComponent(cmb_Format, 0, 294, Short.MAX_VALUE)
                .addGap(161, 161, 161))
                .addComponent(txt_File1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                .addComponent(txt_File3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_InputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(btn_File1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btn_File2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btn_File3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
                );
        
        
        pnl_InputLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_File1, btn_File2, btn_File3});
        
        pnl_InputLayout.setVerticalGroup(
                pnl_InputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnl_InputLayout.createSequentialGroup()
                .addGroup(pnl_InputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(lbl_Format)
                .addComponent(cmb_Format, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnl_InputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(lbl_File1)
                .addComponent(txt_File1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btn_File1))
                .addGap(18, 18, 18)
                .addGroup(pnl_InputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lbl_File2)
                .addComponent(txt_File2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btn_File2))
                .addGap(18, 18, 18)
                .addGroup(pnl_InputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lbl_File3)
                .addComponent(txt_File3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btn_File3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
        
        btn_Back.setText("Back");
        
        btn_Go.setText("Go!");
        btn_Go.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GoActionPerformed(evt);
            }
        });
        
        btn_Help.setText("Help");
        
        javax.swing.GroupLayout pnl_FooterLayout = new javax.swing.GroupLayout(pnl_Footer);
        pnl_Footer.setLayout(pnl_FooterLayout);
        pnl_FooterLayout.setHorizontalGroup(
                pnl_FooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_FooterLayout.createSequentialGroup()
                .addComponent(btn_Back, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_Help)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 411, Short.MAX_VALUE)
                .addComponent(btn_Go, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
                );
        
        
        pnl_FooterLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_Back, btn_Help});
        
        pnl_FooterLayout.setVerticalGroup(
                pnl_FooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnl_FooterLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(pnl_FooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btn_Go, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btn_Back)
                .addComponent(btn_Help)))
                );
        
        javax.swing.GroupLayout pnl_GifLeftLayout = new javax.swing.GroupLayout(pnl_GifLeft);
        pnl_GifLeft.setLayout(pnl_GifLeftLayout);
        pnl_GifLeftLayout.setHorizontalGroup(
                pnl_GifLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 308, Short.MAX_VALUE)
                );
        pnl_GifLeftLayout.setVerticalGroup(
                pnl_GifLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 100, Short.MAX_VALUE)
                );
        
        javax.swing.GroupLayout pnl_GifCenterLayout = new javax.swing.GroupLayout(pnl_GifCenter);
        pnl_GifCenter.setLayout(pnl_GifCenterLayout);
        pnl_GifCenterLayout.setHorizontalGroup(
                pnl_GifCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrl_Gif, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                );
        pnl_GifCenterLayout.setVerticalGroup(
                pnl_GifCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrl_Gif, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                );
        
        javax.swing.GroupLayout pnl_GifRightLayout = new javax.swing.GroupLayout(pnl_GifRight);
        pnl_GifRight.setLayout(pnl_GifRightLayout);
        pnl_GifRightLayout.setHorizontalGroup(
                pnl_GifRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 284, Short.MAX_VALUE)
                );
        pnl_GifRightLayout.setVerticalGroup(
                pnl_GifRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 100, Short.MAX_VALUE)
                );
        
        javax.swing.GroupLayout pnl_GifLayout = new javax.swing.GroupLayout(pnl_Gif);
        pnl_Gif.setLayout(pnl_GifLayout);
        pnl_GifLayout.setHorizontalGroup(
                pnl_GifLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnl_GifLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnl_GifLeft, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(pnl_GifCenter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnl_GifRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(21, 21, 21))
                );
        pnl_GifLayout.setVerticalGroup(
                pnl_GifLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnl_GifLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(pnl_GifRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnl_GifLeft, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addComponent(pnl_GifCenter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                );
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnl_Gif, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnl_Input, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnl_NameAndDesc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnl_Footer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
                );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_NameAndDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_Input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_Footer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnl_Gif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
    }//GEN-END:initComponents

    private void txt_NewMatrixNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_NewMatrixNameActionPerformed
		// TODO add your handling code here:
}//GEN-LAST:event_txt_NewMatrixNameActionPerformed

    private void btn_GoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GoActionPerformed
		// TODO add your handling code here:
}//GEN-LAST:event_btn_GoActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Back;
    private javax.swing.JButton btn_File1;
    private javax.swing.JButton btn_File2;
    private javax.swing.JButton btn_File3;
    private javax.swing.JButton btn_Go;
    private javax.swing.JButton btn_Help;
    private javax.swing.JComboBox cmb_Format;
    private javax.swing.JLabel lbl_File1;
    private javax.swing.JLabel lbl_File2;
    private javax.swing.JLabel lbl_File3;
    private javax.swing.JLabel lbl_Format;
    private javax.swing.JLabel lbl_NewMatrixName;
    private javax.swing.JPanel pnl_Footer;
    private javax.swing.JPanel pnl_Gif;
    private javax.swing.JPanel pnl_GifCenter;
    private javax.swing.JPanel pnl_GifLeft;
    private javax.swing.JPanel pnl_GifRight;
    private javax.swing.JPanel pnl_Input;
    private javax.swing.JPanel pnl_NameAndDesc;
    private javax.swing.JScrollPane scrl_Gif;
    private javax.swing.JScrollPane scrl_NewMatrixDescription;
    private javax.swing.JTextArea txtA_NewMatrixDescription;
    private javax.swing.JTextField txt_File1;
    private javax.swing.JTextField txt_File2;
    private javax.swing.JTextField txt_File3;
    private javax.swing.JTextField txt_NewMatrixName;
    // End of variables declaration//GEN-END:variables
}
