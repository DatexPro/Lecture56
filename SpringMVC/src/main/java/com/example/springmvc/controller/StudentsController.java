package com.example.springmvc.controller;

import com.example.springmvc.model.Student;
import com.example.springmvc.service.StudentServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/students")
@Controller
public class StudentsController {

    private final StudentServiceI studentsService;

    @Autowired
    public StudentsController(StudentServiceI studentsService) {
        this.studentsService = studentsService;

        Student student = new Student();
        student.setFirstName("Ruslan");
        student.setLastName("Kushnir");
        student.setDateOfBirth("2000.25.11");
        student.setEmail("ruslan@gmail.com");
        student.setPhone("0123456789");
        student.setGroupID((long) 1);
        studentsService.saveStudent(student);
    }

    @GetMapping(value = "/")
    @ResponseBody
    public ResponseEntity<List<Student>> read() {
        final List<Student> students = studentsService.getAllStudents();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping("/new")
    public ModelAndView createStudent() {
        Student student = new Student();
        return new ModelAndView("student/studentEdit")
                .addObject("student", student);
    }

    @GetMapping("/{id}")
    public ModelAndView viewStudent(@PathVariable("id") long id) {
        Student student = studentsService.getStudent(id);
        if (student == null) {
            throw new ResourceNotFoundException();
        }
        ModelAndView model = new ModelAndView("student/studentView");
        model.addObject("student", student);
        return model;
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editStudent(@PathVariable("id") int id) {
        Student student = studentsService.getStudent(id);

        return new ModelAndView("student/studentEdit")
                .addObject("student", student);
    }

    @PostMapping(value = "/delete")
    public ModelAndView delete(Student studentID) {
        Student student = studentsService.getStudent(studentID.getId());
        if (student == null) {
            throw new ResourceNotFoundException();
        } else {
            studentsService.deleteStudent(studentID.getId());
        }
        return new ModelAndView("redirect:/students/");
    }
    @PostMapping("/save")
    public String saveStudent(@Valid Student student, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/students/";
        }
        if (student.getId() != null) {
            studentsService.updateStudent(student);
        } else {
            studentsService.saveStudent(student);
        }
        return "redirect:/students/" + student.getId();
    }
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public class ResourceNotFoundException extends RuntimeException {
    }
}
