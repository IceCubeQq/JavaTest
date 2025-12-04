package org.example.resolver;

import org.example.model.Player;
import org.example.model.Position;
import org.example.parser.CsvParser;

import java.util.*;

import java.util.stream.Collectors;

public class Streams implements IResolver {
    private List<Player> players;
    private CsvParser csvParser;

    public Streams() {
        this.csvParser = new CsvParser();
        this.players = new ArrayList<>();
    }
    public Streams(CsvParser mockParser) {
        this.csvParser = mockParser;
        this.players = new ArrayList<>();
    }

    public Streams(List<Player> players) {
        this.players = players;
        this.csvParser = null;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    @Override

    public int getCountWithoutAgency() {
        if (players == null) return 0;
        return (int) players.stream()
                .filter(player -> player.agency() == null || player.agency().isEmpty())
                .count();
    }

    @Override

    public int getMaxDefenderGoalsCount() {
        if (players == null) return 0;
        return players.stream()
                .filter(player -> player.position() == Position.DEFENDER)
                .mapToInt(Player::goals)
                .max()
                .orElse(0);

    }

    @Override
    public String getTheExpensiveGermanPlayerPosition() {
        if (players == null) return null;
        Player expensiveGerman = players.stream()
                .filter(player -> player.nationality().equals("Germany"))
                .max((player1, player2) -> player1.transferCost() - player2.transferCost())
                .orElse(null);

        if (expensiveGerman != null) {
            return switch (expensiveGerman.position()) {
                case GOALKEEPER -> "Вратарь";
                case DEFENDER -> "Защитник";
                case MIDFIELD -> "Полузащитник";
                case FORWARD -> "Нападающий";
            };
        }
        return null;
    }

    @Override
    public Map<Position, List<String>> getPlayersByPosition() {
        if (players == null) return new HashMap<>();
        return players.stream()
                .collect(Collectors.groupingBy(
                        Player::position,
                        Collectors.mapping(Player::name, Collectors.toList())
                ));
    }

    @Override
    public Set<String> getTeams() {
        if (players == null) return new HashSet<>();
        return players.stream()
                .map(Player::team)
                .collect(Collectors.toSet());
    }

    @Override
    public Map<String, Integer> getTop5TeamsByGoalsCount() {
        if (players == null) return new HashMap<>();
        return players.stream()
                .collect(Collectors.groupingBy(
                        Player::team,
                        Collectors.summingInt(Player::goals)
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    @Override
    public String getAgencyWithMinPlayersCount() {
        if (players == null || players.isEmpty()) {
            return null;
        }
        return players.stream()
                .filter(player -> player.agency() != null && !player.agency().isEmpty())
                .collect(Collectors.groupingBy(
                        Player::agency,
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    @Override
    public String getTheRudestTeam() {
        if (players == null) return "";
        return players.stream()
                .collect(Collectors.groupingBy(
                        Player::team,
                        Collectors.averagingDouble(Player::redCards)
                ))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
    }
}