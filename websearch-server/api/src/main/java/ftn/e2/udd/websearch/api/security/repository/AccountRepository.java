package ftn.e2.udd.websearch.api.security.repository;

import ftn.e2.udd.websearch.api.security.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AccountRepository extends JpaRepository<Account, Long> {

    public Account findByUsername(String username);
}
