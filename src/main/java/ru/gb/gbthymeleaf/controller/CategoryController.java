package ru.gb.gbthymeleaf.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.gb.gbapi.category.api.CategoryGateway;
import ru.gb.gbapi.category.dto.CategoryDto;

@Controller
@RequestMapping("/category")
public class CategoryController {

    private final CategoryGateway categoryGateway;

    public CategoryController(
            @Qualifier("categoryExtGateway") CategoryGateway categoryGateway
    ) {
        this.categoryGateway = categoryGateway;
    }

    @GetMapping("/all")
    public String getCategoryList(Model model) {

        model.addAttribute("categories", categoryGateway.getCategoryList());
        return "category-list";
    }

    @GetMapping("/{categoryId}")
    public String info(Model model, @PathVariable(name = "categoryId") Long id) {
        CategoryDto categoryDto;
        if (id != null) {
            categoryDto = categoryGateway.getCategory(id).getBody();
        } else {
            return "redirect:/category/all";
        }
        model.addAttribute("category", categoryDto);
        return "category-info";
    }

    @GetMapping
    public String showForm(Model model, @RequestParam(name = "id", required = false) Long id) {
        CategoryDto categoryDto;

        if (id != null) {
            categoryDto = categoryGateway.getCategory(id).getBody();
        } else {
            categoryDto = new CategoryDto();
        }
        model.addAttribute("category", categoryDto);
        return "category-form";
    }

    @PostMapping
    public String saveCategory(CategoryDto categoryDto) {
        categoryGateway.handlePost(categoryDto);
        return "redirect:/category/all";
    }

    @GetMapping("/delete")
    public String deleteById(@RequestParam(name = "id") Long id) {
        categoryGateway.deleteById(id);
        return "redirect:/category/all";
    }

}
