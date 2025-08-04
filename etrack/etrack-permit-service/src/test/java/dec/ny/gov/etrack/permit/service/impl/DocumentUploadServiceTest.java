package dec.ny.gov.etrack.permit.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import dec.ny.gov.etrack.permit.exception.ETrackPermitException;
import dec.ny.gov.etrack.permit.model.IngestionRequest;
import dec.ny.gov.etrack.permit.repo.SpatialInquiryRepo;
import dec.ny.gov.etrack.permit.repo.SupportDocumentRepo;

@RunWith(SpringJUnit4ClassRunner.class)
public class DocumentUploadServiceTest {
	
	  @Mock
	  private SupportDocumentRepo supportDocumentRepo;
	  @Mock
	  private SpatialInquiryRepo spatialInquiryRepo;
	  @Mock
	  @Qualifier("eTrackOtherServiceRestTemplate")
	  private RestTemplate eTrackOtherServiceRestTemplate;

	  @Rule
		public ExpectedException exceptionRule = ExpectedException.none();
	  @InjectMocks
	  private DocumentUploadService dup;
	  
	  Long projectId =12L;
	  
	  private MockRestServiceServer mockServer;
		
		private ObjectMapper mapper = new ObjectMapper();
		
	    private RestTemplate restTemplate = new RestTemplate();
	  
//	  public DocumentUploadServiceTest() {
//		  mockServer = MockRestServiceServer.createServer(restTemplate);
//	  }
	  
	@Test(expected = ETrackPermitException.class)
	public void testUploadPrintedFormatOfMapDocumentToDMS() {
		byte[] printedMapContent = null;
		String test = dup.uploadGISPrintFormattedMapDocumentToDMS("", "", "", 1L, "", "", false, false);
	}
	
	
	
	@BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }
	
	@Test
	public void testUploadPrintedFormatOfMapDocumentToDMSOne() {
		byte[] printedMapContent = null;
		String printUrl = "https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_ua/eTrack_ExportWebMap/GPServer/Export%20Web%20Map";
		RestTemplate retrievePrintableMapRestTemplate = new RestTemplate();
		  HttpHeaders httpHeaders = new HttpHeaders();
	      HttpEntity<?> mapEntity = new HttpEntity<>(httpHeaders);
		 ResponseEntity<byte[]> printedFormatMapResponse = new ResponseEntity(HttpStatus.OK);
		 printedFormatMapResponse.status(HttpStatus.OK);
		 try {
			 mockServer = MockRestServiceServer.createServer(restTemplate);
			mockServer.expect(ExpectedCount.once(), 
			          requestTo(new URI(printUrl)))
			          .andExpect(method(HttpMethod.GET))
			          .andRespond(withStatus(HttpStatus.OK)
			          .contentType(MediaType.APPLICATION_JSON)
			          .body("")
			        );
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}                                   
		            
//		 Mockito.verify(retrievePrintableMapRestTemplate, Mockito.times(1))
//         .exchange(Mockito.anyString(),
//                         Mockito.<HttpMethod> any(),
//                         Mockito.<HttpEntity<?>> any(),
//                         Mockito.<Class<?>> any(),
//                         Mockito.<String, String> anyMap());
		 exceptionRule.expect(HttpClientErrorException.class);
		 when(retrievePrintableMapRestTemplate.exchange(printUrl, HttpMethod.GET, 
				 mapEntity, byte[].class)).thenReturn(printedFormatMapResponse);
//		 IngestionRequest ingestionRequest = new IngestionRequest();
//		 List<Long> documentIds = new ArrayList<Long>();
//		 when(spatialInquiryRepo.findByDocumentNameAndInquiryId(projectId,"")).thenReturn(documentIds);
		 String test = dup.uploadGISPrintFormattedMapDocumentToDMS("", "", "", 1L, printUrl, "", false, false);
		 mockServer.verify();
		 assertNotNull(test);
	}
	
	//@Test
	private void testUploadPrintedFormatOfMapDocumentToDMS1() {
		byte[] printedMapContent = null;
		String printUrl = "https://gisinternal-np.dec.ny.gov/arcgis/rest/services/eTrack_ua/eTrack_ExportWebMap/GPServer/Export%20Web%20Map";
		RestTemplate retrievePrintableMapRestTemplate = new RestTemplate();
		  HttpHeaders httpHeaders = new HttpHeaders();
	      HttpEntity<?> mapEntity = new HttpEntity<>(httpHeaders);
		 ResponseEntity<byte[]> printedFormatMapResponse = new ResponseEntity(HttpStatus.OK);
		 printedFormatMapResponse.status(HttpStatus.OK);
		 when(retrievePrintableMapRestTemplate.exchange(printUrl, HttpMethod.GET, 
				 mapEntity, byte[].class)).thenReturn(printedFormatMapResponse);
		 IngestionRequest ingestionRequest = new IngestionRequest();
		 List<Long> documentIds = new ArrayList<Long>();
		 when(spatialInquiryRepo.findByDocumentNameAndInquiryId(projectId,"")).thenReturn(documentIds);
		 String test = dup.uploadGISPrintFormattedMapDocumentToDMS("", "", "", 1L, "", "", false, false);
		assertNotNull(test);
		}

}
