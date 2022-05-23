package eist.tum_social.tum_social.model;

import java.util.Date;
import java.util.List;

public class Comment {

    private String text;
    private Person author;
    private Date timestamp;
    private List<Comment> replies;

}
