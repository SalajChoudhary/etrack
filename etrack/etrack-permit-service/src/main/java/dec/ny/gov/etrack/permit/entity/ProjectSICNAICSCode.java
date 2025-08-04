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

@Data
@Entity
@Table(name="E_PROJECT_SIC_NAICS")
public class ProjectSICNAICSCode implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_PROJECT_SIC_NAICS_S")
  @SequenceGenerator(name = "E_PROJECT_SIC_NAICS_S", sequenceName = "E_PROJECT_SIC_NAICS_S", allocationSize = 1)
  private Long projectSicNaicsId;
  private Long projectId;
//  private Long sicNaicsId;
  private Date createDate;
  private String createdById;
  private Date modifiedDate;
  private String modifiedById;
  private String sicCode;
  private String naicsCode;
}
