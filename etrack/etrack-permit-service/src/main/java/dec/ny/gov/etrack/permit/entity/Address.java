package dec.ny.gov.etrack.permit.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_ADDRESS")
public @Data class Address {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_ADDRESS_S")
  @SequenceGenerator(name = "E_ADDRESS_S", sequenceName = "E_ADDRESS_S", allocationSize = 1)
  private Long addressId;
  private Integer foreignAddressInd;
  private String street1;
  private String street2;
  private String city;
  private String state;
  private String country;
  private String zip;
  private String zipExtension;
  private String attentionName;
  private String homePhoneNumber;
  private String businessPhoneNumber;
  private String businessPhoneExt;
  private String faxPhoneNumber;
  private String cellPhoneNumber;
  private String emailAddress;
  private Integer changeCounter;
  private Long edbAddressId;
  private String createdById;
  private Date createDate;
  private String modifiedById;
  private Date modifiedDate;
}
