package org.gwaspi.constants;

public class cDBSamples {

	// * ALL SAMPLES INFO *
	public static final int STATUS_DUMMY = 11;
	public static final String f_ID = "order_id";
	public static final String f_SAMPLE_ID = "sample_id";
	public static final String f_FAMILY_ID = "family_id";
	public static final String f_FATHER_ID = "father_id";
	public static final String f_MOTHER_ID = "mother_id";
	public static final String f_SEX = "sex";
	public static final String f_AFFECTION = "affection";
	public static final String f_CATEGORY = "category";
	public static final String f_DISEASE = "disease";
	public static final String f_POPULATION = "population";
	public static final String f_AGE = "age";
	public static final String f_FILTER = "filter";
	public static final String f_POOL_ID = "pool_id";
	public static final String f_APPROVED = "approved";
	public static final String f_STATUS_ID_FK = "status_id_fk";
	public static final String[] f_PHENOTYPES_COLUMNS = new String[] {
		f_AFFECTION,
		f_AGE,
		f_CATEGORY,
		f_DISEASE,
		f_FAMILY_ID,
		f_FATHER_ID,
		f_MOTHER_ID,
		f_POPULATION,
		f_SEX
	};
	public static final String T_SAMPLES_INFO = "SAMPLES_INFO";
	public static final String[] T_CREATE_SAMPLES_INFO = new String[] {
		f_ID + " INTEGER generated by default as identity",
		f_SAMPLE_ID + " VARCHAR(64) NOT NULL",
		f_FAMILY_ID + " VARCHAR(32) DEFAULT '0'",
		f_FATHER_ID + " VARCHAR(64) DEFAULT '0'",
		f_MOTHER_ID + " VARCHAR(64) DEFAULT '0'",
		f_SEX + " CHAR(1)", // 0 Undefined, 1 Male, 2 Female
		f_AFFECTION + " CHAR(1)", // 0 Undefined, 1 Control, 2 Case
		f_CATEGORY + " VARCHAR(32)",
		f_DISEASE + " VARCHAR(64)",
		f_POPULATION + " VARCHAR(32)",
		f_AGE + " INTEGER",
		f_FILTER + " VARCHAR(60)",
		f_POOL_ID + " VARCHAR(32) NOT NULL",
		f_APPROVED + " INTEGER",
		f_STATUS_ID_FK + " INTEGER"
	};
	public static final String[] F_INSERT_SAMPLES_ALLINFO = new String[] {
		f_SAMPLE_ID,
		f_FAMILY_ID,
		f_FATHER_ID,
		f_MOTHER_ID,
		f_SEX, // 0 Undefined, 1 Male, 2 Female
		f_AFFECTION, // 0 Undefined, 1 Control, 2 Case
		f_CATEGORY,
		f_DISEASE,
		f_POPULATION,
		f_AGE,
		f_POOL_ID
	};
	public static final String[] F_INSERT_DUMMY_SAMPLES_INFO = new String[] {
		f_SAMPLE_ID,
		f_POOL_ID
	};
	public static final String[] F_UPDATE_SAMPLES_ALLINFO = new String[] {
		f_FATHER_ID,
		f_MOTHER_ID,
		f_SEX, // 0 Undefined, 1 Male, 2 Female
		f_AFFECTION, // 0 Undefined, 1 Control, 2 Case
		f_CATEGORY,
		f_DISEASE,
		f_POPULATION,
		f_AGE,
		f_POOL_ID,
		f_STATUS_ID_FK
	};

	private cDBSamples() {
	}
}
