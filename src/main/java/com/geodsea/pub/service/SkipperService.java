package com.geodsea.pub.service;

import com.geodsea.pub.repository.LicensorRepository;
import com.geodsea.pub.repository.ParticipantGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Service
@Transactional
public class SkipperService extends BaseService {

    private final Logger log = LoggerFactory.getLogger(SkipperService.class);

}
