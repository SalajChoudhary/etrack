package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;

@Entity
@Table(name = "E_DOCUMENT_SUB_TYPE")
public @Data class DocumentSubTypeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "e_document_sub_type_s")
	@SequenceGenerator(name = "e_document_sub_type_s", sequenceName = "e_document_sub_type_s", allocationSize = 1)
	@Column(name = "DOCUMENT_SUB_TYPE_ID")
	private Integer id;
	@Column(name = "DOCUMENT_SUB_TYPE_DESC")
	private String description;
	@Column(name = "DOCUMENT_TYPE_ID")
	private Integer documentTypeId;
	@Column(name = "ACTIVE_IND")
	private Integer activeInd;
	@Column(name = "AVAIL_TO_DEP_ONLY_IND")
	private String availToDepInd;

	@Column(name = "INACTIVATED_DATE")
	private Date inactivatedDate;

	@Column(name = "CREATED_BY_ID")
	private String createdById;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "MODIFIED_BY_ID")
	private String moifiedById;

	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@Transient
	private String documentTypeName;

	@ManyToOne
	@JoinColumn(name = "DOCUMENT_TYPE_ID", insertable = false, updatable = false)
	private DocumentTypeEntity documentType;
}
