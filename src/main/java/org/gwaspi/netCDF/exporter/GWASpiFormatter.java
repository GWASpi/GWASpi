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

package org.gwaspi.netCDF.exporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.gwaspi.constants.ExportConstants;
import org.gwaspi.model.DataSetMetadata;
import org.gwaspi.model.DataSetSource;
import org.gwaspi.model.MatrixMetadata;
import org.gwaspi.model.SampleInfo;
import org.gwaspi.progress.IndeterminateProgressHandler;
import org.gwaspi.progress.IntegerProgressHandler;
import org.gwaspi.progress.ProcessInfo;
import org.gwaspi.progress.ProcessStatus;
import org.gwaspi.progress.SubProcessInfo;
import org.gwaspi.progress.SuperProgressSource;

public class GWASpiFormatter implements Formatter {

	@Override
	public void export(
			String exportPath,
			DataSetMetadata rdDataSetMetadata,
			DataSetSource dataSetSource,
			final SuperProgressSource superProgressSource,
			String phenotype)
			throws IOException
	{
		final ProcessInfo exportPI = new SubProcessInfo(
				superProgressSource.getInfo(),
				"export",
				"export in the GWASpi internal format");
		final ProcessInfo exportSamplesPI = new SubProcessInfo(
				exportPI,
				"export samples",
				"format and export sample infos");
		final ProcessInfo exportMarkersPI = new SubProcessInfo(
				exportPI,
				"export markers",
				"export marker infos and genotypes");

		final SuperProgressSource exportPS = new SuperProgressSource(exportPI);
		superProgressSource.replaceSubProgressSource(PLACEHOLDER_PS_EXPORT, exportPS, null);
		exportPS.setNewStatus(ProcessStatus.INITIALIZING);

		final IntegerProgressHandler exportSamplesPS = new IntegerProgressHandler(exportSamplesPI, 0, dataSetSource.getNumSamples() - 1);
		exportPS.addSubProgressSource(exportSamplesPS, 0.8);

		final IndeterminateProgressHandler exportMarkersPS = new IndeterminateProgressHandler(exportMarkersPI);
		exportPS.addSubProgressSource(exportMarkersPS, 0.2);

		final File exportDir = Utils.checkDirPath(exportPath);
		final String friendlyNameSanitized
				= Utils.sanitizeForFileName(rdDataSetMetadata.getFriendlyName());

		exportSamplesPS.setNewStatus(ProcessStatus.INITIALIZING);
		String sep = ExportConstants.SEPARATOR_SAMPLE_INFO;
		BufferedWriter sampleInfoBW = null;
		try {
			//<editor-fold defaultstate="expanded" desc="SAMPLE INFO FILE">
			FileWriter sampleInfoFW = new FileWriter(new File(exportDir.getPath(),
					"SampleInfo_" + friendlyNameSanitized + ".txt"));
			sampleInfoBW = new BufferedWriter(sampleInfoFW);

			sampleInfoBW.append("FamilyID\tSampleID\tFatherID\tMotherID\tSex\tAffection\tCategory\tDesease\tPopulation\tAge");
			sampleInfoBW.append("\n");

			//Iterate through all samples
			int sampleNb = 0;
			exportSamplesPS.setNewStatus(ProcessStatus.RUNNING);
			for (SampleInfo sampleInfo : dataSetSource.getSamplesInfosSource()) {
//				FamilyID
//				SampleID
//				FatherID
//				MotherID
//				Sex
//				Affection
//				Category
//				Desease
//				Population
//				Age

				sampleInfo = org.gwaspi.netCDF.exporter.Utils.formatSampleInfo(sampleInfo);

				String familyId = sampleInfo.getFamilyId();
				String fatherId = sampleInfo.getFatherId();
				String motherId = sampleInfo.getMotherId();
				String sex = sampleInfo.getSexStr();
				String affection = sampleInfo.getAffectionStr();
				String category = sampleInfo.getCategory();
				String desease = sampleInfo.getDisease();
				String population = sampleInfo.getPopulation();
				int age = sampleInfo.getAge();

				sampleInfoBW.write(familyId);
				sampleInfoBW.write(sep);
				sampleInfoBW.write(sampleInfo.getSampleId());
				sampleInfoBW.write(sep);
				sampleInfoBW.write(fatherId);
				sampleInfoBW.write(sep);
				sampleInfoBW.write(motherId);
				sampleInfoBW.write(sep);
				sampleInfoBW.write(sex);
				sampleInfoBW.write(sep);
				sampleInfoBW.write(affection);
				sampleInfoBW.write(sep);
				sampleInfoBW.write(category);
				sampleInfoBW.write(sep);
				sampleInfoBW.write(desease);
				sampleInfoBW.write(sep);
				sampleInfoBW.write(population);
				sampleInfoBW.write(sep);
				sampleInfoBW.write(Integer.toString(age)); // NOTE This conversion is required, because Writer#write(int) actually writes a char, not the int value.
				sampleInfoBW.write('\n');
				sampleInfoBW.flush();

				exportSamplesPS.setProgress(sampleNb);
				sampleNb++;
			}
			//</editor-fold>
		} finally {
			if (sampleInfoBW != null) {
				sampleInfoBW.close();
			}
		}
		exportSamplesPS.setNewStatus(ProcessStatus.COMPLEETED);

		//<editor-fold defaultstate="expanded" desc="GWASpi netCDF MATRIX">
		exportMarkersPS.setNewStatus(ProcessStatus.INITIALIZING);
		File origFile = MatrixMetadata.generatePathToNetCdfFileGeneric(rdDataSetMetadata);
		File newFile = new File(exportDir.getPath(), friendlyNameSanitized + ".nc");
		exportMarkersPS.setNewStatus(ProcessStatus.RUNNING);
		if (!origFile.exists()) {
			throw new IOException("Could not find internal markers storage file");
		}
		org.gwaspi.global.Utils.copyFile(origFile, newFile);
		exportMarkersPS.setNewStatus(ProcessStatus.COMPLEETED);
		//</editor-fold>
		exportPS.setNewStatus(ProcessStatus.COMPLEETED);
	}
}
