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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.ma2.Array;
import ucar.ma2.ArrayChar;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.NCdumpW;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class PrototypeReadNetcdf {

	private static final Logger log = LoggerFactory.getLogger(PrototypeReadNetcdf.class);

	public static void main(String[] arg) throws InvalidRangeException, IOException {

		String filename = "/media/data/work/moapi/genotypes/prototype.nc"; // XXX system dependent path
		NetcdfFile ncfile = null;

		int gtSpan = 2;
		Map<Object, Object> map = new LinkedHashMap<Object, Object>();
		for (int i = 0; i < 10; i++) {
			map.put(i, "00");
		}

		try {
			ncfile = NetcdfFile.open(filename);

			String varName = "genotypes";
			Variable genotypes = ncfile.findVariable(varName);
			if (null == genotypes) {
				return;
			}
			try {
				//Array gt = genotypes.read("0:0:1, 0:9:1, 0:1:1"); // sample 1, snp 0 - 10, alleles 0+1
				ArrayChar.D3 gt = (ArrayChar.D3) genotypes.read("0:0:1, 0:9:1, 0:1:1");
				StringWriter arrStr = new StringWriter();
				NCdumpW.printArray(gt, varName, new PrintWriter(arrStr), null);
				log.info(arrStr.getBuffer().toString());

				Map<Object, Object> filledMap = fillLinkedHashMap(map, gt, gtSpan);

				int stopme = 0;
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

	private static Map<Object, Object> fillLinkedHashMap(Map<Object, Object> map, Array inputArray, int gtSpan) {
		StringBuffer alleles = new StringBuffer("");
		int mapIndex = 0;
		int alleleCount = 0;
		for (int i = 0; i < inputArray.getSize(); i++) {
			if (alleleCount == gtSpan) {
				map.put(mapIndex, alleles);
				alleles = new StringBuffer("");
				alleleCount = 0;
				mapIndex++;
			}
			char c = inputArray.getChar(i);
			alleles.append(c);
			alleleCount++;
		}
		map.put(mapIndex, alleles);
		return map;
	}
}
