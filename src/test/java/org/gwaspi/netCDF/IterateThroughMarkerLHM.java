package org.gwaspi.netCDF;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.gwaspi.samples.SampleSet;
import ucar.nc2.NetcdfFile;

/**
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 */
public class IterateThroughMarkerLHM {

	private Map<String, Object> basesLHM = new LinkedHashMap<String, Object>();
	private Map<String, Object> rdSampleSetLHM = new LinkedHashMap<String, Object>();
	private Map<String, Object> wrSampleSetLHM = new LinkedHashMap<String, Object>();
	private SampleSet rdSampleSet = null;

	public IterateThroughMarkerLHM() throws IOException {
		// Iterate through pmAllelesAndStrandsLHM, use marker item position to read correct GTs from all Samples into rdMarkerIdSetLHM.
		int markerNb = 0;
		NetcdfFile rdNcFile = NetcdfFile.open("pathToMatrix");
		for (Map.Entry<String, Object> entry : basesLHM.entrySet()) {
			String markerId = entry.getKey();
			String bases = entry.getValue().toString();

			// Get alleles from read matrix
			rdSampleSetLHM = rdSampleSet.readAllSamplesGTsFromCurrentMarkerToLHM(rdNcFile, rdSampleSetLHM, markerNb);

			markerNb++;
		}
	}
}
