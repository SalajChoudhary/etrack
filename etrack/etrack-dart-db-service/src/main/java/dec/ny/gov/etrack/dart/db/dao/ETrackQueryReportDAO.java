package dec.ny.gov.etrack.dart.db.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import dec.ny.gov.etrack.dart.db.entity.SubmittedProjectDetail;
import dec.ny.gov.etrack.dart.db.entity.SubmittedProjectRowMapper;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.model.ProjectSubmittedRetrievalCriteria;

@Repository
public class ETrackQueryReportDAO {

  @Autowired
  @Qualifier("submitProjectRetrievalTemplate")
  private JdbcTemplate submitProjectRetrievalTemplate;
  @Autowired
  @Qualifier("namedParameterProjectRetrievalTemplate")
  private NamedParameterJdbcTemplate namedParameterProjectRetrievalTemplate;
  private SimpleDateFormat mmDDYYYFormat = new SimpleDateFormat("MM/dd/yyyy");

  private static final Logger logger = LoggerFactory.getLogger(ETrackQueryReportDAO.class.getName());
  
  /**
   * Build the query dynamically based on the input search criteria and retrieve the matched
   * submitted Projects through different channel like Email, Paper.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param queryCriteria - Search Criteria parameters. Object of
   *        {@link ProjectSubmittedRetrievalCriteria}
   * 
   * @return - Data result list of {@link SubmittedProjectDetail}
   */
  public List<SubmittedProjectDetail> retrieveSubmittedProjectDetails(final String userId,
      final String contextId, final ProjectSubmittedRetrievalCriteria queryCriteria) {

    StringBuilder projectRetrievalQueryBuilder = new StringBuilder();
    final MapSqlParameterSource parameterResource = new MapSqlParameterSource();
    try {
      mmDDYYYFormat.parse(queryCriteria.getStartDate());
      mmDDYYYFormat.parse(queryCriteria.getEndDate());
      parameterResource.addValue("start_date", queryCriteria.getStartDate());
      parameterResource.addValue("end_date", queryCriteria.getEndDate());
    } catch (ParseException e) {
      throw new BadRequestException("DATE_FORMAT_INCORRECT", 
          "Start and/or End date is not in MM/DD/YYYY format", queryCriteria);
    }
    projectRetrievalQueryBuilder.append(
        "select distinct p.mail_in_ind, count(*) total from etrackowner.e_project p, etrackowner.e_facility_polygon fp, "
            + "etrackowner.e_facility_polygon_region fpr   "
            + "where p.project_id=fp.project_id and fp.facility_polygon_id=fpr.facility_polygon_id "
            + "and p.original_submittal_date is not null and p.received_date is not null "
            + "and trunc(received_date) between to_date(:start_date, 'MM/DD/YYYY') and to_date(:end_date, 'MM/DD/YYYY') ");
    
    if (queryCriteria.getRegion() != null) {
      parameterResource.addValue("region_id", queryCriteria.getRegion());
      projectRetrievalQueryBuilder.append("and fpr.dep_region_id=:region_id ");
    }
    if (!CollectionUtils.isEmpty(queryCriteria.getPermitTypes())
        && !CollectionUtils.isEmpty(queryCriteria.getTransTypes())) {
      
      parameterResource.addValue("permit_type", queryCriteria.getPermitTypes());
      parameterResource.addValue("trans_type", queryCriteria.getTransTypes());
      projectRetrievalQueryBuilder
          .append("and p.project_id in (select distinct project_id from etrackowner.e_application "
              + " where permit_type_code in (:permit_type) and trans_type_code in (:trans_type)) ");
    } else if (!CollectionUtils.isEmpty(queryCriteria.getPermitTypes())) {
      parameterResource.addValue("permit_type", queryCriteria.getPermitTypes());
      projectRetrievalQueryBuilder
          .append("and p.project_id in (select distinct project_id from etrackowner.e_application "
              + " where permit_type_code in (:permit_type)) ");
    } else if (!CollectionUtils.isEmpty(queryCriteria.getTransTypes())) {
      projectRetrievalQueryBuilder
          .append("and p.project_id in (select distinct project_id from etrackowner.e_application "
              + " where trans_type_code in (:trans_type)) ");
    }
    projectRetrievalQueryBuilder.append("group by p.mail_in_ind");
    logger.info("Applicant submittal data retrieval query {}", projectRetrievalQueryBuilder.toString());
    return namedParameterProjectRetrievalTemplate.query(projectRetrievalQueryBuilder.toString(),
        parameterResource, new SubmittedProjectRowMapper());
  }
}
