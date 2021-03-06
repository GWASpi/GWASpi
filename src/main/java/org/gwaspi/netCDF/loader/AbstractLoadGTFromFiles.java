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

package org.gwaspi.netCDF.loader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.gwaspi.constants.ImportConstants.ImportFormat;
import org.gwaspi.constants.NetCDFConstants;
import org.gwaspi.constants.NetCDFConstants.Defaults.GenotypeEncoding;
import org.gwaspi.constants.NetCDFConstants.Defaults.StrandType;
import org.gwaspi.model.MarkerKey;
import org.gwaspi.model.MarkerMetadata;
import org.gwaspi.model.SampleInfo;
import org.gwaspi.model.SampleKey;
import org.gwaspi.model.StudyKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLoadGTFromFiles implements GenotypesLoader {

	private final Logger log
			= LoggerFactory.getLogger(AbstractLoadGTFromFiles.class);

	private final MetadataLoader markerSetMetadataLoader;
	private final ImportFormat format;
	private final StrandType matrixStrand;
	private final boolean hasDictionary;
	private GenotypeEncoding guessedGTCode;

	public AbstractLoadGTFromFiles(
			MetadataLoader markerSetMetadataLoader,
			ImportFormat format,
			StrandType matrixStrand,
			boolean hasDictionary
			)
	{
		this.markerSetMetadataLoader = markerSetMetadataLoader;
		this.format = format;
		this.matrixStrand = matrixStrand;
		this.hasDictionary = hasDictionary;
		this.guessedGTCode = GenotypeEncoding.UNKNOWN;
	}

	@Override
	public ImportFormat getFormat() {
		return format;
	}

	@Override
	public StrandType getMatrixStrand() {
		return matrixStrand;
	}

	public MetadataLoader getMetadataLoader() {
		return markerSetMetadataLoader;
	}

	@Override
	public boolean isHasDictionary() {
		return hasDictionary;
	}

	public GenotypeEncoding getGuessedGTCode() {
		return guessedGTCode;
	}

	public void setGuessedGTCode(GenotypeEncoding guessedGTCode) {
		this.guessedGTCode = guessedGTCode;
	}

	protected void addAdditionalBigDescriptionProperties(StringBuilder descSB, GenotypesLoadDescription loadDescription) {
	}

	protected boolean isLoadAllelePerSample() {
		return true;
	}

	private void loadMarkerMetadata(GenotypesLoadDescription loadDescription, DataSetDestination samplesReceiver) throws IOException {

		samplesReceiver.startLoadingMarkerMetadatas(false);
		markerSetMetadataLoader.loadMarkers(samplesReceiver, loadDescription);
		samplesReceiver.finishedLoadingMarkerMetadatas();
	}

	//<editor-fold defaultstate="expanded" desc="PROCESS GENOTYPES">
	@Override
	public void processData(
			GenotypesLoadDescription loadDescription,
			Map<SampleKey, SampleInfo> sampleInfos,
			DataSetDestination dataReceiver)
			throws IOException
	{
		final MarkerInfoExtractorDataSetDestination markerInfoExtractor
				= new MarkerInfoExtractorDataSetDestination(dataReceiver);
		loadMarkerMetadata(loadDescription, markerInfoExtractor);

		dataReceiver.startLoadingAlleles(isLoadAllelePerSample());
		loadGenotypes(loadDescription, sampleInfos, markerInfoExtractor.getMarkerInfos(), dataReceiver);
		dataReceiver.finishedLoadingAlleles();
	}

	protected void loadGenotypes(
			GenotypesLoadDescription loadDescription,
			Map<SampleKey, SampleInfo> sampleInfos,
			Map<MarkerKey, MarkerMetadata> markerInfos,
			DataSetDestination samplesReceiver)
			throws IOException
	{
		final Collection<SampleInfo> sampleInfos2 = sampleInfos.values();
		final Set<MarkerKey> markerKeys = markerInfos.keySet();

		File gtFile = new File(loadDescription.getGtDirPath());
		File[] gtFilesToImport;
		if (gtFile.isDirectory()) {
			gtFilesToImport = org.gwaspi.global.Utils.listFiles(loadDescription.getGtDirPath());
		} else {
			gtFilesToImport = new File[] {new File(loadDescription.getGtDirPath())};
		}
		int sampleIndex = 0;
		for (SampleInfo sampleInfo : sampleInfos2) {
			// PURGE MarkerIdMap
			Map<MarkerKey, byte[]> alleles = fillMap(markerKeys, NetCDFConstants.Defaults.DEFAULT_GT);

			for (File gtFileToImport : gtFilesToImport) {
				try {
					Iterator<Map.Entry<MarkerKey, byte[]>> markerGtIt = iterator(
							loadDescription.getStudyKey(),
							sampleInfo.getKey(),
							gtFileToImport);
					while (markerGtIt.hasNext()) {
						Map.Entry<MarkerKey, byte[]> markerGt = markerGtIt.next();
						alleles.put(markerGt.getKey(), markerGt.getValue());
					}

					if (Thread.interrupted()) {
						throw new InterruptedException();
					}
				} catch (IOException ex) {
					log.warn(null, ex);
				} catch (InterruptedException ex) {
					log.warn(null, ex);
					// TODO Write some cleanup code for when thread has been interrupted
				}
			}

			// WRITING GENOTYPE DATA INTO netCDF FILE
			samplesReceiver.addSampleGTAlleles(sampleIndex, new ArrayList<byte[]>(alleles.values()));
//					org.gwaspi.operations.Utils.saveSingleSampleGTsToMatrix(ncfile, alleles, sampleIndex);

			sampleIndex++;
			if ((sampleIndex == 1) || (sampleIndex % 100 == 0)) {
				log.info("Done processing sample {} / {}", sampleIndex,
						sampleInfos.size());
			}
		}
	}

	static <K, V> Map<K, V> fillMap(Collection<K> keys, V value) {

		Map<K, V> result = new LinkedHashMap<K, V>(keys.size());

		for (K key : keys) {
			result.put(key, value);
		}

		return result;
	}

	/**
	 * Loads all the alleles from a single sample.
	 * @param studyKey the sample is going to be stored in this study
	 * @param sampleKey uniquely specifies the sample to be loaded
	 *   (in the containing set)
	 * @param file to load the sample from
	 * @return
	 * @throws IOException
	 */
	protected abstract Iterator<Map.Entry<MarkerKey, byte[]>> iterator(
			StudyKey studyKey,
			SampleKey sampleKey,
			File file)
			throws IOException;
	//</editor-fold>

	static void logAsWhole(String startTime, int studyId, String dirPath, ImportFormat format, String matrixName, String description) throws IOException {
		// LOG OPERATION IN STUDY HISTORY
		final StringBuilder operationDesc = new StringBuilder(1024);
		operationDesc
				.append("\nLoaded raw ").append(format).append(" genotype data from path ").append(dirPath).append(".\n")
				.append("Start Time: ").append(startTime).append('\n')
				.append("End Time: ").append(org.gwaspi.global.Utils.getMediumDateTimeAsString()).append('\n')
				.append("Data stored in matrix ").append(matrixName).append('\n')
				.append("Description: ").append(description).append('\n');
		org.gwaspi.global.Utils.logOperationInStudyDesc(operationDesc.toString(), studyId);
	}

	static List<SampleKey> extractKeys(Collection<SampleInfo> sampleInfos) {

		List<SampleKey> sampleKeys = new ArrayList<SampleKey>(sampleInfos.size());

		for (SampleInfo sampleInfo : sampleInfos) {
			sampleKeys.add(sampleInfo.getKey());
		}

		return sampleKeys;
	}
}
