package dec.ny.gov.etrack.permit.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_FACILITY_ADDRESS")
public @Data class FacilityAddr implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private Long projectId;
//  private String locationDirections;
  private String city;
  private String state;  
  private String country;
  private String zip;
  private String zipExtension;
  private Date createDate;
  private String createdById;
  private String modifiedById;
  private Date modifiedDate;
//  private Long edbAddressId;
  private Integer changeCounter;
  private String phoneNumber;
  @Column(name = "STREET1")
  private String street1;
  @Column(name = "STREET2")
  private String street2;
 }
