package org.gwaspi.model;

import org.gwaspi.constants.cNetCDF.Defaults.OPType;
import org.gwaspi.global.Text;
import org.gwaspi.gui.GWASpiExplorerPanel;
import org.gwaspi.gui.StartGWASpi;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gwaspi.samples.SampleManager;

/**
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 */
public class GWASpiExplorerNodes {

	private static final Logger log
			= LoggerFactory.getLogger(GWASpiExplorerNodes.class);

	private GWASpiExplorerNodes() {
	}

	//<editor-fold defaultstate="collapsed" desc="NODE DEFINITION">
	public static class NodeElementInfo {

		public static final int NODE_ID_NONE = 0;

		private int nodeId;
		private int parentNodeId;
		private String nodeType;
		private String nodeUniqueName;
		private boolean collapsable;

		public NodeElementInfo(
				int parentNodeId,
				int nodeId,
				String nodeType,
				String nodeName)
		{
			this.parentNodeId = parentNodeId;
			this.nodeId = nodeId;
			this.nodeType = nodeType;
			this.nodeUniqueName = nodeName;
			this.collapsable = false;
		}

		@Override
		public String toString() {
			return nodeUniqueName;
		}

		public int getNodeId() {
			return nodeId;
		}

		public int getParentNodeId() {
			return parentNodeId;
		}

		public String getNodeType() {
			return nodeType;
		}

		public String getNodeUniqueName() {
			return nodeUniqueName;
		}

		public boolean isCollapsable() {
			return collapsable;
		}

		public void setCollapsable(boolean collapsable) {
			this.collapsable = collapsable;
		}
	}

	public static class UncollapsableNodeElementInfo extends NodeElementInfo {

		public UncollapsableNodeElementInfo(
				int parentNodeId,
				int nodeId,
				String nodeType,
				String nodeName)
		{
			super(parentNodeId, nodeId, nodeType, nodeName);
		}

		@Override
		public boolean isCollapsable() {
			return false;
		}
	}

	protected static DefaultMutableTreeNode createStudyTreeNode(int studyId) {
		DefaultMutableTreeNode tn = null;
		try {
			Study study = new Study(studyId);

//			parentNodeId
//			nodeId
//			nodeType
//			nodeUniqueName => will be rsult of toString() call of DefaultMutableTreeNode
//			friendlyName

			tn = new DefaultMutableTreeNode(new NodeElementInfo(
					NodeElementInfo.NODE_ID_NONE,
					study.getStudyId(),
					Text.App.treeStudy,
					"SID: " + study.getStudyId() + " - " + study.getStudyName()));
		} catch (IOException ex) {
			log.error(null, ex);
		}

		return tn;
	}

	protected static DefaultMutableTreeNode createMatrixTreeNode(int matrixId) {
		DefaultMutableTreeNode tn = null;
		try {
			Matrix mx = new Matrix(matrixId);
			tn = new DefaultMutableTreeNode(new NodeElementInfo(mx.getStudyId(),
					matrixId,
					Text.App.treeMatrix,
					"MX: " + matrixId + " - " + mx.getMatrixMetadata().getMatrixFriendlyName()));
		} catch (IOException ex) {
			log.error(null, ex);
		}
		return tn;
	}

	protected static DefaultMutableTreeNode createSampleInfoTreeNode(int studyId) throws IOException {
		DefaultMutableTreeNode tn = null;
		// CHECK IF STUDY EXISTS
		List<Map<String, Object>> rs = SampleManager.getAllSampleInfoFromDBByPoolID(studyId);
		if (!rs.isEmpty()) {
			tn = new DefaultMutableTreeNode(new NodeElementInfo(studyId, // parentNodeId
					studyId, // nodeId
					Text.App.treeSampleInfo, // nodeType
					Text.App.treeSampleInfo)); // nodeUniqueName
		}
		return tn;
	}

	protected static DefaultMutableTreeNode createOperationTreeNode(int opId) {
		DefaultMutableTreeNode tn = null;
		try {
			Operation op = new Operation(opId);
			tn = new DefaultMutableTreeNode(new NodeElementInfo(op.getParentMatrixId(),
					opId,
					Text.App.treeOperation,
					"OP: " + opId + " - " + op.getOperationFriendlyName()));
		} catch (IOException ex) {
			log.error(null, ex);
		}
		return tn;
	}

	protected static DefaultMutableTreeNode createSubOperationTreeNode(int opId) {
		DefaultMutableTreeNode tn = null;
		try {

//			parentNodeId
//			nodeId
//			pathNodeIds
//			nodeType
//			studyNodeName
//			nodeUniqueName

			Operation op = new Operation(opId);
//			int[] pathIds = new int[]{0, op.getStudyId(), op.getParentMatrixId(), op.getParentOperationId(), opId};
			tn = new DefaultMutableTreeNode(new NodeElementInfo(op.getParentOperationId(),
					opId,
					Text.App.treeOperation,
					"OP: " + opId + " - " + op.getOperationFriendlyName()));
		} catch (IOException ex) {
			log.error(null, ex);
		}
		return tn;
	}

	protected static DefaultMutableTreeNode createReportTreeNode(int rpId) {
		DefaultMutableTreeNode tn = null;
		try {
			Report rp = new Report(rpId);
//			int[] pathIds = new int[]{0, rp.getStudyId(), rp.getParentMatrixId(), rp.getParentOperationId(), rpId};
			tn = new DefaultMutableTreeNode(new NodeElementInfo(rpId,
					rpId,
					Text.App.treeReport,
					"RP: " + rpId + " - " + rp.getReportFriendlyName()));
		} catch (IOException ex) {
			log.error(null, ex);
		}
		return tn;
	}
	//</editor-fold>

	//<editor-fold defaultstate="expanded" desc="NODE MANAGEMENT">
	//<editor-fold defaultstate="collapsed" desc="STUDY NODES">
	public static void insertLatestStudyNode() throws IOException {
		try {
			// GET LATEST ADDED STUDY
			List<Study> studyList = StudyList.getStudyList();
			TreePath parentPath = GWASpiExplorerPanel.getSingleton().getTree().getNextMatch(Text.App.treeStudyManagement, 0, Position.Bias.Forward);

			DefaultMutableTreeNode newNode = createStudyTreeNode(studyList.get(studyList.size() - 1).getStudyId());

			if (parentPath != null) {
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
				addNode(parentNode, newNode, true);
			}
		} catch (Exception ex) {
			log.error(null, ex);
		}
	}

	public static void insertStudyNode(int studyId) throws IOException {
		try {
			// GET STUDY
			TreePath parentPath = GWASpiExplorerPanel.getSingleton().getTree().getNextMatch(Text.App.treeStudyManagement, 0, Position.Bias.Forward);

			DefaultMutableTreeNode newNode = createStudyTreeNode(studyId);

			if (parentPath != null) {
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
				addNode(parentNode, newNode, true);
			}
		} catch (Exception ex) {
			log.error(null, ex);
		}
	}

	public static void deleteStudyNode(int studyId) {
		try {
			// GET DELETE PATH BY PREFIX ONLY
			TreePath deletePath = GWASpiExplorerPanel.getSingleton().getTree().getNextMatch("SID: " + studyId + " - ", 0, Position.Bias.Forward);

			if (deletePath != null) {
				DefaultMutableTreeNode deleteNode = (DefaultMutableTreeNode) deletePath.getLastPathComponent();
				deleteNode(deleteNode);
			}
		} catch (Exception ex) {
			log.error(null, ex);
		}
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="MATRIX NODES">
	public static void insertMatrixNode(int studyId, int matrixId) throws IOException {
		if (StartGWASpi.guiMode) {
			try {
				// GET STUDY
				Study study = new Study(studyId);
				TreePath parentPath = GWASpiExplorerPanel.getSingleton().getTree().getNextMatch("SID: " + study.getStudyId() + " - " + study.getStudyName(), 0, Position.Bias.Forward);

				DefaultMutableTreeNode newNode = createMatrixTreeNode(matrixId);

				if (parentPath != null) {
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
					addNode(parentNode, newNode, true);
				}
			} catch (IOException ex) {
				log.error(null, ex);
			}
		}
	}

	public static void deleteMatrixNode(int matrixId) {
		try {
			// GET DELETE PATH BY PREFIX ONLY
			TreePath deletePath = GWASpiExplorerPanel.getSingleton().getTree().getNextMatch("MX: " + matrixId + " - ", 0, Position.Bias.Forward);

			if (deletePath != null) {
				DefaultMutableTreeNode deleteNode = (DefaultMutableTreeNode) deletePath.getLastPathComponent();
				deleteNode(deleteNode);
			}
		} catch (Exception ex) {
			log.error(null, ex);
		}
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="OPERATION NODES">
	public static void insertOperationUnderMatrixNode(int matrixId, int opId) throws IOException {
		try {
			// GET MATRIX
			Matrix matrix = new Matrix(matrixId);
			TreePath parentPath = GWASpiExplorerPanel.getSingleton().getTree().getNextMatch("MX: " + matrixId + " - " + matrix.getMatrixMetadata().getMatrixFriendlyName(), 0, Position.Bias.Forward);

			DefaultMutableTreeNode newNode = createOperationTreeNode(opId);

			if (parentPath != null) {
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
				addNode(parentNode, newNode, true);
			}
		} catch (Exception ex) {
			log.error(null, ex);
		}
	}

	public static void insertSubOperationUnderOperationNode(int parentOpId, int opId) throws IOException {
		try {
			// GET MATRIX
			Operation parentOP = new Operation(parentOpId);
			TreePath parentPath = GWASpiExplorerPanel.getSingleton().getTree().getNextMatch("OP: " + parentOpId + " - " + parentOP.getOperationFriendlyName(), 0, Position.Bias.Forward);

			DefaultMutableTreeNode newNode = createOperationTreeNode(opId);

			if (parentPath != null) {
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
				addNode(parentNode, newNode, true);
			}
		} catch (Exception ex) {
			log.error(null, ex);
		}
	}

	public static void deleteOperationNode(int opId) {
		try {
			// GET DELETE PATH BY PREFIX ONLY
			TreePath deletePath = GWASpiExplorerPanel.getSingleton().getTree().getNextMatch("OP: " + opId + " - ", 0, Position.Bias.Forward);

			if (deletePath != null) {
				DefaultMutableTreeNode deleteNode = (DefaultMutableTreeNode) deletePath.getLastPathComponent();
				deleteNode(deleteNode);
			}
		} catch (Exception ex) {
			log.error(null, ex);
		}
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="REPORT NODES">
	public static void insertReportsUnderOperationNode(int parentOpId) throws IOException {
		try {
			// GET OPERATION
			Operation parentOP = new Operation(parentOpId);
			TreePath parentPath = GWASpiExplorerPanel.getSingleton().getTree().getNextMatch("OP: " + parentOpId + " - " + parentOP.getOperationFriendlyName(), 0, Position.Bias.Forward);
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();

			// GET ALL REPORTS UNDER THIS OPERATION
			List<Report> reportsList = ReportsList.getReportsList(parentOpId, Integer.MIN_VALUE);
			for (int n = 0; n < reportsList.size(); n++) {
				Report rp = reportsList.get(n);

				if (!parentOP.getOperationType().equals(OPType.HARDY_WEINBERG.toString()) && //DON'T SHOW SUPERFLUOUS OPEARATION INFO
						!parentOP.getOperationType().equals(OPType.SAMPLE_QA.toString())) {
					if (!rp.getReportType().equals(OPType.ALLELICTEST.toString())) {
						DefaultMutableTreeNode newNode = createReportTreeNode(reportsList.get(n).getReportId());
						addNode(parentNode, newNode, true);
					}
				}
			}
		} catch (Exception ex) {
			log.error(null, ex);
		}
	}
	//</editor-fold>

	public static DefaultMutableTreeNode addNode(
			DefaultMutableTreeNode parentNode,
			DefaultMutableTreeNode child,
			boolean shouldBeVisible)
	{
		DefaultTreeModel treeModel = (DefaultTreeModel) GWASpiExplorerPanel.getSingleton().getTree().getModel();
		treeModel.insertNodeInto(child, parentNode, parentNode.getChildCount());

		GWASpiExplorerPanel.getSingleton().getTree().expandPath(new TreePath(parentNode.getPath()));

		return child;
	}

	public static DefaultMutableTreeNode deleteNode(DefaultMutableTreeNode child) {

		DefaultTreeModel treeModel = (DefaultTreeModel) GWASpiExplorerPanel.getSingleton().getTree().getModel();
		treeModel.removeNodeFromParent(child);

		return child;
	}
	//</editor-fold>
}
