package ru.gb.gbthymeleaf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.gb.gbapi.product.dto.ProductDto;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    @GetMapping
    public String getProductList(Model model) {
        model.addAttribute("products",
                List.of(ProductDto.builder().id(1L).title("milk").cost(BigDecimal.valueOf(33.44)).build(),
                        ProductDto.builder().id(2L).title("meat").cost(BigDecimal.valueOf(630.44)).build()));
        return "cart-product-list";
    }

    @GetMapping("/add/{productId}")
    public String addProduct(@PathVariable Long productId) {
        return "redirect:/cart";
    }

    @GetMapping("/delete")
    public String deleteById(@RequestParam(name = "id") Long id) {
        return "redirect:/cart";
    }
}
