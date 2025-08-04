package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public @Data class SearchResult implements Serializable {
	  private static final long serialVersionUID = 1L;
	  
	  @Id
	  private Long projectId;
	  private Long districtId;
	  private String standardCode;
	  private String districtName;
	  private String decId;
	  private String facAddress;
	  private Long primaryLrpPublicId;
	  private String primaryLrpName;
	  private String primaryMuni;
	  private Date rcvdDate;
	  private Long documentId;
	  private Long documentTypeId;
	  private Long documentSubTypeId;
	  private String documentNm;
	  private String documentTypeDesc;
	  private String documentSubTypeDesc; 
	  private String permitTypeTxnType;
	  
}
