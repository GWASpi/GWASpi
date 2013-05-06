package org.gwaspi.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.gwaspi.constants.cNetCDF.Defaults.OPType;

/**
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 */
@Entity
@Table(name = "operationX")
@NamedQueries({
	@NamedQuery(
		name = "operation_fetchById",
		query = "SELECT o FROM Operation o WHERE o.id = :id"),
	@NamedQuery(
		name = "operation_listByParentMatrixId",
		query = "SELECT o FROM Operation o WHERE o.parentMatrixId = :parentMatrixId"),
	@NamedQuery(
		name = "operation_listByParentMatrixIdParentOperationId",
		query = "SELECT o FROM Operation o WHERE o.parentMatrixId = :parentMatrixId AND o.parentOperationId = :parentOperationId"),
	@NamedQuery(
		name = "operation_listByParentMatrixIdParentOperationIdOperationType",
		query = "SELECT o FROM Operation o WHERE o.parentMatrixId = :parentMatrixId AND o.parentOperationId = :parentOperationId AND o.operationType = :operationType"),
})
public class Operation implements Serializable {

	private int id; // INTEGER generated by default as identity
	private String friendlyName; // VARCHAR(255) NOT NULL
	private String netCDFName; // VARCHAR(255) NOT NULL
	private OPType type; // VARCHAR(32) NOT NULL
	private int parentMatrixId; // INTEGER
	private int parentOperationId; // INTEGER
	private String command; // VARCHAR(2000)
	private String description; // VARCHAR(255)
	private int studyId; // INTEGER

	protected Operation() {

		this.id = Integer.MIN_VALUE;
		this.friendlyName = "";
		this.netCDFName = "";
		this.type = null;
		this.parentMatrixId = Integer.MIN_VALUE;
		this.parentOperationId = Integer.MIN_VALUE;
		this.command = "";
		this.description = "";
		this.studyId = Integer.MIN_VALUE;
	}

	public Operation(
			int id,
			String friendlyName,
			String netCDFName,
			OPType type,
			int parentMatrixId,
			int parentOperationId,
			String command,
			String description,
			int studyId)
	{
		this.id = id;
		this.friendlyName = friendlyName;
		this.netCDFName = netCDFName;
		this.type = type;
		this.parentMatrixId = parentMatrixId;
		this.parentOperationId = parentOperationId;
		this.command = command;
		this.description = description;
		this.studyId = studyId;
	}

	@Id
	@GeneratedValue
	@Column(
		name       = "id",
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public int getId() {
		return id;
	}

	protected void setId(int id) {
		this.id = id;
	}

	@Id
	@Column(
		name       = "parentMatrixId",
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public int getParentMatrixId() {
		return parentMatrixId;
	}

	protected void setParentMatrixId(int parentMatrixId) {
		this.parentMatrixId = parentMatrixId;
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
		return studyId;
	}

	protected void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	@Column(
		name       = "friendlyName",
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public String getFriendlyName() {
		return friendlyName;
	}

	@Column(
		name       = "netCDFName",
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public String getNetCDFName() {
		return netCDFName;
	}

	@Column(
		name       = "type",
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public OPType getOperationType() {
		return type;
	}

	@Column(
		name       = "command",
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public String getCommand() {
		return command;
	}

	@Column(
		name       = "description",
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public String getDescription() {
		return description;
	}

	@Column(
		name       = "parentOperationId",
		unique     = false,
		nullable   = false,
		insertable = true,
		updatable  = false
		)
	public int getParentOperationId() {
		return parentOperationId;
	}
}
