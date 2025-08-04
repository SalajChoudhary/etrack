package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.TransactionTypeRuleEntity;

@Repository
public interface TransactionTypeRuleRepo extends CrudRepository<TransactionTypeRuleEntity, Long> {
  @Query(value="select t.* from {h-schema}e_transaction_type_rule t, {h-schema}e_permit_type_code p where t.permit_type_code=p.permit_type_code "
      + "and p.renewed_ind=?1 and p.permit_type_code not like 'GP%' order by p.permit_type_code", nativeQuery = true)
  List<TransactionTypeRuleEntity> findAllRulesByPermitByRenewedInd(Integer renewedInd);
  
  @Query(value="select * from {h-schema}E_TRANSACTION_TYPE_RULE where permit_type_code like 'GP-%'", nativeQuery = true)
  List<TransactionTypeRuleEntity> findAllRulesByGeneralPermits();
  
  @Query("select r from TransactionTypeRuleEntity r where r.transactionTypeRuleId != :transactionTypeRuleId "
      + "and permitTypeCode= :permitTypeCode and userSelNewInd= :userSelNewInd and userSelModInd= :userSelModInd and "
      + "userSelExtInd= :userSelExtInd and userSelTransferInd= :userSelTransferInd and userSelRenInd= :userSelRenInd and "
      + "chgOriginalProjectInd= :chgOriginalProjectInd")
  List<TransactionTypeRuleEntity> findAllExistingRulesForPermitType(
      final Long transactionTypeRuleId, final String permitTypeCode, final Integer userSelNewInd,
      final Integer userSelModInd, final Integer userSelExtInd, final Integer userSelTransferInd, final Integer userSelRenInd,
      final Integer chgOriginalProjectInd);
}
