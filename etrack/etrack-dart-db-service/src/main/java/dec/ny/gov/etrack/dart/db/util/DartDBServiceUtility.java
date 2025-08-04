package dec.ny.gov.etrack.dart.db.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import dec.ny.gov.etrack.dart.db.entity.ApplicantDto;
import dec.ny.gov.etrack.dart.db.entity.County;
import dec.ny.gov.etrack.dart.db.entity.Municipality;
import dec.ny.gov.etrack.dart.db.model.DashboardDetail;
import dec.ny.gov.etrack.dart.db.repo.ApplicantRepo;
import dec.ny.gov.etrack.dart.db.repo.CountyRepo;
import dec.ny.gov.etrack.dart.db.repo.MunicipalityRepo;


@Component
public class DartDBServiceUtility {
  
  @Autowired
  private MunicipalityRepo municipalityRepo;
  
  @Autowired
  private CountyRepo countyRepo;
  
  @Autowired
  private ApplicantRepo applicantRepo;
  
  private static final Logger logger = LoggerFactory.getLogger(DartDBServiceUtility.class.getName());
  
  /**
   * Amend the municipality details for the input project/applications
   * 
   * @param dashboardDetails - List of applications which will be displayed in the Dashbboard.
   * 
   * @return - Municipality Amended applications.
   */
  public List<DashboardDetail> amendMunicipalityDetails(List<DashboardDetail> dashboardDetails) {
    if (!CollectionUtils.isEmpty(dashboardDetails)) {
      Map<Long, DashboardDetail> dashboardApps = new HashMap<>();
      dashboardDetails.forEach(dashboardApplication -> {
        dashboardApps.put(dashboardApplication.getProjectId(), dashboardApplication);
      });

      List<Municipality> municipalities =
          municipalityRepo.findMunicipalitiesForProjectIds(dashboardApps.keySet());
      Map<Long, Set<String>> municipalitiesMap = new HashMap<>();
      municipalities.forEach(municipality -> {

        if (municipalitiesMap.get(municipality.getProjectId()) != null) {
          municipalitiesMap.get(municipality.getProjectId())
              .add(municipality.getMunicipalityName());
        } else {
          Set<String> municipalityDetails = new HashSet<String>();
          municipalityDetails.add(municipality.getMunicipalityName());
          municipalitiesMap.put(municipality.getProjectId(), municipalityDetails);
        }
      });
      dashboardApps.keySet().forEach(projectId -> {
        if (municipalitiesMap.get(projectId) != null) {
          dashboardApps.get(projectId).getFacility()
              .setMunicipality(String.join(",", municipalitiesMap.get(projectId)));
        }
      });
      return new ArrayList<>(dashboardApps.values());
    } else {
      return dashboardDetails;
    }
  }

  /**
   * Amend the County details for the input project/applications
   * 
   * @param dashboardDetails - List of applications which will be displayed in the Dashbboard.
   * 
   * @return - County Amended applications.
   */
  public List<DashboardDetail> amendCountyDetails(List<DashboardDetail> dashboardDetails) {

    if (!CollectionUtils.isEmpty(dashboardDetails)) {
      Map<Long, DashboardDetail> pendingSubmissionsMap = new HashMap<>();
      dashboardDetails.forEach(pendingSubmissionApp -> {
        pendingSubmissionsMap.put(pendingSubmissionApp.getProjectId(), pendingSubmissionApp);
      });

      List<County> counties = countyRepo.findCountiesForProjectIds(pendingSubmissionsMap.keySet());
      Map<Long, Set<String>> countiesMap = new HashMap<>();
      counties.forEach(county -> {

        if (countiesMap.get(county.getProjectId()) != null) {
          countiesMap.get(county.getProjectId()).add(county.getCounty());
        } else {
          Set<String> countyDetails = new HashSet<String>();
          countyDetails.add(county.getCounty());
          countiesMap.put(county.getProjectId(), countyDetails);
        }
      });
      pendingSubmissionsMap.keySet().forEach(projectId -> {
        if (countiesMap.get(projectId) != null) {
          pendingSubmissionsMap.get(projectId).getFacility()
              .setCounty(String.join(",", countiesMap.get(projectId)));
        }
      });
      return new ArrayList<>(pendingSubmissionsMap.values());
    } else {
      return dashboardDetails;
    }
  }

  /**
   * Transform the public name into Display name format.
   * 
   * @param lastName - Last name.
   * @param firstName - First name.
   * @param middle - Middle or Initial.
   * 
   * @return - formatted Display name.
   */
  public String preparePublicNameFormat(String lastName, String firstName, String middle) {
    StringBuilder publicNameFormat = new StringBuilder();

    if (StringUtils.hasLength(lastName)) {
      publicNameFormat.append(lastName);
    }
    if (StringUtils.hasLength(firstName)) {
      publicNameFormat.append(", ").append(firstName);
    }
    if (StringUtils.hasLength(middle)) {
      publicNameFormat.append(", ").append(middle);
    }
    return publicNameFormat.toString();
  }

  /**
   * Transform the public name into Display name format.
   * 
   * @param lastName - Last name.
   * @param firstName - First name.
   * @param middle - Middle or Initial.
   * 
   * @return - formatted Display name.
   */
  public String preparePublicNameINSearchResultFormat(String lastName, String firstName, String middle) {
    StringBuilder publicNameFormat = new StringBuilder();
    if (StringUtils.hasLength(lastName)) {
      publicNameFormat.append(lastName);
    }
    if (StringUtils.hasLength(firstName)) {
      publicNameFormat.append(", ").append(firstName);
    }
    if (StringUtils.hasLength(middle)) {
      publicNameFormat.append(" ").append(middle);
    }
    return publicNameFormat.toString();
  }
  
  /**
   * Retrieve the Legal Response Party details.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param regionalProjectInd - Indicates whether the request is for region or user.
   * 
   * @return - Project and LRP mapping.
   */
  public Map<Long, ApplicantDto> getLegalResponsePartyDetails(final String userId, final String contextId, boolean regionalProjectInd) {
    logger.info("Entering into collect the Legal Response Party details. User Id {}, Context Id {}", userId, contextId);
    List<ApplicantDto> lrpsList = null;
    if (regionalProjectInd) {
      lrpsList = applicantRepo.findLRPDetailsByCreateById(null);
    } else {
      lrpsList = applicantRepo.findLRPDetailsByCreateById(userId);
    }
    logger.info("Collected the the Legal Response Party details. User Id {}, Context Id {}", userId, contextId);
    Map<Long, ApplicantDto> lrpsMap = new HashMap<>();
    if (!CollectionUtils.isEmpty(lrpsList)) {
      for (ApplicantDto applicant : lrpsList) {
        ApplicantDto applicantDetail = new ApplicantDto();
        if (StringUtils.hasLength(applicant.getFirstName())) {
          applicantDetail.setDisplayName(preparePublicNameFormat(applicant.getLastName(),
              applicant.getFirstName(), applicant.getMiddleName()));
        } else {
          applicantDetail.setDisplayName(applicant.getDisplayName());
        }
        applicantDetail.setPublicId(applicant.getPublicId());
        lrpsMap.put(applicant.getProjectId(), applicantDetail);
      }
    }
    logger.info("Exiting from collect the Legal Response Party details. User Id {}, Context Id {}", userId, contextId);
    return lrpsMap;
  }
  
  /**
   * This method will check whether if input value is empty/blank. 
   * 
   * @param inputs - Array of String
   * 
   * @return - Returns false if any one of the String is empty. else returns True.
   */
  public static boolean isValidStrings(String... inputs) {
    for (String input : inputs) {
      if (!StringUtils.hasLength(input)) {
        return false;
      }
    }
    return true;
  }
}
