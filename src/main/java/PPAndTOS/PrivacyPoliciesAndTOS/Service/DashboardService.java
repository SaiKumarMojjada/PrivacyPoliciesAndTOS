package PPAndTOS.PrivacyPoliciesAndTOS.Service;

import PPAndTOS.PrivacyPoliciesAndTOS.Model.WebsiteEntity;
import PPAndTOS.PrivacyPoliciesAndTOS.Repository.WebsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final WebsiteRepository websiteRepository;

    public long getMonitoredWebsiteCount() {
        return websiteRepository.count();
    }

    public long getPolicyChangeCount() {
        return websiteRepository.countByPolicyChangedTrue();
    }

    public long getPendingAlertsCount() {
        // Assuming pending alerts are policy changes not yet resolved
        return websiteRepository.countByPolicyChangedTrue();
    }
    public List<WebsiteEntity> getAllWebsites() {
        return websiteRepository.findAll(); // Retrieve all websites from the database
    }

    public WebsiteEntity getWebsiteById(Long id) {
        return websiteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Website not found with ID: " + id));
    }
}
