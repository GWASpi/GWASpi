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

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.gwaspi.model.MatrixKey;
import org.gwaspi.netCDF.loader.DataSetDestination;
import org.gwaspi.netCDF.matrices.MatrixFactory;
import org.gwaspi.operations.genotypesflipper.MatrixGenotypesFlipper;
import org.gwaspi.operations.genotypesflipper.MatrixGenotypesFlipperMetadataFactory;
import org.gwaspi.operations.genotypesflipper.MatrixGenotypesFlipperParams;
import org.gwaspi.progress.DefaultProcessInfo;
import org.gwaspi.progress.NullProgressHandler;
import org.gwaspi.progress.ProcessInfo;
import org.gwaspi.progress.ProcessStatus;
import org.gwaspi.progress.ProgressHandler;
import org.gwaspi.progress.ProgressSource;
import org.gwaspi.progress.SubProcessInfo;
import org.gwaspi.progress.SuperProgressSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlipStrandCombinedOperation extends CommonRunnable {

	private static final ProcessInfo fullFlipStrandMatrixInfo
			= new DefaultProcessInfo("Full Flip Strand",
					"Flip Strand and QA"); // TODO
	private static final ProgressSource PLACEHOLDER_PS_MATRIX_STRAND_FLIP = new NullProgressHandler(
			new SubProcessInfo(null, "PLACEHOLDER_PS_MATRIX_STRAND_FLIP", null));
	private static final Map<ProgressSource, Double> subProgressSourcesAndWeights;
	static {
		final LinkedHashMap<ProgressSource, Double> tmpSubProgressSourcesAndWeights
				= new LinkedHashMap<ProgressSource, Double>(2);
		tmpSubProgressSourcesAndWeights.put(PLACEHOLDER_PS_MATRIX_STRAND_FLIP, 0.4);
		tmpSubProgressSourcesAndWeights.put(QACombinedOperation.PLACEHOLDER_PS_QA, 0.6);
		subProgressSourcesAndWeights = Collections.unmodifiableMap(tmpSubProgressSourcesAndWeights);
	}

	private final MatrixGenotypesFlipperParams params;
	private final SuperProgressSource progressSource;
	private final TaskLockProperties taskLockProperties;

	public FlipStrandCombinedOperation(MatrixGenotypesFlipperParams params) {
		super("Flip Strand Matrix (Genotypes)", "on " + params.getParent().toString());

		this.params = params;
		this.progressSource = new SuperProgressSource(fullFlipStrandMatrixInfo, subProgressSourcesAndWeights);
		this.taskLockProperties = MultiOperations.createTaskLockProperties(params.getParent());
	}

	@Override
	public ProgressSource getProgressSource() {
		return progressSource;
	}

	@Override
	protected ProgressHandler getProgressHandler() {
		return progressSource;
	}

	@Override
	public TaskLockProperties getTaskLockProperties() {
		return taskLockProperties;
	}

	@Override
	protected Logger createLog() {
		return LoggerFactory.getLogger(FlipStrandCombinedOperation.class);
	}

	@Override
	protected void runInternal() throws IOException {

		progressSource.setNewStatus(ProcessStatus.INITIALIZING);
		final DataSetDestination dataSetDestination
				= MatrixFactory.generateMatrixDataSetDestination(params, MatrixGenotypesFlipperMetadataFactory.SINGLETON);
		MatrixGenotypesFlipper matrixOperation = new MatrixGenotypesFlipper(params, dataSetDestination);
		progressSource.replaceSubProgressSource(PLACEHOLDER_PS_MATRIX_STRAND_FLIP, matrixOperation.getProgressSource(), null);

		progressSource.setNewStatus(ProcessStatus.RUNNING);
//		OperationManager.performOperation(matrixOperation); // XXX We can not do that, because our matrixOperation does not support getParams() yet, so instead we do ...
		final MatrixKey resultMatrixKey = matrixOperation.call();

		QACombinedOperation.matrixCompleeted(resultMatrixKey, progressSource);
		progressSource.setNewStatus(ProcessStatus.COMPLEETED);
	}
}
