package fr.takima.training.simpleapi.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "students")
@Schema(description = "Student entity representing a student in the system")
public class Student {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the student", example = "1")
    private Long id;

    @Size(max = 20, message = "First name cannot be longer than 20 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "First name can only contain letters and spaces")
    @Column(name = "first_name")
    @Schema(description = "Student's first name", example = "John")
    private String firstname;

    @NotNull(message = "Last name cannot be null")
    @NotBlank(message = "Last name cannot be empty")
    @Size(max = 30, message = "Last name cannot be longer than 30 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Last name can only contain letters and spaces")
    @Column(name = "last_name")
    @Schema(description = "Student's last name", example = "Doe")
    private String lastname;

    @NotNull(message = "Department cannot be null")
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    @Schema(description = "Department the student belongs to")
    private Department department;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private String firstname;
        private String lastname;
        private Department department;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder firstname(String firstname) {
            this.firstname = firstname;
            return this;
        }

        public Builder lastname(String lastname) {
            this.lastname = lastname;
            return this;
        }

        public Builder department(Department department) {
            this.department = department;
            return this;
        }

        public Student build() {
            Student student = new Student();
            student.setId(id);
            student.setFirstname(firstname);
            student.setLastname(lastname);
            student.setDepartment(department);
            return student;
        }
    }
}
