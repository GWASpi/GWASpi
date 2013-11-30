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

package org.gwaspi.netCDF.operations;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.gwaspi.model.ChromosomeInfo;
import org.gwaspi.model.ChromosomeKey;
import org.gwaspi.model.MarkerKey;
import org.gwaspi.model.OperationKey;
import org.gwaspi.operations.OperationDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarkerOperationSet<V> extends AbstractOperationSet<MarkerKey, V> {

	private static final Logger log = LoggerFactory.getLogger(MarkerOperationSet.class);

	public MarkerOperationSet(OperationKey operationKey) throws IOException {
		super(operationKey, MarkerKey.KEY_FACTORY);
	}

	public MarkerOperationSet(OperationKey operationKey, int from, int to) throws IOException {
		super(operationKey, MarkerKey.KEY_FACTORY, from, to);
	}

//	//<editor-fold defaultstate="expanded" desc="CHROMOSOME INFO">
//	public static Map<ChromosomeKey, ChromosomeInfo> getChrInfoSetMap(OperationKey operationKey) throws IOException {
//
//		Map<ChromosomeKey, ChromosomeInfo> chrInfoMap;
//
//		OperationDataSet opDS = OperationFactory.generateOperationDataSet(operationKey);
//		Map<Integer, ChromosomeKey> chromosomes = opDS.getChromosomes();
//		Iterator<ChromosomeInfo> chromosomeInfosIt = opDS.getChromosomeInfos().iterator();
//		chrInfoMap = new LinkedHashMap<ChromosomeKey, ChromosomeInfo>(chromosomes.size());
//		for (ChromosomeKey chromosomeKey : chromosomes.values()) {
//			chrInfoMap.put(chromosomeKey, chromosomeInfosIt.next());
//		}
//
////		NetcdfFile ncfile = null;
////		try {
////			chrInfoMap = new LinkedHashMap<ChromosomeKey, ChromosomeInfo>(
////			ncfile = NetcdfFile.open(getOperationMetadata().getPathToMatrix());
////
////			// GET NAMES OF CHROMOSOMES
////			Variable var = ncfile.findVariable(cNetCDF.Variables.VAR_CHR_IN_MATRIX);
////
////			if (null == var) {
////				return null;
////			}
////
////			DataType dataType = var.getDataType();
////			int[] varShape = var.getShape();
////
////			try {
////				if (dataType == DataType.CHAR) {
////					ArrayChar.D2 markerSetAC = (ArrayChar.D2) var.read("(0:" + (varShape[0] - 1) + ":1, 0:7:1)");
////					chrInfoMap = NetCdfUtils.writeD2ArrayCharToMapKeys(markerSetAC, null);
////				}
////			} catch (InvalidRangeException ex) {
////				throw new IOException(ex);
////			}
////
////			// GET INFO FOR EACH CHROMOSOME
////			var = ncfile.findVariable(cNetCDF.Variables.VAR_CHR_INFO); // Nb of markers, first physical position, last physical position, start index number in MarkerSet
////			if (var == null) {
////				return null;
////			}
////			dataType = var.getDataType();
////			varShape = var.getShape();
////
////			try {
////				if (dataType == DataType.INT) {
////					ArrayInt.D2 chrSetAI = (ArrayInt.D2) var.read("(0:" + (varShape[0] - 1) + ":1, 0:3:1)");
////					NetCdfUtils.writeD2ArrayIntToChromosomeInfoMapValues(chrSetAI, chrInfoMap);
////				}
////			} catch (InvalidRangeException ex) {
////				throw new IOException(ex);
////			}
////		} catch (IOException ex) {
////			log.error("Cannot open file", ex);
////		} finally {
////			if (null != ncfile) {
////				try {
////					ncfile.close();
////				} catch (IOException ex) {
////					log.warn("Cannot close file", ex);
////				}
////			}
////		}
//
//		return chrInfoMap;
//	}
//	//</editor-fold>

	//<editor-fold defaultstate="expanded" desc="OPERATION-SET PICKERS">
//	public Map<MarkerKey, V> pickValidMarkerSetItemsByValue(NetcdfFile ncfile, String variable, Set<Object> criteria, boolean includes) {
//		Map<MarkerKey, V> returnMap = new LinkedHashMap<MarkerKey, V>();
//		Map<MarkerKey, V> readMap = fillOpSetMapWithVariable(ncfile, variable);
//
//		if (includes) {
//			for (Map.Entry<MarkerKey, V> entry : readMap.entrySet()) {
//				MarkerKey key = entry.getKey();
//				V value = entry.getValue();
//				if (criteria.contains(value)) {
//					returnMap.put(key, value);
//				}
//			}
//		} else {
//			for (Map.Entry<MarkerKey, V> entry : readMap.entrySet()) {
//				MarkerKey key = entry.getKey();
//				V value = entry.getValue();
//				if (!criteria.contains(value)) {
//					returnMap.put(key, value);
//				}
//			}
//		}
//
//		return returnMap;
//	}
	//</editor-fold>
}
