package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.Address;

@Repository
public interface AddressRepo extends CrudRepository<Address, Long> {
  public Optional<Address> findByAddressId(Long addressId);
  
  @Query(value="select a.address_id from {h-schema}e_public p, {h-schema}e_role r, {h-schema}e_address a "
      + "where p.public_id=r.public_id and r.address_id = a.address_id and "
      + "p.project_id=?2 and p.public_type_code=?3 and p.public_id=?1 and r.role_type_id in(2, 3, 4, 5)", nativeQuery=true)
  public List<Long> findAddressExistsForContact(Long publicId, Long ProjectId, String publicTypeCode);

  @Query(value="select a.address_id from {h-schema}e_public p, {h-schema}e_role r, {h-schema}e_address a "
      + "where p.public_id=r.public_id and r.address_id = a.address_id and "
      + "p.project_id=?2 and p.public_type_code=?3 and p.public_id=?1 and r.role_type_id in (6)", nativeQuery=true)
  public List<Long> findAddressExistsForOwner(Long publicId, Long ProjectId, String publicTypeCode);

  @Query(value="select a.address_id from {h-schema}e_public p, {h-schema}e_role r, {h-schema}e_address a "
      + "where p.public_id=r.public_id and r.address_id = a.address_id and "
      + "p.project_id=?2 and p.public_type_code=?3 and p.public_id=?1 "
      + "and (r.role_type_id=1 or r.legally_responsible_type_code in (1,2,3))", nativeQuery=true)
  public List<Long> findAddressExistsForPublic(Long publicId, Long ProjectId, String publicTypeCode);

  @Query(value= "select a.address_id from {h-schema}e_role r, {h-schema}e_address a "
      + "where r.address_id=a.address_id and r.public_id=?1", nativeQuery=true)
  public Long findByPublicId(Long publicId);

}
