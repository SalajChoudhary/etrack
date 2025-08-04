package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "E_DOCUMENT_CLASS")
public @Data class DocumentClassEntity  implements Serializable {
	 private static final long serialVersionUID = 1L;
	  
	  @Id
	  @Column(name="DOCUMENT_CLASS_ID")
	  private String id;
	  @Column(name="DOCUMENT_CLASS_NM")
	  private String description;
	  @Column(name="ACTIVE_IND")
	  private String activeInd;
}
