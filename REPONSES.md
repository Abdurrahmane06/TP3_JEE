# TP03 — Réponses aux questions de compréhension

## Question 1 — JPA vs Hibernate vs Spring Data JPA

| Couche             | Rôle                  | Description |
|--------------------|-----------------------|-------------|
| **JPA**            | **Spécification**     | C'est une spécification Java (API + annotations dans `jakarta.persistence.*`) qui définit comment mapper des objets Java sur des tables relationnelles. JPA ne contient aucun code d'implémentation. |
| **Hibernate**      | **Implémentation**    | C'est une implémentation concrète de la spécification JPA. Il fournit le code réel qui exécute les opérations `persist`, `find`, `merge`, `remove`, etc. C'est un ORM (Object-Relational Mapping). |
| **Spring Data JPA**| **Orchestration**     | C'est une couche d'abstraction de Spring qui simplifie l'utilisation de JPA/Hibernate. Il crée automatiquement le `DataSource`, l'`EntityManagerFactory`, le `TransactionManager`, et offre des repositories auto-générés. |

---

## Question 2 — Pourquoi `@Transactional` sur save/update/delete mais pas sur find ?

Les méthodes `save`, `update`, `delete` et `deleteAll` sont annotées `@Transactional` car elles **modifient** la base de données (INSERT, UPDATE, DELETE). Une transaction garantit :
- **L'atomicité** : soit toutes les modifications sont validées (commit), soit aucune (rollback en cas d'erreur).
- **La cohérence** des données.

Les méthodes `findById` et `findAll` sont de simples **lectures** (SELECT). Elles ne modifient pas la base, donc une transaction n'est pas nécessaire. Hibernate les exécute dans une transaction en lecture seule automatique.

---

## Question 3 — `persist(entity)` vs `merge(entity)`

| Méthode       | Pour quoi ?                                | Cas typique   |
|---------------|--------------------------------------------|---------------|
| `persist(e)`  | INSERT d'une **nouvelle** entité (sans id) | Création      |
| `merge(e)`    | UPDATE d'une entité **détachée** (avec id) → renvoie l'instance gérée | Mise à jour |

**Ce que renvoie `merge`** : l'objet **géré** (managed) par le contexte de persistance.
**L'objet passé en paramètre** reste **détaché** et n'est pas synchronisé avec la base :
```java
Student managed = entityManager.merge(detached);
// managed → géré (synchronisé avec la BDD)
// detached → reste détaché (modifications ultérieures ignorées)
```

---

## Question 4 — JPQL vs SQL

JPQL (Java Persistence Query Language) et SQL diffèrent fondamentalement :

| Aspect       | SQL                                  | JPQL                                  |
|--------------|--------------------------------------|---------------------------------------|
| Opère sur    | **Tables** et **colonnes**           | **Classes** Java et **champs**        |
| Exemple      | `SELECT * FROM student WHERE last_name = ?` | `FROM Student WHERE lastName = :theData` |
| Portable     | Dépend du SGBD                       | Indépendant du SGBD                   |

`FROM Student WHERE lastName = :theData` utilise le nom de la **classe** `Student` (pas la table `student`) et le **champ Java** `lastName` (pas la colonne `last_name`). Hibernate traduit automatiquement vers le SQL natif.

---

## Question 5 — Paramètre nommé vs concaténation

Un paramètre nommé (`:theData`) est **plus sûr** qu'une concaténation de chaînes car :

1. **Sécurité** : JPA/Hibernate **échappe automatiquement** la valeur → **pas d'injection SQL**.
   - Concaténation dangereuse : `"... WHERE lastName = '" + input + "'"` → un attaquant peut injecter `'; DROP TABLE student; --`
   - Paramètre nommé sûr : `"... WHERE lastName = :theData"` → la valeur est traitée comme une donnée, jamais comme du SQL.

2. **Performance** : les requêtes paramétrées sont mises en cache (prepared statements) et réutilisées.

3. **Lisibilité** : le code est plus propre et maintenable.

---

## Question 6 — `@Repository` : rôle au-delà de `@Component`

`@Repository` fait deux choses supplémentaires par rapport à `@Component` :

1. **Déclare un bean Spring** (comme `@Component`) pour l'injection de dépendances.
2. **Traduction d'exceptions** : Spring traduit automatiquement les exceptions de persistance spécifiques (Hibernate, JDBC) en exceptions génériques `DataAccessException` de Spring. Cela permet au code appelant de ne pas dépendre d'un ORM particulier.

---

## Question 7 — Pourquoi pas `entityManager.remove(new Student(5))` ?

`remove()` n'accepte qu'une entité **gérée** (managed), c'est-à-dire une entité qui est connue par le contexte de persistance d'Hibernate.

`new Student(5)` crée un objet **détaché** qui n'est pas géré par l'EntityManager. Hibernate ne sait pas qu'il correspond à une ligne en base.

**Ce qu'il faut faire à la place** :
```java
Student theStudent = entityManager.find(Student.class, 5); // 1. Récupérer (managed)
entityManager.remove(theStudent);                           // 2. Supprimer
```

Le `find()` ramène l'entité dans le contexte de persistance (état managed), et ensuite `remove()` peut la supprimer.

---

## Question 8 — Valeurs de `spring.jpa.hibernate.ddl-auto`

| Valeur          | Comportement |
|-----------------|-------------|
| `none`          | Ne fait rien. Défaut recommandé en **production**. |
| `validate`      | Vérifie la correspondance tables/entités au démarrage. N'écrit rien. **Échoue** si écart entre le modèle et la base. |
| `update`        | Ajoute les colonnes/tables manquantes. **Garde les données**. Ne supprime jamais de colonnes. |
| `create`        | `DROP` + `CREATE` au démarrage → **toutes les données sont perdues**. |
| `create-drop`   | Comme `create`, puis `DROP` à l'arrêt de l'application. Idéal pour les **tests**. |

**En production** : utiliser `none` ou `validate` + un outil de migration (Flyway / Liquibase).

**Pourquoi jamais `create` en prod** : car il supprime toutes les tables et les recrée à chaque démarrage, ce qui entraîne une **perte totale des données**.

**Recommandations** :
- **Développement** → `update` (ou `create-drop` pour les tests)
- **Production** → `none` ou `validate` + migrations
