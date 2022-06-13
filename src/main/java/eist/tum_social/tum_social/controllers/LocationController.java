package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.datastorage.util.Pair;
import eist.tum_social.tum_social.location.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class LocationController {

    @GetMapping("/getIframeUrl")
    public Map<String, String> getIframeUrl(
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "roomName", required = false) String roomName
    ) {
        Map<String, String> ret = new HashMap<>();

        if (location != null) {
            List<OpenStreetmapLocation> locations = OpenStreetMap.findLocation(location);
            if (locations.isEmpty()) {
                ret.put("iframeUrl", OpenStreetMap.getBaseUrl());
            } else {
                OpenStreetmapLocation loc = locations.get(0);
                System.out.println("found location: " + loc.getName());
                ret.put("iframeUrl", OpenStreetMap.createUrl(loc));
            }
        } else if (roomName != null) {
            List<Room> rooms = Navigatum.findRooms(roomName);
            if (rooms.isEmpty()) {
                ret.put("iframeUrl", OpenStreetMap.getBaseUrl());
            } else {
                Room room = rooms.get(0);
                System.out.println("found room: " + room.getName());
                ret.put("roomId", room.getRoomId());
                ret.put("iframeUrl", OpenStreetMap.createUrl(room));

                Pair<String, Marker> imageResult = Navigatum.getRoomImageData(room.getRoomId());
                if (imageResult != null) {
                    String imageUrl = imageResult.first();
                    Marker marker = imageResult.second();

                    ret.put("roomImageUrl", imageUrl);
                    ret.put("markerX", String.valueOf(marker.getX()));
                    ret.put("markerY", String.valueOf(marker.getY()));
                }
            }
        }

        System.out.println(">>>> iFrameUrl: "+ret.get("iframeUrl"));

        return ret;
    }

}
