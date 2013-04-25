package org.gwaspi.dao;

import java.io.IOException;
import java.util.List;
import org.gwaspi.constants.cNetCDF.Defaults.OPType;
import org.gwaspi.model.MatrixOperationSpec;
import org.gwaspi.model.Operation;
import org.gwaspi.model.OperationMetadata;

public interface OperationService {

	Operation getById(int operationId) throws IOException;

	List<Operation> getOperationsList(int parentMatrixId) throws IOException;

	List<Operation> getOperationsList(int parentMatrixId, int parentOpId) throws IOException;

	List<Operation> getOperationsList(int parentMatrixId, int parentOpId, OPType opType) throws IOException;

	List<OperationMetadata> getOperationsTable(int parentMatrixId) throws IOException;

	List<OperationMetadata> getOperationsTable(int parentMatrixId, int opId) throws IOException;

	int getIdOfLastOperationTypeOccurance(List<Operation> operationsList, OPType opType);

	String createOperationsMetadataTable();

	void insertOPMetadata(OperationMetadata operationMetadata) throws IOException;

	List<MatrixOperationSpec> getMatrixOperations(int matrixId) throws IOException;

	void deleteOperationBranch(int studyId, int opId, boolean deleteReports) throws IOException;

	OperationMetadata getOperationMetadata(int opId) throws IOException;

	OperationMetadata getOperationMetadata(String netCDFname) throws IOException;
}
