package org.gwaspi.gui;

/**
 *
 * @author u56124
 */
public class TestProcessTab extends javax.swing.JFrame {

	/**
	 * Creates new form TestProcessTab
	 */
	public TestProcessTab() {
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

        pnl_Orverview = new javax.swing.JPanel();
        scrl_Overview = new javax.swing.JScrollPane();
        tbl_Overview = new javax.swing.JTable();
        pnl_Logo = new javax.swing.JPanel();
        lbl_Logo = new javax.swing.JLabel();
        pnl_ProcessLog = new javax.swing.JPanel();
        scrl_ProcessLog = new javax.swing.JScrollPane();
        txtA_ProcessLog = new javax.swing.JTextArea();
        btn_Save = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnl_Orverview.setBorder(javax.swing.BorderFactory.createTitledBorder("Processes"));

        tbl_Overview.setModel(new javax.swing.table.DefaultTableModel(
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
        scrl_Overview.setViewportView(tbl_Overview);

        pnl_Logo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout pnl_LogoLayout = new javax.swing.GroupLayout(pnl_Logo);
        pnl_Logo.setLayout(pnl_LogoLayout);
        pnl_LogoLayout.setHorizontalGroup(
            pnl_LogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbl_Logo, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        pnl_LogoLayout.setVerticalGroup(
            pnl_LogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbl_Logo, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnl_OrverviewLayout = new javax.swing.GroupLayout(pnl_Orverview);
        pnl_Orverview.setLayout(pnl_OrverviewLayout);
        pnl_OrverviewLayout.setHorizontalGroup(
            pnl_OrverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_OrverviewLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrl_Overview, javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(pnl_Logo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnl_OrverviewLayout.setVerticalGroup(
            pnl_OrverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_OrverviewLayout.createSequentialGroup()
                .addGroup(pnl_OrverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrl_Overview, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnl_Logo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnl_OrverviewLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {pnl_Logo, scrl_Overview});

        pnl_ProcessLog.setBorder(javax.swing.BorderFactory.createTitledBorder("Process Log"));

        txtA_ProcessLog.setColumns(20);
        txtA_ProcessLog.setRows(5);
        scrl_ProcessLog.setViewportView(txtA_ProcessLog);

        btn_Save.setText("Save");

        javax.swing.GroupLayout pnl_ProcessLogLayout = new javax.swing.GroupLayout(pnl_ProcessLog);
        pnl_ProcessLog.setLayout(pnl_ProcessLogLayout);
        pnl_ProcessLogLayout.setHorizontalGroup(
            pnl_ProcessLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_ProcessLogLayout.createSequentialGroup()
                .addGroup(pnl_ProcessLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnl_ProcessLogLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btn_Save, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(scrl_ProcessLog, javax.swing.GroupLayout.DEFAULT_SIZE, 784, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnl_ProcessLogLayout.setVerticalGroup(
            pnl_ProcessLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_ProcessLogLayout.createSequentialGroup()
                .addComponent(scrl_ProcessLog, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Save)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnl_ProcessLog, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnl_Orverview, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnl_Orverview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_ProcessLog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new TestProcessTab().setVisible(true);
			}
		});
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Save;
    private javax.swing.JLabel lbl_Logo;
    private javax.swing.JPanel pnl_Logo;
    private javax.swing.JPanel pnl_Orverview;
    private javax.swing.JPanel pnl_ProcessLog;
    private javax.swing.JScrollPane scrl_Overview;
    private javax.swing.JScrollPane scrl_ProcessLog;
    private javax.swing.JTable tbl_Overview;
    private javax.swing.JTextArea txtA_ProcessLog;
    // End of variables declaration//GEN-END:variables
}