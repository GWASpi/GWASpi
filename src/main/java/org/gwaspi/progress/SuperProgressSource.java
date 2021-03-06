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

package org.gwaspi.progress;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SuperProgressSource extends AbstractProgressHandler<Double> {

	private final List<SuperProgressListener> superProgressListeners;
	private final LinkedHashMap<ProgressSource, Double> subProgressSourcesAndWeights;
	private final Map<ProgressSource, Double> subProgressSourcesAndLastCompletionFraction;
	private final Map<ProgressSource, Integer> subProgressSourcesAndLastNumIntervals;
	private final ProgressListener progressListener;
	private double lastCompletionFraction;
	private double weightSum;

	private class SubProgressListener<ST> extends AbstractProgressListener<ST> {

		@Override
		public void processDetailsChanged(ProcessDetailsChangeEvent evt) {

			final ProgressSource progressSource = evt.getSource();
			final int oldNumIntervals  = getNumericalIntervalls(subProgressSourcesAndLastNumIntervals.get(progressSource));
			final int newNumIntervals  = getNumericalIntervalls(progressSource.getNumIntervals());
			if (oldNumIntervals != newNumIntervals) {
				setNumIntervals(getNumIntervals() - oldNumIntervals + newNumIntervals);
				subProgressSourcesAndLastNumIntervals.put(progressSource, newNumIntervals);
			}
		}

		@Override
		public void progressHappened(ProgressEvent evt) {

			Double currentSubCompletionFraction = evt.getCompletionFraction();
			if (currentSubCompletionFraction != null) {
				final ProgressSource progressSource = evt.getSource();
				final double weight = subProgressSourcesAndWeights.get(progressSource);
				final Double lastSubCompletionFraction = subProgressSourcesAndLastCompletionFraction.get(progressSource);
				fireAdditionalProgressHappened((currentSubCompletionFraction - lastSubCompletionFraction) * weight / weightSum);
				subProgressSourcesAndLastCompletionFraction.put(progressSource, currentSubCompletionFraction);
			}
		}
	}

	public SuperProgressSource(ProcessInfo processInfo, Map<? extends ProgressSource, Double> subProgressSourcesAndWeights) {
		super(processInfo, calculateNumIntervalls(subProgressSourcesAndWeights));

		this.superProgressListeners = new ArrayList<SuperProgressListener>(1);
		this.subProgressSourcesAndWeights = new LinkedHashMap<ProgressSource, Double>(subProgressSourcesAndWeights);
		this.subProgressSourcesAndLastCompletionFraction = new HashMap<ProgressSource, Double>(this.subProgressSourcesAndWeights.size());
		this.subProgressSourcesAndLastNumIntervals = new HashMap<ProgressSource, Integer>(this.subProgressSourcesAndWeights.size());
		this.progressListener = new SubProgressListener();
		this.lastCompletionFraction = 0.0;
		this.weightSum = 0.0;

		for (Map.Entry<? extends ProgressSource, Double> subProgressSourceAndWeight : subProgressSourcesAndWeights.entrySet()) {
			final ProgressSource progressSource = subProgressSourceAndWeight.getKey();
			final double weight = subProgressSourceAndWeight.getValue();
			progressSource.addProgressListener(progressListener);
			subProgressSourcesAndLastCompletionFraction.put(progressSource, 0.0);
			subProgressSourcesAndLastNumIntervals.put(progressSource, progressSource.getNumIntervals());
			weightSum += weight;
		}
	}

	public SuperProgressSource(ProcessInfo processInfo, Collection<? extends ProgressSource> subProgressSources) {
		this(processInfo, createEvenlyDistributedWeights(subProgressSources));
	}

	public SuperProgressSource(ProcessInfo processInfo) {
		this(processInfo, Collections.EMPTY_MAP);
	}

	private static <S extends ProgressSource> Map<S, Double> createEvenlyDistributedWeights(Collection<S> subProgressSources) {

		Map<S, Double> subProgressSourcesAndWeights
				= new HashMap<S, Double>(subProgressSources.size());

		final double weight = 1.0 / subProgressSources.size();
		for (final S progressSource : subProgressSources) {
			subProgressSourcesAndWeights.put(progressSource, weight);
		}

		return subProgressSourcesAndWeights;
	}

	/**
	 * Returns a non <code>null</code> #intervalls.
	 * @param progressSource
	 * @return a non <code>null</code> #intervalls.
	 */
	private static int getNumericalIntervalls(Integer rawNumIntervals) {

		Integer numSubIntervalls = rawNumIntervals;
		if (numSubIntervalls == null) {
			numSubIntervalls = 1;
		}

		return numSubIntervalls;
	}

	private static Integer calculateNumIntervalls(Map<? extends ProgressSource, Double> subProgressSourcesAndWeights) {

		int numIntervalls = 0;
		for (ProgressSource progressSource : subProgressSourcesAndWeights.keySet()) {
			numIntervalls += getNumericalIntervalls(progressSource.getNumIntervals());
		}

		return numIntervalls;
	}

	public void addSubProgressSource(ProgressSource progressSource, Double weight) {

		final int index = subProgressSourcesAndWeights.size();
		subProgressSourcesAndWeights.put(progressSource, weight);
		progressSource.addProgressListener(progressListener);
		subProgressSourcesAndLastCompletionFraction.put(progressSource, 0.0);
		weightSum += weight;
		setNumIntervals(getNumIntervals() + getNumericalIntervalls(progressSource.getNumIntervals()));
//		fireProcessDetailsChanged(); // this is already invoked by setNumIntervals() above
		fireSubProcessAdded(new SubProcessAddedEvent(this, index, progressSource, weight));
	}

	public void replaceSubProgressSource(ProgressSource oldProgressSource, ProgressSource newProgressSource, Double weight) {

		final LinkedHashMap<ProgressSource, Double> newPSs = new LinkedHashMap<ProgressSource, Double>(subProgressSourcesAndWeights.size());
		int replaceIndex = -1;
		int curIndex = 0;
		Double oldWeight = null;
		Double newWeight = null;
		for (Map.Entry<ProgressSource, Double> currentProgressSourceAndWeight : subProgressSourcesAndWeights.entrySet()) {
			if (currentProgressSourceAndWeight.getKey().equals(oldProgressSource)) {
				oldWeight = currentProgressSourceAndWeight.getValue();
				newWeight = (weight == null) ? oldWeight : weight;
				newPSs.put(newProgressSource, newWeight);
				oldProgressSource.removeProgressListener(progressListener);
				newProgressSource.addProgressListener(progressListener);
				weightSum -= oldWeight;
				weightSum += newWeight;
				replaceIndex = curIndex;
			} else {
				newPSs.put(currentProgressSourceAndWeight.getKey(), currentProgressSourceAndWeight.getValue());
			}
			curIndex++;
		}
		final boolean oldFound = (oldWeight != null);
		if (!oldFound) {
			throw new IllegalArgumentException("Could not find old progress source: " + oldProgressSource.toString());
		}

		subProgressSourcesAndWeights.clear();
		subProgressSourcesAndWeights.putAll(newPSs);

		subProgressSourcesAndLastCompletionFraction.remove(oldProgressSource);
		subProgressSourcesAndLastCompletionFraction.put(newProgressSource, 0.0);

		setNumIntervals(getNumIntervals()
				- getNumericalIntervalls(oldProgressSource.getNumIntervals())
				+ getNumericalIntervalls(newProgressSource.getNumIntervals()));
//		fireProcessDetailsChanged(); // this is already invoked by setNumIntervals() above
		fireSubProcessReplaced(new SubProcessReplacedEvent(this, replaceIndex, oldProgressSource, newProgressSource, oldWeight, newWeight));
	}

	public Map<ProgressSource, Double> getSubProgressSourcesAndWeights() {
		return subProgressSourcesAndWeights;
	}

	@Override
	protected void fireStatusChanged(final ProcessStatusChangeEvent evt) {

		if (evt.getNewStatus().equals(ProcessStatus.FINALIZING)
				|| evt.getNewStatus().equals(ProcessStatus.COMPLEETED))
		{
			fireProgressHappened(1.0, 1.0);
		}

		super.fireStatusChanged(evt);
	}

	@Override
	public void setProgress(Double currentState) {
		throw new UnsupportedOperationException("progress should never be set directly on the super process, but only on its child pocesses");
	}

	private void addExistingProgressListener(SuperProgressListener lst) {

		int curIndex = 0;
		for (Map.Entry<ProgressSource, Double> subProgressSourceAndWeight : subProgressSourcesAndWeights.entrySet()) {
			lst.subProcessAdded(new SubProcessAddedEvent(this, curIndex, subProgressSourceAndWeight.getKey(), subProgressSourceAndWeight.getValue()));
			curIndex++;
		}
	}

	public void addSuperProgressListener(SuperProgressListener lst, boolean addExistingSubProcesses) {

		superProgressListeners.add(lst);

		if (addExistingSubProcesses) {
			addExistingProgressListener(lst);
		}
	}

	public void addSuperProgressListener(SuperProgressListener lst) {
		addSuperProgressListener(lst, false);
	}

	public void removeSuperProgressListener(SuperProgressListener lst) {
		superProgressListeners.remove(lst);
	}

	public List<SuperProgressListener> getSuperProgressListeners() {
		return superProgressListeners;
	}

	private void fireAdditionalProgressHappened(Double additionalCompletionFraction) {

		final double newCompletionFraction = lastCompletionFraction + additionalCompletionFraction;
		fireProgressHappened(newCompletionFraction, newCompletionFraction);
		lastCompletionFraction = newCompletionFraction;
	}

	private void fireSubProcessAdded(SubProcessAddedEvent evt) {

		for (SuperProgressListener superProgressListener : superProgressListeners) {
			superProgressListener.subProcessAdded(evt);
		}
	}

	private void fireSubProcessRemoved(SubProcessRemovedEvent evt) {

		for (SuperProgressListener superProgressListener : superProgressListeners) {
			superProgressListener.subProcessRemoved(evt);
		}
	}

	private void fireSubProcessReplaced(SubProcessReplacedEvent evt) {

		for (SuperProgressListener superProgressListener : superProgressListeners) {
			superProgressListener.subProcessReplaced(evt);
		}
	}
}
