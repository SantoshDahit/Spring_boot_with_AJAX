package habsida.spring.boot_security.demo.controllers;

import habsida.spring.boot_security.demo.models.Role;
import habsida.spring.boot_security.demo.models.Student;
import habsida.spring.boot_security.demo.services.RoleService;
import habsida.spring.boot_security.demo.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MainController {

    private final StudentService studentService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MainController(StudentService studentService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.studentService = studentService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/admin/roles")
    @ResponseBody
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.listRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @GetMapping("/admin/adminPage")
    public ModelAndView showStudents() {
        ModelAndView mov = new ModelAndView("/adminPage");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<Student> loggedInUserOptional = studentService.findByName(username);
        if (loggedInUserOptional.isPresent()) {
            Student loggedInUser = loggedInUserOptional.get();
            mov.addObject("loggedInUser", loggedInUser);
        } else {
            mov.addObject("loggedInUser", null);
        }
        mov.addObject("students", studentService.listStudents());
        return mov;
    }

    @GetMapping("/admin/studentsData")
    @ResponseBody
    public ResponseEntity<List<Student>> getAllStudentsData() {
        List<Student> students = studentService.listStudents();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping("/admin/edit")
    public Object editStudent(@RequestParam long id) {
        Optional<Student> studentOptional = studentService.studentById(id);
        if (studentOptional.isPresent()) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("student", studentOptional.get());
            responseData.put("roles", roleService.listRoles());
            return ResponseEntity.ok(responseData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

//    @PutMapping("/admin/student/update")
//    public ResponseEntity<Student> updateStudent(@RequestParam Long id, @RequestBody Student student) {
//        Student updatedStudent = studentService.update(id, student);
//        return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
//    }

    @PutMapping("/admin/student/update")
    public ResponseEntity<Student> updateStudent(@RequestParam Long id, @RequestBody Student student) {
        Optional<Student> existingStudentOptional = studentService.studentById(id);
        if (existingStudentOptional.isPresent()) {
            Student existingStudent = existingStudentOptional.get();

            //
            String newPassword = student.getPassword();
            if (newPassword != null && !newPassword.isEmpty()) {

                existingStudent.setPassword(passwordEncoder.encode(newPassword));
            }

            existingStudent.setName(student.getName());
            existingStudent.setLastName(student.getLastName());
            existingStudent.setDegree(student.getDegree());
            existingStudent.setRoles(student.getRoles());


            Student updatedStudent = studentService.update(existingStudent);
            return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/admin/student")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Student createdStudent = studentService.add(student);
        return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/student")
    public ResponseEntity<Void> deleteStudent(@RequestParam long id) {
        studentService.remove(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/index")
    public String index() {
        return "/index";
    }


}
