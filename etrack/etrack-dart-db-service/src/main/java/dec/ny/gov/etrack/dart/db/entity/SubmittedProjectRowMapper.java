package dec.ny.gov.etrack.dart.db.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SubmittedProjectRowMapper implements RowMapper<SubmittedProjectDetail>{

  @Override
  public SubmittedProjectDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
    
    final SubmittedProjectDetail submittedProjectDetail = new SubmittedProjectDetail();
    submittedProjectDetail.setMailInInd(rs.getInt("mail_in_ind"));
    submittedProjectDetail.setTotal(rs.getBigDecimal("total"));
    return submittedProjectDetail;
  }
}
