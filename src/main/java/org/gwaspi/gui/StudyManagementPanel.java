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

package org.gwaspi.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import org.gwaspi.dao.StudyService;
import org.gwaspi.global.Text;
import org.gwaspi.gui.utils.BrowserHelpUrlAction;
import org.gwaspi.gui.utils.Dialogs;
import org.gwaspi.gui.utils.HelpURLs;
import org.gwaspi.gui.utils.LimitedLengthDocument;
import org.gwaspi.gui.utils.RequestFocusListener;
import org.gwaspi.gui.utils.RowRendererDefault;
import org.gwaspi.gui.utils.SelectAllTextFocusListener;
import org.gwaspi.model.GWASpiExplorerNodes;
import org.gwaspi.model.Study;
import org.gwaspi.model.StudyKey;
import org.gwaspi.model.StudyList;
import org.gwaspi.threadbox.MultiOperations;
import org.gwaspi.threadbox.Deleter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StudyManagementPanel extends JPanel {

	private final Logger log
			= LoggerFactory.getLogger(StudyManagementPanel.class);

	private static final class StudyTableModel extends AbstractTableModel {

		private static final String[] COLUMN_NAMES = new String[] {
				Text.Study.studyID,
				Text.Study.studyName,
				Text.All.description,
				Text.All.createDate};

		private final List<Study> studies;

		StudyTableModel(final List<Study> studies) {

			this.studies = studies;
		}

		public Study getStudyAt(final int row) {
			return studies.get(row);
		}

		@Override
		public int getRowCount() {
			return studies.size();
		}

		@Override
		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		@Override
		public String getColumnName(int column) {
			return COLUMN_NAMES[column];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {

			Study study = studies.get(rowIndex);
			switch (columnIndex) {
				case 0:
					return study.getId();
				case 1:
					return study.getName();
				case 2:
					return study.getDescription();
				case 3:
					return study.getCreationDate();
				default:
					return null;
			}
		}
	}

	private static final class StudyTable extends JTable {

		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}

		@Override
		public Component prepareRenderer(final TableCellRenderer renderer, final int rowIndex, final int vColIndex) {
			final Component component = super.prepareRenderer(renderer, rowIndex, vColIndex);
			if (component instanceof JComponent && getValueAt(rowIndex, vColIndex) != null) {
				final JComponent jComponent = (JComponent) component;
				jComponent.setToolTipText("<html>" + getValueAt(rowIndex, vColIndex).toString().replaceAll("\n", "<br>") + "</html>");
			}
			return component;
		}
	}

	public StudyManagementPanel() throws IOException {

		final JPanel pnl_StudyDesc = new JPanel();
		final JLabel lbl_NewStudyName = new JLabel();
		final JTextField txtF_NewStudyName = new JTextField();
		final JLabel lbl_Desc = new JLabel();
		final JScrollPane scrl_Desc = new JScrollPane();
		final JTextArea txtA_Desc = new JTextArea();
		final JButton btn_DeleteStudy = new JButton();
		final JButton btn_AddStudy = new JButton();
		final JPanel pnl_StudiesTable = new JPanel();
		final JPanel pnl_Footer = new JPanel();
		final JButton btn_Help = new JButton();
		final JButton btn_Back = new JButton();
		final JScrollPane scrl_StudiesTable = new JScrollPane();
		final StudyTable tbl_StudiesTable = new StudyTable();

		tbl_StudiesTable.setDefaultRenderer(Object.class, new RowRendererDefault());

		setBorder(GWASpiExplorerPanel.createMainTitledBorder(Text.Study.studies)); // NOI18N

		final Action actionAddStudy = new AddStudyAction(lbl_NewStudyName, txtF_NewStudyName, txtA_Desc);

		pnl_StudyDesc.setBorder(GWASpiExplorerPanel.createRegularTitledBorder(
				Text.Study.createNewStudy)); // NOI18N
		lbl_NewStudyName.setText(Text.Study.studyName);
		txtF_NewStudyName.setDocument(new LimitedLengthDocument(63));
		RequestFocusListener.applyOn(txtF_NewStudyName);
		txtF_NewStudyName.setAction(actionAddStudy);

		lbl_Desc.setText(Text.All.description);
		txtA_Desc.setColumns(20);
		txtA_Desc.setRows(5);
		txtA_Desc.setText(Text.All.optional);
		txtA_Desc.addFocusListener(new SelectAllTextFocusListener(Text.All.optional));

		scrl_Desc.setViewportView(txtA_Desc);
		btn_AddStudy.setAction(actionAddStudy);

		pnl_StudiesTable.setBorder(GWASpiExplorerPanel.createRegularTitledBorder(
				Text.Study.availableStudies)); // NOI18N
		tbl_StudiesTable.setModel(new StudyTableModel(getStudyService().getStudiesInfos()));
		scrl_StudiesTable.setViewportView(tbl_StudiesTable);
		btn_DeleteStudy.setAction(new DeleteStudyAction(this, tbl_StudiesTable));

		//<editor-fold defaultstate="expanded" desc="LAYOUT STUDY TABLE">
		GroupLayout pnl_StudiesTableLayout = new GroupLayout(pnl_StudiesTable);
		pnl_StudiesTable.setLayout(pnl_StudiesTableLayout);
		pnl_StudiesTableLayout.setHorizontalGroup(
				pnl_StudiesTableLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(pnl_StudiesTableLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(pnl_StudiesTableLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(scrl_StudiesTable, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
				.addComponent(btn_DeleteStudy, GroupLayout.PREFERRED_SIZE, 154, GroupLayout.PREFERRED_SIZE))
				.addContainerGap()));
		pnl_StudiesTableLayout.setVerticalGroup(
				pnl_StudiesTableLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(pnl_StudiesTableLayout.createSequentialGroup()
				.addComponent(scrl_StudiesTable, GroupLayout.PREFERRED_SIZE, 288, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(btn_DeleteStudy)
				.addContainerGap()));
		//</editor-fold>

		// <editor-fold defaultstate="expanded" desc="LAYOUT DESCRIPTION">
		GroupLayout pnl_StudyDescLayout = new GroupLayout(pnl_StudyDesc);
		pnl_StudyDesc.setLayout(pnl_StudyDescLayout);
		pnl_StudyDescLayout.setHorizontalGroup(
				pnl_StudyDescLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(pnl_StudyDescLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(pnl_StudyDescLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(lbl_NewStudyName)
				.addComponent(lbl_Desc))
				.addGap(18, 18, 18)
				.addGroup(pnl_StudyDescLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(scrl_Desc, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE)
				.addComponent(txtF_NewStudyName, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE))
				.addContainerGap())
				.addGroup(GroupLayout.Alignment.TRAILING, pnl_StudyDescLayout.createSequentialGroup()
				.addContainerGap(605, Short.MAX_VALUE)
				.addComponent(btn_AddStudy)
				.addGap(14, 14, 14)));
		pnl_StudyDescLayout.setVerticalGroup(
				pnl_StudyDescLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(pnl_StudyDescLayout.createSequentialGroup()
				.addGroup(pnl_StudyDescLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(lbl_NewStudyName)
				.addComponent(txtF_NewStudyName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(pnl_StudyDescLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(lbl_Desc)
				.addGroup(pnl_StudyDescLayout.createSequentialGroup()
				.addGap(2, 2, 2)
				.addComponent(scrl_Desc, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(btn_AddStudy)));
		// </editor-fold>

		//<editor-fold defaultstate="expanded" desc="FOOTER">
		btn_Back.setAction(new BackAction());
		btn_Help.setAction(new BrowserHelpUrlAction(HelpURLs.QryURL.createStudy));
		GroupLayout pnl_FooterLayout = new GroupLayout(pnl_Footer);
		pnl_Footer.setLayout(pnl_FooterLayout);
		pnl_FooterLayout.setHorizontalGroup(
				pnl_FooterLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(pnl_FooterLayout.createSequentialGroup()
				.addComponent(btn_Back, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
				.addGap(18, 437, Short.MAX_VALUE)
				.addComponent(btn_Help)));

		pnl_FooterLayout.linkSize(SwingConstants.HORIZONTAL, new Component[]{btn_Back, btn_Help});
		pnl_FooterLayout.setVerticalGroup(
				pnl_FooterLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(pnl_FooterLayout.createSequentialGroup()
				.addContainerGap(0, Short.MAX_VALUE)
				.addGroup(pnl_FooterLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(btn_Back)
				.addComponent(btn_Help))));
		//</editor-fold>

		//<editor-fold defaultstate="expanded" desc="LAYOUT">
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(pnl_StudiesTable, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(pnl_StudyDesc, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(pnl_Footer, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addContainerGap()));
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(pnl_StudiesTable, GroupLayout.PREFERRED_SIZE, 362, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(pnl_StudyDesc, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(pnl_Footer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		//</editor-fold>
	}

	private static StudyService getStudyService() {
		return StudyList.getStudyService();
	}

	private static class AddStudyAction extends AbstractAction {

		private final JLabel newStudyNameLabel;
		private final JTextField newStudyName;
		private final JTextArea newStudyDesc;

		AddStudyAction(
				final JLabel newStudyNameLabel,
				final JTextField newStudyName,
				final JTextArea newStudyDesc)
		{
			putValue(NAME, Text.Study.addStudy);

			this.newStudyNameLabel = newStudyNameLabel;
			this.newStudyName = newStudyName;
			this.newStudyDesc = newStudyDesc;
		}

		@Override
		public void actionPerformed(ActionEvent evt) {

			try {
				String study_name = newStudyName.getText();
				if (!study_name.isEmpty()) {
					newStudyNameLabel.setForeground(Color.black);
					String study_description = newStudyDesc.getText();
					if (newStudyDesc.getText().equals(Text.All.optional)) {
						study_description = "";
					}

					StudyKey newStudy = getStudyService().insertStudy(new Study(study_name, study_description));
					GWASpiExplorerNodes.insertStudyNode(newStudy);
					GWASpiExplorerPanel.getSingleton().selectNode(newStudy);
				} else {
					Dialogs.showWarningDialogue(Text.Study.warnNoStudyName);
					newStudyNameLabel.setForeground(Color.red);
				}
			} catch (final IOException ex) {
				throw new IllegalStateException(ex);
			}
		}
	}

	private static class DeleteStudyAction extends AbstractAction {

		private final Component dialogParent;
		private final JTable studiesTable;

		DeleteStudyAction(final Component dialogParent, final JTable studiesTable) {

			putValue(NAME, Text.Study.deleteStudy);

			this.dialogParent = dialogParent;
			this.studiesTable = studiesTable;
		}

		@Override
		public void actionPerformed(ActionEvent evt) {

			if (studiesTable.getSelectedRow() != -1) {
				final StudyTableModel model = (StudyTableModel) studiesTable.getModel();
				final int[] selectedStudyRows = studiesTable.getSelectedRows();
				final List<Study> selectedStudies = new ArrayList<Study>(selectedStudyRows.length);
				for (int i = 0; i < selectedStudyRows.length; i++) {
					selectedStudies.add(model.getStudyAt(selectedStudyRows[i]));
				}

				final int option = JOptionPane.showConfirmDialog(dialogParent, Text.Study.confirmDelete1 + Text.Study.confirmDelete2);
				if (option == JOptionPane.YES_OPTION) {
					final int deleteReportsOption = JOptionPane.showConfirmDialog(dialogParent, Text.Reports.confirmDelete);
					for (final Study selectedStudy : selectedStudies) {
						final StudyKey studyKey = StudyKey.valueOf(selectedStudy);
						if (deleteReportsOption != JOptionPane.CANCEL_OPTION) {
							final boolean deleteReports = (deleteReportsOption == JOptionPane.YES_OPTION);
							final Deleter studyDeleter = new Deleter(studyKey, deleteReports);
							// test if the deleted item is required for a queued worker
							if (MultiOperations.canBeDoneNow(studyDeleter)) {
									MultiOperations.queueTask(studyDeleter);
							} else {
								Dialogs.showWarningDialogue(Text.Processes.cantDeleteRequiredItem);
							}
						}
					}
				}
			}
		}
	}
}
