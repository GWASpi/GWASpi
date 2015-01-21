/*
 * Copyright (C) 2015 Universitat Pompeu Fabra
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

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.gwaspi.global.Text;
import org.gwaspi.gui.utils.Dialogs;
import org.gwaspi.reports.ReportParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LoadReportAction extends AbstractAction {

	private static final Logger log
			= LoggerFactory.getLogger(LoadReportAction.class);

	private final File reportFile;
	private final JTable reportTable;
	private final JFormattedTextField nRows;
	private final ReportParser reportParser;

	LoadReportAction(
			final File reportFile,
			final JTable reportTable,
			final JFormattedTextField nRows,
			final ReportParser reportParser)
	{
		this.reportFile = reportFile;
		this.reportTable = reportTable;
		this.nRows = nRows;
		this.reportParser = reportParser;
		putValue(NAME, Text.All.get);
	}


	private static class DoubleOrIntegerComparator implements Comparator<Object> {

		@Override
		public int compare(final Object o1, final Object o2) {
			try {
				final Double d1 = Double.parseDouble(o1.toString());
				final Double d2 = Double.parseDouble(o2.toString());
				return d1.compareTo(d2);
			} catch (final NumberFormatException exDouble) {
				try {
					final Integer i1 = Integer.parseInt(o1.toString());
					final Integer i2 = Integer.parseInt(o2.toString());
					return i1.compareTo(i2);
				} catch (final NumberFormatException exInteger) {
					log.warn("To compare objects are neither both Double,"
							+ " nor both Integer: {} {}",
							o1,
							o2);
					return o1.toString().compareTo(o2.toString());
				}
			}
		}
	}

	private static class DoubleOrIntegerTableRowSorter extends TableRowSorter {

		private final Comparator comparator;

		DoubleOrIntegerTableRowSorter(final TableModel model) {
			super(model);

			this.comparator = new DoubleOrIntegerComparator();
		}

		@Override
		public Comparator getComparator(final int column) {
			return comparator;
		}

		@Override
		public boolean useToString(final int column) {
			return false;
		}
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

			final TableModel model
					= new DefaultTableModel(tableMatrix, reportParser.getColumnHeaders());
			reportTable.setModel(model);
			reportTable.setRowSorter(new DoubleOrIntegerTableRowSorter(model));
		} else {
			final String message
					= "The report could not be loaded, because the file does either not exist,"
							+ " or is a directory: \"" + reportFile.getAbsolutePath() + "\"";
			log.warn(message);
			Dialogs.showWarningDialogue(message);
		}
	}
}