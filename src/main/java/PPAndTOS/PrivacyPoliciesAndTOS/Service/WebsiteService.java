package PPAndTOS.PrivacyPoliciesAndTOS.Service;

import PPAndTOS.PrivacyPoliciesAndTOS.Model.WebsiteEntity;
import PPAndTOS.PrivacyPoliciesAndTOS.Repository.WebsiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WebsiteService {

    @Autowired
    private WebsiteRepository websiteRepository;

    public List<WebsiteEntity> getAllWebsites() {
        return websiteRepository.findAll();
    }

    // Find website by ID
    public WebsiteEntity findById(Long id) {
        return websiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Website not found with id: " + id));
    }

    // Update website details
    public void updateWebsite(Long id, WebsiteEntity updatedWebsite) {
        WebsiteEntity existingWebsite = findById(id); // Retrieve existing record
        existingWebsite.setWebsiteName(updatedWebsite.getWebsiteName());
        existingWebsite.setUrl(updatedWebsite.getUrl());
        existingWebsite.setPrivacypolicyURL(updatedWebsite.getPrivacypolicyURL());
        existingWebsite.setTosURL(updatedWebsite.getTosURL());
        existingWebsite.setPolicySummary(updatedWebsite.getPolicySummary());
        existingWebsite.setTosSummary(updatedWebsite.getTosSummary());
        existingWebsite.setMonitoringEnabled(updatedWebsite.isMonitoringEnabled());
        existingWebsite.setLastUpdatedBy(updatedWebsite.getLastUpdatedBy());
        websiteRepository.save(existingWebsite); // Save updated entity
    }

    // Delete website by ID
    public void deleteById(Long id) {
        websiteRepository.deleteById(id); // Use repository's deleteById method
    }
}