package gov.ny.dec.etrack.cache.model;

import java.io.Serializable;
import lombok.Data;

public @Data class GISLayerConfigView implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String layerName;
  private String layerUrl;
  private Integer activeInd;
  private String layerType;
  private Integer orderInd;
}
