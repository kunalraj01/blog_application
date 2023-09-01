package com.kunal.blog.application.controller;

import com.kunal.blog.application.entity.Comment;
import com.kunal.blog.application.entity.Post;
import com.kunal.blog.application.repository.CommentRepository;
import com.kunal.blog.application.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;


    @PostMapping("/add")
    public String addComment(@RequestParam String name,
                             @RequestParam String email, @RequestParam String comment, @RequestParam Long postId) {
        // Find the Post entity by its ID
        Post post = postRepository.findById(postId).orElse(null);
        // If the Post entity is found
        if (post != null) {
            // Create a new Comment entity
            Comment commentEntity = new Comment();
            commentEntity.setEmail(email);
            commentEntity.setName(name);
            commentEntity.setPost(post);
            commentEntity.setComment(comment);
            commentRepository.save(commentEntity);
        }
        return "redirect:/posts/" + postId;
    }



    @GetMapping("/{commentId}/update")
    public String showUpdateCommentForm(@PathVariable Long commentId, Model model) {
        Comment comment = commentRepository.findById(commentId).orElse(null);

        if (comment != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin || comment.getName().equals(username)) {
                model.addAttribute("comment", comment);
                return "update-comment";
            }
        }
        return "redirect:/"; // Handle unauthorized access
    }

    @PostMapping("/{commentId}/edit")
    public String updateComment(@PathVariable Long commentId,
                                @RequestParam String name, @RequestParam String email,
                                @RequestParam String comment) {
        Comment commentToUpdate = commentRepository.findById(commentId).orElse(null);
        if (commentToUpdate != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin || commentToUpdate.getName().equals(username)) {
                commentToUpdate.setName(name);
                commentToUpdate.setEmail(email);
                commentToUpdate.setComment(comment);
                commentRepository.save(commentToUpdate);
            }
        }
        return "redirect:/posts/" + commentToUpdate.getPost().getId();
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin || comment.getName().equals(username)) {
                Long postId = (long) comment.getPost().getId();
                commentRepository.delete(comment);
                return "redirect:/posts/" + postId; // Redirect to post details page
            }
        }
        return "redirect:/";
    }

}


