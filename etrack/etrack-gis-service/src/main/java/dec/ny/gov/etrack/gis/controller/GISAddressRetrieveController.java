package dec.ny.gov.etrack.gis.controller;

import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import dec.ny.gov.etrack.gis.service.GISAddressRetrieveService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class GISAddressRetrieveController {
  
  @Autowired
  private GISAddressRetrieveService gisAddressRetrieveService;
  private static final Logger LOGGER = LoggerFactory.getLogger(GISAddressRetrieveController.class.getName());
  
  /**
   * Retrieve the Facility address details for the input address line 1.
   * 
   * @param response - {@link HttpServletResponse}
   * @param address - Input Search Address.
   * 
   * @return - Facility Address details.
   */
  @GetMapping("/ITSAddress")
  @ApiOperation(value = "Retrieve the matching address from ITS-GIS Service.")
  public String getITSAddresses(HttpServletResponse response,
      @RequestParam(name = "SingleLine") @ApiParam(example = "625 Broadway", value="Search address") final String address) {

    String contextId = UUID.randomUUID().toString();

    LOGGER.info("Entering into getITSAddresses {}, Contxt Id {}", address, contextId);
    if (!StringUtils.hasLength(address)) {
      LOGGER.error("Input value address is empty . Context Id {} ", contextId);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return null;
    }
    return gisAddressRetrieveService.getITSAddresses(address, contextId);
  }

  /**
   * Retrieve the ESRI Address for the input search parameters.
   * 
   * @param response - {@link HttpServletResponse}
   * @param address - Address line 1.
   * @param postal - Postal Code.
   * @param city - City.
   * 
   * @return - Matched address details.
   */
  @GetMapping("/EsriAddresses")
  @ApiOperation(value = "Retrieve the matched address for the input postal code and city from ESRI service.")
  public String getEsriAddresses(HttpServletResponse response, @RequestParam final String address,
      @RequestParam @ApiParam(example = "12115", value="Postal/Zip Code") final String postal, 
      @RequestParam @ApiParam(example = "Albany", value="City name") final String city) {

    String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into getEsriAddresses address {} Postal {} City {}, Context Id {}",
        address, postal, city, contextId);

    if (!StringUtils.hasLength(address) || !StringUtils.hasLength(city)) {
      LOGGER.error(
          "One of the mandatory parameter is missing to "
              + "identify the ESRI addresses for the input search parameters."
              + " address {} city {}, postal code {}. Context Id {} ",
          address, city, postal, contextId);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return "One of the mandatory parameter(Address/City) is missing for the lookup ";
    }
    return gisAddressRetrieveService.getEsriAddresses(address, postal, city, contextId);
  }

  /**
   * Retrieve the County details from GIS Service.
   * 
   * @param response - {@link HttpServletResponse}
   * 
   * @return - Counties.
   */
  @GetMapping("/counties")
  @ApiOperation(value = "Retrieve all the Counties in the New York by calling GIS External service. "
      + "This detail will be retrieved by calling GIS ITS.")
  public Object getCounties(HttpServletResponse response) {
    
    String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into getCounties to retrieve Counties. Context Id {}", contextId);
    return gisAddressRetrieveService.getCounties(contextId);
  }

  /**
   * Retrieve the Tax Parcel details for the input search parameters.
   * 
   * @param response - {@link HttpServletResponse}
   * @param taxParcelID - Tax Parcel Id.
   * @param countyName - County Name.
   * @param municipalName - Municipality Name.
   * 
   * @return - Tax Parcel details.
   */
  @GetMapping("/taxParcel")
  @ApiOperation(value="Retrieve the Tax Parcel details for the input Tax Parcel I d, County Name and Municipality by calling GIS External service.")
  public Object getTaxParcel(HttpServletResponse response,
      @RequestParam("taxParcelID") @ApiParam(example = "156.17-2-41", value="Facility Polygon Tax Parcel ID") final String taxParcelID,
      @RequestParam("countyName") @ApiParam(example = "Albany", value="County Name") final String countyName,
      @RequestParam(name = "municipalName", required = false) @ApiParam(example = "Albany", value="Municipality Name") final String municipalName) {

    String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into getTaxParcel {} {} {}. Context Id {}", taxParcelID, countyName,
        municipalName, contextId);
    
    if (!(StringUtils.hasLength(taxParcelID) && StringUtils.hasLength(countyName))) {
      LOGGER.info("Mandatory value is missing {} {} {}", taxParcelID, countyName, municipalName);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return null;
    }
    return gisAddressRetrieveService.getTaxParcel(taxParcelID, countyName, municipalName, contextId);
  }

  /**
   * Retrieve the Municipalities from GIS Service for the input County.
   * 
   * @param response - {@link HttpServletResponse}
   * @param countyName - County name.
   * 
   * @return - Municipality details.
   */
  @GetMapping("/municipalities")
  @ApiOperation(value = "Retrieve all the Municipalities for the input county. This detail will be pulled by calling ITS GIS service.")
  public String getMunicipalities(HttpServletResponse response,
      @RequestParam("countyName") @ApiParam(example = "Albany", value="County Name") final String countyName) {

    String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into getMunicipalities {}, Context Id {}", countyName, contextId);
    if (!StringUtils.hasLength(countyName)) {
      LOGGER.error("Mandatory value is missing {}", countyName);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return null;
    }
    return gisAddressRetrieveService.getMuncipalities(countyName, contextId);
  }

  /**
   * Retrieve the complete Address details.
   * 
   * @param response - {@link HttpServletResponse}
   * @param addressParam - Address Parameter.
   * @param userId - User who initiates this request.
   * 
   * @return - Matched address details.
   */
  @PostMapping(value = "/address", produces = "application/json")
  @ApiOperation(value="Retrieve teh address details for the request details. Street Address 1 and Zip Code.")
  public Object getAddressDetails(HttpServletResponse response,
      @RequestBody Map<String, String> addressParam, 
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId) {

    final String contextId = UUID.randomUUID().toString();
    LOGGER.info("Entering into getAddressDetails Context Id: {}", contextId);
    if (!StringUtils.hasLength(addressParam.get("streetAddress1"))
        || !StringUtils.hasLength(addressParam.get("zipCode"))) {
      LOGGER.error("One of the mandatory parameter is missing to identify the address {}",
          addressParam);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return null;
    }
    return gisAddressRetrieveService.getAddressDetails(userId, contextId, addressParam);
  }

}
