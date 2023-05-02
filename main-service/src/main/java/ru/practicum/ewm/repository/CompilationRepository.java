package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query("select c " +
            "from Compilation c " +
            "where (?1 is null or c.pinned = ?1)"
    )
    Page<Compilation> getCompilations(Boolean pinned, Pageable page);
}