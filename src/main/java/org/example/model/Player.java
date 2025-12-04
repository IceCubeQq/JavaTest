package org.example.model;

public record Player (
    String name,
    String team,
    Position position,
    String nationality,
    String agency,
    int transferCost,
    int goals,
    int redCards
){}
