package com.blog.blog.controllers.SocketController;

import com.blog.blog.DTO.CommentReqeust.CommentDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class LiveCommentController {

    @MessageMapping("/sendComment")
    @SendTo("/topic/comment")
    public CommentDTO sendComment(@RequestBody CommentDTO comment){
        return comment;
    }

}
