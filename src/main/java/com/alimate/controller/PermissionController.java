package com.alimate.controller;

import com.alimate.dto.PermForm;
import com.alimate.model.Permission;
import com.alimate.service.PermissionNotFoundException;
import com.alimate.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import static java.util.Objects.isNull;

@Controller
@RequestMapping(value = "/perms")
public class PermissionController {
    @Autowired private PermissionService permissionService;

    @RequestMapping
    public String showAll(Model model,
                        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                        @RequestParam(value = "size", required = false, defaultValue = "5") int size) {
        page = page - 1;
        if (page < 0) page = 0;
        if (size <= 0) size = 5;

        model.addAttribute("page", permissionService.getPermissions(page, size));

        return "perms/show-all";
    }

    @RequestMapping("{id}")
    public String showOne(@PathVariable("id") long id, Model model) {
        if (id < 0)
            throw new PermissionNotFoundException();

        model.addAttribute("perm", permissionService.getPermission(id));

        return "perms/show-one";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String  showAddPermForm(Model model) {
        model.addAttribute("permForm", new PermForm());
        model.addAttribute("title", "Add Permission");

        return "perms/form";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddPerm(@ModelAttribute("permForm") @Valid PermForm form,
                                 BindingResult result,
                                 Model model) {
        boolean isSucceed = false;
        if (!result.hasErrors()) {
            if (validateName(form.getName(), result)) {
                try {
                    permissionService.create(toPermission(form));
                    isSucceed = true;
                } catch (Exception e) {
                    result.reject("errors.perm.genericError");
                    e.printStackTrace();
                }
            }
        }

        model.addAttribute("isSucceed", isSucceed);
        model.addAttribute("title", "Add Permission");
        if (isSucceed) model.addAttribute("permForm", new PermForm());

        return "perms/form";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String showEditPermForm(@PathVariable("id") long id, Model model) {
        if (id < 0)
            throw new PermissionNotFoundException();

        Permission permission = permissionService.getPermission(id);
        if (isNull(permission))
            throw new PermissionNotFoundException();

        PermForm perm = new PermForm();
        perm.setName(permission.getName());
        model.addAttribute("permForm", perm);
        model.addAttribute("title", "Edit Permission");

        return "perms/form";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
    public String processEditPerm(@ModelAttribute("permForm") @Valid PermForm form,
                                  @PathVariable("id") long id,
                                BindingResult result,
                                Model model) {
        if (id < 0)
            throw new PermissionNotFoundException();

        Permission permission = permissionService.getPermission(id);
        if (isNull(permission))
            throw new PermissionNotFoundException();

        boolean isSucceed = false;
        if (!result.hasErrors()) {
            if (validateName(form.getName(), result, permission.getName())) {
                try {
                    Permission update = toPermission(form);
                    update.setAssignable(permission.isAssignable());
                    update.setId(permission.getId());
                    permissionService.update(update);
                    isSucceed = true;
                } catch (Exception e) {
                    result.reject("errors.perm.genericError");
                    e.printStackTrace();
                }
            }
        }

        model.addAttribute("isSucceed", isSucceed);
        model.addAttribute("title", "Edit Permission");
        if (isSucceed) model.addAttribute("permForm", new PermForm());

        return "perms/form";
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.POST)
    public String removePerm(@PathVariable("id") long id, RedirectAttributes attributes) {
        if (id < 0)
            throw new PermissionNotFoundException();

        Permission permission = permissionService.getPermission(id);
        if (isNull(permission))
            throw new PermissionNotFoundException();

        try {
            permissionService.delete(permission);
            attributes.addFlashAttribute("message", "permission successfully removed");
            return "redirect:/perms";
        } catch (Exception e) {
            attributes.addFlashAttribute("message", "Something went wrong");
            e.printStackTrace();
            return "redirect:/perms/" + permission.getId();
        }
    }

    private boolean validateName(String name, BindingResult result) {
        return validateName(name, result, null);
    }

    private boolean validateName(String name, BindingResult result, String oldName) {
        if (isNull(name) || name.trim().isEmpty()) {
            result.rejectValue("name", "field.required");
            return false;
        }

        if (isNull(oldName) || !name.equals(oldName)) {
            if (permissionService.exists(name)) {
                result.rejectValue("name", "errors.perm.alreadyExist",
                        new Object[]{name}, "Permission already exist");
                return false;
            }
        }

        return true;
    }

    private Permission toPermission(PermForm form) {
        Permission permission = new Permission();
        permission.setName(form.getName());
        permission.setAssignable(true);

        return permission;
    }
}