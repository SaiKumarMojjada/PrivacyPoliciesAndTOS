package PPAndTOS.PrivacyPoliciesAndTOS.Repository;

import PPAndTOS.PrivacyPoliciesAndTOS.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserEmail(String userEmail);
}
