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

package org.gwaspi.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.gwaspi.global.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "study")
@IdClass(StudyKey.class)
@NamedQueries({
	@NamedQuery(name = "study_listKeys",       query = "SELECT s.id FROM Study s"),
	@NamedQuery(name = "study_list",           query = "SELECT s FROM Study s"),
	@NamedQuery(name = "study_fetchById",      query = "SELECT s FROM Study s WHERE s.id = :id"),
})
public class Study implements Serializable {

	private static final Logger log
			= LoggerFactory.getLogger(Study.class);

	private int id; // id INTEGER generated by default as identity
	private String name = ""; // name VARCHAR(64)
	private String description = ""; // study_description LONG VARCHAR
	private String studyType = ""; // study_type VARCHAR(255)
	private String validity = ""; // validity SMALLINT
	private Date creationDate; // creation_date TIMESTAMP (2009-05-13 17:22:10.984)

	protected Study() {

		this.id = StudyKey.NULL_ID;
		this.name = "";
		this.description = "";
		this.studyType = "";
		this.validity = "";
		this.creationDate = new Date();
	}

	public Study(String name, String description) {

		this.id = StudyKey.NULL_ID;
		this.name = name;
		this.description = description;
		this.studyType = "";
		this.validity = "";
		this.creationDate = new Date();
	}

	public Study(int id, String name, String description, String studyType, String validity, Date creationDate) {

		this.id = id;
		this.name = name;
		this.description = description;
		this.studyType = studyType;
		this.validity = validity;
		this.creationDate = (creationDate == null)
				? null : (Date) creationDate.clone();
	}

	/**
	 * Returns the location of the DB-external storage path
	 * for genotypes.
	 * @param studyKey each study has its own sub-path
	 * @return
	 * @throws IOException
	 */
	public static String constructGTPath(StudyKey studyKey) throws IOException {
		return constructStoragePath(studyKey, Config.PROPERTY_GENOTYPES_DIR);
	}

	/**
	 * Returns the location of the DB-external storage path
	 * for reports.
	 * @param studyKey each study has its own sub-path
	 * @return
	 * @throws IOException
	 */
	public static String constructReportsPath(StudyKey studyKey) throws IOException {
		return constructStoragePath(studyKey, Config.PROPERTY_REPORTS_DIR);
	}

	/**
	 * Returns the location of the DB-external storage path
	 * for exports.
	 * @param studyKey each study has its own sub-path
	 * @return
	 * @throws IOException
	 */
	public static String constructExportsPath(StudyKey studyKey) throws IOException {
		return constructStoragePath(studyKey, Config.PROPERTY_EXPORT_DIR);
	}

	private static String constructStoragePath(StudyKey studyKey, String basePathConfigKey) throws IOException {
		return String.format("%s/STUDY_%d/",
				Config.getSingleton().getString(basePathConfigKey, ""),
				studyKey.getId());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Study other = (Study) obj;

		return this.getId() == other.getId();
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 71 * hash + this.getId();
		return hash;
	}

	/**
	 * Returns the unique study identification number.
	 * @return the study-id
	 */
	@Id
	@SequenceGenerator(name = "seqStudyId")
	@GeneratedValue(strategy = javax.persistence.GenerationType.TABLE, generator = "seqStudyId")
	@Column(
		name       = "id",
		unique     = true,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public int getId() {
		return id;
	}

	/**
	 * Sets the unique study identification number.
	 * @param id the study-id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	@Column(
		name       = "name",
		length     = 127,
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	@Column(
		name       = "description",
		length     = 255,
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(
		name       = "studyType",
		length     = 63,
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public String getStudyType() {
		return studyType;
	}

	protected void setStudyType(String studyType) {
		this.studyType = studyType;
	}

	@Column(
		name       = "validity",
		length     = 31,
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public String getValidity() {
		return validity;
	}

	protected void setValidity(String validity) {
		this.validity = validity;
	}

	@Temporal(TemporalType.DATE)
	@Column(
		name       = "creationDate",
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public Date getCreationDate() {
		return (creationDate == null) ? null : (Date) creationDate.clone();
	}

	protected void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
}
