package ua.berlinets.file_manager.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity(name = "storage_plan")
public class StoragePlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private double capacity;
    @OneToMany(mappedBy = "storagePlan")
    private List<User> users;
}
