package com.ydzz.server.service;

import com.ydzz.server.dto.DownInfoDTO;
import com.ydzz.server.dto.DownInfoRequestDTO;

import java.util.List;

public interface DownInfoService {

    DownInfoDTO create(DownInfoDTO dto);

    DownInfoDTO update(DownInfoDTO dto);

    void delete(Long id);

    DownInfoDTO getById(Long id);

    List<DownInfoDTO> listAll();

    boolean recordDownload(DownInfoRequestDTO request);
}