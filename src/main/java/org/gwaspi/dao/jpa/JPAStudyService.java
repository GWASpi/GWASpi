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
import org.gwaspi.dao.SampleInfoService;
import org.gwaspi.dao.StudyService;
import org.gwaspi.gui.GWASpiExplorerPanel;
import org.gwaspi.model.GWASpiExplorerNodes;
import org.gwaspi.model.MatricesList;
import org.gwaspi.model.MatrixKey;
import org.gwaspi.model.SampleInfoList;
import org.gwaspi.model.Study;
import org.gwaspi.model.StudyKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JPA implementation of a study service.
 */
public class JPAStudyService implements StudyService {

	private static final Logger LOG
			= LoggerFactory.getLogger(JPAStudyService.class);

	private final JPAUtil jpaUtil;

	public JPAStudyService(EntityManagerFactory emf) {
		this.jpaUtil = new JPAUtil(emf);
	}

	private MatrixService getMatrixService() {
		return MatricesList.getMatrixService();
	}

	private SampleInfoService getSampleInfoService() {
		return SampleInfoList.getSampleInfoService();
	}

	@Override
	public Study getStudy(StudyKey studyKey) throws IOException {

		Study study = null;

		EntityManager em = null;
		try {
			em = jpaUtil.open();
			study = em.find(Study.class, studyKey);
		} catch (Exception ex) {
			throw new IOException("Failed fetching a study by: " + studyKey.toRawIdString(), ex);
		} finally {
			jpaUtil.close(em);
		}

		return study;
	}

	@Override
	public List<StudyKey> getStudies() throws IOException {

		List<StudyKey> studies = Collections.EMPTY_LIST;

		EntityManager em = null;
		try {
			em = jpaUtil.open();
			Query query = em.createNamedQuery("study_listKeys");
			studies = convertFieldsToStudyKeys(query.getResultList());
		} catch (Exception ex) {
			LOG.error("Failed fetching all study keys", ex);
		} finally {
			jpaUtil.close(em);
		}

		return studies;
	}

	@Override
	public StudyKey getStudyByName(final String name) throws IOException {

		StudyKey studyKey = null;

		EntityManager em = null;
		try {
			em = jpaUtil.open();
			final Query query = em.createNamedQuery("study_fetchKeyByName");
			query.setParameter("name", name);
			final Integer studyId = (Integer) query.getSingleResult();
			studyKey = new StudyKey(studyId);
		} catch (final NoResultException ex) {
			studyKey = null;
		} catch (final Exception ex) {
			LOG.error("Failed fetching study-key by name", ex);
		} finally {
			jpaUtil.close(em);
		}

		return studyKey;
	}

	private static List<StudyKey> convertFieldsToStudyKeys(List<Object> studyIds) {

		List<StudyKey> studies = new ArrayList<StudyKey>(studyIds.size());
		for (Object studyId : studyIds) {
			studies.add(new StudyKey((Integer) studyId));
		}

		return studies;
	}

	@Override
	public List<Study> getStudiesInfos() throws IOException {

		List<Study> studies = Collections.EMPTY_LIST;

		EntityManager em = null;
		try {
			em = jpaUtil.open();
			studies = em.createNamedQuery("study_list").getResultList();
		} catch (Exception ex) {
			LOG.error("Failed fetching all studies", ex);
		} finally {
			jpaUtil.close(em);
		}

		return studies;
	}

	@Override
	public StudyKey insertStudy(Study study) {

		EntityManager em = null;
		try {
			em = jpaUtil.open();
			jpaUtil.begin(em);
			if (study.getId() == StudyKey.NULL_ID) {
				em.persist(study);
			} else {
				throw new IllegalArgumentException("Study was already persisted!");
			}
			jpaUtil.commit(em);
		} catch (Exception ex) {
			LOG.error("Failed adding a study", ex);
			jpaUtil.rollback(em);
		} finally {
			jpaUtil.close(em);
		}

		return StudyKey.valueOf(study);
	}

	@Override
	public void deleteStudy(StudyKey studyKey, boolean deleteReports) throws IOException {

		Study study;

		// DELETE METADATA INFO FROM DB
		EntityManager em = null;
		try {
			em = jpaUtil.open();
			jpaUtil.begin(em);
			study = em.find(Study.class, studyKey);
			if (study == null) {
				throw new IllegalArgumentException("No study found with this key: "
						+ studyKey.toRawIdString());
			}
			em.remove(study);
			jpaUtil.commit(em);
		} catch (Exception ex) {
			jpaUtil.rollback(em);
			throw new IOException("Failed deleting study by: " + studyKey.toRawIdString(), ex);
		} finally {
			jpaUtil.close(em);
		}
		final List<MatrixKey> matrices = getMatrixService().getMatrixKeys(studyKey);

		for (MatrixKey toBeDeletedMatrix : matrices) {
			try {
				getMatrixService().deleteMatrix(toBeDeletedMatrix, deleteReports);
				GWASpiExplorerNodes.deleteMatrixNode(toBeDeletedMatrix);
				GWASpiExplorerPanel.getSingleton().updateTreePanel(true);
			} catch (IOException ex) {
				LOG.warn(null, ex);
			}
		}

		// DELETE STUDY FOLDERS
		File gtStudyFolder = new File(Study.constructGTPath(studyKey));
		org.gwaspi.global.Utils.deleteFolder(gtStudyFolder);

		if (deleteReports) {
			File repStudyFolder = new File(Study.constructReportsPath(studyKey));
			org.gwaspi.global.Utils.deleteFolder(repStudyFolder);
		}

		// DELETE STUDY POOL SAMPLES
		getSampleInfoService().deleteSamples(studyKey);
	}

	@Override
	public void updateStudy(Study study) throws IOException {

		EntityManager em = null;
		try {
			em = jpaUtil.open();
			jpaUtil.begin(em);
			if (study.getId() == StudyKey.NULL_ID) {
				throw new IllegalArgumentException("Study was not yet persisted!");
			}
			em.merge(study);
			jpaUtil.commit(em);
		} catch (Exception ex) {
			LOG.error("Failed updating a study", ex);
			jpaUtil.rollback(em);
		} finally {
			jpaUtil.close(em);
		}
	}
}
