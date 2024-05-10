package habsida.spring.boot_security.demo.controllers;

import habsida.spring.boot_security.demo.services.StudentService;
import habsida.spring.boot_security.demo.services.StudentServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class StudentController {
    private StudentService studentService;

    public StudentController(StudentService studentService){
        this.studentService = studentService;
    }
    @GetMapping("/student")
    public ModelAndView students() {
        ModelAndView mov = new ModelAndView("/student");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        mov.addObject("student", studentService.findByName(username));
        return mov;
    }
}
