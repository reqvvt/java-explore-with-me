package ru.practicum.evm.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Integer> {
    Collection<Compilation> findByPinned(Boolean pinned);
}
