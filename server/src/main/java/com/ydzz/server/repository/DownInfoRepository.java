package com.ydzz.server.repository;

import com.ydzz.server.entity.DownInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DownInfoRepository extends JpaRepository<DownInfo, Long> {

    List<DownInfo> findAllByOrderBySortAsc();
}