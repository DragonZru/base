package com.ylli.api.example;

import com.google.common.base.Strings;
import com.ylli.api.common.exception.GenericException;
import com.ylli.api.example.model.ConfigModel;
import com.ylli.api.example.service.ConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/config")
public class ConfigController {

    ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @PostMapping
    public void create(@RequestBody ConfigModel config) {
        if (Strings.isNullOrEmpty(config.name)) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "name not be null");
        }
        configService.create(config.name, config.value, config.desc);
    }

    @GetMapping
    public Object get(@RequestParam(required = false) String name) {
        return configService.get(name);
    }

    @PutMapping
    public Object update(@RequestBody ConfigModel config) {
        return configService.update(config);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        configService.delete(id);
    }
}
