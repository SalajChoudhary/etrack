package dec.ny.gov.etrack.permit.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

public @Data class SpatialInquiryRequest implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  public Long inquiryId;
  public String polygonId;
  public SpatialInquiryCategory reason;
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
  private String mailingAddressStreet1;
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
}
