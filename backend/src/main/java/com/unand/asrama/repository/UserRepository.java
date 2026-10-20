package com.unand.asrama.repository;

import com.unand.asrama.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByNim(String nim);
    Optional<User> findByEmailOrNim(String email, String nim);
    List<User> findByRole(User.Role role);
    List<User> findByRoleAndIsActiveTrue(User.Role role);
    List<User> findByFasilId(Integer fasilId);
    List<User> findByKamarId(Integer kamarId);
    Optional<User> findByDeviceId(String deviceId);
}
