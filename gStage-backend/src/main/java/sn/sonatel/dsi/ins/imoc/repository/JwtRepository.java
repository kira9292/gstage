package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.sonatel.dsi.ins.imoc.domain.Jwt;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Spring Data JPA repository for the Jwt entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JwtRepository extends JpaRepository<Jwt, Long> {

    Optional<Jwt> findByValeur(String token);

    @Query("FROM Jwt j WHERE j.expire = false AND j.desactive = false  AND j.appUser.email = :email")
    Optional<Jwt> findUserValidToken(@Param("email") String email );

    @Query("FROM Jwt j where j.appUser.email = :email")
    Stream<Jwt> finduser(String email);

    void deleteByDesactiveAndExpire(boolean desactive, boolean expire);
}
