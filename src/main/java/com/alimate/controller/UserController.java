package com.alimate.controller;

import com.alimate.dto.UserForm;
import com.alimate.model.Role;
import com.alimate.model.User;
import com.alimate.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired private UserService userService;
    @Autowired private RoleService roleService;

    @RequestMapping
    public String showAll(Model model,
                          @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                          @RequestParam(value = "size", required = false, defaultValue = "5") int size) {
        page = page - 1;
        if (page < 0) page = 0;
        if (size <= 0) size = 5;

        Page<User> users = userService.getAll(page, size);
        model.addAttribute("page", users);

        return "users/show-all";
    }

    @RequestMapping("/{id}")
    public String showOneUSer(@PathVariable("id") long id, Model model) {
        if (id < 0)
            throw new UserNotFoundException();

        User user = userService.getUser(id);
        model.addAttribute("user", user);

        return "users/show-one";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String showAddUserForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        model.addAttribute("title", "Add User");
        model.addAttribute("roles", roleService.getAllAssignableRoles());
        return "users/form";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddUser(@ModelAttribute("userForm") @Valid UserForm form,
                                 BindingResult result,
                                 Model model) {
        boolean isSucceed = false;
        if (!result.hasErrors()) {
            if (validate(form, result)) {
                try {
                    userService.create(toUser(form), form.getPassword());
                    isSucceed = true;
                } catch (UserWithMutexRolesException e) {
                    result.rejectValue("roles", "errors.users.roles.mutex",
                            new Object[] {e.getRole(), e.getOther()}, "errors.user.genericError");
                    e.printStackTrace();
                } catch (Exception e) {
                    result.reject("errors.user.genericError");
                    e.printStackTrace();
                }
            }
        }
        model.addAttribute("isSucceed", isSucceed);
        model.addAttribute("title", "Add User");
        model.addAttribute("roles", roleService.getAllAssignableRoles());
        if (isSucceed) model.addAttribute("userForm", new UserForm());

        return "users/form";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String showEditForm(@PathVariable("id") long id, Model model) {
        User user = userService.getUser(id);

        model.addAttribute("userForm", toUserForm(user));
        model.addAttribute("roles", roleService.getAllAssignableRoles());
        model.addAttribute("isEditForm", true);
        model.addAttribute("title", "Edit User");

        return "users/form";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
    public String processEditForm(@ModelAttribute("userForm") @Valid UserForm form,
                                @PathVariable("id") long id,
                                BindingResult result,
                                Model model) {
        User user = userService.getUser(form.getUsername());
        if (!user.getId().equals(id))
            throw new UserNotFoundException();

        boolean isSucceed = false;
        if (!result.hasErrors()) {
            if (validate(form, result, true)) {
                try {
                    User update = toUser(form);
                    update.setId(user.getId());
                    userService.update(update, form.getPassword());
                    isSucceed = true;
                } catch (UserWithMutexRolesException e) {
                    result.rejectValue("roles", "errors.users.roles.mutex",
                            new Object[] {e.getRole(), e.getOther()}, "errors.user.genericError");
                    e.printStackTrace();
                } catch (Exception e) {
                    result.reject("errors.user.genericError");
                    e.printStackTrace();
                }
            }
        }

        model.addAttribute("isSucceed", isSucceed);
        model.addAttribute("title", "Edit User");
        model.addAttribute("roles", roleService.getAllAssignableRoles());
        if (isSucceed) model.addAttribute("userForm", new UserForm());
        else model.addAttribute("isEditForm", true);

        return "users/form";
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.POST)
    public String removeUser(@PathVariable("id") long id, RedirectAttributes attributes) {
        try {
            userService.delete(id);
            attributes.addFlashAttribute("message", "requested user successfully deleted");
            return "redirect:/users";
        } catch (Exception e) {
            attributes.addFlashAttribute("message", "user deletion was unsuccessful");
            e.printStackTrace();
            return "redirect:/users/" + id;
        }
    }

    private User toUser(UserForm form) {
        User user = new User();
        user.setUsername(form.getUsername());
        user.setFirstName(form.getFirstName());
        user.setLastName(form.getLastName());
        user.setEmail(form.getEmail());
        user.setEnabled(true);

        Predicate<String> isEmpty = String::isEmpty;
        user.setRoles(form.getRoles().stream()
                .filter(Objects::nonNull)
                .filter(isEmpty.negate())
                .map(roleService::getRole)
                .collect(toSet()));


        return user;
    }

    private UserForm toUserForm(User user) {
        UserForm form = new UserForm();
        form.setUsername(user.getUsername());
        form.setEmail(user.getEmail());
        form.setFirstName(user.getFirstName());
        form.setLastName(user.getLastName());
        form.setRoles(user.getRoles().stream().map(Role::getName).collect(toSet()));

        return form;
    }

    private boolean validate(UserForm form, BindingResult result) {
        return validate(form, result, false);
    }

    private boolean validate(UserForm form, BindingResult result, boolean isEditForm) {
        if (!isEditForm) {
            if (!validateUsername(form.getUsername(), result)) return false;
            if (!validateEmail(form.getEmail(), result)) return false;
        }

        if (!validatePasswords(form.getPassword(), form.getPasswordRetype(), result, isEditForm))
            return false;

        if (!validateRoles(form.getRoles(), result)) return false;

        return true;
    }

    private boolean validateRoles(Set<String> roleNames, BindingResult result) {
        for (String name : roleNames) {
            if (nonNull(name) && !name.trim().isEmpty()) {
                if (!roleService.exists(name)) {
                    result.rejectValue("roles", "errors.role.nonExistentRole",
                            new Object[]{name}, "Role does not exist");
                    return false;
                }
            }
        }

        return true;
    }

    private boolean validatePasswords(String password, String retype,
                                      BindingResult result, boolean isEditForm) {
        if (!isEditForm) {
            boolean arePasswordsAreBlank = isNull(password) || isNull(retype)
                    || password.trim().isEmpty() || retype.trim().isEmpty();
            if (arePasswordsAreBlank) {
                result.rejectValue("password", "errors.notNull");
                result.rejectValue("passwordRetype", "errors.notNull");
                return false;
            }
        } else {
            if (isNull(password) && isNull(retype)) return true;
            if (password.trim().isEmpty() && retype.trim().isEmpty()) return true;
        }

        if (!password.equals(retype)) {
            result.rejectValue("password", "errors.user.password.mismatch");
            result.rejectValue("passwordRetype", "errors.user.password.mismatch");
            return false;
        }

        return true;
    }

    private boolean validateEmail(String email, BindingResult result) {
        if (isNull(email) || email.trim().isEmpty()) {
            result.rejectValue("email", "errors.notNull");
            return false;
        }

        if (userService.isEmailRegistered(email)) {
            result.rejectValue("email", "errors.user.email.alreadyExists",
                    new Object[]{email}, "Email already exists");
            return false;
        }

        return true;
    }

    private boolean validateUsername(String username, BindingResult result) {
        if (isNull(username) || username.trim().isEmpty()) {
            result.rejectValue("username", "errors.notNull");
            return false;
        }

        if (userService.exists(username)) {
            result.rejectValue("username", "errors.user.username.alreadyExists", new Object[]{username},
                    "Username already exists");
            return false;
        }

        return true;
    }
}