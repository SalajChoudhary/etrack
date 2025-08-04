package gov.ny.dec.district.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import gov.ny.dec.dart.district.model.DistrictDetail;
import gov.ny.dec.district.etrack.entity.ETrackDocumentFile;
import gov.ny.dec.district.etrack.entity.ETrackDocumentNonRelDetails;
import gov.ny.dec.district.etrack.entity.SubmitDocument;

@RunWith(SpringJUnit4ClassRunner.class)
public class DistrictResponseHandlerTest {
  
  @InjectMocks
  private DistrictResponseHandler districtResponseHandler;

  @Test
  public void testReturnNullWhenInputListIsNull() {
    DistrictDetail districtDetail =  districtResponseHandler.transformDistrictDetails("userId", "contextId", null);
    assertNotNull(districtDetail);
    assertEquals(0, districtDetail.getDocuments().size());
  }

  @Test
  public void testReturnEmptyObjectWhenInputListIsEmpty() {
    DistrictDetail districtDetail =  districtResponseHandler.transformDistrictDetails("userId", "contextId",null);
    assertNotNull(districtDetail);
    assertEquals(0, districtDetail.getDocuments().size());
  }

  @Test
  public void testReturnEmptyFilesListWhenNoFilesInInput() {
    SubmitDocument submitDocument = new SubmitDocument();
    submitDocument.setDocumentId(13123L);
    submitDocument.setAccessByDepOnlyInd("N");
    submitDocument.setDocReleasableCode("REL");
    submitDocument.setDocumentTypeId(1);
    submitDocument.setDocumentStateCode("STAT");
    submitDocument.setDocumentDesc("DESC");
    submitDocument.setDocumentNm("Name");
    submitDocument.setDocSubTypeOtherTxt("OTHER");
    submitDocument.setCreatedById("userId");
    submitDocument.setTrackedApplicationId("appId");
    List<SubmitDocument> submittedDocuments = new ArrayList<>();
    submittedDocuments.add(submitDocument);
    DistrictDetail districtDetail =  districtResponseHandler.transformDistrictDetails("userId", "contextId", submittedDocuments);
    assertNull(districtDetail.getDocuments().get(0).getFiles());
  }
  
  @Test
  public void testReturnFilesAndNoReleasableDetails() {
    SubmitDocument submitDocument = new SubmitDocument();
    submitDocument.setDocumentId(13123L);
    submitDocument.setAccessByDepOnlyInd("N");
    submitDocument.setDocReleasableCode("REL");
    submitDocument.setDocumentTypeId(1);
    submitDocument.setDocumentStateCode("STAT");
    submitDocument.setDocumentDesc("DESC");
    submitDocument.setDocumentNm("Name");
    submitDocument.setDocSubTypeOtherTxt("OTHER");
    submitDocument.setCreatedById("userId");
    submitDocument.setTrackedApplicationId("appId");
    ETrackDocumentFile file = new ETrackDocumentFile();
    file.setFileNbr(0);
    file.setFileNm("test.dat");
    Set<ETrackDocumentFile> fileSet = new HashSet<>();
    fileSet.add(file);
    submitDocument.setETrackDocumentFile(fileSet);
    List<SubmitDocument> submittedDocuments = new ArrayList<>();
    submittedDocuments.add(submitDocument);
    DistrictDetail districtDetail =  districtResponseHandler.transformDistrictDetails("userId", "contextId", submittedDocuments);
    assertNotNull(districtDetail.getDocuments().get(0).getFiles());
    assertNull(districtDetail.getDocuments().get(0).getDocNonRelReasonCodes());
  }
  
  @Test
  public void testReturnFilesNoReleasableDetails() {
    SubmitDocument submitDocument = new SubmitDocument();
    submitDocument.setDocumentId(13123L);
    submitDocument.setAccessByDepOnlyInd("N");
    submitDocument.setDocReleasableCode("REL");
    submitDocument.setDocumentTypeId(1);
    submitDocument.setDocumentStateCode("STAT");
    submitDocument.setDocumentDesc("DESC");
    submitDocument.setDocumentNm("Name");
    submitDocument.setDocSubTypeOtherTxt("OTHER");
    submitDocument.setCreatedById("userId");
    submitDocument.setTrackedApplicationId("appId");
    ETrackDocumentFile file = new ETrackDocumentFile();
    file.setFileNbr(0);
    file.setFileNm("test.dat");
    Set<ETrackDocumentFile> fileSet = new HashSet<>();
    fileSet.add(file);
    submitDocument.setETrackDocumentFile(fileSet);
    
    Set<ETrackDocumentNonRelDetails> nonReleasableDetails = new HashSet<>();
    ETrackDocumentNonRelDetails nonRelDetail = new ETrackDocumentNonRelDetails();
    nonRelDetail.setDocNonRelReasonCode("NON_REL_CODE");
    nonReleasableDetails.add(nonRelDetail);
    submitDocument.setETrackDocumentNonRelDetails(nonReleasableDetails);
    List<SubmitDocument> submittedDocuments = new ArrayList<>();
    submittedDocuments.add(submitDocument);
    
    DistrictDetail districtDetail =  districtResponseHandler.transformDistrictDetails("userId", "contextId", submittedDocuments);
    assertNotNull(districtDetail.getDocuments().get(0).getFiles());
    assertNotNull(districtDetail.getDocuments().get(0).getDocNonRelReasonCodes());
  }
}
