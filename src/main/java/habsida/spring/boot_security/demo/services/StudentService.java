package habsida.spring.boot_security.demo.services;


import habsida.spring.boot_security.demo.models.Student;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    Student add(Student student);
    List<Student> listStudents();

    void remove(Long id);

    Student update(Student student);

    Optional<Student> studentById(Long id);

    Optional<Student> findByName(String userName);
}
