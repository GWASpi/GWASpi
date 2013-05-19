package org.gwaspi.reports;

import java.io.IOException;
import java.util.Map;
import org.gwaspi.constants.cNetCDF;
import org.gwaspi.model.OperationMetadata;
import org.gwaspi.model.OperationsList;
import org.gwaspi.model.SampleKey;
import org.gwaspi.netCDF.operations.SampleOperationSet;
import ucar.nc2.NetcdfFile;

public class GatherQASamplesData {

	private GatherQASamplesData() {
	}

	public static Map<SampleKey, Double> loadSamplesQAMissingRatio(int opId) throws IOException {

		OperationMetadata rdOPMetadata = OperationsList.getOperationMetadata(opId);

		SampleOperationSet rdInfoSampleSet = new SampleOperationSet(rdOPMetadata.getStudyId(), opId);
		Map<SampleKey, Double> rdMatrixSampleSetMap = rdInfoSampleSet.getOpSetMap();

		NetcdfFile ncFile = NetcdfFile.open(rdOPMetadata.getPathToMatrix());
		rdMatrixSampleSetMap = rdInfoSampleSet.fillOpSetMapWithVariable(ncFile, cNetCDF.Census.VAR_OP_SAMPLES_MISSINGRAT);

		ncFile.close();
		return rdMatrixSampleSetMap;
	}

	public static Map<SampleKey, Double> loadSamplesQAHetZygRatio(int opId) throws IOException {

		OperationMetadata rdOPMetadata = OperationsList.getOperationMetadata(opId);

		SampleOperationSet rdInfoSampleSet = new SampleOperationSet(rdOPMetadata.getStudyId(), opId);
		Map<SampleKey, Double> rdMatrixSampleSetMap = rdInfoSampleSet.getOpSetMap();

		NetcdfFile ncFile = NetcdfFile.open(rdOPMetadata.getPathToMatrix());
		rdMatrixSampleSetMap = rdInfoSampleSet.fillOpSetMapWithVariable(ncFile, cNetCDF.Census.VAR_OP_SAMPLES_HETZYRAT);

		ncFile.close();
		return rdMatrixSampleSetMap;
	}
}
