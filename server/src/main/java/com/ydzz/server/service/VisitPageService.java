package com.ydzz.server.service;

import com.ydzz.server.dto.VisitPageDTO;

public interface VisitPageService {

    VisitPageDTO recordVisit(String ip);
}