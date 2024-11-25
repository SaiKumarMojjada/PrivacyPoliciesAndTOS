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
}