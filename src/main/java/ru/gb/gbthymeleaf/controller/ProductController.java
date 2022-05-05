package ru.gb.gbthymeleaf.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.gb.gbapi.category.api.CategoryGateway;
import ru.gb.gbapi.category.dto.CategoryDto;
import ru.gb.gbapi.manufacturer.api.ManufacturerGateway;
import ru.gb.gbapi.product.api.ProductGateway;
import ru.gb.gbapi.product.dto.ProductDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductGateway productGateway;

    private final CategoryGateway categoryGateway;

    private final ManufacturerGateway manufacturerGateway;

    public ProductController(
            @Qualifier("productExtGateway") ProductGateway productGateway,
            @Qualifier("categoryExtGateway") CategoryGateway categoryGateway,
            @Qualifier("manufacturerExtGateway") ManufacturerGateway manufacturerGateway
    ) {
        this.productGateway = productGateway;
        this.categoryGateway = categoryGateway;
        this.manufacturerGateway = manufacturerGateway;
    }

    @GetMapping("/all")
    public String getProductList(Model model) {

        model.addAttribute("products", productGateway.getProductList());
        return "product-list";
    }

    @GetMapping("/{productId}")
    public String info(Model model, @PathVariable(name = "productId") Long id) {
        ProductDto product;
        if (id != null) {
            product = productGateway.getProduct(id).getBody();
        } else {
            return "redirect:/product/all";
        }
        model.addAttribute("product", product);
        return "product-info";
    }

    @GetMapping
    public String showForm(Model model, @RequestParam(name = "id", required = false) Long id) {
        ProductDto product;

        if (id != null) {
            product = productGateway.getProduct(id).getBody();
        } else {
            product = new ProductDto();
        }
        model.addAttribute("product", product);
        List<Long> productCategoryIds = product.getCategories() != null ?
                product.getCategories().stream()
                        .map(CategoryDto::getId).collect(Collectors.toList()) : new ArrayList<>();
        model.addAttribute("productCategoryIds", productCategoryIds);
        model.addAttribute("allCategories", categoryGateway.getCategoryList());
        model.addAttribute("allManufactures", manufacturerGateway.getManufacturerList());
        return "product-form";
    }

    @PostMapping
    public String saveProduct(@ModelAttribute ProductDto product) {
        if (!CollectionUtils.isEmpty(product.getCategoriesIdForAdding())) {
            List<CategoryDto> categories = product.getCategoriesIdForAdding().stream()
                    .map(categoryId -> CategoryDto.builder().id(Long.parseLong(categoryId)).build())
                    .collect(Collectors.toList());
            product.setCategories(categories);
        }
        productGateway.handlePost(product);

        return "redirect:/product/all";
    }

    @GetMapping("/delete")
    public String deleteById(@RequestParam(name = "id") Long id) {
        productGateway.deleteById(id);
        return "redirect:/product/all";
    }

}
