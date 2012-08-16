package org.gwaspi.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CombinedRangeXYPlot;

/**
 *
 * @author u56124
 */
public class TestReports extends javax.swing.JPanel {

	public static void main(final String[] args) throws InterruptedException, FileNotFoundException, IOException {

		File plinkFile = new File("/home/fernando/work/Moapi/input/plink.assoc");
		File outputFile = new File("/home/fernando/work/Moapi/input/plink.png");
		HashSet redMarkersHS = new HashSet();
		createCombinedPNGFromAssocUnadjLogPvsPos(plinkFile, outputFile, redMarkersHS, 4048, 700);

		System.out.println("This is a main test");

	}

	public static File createCombinedPNGFromAssocUnadjLogPvsPos(File plinkReport, File outputFile, HashSet redMarkersHS, int width, int height) throws FileNotFoundException, IOException {
		//Generating XY scatter plot with loaded data
		CombinedRangeXYPlot combinedPlot = org.gwaspi.reports.PlinkReportLoaderCombined.loadAssocUnadjLogPvsPos(plinkReport, redMarkersHS);

		JFreeChart chart = new JFreeChart("P value x Chr position", JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, true);

		try {
			ChartUtilities.saveChartAsPNG(outputFile,
					chart,
					width,
					height);
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart.");
			e.printStackTrace();
		}

		return null;
	}
}
