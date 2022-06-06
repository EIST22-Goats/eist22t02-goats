package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.location.OpenStreetMap;
import eist.tum_social.tum_social.location.OpenStreetmapLocation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LocationController {

    @GetMapping("/getIframeUrl")
    public String getIframeUrl(
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "roomName", required = false) String roomName
    ) {
        if (location != null) {
            OpenStreetmapLocation loc = OpenStreetMap.findLocation(location).get(0);
            return OpenStreetMap.createUrl(loc);
        } else if (roomName != null) {
            return "";
        } else {
            return "";
        }
    }

}
