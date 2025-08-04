package dec.ny.gov.etrack.fmis.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
public @Data class Applicant implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  private Long publicId;
  private String displayName;
  private String firstName;
  private String lastName;
  private String street1;
  private String street2;
  private String city;
  private String state;
  private String country;
  private String zip;
  private String email;
  private String homePhoneNumber;
  private String businessPhoneNumber;
  private String cellPhoneNumber;
}
