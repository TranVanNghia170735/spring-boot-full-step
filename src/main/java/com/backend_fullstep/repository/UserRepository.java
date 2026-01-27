package com.backend_fullstep.repository;

import com.backend_fullstep.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserName(String userName);
    Optional<UserEntity> findByEmail (String email);


    @Query(value="select u from UserEntity u where u.status='ACTIVE' " +
            "and (lower(u.firstName) like :keyword " +
            "or lower(u.lastName) like :keyword " +
            "or lower(u.userName) like :keyword " +
            "or lower(u.phone) like :keyword " +
            "or lower(u.email) like :keyword)"
    )
    Page<UserEntity> searchByKeyword(String keyword, Pageable pageable);

    @Query("select r.name from Role r inner join UserHasRole ur on r.id = ur.user.id where ur.id=:userId")
    List<String> findAllRolesByUserId(Long userId);


}
