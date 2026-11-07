package com.tp3.cruddemo;

import com.tp3.cruddemo.dao.StudentDAO;
import com.tp3.cruddemo.entity.Student;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class CruddemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CruddemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(StudentDAO studentDAO) {
		return args -> {
			String action = (args.length > 0) ? args[0] : "create";

			switch (action) {
				case "create" -> createStudent(studentDAO);
				case "read"   -> readStudents(studentDAO);
				case "update" -> updateStudent(studentDAO);
				case "delete" -> deleteStudent(studentDAO);
				default       -> System.out.println("Action inconnue: " + action);
			}
		};
	}

	// --- Exercice 5.1 : Create ---
	private void createStudent(StudentDAO studentDAO) {
		// Créer les étudiants
		System.out.println("Creating 3 student objects...");
		Student tempStudent1 = new Student("Paul", "Doe", "paul@test.com");
		Student tempStudent2 = new Student("Marie", "Dupont", "marie@test.com");
		Student tempStudent3 = new Student("Jean", "Doe", "jean@test.com");

		// Sauvegarder
		System.out.println("Saving the students...");
		studentDAO.save(tempStudent1);
		studentDAO.save(tempStudent2);
		studentDAO.save(tempStudent3);

		// Afficher les ids générés
		System.out.println("Saved student. Generated id: " + tempStudent1.getId());
		System.out.println("Saved student. Generated id: " + tempStudent2.getId());
		System.out.println("Saved student. Generated id: " + tempStudent3.getId());
	}

	// --- Exercice 5.2 + 5.3 + 5.4 : Read ---
	private void readStudents(StudentDAO studentDAO) {
		// 5.2 — findById
		System.out.println("\n--- findById ---");
		Student myStudent = studentDAO.findById(1);
		System.out.println("Found the student: " + myStudent);

		// 5.3 — findAll
		System.out.println("\n--- findAll (trié par lastName) ---");
		List<Student> theStudents = studentDAO.findAll();
		for (Student s : theStudents) {
			System.out.println(s);
		}

		// 5.4 — findByLastName
		System.out.println("\n--- findByLastName('Doe') ---");
		List<Student> doeStudents = studentDAO.findByLastName("Doe");
		doeStudents.forEach(System.out::println);
	}

	// --- Exercice 5.5 : Update ---
	private void updateStudent(StudentDAO studentDAO) {
		// Récupérer le premier étudiant disponible (id dynamique)
		List<Student> all = studentDAO.findAll();
		if (all.isEmpty()) { System.out.println("Aucun étudiant. Lance d'abord: create"); return; }
		int studentId = all.get(0).getId();
		Student myStudent = studentDAO.findById(studentId);
		System.out.println("Before update: " + myStudent);

		// Modifier le prénom Paul → John
		myStudent.setFirstName("John");
		studentDAO.update(myStudent);

		// Vérifier
		System.out.println("Updated student: " + studentDAO.findById(studentId));
	}

	// --- Exercice 5.6 + 5.7 : Delete ---
	private void deleteStudent(StudentDAO studentDAO) {
		// Récupérer le premier étudiant disponible (id dynamique)
		List<Student> all = studentDAO.findAll();
		if (all.isEmpty()) { System.out.println("Aucun étudiant. Lance d'abord: create"); return; }
		// 5.6 — delete par id
		int studentId = all.get(0).getId();
		System.out.println("Deleting student id: " + studentId);
		studentDAO.delete(studentId);
		System.out.println("After delete, findById(" + studentId + ") = " + studentDAO.findById(studentId));

		// 5.7 — deleteAll
		System.out.println("\nDeleting all students...");
		int numDeleted = studentDAO.deleteAll();
		System.out.println("Deleted row count: " + numDeleted);
	}
}
