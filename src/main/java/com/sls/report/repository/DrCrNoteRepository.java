package com.sls.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sls.report.entity.DrCrNote;

@Repository
public interface DrCrNoteRepository extends JpaRepository<DrCrNote, Long> {

}
