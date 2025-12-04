package br.com.truta.models;

import com.fasterxml.jackson.databind.JsonNode;

public record PartnerDTO(
    String tradingName,
    String ownerName,
    String document,
    JsonNode address,
    JsonNode coverageArea
) {}