package dec.ny.gov.etrack.permit.repo;

//@Repository
//public interface OnlineUserRepo extends CrudRepository<OnlineUser, Long> {
public interface OnlineUserRepo {
  public void test();
//  List<OnlineUser> findByProjectIdAndEmailAddress(Long projectId, String emailAddress);
//
//  @Query(value="select * from {h-schema}e_online_user where project_id=?1 and online_user_type_code='O'", nativeQuery = true)
//  OnlineUser findByProjectIdAndOnlineUser(Long projectId);
//  
//  @Query(value="select p.first_name, p.last_name, a.email_address from {h-schema}e_public p, "
//      + "{h-schema}e_role r, {h-schema}e_address a where p.public_id=r.public_id "
//      + "and r.address_id=a.address_id and p.project_id=?1 and p.selected_in_etrack_ind=1 and p.public_id=?2", nativeQuery = true)
//  List<String> findPublicDetailsByProjectIdAndPublicId(Long projectId, Long publicId);
}
