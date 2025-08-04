package dec.ny.gov.etrack.permit.util;

import static dec.ny.gov.etrack.permit.util.Messages.INVALID_REQ;
import static dec.ny.gov.etrack.permit.util.Messages.INVALID_REQ_MSG;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import dec.ny.gov.etrack.permit.exception.BadRequestException;
import dec.ny.gov.etrack.permit.model.Applicant;
import dec.ny.gov.etrack.permit.model.ApplicantAddress;
import dec.ny.gov.etrack.permit.model.Contact;
import dec.ny.gov.etrack.permit.model.FacilityAddress;
import dec.ny.gov.etrack.permit.model.Individual;
import dec.ny.gov.etrack.permit.model.Organization;
import dec.ny.gov.etrack.permit.model.ProjectDetail;

public class Validator {

  private static final String ALPHABETS_ONLY = "^[a-zA-Z]+$";
//  private static final String NUMERIC_ONLY = "^[0-9]+$";
  private static final String EMAIL_PATTERN = "^(.+)@(.+)$";
  private static final String INDIVIDUAL = "I";
  private static final String SOLE_PROPRIETOR = "P";
  private static final String INCORPORATED_BIZ = "X";
  private static final String TRUST_OR_ASSOCIATION = "T";
  private static final String CORPN_PARTNER = "C";
  private static final String FEDERAL_AGENCY = "F";
  private static final String STATE_AGENCY = "S";
  private static final String MUNI_OR_COUNTY = "M";
  private static final Integer US_ADR_TYPE = 0;
  private static final Integer NON_US_ADR_TYPE = 1;


  private static final Logger logger = LoggerFactory.getLogger(Validator.class.getName());

  /**
   * Validate the Applicant informations entered by the user in the portal.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param applicant - Public information.
   * @param category - Public Category.
   * 
   * @return - true or false if input is valid or not.
   */
  public static boolean isValid(final String userId, final String contextId,
      final Applicant applicant, final String category) {
    
    logger.info("Entering into validate Applicant details User Id {}, Context Id {}", userId,
        contextId);
    Pattern pattern = null;
    String publicType = applicant.getPublicTypeCode();

    if (StringUtils.isEmpty(publicType)) {
      throw new BadRequestException("PUBLIC_TYPE_EMPTY", "There is no public type is passed", applicant);
    }

    if (!publicType.equals(SOLE_PROPRIETOR) && StringUtils.hasLength(applicant.getDba())) {
      throw new BadRequestException("DBA_INFO_PASSED", 
          "DBA information should be passed only if the Public is Sole Proprietor", applicant);
    }
    
    if (category.equals("C") || category.equals("O")) {
      if (!CollectionUtils.isEmpty(applicant.getPropertyRelationships())) {
        throw new BadRequestException("PROPERTY_REL_INFO_PASSED", "Owner and/or Contact/Agent won't have Property relationship information",
            applicant.getPropertyRelationships());
      }
    } else {
      List<Integer> propertyRelationship = applicant.getPropertyRelationships();
      if (CollectionUtils.isEmpty(propertyRelationship)) {
        throw new BadRequestException("NO_PROPERTY_REL_AVAIL", "Property relationship detail is blank or not passed", applicant);
      }
      propertyRelationship.forEach(relationship -> {
        if (relationship == null) {
          throw new BadRequestException("NO_PROPERTY_REL_AVAIL", "Property relationship detail is blank or not passed", propertyRelationship);
        }
      });
    }

    if (publicType.equals(INDIVIDUAL) || publicType.equals(SOLE_PROPRIETOR)) {
      Individual individual = applicant.getIndividual();
      if (individual == null) {
        throw new BadRequestException("NO_PUBLIC_INDIVIDUAL_AVAIL", "There is no Individual information is passed", applicant);
      }
      if (StringUtils.isEmpty(individual.getFirstName())) {
        throw new BadRequestException("NO_FNAME_AVAIL", "First name is empty or blank", applicant);
      }
      if (StringUtils.hasLength(individual.getMiddleName())
          && individual.getMiddleName().length() == 1) {
        pattern = Pattern.compile(ALPHABETS_ONLY);
        if (!pattern.matcher(individual.getMiddleName()).matches()) {
          throw new BadRequestException("MNAME_INVALID", "Middle name is invalid", applicant);
        }
      }
      if (StringUtils.isEmpty(individual.getLastName())) {
        throw new BadRequestException("NO_LNAME_AVAIL", "Last name is empty or blank", applicant);
      }
      
    }

    if (publicType.equals(INCORPORATED_BIZ) || publicType.equals(TRUST_OR_ASSOCIATION)
        || publicType.equals(CORPN_PARTNER)) {

      Organization organization = applicant.getOrganization();
      if (organization == null || StringUtils.isEmpty(organization.getBusOrgName())) {
        throw new BadRequestException("NO_ORG_NAME_AVAIL", "Business Organization is passed as empty or blank", applicant);
      }
      
      if (StringUtils.hasLength(organization.getIncorporationState())
          && "NY".equals(organization.getIncorporationState())
          && StringUtils.isEmpty(organization.getBusinessVerified())) {
        throw new BadRequestException("NO_BIZ_VERIFY_AVAIL", "Business verification details is not passed", applicant);
      }

      if (StringUtils.hasLength(organization.getBusinessVerified())
          && !("Y".equals(organization.getBusinessVerified())
              || "N".equals(organization.getBusinessVerified()))) {
        throw new BadRequestException("BIZ_VERIFY_INVALID", "Invalid Business verification details is passed", applicant);
      }

      if (StringUtils.hasLength(organization.getIncorporationState())
          && "Other".equals(organization.getIncorporationState())) {
        if (!StringUtils.hasLength(organization.getIncorporateCountry())) {
          throw new BadRequestException("INCOR_COUNTRY_NA", "Incorporate Country is empty or blank.", applicant);
        }
      }
    }
    if (publicType.equals(FEDERAL_AGENCY) || publicType.equals(MUNI_OR_COUNTY)
        || publicType.equals(STATE_AGENCY)) {

      if (StringUtils.isEmpty(applicant.getGovtAgencyName())) {
        throw new BadRequestException("NO_AGENCY_AVAIL", "There is no agency/municipality or county information is passed", applicant);
      }
    }

    ApplicantAddress address = applicant.getAddress();
    if (address == null || !StringUtils.hasLength(address.getStreetAdr1())) {
      throw new BadRequestException("STR_ADR_1", "Address Street 1 is empty or blank.", applicant);
    }

    Integer addrType = address.getAdrType();
    if (addrType == null) {
      throw new BadRequestException("ADR_TYPE_NA", "Address Type (US or Non US address) is passed.", applicant);
    }

    // US address validation
    if (addrType.equals(US_ADR_TYPE)) {
      if (!StringUtils.hasLength(address.getCity())) {
        throw new BadRequestException("CITY_NO_AVAIL", "City is not passed or blank.", applicant);
      }
      
      if (StringUtils.isEmpty(address.getZipCode())) {
        throw new BadRequestException(INVALID_REQ, INVALID_REQ_MSG, applicant);
      }

      if (address.getZipCode().length() != 5) {
        throw new BadRequestException(INVALID_REQ, INVALID_REQ_MSG, applicant);
      }

    } else if (addrType.equals(NON_US_ADR_TYPE)) {
      // Non US address validation
      if (StringUtils.isEmpty(address.getCity())) {
        throw new BadRequestException(INVALID_REQ, INVALID_REQ_MSG, applicant);
      }

      if (StringUtils.isEmpty(address.getState())) {
        throw new BadRequestException(INVALID_REQ, INVALID_REQ_MSG, applicant);
      }

      if (StringUtils.isEmpty(address.getPostalCode())) {
        throw new BadRequestException(INVALID_REQ, INVALID_REQ_MSG, applicant);
      }
      address.setZipCode(address.getPostalCode());
    } else {
      throw new BadRequestException(INVALID_REQ, INVALID_REQ_MSG, applicant);
    }

    Contact contact = applicant.getContact();
    if (contact == null) {
      throw new BadRequestException(INVALID_REQ, INVALID_REQ_MSG, applicant);
    }
//    if (StringUtils.isEmpty(contact.getCellNumber())
//        && StringUtils.isEmpty(contact.getWorkPhoneNumber())
//        && StringUtils.isEmpty(contact.getHomePhoneNumber())) {
//      throw new BadRequestException(INVALID_REQ, INVALID_REQ_MSG, applicant);
//    }

    isPhoneNumberValid(contact.getCellNumber(), contact.getWorkPhoneNumber(),
        contact.getHomePhoneNumber());

    // if (StringUtils.isEmpty(contact.getEmailAddress())) {
    // throw new BadRequestException(INVALID_REQ, INVALID_REQ_MSG, applicant);
    // }
    pattern = Pattern.compile(EMAIL_PATTERN);
    if (StringUtils.hasLength(contact.getEmailAddress())
        && !pattern.matcher(contact.getEmailAddress()).matches()) {
      throw new BadRequestException(INVALID_REQ, INVALID_REQ_MSG, applicant);
    }
    return true;
  }

  private static boolean isPhoneNumberValid(String... phoneNumbers) {
    try {
      for (String phoneNumber : phoneNumbers) {

        if (!StringUtils.isEmpty(phoneNumber)) {
          Long.parseLong(phoneNumber);
        }
      }
      return true;
    } catch (Exception e) {
      throw new BadRequestException(INVALID_REQ, INVALID_REQ_MSG, phoneNumbers);
    }
  }

  /**
   * Validate the input Project details.
   * 
   * @param project - Project details.
   * 
   * @return - true or false indicator.
   */
  public static boolean isProjectValid(ProjectDetail project) {
    if (project.getMailInInd() == null || project.getMailInInd() == 0) {
      throw new BadRequestException(INVALID_REQ, INVALID_REQ_MSG, project);
    }
    if (project.getApplicantTypeCode() == null || project.getApplicantTypeCode() == 0) {
      throw new BadRequestException(INVALID_REQ, INVALID_REQ_MSG, project);
    }
    isValidString(project.getPolygonId());
    isValidString(project.getLocDirections());
    if (project.getFacility() == null)
      throw new BadRequestException(INVALID_REQ, INVALID_REQ_MSG, project);
    if (project.getFacility().getAddress() == null)
      throw new BadRequestException(INVALID_REQ, INVALID_REQ_MSG, project);
    FacilityAddress facilityAddress = project.getFacility().getAddress();
    isValidString(facilityAddress.getCity());
    isValidString(facilityAddress.getState());
    isValidString(facilityAddress.getZip());
    isValidString(facilityAddress.getZipExtension());
    return true;
  }

  private static void isValidString(String input) {
    if (StringUtils.isEmpty(input))
      throw new BadRequestException(INVALID_REQ, INVALID_REQ_MSG, input);
  }
}
