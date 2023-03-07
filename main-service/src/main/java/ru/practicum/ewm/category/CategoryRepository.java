package ru.practicum.ewm.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("select (count(c) > 0) from Category c where c.name = ?1")
    boolean existsCategoryByName(String name);
}
