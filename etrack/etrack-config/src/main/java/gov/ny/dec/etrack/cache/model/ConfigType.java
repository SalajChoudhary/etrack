package gov.ny.dec.etrack.cache.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import gov.ny.dec.etrack.cache.entity.ActionType;
import gov.ny.dec.etrack.cache.entity.ActivityTaskStatus;
import gov.ny.dec.etrack.cache.entity.ApplicantType;
import gov.ny.dec.etrack.cache.entity.CountryCode;
import gov.ny.dec.etrack.cache.entity.DevelopmentType;
import gov.ny.dec.etrack.cache.entity.ProposedUseCode;
import gov.ny.dec.etrack.cache.entity.PublicType;
import gov.ny.dec.etrack.cache.entity.ResidentialDevelopType;
import gov.ny.dec.etrack.cache.entity.StateCode;
import lombok.Data;

@JsonInclude(value = Include.NON_NULL)
public @Data class ConfigType {
  private List<ApplicantType> applicantTypes;
  private List<ActivityTaskStatus> activityTaskStatus;
  private List<ProposedUseCode> proposedUseCodes;
  private List<DevelopmentType> developmentTypes;
  private List<ResidentialDevelopType> residentialDevelopType;
  private List<PublicType> publicTypes;
  private List<CountryCode> countries;
  private List<StateCode> states;
  private List<ActionType> actionTypes;
}
