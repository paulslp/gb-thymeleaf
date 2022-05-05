package ru.gb.gbthymeleaf.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.gb.gbapi.manufacturer.api.ManufacturerGateway;
import ru.gb.gbapi.manufacturer.dto.ManufacturerDto;

@Controller
@RequestMapping("/manufacturer")
public class ManufacturerController {

    private final ManufacturerGateway manufacturerGateway;

    public ManufacturerController(
            @Qualifier("manufacturerExtGateway") ManufacturerGateway manufacturerGateway
    ) {
        this.manufacturerGateway = manufacturerGateway;
    }

    @GetMapping("/all")
    public String getManufacturerList(Model model) {

        model.addAttribute("manufacturers", manufacturerGateway.getManufacturerList());
        return "manufacturer-list";
    }

    @GetMapping("/{manufacturerId}")
    public String info(Model model, @PathVariable(name = "manufacturerId") Long id) {
        ManufacturerDto manufacturerDto;
        if (id != null) {
            manufacturerDto = manufacturerGateway.getManufacturer(id).getBody();
        } else {
            return "redirect:/manufacturer/all";
        }
        model.addAttribute("manufacturer", manufacturerDto);
        return "manufacturer-info";
    }

    @GetMapping
    public String showForm(Model model, @RequestParam(name = "id", required = false) Long id) {
        ManufacturerDto manufacturerDto;

        if (id != null) {
            manufacturerDto = manufacturerGateway.getManufacturer(id).getBody();
        } else {
            manufacturerDto = new ManufacturerDto();
        }
        model.addAttribute("manufacturer", manufacturerDto);
        return "manufacturer-form";
    }

    @PostMapping
    public String saveManufacturer(ManufacturerDto manufacturerDto) {
        manufacturerGateway.handlePost(manufacturerDto);
        return "redirect:/manufacturer/all";
    }

    @GetMapping("/delete")
    public String deleteById(@RequestParam(name = "id") Long id) {
        manufacturerGateway.deleteById(id);
        return "redirect:/manufacturer/all";
    }

}
