package eist.tum_social.tum_social.controllers.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    public static String formatTimestamp(LocalDate date, LocalTime time) {
        String dateString = "";

        // TODO: fix midnight wrap around

        if (time.isAfter(LocalTime.now().minusHours(1))) {
            long minDelta = time.until(LocalTime.now(), ChronoUnit.MINUTES);
            if (minDelta <= 1) {
                dateString += "Gerade eben";
            } else {
                dateString += "Vor " + minDelta + " Minuten";
            }
        } else if (time.isAfter(LocalTime.now().minusHours(12))) {
            dateString += "Vor " + time.until(LocalTime.now(), ChronoUnit.HOURS) + " Stunden";
        } else {
            dateString += time.format(DateTimeFormatter.ofPattern("HH:mm"));
            if (date.isEqual(LocalDate.now())) {
                dateString += ", Heute";
            } else if (date.isEqual(LocalDate.now().minusDays(1))) {
                dateString += ", Gestern";
            } else {
                dateString += ", " + date.format(DateTimeFormatter.ofPattern("dd.MM."));
            }
            if (LocalDate.now().getYear() != date.getYear()) {
                dateString += date.format(DateTimeFormatter.ofPattern("yyyy"));
            }
        }

        return dateString;
    }

    public static void main(String[] args) {

    }
}
