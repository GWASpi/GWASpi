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

package org.gwaspi.threadbox;

import org.gwaspi.constants.cNetCDF;
import org.gwaspi.model.GWASpiExplorerNodes;
import org.gwaspi.model.OperationKey;
import org.gwaspi.model.OperationsList;
import org.gwaspi.netCDF.operations.OperationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Threaded_HardyWeinberg extends CommonRunnable {

	private int censusOpId;

	public Threaded_HardyWeinberg(
			int matrixId,
			int censusOpId)
	{
		super(
				"Hardy-Weinberg",
				"Hardy-Weinberg test",
				"Hardy-Weinberg on Matrix ID: " + matrixId,
				"Hardy-Weinberg");

		this.censusOpId = censusOpId;
	}

	@Override
	protected Logger createLog() {
		return LoggerFactory.getLogger(Threaded_HardyWeinberg.class);
	}

	@Override
	protected void runInternal(SwingWorkerItem thisSwi) throws Exception {

		// HW ON GENOTYPE FREQ.
		if (thisSwi.getQueueState().equals(QueueState.PROCESSING)
				&& (censusOpId != Integer.MIN_VALUE))
		{
			int hwOpId = OperationManager.performHardyWeinberg(censusOpId, cNetCDF.Defaults.DEFAULT_AFFECTION);
			OperationKey hwOpKey = OperationKey.valueOf(OperationsList.getById(hwOpId));
			GWASpiExplorerNodes.insertSubOperationUnderOperationNode(censusOpId, hwOpKey);
		}
	}
}
