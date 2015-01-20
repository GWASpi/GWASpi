/*
 * Copyright (C) 2013 Universitat Pompeu Fabra
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gwaspi.gui.reports;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.gwaspi.global.Text;
import org.gwaspi.gui.BackAction;
import org.gwaspi.gui.GWASpiExplorerPanel;
import org.gwaspi.gui.utils.BrowserHelpUrlAction;
import org.gwaspi.gui.utils.HelpURLs;
import org.gwaspi.gui.utils.IntegerInputVerifier;
import org.gwaspi.gui.utils.RowRendererDefault;
import org.gwaspi.model.DataSetKey;
import org.gwaspi.model.OperationKey;
import org.gwaspi.model.Study;
import org.gwaspi.reports.OutputHardyWeinberg;
import org.gwaspi.reports.ReportParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Report_HardyWeinbergSummary extends JPanel {

	private static final Logger log
			= LoggerFactory.getLogger(Report_HardyWeinbergSummary.class);

	// Variables declaration - do not modify
	private final File reportFile;
	private final OperationKey operationKey;
	private final JButton btn_Get;
	private final JButton btn_Save;
	private final JButton btn_Back;
	private final JButton btn_Help;
	private final JPanel pnl_Footer;
	private final JPanel pnl_Summary;
	private final JLabel lbl_suffix1;
	private final JScrollPane scrl_ReportTable;
	private final JTable tbl_ReportTable;
	private final JFormattedTextField txt_NRows;
	// End of variables declaration
	private final ReportParser reportParser;

	public Report_HardyWeinbergSummary(final OperationKey operationKey, final String reportFileName) {

		String reportName = GWASpiExplorerPanel.getSingleton().getTree().getLastSelectedPathComponent().toString();
		reportName = reportName.substring(reportName.indexOf('-') + 2);
		final String nRowsSuffix = Text.Reports.radio1Suffix_pVal;
		final String helpUrlSuffix = HelpURLs.QryURL.hwReport;
		this.reportParser = new OutputHardyWeinberg.HWReportParser();

		this.operationKey = operationKey;

		String reportPath = "";
		try {
			reportPath = Study.constructReportsPath(operationKey.getParentMatrixKey().getStudyKey());
		} catch (IOException ex) {
			log.error(null, ex);
		}
		reportFile = new File(reportPath + reportFileName);

		pnl_Summary = new JPanel();
		txt_NRows = new JFormattedTextField();
		txt_NRows.setInputVerifier(new IntegerInputVerifier());
		txt_NRows.setValue(100);
		txt_NRows.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent evt) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						txt_NRows.selectAll();
					}
				});
			}
		});
		lbl_suffix1 = new JLabel();
		btn_Get = new JButton();
		scrl_ReportTable = new JScrollPane();
		tbl_ReportTable = new JTable() {
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		tbl_ReportTable.setDefaultRenderer(Object.class, new RowRendererDefault());

		pnl_Footer = new JPanel();
		btn_Save = new JButton();
		btn_Back = new JButton();
		btn_Help = new JButton();

		setBorder(GWASpiExplorerPanel.createMainTitledBorder(
				Text.Reports.report + ": " + reportName)); // NOI18N

		pnl_Summary.setBorder(GWASpiExplorerPanel.createRegularTitledBorder(Text.Reports.summary));

		final Action loadReportAction = new LoadReportAction(
				reportFile, tbl_ReportTable, txt_NRows, reportParser);

		txt_NRows.setInputVerifier(new IntegerInputVerifier());
		txt_NRows.setHorizontalAlignment(JFormattedTextField.TRAILING);
		lbl_suffix1.setText(nRowsSuffix);

		//<editor-fold defaultstate="expanded" desc="LAYOUT1">
		GroupLayout pnl_SummaryLayout = new GroupLayout(pnl_Summary);
		pnl_Summary.setLayout(pnl_SummaryLayout);
		pnl_SummaryLayout.setHorizontalGroup(
				pnl_SummaryLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(pnl_SummaryLayout.createSequentialGroup()
				.addContainerGap()
				.addComponent(txt_NRows, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(lbl_suffix1)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 152, Short.MAX_VALUE)
				.addComponent(btn_Get, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE)
				.addContainerGap()));
		pnl_SummaryLayout.setVerticalGroup(
				pnl_SummaryLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(pnl_SummaryLayout.createSequentialGroup()
				.addGroup(pnl_SummaryLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(txt_NRows, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(lbl_suffix1)
				.addComponent(btn_Get))
				.addContainerGap()));
		//</editor-fold>

		scrl_ReportTable.setViewportView(tbl_ReportTable);

		//<editor-fold defaultstate="expanded" desc="FOOTER">
		GroupLayout pnl_FooterLayout = new GroupLayout(pnl_Footer);
		pnl_Footer.setLayout(pnl_FooterLayout);
		pnl_FooterLayout.setHorizontalGroup(
				pnl_FooterLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(GroupLayout.Alignment.TRAILING, pnl_FooterLayout.createSequentialGroup()
				.addContainerGap()
				.addComponent(btn_Back, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE)
				.addGap(18, 18, 18)
				.addComponent(btn_Help, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 321, Short.MAX_VALUE)
				.addComponent(btn_Save, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)
				.addContainerGap()));
		pnl_FooterLayout.linkSize(SwingConstants.HORIZONTAL, new Component[]{btn_Back, btn_Help});
		pnl_FooterLayout.setVerticalGroup(
				pnl_FooterLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(pnl_FooterLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(btn_Save)
				.addComponent(btn_Back)
				.addComponent(btn_Help)));
		//</editor-fold>

		//<editor-fold defaultstate="expanded" desc="LAYOUT">
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(pnl_Footer, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(scrl_ReportTable, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
				.addComponent(pnl_Summary, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addContainerGap()));
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
				.addComponent(pnl_Summary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(scrl_ReportTable)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(pnl_Footer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addContainerGap()));
		//</editor-fold>

		tbl_ReportTable.setModel(new DefaultTableModel(
				new Object[][] {
					{null, null, null, "Go!"}
				},
				new String[] {"", "", "", ""}));

		txt_NRows.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				final int key = e.getKeyChar();
				if (key == KeyEvent.VK_ENTER) {
					loadReportAction.actionPerformed(null);
				}
			}
		});
		btn_Get.setAction(loadReportAction);
		btn_Save.setAction(new Report_Analysis.SaveAsAction(
				operationKey.getParentMatrixKey().getStudyKey(),
				reportFileName,
				tbl_ReportTable,
				txt_NRows));
		btn_Back.setAction(new BackAction(new DataSetKey(operationKey)));
		btn_Help.setAction(new BrowserHelpUrlAction(helpUrlSuffix));

		loadReportAction.actionPerformed(null);
	}

	private static class LoadReportAction extends AbstractAction {

		private final File reportFile;
		private final JTable reportTable;
		private final JFormattedTextField nRows;
		private final ReportParser reportParser;

		LoadReportAction(File reportFile, JTable reportTable, JFormattedTextField nRows, final ReportParser reportParser) {

			this.reportFile = reportFile;
			this.reportTable = reportTable;
			this.nRows = nRows;
			this.reportParser = reportParser;
			putValue(NAME, Text.All.get);
		}

		@Override
		public void actionPerformed(ActionEvent evt) {

			if (reportFile.exists() && !reportFile.isDirectory()) {
				final int numRowsToFetch = Integer.parseInt(nRows.getText());

				final List<Object[]> tableRows;
				try {
					tableRows = reportParser.parseReport(reportFile, numRowsToFetch, false);
				} catch (final IOException ex) {
					log.error(null, ex);
					// TODO maybe inform the user through a dialog?
					return;
				}

				final Object[][] tableMatrix = tableRows.toArray(new Object[0][0]);

				TableModel model = new DefaultTableModel(tableMatrix, reportParser.getColumnHeaders());
				reportTable.setModel(model);

				TableRowSorter sorter = new TableRowSorter(model) {
					Comparator<Object> comparator = new Comparator<Object>() {
						@Override
						public int compare(Object o1, Object o2) {
							try {
								Double d1 = Double.parseDouble(o1.toString());
								Double d2 = Double.parseDouble(o2.toString());
								return d1.compareTo(d2);
							} catch (final NumberFormatException exDouble) {
								try {
									Integer i1 = Integer.parseInt(o1.toString());
									Integer i2 = Integer.parseInt(o2.toString());
									return i1.compareTo(i2);
								} catch (final NumberFormatException exInteger) {
									log.warn("To compare objects are neither both Double, nor both Integer: {} {}", o1, o2);
									return o1.toString().compareTo(o2.toString());
								}
							}
						}
					};

					@Override
					public Comparator getComparator(int column) {
						return comparator;
					}

					@Override
					public boolean useToString(int column) {
						return false;
					}
				};

				reportTable.setRowSorter(sorter);
			}
		}
	}
}
