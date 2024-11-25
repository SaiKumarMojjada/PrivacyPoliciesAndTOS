package PPAndTOS.PrivacyPoliciesAndTOS.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "websites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebsiteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String websiteName;

    private String url;

    private String privacypolicyURL;

    private String tosURL;

    @Column(name = "current_policy", columnDefinition = "TEXT")
    private String currentPolicy;

    @Column(name = "previous_policy", columnDefinition = "TEXT")
    private String previousPolicy;

    @Column(name = "policy_summary", columnDefinition = "TEXT")
    private String policySummary;

    @Column(name = "policy_changed")
    private boolean policyChanged;

    @Column(name = "current_tos", columnDefinition = "TEXT")
    private String currentTos;

    @Column(name = "previous_tos", columnDefinition = "TEXT")
    private String previousTos;

    @Column(name = "tos_summary", columnDefinition = "TEXT")
    private String tosSummary;

    @Column(name = "tos_changed")
    private Boolean tosChanged;

    @Column(name = "is_updated")
    private boolean isUpdated;

    @Column(name = "monitoring_enabled")
    private boolean monitoringEnabled;

    @Column(name = "last_checked")
    private LocalDateTime lastChecked;

    @Column(name = "last_updated_by")
    private String lastUpdatedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "alert_status")
    private String alertStatus;

    @Column(name = "alert_count")
    private int alertCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude // Prevent circular reference
    private User user;

}
