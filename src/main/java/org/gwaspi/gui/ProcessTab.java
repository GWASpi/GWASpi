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

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.gwaspi.global.Config;
import org.gwaspi.global.Text;
import org.gwaspi.gui.utils.Dialogs;
import org.gwaspi.gui.utils.LogDocument;
import org.gwaspi.gui.utils.RowRendererProcessOverviewWithAbortIcon;
import org.gwaspi.gui.utils.TaskQueueTableModel;
import org.gwaspi.progress.ProcessDetailsChangeEvent;
import org.gwaspi.progress.ProcessStatusChangeEvent;
import org.gwaspi.progress.ProgressEvent;
import org.gwaspi.progress.ProgressListener;
import org.gwaspi.progress.ProgressSource;
import org.gwaspi.progress.SuperSwingProgressListener;
import org.gwaspi.progress.SwingProgressListener;
import org.gwaspi.threadbox.TaskQueue;
import org.gwaspi.threadbox.TaskQueueStatusChangedEvent;
import org.gwaspi.threadbox.TaskQueueListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessTab extends JPanel implements TaskQueueListener, ProgressListener {

	private static final Logger log = LoggerFactory.getLogger(MatrixAnalysePanel.class);

	private final JScrollPane scrl_Overview;
	private final JPanel pnl_progress;
	private final List<SwingProgressListener> taskProgressDisplays;
	private static ProcessTab singleton = null;

	private ProcessTab() {

		this.taskProgressDisplays = new ArrayList<SwingProgressListener>();

		final JPanel pnl_top = new JPanel();
		this.scrl_Overview = new JScrollPane();
		final JScrollPane scrl_progress = new JScrollPane();
		this.pnl_progress = new JPanel();
		this.pnl_progress.setLayout(new GridLayout(0, 1));
		scrl_progress.setViewportView(this.pnl_progress);
		pnl_top.setLayout(new BorderLayout(CurrentStudyPanel.GAP, CurrentStudyPanel.GAP));
		pnl_top.add(scrl_Overview, BorderLayout.NORTH);
		pnl_top.add(scrl_progress, BorderLayout.CENTER);

		final JPanel pnl_center = new JPanel();
		final JScrollPane scrl_ProcessLog = new JScrollPane();
		scrl_progress.setMinimumSize(new Dimension(300, 250));
		final JTextArea txtA_ProcessLog = new JTextArea();
		pnl_center.setLayout(new BorderLayout(CurrentStudyPanel.GAP, CurrentStudyPanel.GAP));
		pnl_center.add(scrl_ProcessLog, BorderLayout.CENTER);

		final JPanel pnl_bottom = new JPanel();
		final JPanel pnl_bottomButtonsEast = new JPanel();
		final JButton btn_Save = new JButton();
		pnl_bottomButtonsEast.add(btn_Save);
		pnl_bottom.setLayout(new BorderLayout(CurrentStudyPanel.GAP, CurrentStudyPanel.GAP));
		pnl_bottom.add(pnl_bottomButtonsEast, BorderLayout.EAST);

		this.setLayout(new BorderLayout(CurrentStudyPanel.GAP, CurrentStudyPanel.GAP));
		this.add(pnl_top, BorderLayout.NORTH);
		this.add(pnl_center, BorderLayout.CENTER);
		this.add(pnl_bottom, BorderLayout.SOUTH);

		this.setBorder(GWASpiExplorerPanel.createMainTitledBorder(Text.Processes.processes)); // NOI18N

		txtA_ProcessLog.setColumns(20);
		txtA_ProcessLog.setRows(5);
		txtA_ProcessLog.setEditable(false);
		scrl_ProcessLog.setViewportView(txtA_ProcessLog);

		btn_Save.setAction(new SaveAsAction(txtA_ProcessLog));

		if (!StartGWASpi.logOff) {
			txtA_ProcessLog.setDocument(new LogDocument());
		}
	}

	public static ProcessTab getSingleton() {

		if (singleton == null) {
			singleton = new ProcessTab();
//			SwingWorkerItemList.addTaskListener(singleton);
		}

		return singleton;
	}

	@Override
	public void processDetailsChanged(ProcessDetailsChangeEvent evt) {
		updateProcessOverview();
	}

	@Override
	public void statusChanged(ProcessStatusChangeEvent evt) {
		updateProcessOverview();
	}

	@Override
	public void progressHappened(ProgressEvent evt) {}

	private static class ProcessesTable extends JTable {

		public ProcessesTable() {

			addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseMoved(MouseEvent me) {
					//displayColumnCursor(me, tmpTable);
				}
			});
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					int rowIndex = getSelectedRow();
					int colIndex = getSelectedColumn();
					if (colIndex == 7) { // Abort
						throw new UnsupportedOperationException("needs to be implemented!"); // TODO implement aborting the selected task, also maybe add pausing support!
					}
				}
			});
			setDefaultRenderer(Object.class, new RowRendererProcessOverviewWithAbortIcon());
			setSelectionMode(0);

			setModel(new TaskQueueTableModel(TaskQueue.getInstance()));
			scrollRectToVisible(getBounds());
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return false; // Renders column 0 uneditable.
		}
	}

	@Override
	public void taskStatusChanged(final TaskQueueStatusChangedEvent evt) {

		final ProgressSource progressSource = evt.getTask().getProgressSource();

		evt.getProgressSource().addProgressListener(this); // XXX This is not very nice.. to have two different, yet very similar progress sources, one for the outer task (Swing*Item) and one for the inner task (CommonRunnable (in case of SwingWorkerItem), SwingDeleterItem (in case of SwingDeleterItem))

		SwingProgressListener taskProgressDisplay
				= SuperSwingProgressListener.newDisplay(progressSource);
		progressSource.addProgressListener(taskProgressDisplay);

		final JComponent taskGUI = taskProgressDisplay.getMainComponent();
		final Insets allEdgesSmall = new Insets(
				CurrentStudyPanel.GAP_SMALL,
				CurrentStudyPanel.GAP_SMALL,
				CurrentStudyPanel.GAP_SMALL,
				CurrentStudyPanel.GAP_SMALL);
		taskGUI.setBorder(new CompoundBorder(
				new EmptyBorder(allEdgesSmall),
				new CompoundBorder(new TitledBorder(""), new EmptyBorder(allEdgesSmall))));
		pnl_progress.add(taskGUI);
		pnl_progress.add(taskGUI, 0); // TODO maybe we should instead replace the compleeted ones? and/or add at the bottom and scroll down?
		taskProgressDisplays.add(taskProgressDisplay);

		updateProcessOverview();
	}

	private void updateProcessOverview() {

		if (StartGWASpi.guiMode) {
			final JTable tmpTable = new ProcessesTable();
			final int x = scrl_Overview.getHorizontalScrollBar().getValue();
			final int y = scrl_Overview.getVerticalScrollBar().getValue();
			scrl_Overview.setViewportView(tmpTable);
			scrl_Overview.getHorizontalScrollBar().setValue(x);
			scrl_Overview.getVerticalScrollBar().setValue(y);
		}
	}

	public void showTab() {
		StartGWASpi.getMainFrame().getTabs().setSelectedComponent(this);
	}

	private static class SaveAsAction extends AbstractAction {

		private static final String LOG_FILE_NAME = "process.log";

		private final JTextArea processLog;

		SaveAsAction(JTextArea processLog) {

			this.processLog = processLog;
			putValue(NAME, Text.All.save);
		}

		@Override
		public void actionPerformed(ActionEvent evt) {

			FileWriter writer = null;
			try {
				// XXX This would be better done by letting the user choose the file directly, not just the directory, right? With sane defaults, it can be just as easy, but more powerfull
				final File selectedPath = Dialogs.selectDirectoryDialog(
						Config.PROPERTY_LOG_DIR,
						"Choose directory to save '" + LOG_FILE_NAME + "' to",
						processLog);
				if (selectedPath == null) {
					return;
				}
				File newFile = new File(selectedPath, LOG_FILE_NAME);
				writer = new FileWriter(newFile);
				writer.write(processLog.getText());
			} catch (IOException ex) {
				log.error(null, ex);
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException ex) {
						log.warn(null, ex);
					}
				}
			}
		}
	}

	/**
	 * Method to change cursor based on some arbitrary rule.
	 */
	private void displayColumnCursor(MouseEvent me, JTable table) {

		Point p = me.getPoint();
		int column = table.columnAtPoint(p);
		String columnName = table.getColumnName(column);
		if (columnName.equals(Text.Reports.zoom)) {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else if (columnName.equals(Text.Reports.ensemblLink)) {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else if (columnName.equals(Text.Reports.NCBILink)) {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else {
			setCursor(Cursor.getDefaultCursor());
		}
	}
}
