package gov.ny.dec.etrack.cache.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import gov.ny.dec.etrack.cache.entity.TransactionTypeRuleEntity;
import gov.ny.dec.etrack.cache.exception.ETrackConfigDuplicateDataFoundException;
import gov.ny.dec.etrack.cache.exception.ETrackConfigNoDataFoundException;
import gov.ny.dec.etrack.cache.model.TransactionTypeRule;
import gov.ny.dec.etrack.cache.repostitory.TransactionTypeRuleRepo;

@Component
public class TransformationService {

  @Autowired
  private TransactionTypeRuleRepo transactionTypeRuleRepo;
  
  /**
   * Transform the Transaction Type Rule entity to Model can be used by UI.
   * 
   * @param tranactionTypeList - Transaction Type Rules configured.
   * 
   * @return - {@link List}
   */
  public List<TransactionTypeRule> transferTransactionTypeRuleToView(
      List<TransactionTypeRuleEntity> tranactionTypeList) {
    
    List<TransactionTypeRule> transactionTypeRules = new ArrayList<>();
    tranactionTypeList.forEach(transactionType -> {
      TransactionTypeRule transactionTypeRule = new TransactionTypeRule();
      transactionTypeRule.setTransactionTypeRuleId(transactionType.getTransactionTypeRuleId());
      transactionTypeRule.setPermitTypeCode(transactionType.getPermitTypeCode());
      transactionTypeRule.setTransTypeCode(transactionType.getTransTypeCode());
      transactionTypeRule.setUserSelNewInd(transactionType.getUserSelNewInd());
      transactionTypeRule.setUserSelModInd(transactionType.getUserSelModInd());
      transactionTypeRule.setUserSelExtInd(transactionType.getUserSelExtInd());
      transactionTypeRule.setUserSelTransferInd(transactionType.getUserSelTransferInd());
      transactionTypeRule.setUserSelRenInd(transactionType.getUserSelRenInd());
      transactionTypeRule.setChgOriginalProjectInd(transactionType.getChgOriginalProjectInd());
      transactionTypeRule.setSupportDocTransType(transactionType.getSupportDocTransType());
      if (transactionType.getDocumentSubTypeTitleId() != null) {
        transactionTypeRule.setModExtFormInd(1);
      } else {
        transactionTypeRule.setModExtFormInd(0);
      }
      transactionTypeRule.setActiveInd(transactionType.getActiveInd());
      transactionTypeRules.add(transactionTypeRule);
    });
    return transactionTypeRules.stream().sorted(
        Comparator.comparing(TransactionTypeRule::getPermitTypeCode)).collect(Collectors.toList());
  }
  
  /**
   * Transform the Transaction Type Rule View to Entity which can used to persist.
   * 
   * @param tranactionTypeList - Transaction Type Rules details passed by the user.
   * 
   * @return - {@link List}
   */
  public List<TransactionTypeRuleEntity> transferTransactionTypeRuleToEntity(
      final String userId, final String contextId,
      List<TransactionTypeRule> tranactionTypeList) {
    
    List<TransactionTypeRuleEntity> transactionTypeRules = new ArrayList<>();
    tranactionTypeList.forEach(transactionType -> {
      TransactionTypeRuleEntity transactionTypeRule = null;
      if (transactionType.getTransactionTypeRuleId() != null) {
        Optional<TransactionTypeRuleEntity> existingTransactionTypeOptional = transactionTypeRuleRepo.findById(transactionType.getTransactionTypeRuleId());
        if (!existingTransactionTypeOptional.isPresent()) {
          throw new ETrackConfigNoDataFoundException("Transaction Type Rule doesn't exist in the List");
        }
        List<TransactionTypeRuleEntity> existingTransTypeRules = transactionTypeRuleRepo.findAllExistingRulesForPermitType(
            transactionType.getTransactionTypeRuleId(), transactionType.getPermitTypeCode(), 
            transactionType.getUserSelNewInd(), transactionType.getUserSelModInd(), 
            transactionType.getUserSelExtInd(), transactionType.getUserSelTransferInd(), 
            transactionType.getUserSelRenInd(), transactionType.getChgOriginalProjectInd());
        
        if (!CollectionUtils.isEmpty(existingTransTypeRules)) {
          throw new ETrackConfigDuplicateDataFoundException("Transaction Type Rule exists in the List");
        }
        transactionTypeRule = existingTransactionTypeOptional.get();
        transactionTypeRule.setModifiedById(userId);
        transactionTypeRule.setModifiedDate(new Date());
      } else {
        transactionTypeRule = new TransactionTypeRuleEntity();
        transactionTypeRule.setCreatedById(userId);
        transactionTypeRule.setCreateDate(new Date());
        transactionTypeRule.setEdbTransTypeCode(transactionType.getTransTypeCode());
      }
      transactionTypeRule.setTransactionTypeRuleId(transactionType.getTransactionTypeRuleId());
      transactionTypeRule.setPermitTypeCode(transactionType.getPermitTypeCode());
      transactionTypeRule.setTransTypeCode(transactionType.getTransTypeCode());
      transactionTypeRule.setUserSelNewInd(transactionType.getUserSelNewInd());
      transactionTypeRule.setUserSelModInd(transactionType.getUserSelModInd());
      transactionTypeRule.setUserSelExtInd(transactionType.getUserSelExtInd());
      transactionTypeRule.setUserSelTransferInd(transactionType.getUserSelTransferInd());
      transactionTypeRule.setUserSelRenInd(transactionType.getUserSelRenInd());
      transactionTypeRule.setChgOriginalProjectInd(transactionType.getChgOriginalProjectInd());
      transactionTypeRule.setSupportDocTransType(transactionType.getSupportDocTransType());
      if (transactionType.getModExtFormInd() != null && transactionType.getModExtFormInd() == 1) {
        transactionTypeRule.setDocumentSubTypeTitleId(Long.valueOf(264));
      } else {
        transactionTypeRule.setDocumentSubTypeTitleId(null);
      }
      transactionTypeRule.setActiveInd(transactionType.getActiveInd());
      transactionTypeRules.add(transactionTypeRule);
    });
    return transactionTypeRules;
  }
  
}
