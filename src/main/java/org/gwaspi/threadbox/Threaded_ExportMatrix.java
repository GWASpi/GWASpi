package org.gwaspi.threadbox;

import org.gwaspi.constants.cExport;
import org.gwaspi.constants.cExport.ExportFormat;
import org.gwaspi.gui.ProcessTab;
import org.gwaspi.netCDF.exporter.MatrixExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 */
public class Threaded_ExportMatrix extends CommonRunnable {

	private boolean startWithGUI = org.gwaspi.gui.StartGWASpi.guiMode;
	private int matrixId;
	private ExportFormat format;
	private String phenotype;

	public Threaded_ExportMatrix(
			String timeStamp,
			int matrixId,
			ExportFormat format,
			String phenotype)
	{
		super("Export Matrix", timeStamp, "Exporting Matrix", "Exporting Matrix");

		this.matrixId = matrixId;
		this.format = format;
		this.phenotype = phenotype;
	}

	protected Logger createLog() {
		return LoggerFactory.getLogger(Threaded_ExportMatrix.class);
	}

	protected void runInternal(SwingWorkerItem thisSwi) throws Exception {

		if (format != null) {
			switch (cExport.ExportFormat.compareTo(format.toString())) {
				case PLINK:
					if (startWithGUI) {
						ProcessTab.getSingleton().showTab();
					}
					MatrixExporter mEx = new MatrixExporter(matrixId);
					mEx.exportToFormat(cExport.ExportFormat.PLINK.toString(), phenotype);
					break;
				case PLINK_Binary:
					if (startWithGUI) {
						ProcessTab.getSingleton().showTab();
					}
					mEx = new MatrixExporter(matrixId);
					mEx.exportToFormat(cExport.ExportFormat.PLINK_Binary.toString(), phenotype);
					break;
				case Eigensoft_Eigenstrat:
					if (startWithGUI) {
						ProcessTab.getSingleton().showTab();
					}
					mEx = new MatrixExporter(matrixId);
					mEx.exportToFormat(cExport.ExportFormat.Eigensoft_Eigenstrat.toString(), phenotype);
					break;
				case PLINK_Transposed:
					if (startWithGUI) {
						ProcessTab.getSingleton().showTab();
					}
					mEx = new MatrixExporter(matrixId);
					mEx.exportToFormat(cExport.ExportFormat.PLINK_Transposed.toString(), phenotype);
					break;
				case BEAGLE:
					if (startWithGUI) {
						ProcessTab.getSingleton().showTab();
					}
					mEx = new MatrixExporter(matrixId);
					mEx.exportToFormat(cExport.ExportFormat.BEAGLE.toString(), phenotype);
					break;
				case GWASpi:
					if (startWithGUI) {
						ProcessTab.getSingleton().showTab();
					}
					mEx = new MatrixExporter(matrixId);
					mEx.exportToFormat(cExport.ExportFormat.GWASpi.toString(), phenotype);
					break;
				case Spreadsheet:
					if (startWithGUI) {
						ProcessTab.getSingleton().showTab();
					}
					mEx = new MatrixExporter(matrixId);
					mEx.exportToFormat(cExport.ExportFormat.Spreadsheet.toString(), phenotype);
					break;
				case MACH:
					if (startWithGUI) {
						ProcessTab.getSingleton().showTab();
					}
					mEx = new MatrixExporter(matrixId);
					mEx.exportToFormat(cExport.ExportFormat.MACH.toString(), phenotype);
					break;
				default:
					throw new IllegalArgumentException("invalid format: " + format.toString());
			}
		}
	}
}
