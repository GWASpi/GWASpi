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

package org.gwaspi.operations.merge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.gwaspi.constants.NetCDFConstants;
import org.gwaspi.global.Text;
import org.gwaspi.model.GenotypesList;
import org.gwaspi.model.MarkerKey;
import org.gwaspi.model.MatrixKey;
import org.gwaspi.model.SampleKey;
import org.gwaspi.model.SamplesKeysSource;
import org.gwaspi.netCDF.loader.DataSetDestination;
import org.gwaspi.operations.DefaultOperationTypeInfo;
import org.gwaspi.operations.MatrixOperationFactory;
import org.gwaspi.operations.OperationManager;
import org.gwaspi.operations.OperationTypeInfo;
import org.gwaspi.progress.DefaultProcessInfo;
import org.gwaspi.progress.ProcessInfo;

public class MergeMarkersMatrixOperation extends AbstractMergeMarkersMatrixOperation {

	private static final ProcessInfo PROCESS_INFO = new DefaultProcessInfo(
			Text.Trafo.mergeMarkersOnly,
			Text.Trafo.mergeMethodMarkerJoin);

	private static final OperationTypeInfo OPERATION_TYPE_INFO
			= new DefaultOperationTypeInfo(
					true,
					Text.Trafo.mergeMarkersOnly,
					Text.Trafo.mergeMethodMarkerJoin,
					null);
	static {
		// NOTE When converting to OSGi, this would be done in bundle init,
		//   or by annotations.
		OperationManager.registerOperationFactory(new MatrixOperationFactory(
				MergeMarkersMatrixOperation.class, OPERATION_TYPE_INFO));
	}

	public MergeMarkersMatrixOperation(
			MergeMatrixOperationParams params,
			DataSetDestination dataSetDestination)
			throws IOException
	{
		super(
				params,
				dataSetDestination);
	}

	@Override
	public OperationTypeInfo getTypeInfo() {
		return OPERATION_TYPE_INFO;
	}

	@Override
	public ProcessInfo getProcessInfo() {
		return PROCESS_INFO;
	}

	@Override
	public MatrixKey call() throws IOException {

		// Get combo SampleSet with position[] (wrPos, rdMatrixNb, rdPos)
		Map<SampleKey, int[]> wrSampleSetMap = getSampleSetWithIndicesMap(dataSetSource1.getSamplesKeysSource(), dataSetSource2.getSamplesKeysSource());
		// Keep rdMatrix1Metadata from Matrix1. SampleSet is constant
		SamplesKeysSource sampleKeys = dataSetSource1.getSamplesKeysSource();

		return mergeMatrices(
				wrSampleSetMap,
				sampleKeys,
				OPERATION_TYPE_INFO.getName(),
				OPERATION_TYPE_INFO.getDescription());
	}

	@Override
	protected void writeGenotypes(
			Map<SampleKey, int[]> wrSampleSetMap,
			Collection<MarkerKey> wrComboSortedMarkers)
			throws IOException
	{
		// Get SampleId index from each Matrix
		// Iterate through wrSampleSetMap
		getDataSetDestination().startLoadingAlleles(true);
		for (int[] sampleIndices : wrSampleSetMap.values()) { // position[rdPos matrix 1, rdPos matrix 2]
			final int readDataSet1SampleIndex = sampleIndices[0];
			final int readDataSet2SampleIndex = sampleIndices[1];

			GenotypesList dataSet1SampleGenotypes = dataSetSource1.getSamplesGenotypesSource().get(readDataSet1SampleIndex);
			GenotypesList dataSet2SampleGenotypes = dataSetSource2.getSamplesGenotypesSource().get(readDataSet2SampleIndex);

			// Fill wrSortedMingledMarkerMap with matrix 1+2 Genotypes
			Map<MarkerKey, byte[]> wrComboSortedMarkerGTs = new LinkedHashMap<MarkerKey, byte[]>(wrComboSortedMarkers.size());
			for (MarkerKey markerKey : wrComboSortedMarkers) {
				byte[] genotype;
				final int dataSet1MarkerIndex = dataSetSource1.getMarkersKeysSource().indexOf(markerKey);
				if (dataSet1MarkerIndex >= 0) {
					genotype = dataSet1SampleGenotypes.get(dataSet1MarkerIndex);
				} else {
					final int dataSet2MarkerIndex = dataSetSource2.getMarkersKeysSource().indexOf(markerKey);
					if (dataSet2MarkerIndex >= 0) {
						genotype = dataSet2SampleGenotypes.get(dataSet2MarkerIndex);
					} else {
						genotype = NetCDFConstants.Defaults.DEFAULT_GT;
					}
				}

				wrComboSortedMarkerGTs.put(markerKey, genotype);
			}

			addSampleGTAlleles(readDataSet1SampleIndex, new ArrayList<byte[]>(wrComboSortedMarkerGTs.values()));
		}
		getDataSetDestination().finishedLoadingAlleles();
	}

	private static Map<SampleKey, int[]> getSampleSetWithIndicesMap(SamplesKeysSource sampleKeys1, SamplesKeysSource sampleKeys2) {
		Map<SampleKey, int[]> resultMap = new LinkedHashMap<SampleKey, int[]>();

		int rdPos = 0;
		for (SampleKey key : sampleKeys1) {
			int[] position = new int[] {rdPos, -1}; // rdPos matrix 1
			resultMap.put(key, position);
			rdPos++;
		}

		rdPos = 0;
		for (SampleKey key : sampleKeys2) {
			// IF SAMPLE ALLREADY EXISTS IN MATRIX1 SUBSTITUTE VALUES WITH MATRIX2
			if (resultMap.containsKey(key)) {
				int[] position = resultMap.get(key);
				position[1] = rdPos; // rdPos matrix 2
				resultMap.put(key, position);
			}

			rdPos++;
		}

		return resultMap;
	}
}
