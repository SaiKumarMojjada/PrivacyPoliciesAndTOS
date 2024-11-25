package PPAndTOS.PrivacyPoliciesAndTOS.Service;

import PPAndTOS.PrivacyPoliciesAndTOS.Model.WebsiteEntity;
import PPAndTOS.PrivacyPoliciesAndTOS.Repository.WebsiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class FindDiffInContent {

    @Autowired
    private WebsiteScrappingService websiteScrappingService;

    @Autowired
    private WebsiteRepository websiteRepository;

    public String anyChange(WebsiteEntity websiteEntity) {
        String currentPPinDB = websiteEntity.getCurrentPolicy();
        String currentTosinDB = websiteEntity.getCurrentTos();

        String newPP = websiteScrappingService.scrapeTheContent(websiteEntity, "privacy");
        String newTOS = websiteScrappingService.scrapeTheContent(websiteEntity, "tos");

        String currentPPHash = calculateSHA256(currentPPinDB);
        String currentTOSHash = calculateSHA256(currentTosinDB);
        String newPPHash = calculateSHA256(newPP);
        String newTOSHash = calculateSHA256(newTOS);

        String ppDifference = "";
        String tosDifference = "";
        boolean isSaved = false;

        if (!currentPPHash.equals(newPPHash)) {
            ppDifference = findDifference(currentPPinDB, newPP);

            websiteEntity.setPreviousPolicy(currentPPinDB);
            websiteEntity.setCurrentPolicy(newPP);
            websiteEntity.setLastChecked(LocalDateTime.now()); // Update timestamp
            isSaved = true;
        }

        if (!currentTOSHash.equals(newTOSHash)) {
            tosDifference = findDifference(currentTosinDB, newTOS);

            websiteEntity.setPreviousTos(currentTosinDB); // Move current to previous
            websiteEntity.setCurrentTos(newTOS); // Save new content as current
            isSaved = true;
        }

        if (isSaved) {
            WebsiteEntity savedEntity = websiteRepository.save(websiteEntity);

            boolean ppSaved = newPP != null && newPP.equals(savedEntity.getCurrentPolicy());
            boolean tosSaved = newTOS != null && newTOS.equals(savedEntity.getCurrentTos());

            if (!ppSaved || !tosSaved) {
                return "Error: Failed to save updates to the database.";
            }
        }

        return String.format(
                "Privacy Policy Changes: %s\nTerms of Service Changes: %s",
                ppDifference.isEmpty() ? "No changes" : ppDifference,
                tosDifference.isEmpty() ? "No changes" : tosDifference
        );
    }

    private String calculateSHA256(String content) {
        if (content == null) {
            return "";
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error calculating SHA-256 hash", e);
        }
    }

    private String findDifference(String oldContent, String newContent) {
        if (oldContent == null || newContent == null) {
            return "Content missing for comparison.";
        }

        if (oldContent.equals(newContent)) {
            return ""; // No difference
        }

        StringBuilder diffBuilder = new StringBuilder();
        String[] oldLines = oldContent.split("\n");
        String[] newLines = newContent.split("\n");

        int maxLength = Math.max(oldLines.length, newLines.length);

        for (int i = 0; i < maxLength; i++) {
            String oldLine = i < oldLines.length ? oldLines[i] : "";
            String newLine = i < newLines.length ? newLines[i] : "";

            if (!oldLine.equals(newLine)) {
                diffBuilder.append(String.format("Line %d changed:\nOld: %s\nNew: %s\n\n", i + 1, oldLine, newLine));
            }
        }

        return diffBuilder.toString().trim();
    }
}
