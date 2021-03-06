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
import java.util.Set;
import org.gwaspi.global.Config;
import org.gwaspi.gui.GWASpiExplorerPanel;
import org.gwaspi.model.DataSetKey;
import org.gwaspi.model.MatrixKey;

public class MultiOperations {

	private MultiOperations() {
	}

	public static Task queueTask(final CommonRunnable job) {

		final Task task = new DefaultTask(job);
		TaskQueue.getInstance().queueTask(task);

		return task;
	}

	public static TaskLockProperties createTaskLockProperties(
			final DataSetKey parent,
			final Set<MatrixKey> participatingMatrices)
	{
		final TaskLockProperties lockProperties = new TaskLockProperties();

//		if (params.getMatrixKey().getStudyKey().isSpecifiedByName()) {
//			throw new IllegalStateException(); // FIXME need to fetch the study-id
//		}
//		if (params.getMatrixKey().isSpecifiedByName()) {
//			throw new IllegalStateException(); // FIXME need to fetch the matrix-id
//		}

		for (MatrixKey participatingMatrix : participatingMatrices) {
			lockProperties.addRequired(new DataSetKey(participatingMatrix));
		}

		if (parent.isOperation()) {
			lockProperties.addRequired(new DataSetKey(parent.getOperationParent()));
		}

		return lockProperties;
	}

	public static TaskLockProperties createTaskLockProperties(final DataSetKey parent) {
		return createTaskLockProperties(parent, Collections.singleton(parent.getOrigin()));
	}

	public static boolean canBeDoneNow(final Task task) {
		return TaskQueue.getInstance().canBeDone(task);
	}

	public static boolean canBeDoneNow(final CommonRunnable task) {
		return canBeDoneNow(new DefaultTask(task));
	}

	public static Task queueTask(final Deleter sdi) {

		final Task task = new DefaultTask(sdi);
		TaskQueue.getInstance().queueTask(task);

		return task;
	}

	//<editor-fold defaultstate="expanded" desc="HELPERS">
	public static void printFinished(String operationName) {
		org.gwaspi.global.Utils.sysoutFinish(operationName);
	}

	public static void printCompleted(String operationName) {
		org.gwaspi.global.Utils.sysoutCompleted(operationName);
	}

	public static void printError(String operationName) {
		org.gwaspi.global.Utils.sysoutError(operationName);
	}

	public static void updateTree() throws IOException {
		if (Config.getSingleton().getBoolean(Config.PROPERTY_GUI_MODE, false)) {
			GWASpiExplorerPanel.getSingleton().getTree().setEnabled(false);
			GWASpiExplorerPanel.getSingleton().updateTreePanel(false);
			GWASpiExplorerPanel.getSingleton().getTree().setEnabled(true);
		}
	}

	public static void updateTreeAndPanel() throws IOException {
		if (Config.getSingleton().getBoolean(Config.PROPERTY_GUI_MODE, false)) {
			GWASpiExplorerPanel.getSingleton().getTree().setEnabled(false);
			GWASpiExplorerPanel.getSingleton().updateTreePanel(true);
			GWASpiExplorerPanel.getSingleton().getTree().setEnabled(true);
		}
	}
	//</editor-fold>
}
