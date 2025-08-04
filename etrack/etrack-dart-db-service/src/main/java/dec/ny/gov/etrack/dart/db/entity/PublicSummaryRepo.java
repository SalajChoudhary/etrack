package dec.ny.gov.etrack.dart.db.entity;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicSummaryRepo extends CrudRepository<PublicSummary, Long> {
 
//  @Query(
//      value = "select distinct p.public_id as public_id, p.edb_public_id as edb_public_id, p.public_signed_ind as public_signed_ind, "
//          + "p.public_name as public_name, p.taxpayer_id as taxpayer_id, p.display_name as display_name, p.first_name as first_name, "
//          + "p.middle_name as middle_name, p.last_name as last_name, p.suffix as suffix, p.dba_name as dba_name, p.incorp_ind as incorp_ind, p.incorp_state as incorp_state, "
//          + "p.territory_or_country as territory_or_country, a.address_id as address_id, "
//          + "a.street1 as street1, a.street2 as street2, "
//          + "a.city as city, "
//          + "a.state as state, a.country as country, a.zip as zip, a.zip_extension as zip_extension,"
//          + "a.home_phone_number as home_phone_number, a.business_phone_number as business_phone_number, "
//          + "a.business_phone_ext as business_phone_ext, a.fax_phone_number as fax_phone_number,"
//          + "a.cell_phone_number as cell_phone_number, a.email_address as email_address, a.foreign_address_ind as foreign_address_ind, "
//          + "ph.edb_public_id as edb_public_id_hist, ph.public_signed_ind as public_signed_ind_hist, ph.public_name as public_name_hist, "
//          + "ph.taxpayer_id as taxpayer_id_hist, ph.display_name as display_name_hist, ph.first_name as first_name_hist, "
//          + "ph.middle_name as middle_name_hist, ph.last_name as last_name_hist, ph.suffix as suffix_hist, ph.dba_name as dba_name_hist, "
//          + "ph.incorp_ind as incorp_ind_hist, ph.incorp_state as incorp_state_hist, "
//          + "ph.territory_or_country as territory_or_country_hist, ah.address_id as address_id_hist, "
//          + "ah.street1 as street1_hist, ah.street2 as street2_hist, "
//          + " ah.city as city_hist, ah.state as state_hist, ah.country as country_hist ,ah.zip as zip_hist, "
//          + "ah.zip_extension as zip_extension_hist, ah.home_phone_number as home_phone_number_hist, "
//          + "ah.business_phone_number as business_phone_number_hist, ah.business_phone_ext as business_phone_ext_hist, "
//          + "ah.fax_phone_number as fax_phone_number_hist, ah.cell_phone_number as cell_phone_number_hist, ah.email_address as email_address_hist, "
//          + "ah.foreign_address_ind as foreign_address_ind_hist "
//          + "from {h-schema}e_public p, {h-schema}e_public_h ph, {h-schema}e_role r, {h-schema}e_address a,  {h-schema}e_address_h ah "
//          + "where p.public_id=ph.public_id and p.public_id=r.public_id and p.project_id=ph.project_id "
//          + "and r.address_id=a.address_id and r.address_id=ah.address_id and p.project_id=?1 "
//          + "and  ph.change_counter=0 and ah.change_counter=0 and p.selected_in_etrack_ind=1 "
//          + "and r.role_type_id in (2,3,4,5)", nativeQuery = true)
  @Query(value="select distinct p.public_id as public_id, p.edb_public_id as edb_public_id, p.public_signed_ind as public_signed_ind, "
      + "p.public_name as public_name, p.taxpayer_id as taxpayer_id, p.display_name as display_name, p.first_name as first_name, "
      + "p.middle_name as middle_name, p.last_name as last_name, p.suffix as suffix, p.dba_name as dba_name, p.incorp_ind as incorp_ind, p.incorp_state as incorp_state, "
      + "p.territory_or_country as territory_or_country, a.address_id as address_id, a.street1 as street1, a.street2 as street2, a.city as city, "
      + "a.state as state, a.country as country, a.zip as zip, a.zip_extension as zip_extension,"
      + "a.home_phone_number as home_phone_number, a.business_phone_number as business_phone_number, "
      + "a.business_phone_ext as business_phone_ext, a.fax_phone_number as fax_phone_number,"
      + "a.cell_phone_number as cell_phone_number, a.email_address as email_address, a.foreign_address_ind as foreign_address_ind "
      + "from {h-schema}e_public p, {h-schema}e_role r, {h-schema}e_address a where p.public_id=r.public_id "
      + "and r.address_id=a.address_id and p.project_id=?1 and r.selected_in_etrack_ind=1 and r.role_type_id in (2,3,4,5)", nativeQuery=true)
  List<PublicSummary> findAllContactsHistoryByProjectId(final Long projectId);
  
//  @Query(
//      value = "select distinct p.public_id as public_id, p.edb_public_id as edb_public_id, p.public_signed_ind as public_signed_ind, "
//          + "p.public_name as public_name, p.taxpayer_id as taxpayer_id, p.display_name as display_name, p.first_name as first_name, "
//          + "p.middle_name as middle_name, p.last_name as last_name, p.suffix as suffix, p.dba_name as dba_name, p.incorp_ind as incorp_ind, p.incorp_state as incorp_state, "
//          + "p.territory_or_country as territory_or_country, a.address_id as address_id, "
//          + "a.street1 as street1, a.street2 as street2, "
//          + "a.city as city, "
//          + "a.state as state, a.country as country, a.zip as zip, a.zip_extension as zip_extension,"
//          + "a.home_phone_number as home_phone_number, a.business_phone_number as business_phone_number, "
//          + "a.business_phone_ext as business_phone_ext, a.fax_phone_number as fax_phone_number,"
//          + "a.cell_phone_number as cell_phone_number, a.email_address as email_address, a.foreign_address_ind as foreign_address_ind, "
//          + "ph.edb_public_id as edb_public_id_hist, ph.public_signed_ind as public_signed_ind_hist, ph.public_name as public_name_hist, "
//          + "ph.taxpayer_id as taxpayer_id_hist, ph.display_name as display_name_hist, ph.first_name as first_name_hist, "
//          + "ph.middle_name as middle_name_hist, ph.last_name as last_name_hist, ph.suffix as suffix_hist, ph.dba_name as dba_name_hist, "
//          + "ph.incorp_ind as incorp_ind_hist, ph.incorp_state as incorp_state_hist, "
//          + "ph.territory_or_country as territory_or_country_hist, ah.address_id as address_id_hist, "
//          + "ah.street1 as street1_hist, ah.street2 as street2_hist, "
//          + " ah.city as city_hist, ah.state as state_hist, ah.country as country_hist ,ah.zip as zip_hist, "
//          + "ah.zip_extension as zip_extension_hist, ah.home_phone_number as home_phone_number_hist, "
//          + "ah.business_phone_number as business_phone_number_hist, ah.business_phone_ext as business_phone_ext_hist, "
//          + "ah.fax_phone_number as fax_phone_number_hist, ah.cell_phone_number as cell_phone_number_hist, ah.email_address as email_address_hist, "
//          + "ah.foreign_address_ind as foreign_address_ind_hist "
//          + "from {h-schema}e_public p, {h-schema}e_public_h ph, {h-schema}e_role r, {h-schema}e_address a,  {h-schema}e_address_h ah "
//          + "where p.public_id=ph.public_id and p.public_id=r.public_id and p.project_id=ph.project_id "
//          + "and r.address_id=a.address_id and r.address_id=ah.address_id and p.project_id=?1 "
////          + "and p.edb_public_id is not null "
//          + "and  ph.change_counter=0 and ah.change_counter=0 and p.selected_in_etrack_ind=1 "
//          + "and r.role_type_id=6", nativeQuery = true)
  @Query(value="select distinct p.public_id as public_id, p.edb_public_id as edb_public_id, p.public_signed_ind as public_signed_ind, "
      + "p.public_name as public_name, p.taxpayer_id as taxpayer_id, p.display_name as display_name, p.first_name as first_name, "
      + "p.middle_name as middle_name, p.last_name as last_name, p.suffix as suffix, p.dba_name as dba_name, p.incorp_ind as incorp_ind, p.incorp_state as incorp_state, "
      + "p.territory_or_country as territory_or_country, a.address_id as address_id, "
      + "a.street1 as street1, a.street2 as street2, a.city as city, "
      + "a.state as state, a.country as country, a.zip as zip, a.zip_extension as zip_extension, "
      + "a.home_phone_number as home_phone_number, a.business_phone_number as business_phone_number,  "
      + "a.business_phone_ext as business_phone_ext, a.fax_phone_number as fax_phone_number, "
      + "a.cell_phone_number as cell_phone_number, a.email_address as email_address, a.foreign_address_ind as foreign_address_ind  "
      + "from {h-schema}e_public p, {h-schema}e_role r, {h-schema}e_address a where p.public_id=r.public_id "
      + "and r.address_id=a.address_id and p.project_id=?1 and r.selected_in_etrack_ind=1 and r.role_type_id=6", nativeQuery=true)
  List<PublicSummary> findAllOwnersHistoryByProjectId(final Long projectId);

//  @Query(
//      value = "select distinct p.public_id as public_id, p.edb_public_id as edb_public_id, p.public_signed_ind as public_signed_ind, "
//          + "p.public_name as public_name, p.taxpayer_id as taxpayer_id, p.display_name as display_name, p.first_name as first_name, "
//          + "p.middle_name as middle_name, p.last_name as last_name, p.suffix as suffix, p.dba_name as dba_name, p.incorp_ind as incorp_ind, p.incorp_state as incorp_state, "
//          + "p.territory_or_country as territory_or_country, a.address_id as address_id, "
//          + "a.street1 as street1, a.street2 as street2, "
//          + "a.city as city, "
//          + "a.state as state, a.country as country, a.zip as zip, a.zip_extension as zip_extension,"
//          + "a.home_phone_number as home_phone_number, a.business_phone_number as business_phone_number, "
//          + "a.business_phone_ext as business_phone_ext, a.fax_phone_number as fax_phone_number,"
//          + "a.cell_phone_number as cell_phone_number, a.email_address as email_address, a.foreign_address_ind as foreign_address_ind, "
//          + "ph.edb_public_id as edb_public_id_hist, ph.public_signed_ind as public_signed_ind_hist, ph.public_name as public_name_hist, "
//          + "ph.taxpayer_id as taxpayer_id_hist, ph.display_name as display_name_hist, ph.first_name as first_name_hist, "
//          + "ph.middle_name as middle_name_hist, ph.last_name as last_name_hist, ph.suffix as suffix_hist, ph.dba_name as dba_name_hist, "
//          + "ph.incorp_ind as incorp_ind_hist, ph.incorp_state as incorp_state_hist, "
//          + "ph.territory_or_country as territory_or_country_hist, ah.address_id as address_id_hist, "
//          + "ah.street1 as street1_hist, ah.street2 as street2_hist, "
//          + " ah.city as city_hist, ah.state as state_hist, ah.country as country_hist ,ah.zip as zip_hist, "
//          + "ah.zip_extension as zip_extension_hist, ah.home_phone_number as home_phone_number_hist, "
//          + "ah.business_phone_number as business_phone_number_hist, ah.business_phone_ext as business_phone_ext_hist, "
//          + "ah.fax_phone_number as fax_phone_number_hist, ah.cell_phone_number as cell_phone_number_hist, ah.email_address as email_address_hist, "
//          + "ah.foreign_address_ind as foreign_address_ind_hist "
//          + "from {h-schema}e_public p, {h-schema}e_public_h ph, {h-schema}e_role r, {h-schema}e_address a,  {h-schema}e_address_h ah "
//          + "where p.public_id=ph.public_id and p.public_id=r.public_id and p.project_id=ph.project_id "
//          + "and r.address_id=a.address_id and r.address_id=ah.address_id and p.project_id=?1 "
////          + "and p.edb_public_id is not null "
//          + "and  ph.change_counter=0 and ah.change_counter=0 and p.selected_in_etrack_ind=1 "
//          + "and (r.role_type_id=1 or r.legally_responsible_type_code in (1,2,3))", nativeQuery = true)
  
  @Query(value="select distinct p.public_id as public_id, p.edb_public_id as edb_public_id, p.public_signed_ind as public_signed_ind, "
      + "p.public_name as public_name, p.taxpayer_id as taxpayer_id, p.display_name as display_name, p.first_name as first_name, "
      + "p.middle_name as middle_name, p.last_name as last_name, p.suffix as suffix, p.dba_name as dba_name, p.incorp_ind as incorp_ind, p.incorp_state as incorp_state, "
      + "p.territory_or_country as territory_or_country, a.address_id as address_id, a.street1 as street1, a.street2 as street2, "
      + "a.city as city, a.state as state, a.country as country, a.zip as zip, a.zip_extension as zip_extension,"
      + "a.home_phone_number as home_phone_number, a.business_phone_number as business_phone_number, "
      + "a.business_phone_ext as business_phone_ext, a.fax_phone_number as fax_phone_number, "
      + "a.cell_phone_number as cell_phone_number, a.email_address as email_address, a.foreign_address_ind as foreign_address_ind "
      + "from {h-schema}e_public p, {h-schema}e_role r, {h-schema}e_address a  where p.public_id=r.public_id and r.address_id=a.address_id and p.project_id=?1 "
      + "and r.selected_in_etrack_ind=1 and (r.role_type_id=1 or r.legally_responsible_type_code in (1,2,3))", nativeQuery=true)
  List<PublicSummary> findAllPublicsHistoryByProjectId(final Long projectId);
}
