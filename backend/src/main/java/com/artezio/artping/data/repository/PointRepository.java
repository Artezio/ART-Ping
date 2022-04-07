package com.artezio.artping.data.repository;

import com.artezio.artping.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Репозиторий для работы с геоданными
 */
public interface PointRepository extends JpaRepository<Point, UUID> {
}
