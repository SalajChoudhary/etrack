package dec.ny.gov.etrack.permit.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class ActionNoteEntity implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private Long projectNoteId;
  private Long projectId;
  private Integer actionTypeCode;
  private Date actionDate;
  private String actionNote;
  private String comments;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
  private String actionTypeDesc;
  private String cancelUserId;
  private String cancelUserNm;
}
