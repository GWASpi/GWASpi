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

package org.gwaspi.netCDF;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.ma2.ArrayByte;
import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayInt;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class BenchmarkReadCompareD3Array {

	private static final Logger log = LoggerFactory.getLogger(BenchmarkReadCompareD3Array.class);

	public static void main(String[] arg) throws InvalidRangeException, IOException {
		int method = 2; // 1=int, 2=byte, 3=char
		int markerNb = 100000;
		String filename = "/media/data/work/GWASpi/genotypes/method" + method + "mk" + markerNb + ".nc"; // XXX system dependent path
		NetcdfFile ncfile = null;

		long timeAverage = 0;

		try {
			ncfile = NetcdfFile.open(filename);
			List<Dimension> dims = ncfile.getDimensions();
			Dimension samplesDim = dims.get(0);
			Dimension markersDim = dims.get(1);
			Dimension allelesDim = dims.get(2);

			String varName = "genotypes";
			Variable genotypes = ncfile.findVariable(varName);
			if (null == genotypes) {
				return;
			}

			try {
				switch (method) {
					case 1: {
						// int
						for (int sampleNb = 0; sampleNb < samplesDim.getLength(); sampleNb++) {
							Date start = new Date();
							ArrayInt.D3 rdArray = (ArrayInt.D3) genotypes.read(sampleNb + ":" + sampleNb + ":1,"
									+ " 0:" + (markersDim.getLength() - 1) + ":1,"
									+ " 0:" + (allelesDim.getLength() - 1) + ":1");

							Date end = new Date();
							long tmpTime = end.getTime() - start.getTime();
							timeAverage = ((timeAverage * sampleNb) + tmpTime) / (sampleNb + 1);
							if (sampleNb % 10 == 0) {
								log.info("Processing {}", sampleNb);
							}
						}
						log.info("Time average with int: {}", timeAverage);
						break;
					}
					case 2: {
						// byte
						for (int sampleNb = 0; sampleNb < samplesDim.getLength(); sampleNb++) {
							Date start = new Date();
							ArrayByte.D3 rdArray = (ArrayByte.D3) genotypes.read(sampleNb + ":" + sampleNb + ":1,"
									+ " 0:" + (markersDim.getLength() - 1) + ":1,"
									+ " 0:" + (allelesDim.getLength() - 1) + ":1");

							int[] shape = rdArray.getShape();
							Index index = rdArray.getIndex();
							Map<Object, String> stringMap = new LinkedHashMap<Object, String>();
							for (int i = 0; i < shape[0]; i++) {
								//ArrayChar wrCharArray = new ArrayChar ( new int[] {1, shape[1]} );
								//ArrayChar.D2.arraycopy(rdArray, i*shape[1], wrCharArray, 0, shape[1]);

								ArrayByte rdByteArray = new ArrayByte(new int[]{1, shape[1]});
								ArrayByte.D2.arraycopy(rdArray, i * shape[1], rdByteArray, 0, shape[1]);

								//byte[] values = (byte[]) rdByteArray.copyTo1DJavaArray();
								byte[] values = (byte[]) rdByteArray.copyTo1DJavaArray();
								for (int h = 0; h < values.length; h = h + 2) {
									stringMap.put(h / 2, Byte.toString(values[h]) + Byte.toString(values[h + 1]));
								}
							}

							Date end = new Date();
							long tmpTime = end.getTime() - start.getTime();
							timeAverage = ((timeAverage * sampleNb) + tmpTime) / (sampleNb + 1);
							if (sampleNb % 10 == 0) {
								log.info("Processing {}", sampleNb);
							}
						}
						log.info("Time average with int: {}", timeAverage);
						break;
					}
					case 3: {
						// char
						for (int sampleNb = 0; sampleNb < samplesDim.getLength(); sampleNb++) {
							Date start = new Date();
							ArrayChar.D3 gt_ACD3 = (ArrayChar.D3) genotypes.read("(" + sampleNb + ":" + sampleNb + ":1, "
									+ "0:" + (markersDim.getLength() - 1) + ":1, "
									+ "0:" + (allelesDim.getLength() - 1) + ":1)");
//							ArrayChar.D3 rdArray = (ArrayChar.D3) genotypes.read(
//									"(" + sampleNb + ":" + sampleNb + ":1,"
//									+ " 0:" + (markersDim.getLength() - 1) + ":1,"
//									+ " 0:" + (allelesDim.getLength() - 1) + ":1)");
							Date end = new Date();
							long tmpTime = end.getTime() - start.getTime();
							timeAverage = ((timeAverage * sampleNb) + tmpTime) / (sampleNb + 1);
							if (sampleNb % 10 == 0) {
								log.info("Processing {}", sampleNb);
							}
						}
						log.info("Time average with int: {}", timeAverage);
						break;
					}
				}
				//Array gt = genotypes.read("0:0:1, 0:9:1, 0:1:1"); //sample 1, snp 0 - 10, alleles 0+1
			} catch (IOException ex) {
				log.error("Cannot read data", ex);
			} catch (InvalidRangeException ex) {
				log.error("Cannot read data", ex);
			}
		} catch (IOException ex) {
			log.error("Cannot open file", ex);
		} finally {
			if (null != ncfile) {
				try {
					ncfile.close();
				} catch (IOException ex) {
					log.warn("Cannot close file", ex);
				}
			}
		}
	}
}
