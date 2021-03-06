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

package org.gwaspi.operations.hardyweinberg;

import java.io.IOException;
import java.util.List;
import org.gwaspi.operations.OperationDataSet;

public interface HardyWeinbergOperationDataSet extends OperationDataSet<HardyWeinbergOperationEntry> {

	// - Variables.VAR_OPSET: [Collection<MarkerKey>]
	// - Variables.VAR_MARKERS_RSID: [Collection<String>]
	// - Variables.VAR_IMPLICITSET: [Collection<SampleKey>]
	// - HardyWeinberg.VAR_OP_MARKERS_HWPval_CTRL: Control P-Value [Double[1]]
	// - HardyWeinberg.VAR_OP_MARKERS_HWHETZY_CTRL: Control Obs Hetzy & Exp Hetzy [Double[2]]
	// - HardyWeinberg.VAR_OP_MARKERS_HWPval_ALT: Hardy-Weinberg Alternate P-Value [Double[1]]
	// - HardyWeinberg.VAR_OP_MARKERS_HWHETZY_ALT: Hardy-Weinberg Alternate Obs Hetzy & Exp Hetzy [Double[2]]

	void addEntry(HardyWeinbergOperationEntry entry) throws IOException;

	List<HardyWeinbergOperationEntry> getEntriesControl() throws IOException;
	List<HardyWeinbergOperationEntry> getEntriesAlternate() throws IOException;

	List<Double> getPs(HardyWeinbergOperationEntry.Category category, int from, int to) throws IOException;
	List<Double> getHwHetzyObses(HardyWeinbergOperationEntry.Category category, int from, int to) throws IOException;
	List<Double> getHwHetzyExps(HardyWeinbergOperationEntry.Category category, int from, int to) throws IOException;
}
