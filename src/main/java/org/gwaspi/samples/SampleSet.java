package org.gwaspi.samples;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.gwaspi.netCDF.matrices.MatrixMetadata;
import ucar.ma2.ArrayByte;
import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayDouble;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

/**
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 */
public class SampleSet {

	//SAMPLESET_MEATADATA
	private int sampleset_id = Integer.MIN_VALUE;   //id
	private int sampleSetSize = 0;
	private MatrixMetadata matrixMetadata;
	private Map<String, Object> sampleIdSetLHM = new LinkedHashMap<String, Object>();

	public SampleSet(int studyId, int matrixId) throws IOException {
		matrixMetadata = new MatrixMetadata(matrixId);
		sampleSetSize = matrixMetadata.getMarkerSetSize();

	}

	public SampleSet(int studyId, String netCDFName) throws IOException {
		matrixMetadata = new MatrixMetadata(netCDFName);
		sampleSetSize = matrixMetadata.getMarkerSetSize();

	}

	public SampleSet(int studyId, String netCDFPath, String netCDFName) throws IOException {
		matrixMetadata = new MatrixMetadata(netCDFPath, studyId, netCDFName);
		sampleSetSize = matrixMetadata.getMarkerSetSize();

	}

	////////// ACCESSORS /////////
	public int getSampleSetId() {
		return sampleset_id;
	}

	public int getSampleSetSize() {
		return sampleSetSize;
	}

	public MatrixMetadata getMatrixMetadata(List<String> _sampleSetAL) {
		return matrixMetadata;
	}

	//<editor-fold defaultstate="collapsed" desc="SAMPLESET FETCHERS">
	public Map<String, Object> getSampleIdSetLHM() throws InvalidRangeException {
		NetcdfFile ncfile = null;

		try {
			ncfile = NetcdfFile.open(matrixMetadata.getPathToMatrix());
			Variable var = ncfile.findVariable(org.gwaspi.constants.cNetCDF.Variables.VAR_SAMPLESET);

			if (null == var) {
				return null;
			}

			int[] varShape = var.getShape();
			Dimension markerSetDim = ncfile.findDimension(org.gwaspi.constants.cNetCDF.Dimensions.DIM_SAMPLESET);

			try {
				sampleSetSize = markerSetDim.getLength();
				ArrayChar.D2 sampleSetAC = (ArrayChar.D2) var.read("(0:" + (sampleSetSize - 1) + ":1, 0:" + (varShape[1] - 1) + ":1)");

				sampleIdSetLHM = org.gwaspi.netCDF.operations.Utils.writeD2ArrayCharToLHMKeys(sampleSetAC);

			} catch (IOException ioe) {
				System.out.println("Cannot read data: " + ioe);
			} catch (InvalidRangeException e) {
				System.out.println("Cannot read data: " + e);
			}

		} catch (IOException ioe) {
			System.out.println("Cannot open file: " + ioe);
		} finally {
			if (null != ncfile) {
				try {
					ncfile.close();
				} catch (IOException ioe) {
					System.out.println("Cannot close file: " + ioe);
				}
			}
		}

		return sampleIdSetLHM;
	}

	public Map<String, Object> getSampleIdSetLHM(String matrixImportPath) throws InvalidRangeException {
		NetcdfFile ncfile = null;

		try {
			ncfile = NetcdfFile.open(matrixImportPath);
			Variable var = ncfile.findVariable(org.gwaspi.constants.cNetCDF.Variables.VAR_SAMPLESET);

			if (null == var) {
				return null;
			}

			int[] varShape = var.getShape();
			Dimension markerSetDim = ncfile.findDimension(org.gwaspi.constants.cNetCDF.Dimensions.DIM_SAMPLESET);

			try {
				sampleSetSize = markerSetDim.getLength();
				ArrayChar.D2 sampleSetAC = (ArrayChar.D2) var.read("(0:" + (sampleSetSize - 1) + ":1, 0:" + (varShape[1] - 1) + ":1)");

				sampleIdSetLHM = org.gwaspi.netCDF.operations.Utils.writeD2ArrayCharToLHMKeys(sampleSetAC);

			} catch (IOException ioe) {
				System.out.println("Cannot read data: " + ioe);
			} catch (InvalidRangeException e) {
				System.out.println("Cannot read data: " + e);
			}

		} catch (IOException ioe) {
			System.out.println("Cannot open file: " + ioe);
		} finally {
			if (null != ncfile) {
				try {
					ncfile.close();
				} catch (IOException ioe) {
					System.out.println("Cannot close file: " + ioe);
				}
			}
		}

		return sampleIdSetLHM;
	}

	//</editor-fold>
	//<editor-fold defaultstate="collapsed" desc="SAMPLESET FILLERS">
	public Map<String, Object> readAllSamplesGTsFromCurrentMarkerToLHM(NetcdfFile rdNcFile, Map<String, Object> rdLhm, int markerNb) throws IOException {
		try {
			Variable genotypes = rdNcFile.findVariable(org.gwaspi.constants.cNetCDF.Variables.VAR_GENOTYPES);

			if (null == genotypes) {
				return rdLhm;
			}
			try {
				int[] varShape = genotypes.getShape();
//                Dimension markerSetDim = ncfile.findDimension(org.gwaspi.constants.cNetCDF.DIM_MARKERSET);
//                ArrayChar.D3 gt_ACD3 = (ArrayChar.D3) genotypes.read("(" + markerNb + ":" + markerNb + ":1, 0:" + (markerSetDim.getLength() - 1) + ":1, 0:" + (varShape[2] - 1) + ":1)");

				Dimension sampleSetDim = rdNcFile.findDimension(org.gwaspi.constants.cNetCDF.Dimensions.DIM_SAMPLESET);
//                ArrayChar.D3 gt_ACD3 = (ArrayChar.D3) genotypes.read("(0:" + (sampleSetDim.getLength() - 1) + ":1, "
//                                                                     + markerNb + ":" + markerNb + ":1, "
//                                                                     + "0:" + (varShape[2] - 1) + ":1)");

				ArrayByte.D3 gt_ACD3 = (ArrayByte.D3) genotypes.read("(0:" + (sampleSetDim.getLength() - 1) + ":1, "
						+ markerNb + ":" + markerNb + ":1, "
						+ "0:" + (varShape[2] - 1) + ":1)");

				int[] shp = gt_ACD3.getShape();
				int reducer = 0;
				if (shp[0] == 1) {
					reducer++;
				}
				if (shp[1] == 1) {
					reducer++;
				}
				if (shp[2] == 1) {
					reducer++;
				}

				if (reducer == 1) {
					ArrayByte.D2 gt_ACD2 = (ArrayByte.D2) gt_ACD3.reduce();
					rdLhm = org.gwaspi.netCDF.operations.Utils.writeD2ArrayByteToLHMValues(gt_ACD2, rdLhm);
				} else if (reducer == 2) {
					ArrayByte.D1 gt_ACD1 = (ArrayByte.D1) gt_ACD3.reduce();
					rdLhm = org.gwaspi.netCDF.operations.Utils.writeD1ArrayByteToLHMValues(gt_ACD1, rdLhm);
				}

//                ArrayChar.D2 gt_ACD2 = (ArrayChar.D2) gt_ACD3.reduce();
//                rdLhm = org.gwaspi.netCDF.operations.Utils.writeD2ArrayCharToLHMValues(gt_ACD2, rdLhm);

			} catch (InvalidRangeException e) {
				System.out.println("Cannot read data: " + e);
			}
		} catch (IOException iOException) {
			System.out.println("Cannot open file: " + iOException);
		}

		return rdLhm;
	}

	public Map<String, Object> fillSampleIdSetLHMWithVariable(NetcdfFile ncfile, String variable) {
		try {
			Variable var = ncfile.findVariable(variable);

			if (null == var) {
				return null;
			}

			DataType dataType = var.getDataType();
			int[] varShape = var.getShape();

			sampleSetSize = varShape[0];
			if (dataType == DataType.CHAR) {
				ArrayChar.D2 sampleSetAC = (ArrayChar.D2) var.read("(0:" + (sampleSetSize - 1) + ":1, 0:" + (varShape[1] - 1) + ":1)");
				sampleIdSetLHM = org.gwaspi.netCDF.operations.Utils.writeD2ArrayCharToLHMValues(sampleSetAC, sampleIdSetLHM);
			}
			if (dataType == DataType.DOUBLE) {
				ArrayDouble.D1 sampleSetAF = (ArrayDouble.D1) var.read("(0:" + (sampleSetSize - 1) + ":1");
				sampleIdSetLHM = org.gwaspi.netCDF.operations.Utils.writeD1ArrayDoubleToLHMValues(sampleSetAF, sampleIdSetLHM);
			}

		} catch (IOException ioe) {
			System.out.println("Cannot open file: " + ioe);
		} catch (InvalidRangeException e) {
			System.out.println("Cannot read data: " + e);
		} finally {
			if (null != ncfile) {
				try {
					ncfile.close();
				} catch (IOException ioe) {
					System.out.println("Cannot close file: " + ioe);
				}
			}
		}

		return sampleIdSetLHM;
	}

	public Map<String, Object> fillSampleIdSetLHMWithVariable(Map<String, Object> lhm, String variable) {
		NetcdfFile ncfile = null;

		try {
			ncfile = NetcdfFile.open(matrixMetadata.getPathToMatrix());
			Variable var = ncfile.findVariable(variable);

			if (null == var) {
				return null;
			}

			DataType dataType = var.getDataType();
			int[] varShape = var.getShape();
			Dimension sampleSetDim = ncfile.findDimension(org.gwaspi.constants.cNetCDF.Dimensions.DIM_SAMPLESET);

			try {
				sampleSetSize = sampleSetDim.getLength();
				if (dataType == DataType.CHAR) {
					ArrayChar.D2 sampleSetAC = (ArrayChar.D2) var.read("(0:" + (sampleSetSize - 1) + ":1, 0:" + (varShape[1] - 1) + ":1)");
					sampleIdSetLHM = org.gwaspi.netCDF.operations.Utils.writeD2ArrayCharToLHMValues(sampleSetAC, lhm);
				}
				if (dataType == DataType.DOUBLE) {
					ArrayDouble.D1 sampleSetAF = (ArrayDouble.D1) var.read("(0:" + (sampleSetSize - 1) + ":1");
					sampleIdSetLHM = org.gwaspi.netCDF.operations.Utils.writeD1ArrayDoubleToLHMValues(sampleSetAF, lhm);
				}

			} catch (IOException ioe) {
				System.out.println("Cannot read data: " + ioe);
			} catch (InvalidRangeException e) {
				System.out.println("Cannot read data: " + e);
			}

		} catch (IOException ioe) {
			System.out.println("Cannot open file: " + ioe);
		} finally {
			if (null != ncfile) {
				try {
					ncfile.close();
				} catch (IOException ioe) {
					System.out.println("Cannot close file: " + ioe);
				}
			}
		}

		return sampleIdSetLHM;
	}

	public Map<String, Object> fillSampleIdSetLHMWithFilterVariable(Map<String, Object> lhm, String variable, int filterPos) {
		NetcdfFile ncfile = null;

		try {
			ncfile = NetcdfFile.open(matrixMetadata.getPathToMatrix());
			Variable var = ncfile.findVariable(variable);

			if (null == var) {
				return null;
			}

			DataType dataType = var.getDataType();
			int[] varShape = var.getShape();
			Dimension sampleSetDim = ncfile.findDimension(org.gwaspi.constants.cNetCDF.Dimensions.DIM_SAMPLESET);

			try {
				sampleSetSize = sampleSetDim.getLength();
				if (dataType == DataType.CHAR) {
					ArrayChar.D2 sampleSetAC = (ArrayChar.D2) var.read("(0:" + (sampleSetSize - 1) + ":1, " + filterPos + ":" + filterPos + ":1)");
					sampleIdSetLHM = org.gwaspi.netCDF.operations.Utils.writeD2ArrayCharToLHMValues(sampleSetAC, lhm);
				}
			} catch (IOException ioe) {
				System.out.println("Cannot read data: " + ioe);
			} catch (InvalidRangeException e) {
				System.out.println("Cannot read data: " + e);
			}

		} catch (IOException ioe) {
			System.out.println("Cannot open file: " + ioe);
		} finally {
			if (null != ncfile) {
				try {
					ncfile.close();
				} catch (IOException ioe) {
					System.out.println("Cannot close file: " + ioe);
				}
			}
		}

		return sampleIdSetLHM;
	}

	public Map<String, Object> fillLHMWithDefaultValue(Map<String, Object> lhm, Object defaultVal) {
		for (Iterator<String> it = lhm.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			lhm.put(key, defaultVal);
		}
		return lhm;
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="SAMPLESET PICKERS">
	public Map<String, Object> pickValidSampleSetItemsByDBField(Object poolId, Map<String, Object> lhm, String dbField, Set<Object> criteria, boolean include) throws IOException {
		Map<String, Object> returnLHM = new LinkedHashMap<String, Object>();
		List<Map<String, Object>> rs = org.gwaspi.samples.SampleManager.getAllSampleInfoFromDBByPoolID(poolId);

		int pickCounter = 0;
		if (include) {
			for (Iterator<String> it = lhm.keySet().iterator(); it.hasNext();) {
				String key = it.next();
				for (int i = 0; i < rs.size(); i++) // loop through rows of result set
				{
					//PREVENT PHANTOM-DB READS EXCEPTIONS - CAUTION!!
					if (!rs.isEmpty() && rs.get(i).size() == org.gwaspi.constants.cDBSamples.T_CREATE_SAMPLES_INFO.length) {
						if (rs.get(i).get(org.gwaspi.constants.cDBSamples.f_SAMPLE_ID).toString().equals(key.toString())) {
							if (criteria.contains(rs.get(i).get(dbField).toString())) {
								returnLHM.put(key, pickCounter);
							}
						}
					}
				}
				pickCounter++;
			}
		} else {
			for (Iterator<String> it = lhm.keySet().iterator(); it.hasNext();) {
				String key = it.next();
				for (int i = 0; i < rs.size(); i++) // loop through rows of result set
				{
					//PREVENT PHANTOM-DB READS EXCEPTIONS - CAUTION!!
					if (!rs.isEmpty() && rs.get(i).size() == org.gwaspi.constants.cDBSamples.T_CREATE_SAMPLES_INFO.length) {
						if (rs.get(i).get(org.gwaspi.constants.cDBSamples.f_SAMPLE_ID).toString().equals(key.toString())) {
							if (!criteria.contains(rs.get(i).get(dbField).toString())) {
								returnLHM.put(key, pickCounter);
							}
						}
					}
				}
				pickCounter++;
			}
		}

		return returnLHM;
	}

	public Map<String, Object> pickValidSampleSetItemsByNetCDFValue(Map<String, Object> lhm, String variable, Set<Object> criteria, boolean include) {
		Map<String, Object> returnLHM = new LinkedHashMap<String, Object> ();
		lhm = this.fillSampleIdSetLHMWithVariable(lhm, variable);

		int pickCounter = 0;
		if (include) {
			for (Iterator<String> it = lhm.keySet().iterator(); it.hasNext();) {
				String key = it.next();
				Object value = lhm.get(key);
				if (criteria.contains(value)) {
					returnLHM.put(key, pickCounter);
				}
				pickCounter++;
			}
		} else {
			for (Iterator<String> it = lhm.keySet().iterator(); it.hasNext();) {
				String key = it.next();
				Object value = lhm.get(key);
				if (!criteria.contains(value)) {
					returnLHM.put(key, pickCounter);
				}
				pickCounter++;
			}
		}

		return returnLHM;
	}

	public Map<String, Object> pickValidSampleSetItemsByNetCDFFilter(Map<String, Object> lhm, String variable, int fiterPos, Set<Object> criteria, boolean include) {
		Map<String, Object> returnLHM = new LinkedHashMap<String, Object>();
		lhm = this.fillSampleIdSetLHMWithFilterVariable(lhm, variable, fiterPos);

		int pickCounter = 0;
		if (include) {
			for (Iterator<String> it = lhm.keySet().iterator(); it.hasNext();) {
				String key = it.next();
				Object value = lhm.get(key);
				if (criteria.contains(value)) {
					returnLHM.put(key, pickCounter);
				}
				pickCounter++;
			}
		} else {
			for (Iterator<String> it = lhm.keySet().iterator(); it.hasNext();) {
				String key = it.next();
				Object value = lhm.get(key);
				if (!criteria.contains(value)) {
					returnLHM.put(key, pickCounter);
				}
				pickCounter++;
			}
		}

		return returnLHM;
	}

	public Map<String, Object> pickValidSampleSetItemsByNetCDFKey(Map<String, Object> lhm, Set<Object> criteria, boolean include) throws IOException {
		Map<String, Object> returnLHM = new LinkedHashMap<String, Object>();

		int pickCounter = 0;
		if (include) {
			for (Iterator<String> it = lhm.keySet().iterator(); it.hasNext();) {
				String key = it.next();
				if (criteria.contains(key)) {
					returnLHM.put(key, pickCounter);
				}
				pickCounter++;
			}
		} else {
			for (Iterator<String> it = lhm.keySet().iterator(); it.hasNext();) {
				String key = it.next();
				if (!criteria.contains(key)) {
					returnLHM.put(key, pickCounter);
				}
				pickCounter++;
			}
		}

		return returnLHM;
	}
	//</editor-fold>
}
