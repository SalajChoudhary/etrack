package dec.ny.gov.etrack.gis.service.impl;

import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dec.ny.gov.etrack.gis.exception.DataNotFoundException;
import dec.ny.gov.etrack.gis.exception.GISException;

@RunWith(SpringJUnit4ClassRunner.class)
public class GISAddressRetrieveServiceImplTest {

  @InjectMocks
  private GISAddressRetrieveServiceImpl gisServiceImpl;

  @Mock
  private RestTemplate gisITSServiceRestTemplate;

  @Mock
  private RestTemplate geoCodeServiceRestTemplate;

  @Mock
  private RestTemplate eTrackOtherServiceRestTemplate;

  @Mock
  private RestTemplate gisInternalServiceRestTemplate;

  @Mock
  private RestTemplate eTrackGISServiceRestTemplate;

  @Mock
  private ObjectMapper ObjectMapper;
  
  @Mock
  private RestTemplate gisExternalServiceRestTemplate;
  
  private String taxParcelId = "Parcel id";
  private String municipalName = "Gyeongi-do";
  private String contextId = "1245";
  private String postal = "12207";
  private String city = "Seoul";
  private String address = "890 Seoul Drive";
  private String county = "Albany";
  private String userId = "userid";

  
  @Test(expected = GISException.class)
  public void testGetITSAddressesThrowsHttpClientErrorExceptionWhenClientsThrowsError() {
    HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Status Text");
    when(gisITSServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenThrow(httpClientErrorException);
    gisServiceImpl.getITSAddresses(address, contextId);
  }
  
  @Test(expected = GISException.class)
  public void testGetITSAddressesThrowsHttpServerErrorExceptionWhenServiceThrowsError() {
    HttpServerErrorException httpServerErrorException = new HttpServerErrorException(HttpStatus.BAD_REQUEST, "Status Text");
    when(gisITSServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenThrow(httpServerErrorException);
    gisServiceImpl.getITSAddresses(address, contextId);
  }
  
  @Test
  public void testGetITSAddressesSuccessfully() {
    when(gisITSServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn("ITS Address");
    String result = gisServiceImpl.getITSAddresses(address, contextId);
    Assert.assertEquals("ITS Address", result);
  }

  @Test(expected = GISException.class)
  public void testGetEsriAddressesThrowsHttpClientErrorExceptionWhenClientsThrowsError() {
    HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Status Text");
    when(geoCodeServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenThrow(httpClientErrorException);
    gisServiceImpl.getEsriAddresses(address, postal, city, contextId);
  }
  
  @Test(expected = GISException.class)
  public void testGetEsriAddressesThrowsHttpServerErrorExceptionWhenServiceThrowsError() {
    HttpServerErrorException httpServerErrorException = new HttpServerErrorException(HttpStatus.BAD_REQUEST, "Status Text");
    when(geoCodeServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenThrow(httpServerErrorException);
    gisServiceImpl.getEsriAddresses(address, postal, city, contextId);
  }

  @Test
  public void testGetEsriAddressesSuccessfully() {
    when(geoCodeServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any()))
        .thenReturn("ESRI Address");
    String result = gisServiceImpl.getEsriAddresses(address, postal, city, contextId);
    Assert.assertEquals("ESRI Address", result);
  }

  @Test
  public void testGetCounties() {
    when(gisExternalServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn("Counties");
    String result = (String) gisServiceImpl.getCounties(contextId);
    Assert.assertEquals("Counties", result);
  }

  @Test(expected = GISException.class)
  public void testGetCountiesThrowsHttpClientErrorExceptionWhenClientsThrowsError() {
    HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Status Text");
    when(gisExternalServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenThrow(httpClientErrorException);
    gisServiceImpl.getCounties(contextId);
  }
  
  @Test(expected = GISException.class)
  public void testGetCountiesThrowsHttpServerErrorExceptionWhenServiceThrowsError() {
    HttpServerErrorException httpServerErrorException = new HttpServerErrorException(HttpStatus.BAD_REQUEST, "Status Text");
    when(gisExternalServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenThrow(httpServerErrorException);
    gisServiceImpl.getCounties(contextId);
  }

  @Test
  public void testGetTaxParcelWithMunicipalityName() {
    when(gisExternalServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn("Tax Parcel");
    String result = (String) gisServiceImpl.getTaxParcel(taxParcelId, county, municipalName, contextId);
    Assert.assertEquals("Tax Parcel", result);
  }

  @Test(expected = GISException.class)
  public void testGetTaxParcelThrowsHttpClientErrorExceptionWhenClientsThrowsError() {
    HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Status Text");
    when(gisExternalServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenThrow(httpClientErrorException);
    gisServiceImpl.getTaxParcel(taxParcelId, county, municipalName, contextId);
  }

  @Test(expected = GISException.class)
  public void testGetTaxParcelThrowsHttpServerErrorExceptionWhenServiceThrowsError() {
    HttpServerErrorException httpServerErrorException = new HttpServerErrorException(HttpStatus.BAD_REQUEST, "Status Text");
    when(gisExternalServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenThrow(httpServerErrorException);
    gisServiceImpl.getTaxParcel(taxParcelId, county, municipalName, contextId);
  }

  @Test
  public void testGetMunicipalitiesSuccessfully() {
    when(gisITSServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn("Municipalities");
    String result = gisServiceImpl.getMuncipalities(county, contextId);
    Assert.assertEquals("Municipalities", result);
  }

  @Test(expected = GISException.class)
  public void testGetMunicipalitiesThrowsHttpClientErrorExceptionWhenClientsThrowsError() {
    HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Status Text");
    when(gisITSServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenThrow(httpClientErrorException);
    gisServiceImpl.getMuncipalities(county, contextId);

  }

  @Test(expected = GISException.class)
  public void testGetMunicipalitiesThrowsHttpServerErrorExceptionWhenServiceThrowsError() {
    HttpServerErrorException httpServerErrorException = new HttpServerErrorException(HttpStatus.BAD_REQUEST, "Status Text");
    when(gisITSServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenThrow(httpServerErrorException);
    gisServiceImpl.getMuncipalities(county, contextId);
  }

  @Test(expected = GISException.class)
  public void testGetAddressDetailsThrowsHttpClientErrorExceptionWhenClientsThrowsError() {
    Map<String, String> addressParam = new HashMap<>();
    addressParam.put("streetAddress1", "73 N pearl st");
    addressParam.put("zipCode", "Shenzhen");
    HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Status Text");
    when(geoCodeServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenThrow(httpClientErrorException);
    gisServiceImpl.getAddressDetails(userId, contextId, addressParam);
  }

  @Test(expected = GISException.class)
  public void testGetAddressDetailsThrowsHttpServerErrorExceptionWhenServiceThrowsError() {
    Map<String, String> addressParam = new HashMap<>();
    addressParam.put("streetAddress1", "73 N pearl st");
    addressParam.put("zipCode", "Shenzhen");
    HttpServerErrorException httpServerErrorException = new HttpServerErrorException(HttpStatus.BAD_REQUEST, "Status Text");
    when(geoCodeServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenThrow(httpServerErrorException);
    gisServiceImpl.getAddressDetails(userId, contextId, addressParam);
  }

  @Test(expected = DataNotFoundException.class)
  public void testGetAddressDetailsThrowsDataNotFoundWhenThereIsNoAddressDetails() {
    Map<String, String> addressParam = new HashMap<>();
    addressParam.put("streetAddress1", "73 N pearl st");
    addressParam.put("zipCode", "Shenzhen");
    String addressCandidate = "{"
        + "    \"spatialReference\" : {"
        + "        \"wkid\" : 12312,"
        + "        \"latestWkid\" : 24234"
        + "    }"
        + "}";
    when(geoCodeServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn(addressCandidate);
    gisServiceImpl.getAddressDetails(userId, contextId, addressParam);
  }

  @Test(expected = DataNotFoundException.class)
  public void testGetAddressDetailsThrowsDataNotFoundWhenThereIsAddressScoreLessThan70() throws JsonMappingException, JsonProcessingException {
    Map<String, String> addressParam = new HashMap<>();
    addressParam.put("streetAddress1", "73 N pearl st");
    addressParam.put("zipCode", "Shenzhen");
    String addressCandidate = "{"
        + "    \"spatialReference\" : {"
        + "        \"wkid\" : 12312,"
        + "        \"latestWkid\" : 24234"
        + "    }, "
        + "    \"candidates\" : ["
        + "        {"
        + "        \"score\" : 65,"
        + "        \"zipcode\" : 23423,"
        + "        \"attributes\" : {"
        + "            \"City\" : \"city value\","
        + "            \"RegionAbbr\" : \"state-address\""
        + "        }    "
        + "    }"
        + "    ]"
        + "}";

    when(geoCodeServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn(addressCandidate);
    gisServiceImpl.getAddressDetails(userId, contextId, addressParam);
  }

  @Test(expected = DataNotFoundException.class)
  public void testGetAddressDetailsThrowsDataNotFoundWhenThereIsAddressScoreIsNull() throws JsonMappingException, JsonProcessingException {
    Map<String, String> addressParam = new HashMap<>();
    addressParam.put("streetAddress1", "73 N pearl st");
    addressParam.put("zipCode", "Shenzhen");
    String addressCandidate = "{"
        + "    \"spatialReference\" : {"
        + "        \"wkid\" : 12312,"
        + "        \"latestWkid\" : 24234"
        + "    }, "
        + "    \"candidates\" : ["
        + "        {"
        + "        \"zipcode\" : 23423,"
        + "        \"attributes\" : {"
        + "            \"City\" : \"city value\","
        + "            \"RegionAbbr\" : \"state-address\""
        + "        }    "
        + "    }"
        + "    ]"
        + "}";
    when(geoCodeServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn(addressCandidate);
    gisServiceImpl.getAddressDetails(userId, contextId, addressParam);
  }
  
  @Test
  public void testGetAddressDetailsReturnsValidDetailsScoreIs70() throws JsonMappingException, JsonProcessingException {
    Map<String, String> addressParam = new HashMap<>();
    addressParam.put("streetAddress1", "73 N pearl st");
    addressParam.put("zipCode", "Shenzhen");
    String addressCandidate = "{"
        + "    \"spatialReference\" : {"
        + "        \"wkid\" : 12312,"
        + "        \"latestWkid\" : 24234"
        + "    }, "
        + "    \"candidates\" : ["
        + "        {"
        + "        \"score\" : 70,"
        + "        \"zipcode\" : 23423,"
        + "        \"attributes\" : {"
        + "            \"City\" : \"city value\","
        + "            \"RegionAbbr\" : \"state-address\""
        + "        }    "
        + "    }"
        + "    ]"
        + "}";    
    when(geoCodeServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn(addressCandidate);
    @SuppressWarnings("unchecked")
    Map<String, Object> result = (Map<String, Object>) gisServiceImpl.getAddressDetails(userId, contextId, addressParam);
    Assert.assertEquals(result.get("city"), "city value");
    Assert.assertEquals(result.get("state"), "state-address");
    Assert.assertEquals(result.get("streetAddress1"), "73 N pearl st");
    Assert.assertEquals(result.get("zip"), "Shenzhen");
  }
  
  @Test
  public void testGetAddressDetailsReturnsValidDetailsScoreIsGT70() throws JsonMappingException, JsonProcessingException {
    Map<String, String> addressParam = new HashMap<>();
    addressParam.put("streetAddress1", "73 N pearl st");
    addressParam.put("zipCode", "Shenzhen");
    String addressCandidate = "{"
        + "    \"spatialReference\" : {"
        + "        \"wkid\" : 12312,"
        + "        \"latestWkid\" : 24234"
        + "    }, "
        + "    \"candidates\" : ["
        + "        {"
        + "        \"score\" : 72,"
        + "        \"zipcode\" : 23423,"
        + "        \"attributes\" : {"
        + "            \"City\" : \"city value\","
        + "            \"RegionAbbr\" : \"state-address\""
        + "        }    "
        + "    }"
        + "    ]"
        + "}";    
    when(geoCodeServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn(addressCandidate);
    @SuppressWarnings("unchecked")
    Map<String, Object> result = (Map<String, Object>) gisServiceImpl.getAddressDetails(userId, contextId, addressParam);
    Assert.assertEquals(result.get("city"), "city value");
    Assert.assertEquals(result.get("state"), "state-address");
    Assert.assertEquals(result.get("streetAddress1"), "73 N pearl st");
    Assert.assertEquals(result.get("zip"), "Shenzhen");
  }
  
  @Test
  public void testGetAddressDetailsReturnsValidDetailsWithoutStateIfNoRegionAbbr() throws JsonMappingException, JsonProcessingException {
    Map<String, String> addressParam = new HashMap<>();
    addressParam.put("streetAddress1", "73 N pearl st");
    addressParam.put("zipCode", "Shenzhen");
    String addressCandidate = "{"
        + "    \"spatialReference\" : {"
        + "        \"wkid\" : 12312,"
        + "        \"latestWkid\" : 24234"
        + "    }, "
        + "    \"candidates\" : ["
        + "        {"
        + "        \"score\" : 70,"
        + "        \"zipcode\" : 23423,"
        + "        \"attributes\" : {"
        + "            \"City\" : \"city value\""
        + "        }    "
        + "    }"
        + "    ]"
        + "}";    
    when(geoCodeServiceRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn(addressCandidate);
    @SuppressWarnings("unchecked")
    Map<String, Object> result = (Map<String, Object>) gisServiceImpl.getAddressDetails(userId, contextId, addressParam);
    Assert.assertEquals(result.get("city"), "city value");
    Assert.assertNull(result.get("state"));
    Assert.assertEquals(result.get("streetAddress1"), "73 N pearl st");
    Assert.assertEquals(result.get("zip"), "Shenzhen");
  }
}
