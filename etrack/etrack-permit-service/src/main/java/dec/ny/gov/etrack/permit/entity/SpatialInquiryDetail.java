package dec.ny.gov.etrack.permit.entity;

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
@Table(name="E_SPATIAL_INQ_DETAIL")
public @Data class SpatialInquiryDetail implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_SPATIAL_INQ_DETAIL_S")
  @SequenceGenerator(name = "E_SPATIAL_INQ_DETAIL_S", sequenceName = "E_SPATIAL_INQ_DETAIL_S", allocationSize = 1)
  public Long inquiryId;
  public String polygonId;
  public Integer spatialInqCategoryId;
  public String region;
//  public String blockLot;
  public String requestorName;
  public String streetAddress;
  public String mailingAddress;
  public String phoneNumber;
  public String projectName;
  public String projectDescription;
  public String projectSponsor;
  public String issuesQuestions;
//  public String tagsKeywords;
  public String leadAgencyName;
  public String leadAgencyContact;
  public String efcContact;
  public String planDescription;
  public String extenderName;
  public String dowContact;
  public String developer;
  public String owner;
  public String pscDocketNum;
  public String depProjectManager;
//  public String personReporting;
  public String comments;
  public String email;
//  public String violationDesc;
//  public String allegedViolator;
//  public String jurisdictions;
  public String taxParcel;
  public String county;
  public String municipality;
  private String searchBy;
  private String state;
  private String zip;
  private String city;
  private String street;
  private String borough;
  private String block;
  private String lot;
  @Column(name="MAILING_ADDRESS_STREET_1")
  private String mailingAddressStreet1;
  @Column(name="MAILING_ADDRESS_STREET_2")
  private String mailingAddressStreet2;
  private String mailingAddressZip;
  private String mailingAddressState;
  private String mailingAddressCity;
  private String planName;
  private String createdById;
  private Date createDate;
  private Date rcvdDate;
  private String searchByMunicipality;
  private String searchByCounty;
  private String searchByTaxParcel;
  private String response;
  private Date responseDate;
  private Integer originalSubmittalInd;
  private Date originalSubmittalDate;
  private String analystAssignedId;
  private String assignedAnalystName;
  private Date analystAssignedDate;
  private String modifiedById;
  private Date modifiedDate;
}
