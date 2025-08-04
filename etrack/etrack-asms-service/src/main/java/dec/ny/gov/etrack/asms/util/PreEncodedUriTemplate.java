package dec.ny.gov.etrack.asms.util;

import java.net.URI;
import org.springframework.web.util.UriTemplate;

public class PreEncodedUriTemplate extends UriTemplate {

  public PreEncodedUriTemplate(String uriTemplate) {
    super(uriTemplate);
  }

  public URI encodeUri(final String uri) {
    return URI.create(uri);
  }
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

}
