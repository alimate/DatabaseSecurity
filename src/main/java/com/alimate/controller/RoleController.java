package com.alimate.controller;

import com.alimate.dto.RoleForm;
import com.alimate.model.Permission;
import com.alimate.model.Role;
import com.alimate.service.MutexConflictWithRoleHierarchyException;
import com.alimate.service.PermissionService;
import com.alimate.service.RoleNotFoundException;
import com.alimate.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Controller
@RequestMapping("/roles")
public class RoleController {
    @Autowired private RoleService roleService;
    @Autowired private PermissionService permissionService;

    @RequestMapping
    public String showRoles(Model model,
                            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                            @RequestParam(value = "size", required = false, defaultValue = "5") int size)   {
        page = page - 1;
        if (page < 0) page = 0;
        if (size <= 0) size = 5;

        Page<Role> result = roleService.getAll(page, size);
        model.addAttribute("page", result);

        return "roles/show-all";
    }

    @RequestMapping(value = "/{id}")
    public String showRole(Model model, @PathVariable("id") long id) {
        if (id < 0)
            throw new RoleNotFoundException();

        model.addAttribute("role", roleService.getRole(id));
        return "roles/show-one";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String showAddRoleForm(Model model) {
        model.addAttribute("form", new RoleForm());
        model.addAttribute("title", "Add Role");
        populateRoles(model);
        populatePerms(model);
        return "roles/form";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddRole(@ModelAttribute("form") @Valid RoleForm form,
                                 BindingResult result,
                                 Model model) {
        boolean isSucceed1 = false;
        if (!result.hasErrors()) {
            if (isValid(form, null, result)) {
                try {
                    roleService.create(toRole(form));
                    isSucceed1 = true;
                } catch (MutexConflictWithRoleHierarchyException e) {
                    result.rejectValue("mutex",
                            "errors.role.mutexConflictWithParent",
                            new Object[]{e.getConflictingRole()},
                            "One of roles conflicts with role hierarchy");
                    e.printStackTrace();
                } catch (Exception e) {
                    result.reject("errors.role.genericError");
                    e.printStackTrace();
                }
            }
        }
        boolean isSucceed = isSucceed1;

        model.addAttribute("isSucceed", isSucceed);
        model.addAttribute("title", "Add Role");
        if (isSucceed) model.addAttribute("form", new RoleForm());
        populateRoles(model);
        populatePerms(model);

        return "roles/form";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String showEditForm(Model model, @PathVariable("id") long id) {
        if (id < 0)
            throw new RoleNotFoundException();

        Role role = roleService.getRole(id);
        RoleForm form = new RoleForm();
        form.setName(role.getName());
        form.setParent(nonNull(role.getParent()) ? role.getParent().getName() : null);
        form.setMutex(role.getMutexRoles().stream().map(Role::getName).collect(toList()));
        form.setPermissions(role.getPermissions().stream().map(Permission::getName).collect(toList()));

        model.addAttribute("form", form);
        model.addAttribute("title", "Edit Role");
        model.addAttribute("roles", roleService.getAllAssignableRoles()
                .stream().filter(r -> !role.equals(r)).map(Role::getName).collect(toList()));
        populatePerms(model);

        return "roles/form";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
    public String processEditForm(@ModelAttribute("form") @Valid RoleForm form,
                                  @PathVariable("id") long id,
                                  Model model,
                                  BindingResult result) {
        if (id < 0)
            throw new RoleNotFoundException();

        Role role = roleService.getRole(id);
        boolean isSucceed = false;
        if (!result.hasErrors()) {
            if (isValid(form, role.getName(), result)) {
                try {
                    Role update = toRole(form);
                    update.setId(role.getId());
                    roleService.update(update);
                    isSucceed = true;
                } catch (MutexConflictWithRoleHierarchyException e) {
                    result.rejectValue("mutex",
                            "errors.role.mutexConflictWithParent",
                            new Object[]{e.getConflictingRole()},
                            "One of roles conflicts with role hierarchy");
                    e.printStackTrace();
                } catch (Exception e) {
                    result.reject("errors.role.genericError");
                    e.printStackTrace();
                }
            }
        }

        model.addAttribute("isSucceed", isSucceed);
        model.addAttribute("title", "Edit Role");
        populatePerms(model);
        model.addAttribute("roles", roleService.getAllAssignableRoles()
                .stream().filter(r -> !role.equals(r)).map(Role::getName).collect(toList()));
        return "roles/form";
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.POST)
    public String removeRole(@PathVariable("id") long id, RedirectAttributes attributes) {
        if (id < 0)
            throw new RoleNotFoundException();

        Role role = roleService.getRole(id);
        try {
            roleService.remove(role);
            attributes.addFlashAttribute("message", "successfully deleted");
            return "redirect:/roles";
        } catch (Exception e) {
            attributes.addFlashAttribute("message", "attempt was unsuccessful");
            e.printStackTrace();
            return "redirect:/roles/" + role.getId();
        }
    }

    private boolean isValid(RoleForm form, String roleName, BindingResult result) {
        if (!validateRoleName(form.getName(), roleName, result)) return false;
        if (!validateParentRole(form.getParent(), result)) return false;
        if (!validateMutexRoles(form.getMutex(), result)) return false;
        if (!validatePermissions(form.getPermissions(), result)) return false;

        return true;
    }

    private boolean validatePermissions(List<String> perms, BindingResult result) {
        for (String name : perms) {
            boolean notNullAndEmpty = nonNull(name) && !name.trim().isEmpty();
            if (notNullAndEmpty && !permissionService.exists(name)) {
                result.rejectValue("permissions", "errors.perm.nonExistentPerm", new Object[]{name},
                        "Permission does not exist");
                return false;
            }
        }

        return true;
    }

    private boolean validateMutexRoles(List<String> roles, BindingResult result) {
        for (String name : roles) {
            boolean notNullAndEmpty = nonNull(name) && !name.trim().isEmpty();
            if (notNullAndEmpty && !roleService.exists(name)) {
                result.rejectValue("mutex", "errors.role.nonExistentRole", new Object[]{name},
                        "Role does not exist");
                return false;
            }
        }

        return true;
    }

    private boolean validateParentRole(String parentName, BindingResult result) {
        if (isNull(parentName) || parentName.trim().isEmpty()) return true;

        if (!roleService.exists(parentName)) {
            result.rejectValue("parent", "errors.role.nonExistentRole", new Object[]{parentName},
                    "Role does not exist");
            return false;
        }

        return true;
    }

    private boolean validateRoleName(String name, String oldName, BindingResult result) {
        if (isNull(name) || name.trim().isEmpty()) {
            result.rejectValue("name", "errors.notNull");
            return false;
        }

        if (roleService.exists(name)) {
            if (!name.equals(oldName)) {
                result.rejectValue("name", "errors.role.alreadyExist", new Object[]{name},
                        "Role already exist");
                return false;
            }
        }

        return true;
    }

    private Role toRole(RoleForm form) {
        Role role = new Role();
        role.setName(form.getName());

        boolean hasParent = nonNull(form.getParent())
                && !form.getParent().trim().isEmpty();
        if (hasParent)
            role.setParent(roleService.getRole(form.getParent()));

        Predicate<String> isNotNull = Objects::nonNull;
        Predicate<String> isEmpty = String::isEmpty;

        role.setMutexRoles(form.getMutex().stream()
                .filter(isNotNull.and(isEmpty.negate()))
                .map(roleService::getRole)
                .collect(toSet()));

        role.setPermissions(form.getPermissions().stream()
                .filter(isNotNull.and(isEmpty.negate()))
                .map(permissionService::getPermission)
                .collect(toSet()));

        role.setAssignable(true);

        return role;
    }

    private void populateRoles(Model model) {
        model.addAttribute("roles", roleService.getAllAssignableRoles()
                .stream().map(Role::getName).collect(toList()));
    }

    private void populatePerms(Model model) {
        model.addAttribute("perms", permissionService.getAllAssignablePermissions()
                .stream().map(Permission::getName).collect(toList()));
    }
}