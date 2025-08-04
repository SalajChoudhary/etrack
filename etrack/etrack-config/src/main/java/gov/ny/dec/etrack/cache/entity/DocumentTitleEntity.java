package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "E_DOCUMENT_TITLE")
public @Data class DocumentTitleEntity  implements Serializable {
	 private static final long serialVersionUID = 1L;
	  
	  @Id
	  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "e_document_title_s")
	  @SequenceGenerator(name = "e_document_title_s", sequenceName = "e_document_title_s", allocationSize = 1)
	  @Column(name="DOCUMENT_TITLE_ID")
	  private Integer id;
	  @Column(name="DOCUMENT_TITLE")
	  private String description;
	  
	  @Column(name="CREATED_BY_ID")
	  private String createdById;
	  
	  @Column(name="CREATE_DATE")
	  private Date createDate;
	  
	  @Column(name="MODIFIED_BY_ID")
	  private String moifiedById;
	  
	  @Column(name="MODIFIED_DATE")
	  private Date modifiedDate;
	  
}
