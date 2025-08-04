package dec.ny.gov.etrack.gis.service;

import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public interface GISAddressRetrieveService {

  /**
   * Retrieve the Facility address details for the input address line 1.
   * 
   * @param address - Address line 1.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Returns Complete address details from ITS service.
   */
  String getITSAddresses(final String address, final String contextId);
  
  /**
   * Retrieve the ESRI Address for the input search parameters.
   * 
   * @param address - Address line 1.
   * @param postal - Postal Code.
   * @param city - City.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - ESRI Address.
   */
  String getEsriAddresses(final String address, final String postal, final String city, final String contextId);
  
  /**
   * Retrieve the County details from GIS Service.
   * 
   * @param contextId - Unique UUID to track this request.
   * @return - Returns the Counties.
   */
  Object getCounties(final String contextId);
  
  /**
   * Retrieve the Tax Parcel details for the input search parameters.
   * 
   * @param taxParcelID - Tax Parcel Id.
   * @param countyName - County Name.
   * @param muncipalName - Municipality Name.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Tax Parcel details.
   */
  Object getTaxParcel(final String taxParcelID, final String countyName,
      final String muncipalName, final String contextId);

  /**
   * Retrieve the Municipalities from GIS Service for the input County.
   * 
   * @param countyName = County name.
   * @param contextId - Unique UUID to track this request.
   * 
   * @return - Municipality details.
   */
  String getMuncipalities(final String countyName, final String contextId);

  /**
   * Get the Address details for the input address parameter.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param addressParam - Address details.
   * 
   * @return - Address details.
   */
  Object getAddressDetails(final String userId, final String contextId,
      final Map<String, String> addressParam);

}
