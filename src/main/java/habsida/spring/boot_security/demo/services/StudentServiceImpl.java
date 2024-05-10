package habsida.spring.boot_security.demo.services;

import habsida.spring.boot_security.demo.models.Student;
import habsida.spring.boot_security.demo.models.Role;
import habsida.spring.boot_security.demo.repositories.RoleRepository;
import habsida.spring.boot_security.demo.repositories.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Student add(Student student) {
        student.setPassword(passwordEncoder.encode(student.getPassword()));
       Set<Role> roles = student.getRoles();
       if(roles != null && !roles.isEmpty()) {
           for(Role role : roles) {
               if(role.getId() == null) {
                   roleRepository.save(role);
               }
           }
       }
       studentRepository.save(student);
       return student;
    }

    @Override
    public List<Student> listStudents() {
        return studentRepository.findAll();
    }

    @Override
    public void remove(Long id) {
        studentRepository.deleteById(id);
    }

    @Override
    public Student update(Student student) {
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        Set<Role> roles = student.getRoles();
        if(roles != null && !roles.isEmpty()) {
            for(Role role : roles) {
                if(role.getId() == null) {
                    roleRepository.save(role);
                }
            }
        }

        studentRepository.save(student);
        return student;
    }

    @Override
    public Optional<Student> studentById(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    public Optional<Student> findByName(String userName) {
        return studentRepository.findByName(userName);
    }
}
