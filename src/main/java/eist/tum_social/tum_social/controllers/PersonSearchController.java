package eist.tum_social.tum_social.controllers;

import eist.tum_social.tum_social.datastorage.Storage;
import eist.tum_social.tum_social.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static eist.tum_social.tum_social.controllers.util.Similarity.levenshteinDistance;
import static eist.tum_social.tum_social.controllers.util.Util.getCurrentPerson;

@RestController
public class PersonSearchController {
    private final Storage storage;
    public PersonSearchController(@Autowired Storage storage) {
        this.storage = storage;
    }
    private int similarity(Person a, String searchText) {
        return Collections.min(
                List.of(
                        levenshteinDistance(a.getFirstname(), searchText),
                        levenshteinDistance(a.getLastname(), searchText),
                        levenshteinDistance(a.getTumId(), searchText)
                )
        );
    }

    @PostMapping("/searchPersons")
    public List<Map<String, String>> searchPersons(@RequestParam("searchText") String searchText) {

        if (searchText.isBlank()) {
            return new ArrayList<>();
        }

        List<Person> persons = storage.getPersons();

        Person currentPerson = getCurrentPerson();

        persons = persons.stream().filter(person ->
                !person.getTumId().equals(currentPerson.getTumId()) &&
                        !currentPerson.getFriends().contains(person) &&
                        !currentPerson.getOutgoingFriendRequests().contains(person)
        ).toList();

        persons = persons.stream().filter(person -> similarity(person, searchText) <= 5).toList();
        persons = persons.stream().sorted(Comparator.comparingInt((Person a) -> similarity(a, searchText))).toList();

        List<Map<String, String>> result = new ArrayList<>();
        for (Person person : persons) {
            result.add(Map.of(
                    "id", String.valueOf(person.getId()),
                    "tumId", person.getTumId(),
                    "firstname", person.getFirstname(),
                    "lastname", person.getLastname()
            ));
        }
        return result;
    }


}
