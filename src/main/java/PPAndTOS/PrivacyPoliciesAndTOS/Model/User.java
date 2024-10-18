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

    @Column(name="name")
    private String userName;

    @Column(name = "email")
    private String userEmail;

    @Column(name ="password")
    private String userPassword;

}
