package ru.practicum.ewm.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Integer> {

    @Query("select c from Compilation c where c.pinned = ?1")
    Collection<Compilation> findByPinned(Boolean pinned);
}
