package com.kunal.blog.application.controller;

import com.kunal.blog.application.entity.Post;
import com.kunal.blog.application.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/drafts")
public class DraftController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping
    public String getAllDrafts(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        String username = authentication.getName();

        List<Post> drafts;
        if (isAdmin) {
            drafts = postRepository.findByIsPublishedFalse();
        } else {
            drafts = postRepository.findByIsPublishedFalse(username);
        }
        model.addAttribute("drafts", drafts);
        return "drafts";
    }


    @GetMapping("/{draftId}")
    public String viewDraft(@PathVariable Long draftId, Model model) {
        // Retrieve the specific draft by ID from the repository
        Optional<Post> draftOptional = postRepository.findById(draftId);
        if (draftOptional.isPresent()) {
            Post draft = draftOptional.get();
            model.addAttribute("draft", draft);
            return "draft-details"; // Create a template for viewing a draft
        } else {
            return "error";
        }
    }

    @GetMapping("/{draftId}/edit")
    public String editDraft(@PathVariable Long draftId, Model model) {
        // Retrieve the specific draft by ID from the repository
        Optional<Post> draftOptional = postRepository.findById(draftId);
        if (draftOptional.isPresent()) {
            Post draft = draftOptional.get();
            model.addAttribute("draft", draft);
            return "edit-draft"; // Create a template for editing a draft
        } else {
            return "error";
        }
    }

    @PostMapping("/{draftId}/update")
    public String updateDraft(@PathVariable Long draftId, @ModelAttribute Post updatedDraft) {
        // Retrieve the specific draft by ID from the repository
        Optional<Post> draftOptional = postRepository.findById(draftId);
        if (draftOptional.isPresent()) {
            Post draft = draftOptional.get();
            // Update draft properties with the new values
            draft.setTitle(updatedDraft.getTitle());
            draft.setExcerpt(updatedDraft.getExcerpt());
            draft.setContent(updatedDraft.getContent());
            // Update other fields as needed
            postRepository.save(draft);
            return "redirect:/drafts"; // Redirect to the drafts listing
        } else {
            return "error";
        }
    }

    @PostMapping("/{draftId}/delete")
    public String deleteDraft(@PathVariable Long draftId) {
        // Delete the specific draft by ID from the repository
        postRepository.deleteById(draftId);
        return "redirect:/drafts"; // Redirect to the drafts listing
    }

    @PostMapping("/{draftId}/publish")
    public String publishDraft(@PathVariable Long draftId) {
        // Retrieve the specific draft by ID from the repository
        Optional<Post> draftOptional = postRepository.findById(draftId);
        if (draftOptional.isPresent()) {
            Post draft = draftOptional.get();
            // Set draft as published and update publishedAt
            draft.setPublished(true);
            draft.setPublishedAt(LocalDateTime.now());
            postRepository.save(draft);
            return "redirect:/posts"; // Redirect to the published posts listing
        } else {
            return "error";
        }
    }
}
