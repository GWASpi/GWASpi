package org.gwaspi.model;

import org.gwaspi.constants.cDBSamples;

public class SampleInfo implements Comparable<SampleInfo> {

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

	private int orderId;
	private String sampleId;
	private String familyId;
	private String fatherId;
	private String motherId;
	private Sex sex;
	private Affection affection;
	private String category;
	private String disease;
	private String population;
	private int age;
	private String filter;
	private String poolId;
	private int approved;
	private int status;

	public SampleInfo() {
		this.orderId = Integer.MIN_VALUE;
		this.sampleId = "0";
		this.familyId = "0";
		this.fatherId = "0";
		this.motherId = "0";
		this.sex = Sex.UNKNOWN;
		this.affection = Affection.UNKNOWN;
		this.category = "0";
		this.disease = "0";
		this.population = "0";
		this.age = 0;
		this.filter = "";
		this.poolId = "";
		this.approved = 0;
		this.status = 0;
	}

	public SampleInfo(String sampleId) {
		this();

		this.sampleId = sampleId;
	}

	public SampleInfo(
			String sampleId,
			String familyId,
			String fatherId,
			String motherId,
			Sex sex,
			Affection affection,
			String category,
			String disease,
			String population,
			int age)
	{
		this.orderId = Integer.MIN_VALUE;
		this.sampleId = sampleId;
		this.familyId = familyId;
		this.fatherId = fatherId;
		this.motherId = motherId;
		this.sex = sex;
		this.affection = affection;
		this.category = category;
		this.disease = disease;
		this.population = population;
		this.age = age;
		this.filter = "";
		this.poolId = "";
		this.approved = 0;
		this.status = 0;
	}


	public SampleInfo(
			int orderId,
			String sampleId,
			String familyId,
			String fatherId,
			String motherId,
			Sex sex,
			Affection affection,
			String category,
			String disease,
			String population,
			int age,
			String filter,
			String poolId,
			int approved,
			int status)
	{
		this.orderId = orderId;
		this.sampleId = sampleId;
		this.familyId = familyId;
		this.fatherId = fatherId;
		this.motherId = motherId;
		this.sex = sex;
		this.affection = affection;
		this.category = category;
		this.disease = disease;
		this.population = population;
		this.age = age;
		this.filter = filter;
		this.poolId = poolId;
		this.approved = approved;
		this.status = status;
	}

	@Override
	public int compareTo(SampleInfo other) {
		return getSampleId().compareTo(other.getSampleId());
	}

	public int getOrderId() {
		return orderId;
	}

	public String getSampleId() {
		return sampleId;
	}

	public String getFamilyId() {
		return familyId;
	}

	public String getFatherId() {
		return fatherId;
	}

	public String getMotherId() {
		return motherId;
	}

	public Sex getSex() {
		return sex;
	}

	/** @deprecated */
	public String getSexStr() {

		if (getSex() != null) {
			return String.valueOf(getSex().ordinal());
		} else {
			return "0";
		}
	}

	public Affection getAffection() {
		return affection;
	}

	/** @deprecated */
	public String getAffectionStr() {

		if (getAffection() != null) {
			return String.valueOf(getAffection().ordinal());
		} else {
			return "0";
		}
	}

	public String getCategory() {
		return category;
	}

	public String getDisease() {
		return disease;
	}

	public String getPopulation() {
		return population;
	}

	public int getAge() {
		return age;
	}

	public String getFilter() {
		return filter;
	}

	public String getPoolId() {
		return poolId;
	}

	public int getApproved() {
		return approved;
	}

	public int getStatus() {
		return status;
	}

	/**
	 * @deprecated
	 * @param fieldName see org.gwaspi.constants.cDBSamples#f_*
	 * @return String, Integer, Boolean or null
	 */
	public Object getField(String fieldName) {

		if (fieldName.equals(cDBSamples.f_ID)) {
			return getOrderId();
		} else if (fieldName.equals(cDBSamples.f_SAMPLE_ID)) {
			return getSampleId();
		} else if (fieldName.equals(cDBSamples.f_FAMILY_ID)) {
			return getFamilyId();
		} else if (fieldName.equals(cDBSamples.f_FATHER_ID)) {
			return getFatherId();
		} else if (fieldName.equals(cDBSamples.f_MOTHER_ID)) {
			return getMotherId();
		} else if (fieldName.equals(cDBSamples.f_SEX)) {
			return getSexStr();
		} else if (fieldName.equals(cDBSamples.f_AFFECTION)) {
			return getAffectionStr();
		} else if (fieldName.equals(cDBSamples.f_CATEGORY)) {
			return getCategory();
		} else if (fieldName.equals(cDBSamples.f_DISEASE)) {
			return getDisease();
		} else if (fieldName.equals(cDBSamples.f_POPULATION)) {
			return getPopulation();
		} else if (fieldName.equals(cDBSamples.f_AGE)) {
			return getAge();
		} else if (fieldName.equals(cDBSamples.f_FILTER)) {
			return getFilter();
		} else if (fieldName.equals(cDBSamples.f_POOL_ID)) {
			return getPoolId();
		} else if (fieldName.equals(cDBSamples.f_APPROVED)) {
			return getApproved();
		} else if (fieldName.equals(cDBSamples.f_STATUS_ID_FK)) {
			return getStatus();
		} else {
			return null;
		}
	}
}
