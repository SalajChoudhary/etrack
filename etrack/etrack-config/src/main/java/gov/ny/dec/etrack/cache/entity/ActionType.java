package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_PROJECT_NOTE_ACTION_TYPE")
public @Data class ActionType implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  private Integer actionTypeCode;
  private String actionTypeDesc;
  private Integer displayOrder;
  private Integer systemNoteInd; 
}
