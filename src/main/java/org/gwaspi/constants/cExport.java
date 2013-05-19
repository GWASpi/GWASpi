package org.gwaspi.constants;

public class cExport {

	public static final String separator_PLINK = " ";
	public static final String separator_PLINK_big = "\t";
	public static final String separator_BEAGLE = " ";
	public static final String separator_MACH = " ";
	public static final String separator_REPORTS = "\t";
	public static final String separator_SAMPLE_INFO = "\t";

	public static enum ExportFormat {

		BEAGLE,
		Eigensoft_Eigenstrat,
		GWASpi,
		MACH,
		PLINK,
		PLINK_Binary,
		PLINK_Transposed,
		Spreadsheet;

		public static ExportFormat compareTo(String str) {
			try {
				return valueOf(str);
			} catch (Exception ex) {
				return PLINK;
			}
		}
	}

	private cExport() {
	}
}
