package org.gwaspi.reports;

import java.io.IOException;
import java.util.Map;
import org.gwaspi.constants.cNetCDF;
import org.gwaspi.model.MarkerKey;
import org.gwaspi.model.OperationMetadata;
import org.gwaspi.model.OperationsList;
import org.gwaspi.netCDF.operations.MarkerOperationSet;
import ucar.nc2.NetcdfFile;

/**
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 */
public class GatherHardyWeinbergData {

	private GatherHardyWeinbergData() {
	}

	public static Map<MarkerKey, Object> loadHWPval_ALT(int opId) throws IOException {

		OperationMetadata rdOPMetadata = OperationsList.getOperationMetadata(opId);

		MarkerOperationSet rdInfoMarkerSet = new MarkerOperationSet(rdOPMetadata.getStudyId(), opId);
		Map<MarkerKey, Object> rdMatrixMarkerSetMap = rdInfoMarkerSet.getOpSetMap();

		NetcdfFile assocNcFile = NetcdfFile.open(rdOPMetadata.getPathToMatrix());
		rdMatrixMarkerSetMap = rdInfoMarkerSet.fillOpSetMapWithVariable(assocNcFile, cNetCDF.HardyWeinberg.VAR_OP_MARKERS_HWPval_ALT);

		assocNcFile.close();
		return rdMatrixMarkerSetMap;
	}

	public static Map<MarkerKey, Object> loadHWPval_ALL(int opId) throws IOException {

		OperationMetadata rdOPMetadata = OperationsList.getOperationMetadata(opId);

		MarkerOperationSet rdInfoMarkerSet = new MarkerOperationSet(rdOPMetadata.getStudyId(), opId);
		Map<MarkerKey, Object> rdMatrixMarkerSetMap = rdInfoMarkerSet.getOpSetMap();

		NetcdfFile assocNcFile = NetcdfFile.open(rdOPMetadata.getPathToMatrix());
		rdMatrixMarkerSetMap = rdInfoMarkerSet.fillOpSetMapWithVariable(assocNcFile, cNetCDF.HardyWeinberg.VAR_OP_MARKERS_HWPval_ALL);

		assocNcFile.close();
		return rdMatrixMarkerSetMap;
	}

	public static Map<MarkerKey, Object> loadHWPval_CASE(int opId) throws IOException {

		OperationMetadata rdOPMetadata = OperationsList.getOperationMetadata(opId);

		MarkerOperationSet rdInfoMarkerSet = new MarkerOperationSet(rdOPMetadata.getStudyId(), opId);
		Map<MarkerKey, Object> rdMatrixMarkerSetMap = rdInfoMarkerSet.getOpSetMap();

		NetcdfFile assocNcFile = NetcdfFile.open(rdOPMetadata.getPathToMatrix());
		rdMatrixMarkerSetMap = rdInfoMarkerSet.fillOpSetMapWithVariable(assocNcFile, cNetCDF.HardyWeinberg.VAR_OP_MARKERS_HWPval_CASE);

		assocNcFile.close();
		return rdMatrixMarkerSetMap;
	}

	public static Map<MarkerKey, Object> loadHWPval_CTRL(int opId) throws IOException {

		OperationMetadata rdOPMetadata = OperationsList.getOperationMetadata(opId);

		MarkerOperationSet rdInfoMarkerSet = new MarkerOperationSet(rdOPMetadata.getStudyId(), opId);
		Map<MarkerKey, Object> rdMatrixMarkerSetMap = rdInfoMarkerSet.getOpSetMap();

		NetcdfFile assocNcFile = NetcdfFile.open(rdOPMetadata.getPathToMatrix());
		rdMatrixMarkerSetMap = rdInfoMarkerSet.fillOpSetMapWithVariable(assocNcFile, cNetCDF.HardyWeinberg.VAR_OP_MARKERS_HWPval_CTRL);

		assocNcFile.close();
		return rdMatrixMarkerSetMap;
	}

	public static Map<MarkerKey, Object> loadHWHETZY_ALT(int opId) throws IOException {

		OperationMetadata rdOPMetadata = OperationsList.getOperationMetadata(opId);

		MarkerOperationSet rdInfoMarkerSet = new MarkerOperationSet(rdOPMetadata.getStudyId(), opId);
		Map<MarkerKey, Object> rdMatrixMarkerSetMap = rdInfoMarkerSet.getOpSetMap();

		NetcdfFile assocNcFile = NetcdfFile.open(rdOPMetadata.getPathToMatrix());
		rdMatrixMarkerSetMap = rdInfoMarkerSet.fillOpSetMapWithVariable(assocNcFile, cNetCDF.HardyWeinberg.VAR_OP_MARKERS_HWHETZY_ALT);

		assocNcFile.close();
		return rdMatrixMarkerSetMap;
	}

	public static Map<MarkerKey, Object> loadHWHETZY_ALL(int opId) throws IOException {

		OperationMetadata rdOPMetadata = OperationsList.getOperationMetadata(opId);

		MarkerOperationSet rdInfoMarkerSet = new MarkerOperationSet(rdOPMetadata.getStudyId(), opId);
		Map<MarkerKey, Object> rdMatrixMarkerSetMap = rdInfoMarkerSet.getOpSetMap();

		NetcdfFile assocNcFile = NetcdfFile.open(rdOPMetadata.getPathToMatrix());
		rdMatrixMarkerSetMap = rdInfoMarkerSet.fillOpSetMapWithVariable(assocNcFile, cNetCDF.HardyWeinberg.VAR_OP_MARKERS_HWHETZY_ALL);

		assocNcFile.close();
		return rdMatrixMarkerSetMap;
	}

	public static Map<MarkerKey, Object> loadHWHETZY_CASE(int opId) throws IOException {

		OperationMetadata rdOPMetadata = OperationsList.getOperationMetadata(opId);

		MarkerOperationSet rdInfoMarkerSet = new MarkerOperationSet(rdOPMetadata.getStudyId(), opId);
		Map<MarkerKey, Object> rdMatrixMarkerSetMap = rdInfoMarkerSet.getOpSetMap();

		NetcdfFile assocNcFile = NetcdfFile.open(rdOPMetadata.getPathToMatrix());
		rdMatrixMarkerSetMap = rdInfoMarkerSet.fillOpSetMapWithVariable(assocNcFile, cNetCDF.HardyWeinberg.VAR_OP_MARKERS_HWHETZY_CASE);

		assocNcFile.close();
		return rdMatrixMarkerSetMap;
	}

	public static Map<MarkerKey, Object> loadHWHETZY_CTRL(int opId) throws IOException {

		OperationMetadata rdOPMetadata = OperationsList.getOperationMetadata(opId);

		MarkerOperationSet rdInfoMarkerSet = new MarkerOperationSet(rdOPMetadata.getStudyId(), opId);
		Map<MarkerKey, Object> rdMatrixMarkerSetMap = rdInfoMarkerSet.getOpSetMap();

		NetcdfFile assocNcFile = NetcdfFile.open(rdOPMetadata.getPathToMatrix());
		rdMatrixMarkerSetMap = rdInfoMarkerSet.fillOpSetMapWithVariable(assocNcFile, cNetCDF.HardyWeinberg.VAR_OP_MARKERS_HWHETZY_CTRL);

		assocNcFile.close();
		return rdMatrixMarkerSetMap;
	}
}
