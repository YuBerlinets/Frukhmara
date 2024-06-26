package ua.berlinets.file_manager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import ua.berlinets.file_manager.enums.RoleEnum;

import java.util.List;

@Data
@Entity(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long roleId;
    @Column(name = "role_name")
    @Enumerated(EnumType.STRING)
    private RoleEnum roleName;
    @JsonIgnore
    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private List<User> users;
}
