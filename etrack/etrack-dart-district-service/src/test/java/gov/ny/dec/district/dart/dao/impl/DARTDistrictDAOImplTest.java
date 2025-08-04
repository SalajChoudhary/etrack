package gov.ny.dec.district.dart.dao.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import gov.ny.dec.district.dart.entity.District;

@RunWith(SpringJUnit4ClassRunner.class)
public class DARTDistrictDAOImplTest {

  @InjectMocks
  private DARTDistrictDAOImpl dartDistrictDAOImpl;

  @Mock
  private SimpleJdbcCall decIDSearchJdbcCall;
 
  @Mock
  private SimpleJdbcCall facilityNameSearchJdbcCall;
  
  @Test
  public void testReturnsListSearchDistrictDetailByDecId() {
    District district = new District();
    district.setDecId("112321312");
    List<District> districts = new ArrayList<>();
    districts.add(district);
    Map<String, Object> resultMap = new HashMap<>();
    resultMap.put("CUR_FACILITY", districts);
    doReturn(decIDSearchJdbcCall).when(decIDSearchJdbcCall).declareParameters(Mockito.any(), Mockito.any());
    doReturn(decIDSearchJdbcCall).when(decIDSearchJdbcCall).returningResultSet(Mockito.anyString(),
        Mockito.any());
    doReturn(resultMap).when(decIDSearchJdbcCall).execute(Mockito.anyMap());

    List<District> districtDetails = dartDistrictDAOImpl.searchDistrictDetailByDecId("userId", "contextId", "2334322");
    assertTrue(districtDetails instanceof List);
    assertNotNull(districts);
  }

  @Test
  public void testReturnsListSearchDistrictDetailByFacilityName() {
    District district = new District();
    district.setDecId("112321312");
    List<District> districts = new ArrayList<>();
    districts.add(district);
    Map<String, Object> resultMap = new HashMap<>();
    resultMap.put("CUR_FACILITY", districts);
    doReturn(facilityNameSearchJdbcCall).when(facilityNameSearchJdbcCall).declareParameters(Mockito.any(), Mockito.any(), Mockito.any());
    doReturn(facilityNameSearchJdbcCall).when(facilityNameSearchJdbcCall).returningResultSet(Mockito.anyString(),
        Mockito.any());
    doReturn(resultMap).when(facilityNameSearchJdbcCall).execute(Mockito.anyMap());
    List<District> districtDetails =
        dartDistrictDAOImpl.searchDistrictDetailByFacilityName("userId", "contextId", "Facility", "S");
    assertTrue(districtDetails instanceof List);
    assertNotNull(districts);
  }
}
