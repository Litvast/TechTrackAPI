package ru.litvast.techtrackapi.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.litvast.techtrackapi.model.entity.equipment.Printer;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Computer;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    private String position;

    private String email;

    private String phone;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @OneToOne
    @JoinColumn(name = "assigned_computer_id")
    private Computer assignedComputer;

    @OneToOne
    @JoinColumn(name = "assigned_printer_id")
    private Printer assignedPrinter;
}