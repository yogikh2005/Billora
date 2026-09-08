package com.gstbilling.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class UpdateWorkerBusinessesDTO {

    private Set<Long> businessIds = new HashSet<>();
}