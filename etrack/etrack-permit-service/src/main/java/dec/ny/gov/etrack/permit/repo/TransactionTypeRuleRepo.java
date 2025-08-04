package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.TransactionTypeRule;

@Repository
public interface TransactionTypeRuleRepo extends CrudRepository<TransactionTypeRule, Integer> {

  @Query("select t from TransactionTypeRule t where permitTypeCode= :permitTypeCode "
      + "and chgOriginalProjectInd= :chgOriginalProjectInd and userSelTransferInd= :userSelTransferInd "
      + "and userSelExtInd= :userSelExtnInd and userSelModInd= :userSelModInd and userSelNewInd= :userSelNewInd "
      + "and userSelRenInd= :userSelRenInd")
  List<TransactionTypeRule> findTranstypeAndAssociateDetails(
      final String permitTypeCode, final Integer userSelNewInd, final Integer userSelModInd, 
      final Integer userSelExtnInd, final Integer userSelTransferInd, final Integer userSelRenInd, 
      final Integer chgOriginalProjectInd);
}
