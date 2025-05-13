package fr.takima.training.simpleapi.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "departments")
@Schema(description = "Department entity representing an academic department")
public class Department {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the department", example = "1")
    private Long id;

    @NotNull(message = "Department name cannot be null")
    @NotBlank(message = "Department name cannot be empty")
    @Size(max = 20, message = "Department name cannot be longer than 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Department name can only contain letters, numbers, and spaces")
    @Column(name = "name")
    @Schema(description = "Name of the department", example = "Computer Science")
    private String name;

    @OneToMany(mappedBy = "department")
    @Schema(description = "List of students in this department")
    private List<Student> students;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private String name;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Department build() {
            Department department = new Department();
            department.setId(id);
            department.setName(name);
            return department;
        }
    }
}
