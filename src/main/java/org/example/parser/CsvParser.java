package org.example.parser;

import org.example.model.Player;
import org.example.model.Position;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CsvParser {
    public static List<Player> parseCsvToList(String pathString) throws IOException {
        return Files.readAllLines(Paths.get(pathString))
                .stream()
                .skip(1)
                .map(CsvParser::parsePlayerRow)
                .toList();
    }

    private static Player parsePlayerRow(String row) {
        var cells = row.split(";");
        return new Player(cells[0], cells[1], Position.valueOf(cells[3]), cells[4], cells[5],
                Integer.parseInt(cells[6]), Integer.parseInt(cells[8]), Integer.parseInt(cells[11]));
    }
}
