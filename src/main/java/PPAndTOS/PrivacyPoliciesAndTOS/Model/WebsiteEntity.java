package PPAndTOS.PrivacyPoliciesAndTOS.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "policy_changed")
    private boolean policyChanged;

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

    // Many-to-one relationship with User
//    Change the type from string to User here after alpha
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}