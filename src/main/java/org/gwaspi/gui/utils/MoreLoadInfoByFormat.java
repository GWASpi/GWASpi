package org.gwaspi.gui.utils;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.gwaspi.constants.cImport;
import org.gwaspi.constants.cNetCDF;
import org.gwaspi.constants.cNetCDF.Defaults.GenotypeEncoding;
import org.gwaspi.constants.cNetCDF.Defaults.StrandType;
import org.gwaspi.global.Text;
import org.gwaspi.netCDF.operations.GWASinOneGOParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoreLoadInfoByFormat {

	private final Logger log = LoggerFactory.getLogger(MoreLoadInfoByFormat.class);

	// Variables declaration - do not modify
	private JButton btn_Go;
	private JButton btn_Help;
	private JButton btn_Cancel;
	private JLabel lbl_Chromosome;
	private JComboBox cmb_Chromosome;
	private JLabel lbl_Strand;
	private JComboBox cmb_Strand;
	private JLabel lbl_GTCode;
	private JComboBox cmb_GTCode;
	private JFrame myFrame = new JFrame("GridBagLayout Test");
	private JDialog dialog;
	private GWASinOneGOParams gwasParams = new GWASinOneGOParams();
	private String format = "";
	// End of variables declaration

	private GWASinOneGOParams showMoreInfo(String _format) {

		gwasParams.setProceed(false);
		format = _format;

		switch (cImport.ImportFormat.compareTo(format)) {
			case Affymetrix_GenomeWide6:
				gwasParams.setProceed(true);
				break;
			default:
				// Create a modal dialog
				dialog = new JDialog(myFrame, "Additional Info", true);
				format = _format;

				Toolkit tk = Toolkit.getDefaultToolkit();
				Dimension screenSize = tk.getScreenSize();
				int screenHeight = screenSize.height;
				int screenWidth = screenSize.width;
				dialog.setLocation(screenWidth / 4, screenHeight / 4);

				dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				Container myPane = dialog.getContentPane();
				myPane.setLayout(new GridBagLayout());
				GridBagConstraints c = new GridBagConstraints();
				setConstraints(c, 0, 0, GridBagConstraints.CENTER);
				myPane.add(getQuestionsPanel(), c);
				setConstraints(c, 0, 1, GridBagConstraints.CENTER);
				myPane.add(getFooterPanel(), c);
				dialog.pack();
				dialog.setVisible(true);
		}

		return gwasParams;
	}

	private JPanel getQuestionsPanel() {

		JPanel pnl_Questions = new JPanel(new GridBagLayout());
		pnl_Questions.setBorder(BorderFactory.createTitledBorder("Additional Information..."));

		lbl_Chromosome = new JLabel();
		lbl_Strand = new JLabel();
		lbl_GTCode = new JLabel();

		cmb_Chromosome = new JComboBox();
		cmb_Strand = new JComboBox();
		cmb_GTCode = new JComboBox();

		GridBagConstraints c = new GridBagConstraints();
		int rowNb = 0;

		//<editor-fold defaultstate="expanded" desc="FORMAT DEPENDENT">
		lbl_Chromosome.setText("  " + Text.Dialog.chromosome + "  ");
		lbl_Strand.setText("  " + Text.Dialog.strand + "  ");
		lbl_GTCode.setText("  " + Text.Dialog.genotypeEncoding + "  ");

		switch (cImport.ImportFormat.compareTo(format)) {
			case BEAGLE:
				cmb_Chromosome.setModel(new DefaultComboBoxModel(cNetCDF.Defaults.Chromosomes));
				cmb_Chromosome.setSelectedIndex(0);
				setConstraints(c, 0, rowNb, GridBagConstraints.LINE_START);
				pnl_Questions.add(lbl_Chromosome, c);
				setConstraints(c, 1, rowNb, GridBagConstraints.WEST);
				pnl_Questions.add(cmb_Chromosome, c);
				rowNb++;
				break;
            default:
//				cmb_Chromosome.setModel(new DefaultComboBoxModel(cNetCDF.Defaults.Chromosomes));
//				cmb_Chromosome.setSelectedIndex(0);
//				cmb_Strand.setModel(new DefaultComboBoxModel(cNetCDF.Defaults.StrandType.values()));
//				cmb_Strand.setSelectedIndex(6);
//				cmb_GTCode.setModel(new DefaultComboBoxModel(cNetCDF.Defaults.GenotypeCode.values()));
//				cmb_GTCode.setSelectedIndex(0);
//
//				setMyConstraints(c,0,rowNb,GridBagConstraints.LINE_START);
//				pnl_Questions.add(lbl_Chromosome,c);
//				setMyConstraints(c,1,rowNb,GridBagConstraints.WEST);
//				pnl_Questions.add(cmb_Chromosome,c);
//				rowNb++;
//				setMyConstraints(c,0,rowNb,GridBagConstraints.LINE_START);
//				pnl_Questions.add(lbl_Strand,c);
//				setMyConstraints(c,1,rowNb,GridBagConstraints.WEST);
//				pnl_Questions.add(cmb_Strand,c);
//				rowNb++;
//				setMyConstraints(c,0,rowNb,GridBagConstraints.LINE_START);
//				pnl_Questions.add(lbl_GTCode,c);
//				setMyConstraints(c,1,rowNb,GridBagConstraints.WEST);
//				pnl_Questions.add(cmb_GTCode,c);
//				rowNb++;
                break;
		}
		//</editor-fold>

		pnl_Questions.setVisible(true);

		return pnl_Questions;
	}

	private JPanel getFooterPanel() {

		JPanel pnl_Footer = new JPanel(new GridBagLayout());

		btn_Go = new JButton();
		btn_Help = new JButton();
		btn_Cancel = new JButton();

		btn_Help.setAction(new BrowserHelpUrlAction(HelpURLs.QryURL.GWASinOneGo));

		btn_Go.setAction(new GoAction());

		btn_Cancel.setAction(new CancelAction());

		GridBagConstraints c = new GridBagConstraints();
		setConstraints(c, 0, 0, GridBagConstraints.LINE_START);
		pnl_Footer.add(btn_Cancel, c);
		setConstraints(c, 1, 0, GridBagConstraints.LINE_END);
		pnl_Footer.add(new JLabel("    "), c);
		setConstraints(c, 2, 0, GridBagConstraints.LINE_START);
		pnl_Footer.add(btn_Help, c);
		setConstraints(c, 3, 0, GridBagConstraints.LINE_END);
		pnl_Footer.add(new JLabel("    "), c);
		setConstraints(c, 4, 0, GridBagConstraints.LINE_END);
		pnl_Footer.add(btn_Go, c);

		pnl_Footer.setVisible(true);

		return pnl_Footer;
	}

	private class GoAction extends AbstractAction {

		GoAction() {

			putValue(NAME, Text.All.go);
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			try {
				if (cmb_Chromosome.getSelectedItem() != null) {
					gwasParams.setChromosome(cmb_Chromosome.getSelectedItem().toString());
				}
				if (cmb_Strand.getSelectedItem() != null) {
					gwasParams.setStrandType((StrandType) cmb_Strand.getSelectedItem());
				}
				if (cmb_GTCode.getSelectedItem() != null) {
					gwasParams.setGtCode((GenotypeEncoding) cmb_GTCode.getSelectedItem());
				}
				gwasParams.setProceed(true);
			} catch (NumberFormatException ex) {
				log.warn(null, ex);
			}
			dialog.dispose();
		}
	}

	private class CancelAction extends AbstractAction {

		CancelAction() {

			putValue(NAME, Text.All.cancel);
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			dialog.setVisible(false);
		}
	}

	private void setConstraints(GridBagConstraints c,
			int gridx,
			int gridy,
			int anchor)
	{
		c.gridx = gridx;
		c.gridy = gridy;
		c.anchor = anchor;
	}
}
