package dec.ny.gov.etrack.gis.controller;

import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import dec.ny.gov.etrack.gis.service.GISAddressRetrieveService;

@RunWith(SpringRunner.class)
public class GISAddressRetrieveControllerTest {
  
  @InjectMocks
  private GISAddressRetrieveController gisController;

  @Mock
  private GISAddressRetrieveService gisService;

  private String esriResultString =
      "One of the mandatory parameter(Address/City) is missing for the lookup ";
  private String county = "Albany";
  private String address = "1153 Meadowdale";
  private String city = "Seattle";
  private String postal = "21014";
  private String municipalName = "Municipal!";
  private String taxParcelId = "TAX123";
  private MockHttpServletResponse response = new MockHttpServletResponse();

  @Test
  public void testGetITSAddressesWithNoAddress() {
    this.gisController.getITSAddresses(response, null);
    Assert.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
  }

  @Test
  public void testGetITSAddressesSuccessfully() {
    when(gisService.getITSAddresses(Mockito.anyString(), Mockito.anyString())).thenReturn("ITSAddress");
    Assert.assertEquals("ITSAddress", this.gisController.getITSAddresses(response, address));
  }

  @Test
  public void testGetEsriAddressesWithNoAddress() {
    this.gisController.getEsriAddresses(response, null, postal, city);
    String result = this.gisController.getEsriAddresses(response, null, postal, city);
    Assert.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    Assert.assertEquals(esriResultString, result);

  }

  @Test
  public void testGetEsriAddressesWithNoCity() {
    String result = this.gisController.getEsriAddresses(response, address, postal, null);
    Assert.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    Assert.assertEquals(esriResultString, result);
  }

  @Test
  public void testGetEsriAddressesSuccessfully() {
    when(gisService.getEsriAddresses(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
        Mockito.anyString())).thenReturn("ESRI Returns Success");
    String result = this.gisController.getEsriAddresses(response, address, postal, city);
    Assert.assertEquals("ESRI Returns Success", result);
  }

  @Test
  public void testGetCountiesSuccessfully() {
    when(gisService.getCounties(Mockito.anyString())).thenReturn("Counties");
    String countiesResult = (String)gisController.getCounties(response);
    Assert.assertEquals("Counties", countiesResult);
  }

  @Test
  public void testGetTaxParcelWithNoTaxParcelId() {
    Object responseObj =
        this.gisController.getTaxParcel(response, null, county, municipalName);
    Assert.assertEquals(responseObj, null);
    Assert.assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST.value());
  }

  @Test
  public void testGetTaxParcelWithNoCounty() {
    Object responseObj =
        this.gisController.getTaxParcel(response, taxParcelId, null, municipalName);
    Assert.assertEquals(responseObj, null);
    Assert.assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST.value());
  }

  @Test
  public void testGetTaxParcelSuccessfully() {
    when(gisService.getTaxParcel(Mockito.anyString(), Mockito.anyString(), 
        Mockito.anyString(), Mockito.anyString())).thenReturn("Tax Parcel");
    String result = (String) gisController.getTaxParcel(response, taxParcelId, county, municipalName);
    Assert.assertNotEquals("Tax Parcel", result);
  }

  @Test
  public void testGetMunicipalitiesWithNoCounty() {
    String result = this.gisController.getMunicipalities(response, null);
    Assert.assertNull(result);
    Assert.assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST.value());
  }

  @Test
  public void testGetMunicipalitiesSuccessfully() {
    when(gisService.getMuncipalities(Mockito.anyString(), Mockito.anyString())).thenReturn("Municipalities");
    String result = gisController.getMunicipalities(response, county);
    Assert.assertEquals("Municipalities", result);
  }

  @Test
  public void testGetAddressReturnsBadRequestWhenNoStreetAddress1Passed() {
    Map<String, String> addressParam = new HashMap<>();
    addressParam.put("zipCode", "12321");
    String result = (String) gisController.getAddressDetails(response,addressParam, "userId");
    Assert.assertNull(result);
    Assert.assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST.value());
  }
  
  @Test
  public void testGetAddressReturnsBadRequestWhenNoZipcodePassed() {
    Map<String, String> addressParam = new HashMap<>();
    addressParam.put("streetAddress1", "streetAddress1");
    String result = (String) gisController.getAddressDetails(response,addressParam, "userId");
    Assert.assertNull(result);
    Assert.assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST.value());
  }

  @Test
  public void testGetAddressReturnsSuccessfully() {
    Map<String, String> addressParam = new HashMap<>();
    addressParam.put("streetAddress1", "streetAddress1");
    addressParam.put("zipCode", "12321");
    when(gisService.getAddressDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn("Street Address Detail");
    String result = (String) gisController.getAddressDetails(response, addressParam, "userId");
    Assert.assertEquals("Street Address Detail", result);
  }
  
}
