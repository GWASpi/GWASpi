package org.gwaspi.samples;

/**
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 */
public class DummySampleInfo {

	private DummySampleInfo() {
	}

	public static Object[] createDummySampleValues() {

		Object[] dummySampleValues = new Object[10];

		dummySampleValues[0] = "0";  // FamilyID
		dummySampleValues[1] = "0";  // SampleID
		dummySampleValues[2] = "0";  // FatherID
		dummySampleValues[3] = "0";  // MotherID
		dummySampleValues[4] = "0";  // Sex
		dummySampleValues[5] = "0";  // Affection
		dummySampleValues[6] = "0";  // Category
		dummySampleValues[7] = "0";  // Desease
		dummySampleValues[8] = "0";  // Population
		dummySampleValues[9] = 0;    // Age (INT, default=0)

		return dummySampleValues;
	}
}
