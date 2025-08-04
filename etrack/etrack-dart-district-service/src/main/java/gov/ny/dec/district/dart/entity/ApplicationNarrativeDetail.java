package gov.ny.dec.district.dart.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ApplicationNarrativeDetail implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private Long applId;
  @Lob
  private String applNarrHtml;
}
