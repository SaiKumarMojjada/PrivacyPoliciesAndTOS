package PPAndTOS.PrivacyPoliciesAndTOS.Repository;


import PPAndTOS.PrivacyPoliciesAndTOS.Model.WebsiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebsiteRepository extends JpaRepository<WebsiteEntity, Long> {
    long countByPolicyChangedTrue();
}
