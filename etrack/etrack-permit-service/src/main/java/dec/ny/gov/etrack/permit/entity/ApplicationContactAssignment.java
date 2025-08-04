package dec.ny.gov.etrack.permit.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name="E_APPLN_CONTACT_ASSIGN")
public @Data class ApplicationContactAssignment implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_APPLN_CONTACT_ASSIGN_S")
  @SequenceGenerator(name = "E_APPLN_CONTACT_ASSIGN_S", sequenceName = "E_APPLN_CONTACT_ASSIGN_S", allocationSize = 1)
  private Long applnContactAssignId;
  private Long applicationId;
  private Long roleId;
  private Long permitFormId;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
}
