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

package org.gwaspi.operations.qasamples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.gwaspi.constants.NetCDFConstants;
import org.gwaspi.model.DataSetKey;
import org.gwaspi.model.MatrixKey;
import org.gwaspi.model.OperationKey;
import org.gwaspi.model.OperationMetadata;
import org.gwaspi.model.SampleKey;
import org.gwaspi.operations.NetCdfUtils;
import org.gwaspi.operations.AbstractNetCdfOperationDataSet;
import org.gwaspi.operations.OperationTypeInfo;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayInt;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.ma2.Range;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;

public class NetCdfQASamplesOperationDataSet
		extends AbstractNetCdfOperationDataSet<QASamplesOperationEntry>
		implements QASamplesOperationDataSet
{

	// - cNetCDF.Variables.VAR_OPSET: (String, key.getSampleId() + " " + key.getFamilyId()) sample keys
	// - cNetCDF.Variables.VAR_IMPLICITSET: (String, key.getId()) marker keys
	// - cNetCDF.Census.VAR_OP_SAMPLES_MISSINGRAT: (double) missing ratio for each sample
	// - cNetCDF.Census.VAR_OP_SAMPLES_MISSINGCOUNT: (int) missing count for each sample
	// - cNetCDF.Census.VAR_OP_SAMPLES_HETZYRAT: (double) heterozygosity ratio for each sample

	private ArrayDouble.D1 netCdfMissingRatios;
	private ArrayInt.D1 netCdfMissingCounts;
	private ArrayDouble.D1 netCdfHetzyRatios;

	public NetCdfQASamplesOperationDataSet(MatrixKey origin, DataSetKey parent, OperationKey operationKey) {
		super(false, origin, parent, operationKey);
	}

	public NetCdfQASamplesOperationDataSet(MatrixKey origin, DataSetKey parent) {
		this(origin, parent, null);
	}

	@Override
	public OperationTypeInfo getTypeInfo() {
		return QASamplesOperationFactory.OPERATION_TYPE_INFO;
	}

	@Override
	protected void supplementNetCdfHandler(
			NetcdfFileWriteable ncFile,
			OperationMetadata operationMetadata,
			List<Dimension> markersSpace,
			List<Dimension> chromosomesSpace,
			List<Dimension> samplesSpace)
			throws IOException
	{
		// Define Variables
		ncFile.addVariable(NetCDFConstants.Census.VAR_OP_SAMPLES_MISSINGRAT, DataType.DOUBLE, samplesSpace);
		ncFile.addVariable(NetCDFConstants.Census.VAR_OP_SAMPLES_MISSINGCOUNT, DataType.INT, samplesSpace);
		ncFile.addVariable(NetCDFConstants.Census.VAR_OP_SAMPLES_HETZYRAT, DataType.DOUBLE, samplesSpace);
	}

	@Override
	public List<Double> getMissingRatios(int from, int to) throws IOException {

		List<Double> missingRatios = new ArrayList<Double>(0);
		NetCdfUtils.readVariable(getNetCdfReadFile(), NetCDFConstants.Census.VAR_OP_SAMPLES_MISSINGRAT, from, to, missingRatios, null);
//		// the old way:
//		OperationMetadata rdOPMetadata = OperationsList.getOperation(getResultOperationKey());
//		SampleOperationSet rdInfoSampleSet = new SampleOperationSet(getResultOperationKey(), from, to);
//		Map<SampleKey, Double> rdMatrixSampleSetMap = rdInfoSampleSet.getOpSetMap();
//		NetcdfFile ncFile = NetcdfFile.open(rdOPMetadata.getPathToMatrix());
//		rdMatrixSampleSetMap = rdInfoSampleSet.fillOpSetMapWithVariable(ncFile, cNetCDF.Census.VAR_OP_SAMPLES_MISSINGRAT);
//		ncFile.close();

		return missingRatios;
	}

	@Override
	public List<Integer> getMissingCounts(int from, int to) throws IOException {

		List<Integer> missingCount = new ArrayList<Integer>(0);
		NetCdfUtils.readVariable(getNetCdfReadFile(), NetCDFConstants.Census.VAR_OP_SAMPLES_MISSINGCOUNT, from, to, missingCount, null);

		return missingCount;
	}

	@Override
	public List<Double> getHetzyRatios(int from, int to) throws IOException {

		List<Double> hetzyRatios = new ArrayList<Double>(0);
		NetCdfUtils.readVariable(getNetCdfReadFile(), NetCDFConstants.Census.VAR_OP_SAMPLES_HETZYRAT, from, to, hetzyRatios, null);

		return hetzyRatios;
	}

	@Override
	public List<QASamplesOperationEntry> getEntries(int from, int to) throws IOException {

//		SampleOperationSet rdSampleSet = new SampleOperationSet(getOperationKey(), from, to);
//		Map<SampleKey, Integer> rdSamples = rdSampleSet.getOpSetMap();
		Map<Integer, SampleKey> samplesKeys = getSamplesKeysSource().getIndicesMap(from, to);

		List<Double> missingRatios = getMissingRatios(from, to);
		List<Integer> missingCount = getMissingCounts(from, to);
		List<Double> hetzyRatios = getHetzyRatios(from, to);

		List<QASamplesOperationEntry> entries
				= new ArrayList<QASamplesOperationEntry>(missingRatios.size());
		Iterator<Double> missingRatioIt = missingRatios.iterator();
		Iterator<Integer> missingCountIt = missingCount.iterator();
		Iterator<Double> hetzyRatiosIt = hetzyRatios.iterator();
//		for (Map.Entry<SampleKey, Integer> sampleKeyIndex : rdSamples.entrySet()) {
		for (Map.Entry<Integer, SampleKey> origIndicesAndKey : samplesKeys.entrySet()) {
			entries.add(new DefaultQASamplesOperationEntry(
					origIndicesAndKey.getValue(),
					origIndicesAndKey.getKey(),
					missingRatioIt.next(),
					missingCountIt.next(),
					hetzyRatiosIt.next()));
		}

		return entries;
	}

	@Override
	protected void writeEntries(int alreadyWritten, Queue<QASamplesOperationEntry> writeBuffer) throws IOException {

		int[] origin = new int[] {alreadyWritten};
		if (netCdfMissingRatios == null) {
			// only create once, and reuse later on
			// NOTE This might be bad for multi-threading in a later stage
			netCdfMissingRatios = new ArrayDouble.D1(writeBuffer.size());
			netCdfMissingCounts = new ArrayInt.D1(writeBuffer.size());
			netCdfHetzyRatios = new ArrayDouble.D1(writeBuffer.size());
		} else if (writeBuffer.size() < netCdfMissingRatios.getShape()[0]) {
			// we end up here at the end of the processing, if, for example,
			// we have a buffer size of 10, but only 7 items are left to be written
			List<Range> reducedRange1D = new ArrayList<Range>(1);
			reducedRange1D.add(new Range(writeBuffer.size()));
			try {
				netCdfMissingRatios = (ArrayDouble.D1) netCdfMissingRatios.sectionNoReduce(reducedRange1D);
				netCdfMissingCounts = (ArrayInt.D1) netCdfMissingCounts.sectionNoReduce(reducedRange1D);
				netCdfHetzyRatios = (ArrayDouble.D1) netCdfHetzyRatios.sectionNoReduce(reducedRange1D);
			} catch (InvalidRangeException ex) {
				throw new IOException(ex);
			}
		}
		int index = 0;
		for (QASamplesOperationEntry entry : writeBuffer) {
			netCdfMissingRatios.setDouble(netCdfMissingRatios.getIndex().set(index), entry.getMissingRatio());
			netCdfMissingCounts.setInt(netCdfMissingCounts.getIndex().set(index), entry.getMissingCount());
			netCdfHetzyRatios.setDouble(netCdfHetzyRatios.getIndex().set(index), entry.getHetzyRatio());
			index++;
		}
		try {
			getNetCdfWriteFile().write(NetCDFConstants.Census.VAR_OP_SAMPLES_MISSINGRAT, origin, netCdfMissingRatios);
			getNetCdfWriteFile().write(NetCDFConstants.Census.VAR_OP_SAMPLES_MISSINGCOUNT, origin, netCdfMissingCounts);
			getNetCdfWriteFile().write(NetCDFConstants.Census.VAR_OP_SAMPLES_HETZYRAT, origin, netCdfHetzyRatios);
		} catch (InvalidRangeException ex) {
			throw new IOException(ex);
		}
	}
}
