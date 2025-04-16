package com.ylli.api.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ylli
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExampleInfo {
    public String serialNo;

    public String key;

    public String val;
}
