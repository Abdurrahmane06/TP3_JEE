package com.tp3.cruddemo.dao;

import com.tp3.cruddemo.entity.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class StudentDAOImpl implements StudentDAO {

    private EntityManager entityManager;

    @Autowired
    public StudentDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // --- Exercice 5.1 : save (persist) ---
    @Override
    @Transactional
    public void save(Student theStudent) {
        entityManager.persist(theStudent);
    }

    // --- Exercice 5.2 : findById (find) ---
    @Override
    public Student findById(Integer id) {
        return entityManager.find(Student.class, id);
    }

    // --- Exercice 5.3 : findAll (JPQL) ---
    @Override
    public List<Student> findAll() {
        TypedQuery<Student> theQuery =
                entityManager.createQuery("FROM Student ORDER BY lastName", Student.class);
        return theQuery.getResultList();
    }

    // --- Exercice 5.4 : findByLastName (paramètre nommé) ---
    @Override
    public List<Student> findByLastName(String theLastName) {
        TypedQuery<Student> theQuery = entityManager.createQuery(
                "FROM Student WHERE lastName = :theData", Student.class);
        theQuery.setParameter("theData", theLastName);
        return theQuery.getResultList();
    }

    // --- Exercice 5.5 : update (merge) ---
    @Override
    @Transactional
    public void update(Student theStudent) {
        entityManager.merge(theStudent);
    }

    // --- Exercice 5.6 : delete (remove) ---
    @Override
    @Transactional
    public void delete(Integer id) {
        Student theStudent = entityManager.find(Student.class, id);
        if (theStudent != null) {
            entityManager.remove(theStudent);
        }
    }

    // --- Exercice 5.7 : deleteAll (bulk delete) ---
    @Override
    @Transactional
    public int deleteAll() {
        return entityManager.createQuery("DELETE FROM Student").executeUpdate();
    }
}
