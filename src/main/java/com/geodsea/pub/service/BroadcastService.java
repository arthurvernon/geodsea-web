package com.geodsea.pub.service;

import com.geodsea.common.dto.VesselLocationDTO;
import com.geodsea.pub.domain.LocationTime;
import com.geodsea.pub.domain.Monitor;
import com.geodsea.pub.domain.Vessel;
import com.geodsea.pub.web.rest.mapper.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Set;

/**
 * Service to broadcast location updates to clients.
 */
@Controller
public class BroadcastService {

    private static final Logger log = LoggerFactory.getLogger(BroadcastService.class);


    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public BroadcastService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    @SubscribeMapping("/topic/monitor")
    @SendToUser("/queue/position-updates")
    public VesselLocationDTO reportLocation(LocationTime locationTime, Principal principal) {
        return Mapper.vesselLocation(locationTime, null);
    }


    /**
     * Distribute the notification of a vessel's location to relevant monitors.
     *
     * @param monitors
     * @param locationTime
     * @param vessel
     */
    public void broadcastVesselLocation(Set<Monitor> monitors, LocationTime locationTime, Vessel vessel) {

        for (Monitor m : monitors) {
            messagingTemplate.convertAndSendToUser(m.getParticipant().getLogin(), "/queue/position-updates", Mapper.vesselLocation(locationTime, vessel));
        }
    }

}
