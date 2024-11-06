package sn.sonatel.dsi.ins.imoc.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.sonatel.dsi.ins.imoc.domain.AppUser;

/**
 * Spring Data JPA repository for the AppUser entity.
 *
 * When extending this class, extend AppUserRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface AppUserRepository extends AppUserRepositoryWithBagRelationships, JpaRepository<AppUser, Long> {
    default Optional<AppUser> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<AppUser> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<AppUser> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }

    Optional<AppUser> findByEmail(String email);

//    @Query("SELECT u FROM AppUser u JOIN FETCH u.roles WHERE u.email = :email")
//    Optional<AppUser> findByEmailWithRoles(@Param("email") String email);
}
