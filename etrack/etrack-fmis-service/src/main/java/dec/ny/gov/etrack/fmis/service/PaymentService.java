package dec.ny.gov.etrack.fmis.service;

import org.springframework.stereotype.Service;
import dec.ny.gov.etrack.fmis.model.OAuthToken;
import dec.ny.gov.etrack.fmis.model.VPSResponse;

@Service
public interface PaymentService {

  /**
   * Request a VPS transaction id to initiate payment request.
   * 
   * @param userId - User who initiates this request
   * @param contextId - Unique id to track the request
   * @param projectId - Project Id.
   * @param invoiceNumber - Invoice number.
   * @param oAuthToken - OAuth VPS Token to interact with VPS system
   * 
   * @return - Transaction Id.
   */
  Object requestTransactionId(final String userId, final String contextId, final Long projectId,
      final String invoiceNumber, final OAuthToken oAuthToken);
  
  /**
   * Update the Payment confirmation number received from VPS system.
   * 
   * @param contextId - Unique UUID to track this request.
   * @param receipt - VPS payment receipt details.
   */
  void updateConfirmationNumber(final String contextId, final VPSResponse receipt);
}
