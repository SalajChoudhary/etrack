package dec.ny.gov.etrack.gis.service;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import dec.ny.gov.etrack.gis.model.ProjectPolygon;

@Service
public interface GISPolygonService {
  
  /**
   * Retrieve the DEC Polygon by Tax Id, County and Municipality details.
   * 
   * @param taxParcelID - Tax Parcel Id.
   * @param countyName - Country name.
   * @param municipalName - Municipality name.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - DEC Polygon related details.
   */
  String getDECPolygonByTaxId(final String taxParcelID, final String countyName,
      final String municipalName, final String contextId);

  /**
   * Retrieve the DEC Polygon details by the input address.
   * 
   * @param street - Street address detail.
   * @param city - City name.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - DEC Polygon details.
   */
  String getDECPolygonByAddress(final String street, final String city, final String contextId);

  /**
   * Retrieve the DEC Polygon details by the input DEC Id.
   * 
   * @param decId - DEC ID.
   * @param contextId - Unique UUID to track this request.
   * @param jwtToken - JWT Token.
   * 
   * @return - DEC Polygon detail for the input DEC ID.
   */
  String getDECPolygonByDecId(final String decId, final String contextId, final String jwtToken);
  
  /**
   * Save/Amend the Applicant Polygon details.
   * 
   * @param featureMap - Feature details.
   * @param value - pjson value.
   * @param actionInd - Save-S/Update-U.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Updated Polygon after save/amend.
   */
  Object applicantPolygon(final List<Object> featureMap, final String value,
      final String actionInd, final String contextId);

  /**
   * Save Or Update the Analyst Polygon based on the action requested by the user.
   * 
   * @param featureMap - Feature details.
   * @param value - pjson value.
   * @param actionInd - Save(S) or Update(U)
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Save or Update Analyst Polygon details.
   */
  Object analystPolygon(final List<Object> featureMap, final String value,
      final String actionInd, final String contextId);
  
  /**
   * Save or Update the Submitted Polygon requested by the user.
   * 
   * @param featureMap - Feature details.
   * @param value - pjson value.
   * @param actionInd - Save(S) or Update(U)
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Save or Update Submitted Polygon details.
   */
  Object submittedPolygon(final List<Object> featureMap, final String value,
      final String actionInd, final String contextId);
  
  /**
   * Retrieve the Applicant Polygon details for the input application id.
   *  
   * @param applicationId - Application Id.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Applicant Polygon details.
   */
  String getApplicantPolygon(final String applicationId, final String contextId);

  /**
   * Retrieve the Analyst Polygon details for the requested Analyst Id.
   * 
   * @param analystId - Analyst Id.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Analyst Polygon details.
   */
  String getAnalystPolygon(final String analystId, final String contextId);
  
  /**
   * Retrieve the Submitted Polygon details for the requested application submit id.
   *  
   * @param applSubId - Application Submission Id.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Submitted Polygon details.
   */
  String getsubmitedPolygon(final String applSubId, final String contextId);
  
  
  /**
   * Retrieve the DEC ID by the program Id and Type requested by the user.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param jwtToken - JWT Token.
   * @param programId - ProgramId.
   * @param programType - Program Type.
   * 
   * @return - DEC ID.
   */
  Map<String, Object> getDECIdByProgramType(final String userId, final String contextId,
      final String jwtToken, final String programId, final String programType);

  /**
   * Delete the requested Polygon Object id.
   * @param objectIdInput - Object Id.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Deleted Polygon details.
   */
  Object deletePolygonByObjId(final String objectIdInput, final String contextId);
  
  /**
   * Upload the shape file.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param filetype - File Type.
   * @param publishParameters - Published Parameters.
   * @param value - pjson value.
   * @param files - Files to be uploaded.
   * 
   * @return - Uploaded shape file details.
   */
  Object uploadShapefile(final String userId, final String contextId, final String filetype,
      final String publishParameters, final String value, final MultipartFile files);
  
  /**
   * Retrieve the DEC Id by the Tax map.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param jwtToken - JWT Token.
   * @param txmap - Tax Map number.
   * @param county - County.
   * @param municipality - Municipality.
   * 
   * @return - DEC ID.
   */
  ResponseEntity<String> getDECIdByTxmap(final String userId, final String contextId, final String jwtToken,
      final String txmap, final String county, final String municipality);

  /**
   * Delete the Analyst Polygon by Object Id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param objectId - Polygon Object id.
   * 
   * @return - Delete the Polygon Object id.
   */
  Object deleteAnalystPolygonByObjId(String userId, String contextId, String objectId);
  
  /**
   * Delete the Applicant Submittal Polygon associated with the object id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param objectId - Polygon Object id.
   * 
   * @return - Deleted Polygon response.
   */
  Object deleteApplicantSubmittalPolygonByObjId(String userId, String contextId,
      String objectId);
  
  
  /**
   * Save or Update the Work area polygon.
   * 
   * @param featureMap - List of feature map details.
   * @param value - pjson.
   * @param action - Save/Update/Delete action.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Updated Polygon details after the action is performed.
   */
  Object saveOrUpdateWorkAreaPolygon(List<Object> featureMap, String value, String action,
      String contextId);
  
  /**
   * Delete the work area polygon for the requested object id.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param objectId - Polygon object id.
   * 
   * @return - Deleted Work area polygon.
   */
  Object deleteWorkAreaPolygonByObjId(String userId, String contextId, String objectId);
  
  /**
   * Retrieve the Work area polygon for the work area id requested.
   * 
   * @param workareaId - Work area id.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - WorkArea polygon id.
   */
  String getWorkAreaPolygon(String workareaId, String contextId);
  
  /**
   * Upload the Approved Polygon objects into eFind.
   * 
   * @param contextId - Unique UUID to track this request.
   * @param uploadPolygonProjects - Upload Polygon Project details.
   * 
   * @return - Project id and status of the upload map. 
   */
  Map<Long, String> uploadApprovedPolygonToEFind(String contextId,
      List<ProjectPolygon> uploadPolygonProjects);
}
