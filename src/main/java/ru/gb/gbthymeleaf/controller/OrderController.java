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
import ru.gb.gbapi.order.api.OrderGateway;
import ru.gb.gbapi.order.dto.OrderDto;
import ru.gb.gbapi.product.api.ProductGateway;
import ru.gb.gbapi.product.dto.ProductDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderGateway orderGateway;

    private final ProductGateway productGateway;

    public OrderController(
            @Qualifier("orderExtGateway") OrderGateway orderGateway,
            @Qualifier("productExtGateway") ProductGateway productGateway) {
        this.orderGateway = orderGateway;
        this.productGateway = productGateway;
    }

    @GetMapping("/all")
    public String getOrderList(Model model) {

        model.addAttribute("orders", orderGateway.getOrderList());
        return "order-list";
    }

    @GetMapping("/{orderId}")
    public String info(Model model, @PathVariable(name = "orderId") Long id) {
        OrderDto order;
        if (id != null) {
            order = orderGateway.getOrder(id).getBody();
        } else {
            return "redirect:/order/all";
        }
        model.addAttribute("order", order);
        return "order-info";
    }

    @GetMapping
    public String showForm(Model model, @RequestParam(name = "id", required = false) Long id) {
        OrderDto order;

        if (id != null) {
            order = orderGateway.getOrder(id).getBody();
        } else {
            order = new OrderDto();
        }
        model.addAttribute("order", order);
        List<Long> orderProductIds = order != null && order.getProducts() != null ?
                order.getProducts().stream()
                        .map(ProductDto::getId).collect(Collectors.toList()) : new ArrayList<>();
        model.addAttribute("orderProductIds", orderProductIds);
        model.addAttribute("allProducts", productGateway.getProductList());
        return "order-form";
    }

    @PostMapping
    public String saveOrder(@ModelAttribute OrderDto order) {
        if (!CollectionUtils.isEmpty(order.getProductsIdForAdding())) {
            List<ProductDto> products = order.getProductsIdForAdding().stream()
                    .map(productId -> ProductDto.builder().id(Long.parseLong(productId)).build())
                    .collect(Collectors.toList());
            order.setProducts(products);
        }
        order.setStatus("Saved");
        orderGateway.handlePost(order);

        return "redirect:/order/all";
    }

    @GetMapping("/delete")
    public String deleteById(@RequestParam(name = "id") Long id) {
        orderGateway.deleteById(id);
        return "redirect:/order/all";
    }

}
