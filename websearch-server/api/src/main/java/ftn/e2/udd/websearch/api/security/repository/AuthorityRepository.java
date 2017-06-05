package ftn.e2.udd.websearch.api.security.repository;

import ftn.e2.udd.websearch.api.security.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AuthorityRepository extends JpaRepository<Authority, Long> {

}
