package PPAndTOS.PrivacyPoliciesAndTOS.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;

    @Column(unique = true, nullable = false)
    private String userEmail;

    private String userPassword;

}
