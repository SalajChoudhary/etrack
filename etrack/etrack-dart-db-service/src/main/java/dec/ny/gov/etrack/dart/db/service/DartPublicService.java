package dec.ny.gov.etrack.dart.db.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import dec.ny.gov.etrack.dart.db.model.PublicType;
import dec.ny.gov.etrack.dart.db.model.SearchPatternEnum;

@Service
public interface DartPublicService {

  /**
   * Retrieve the Applicant details for the input project id.
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param categoryCode - Category code.
   * @param projectId - Project Id.
   * @param assignedProject - Associated indicator. valid values are 0, 1.
   * 
   * @return - Applicant details.
   */
  ResponseEntity<Object> retrieveApplicants(final String userId, final String contextId,
      final String categoryCode, final Long projectId, final Integer assignedProject);

  /**
   * Retrieve all the Publics associated with this project id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * 
   * @return - Retrieve the Public Summary details.
   */
  ResponseEntity<Object> retrieveAllPublicsAssociatedWithThisProject(final String userId,
      final String contextId, final Long projectId);

  /**
   * Returns all the Matched applicants for the search parameters passed as an input.
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param publicType - Public Type.
   * @param firstName - First name search text.
   * @param fType - Search Type. S - starts with , E - Exact and C - Contains.
   * @param lastName - Last name search text.
   * @param lType - Search Type. S - starts with , E - Exact and C - Contains.
   * 
   * @return - Returns all the matched applicants if any.
   */
  ResponseEntity<Object> getAllMatchedApplicants(final String userId, final String contextId,
      final PublicType publicType, final String firstName, final SearchPatternEnum fType, final String lastName,
      final SearchPatternEnum lType);

  /**
   * Retrieve the applicant details for the input Public Id.
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param publicId - Public Id.
   * @param aplctType - Applicant Type. P - Public, O - Owner and C- Contact/Agent.
   *  
   * @return - Applicant/Public information for the input public id.
   */
  ResponseEntity<Object> getApplicantInfo(final String userId, final String contextId,
      final Long projectId, final Long publicId, final String aplctType);
  
  /**
   * Retrieve the Public details from Enterprise system for the input Enterprise Public Id.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param edbPublicId - Enterprise Public Id.
   * @param aplctType - Applicant type. I-Individual, S - Sole Proprietor.
   * 
   * @return - Return the Enterprise applicant information detail.
   */
  ResponseEntity<Object> getEdbApplicantInfo(final String userId, final String contextId,
      final Long projectId, final Long edbPublicId, final String aplctType);

  /**
   * Retrieve the existing data from DART which gets loaded into eTrack E_PUBLIC_H table CHANGE_COUNTER=0. 
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param applicantId - Applicant/public Id.
   * 
   * @return - History of the Applicant.
   */
  ResponseEntity<Object> retrieveApplicantHistory(final String userId, final String contextId,
      Long projectId, Long applicantId);

  /**
   * Validate whether the requested Dart Public Id is available in DART or not.
   * 
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param publicId - eTrack Public Id.
   * @param edbPublicId - Enterprise/Dart Public id.
   * 
   * @return - Status of the Enterprise Public id.
   */
  Object validateEdbPublicId(String userId, String contextId, Long projectId, Long publicId,
      Long edbPublicId);
  
  /**
   * Retrieve the applicant summary details to display in the Step 2 sub step 1, 2 and 3.
   * @param userId - User Id who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * @param assignedProject - Assigned project Indicator. 0 - not associated, 1 - associated to facility
   * 
   * @return - Applicant Summary with appropriate HttpStatus {@link ResponseEntity}
   */
  ResponseEntity<Object> getApplicantsSummary(final String userId, final String contextId,
      final Long projectId, final Integer assignedProject);

//  /**
//   * Returns the list of Property Owners associated with this project.
//   * 
//   * @param userId - User Id who initiates this request.
//   * @param contextId - Unique UUID to track this request.
//   * @param projectId - Project Id.
//   * 
//   * @return - Property Owners summary.
//   */
//  Map<String, Object> getPropertyOwnerSummary(final String userId, final String contextId,
//      final Long projectId);
//
//  /**
//   * Returns the Contact summary associated with the project Id.
//   * 
//   * @param userId - User Id who initiates this request.
//   * @param contextId - Unique UUID to track this request.
//   * @param projectId - Project Id.
//   * @param associatedInd - 0 - Not Associated with the Project. 1 - Associated with the project.
//   * 
//   * @return - Contact/agent summary details.
//   */
//  ResponseEntity<Object> getContactsSummary(final String userId, final String contextId,
//      final Long projectId, final Integer associatedInd);
}
