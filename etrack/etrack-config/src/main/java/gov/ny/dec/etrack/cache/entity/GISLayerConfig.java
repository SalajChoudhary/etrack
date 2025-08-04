package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name="E_GIS_LAYER_CONFIG")
public @Data class GISLayerConfig implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name="LAYER_NAME")
  private String layerName;
  @Column(name="LAYER_URL")
  private String layerUrl;
  @Column(name="ACTIVE_IND")
  private Integer activeInd;
  @Column(name="LAYER_TYPE")
  private String layerType;
  @Column(name="ORDER_IND")
  private Integer orderInd;
  private String createdById;
  private String modifiedById;
  private Date createDate;
  private Date modifiedDate;
}
