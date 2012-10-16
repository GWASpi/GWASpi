package org.gwaspi.model;

import org.gwaspi.constants.cNetCDF.Defaults.GenotypeEncoding;
import org.gwaspi.dao.MatrixService;
import org.gwaspi.dao.sql.MatrixServiceImpl;
import org.gwaspi.database.DbManager;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.gwaspi.netCDF.matrices.MatrixMetadata;

/**
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 * @deprecated use MatrixService directly
 */
public final class MatricesList {

	private static final MatrixService matrixService = new MatrixServiceImpl();

	private MatricesList() {
	}

	public static Matrix getById(int matrixId) throws IOException {
		return matrixService.getById(matrixId);
	}

	public static List<Matrix> getMatrixList(int studyId) throws IOException {
		return matrixService.getMatrixList(studyId);
	}

	public static List<Matrix> getMatrixList() throws IOException {
		return matrixService.getMatrixList();
	}

	public static List<Map<String, Object>> getAllMatricesList() throws IOException {
		return matrixService.getAllMatricesList();
	}

	public static Object[][] getMatricesTable(int studyId) throws IOException {
		return matrixService.getMatricesTable(studyId);
	}

	public static String createMatricesTable(DbManager db) {
		return matrixService.createMatricesTable(db);
	}

	public static void insertMatrixMetadata(
			DbManager dBManager,
			int studyId,
			String matrix_name,
			String netCDF_name,
			GenotypeEncoding matrix_type,
			int parent_matrix1_id,
			int parent_matrix2_id,
			String input_location,
			String description,
			int loaded)
			throws IOException
	{
		matrixService.insertMatrixMetadata(
				dBManager,
				studyId,
				matrix_name,
				netCDF_name,
				matrix_type,
				parent_matrix1_id,
				parent_matrix2_id,
				input_location,
				description,
				loaded);
	}

	public static void deleteMatrix(int matrixId, boolean deleteReports) {
		matrixService.deleteMatrix(matrixId, deleteReports);
	}

	public static String generateMatrixNetCDFNameByDate() {
		return matrixService.generateMatrixNetCDFNameByDate();
	}

	public static MatrixMetadata getLatestMatrixId() throws IOException {
		return matrixService.getLatestMatrixId();
	}
}
