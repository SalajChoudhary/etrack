package dec.ny.gov.etrack.gis.util;

import static dec.ny.gov.etrack.gis.util.Messages.INVALID_REQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import dec.ny.gov.etrack.gis.exception.BadRequestException;
import dec.ny.gov.etrack.gis.model.FacilityAddress;
import dec.ny.gov.etrack.gis.model.ProjectDetail;

public class Validator {

  private static final Logger LOGGER = LoggerFactory.getLogger(Validator.class.getName());
  
  private Validator() {
    
  }
 
  /**
   * Validate the Project details passed as an input.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param project - Project facility details.
   * 
   * @return - Boolean indicator whether the project facility details are valid or not.
   */
  public static boolean isProjectValid(final String userId, final String contextId, ProjectDetail project) {
    LOGGER.info("Entering into validate "
        + "the project input request User Id :{}, Context Id :{}", userId, contextId);
    
    if (project.getMailInInd() == null) {
      throw new BadRequestException(INVALID_REQ, "Mail Indicator is not passed", project);
    }
    if (project.getApplicantTypeCode() == null || project.getApplicantTypeCode() == 0) {
      throw new BadRequestException(INVALID_REQ, "Applicant Type is not passed", project);
    }
    if (project.getFacility() == null)
      throw new BadRequestException(INVALID_REQ, "Facility details are not passed", project);
    if (project.getFacility().getAddress() == null)
      throw new BadRequestException(INVALID_REQ, "Facility Address details are not passed", project);
    FacilityAddress facilityAddress = project.getFacility().getAddress();
    isValidString(facilityAddress.getCity(), facilityAddress.getState(), project.getPolygonId());
    LOGGER.info("Exiting from validate "
        + "the project input request User Id :{}, Context Id :{}", userId, contextId);
    return true;
  }

  private static void isValidString(String... input) {
    if (StringUtils.isEmpty(input))
      throw new BadRequestException(INVALID_REQ, "One of the field City, State or Polygon id is empty or blank", input);
  }
}
