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

package org.gwaspi.dao.jpa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.gwaspi.dao.MatrixService;
import org.gwaspi.dao.OperationService;
import org.gwaspi.dao.ReportService;
import org.gwaspi.model.DataSetKey;
import org.gwaspi.model.MatrixKey;
import org.gwaspi.model.MatrixMetadata;
import org.gwaspi.model.OperationMetadata;
import org.gwaspi.model.OperationsList;
import org.gwaspi.model.ReportsList;
import org.gwaspi.model.Study;
import org.gwaspi.model.StudyKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JPA implementation of a matrix service.
 */
public class JPAMatrixService implements MatrixService {

	private static final Logger LOG
			= LoggerFactory.getLogger(JPAMatrixService.class);

	private final JPAUtil jpaUtil;

	public JPAMatrixService(final EntityManagerFactory emf) {
		this.jpaUtil = new JPAUtil(emf);
	}

	private OperationService getOperationService() {
		return OperationsList.getOperationService();
	}

	private ReportService getReportService() {
		return ReportsList.getReportService();
	}

	private static List<MatrixKey> convertMatrixIdsToKeys(final StudyKey studyKey, final List<Integer> matrixIds) {

		final List<MatrixKey> matrixKeys = new ArrayList<MatrixKey>(matrixIds.size());
		for (final Integer matrixId : matrixIds) {
			matrixKeys.add(new MatrixKey(studyKey, matrixId));
		}

		return matrixKeys;
	}

	@Override
	public List<MatrixKey> getMatrixKeys(StudyKey studyKey) throws IOException {

		List<MatrixKey> matrices = Collections.EMPTY_LIST;

		EntityManager em = null;
		try {
			em = jpaUtil.open();
			Query query = em.createNamedQuery("matrixMetadata_listIdsByStudyId");
			query.setParameter("studyId", studyKey.getId());
			List<Integer> matricesIds = query.getResultList();
			matrices = new ArrayList<MatrixKey>(matricesIds.size());
			for (Integer matrixId : matricesIds) {
				matrices.add(new MatrixKey(studyKey, matrixId));
			}
//		} catch (Exception ex) {
//			LOG.error("Failed fetching all matrices", ex);
		} finally {
			jpaUtil.close(em);
		}

		return matrices;
	}

	@Override
	public List<MatrixMetadata> getMatrices(StudyKey studyKey) throws IOException {

		List<MatrixMetadata> matricesMetadata = Collections.EMPTY_LIST;

		EntityManager em = null;
		try {
			em = jpaUtil.open();
			Query query = em.createNamedQuery("matrixMetadata_listByStudyId");
			query.setParameter("studyId", studyKey.getId());
			matricesMetadata = query.getResultList();
//		} catch (IOException ex) {
//			LOG.error("Failed fetching all matrices-metadata", ex);
		} finally {
			jpaUtil.close(em);
		}

		return matricesMetadata;
	}

	@Override
	public MatrixKey insertMatrix(MatrixMetadata matrixMetadata) throws IOException {

		EntityManager em = null;
		try {
			em = jpaUtil.open();
			jpaUtil.begin(em);
			if (matrixMetadata.getMatrixId() == MatrixKey.NULL_ID) {
				em.persist(matrixMetadata);
			} else {
				em.merge(matrixMetadata);
			}
			jpaUtil.commit(em);
		} catch (Exception ex) {
			LOG.error("Failed adding a matrix-metadata", ex);
			jpaUtil.rollback(em);
		} finally {
			jpaUtil.close(em);
		}

		return MatrixKey.valueOf(matrixMetadata);
	}

	@Override
	public void deleteMatrix(MatrixKey matrixKey, boolean deleteReports) throws IOException {

		MatrixMetadata matrixMetadata = null;

		// DELETE METADATA INFO FROM DB
		EntityManager em = null;
		try {
			em = jpaUtil.open();
			jpaUtil.begin(em);
			matrixMetadata = em.find(MatrixMetadata.class, matrixKey);
			if (matrixMetadata == null) {
				throw new IllegalArgumentException("No matrix found with this ID: (" + matrixKey.getStudyId() + ") " + matrixKey.getMatrixId());
			}
			em.remove(matrixMetadata); // This is done implicitly by remove(matrix)
			jpaUtil.commit(em);
		} catch (Exception ex) {
			jpaUtil.rollback(em);
			throw new IOException("Failed deleting matrix by"
					+ ": study-id: " + matrixKey.getStudyId()
					+ ", matrix-id: " + matrixKey.getMatrixId(),
					ex);
		} finally {
			jpaUtil.close(em);
		}

		String genotypesFolder = Study.constructGTPath(matrixMetadata.getKey().getStudyKey());

		final DataSetKey matrixDataSetKey = new DataSetKey(matrixKey);

		// DELETE OPERATION netCDFs FROM THIS MATRIX
		final List<OperationMetadata> operations = getOperationService().getOffspringOperationsMetadata(matrixDataSetKey);
		for (OperationMetadata op : operations) {
			org.gwaspi.global.Utils.tryToDeleteFile(OperationMetadata.generatePathToNetCdfFile(op));
		}

		getReportService().deleteReports(matrixDataSetKey);

		// DELETE MATRIX NETCDF FILE
		File matrixFile = MatrixMetadata.generatePathToNetCdfFile(matrixMetadata);
		org.gwaspi.global.Utils.tryToDeleteFile(matrixFile);
	}

	@Override
	public void updateMatrix(MatrixMetadata matrixMetadata) throws IOException {

		EntityManager em = null;
		try {
			em = jpaUtil.open();
			jpaUtil.begin(em);
			em.merge(matrixMetadata);
			jpaUtil.commit(em);
		} catch (Exception ex) {
			LOG.error("Failed adding a matrix-metadata", ex);
			jpaUtil.rollback(em);
		} finally {
			jpaUtil.close(em);
		}
	}

	@Override
	public MatrixMetadata getMatrix(MatrixKey matrixKey) throws IOException {

		MatrixMetadata matrixMetadata = null;

		EntityManager em = null;
		try {
			em = jpaUtil.open();
			Query query = em.createNamedQuery("matrixMetadata_fetchById");
			query.setParameter("id", matrixKey.getMatrixId());
			matrixMetadata = (MatrixMetadata) query.getSingleResult();
		} catch (NoResultException ex) {
			LOG.error("Failed fetching matrix-metadata by id: " + matrixKey.toRawIdString()
					+ " (id not found)", ex);
			jpaUtil.close(em);
			LOG.info("Available matrices:");
			List<MatrixKey> matrixList = getMatrixKeys(matrixKey.getStudyKey());
			StringBuilder matrices = new StringBuilder();
			for (MatrixKey mat : matrixList) {
				matrices.append(" {study-id: ");
				matrices.append(mat.getStudyId()).append(", matrix-id: ");
				matrices.append(mat.getMatrixId()).append("}");
			}
			LOG.info(matrices.toString());
		} catch (Exception ex) {
			LOG.error("Failed fetching matrix-metadata by id: " + matrixKey.toRawIdString(), ex);
		} finally {
			jpaUtil.close(em);
		}

		return matrixMetadata;
	}

	@Override
	public List<MatrixKey> getMatrixKeysBySimpleName(final StudyKey studyKey, final String simpleName) throws IOException {

		List<MatrixKey> matrices = Collections.EMPTY_LIST;

		EntityManager em = null;
		try {
			em = jpaUtil.open();
			Query query = em.createNamedQuery("matrixMetadata_listKeysByStudyIdAndSimpleName");
			query.setParameter("studyId", studyKey.getId());
			query.setParameter("simpleName", simpleName);
			matrices = convertMatrixIdsToKeys(studyKey, query.getResultList());
		} catch (NoResultException ex) {
			LOG.error("Failed fetching matrix-keys by simple name: " + simpleName
					+ " (id not found)", ex);
		} catch (Exception ex) {
			LOG.error("Failed fetching matrix-keys by simple name: " + simpleName, ex);
		} finally {
			jpaUtil.close(em);
		}

		return matrices;
	}

	@Override
	public List<MatrixKey> getMatrixKeysByName(final StudyKey studyKey, final String friendlyName) throws IOException {

		List<MatrixKey> matrices = Collections.EMPTY_LIST;

		EntityManager em = null;
		try {
			em = jpaUtil.open();
			Query query = em.createNamedQuery("matrixMetadata_listKeysByStudyIdAndFriendlyName");
			query.setParameter("studyId", studyKey.getId());
			query.setParameter("friendlyName", friendlyName);
			matrices = convertMatrixIdsToKeys(studyKey, query.getResultList());
		} catch (NoResultException ex) {
			LOG.error("Failed fetching matrix-keys by friendly name: " + friendlyName
					+ " (id not found)", ex);
		} catch (Exception ex) {
			LOG.error("Failed fetching matrix-keys by friendly name: " + friendlyName, ex);
		} finally {
			jpaUtil.close(em);
		}

		return matrices;
	}
}
