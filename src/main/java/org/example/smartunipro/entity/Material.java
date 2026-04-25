package org.example.smartunipro.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.smartunipro.model.Auditable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "materials")
public class Material extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String pdfUrl;

    private String videoUrl;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

}