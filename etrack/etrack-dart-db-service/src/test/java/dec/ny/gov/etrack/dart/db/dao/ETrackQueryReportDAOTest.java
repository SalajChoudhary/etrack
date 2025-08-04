package dec.ny.gov.etrack.dart.db.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.model.ProjectSubmittedRetrievalCriteria;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(MockitoExtension.class)
class ETrackQueryReportDAOTest {

	@Mock
	@Qualifier("submitProjectRetrievalTemplate")
	private JdbcTemplate submitProjectRetrievalTemplate;
	
	@Mock
	@Qualifier("namedParameterProjectRetrievalTemplate")
	private NamedParameterJdbcTemplate namedParameterProjectRetrievalTemplate;
	
	@InjectMocks
	private ETrackQueryReportDAO eTrackQueryReportDAO;
	
	String userId = "1234";
	String contextId = UUID.randomUUID().toString();
	
	@Test
	void retrieveSubmittedProjectDetailsTest() {
		ProjectSubmittedRetrievalCriteria retrievalCriteria = new ProjectSubmittedRetrievalCriteria();
		retrievalCriteria.setStartDate("02/02/2024");
		retrievalCriteria.setEndDate("02/02/2024");
		retrievalCriteria.setRegion(1);
		retrievalCriteria.setPermitTypes(Arrays.asList("permit"));
		retrievalCriteria.setTransTypes(Arrays.asList("trans"));
		assertNotNull(eTrackQueryReportDAO.retrieveSubmittedProjectDetails(userId, contextId, retrievalCriteria));
	}
	
	@Test
	void retrieveSubmittedProjectDetailsEmptyTransTypesTest() {
		ProjectSubmittedRetrievalCriteria retrievalCriteria = new ProjectSubmittedRetrievalCriteria();
		retrievalCriteria.setStartDate("02/02/2024");
		retrievalCriteria.setEndDate("02/02/2024");
		retrievalCriteria.setPermitTypes(Arrays.asList("permit"));
		assertNotNull(eTrackQueryReportDAO.retrieveSubmittedProjectDetails(userId, contextId, retrievalCriteria));
	}
	
	@Test
	void retrieveSubmittedProjectDetailsEmptyPermitTypesTest() {
		ProjectSubmittedRetrievalCriteria retrievalCriteria = new ProjectSubmittedRetrievalCriteria();
		retrievalCriteria.setStartDate("02/02/2024");
		retrievalCriteria.setEndDate("02/02/2024");
		retrievalCriteria.setRegion(1);
		retrievalCriteria.setTransTypes(Arrays.asList("trans"));
		assertNotNull(eTrackQueryReportDAO.retrieveSubmittedProjectDetails(userId, contextId, retrievalCriteria));
	}
	
	@Test
	void retrieveSubmittedProjectDetailsBadRequestExceptionTest() {
		ProjectSubmittedRetrievalCriteria retrievalCriteria = new ProjectSubmittedRetrievalCriteria();
		retrievalCriteria.setStartDate("02/0-/24");
		retrievalCriteria.setEndDate("02/02/2024");
		assertThrows(BadRequestException.class, ()-> eTrackQueryReportDAO.retrieveSubmittedProjectDetails(userId, contextId, retrievalCriteria));
	}
	
}
