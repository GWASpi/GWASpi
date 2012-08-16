package org.gwaspi.constants;

/**
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 */
public class cDBOperations {

	public static String f_ID = "id";
	public static String f_OP_NAME = "operation_name";
	public static String f_OP_NETCDF_NAME = "netcdf_name";
	public static String f_OP_TYPE = "operation_type";
	public static String f_OP_COMMAND = "operation_command";
	public static String f_PARENT_MATRIXID = "parent_matrix_id";
	public static String f_PARENT_OPID = "parent_operation_id";
	public static String f_DESCRIPTION = "description";
	public static String f_STUDYID = "studyid";
	public static String f_CREATION_DATE = "creation_date";
	public static String T_OPERATIONS = "OPERATIONS";
	public static String[] T_CREATE_OPERATIONS = new String[]{f_ID + " INTEGER generated by default as identity",
		f_PARENT_MATRIXID + " INTEGER",
		f_PARENT_OPID + " INTEGER",
		f_OP_NAME + " VARCHAR(255) NOT NULL",
		f_OP_NETCDF_NAME + " VARCHAR(255) NOT NULL",
		f_OP_TYPE + " VARCHAR(32)",
		f_OP_COMMAND + " VARCHAR(255)",
		f_DESCRIPTION + " VARCHAR(2000)",
		f_STUDYID + " INTEGER",
		f_CREATION_DATE + " TIMESTAMP default CURRENT_TIMESTAMP"
	};
	public static final String[] F_INSERT_OPERATION = new String[]{f_PARENT_MATRIXID,
		f_PARENT_OPID,
		f_OP_NAME,
		f_OP_NETCDF_NAME,
		f_OP_TYPE,
		f_OP_COMMAND,
		f_DESCRIPTION,
		f_STUDYID
	};
}
