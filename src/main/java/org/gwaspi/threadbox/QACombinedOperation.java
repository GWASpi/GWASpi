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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.gwaspi.constants.NetCDFConstants.Defaults.OPType;
import org.gwaspi.model.DataSetKey;
import org.gwaspi.model.GWASpiExplorerNodes;
import org.gwaspi.model.MatrixKey;
import org.gwaspi.model.OperationKey;
import org.gwaspi.operations.MatrixOperation;
import org.gwaspi.operations.OperationManager;
import org.gwaspi.operations.qamarkers.QAMarkersOperation;
import org.gwaspi.operations.qamarkers.QAMarkersOperationParams;
import org.gwaspi.operations.qasamples.QASamplesOperation;
import org.gwaspi.operations.qasamples.QASamplesOperationParams;
import org.gwaspi.progress.DefaultProcessInfo;
import org.gwaspi.progress.NullProgressHandler;
import org.gwaspi.progress.ProcessInfo;
import org.gwaspi.progress.ProcessStatus;
import org.gwaspi.progress.ProgressHandler;
import org.gwaspi.progress.ProgressSource;
import org.gwaspi.progress.SubProcessInfo;
import org.gwaspi.progress.SuperProgressSource;
import org.gwaspi.reports.OutputQAMarkers;
import org.gwaspi.reports.OutputQASamples;
import org.gwaspi.reports.QAMarkersOutputParams;
import org.gwaspi.reports.QASamplesOutputParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QACombinedOperation extends CommonRunnable {

	static final ProgressSource PLACEHOLDER_PS_QA = new NullProgressHandler(
			new SubProcessInfo(null, "PLACEHOLDER_PS_QA", null));
	private static final ProcessInfo fullQAProcessInfo
			= new DefaultProcessInfo("Full QA Test",
					"Complete QA Test (Samples & Markers) procedure and generation of reports"); // TODO
	private static final ProgressSource PLACEHOLDER_PS_QA_SAMPLES = new NullProgressHandler(
			new SubProcessInfo(null, "PLACEHOLDER_PS_QA_SAMPLES", null));
	private static final ProgressSource PLACEHOLDER_PS_QA_SAMPLES_REPORTS = new NullProgressHandler(
			new SubProcessInfo(null, "PLACEHOLDER_PS_QA_SAMPLES_REPORTS", null));
	private static final ProgressSource PLACEHOLDER_PS_QA_MARKERS = new NullProgressHandler(
			new SubProcessInfo(null, "PLACEHOLDER_PS_QA_MARKERS", null));
	private static final ProgressSource PLACEHOLDER_PS_QA_MARKERS_REPORTS = new NullProgressHandler(
			new SubProcessInfo(null, "PLACEHOLDER_PS_QA_MARKERS_REPORTS", null));
	private static final Map<ProgressSource, Double> subProgressSourcesAndWeights;
	static {
		final LinkedHashMap<ProgressSource, Double> tmpSubProgressSourcesAndWeights
				= new LinkedHashMap<ProgressSource, Double>(4);
		tmpSubProgressSourcesAndWeights.put(PLACEHOLDER_PS_QA_SAMPLES, 0.2);
		tmpSubProgressSourcesAndWeights.put(PLACEHOLDER_PS_QA_SAMPLES_REPORTS, 0.1);
		tmpSubProgressSourcesAndWeights.put(PLACEHOLDER_PS_QA_MARKERS, 0.6);
		tmpSubProgressSourcesAndWeights.put(PLACEHOLDER_PS_QA_MARKERS_REPORTS, 0.1);
		subProgressSourcesAndWeights = Collections.unmodifiableMap(tmpSubProgressSourcesAndWeights);
	}

	private final DataSetKey parentKey;
	private final boolean createReports;
	private OperationKey samplesQAOperationKey;
	private OperationKey markersQAOperationKey;
	private final SuperProgressSource progressSource;
	private final TaskLockProperties taskLockProperties;

	public QACombinedOperation(final DataSetKey parentKey, final boolean createReports) {
		super("Quality Assurance & Reports", "on " + parentKey.toString());

		this.parentKey = parentKey;
		this.createReports = createReports;
		this.samplesQAOperationKey = null;
		this.markersQAOperationKey = null;
		this.progressSource = new SuperProgressSource(fullQAProcessInfo, subProgressSourcesAndWeights);
		this.taskLockProperties = MultiOperations.createTaskLockProperties(parentKey);
	}

	public QACombinedOperation(final DataSetKey parentKey) {
		this(parentKey, true);
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
		return LoggerFactory.getLogger(QACombinedOperation.class);
	}

	public OperationKey getSamplesQAOperationKey() {
		return samplesQAOperationKey;
	}

	public OperationKey getMarkersQAOperationKey() {
		return markersQAOperationKey;
	}

	@Override
	protected void runInternal() throws IOException {

		progressSource.setNewStatus(ProcessStatus.INITIALIZING);
		List<OPType> necessaryOpTypes = new ArrayList<OPType>();
		necessaryOpTypes.add(OPType.SAMPLE_QA);
		necessaryOpTypes.add(OPType.MARKER_QA);
		List<OPType> missingOPs = OperationManager.checkForNecessaryOperations(necessaryOpTypes, parentKey, true);
		progressSource.setNewStatus(ProcessStatus.RUNNING);

		if (missingOPs.contains(OPType.SAMPLE_QA)) {
			final QASamplesOperationParams qaSamplesOperationParams = new QASamplesOperationParams(parentKey);
			final MatrixOperation qaSamplesOperation = new QASamplesOperation(qaSamplesOperationParams);
			progressSource.replaceSubProgressSource(PLACEHOLDER_PS_QA_SAMPLES, qaSamplesOperation.getProgressSource(), null);
			samplesQAOperationKey = OperationManager.performOperationCreatingOperation(qaSamplesOperation);

			if (createReports) {
				final QASamplesOutputParams qaSamplesOutputParams = new QASamplesOutputParams(samplesQAOperationKey, true);
				final MatrixOperation outputQASamples = new OutputQASamples(qaSamplesOutputParams);
				progressSource.replaceSubProgressSource(PLACEHOLDER_PS_QA_SAMPLES_REPORTS, outputQASamples.getProgressSource(), null);
				OperationManager.performOperationCreatingOperation(outputQASamples);
			}
		}
		if (missingOPs.contains(OPType.MARKER_QA)) {
			final QAMarkersOperationParams qaMarkersOperationParams = new QAMarkersOperationParams(parentKey);
			final MatrixOperation qaMarkersOperation = new QAMarkersOperation(qaMarkersOperationParams);
			progressSource.replaceSubProgressSource(PLACEHOLDER_PS_QA_MARKERS, qaMarkersOperation.getProgressSource(), null);
			markersQAOperationKey = OperationManager.performOperationCreatingOperation(qaMarkersOperation);

			if (createReports) {
				final QAMarkersOutputParams qaMarkersOutputParams = new QAMarkersOutputParams(markersQAOperationKey);
				final MatrixOperation outputQAMarkers = new OutputQAMarkers(qaMarkersOutputParams);
				progressSource.replaceSubProgressSource(PLACEHOLDER_PS_QA_MARKERS_REPORTS, outputQAMarkers.getProgressSource(), null);
				OperationManager.performOperationCreatingOperation(outputQAMarkers);
			}
		}
		progressSource.setNewStatus(ProcessStatus.COMPLEETED);
	}

	static OperationKey[] matrixCompleeted(MatrixKey matrixKey, final SuperProgressSource superProgressSource)
			throws IOException
	{
		OperationKey[] resultOperationKeys = new OperationKey[2];

		GWASpiExplorerNodes.insertMatrixNode(matrixKey);

		// NOTE ABORTION_POINT We could be gracefully aborted here
		final DataSetKey parent = new DataSetKey(matrixKey);
		final QACombinedOperation matrixQA = new QACombinedOperation(parent, true);
		superProgressSource.replaceSubProgressSource(PLACEHOLDER_PS_QA, matrixQA.getProgressSource(), null);

		// run within this thread
		CommonRunnable.doRunNowInThread(matrixQA);

		resultOperationKeys[0] = matrixQA.getSamplesQAOperationKey();
		resultOperationKeys[1] = matrixQA.getMarkersQAOperationKey();

		return resultOperationKeys;
	}
}
