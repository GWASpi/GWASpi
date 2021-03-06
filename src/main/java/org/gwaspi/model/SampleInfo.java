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

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.gwaspi.constants.DBSamplesConstants;
import org.gwaspi.global.Extractor;

@Entity
@Table(name = "sampleInfo")
@IdClass(SampleKey.class)
@NamedQueries({
	@NamedQuery(
		name = "sampleInfo_listKeysByStudyId",
		query = "SELECT s.studyId, s.sampleId, s.familyId FROM SampleInfo s WHERE s.studyId = :studyId"),
	@NamedQuery(
		name = "sampleInfo_listByStudyId",
		query = "SELECT s FROM SampleInfo s WHERE s.studyId = :studyId"),
	@NamedQuery(
		name = "sampleInfo_listBySampleIdFamilyIdStudyId",
		query = "SELECT s FROM SampleInfo s WHERE s.sampleId = :sampleId AND s.familyId = :familyId AND s.studyId = :studyId"),
	@NamedQuery(
		name = "sampleInfo_deleteByStudyId",
		query = "DELETE FROM SampleInfo s WHERE s.studyId = :studyId"),
})
public class SampleInfo implements Comparable<SampleInfo>, Serializable {

	private static final long serialVersionUID = 1L;

	public static final int ORDER_NULL_ID = -1; // alternatively: Integer.MIN_VALUE

	public static final Extractor<SampleInfo, SampleKey> TO_SAMPLE_KEY
			= new Extractor<SampleInfo, SampleKey>()
	{
		@Override
		public SampleKey extract(SampleInfo from) {
			return SampleKey.valueOf(from);
		}
	};

	public static final Extractor<SampleInfo, String> TO_SAMPLE_ID
			= new Extractor<SampleInfo, String>()
	{
		@Override
		public String extract(SampleInfo from) {
			return from.getSampleId();
		}
	};

	public static final Extractor<SampleInfo, String> TO_FAMILY_ID
			= new Extractor<SampleInfo, String>()
	{
		@Override
		public String extract(SampleInfo from) {
			return from.getFamilyId();
		}
	};

	public static final Extractor<SampleInfo, Integer> TO_ORDER_ID
			= new Extractor<SampleInfo, Integer>()
	{
		@Override
		public Integer extract(SampleInfo from) {
			return from.getOrderId();
		}
	};

	public static final Extractor<SampleInfo, String> TO_FATHER_ID
			= new Extractor<SampleInfo, String>()
	{
		@Override
		public String extract(SampleInfo from) {
			return from.getFatherId();
		}
	};

	public static final Extractor<SampleInfo, String> TO_MOTHER_ID
			= new Extractor<SampleInfo, String>()
	{
		@Override
		public String extract(SampleInfo from) {
			return from.getMotherId();
		}
	};

	public static final Extractor<SampleInfo, Sex> TO_SEX
			= new Extractor<SampleInfo, Sex>()
	{
		@Override
		public Sex extract(SampleInfo from) {
			return from.getSex();
		}
	};

	public static final Extractor<SampleInfo, Affection> TO_AFFECTION
			= new Extractor<SampleInfo, Affection>()
	{
		@Override
		public Affection extract(SampleInfo from) {
			return from.getAffection();
		}
	};

	public static final Extractor<SampleInfo, String> TO_CATEGORY
			= new Extractor<SampleInfo, String>()
	{
		@Override
		public String extract(SampleInfo from) {
			return from.getCategory();
		}
	};

	public static final Extractor<SampleInfo, String> TO_DISEASE
			= new Extractor<SampleInfo, String>()
	{
		@Override
		public String extract(SampleInfo from) {
			return from.getDisease();
		}
	};

	public static final Extractor<SampleInfo, String> TO_POPULATION
			= new Extractor<SampleInfo, String>()
	{
		@Override
		public String extract(SampleInfo from) {
			return from.getPopulation();
		}
	};

	public static final Extractor<SampleInfo, Integer> TO_AGE
			= new Extractor<SampleInfo, Integer>()
	{
		@Override
		public Integer extract(SampleInfo from) {
			return from.getAge();
		}
	};

	public static final Extractor<SampleInfo, String> TO_FILTER
			= new Extractor<SampleInfo, String>()
	{
		@Override
		public String extract(SampleInfo from) {
			return from.getFilter();
		}
	};

	public static final Extractor<SampleInfo, Integer> TO_APPROVED
			= new Extractor<SampleInfo, Integer>()
	{
		@Override
		public Integer extract(SampleInfo from) {
			return from.getApproved();
		}
	};

	public static final Extractor<SampleInfo, Integer> TO_STATUS
			= new Extractor<SampleInfo, Integer>()
	{
		@Override
		public Integer extract(SampleInfo from) {
			return from.getStatus();
		}
	};

	public static enum Sex {
		UNKNOWN,
		MALE,
		FEMALE;

		/**
		 * Parse by the PLINK standard
		 */
		public static Sex parse(String sexStr) {

			try {
				return Sex.values()[Integer.parseInt(sexStr)];
			} catch (NumberFormatException ex) {
				throw new ParseException(sexStr + " is not a valid sex value", ex);
			}
		}

		/**
		 * Encode with the PLINK standard,
		 * optionally using the alternative long-encoding
		 */
		public static int toNumber(Sex sex) {
			return sex.ordinal();
		}
	}

	/**
	 * PLINK standards:
	 *
	 * default:
	 *   -9 missing
	 *   0 missing
	 *   1 unaffected
	 *   2 affected
	 *
	 * alternative:
	 *   -9 missing
	 *   0 unaffected
	 *   1 affected
	 */
	public static enum Affection {
		/** Undefined / Missing */
		UNKNOWN,
		/** Control */
		UNAFFECTED,
		/** Case */
		AFFECTED;

		/**
		 * Returns whether the supplied affection is valid for testing,
		 * which requires it to be either affected or unaffected.
		 */
		public static boolean isValid(Affection affection) {
//			return ((affection != null) && (affection != UNKNOWN));
			return ((affection == AFFECTED) || (affection == UNAFFECTED));
		}

		/** Parse by the PLINK standard */
		public static Affection parse(String affectionStr) {
			return parse(affectionStr, false);
		}

		/**
		 * Parse by the PLINK standard,
		 * optionally using the alternative encoding.
		 * @param altEnc use the alternative encoding (@{see #Affection})
		 */
		public static Affection parse(String affectionStr, boolean altEnc) {

			Affection result = Affection.UNKNOWN;

			try {
				int affectionInt = Integer.parseInt(affectionStr);

				if (altEnc) {
					switch (affectionInt) {
						case 0:
							result = UNAFFECTED;
							break;
						case 1:
							result = AFFECTED;
							break;
						case -9:
							result = UNKNOWN;
							break;
						default:
							throw new ParseException(affectionInt + " is not a valid affection value");
					}
				} else {
					// default
					switch (affectionInt) {
						case 0:
							result = UNKNOWN;
							break;
						case 1:
							result = UNAFFECTED;
							break;
						case 2:
							result = AFFECTED;
							break;
						case -9:
							result = UNKNOWN;
							break;
						default:
							throw new ParseException(affectionInt + " is not a valid affection value (alternative standard)");
					}
				}
			} catch (NumberFormatException ex) {
				throw new ParseException("\"" + affectionStr + "\" is not a valid affection value", ex);
			}

			return result;
		}

		/**
		 * Encode with the PLINK standard,
		 * optionally using the alternative long-encoding
		 */
		public static int toNumber(Affection affection) {
			return toNumber(affection, false);
		}

		/**
		 * Encode with the PLINK standard.
		 * @param altEnc use the alternative encoding (@{see #Affection})
		 */
		public static int toNumber(Affection affection, boolean altEnc) {

			int result = -9;

			if (altEnc) {
				switch (affection) {
					case UNKNOWN:
						result = -9;
						break;
					case AFFECTED:
						result = 0;
						break;
					case UNAFFECTED:
						result = 1;
						break;
					default:
						throw new ParseException(affection + " is not a valid affection value; can not be converted to a number");
				}
			} else {
				// default
				switch (affection) {
					case UNKNOWN:
						result = 0;
						break;
					case AFFECTED:
						result = 1;
						break;
					case UNAFFECTED:
						result = 2;
						break;
					default:
						throw new ParseException(affection + " is not a valid affection value; can not be converted to a number (alternative standard)");
				}
			}

			return result;
		}
	}

	private SampleKey key;
	private int orderId;
	private String fatherId;
	private String motherId;
	private Sex sex;
	private Affection affection;
	private String category;
	private String disease;
	private String population;
	private int age;
	private String filter;
	private int approved;
	private int status;

	public SampleInfo() {

		this.orderId = ORDER_NULL_ID;
		this.fatherId = "0";
		this.motherId = "0";
		this.sex = Sex.UNKNOWN;
		this.affection = Affection.UNKNOWN;
		this.category = "0";
		this.disease = "0";
		this.population = "0";
		this.age = 0;
		this.filter = "";
		this.approved = 0;
		this.status = 0;
		this.key = new SampleKey(new StudyKey(StudyKey.NULL_ID), "0", "0");
	}

	public SampleInfo(StudyKey studyKey, String sampleId) {
		this();

		this.key = new SampleKey(studyKey, sampleId, "0");
	}

	public SampleInfo(
			StudyKey studyKey,
			String sampleId,
			String familyId,
			Sex sex,
			Affection affection)
	{
		this.orderId = ORDER_NULL_ID;
		this.fatherId = "";
		this.motherId = "";
		this.sex = sex;
		this.affection = affection;
		this.category = "";
		this.disease = "";
		this.population = "";
		this.age = 0;
		this.filter = "";
		this.approved = 0;
		this.status = 0;
		this.key = new SampleKey(studyKey, sampleId, familyId);
	}

	public SampleInfo(
			StudyKey studyKey,
			String sampleId,
			String familyId,
			String fatherId,
			String motherId,
			Sex sex,
			Affection affection)
	{
		this.orderId = ORDER_NULL_ID;
		this.fatherId = fatherId;
		this.motherId = motherId;
		this.sex = sex;
		this.affection = affection;
		this.category = "0";
		this.disease = "0";
		this.population = "0";
		this.age = 0;
		this.filter = "";
		this.approved = 0;
		this.status = 0;
		this.key = new SampleKey(studyKey, sampleId, familyId);
	}

	public SampleInfo(
			StudyKey studyKey,
			String sampleId,
			String familyId,
			int orderId,
			String fatherId,
			String motherId,
			Sex sex,
			Affection affection,
			String category,
			String disease,
			String population,
			int age,
			String filter,
			int approved,
			int status)
	{
		this.key = new SampleKey(studyKey, sampleId, familyId);
		this.orderId = orderId;
		this.fatherId = fatherId;
		this.motherId = motherId;
		this.sex = sex;
		this.affection = affection;
		this.category = category;
		this.disease = disease;
		this.population = population;
		this.age = age;
		this.filter = filter;
		this.approved = approved;
		this.status = status;
	}

	@Override
	public int compareTo(SampleInfo other) {
		return hashCode() - other.hashCode();
	}

	@Override
	public boolean equals(Object other) {

		boolean equal = false;

		if (other instanceof SampleInfo) { // TODO replace with sampleKey.equals other.sampleKey for code reuse ++
			SampleInfo otherSampleInfo = (SampleInfo) other;
			equal = getFamilyId().equals(otherSampleInfo.getFamilyId());
			equal = equal && (getStudyId() == otherSampleInfo.getStudyId());
			equal = equal && getSampleId().equals(otherSampleInfo.getSampleId());
		}

		return equal;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 23 * hash + (this.getSampleId() != null ? this.getSampleId().hashCode() : 0);
		hash = 23 * hash + (this.getFamilyId() != null ? this.getFamilyId().hashCode() : 0);
		hash = 23 * hash + this.getStudyId();
		return hash;
	}

	/**
	 * Returns a unique identifier for this specific sample.
	 */
	@Transient
	public SampleKey getKey() {
		return key;
	}

	@Column(
		name       = "orderId",
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public int getOrderId() {
		return orderId;
	}

	protected void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	/**
	 * Returns the sample-id, which is unique in combination with the family-id.
	 * @return the sample-id
	 */
	@Id
	@Column(
		name       = "sampleId",
		length     = 127,
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public String getSampleId() {
		return getKey().getSampleId();
	}

	/**
	 * Sets the unique study identification number.
	 * @param sampleId the sample-id to set
	 */
	public void setSampleId(String sampleId) {
		this.key = new SampleKey(
				getStudyKey(),
				sampleId,
				getFamilyId());
	}

	/**
	 * Returns the family-id, which is unique in combination with the sample-id.
	 * @return the family-id
	 */
	@Id
	@Column(
		name       = "familyId",
		length     = 127,
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public String getFamilyId() {
		return getKey().getFamilyId();
	}

	protected void setFamilyId(String familyId) {
		this.key = new SampleKey(
				getStudyKey(),
				getSampleId(),
				familyId);
	}

	@Column(
		name       = "fatherId",
		length     = 127,
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public String getFatherId() {
		return fatherId;
	}

	protected void setFatherId(String fatherId) {
		this.fatherId = fatherId;
	}

	@Column(
		name       = "motherId",
		length     = 127,
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public String getMotherId() {
		return motherId;
	}

	protected void setMotherId(String motherId) {
		this.motherId = motherId;
	}

	@Column(
		name       = "sex",
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public Sex getSex() {
		return sex;
	}

	protected void setSex(Sex sex) {
		this.sex = sex;
	}

	/** @deprecated */
	@Transient
	public String getSexStr() {

		if (getSex() != null) {
			return String.valueOf(getSex().ordinal());
		} else {
			return "0";
		}
	}

	@Column(
		name       = "affection",
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public Affection getAffection() {
		return affection;
	}

	protected void setAffection(Affection affection) {
		this.affection = affection;
	}

	/** @deprecated */
	@Transient
	public String getAffectionStr() {

		if (getAffection() != null) {
			return String.valueOf(getAffection().ordinal());
		} else {
			return "0";
		}
	}

	@Column(
		name       = "category",
		length     = 127,
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public String getCategory() {
		return category;
	}

	protected void setCategory(String category) {
		this.category = category;
	}

	@Column(
		name       = "disease",
		length     = 127,
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public String getDisease() {
		return disease;
	}

	protected void setDisease(String disease) {
		this.disease = disease;
	}

	@Column(
		name       = "population",
		length     = 127,
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public String getPopulation() {
		return population;
	}

	protected void setPopulation(String population) {
		this.population = population;
	}

	@Column(
		name       = "age",
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public int getAge() {
		return age;
	}

	protected void setAge(int age) {
		this.age = age;
	}

	@Column(
		name       = "filter",
		length     = 255,
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public String getFilter() {
		return filter;
	}

	protected void setFilter(String filter) {
		this.filter = filter;
	}

	@Id
	@Column(
		name       = "studyId",
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public int getStudyId() {
		return key.getStudyKey().getId();
	}

	protected void setStudyId(int studyId) {
		this.key = new SampleKey(
				new StudyKey(studyId),
				getSampleId(),
				getFamilyId());
	}

	@Transient
	public StudyKey getStudyKey() {
		return key.getStudyKey();
	}

	@Column(
		name       = "approved",
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public int getApproved() {
		return approved;
	}

	protected void setApproved(int approved) {
		this.approved = approved;
	}

	@Column(
		name       = "status",
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public int getStatus() {
		return status;
	}

	protected void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @deprecated
	 * @param fieldName see org.gwaspi.constants.cDBSamples#F_*
	 * @return String, Integer, Boolean or null
	 */
	public Object getField(String fieldName) {

		if (fieldName.equals(DBSamplesConstants.F_ID)) {
			return getOrderId();
		} else if (fieldName.equals(DBSamplesConstants.F_SAMPLE_ID)) {
			return getSampleId();
		} else if (fieldName.equals(DBSamplesConstants.F_FAMILY_ID)) {
			return getFamilyId();
		} else if (fieldName.equals(DBSamplesConstants.F_FATHER_ID)) {
			return getFatherId();
		} else if (fieldName.equals(DBSamplesConstants.F_MOTHER_ID)) {
			return getMotherId();
		} else if (fieldName.equals(DBSamplesConstants.F_SEX)) {
			return getSexStr();
		} else if (fieldName.equals(DBSamplesConstants.F_AFFECTION)) {
			return getAffectionStr();
		} else if (fieldName.equals(DBSamplesConstants.F_CATEGORY)) {
			return getCategory();
		} else if (fieldName.equals(DBSamplesConstants.F_DISEASE)) {
			return getDisease();
		} else if (fieldName.equals(DBSamplesConstants.F_POPULATION)) {
			return getPopulation();
		} else if (fieldName.equals(DBSamplesConstants.F_AGE)) {
			return getAge();
		} else if (fieldName.equals(DBSamplesConstants.F_FILTER)) {
			return getFilter();
		} else if (fieldName.equals(DBSamplesConstants.F_POOL_ID)) {
			return getStudyId();
		} else if (fieldName.equals(DBSamplesConstants.F_APPROVED)) {
			return getApproved();
		} else if (fieldName.equals(DBSamplesConstants.F_STATUS_ID_FK)) {
			return getStatus();
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		return "SampleInfo[" + getKey().toString() + "]";
	}
}
