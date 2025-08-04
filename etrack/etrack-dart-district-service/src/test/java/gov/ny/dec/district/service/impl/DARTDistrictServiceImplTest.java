package gov.ny.dec.district.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import gov.ny.dec.dart.district.model.DistrictDetail;
import gov.ny.dec.district.dart.dao.DARTDistrictDAO;
import gov.ny.dec.district.dart.entity.District;
import gov.ny.dec.district.etrack.entity.SubmitDocument;
import gov.ny.dec.district.etrack.repository.ETrackSubmitDocumentRepository;
import gov.ny.dec.district.etrack.repository.SupportDocumentRepo;
import gov.ny.dec.district.exception.DARTDistrictServiceException;
import gov.ny.dec.district.exception.ValidationException;
import gov.ny.dec.district.util.DistrictResponseHandler;

@RunWith(SpringJUnit4ClassRunner.class)
public class DARTDistrictServiceImplTest {

  @InjectMocks
  private DARTDistrictServiceImpl dartDistrictServiceImpl;

  @Mock
  private ETrackSubmitDocumentRepository eTrackSubmitDocRepository;

  @Mock
  private DistrictResponseHandler districtResponseHandler;

  @Mock
  private DARTDistrictDAO dartDistrictDAO;

  @Mock
  private SupportDocumentRepo supportDocumentRepo;
  
  @Test
  public void testGetDistrictDetails() {
    List<SubmitDocument> submittedDocuments = new ArrayList<>();
    SubmitDocument submitDocument = new SubmitDocument();
    submitDocument.setEdbDistrictId(12312L);
    submittedDocuments.add(submitDocument);

    DistrictDetail districtDetail = new DistrictDetail();
    districtDetail.setDistrictId(24324L);
    doReturn(submittedDocuments).when(eTrackSubmitDocRepository)
        .findByEdbDistrictIdAndDocumentStateCodeOrderByModifiedDateDesc(Mockito.anyLong(), Mockito.anyString());

    doReturn(districtDetail).when(districtResponseHandler)
        .transformDistrictDetails("userId", "contextId", submittedDocuments);
    ResponseEntity<DistrictDetail> responseEntity =
        dartDistrictServiceImpl.getDistrictDetails( "userId", "contextId", 1234L);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  public void testGetDistrictDetailsReturnsOK() {
    List<SubmitDocument> submittedDocuments = new ArrayList<>();
    SubmitDocument submitDocument = new SubmitDocument();
    submitDocument.setEdbDistrictId(12312L);
    submittedDocuments.add(submitDocument);

    DistrictDetail districtDetail = new DistrictDetail();
    districtDetail.setDistrictId(24324L);
    doReturn(submittedDocuments).when(eTrackSubmitDocRepository)
        .findByEdbDistrictIdAndDocumentStateCodeOrderByModifiedDateDesc(Mockito.anyLong(), Mockito.anyString());

    doReturn(null).when(districtResponseHandler).transformDistrictDetails("userId", "contextId", submittedDocuments);
    ResponseEntity<DistrictDetail> responseEntity =
        dartDistrictServiceImpl.getDistrictDetails("userId", "contextId", 1234L);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test(expected = DARTDistrictServiceException.class)
  public void testGetDistrictDetailsThrowsDARTDistrictException() {
    List<SubmitDocument> submittedDocuments = new ArrayList<>();
    SubmitDocument submitDocument = new SubmitDocument();
    submitDocument.setEdbDistrictId(12312L);
    submittedDocuments.add(submitDocument);

    DistrictDetail districtDetail = new DistrictDetail();
    districtDetail.setDistrictId(24324L);
    doThrow(DARTDistrictServiceException.class).when(eTrackSubmitDocRepository)
        .findByEdbDistrictIdAndDocumentStateCodeOrderByModifiedDateDesc(Mockito.anyLong(), Mockito.anyString());
    doReturn(districtDetail).when(districtResponseHandler)
        .transformDistrictDetails("userId", "contextId", submittedDocuments);
    dartDistrictServiceImpl.getDistrictDetails("userId", "contextId", 1234L);
  }

  @Test(expected = DARTDistrictServiceException.class)
  public void testGetDistrictDetailsThrowsRunTimeException() {
    List<SubmitDocument> submittedDocuments = new ArrayList<>();
    SubmitDocument submitDocument = new SubmitDocument();
    submitDocument.setEdbDistrictId(12312L);
    submittedDocuments.add(submitDocument);

    DistrictDetail districtDetail = new DistrictDetail();
    districtDetail.setDistrictId(24324L);
    doThrow(RuntimeException.class).when(eTrackSubmitDocRepository)
        .findByEdbDistrictIdAndDocumentStateCodeOrderByModifiedDateDesc(Mockito.anyLong(), Mockito.anyString());
    doReturn(districtDetail).when(districtResponseHandler)
        .transformDistrictDetails("userId", "contextId", submittedDocuments);
    dartDistrictServiceImpl.getDistrictDetails("userId", "contextId", 1234L);
  }

  @Test
  public void testGetDistrictDetailsByDecId() {
    List<District> districts = new ArrayList<>();
    District district = new District();
    district.setDecId("1233");
    districts.add(district);

    doReturn(districts).when(dartDistrictDAO).searchDistrictDetailByDecId(
        Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    ResponseEntity<List<District>> responseEntity =
        dartDistrictServiceImpl.getDistrictDetailsByDecId("userId", "contextId","1234");
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  public void testGetDistrictDetailsByDecIdReturnsNoContent() {
    doReturn(null).when(dartDistrictDAO).searchDistrictDetailByDecId(
        Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    ResponseEntity<List<District>> responseEntity =
        dartDistrictServiceImpl.getDistrictDetailsByDecId("userId", "contextId","1234");
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
  }

  @Test(expected = DARTDistrictServiceException.class)
  public void testGetDistrictDetailsByDecIdThrowsDARTDistrictException() {
    doThrow(DARTDistrictServiceException.class).when(dartDistrictDAO)
        .searchDistrictDetailByDecId(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    dartDistrictServiceImpl.getDistrictDetailsByDecId("userId", "contextId","1234");
  }

  @Test(expected = DARTDistrictServiceException.class)
  public void testGetDistrictDetailsByDecIdThrowsRunTimeException() {
    doThrow(RuntimeException.class).when(dartDistrictDAO)
        .searchDistrictDetailByDecId(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    dartDistrictServiceImpl.getDistrictDetailsByDecId("userId", "contextId","1234");
  }

  @Test
  public void testGetDistrictDetailsByFacilityName() {
    List<District> districts = new ArrayList<>();
    District district = new District();
    district.setDecId("1233");
    districts.add(district);

    doReturn(districts).when(dartDistrictDAO)
        .searchDistrictDetailByFacilityName(
            Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    ResponseEntity<List<District>> responseEntity =
        dartDistrictServiceImpl.getDistrictDetailsByFacilityName("userId", "contextId", "Facility", "S");
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  public void testGetDistrictDetailsByFacilityNameReturnsNoContent() {
    doReturn(null).when(dartDistrictDAO).searchDistrictDetailByFacilityName(
        Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
        Mockito.anyString());
    ResponseEntity<List<District>> responseEntity =
        dartDistrictServiceImpl.getDistrictDetailsByFacilityName("userId", "contextId","Facility", "S");
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
  }

  @Test(expected = DARTDistrictServiceException.class)
  public void testGetDistrictDetailsByFacilityNameThrowsDARTDistrictException() {
    doThrow(DARTDistrictServiceException.class).when(dartDistrictDAO)
        .searchDistrictDetailByFacilityName(
            Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    dartDistrictServiceImpl.getDistrictDetailsByFacilityName("userId", "contextId","Facility", "S");
  }

  @Test(expected = DARTDistrictServiceException.class)
  public void testGetDistrictDetailsByFacilityNameThrowsRunTimeException() {
    doThrow(RuntimeException.class).when(dartDistrictDAO)
        .searchDistrictDetailByFacilityName(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    dartDistrictServiceImpl.getDistrictDetailsByFacilityName("userId", "contextId", "Facility", "S");
  }
  
  @Test(expected = ValidationException.class)
  public void testThrowsErrorWhenResultSetIsEqualToThreshold() {
    List<District> districts = new ArrayList<>();
    for (int i = 0; i < 502 ; i++) {
      District district = new District();
      district.setDistrictId(Long.valueOf(i));
      districts.add(district);
    }
    doReturn(districts).when(dartDistrictDAO).searchDistrictDetailByFacilityName(
        Mockito.anyString(), Mockito.anyString(),Mockito.anyString(), Mockito.anyString());
    dartDistrictServiceImpl.getDistrictDetailsByFacilityName("userId", "contextId", "-----", "C");
  }
  
  @Test(expected = ValidationException.class)
  public void testThrowsErrorWhenResultSetIsAboveThreshold() {
    List<District> districts = new ArrayList<>();
    for (int i = 0; i < 503 ; i++) {
      District district = new District();
      district.setDistrictId(Long.valueOf(i));
      districts.add(district);
    }
    doReturn(districts).when(dartDistrictDAO).searchDistrictDetailByFacilityName(
        Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    dartDistrictServiceImpl.getDistrictDetailsByFacilityName("userId", "contextId", "-----", "C");
  }
}
