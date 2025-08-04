package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
@Entity
@Table(name = "E_MESSAGE_TYPE")
public @Data  class MessageType implements Serializable {
	/**
	   * 
	   */
	  private static final long serialVersionUID = 1L;
	  
	  @Id
	  @Column(name="MESSAGE_TYPE_ID")
	  private Integer messageTypeId;	  

	  
	  @Column(name="MESSAGE_TYPE_DESC")
	  private String messageTypeDescription;


}




