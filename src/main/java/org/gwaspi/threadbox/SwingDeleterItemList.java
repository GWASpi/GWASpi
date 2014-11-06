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
import java.util.List;
import org.gwaspi.global.Text;
import org.gwaspi.gui.GWASpiExplorerPanel;
import org.gwaspi.gui.StartGWASpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwingDeleterItemList {

	private static final Logger log = LoggerFactory.getLogger(SwingDeleterItemList.class);
	private static List<SwingDeleterItem> swingDeleterItems = new ArrayList<SwingDeleterItem>();

	private SwingDeleterItemList() {
	}

	public static void add(SwingDeleterItem sdi) {

		//SwingDeleterItemList.purgeDoneDeletes();
		boolean addMe = true;
		for (SwingDeleterItem allreadySdi : swingDeleterItems) {
			if (allreadySdi.getStudyKey() == sdi.getStudyKey()
					&& allreadySdi.getMatrixKey() == sdi.getMatrixKey()
					&& allreadySdi.getOperationKey() == sdi.getOperationKey()) {
				addMe = false;
			}
		}
		if (addMe) {
			// Add at start of list
			swingDeleterItems.add(swingDeleterItems.size(), sdi);
		}

		// CHECK IF ANY ITEM IS RUNNING, START PROCESSING NEWLY ADDED SwingDeleter
		if (SwingWorkerItemList.sizePending() == 0) {
			deleteAllListed();
		}

		final TaskQueueStatusChangedEvent taskEvent = new TaskQueueStatusChangedEvent(sdi, sdi.getProgressSource());
		SwingWorkerItemList.fireTaskRegistered(taskEvent);
	}

	public static void deleteAllListed() {
//		if (StartGWASpi.guiMode) {
//			StartGWASpi.mainGUIFrame.setCursor(CursorUtils.WAIT_CURSOR);
//		}

		for (SwingDeleterItem currentSdi : swingDeleterItems) {
			if (currentSdi.getQueueState().equals(QueueState.QUEUED)) {
				currentSdi.run();
			}
		}

		// IF WE ARE IN GUI MODE, UPDATE TREE. ELSE EXIT PROGRAM
		if (StartGWASpi.guiMode) {
//			StartGWASpi.mainGUIFrame.setCursor(CursorUtils.DEFAULT_CURSOR);
			try {
				MultiOperations.updateTreeAndPanel();
			} catch (IOException ex) {
				log.warn("Failed to update GUI tree after deleting items", ex);
			}
			GWASpiExplorerPanel.getSingleton().setAllNodesCollapsable();
		} else {
			log.info(Text.Cli.done);
		}
	}

	public static List<SwingDeleterItem> getItems() {
		return swingDeleterItems;
	}

	public static int size() {
		return swingDeleterItems.size();
	}

	public static int sizePending() {

		int numPending = 0;

		for (SwingDeleterItem currentSdi : getItems()) {
			if (currentSdi.isCurrent()) {
				numPending++;
			}
		}

		return numPending;
	}

	public static void purgeDoneDeletes() {
		for (int i = swingDeleterItems.size() - 1; i >= 0; i--) {
			if (swingDeleterItems.get(i).getQueueState() == QueueState.DELETED) {
				swingDeleterItems.remove(i);
			}
		}
	}

	public static void abortSwingWorker(int idx) {
		SwingDeleterItem sdi = swingDeleterItems.get(idx);
		if (sdi.isCurrent()) {
			sdi.setQueueState(QueueState.ABORT);

			log.info("");
			log.info(Text.Processes.abortingProcess);
			log.info(sdi.getDescription());
			log.info("Delete Launch Time: {}", org.gwaspi.global.Utils.getShortDateTimeAsString(sdi.getCreateTime()));
			log.info("");
		}
	}
}
