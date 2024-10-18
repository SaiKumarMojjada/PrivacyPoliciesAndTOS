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

    private String url;

    @Column(name = "policy_changed")
    private boolean policyChanged;

    @Column(name = "last_checked")
    private LocalDateTime lastChecked;
}