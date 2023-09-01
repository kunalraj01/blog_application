package com.kunal.blog.application.controller;
import com.kunal.blog.application.entity.Comment;
import com.kunal.blog.application.entity.Post;
import com.kunal.blog.application.entity.Tag;
import com.kunal.blog.application.repository.PostRepository;
import com.kunal.blog.application.repository.CommentRepository;
import com.kunal.blog.application.repository.TagRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;



import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TagRepository tagRepository;

    @GetMapping
    @Secured({"ROLE_ADMIN", "ROLE_AUTHOR"})
    public String getAllPosts(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "authors", required = false) List<String> authors,
            @RequestParam(name = "tags", required = false) List<String> tags,
            Model model
    ) {
        Pageable pageable;
        Sort.Direction sortDirection = "asc".equals(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        pageable = PageRequest.of(page, size, Sort.by(sortDirection, "publishedAt"));

        List<String> allAuthors = postRepository.findAllAuthors();
        List<String> allTags = tagRepository.findAllTagNames();

        Page<Post> postsPage = postRepository.getPosts(
                (tags == null || tags.isEmpty()) ? allTags : tags,
                (authors == null || authors.isEmpty()) ? allAuthors : authors,
                search, pageable);

        List<Post> posts = postsPage.getContent();


        model.addAttribute("posts", posts);
        model.addAttribute("allAuthors", allAuthors);
        model.addAttribute("allTags", allTags);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postsPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("postsPage", postsPage);
        model.addAttribute("search", search);
        model.addAttribute("authors", authors);
        model.addAttribute("tags", tags);

        return "posts";
    }




    @GetMapping("/{postId}")
    public String getPostDetails(@PathVariable Long postId, Model model) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            List<Comment> comments = post.getComments();
            List<Tag> tags = post.getTags(); // Get the tags associated with the post
            model.addAttribute("post", post);
            model.addAttribute("tags", tags); // Add the tags to the model
            return "post-details";
        } else {
            return "error";
        }
    }


    @PostMapping("/create")
    public String createPost(@ModelAttribute Post post,
                             @RequestParam(name = "tagIds", required = false) List<Long> tagIds,
                             @RequestParam(name = "newTag", required = false) String newTag,
                             @RequestParam(name = "action") String action) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().stream().findFirst().get().getAuthority();

        if ("ROLE_ADMIN".equals(role)) {
            // If the user is an AUTHOR but not the post's author, throw an error or handle as needed.
            if (tagIds != null) {
                List<Tag> tags = tagRepository.findAllById(tagIds);
                post.setTags(tags);
            }

            if (newTag != null && !newTag.isEmpty()) {
                Tag tag = new Tag();
                tag.setName(newTag);
                tagRepository.save(tag);
                post.getTags().add(tag); // Add the new tag to the post's tag list
            }

            LocalDateTime now = LocalDateTime.now();
            post.setCreatedAt(now);
            post.setUpdatedAt(now);

            if ("publish".equals(action)) {
                post.setPublished(true);
                post.setPublishedAt(now);
            } else if ("draft".equals(action)) {
                post.setPublished(false);
                post.setPublishedAt(null); // Optionally set publishedAt to null for drafts
            }

            postRepository.save(post);
            return "redirect:/posts";
        }else if ("ROLE_AUTHOR".equals(role)) {
            // Author users can create posts with author check
            authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

              post.setAuthor(username);
              if (!username.equals(post.getAuthor())) {
                return "error"; // Handle unauthorized access
            }
            if (tagIds != null) {
                List<Tag> tags = tagRepository.findAllById(tagIds);
                post.setTags(tags);
            }

            if (newTag != null && !newTag.isEmpty()) {
                Tag tag = new Tag();
                tag.setName(newTag);
                tagRepository.save(tag);
                post.getTags().add(tag); // Add the new tag to the post's tag list
            }

            LocalDateTime now = LocalDateTime.now();
            post.setCreatedAt(now);
            post.setUpdatedAt(now);

            if ("publish".equals(action)) {
                post.setPublished(true);
                post.setPublishedAt(now);
            } else if ("draft".equals(action)) {
                post.setPublished(false);
                post.setPublishedAt(null); // Optionally set publishedAt to null for drafts
            }

            postRepository.save(post);
            return "redirect:/posts";

        }

        return "error";
    }




    @GetMapping("/new")
    public String showCreateForm(Model model) {
        List<Tag> allTags = tagRepository.findAll();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        model.addAttribute("allTags", allTags);
        model.addAttribute("post", new Post());
        model.addAttribute("loggedInUsername", username); // Pass the logged-in username to the template
        return "create-post";
    }


    @GetMapping("/{postId}/edit")
    public String showEditForm(@PathVariable Long postId, Model model) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            List<Tag> allTags = tagRepository.findAll();
            model.addAttribute("allTags", allTags);
            model.addAttribute("post", post);
            return "update-post";
        } else {
            return "error";
        }
    }


    @PostMapping("/{postId}/edit")
    public String updatePost(
            @PathVariable Long postId,
            @ModelAttribute Post updatedPost,
            @RequestParam List<Long> tagIds,
            @RequestParam(name = "newTagName", required = false) String newTagName,
            @RequestParam(name = "deleteTagIds", required = false) List<Long> deleteTagIds
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream().findFirst().get().getAuthority();

        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();

            if ("ROLE_ADMIN".equals(role) || ("ROLE_AUTHOR".equals(role) && username.equals(post.getAuthor()))) {
                // Admin users can update posts without author check
                // Author users can update their own posts with author check

                // Check if the user has the ROLE_AUTHOR role and is the author of the post

                if ("ROLE_AUTHOR".equals(role) && !username.equals(post.getAuthor())) {

                    return "error";
                }

                // Update existing tags
                List<Tag> tags = tagRepository.findAllById(tagIds);
                post.setTags(tags);

                // Add new tag
                if (newTagName != null && !newTagName.isEmpty()) {
                    Tag newTag = new Tag();
                    newTag.setName(newTagName);
                    tagRepository.save(newTag);
                    post.getTags().add(newTag);
                }

                // Remove tags
                if (deleteTagIds != null && !deleteTagIds.isEmpty()) {
                    List<Tag> tagsToDelete = tagRepository.findAllById(deleteTagIds);
                    post.getTags().removeAll(tagsToDelete);

                    // Delete tags from the database
                    tagRepository.deleteAll(tagsToDelete);
                }

                post.setTitle(updatedPost.getTitle());
                post.setExcerpt(updatedPost.getExcerpt());
                post.setContent(updatedPost.getContent());
                // Update other fields as needed
                postRepository.save(post);
                return "redirect:/posts";
            } else {
                return "error";
            }
        } else {
            return "error";
        }
    }




    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream().findFirst().get().getAuthority();

        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();

            if ("ROLE_ADMIN".equals(role) || ("ROLE_AUTHOR".equals(role) && username.equals(post.getAuthor()))) {

                postRepository.deleteById(postId);
                return "redirect:/posts";
            } else {
                // Handle unauthorized access (e.g., show an error message or redirect to an error page)
                return "error";
            }
        } else {
            return "error"; // Handle post not found
        }
    }

    @PostMapping("/{postId}/publish")
    public String publishPost(@PathVariable Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().stream().findFirst().get().getAuthority();
        String username = authentication.getName();

        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();

            if ("ROLE_ADMIN".equals(role) || ("ROLE_AUTHOR".equals(role) && username.equals(post.getAuthor()))) {

                post.setPublished(true);
                post.setPublishedAt(LocalDateTime.now());
                postRepository.save(post);
                return "redirect:/posts";
            } else {
                return "error"; // Handle unauthorized access
            }
        } else {
            return "error";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/posts";
    }

}
