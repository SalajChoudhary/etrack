package gov.ny.dec.etrack.cache.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import gov.ny.dec.etrack.cache.entity.NAICSCode;
import gov.ny.dec.etrack.cache.entity.SICCodes;
import gov.ny.dec.etrack.cache.repostitory.NAICSCodeRepo;
import gov.ny.dec.etrack.cache.repostitory.SICCodeRepo;
import gov.ny.dec.etrack.cache.service.NAICSSICService;

@Service
public class NAICSSICServiceImpl implements NAICSSICService {

  @Autowired
  private NAICSCodeRepo naicsCodeRepo;
  
  @Autowired
  private SICCodeRepo sicCodeRepo;

  private static final Logger logger = LoggerFactory.getLogger(NAICSSICServiceImpl.class.getName());
  
  @Override
  public Map<String, String> getNAICSCodes(final String userId, final String contextId, final String sicCode) {
    List<NAICSCode> naicsCodesList = naicsCodeRepo.findAllBySicCode(sicCode);
    logger.info("NAICSCode list {} for the input SICCode {}, Context Id {}", naicsCodesList);
    Map<String, String> naicsCodes = new HashMap<>();
    naicsCodesList.forEach(naicsCode -> {
      naicsCodes.put(naicsCode.getNaicsCode(), naicsCode.getNaicsDesc());
    });
    return naicsCodes;
  }

  @Override
  public List<SICCodes> getSICCodes(final String userId, final String contextId) {
    List<SICCodes> sicCodesList = sicCodeRepo.findAllSICCodes();
    return sicCodesList.stream().sorted(
        Comparator.comparing(SICCodes::getSicCode)).collect(Collectors.toList());
  }
}
