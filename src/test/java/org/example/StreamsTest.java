package org.example;

import org.example.model.Player;
import org.example.model.Position;
import org.example.resolver.Streams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class StreamsTest {

    @Mock
    private org.example.parser.CsvParser mockParser;
    private static Stream<Arguments> provideMaxDefenderGoalsCountTestData() {
        return Stream.of(
                Arguments.of(List.of(), 0),
                Arguments.of(
                        List.of(new Player("Иванов Иван", "Бульдоги",
                                Position.DEFENDER, "русский", "Ночь", 1000,
                                10, 3)),
                        10
                ),
                Arguments.of(
                        List.of(new Player("Иванов Иван", "Бульдоги",
                                Position.FORWARD, "русский", "Ночь", 1000,
                                10, 3)),
                        0
                ),
                Arguments.of(
                        List.of(
                                new Player("Иванов Иван", "Бульдоги",
                                        Position.DEFENDER, "русский", "Ночь", 1000,
                                        10, 3),
                                new Player("Сергей Петров", "Корги",
                                        Position.DEFENDER, "русский", "День", 1500,
                                        15, 7)
                        ),
                        15
                ),
                Arguments.of(
                        List.of(new Player("Иванов Иван", "Бульдоги",
                                Position.DEFENDER, "русский", "Ночь", 1000,
                                0, 3)),
                        0
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideMaxDefenderGoalsCountTestData")
    void testGetMaxDefenderGoalsCount(List<Player> players, int expected) {
        var streams = new Streams(players);
        assertEquals(expected, streams.getMaxDefenderGoalsCount());
    }

    private static Stream<Arguments> provideCountWithoutAgencyTestData() {
        return Stream.of(
                Arguments.of(List.of(), 0),
                Arguments.of(
                        List.of(
                                new Player("Иванов Иван", "Бульдоги",
                                        Position.FORWARD, "русский", "Агентство1", 1000,
                                        10, 3),
                                new Player("Сергей Петров", "Корги",
                                        Position.DEFENDER, "русский", "Агентство2", 1500,
                                        15, 7)
                        ),
                        0
                ),
                Arguments.of(
                        List.of(
                                new Player("Иванов Иван", "Бульдоги",
                                        Position.FORWARD, "русский", "", 1000,
                                        10, 3),
                                new Player("Сергей Петров", "Корги",
                                        Position.DEFENDER, "русский", null, 1500,
                                        15, 7),
                                new Player("Адам Смит", "Пудели",
                                        Position.GOALKEEPER, "американец", "Агентство", 2000,
                                        2, 2),
                                new Player("Клинт", "Найт",
                                        Position.MIDFIELD, "француз", "", 3000,
                                        39, 13)
                        ),
                        3
                ),
                Arguments.of(
                        List.of(
                                new Player("Иванов Иван", "Бульдоги",
                                        Position.FORWARD, "русский", null, 1000,
                                        10, 3),
                                new Player("Сергей Петров", "Корги",
                                        Position.DEFENDER, "русский", "", 1500,
                                        15, 7)
                        ),
                        2
                ),
                Arguments.of(
                        List.of(
                                new Player("Иванов Иван", "Бульдоги",
                                        Position.FORWARD, "русский", "Агентство", 1000,
                                        10, 3),
                                new Player("Петр Петров", "Львы",
                                        Position.MIDFIELD, "русский", null, 1200,
                                        8, 5)
                        ),
                        1
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideCountWithoutAgencyTestData")
    void testGetCountWithoutAgency(List<Player> players, int expected) {
        var streams = new Streams(players);
        assertEquals(expected, streams.getCountWithoutAgency());
    }

    private static Stream<Arguments> provideExpensiveGermanPlayerPositionTestData() {
        return Stream.of(
                Arguments.of(List.of(), null),
                Arguments.of(
                        List.of(
                                new Player("Иванов Иван", "Бульдоги",
                                        Position.FORWARD, "Россия", "Агентство", 1000,
                                        10, 3),
                                new Player("Адам Смит", "Пудели",
                                        Position.GOALKEEPER, "Англия", "Агентство", 2000,
                                        2, 2)
                        ),
                        null
                ),
                Arguments.of(
                        List.of(
                                new Player("Мануэль Нойер", "Бавария",
                                        Position.GOALKEEPER, "Germany", "Агентство1", 5000,
                                        0, 1)
                        ),
                        "Вратарь"
                ),
                Arguments.of(
                        List.of(
                                new Player("Игрок1", "Команда1",
                                        Position.FORWARD, "Germany", "Агентство", 3000,
                                        10, 2),
                                new Player("Игрок2", "Команда2",
                                        Position.DEFENDER, "Germany", "Агентство", 5000,
                                        5, 1),
                                new Player("Игрок3", "Команда3",
                                        Position.MIDFIELD, "Germany", "Агентство", 4000,
                                        8, 0)
                        ),
                        "Защитник"
                ),
                Arguments.of(
                        List.of(
                                new Player("Игрок1", "Команда1",
                                        Position.FORWARD, "Germany", "Агентство", 5000,
                                        10, 2),
                                new Player("Игрок2", "Команда2",
                                        Position.DEFENDER, "Germany", "Агентство", 5000,
                                        5, 1)
                        ),
                        "Нападающий"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideExpensiveGermanPlayerPositionTestData")
    void testGetTheExpensiveGermanPlayerPosition(List<Player> players, String expected) {
        var streams = new Streams(players);
        assertEquals(expected, streams.getTheExpensiveGermanPlayerPosition());
    }

    @Test
    void testGetPlayersByPosition() {
        List<Player> players = List.of(
                new Player("Иванов Иван", "Бульдоги",
                        Position.FORWARD, "русский", "Агентство", 1000,
                        10, 3),
                new Player("Сергей Петров", "Корги",
                        Position.DEFENDER, "русский", "Агентство", 1500,
                        15, 7),
                new Player("Адам Смит", "Пудели",
                        Position.GOALKEEPER, "американец", "Агентство", 2000,
                        2, 2),
                new Player("Клинт", "Бульдоги",
                        Position.MIDFIELD, "француз", "Агентство", 3000,
                        39, 13),
                new Player("Петр Петров", "Львы",
                        Position.FORWARD, "русский", "Агентство", 1200,
                        8, 5)
        );

        var streams = new Streams(players);
        Map<Position, List<String>> result = streams.getPlayersByPosition();

        assertEquals(4, result.size());
        assertTrue(result.containsKey(Position.FORWARD));
        assertTrue(result.containsKey(Position.DEFENDER));
        assertTrue(result.containsKey(Position.GOALKEEPER));
        assertTrue(result.containsKey(Position.MIDFIELD));

        assertEquals(2, result.get(Position.FORWARD).size());
        assertEquals(List.of("Иванов Иван", "Петр Петров"), result.get(Position.FORWARD));
        assertEquals(List.of("Сергей Петров"), result.get(Position.DEFENDER));
        assertEquals(List.of("Адам Смит"), result.get(Position.GOALKEEPER));
        assertEquals(List.of("Клинт"), result.get(Position.MIDFIELD));
    }

    @Test
    void testGetPlayersByPosition_EmptyList() {
        var streams = new Streams(List.of());
        Map<Position, List<String>> result = streams.getPlayersByPosition();
        assertTrue(result.isEmpty());
    }

    private static Stream<Arguments> provideGetTeamsTestData() {
        return Stream.of(
                Arguments.of(List.of(), Set.of()),
                Arguments.of(
                        List.of(
                                new Player("Иванов Иван", "Бульдоги",
                                        Position.FORWARD, "русский", "Ночь", 1000,
                                        10, 3),
                                new Player("Сергей Петров", "Бульдоги",
                                        Position.DEFENDER, "русский", "День", 1500,
                                        15, 7)
                        ),
                        Set.of("Бульдоги")
                ),
                Arguments.of(
                        List.of(
                                new Player("Иванов Иван", "Бульдоги",
                                        Position.FORWARD, "русский", "Ночь", 1000,
                                        10, 3),
                                new Player("Сергей Петров", "Корги",
                                        Position.DEFENDER, "русский", "День", 1500,
                                        15, 7),
                                new Player("Адам Смит", "Пудели",
                                        Position.GOALKEEPER, "американец", "Полдень", 2000,
                                        2, 2),
                                new Player("Клинт", "Бульдоги",
                                        Position.MIDFIELD, "француз", "Ночь", 3000,
                                        39, 13)
                        ),
                        Set.of("Бульдоги", "Корги", "Пудели")
                ),
                Arguments.of(
                        List.of(
                                new Player("Иванов Иван", "Бульдоги",
                                        Position.FORWARD, "русский", "Ночь", 1000,
                                        10, 3),
                                new Player("Сергей Петров", "Бульдоги",
                                        Position.DEFENDER, "русский", "День", 1500,
                                        15, 7),
                                new Player("Адам Смит", "Пудели",
                                        Position.GOALKEEPER, "американец", "Полдень", 2000,
                                        2, 2),
                                new Player("Клинт", "Пудели",
                                        Position.MIDFIELD, "француз", "Ночь", 3000,
                                        39, 13)
                        ),
                        Set.of("Бульдоги", "Пудели")
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideGetTeamsTestData")
    void testGetTeams(List<Player> players, Set<String> expected) {
        var streams = new Streams(players);
        Set<String> result = streams.getTeams();
        assertEquals(expected, result);
    }

    @Test
    void testGetTop5TeamsByGoalsCount() {
        List<Player> players = List.of(
                new Player("Игрок1", "КомандаA", Position.FORWARD, "RU", "Агент", 1000, 10, 0),
                new Player("Игрок2", "КомандаA", Position.FORWARD, "RU", "Агент", 1000, 15, 0),
                new Player("Игрок3", "КомандаB", Position.FORWARD, "RU", "Агент", 1000, 20, 0),
                new Player("Игрок4", "КомандаB", Position.FORWARD, "RU", "Агент", 1000, 5, 0),
                new Player("Игрок5", "КомандаC", Position.FORWARD, "RU", "Агент", 1000, 30, 0),
                new Player("Игрок6", "КомандаD", Position.FORWARD, "RU", "Агент", 1000, 8, 0),
                new Player("Игрок7", "КомандаE", Position.FORWARD, "RU", "Агент", 1000, 12, 0),
                new Player("Игрок8", "КомандаF", Position.FORWARD, "RU", "Агент", 1000, 3, 0)
        );

        var streams = new Streams(players);
        Map<String, Integer> result = streams.getTop5TeamsByGoalsCount();

        assertEquals(5, result.size());
        var entries = new ArrayList<>(result.entrySet());
        assertEquals("КомандаC", entries.get(0).getKey());
        assertEquals(30, entries.get(0).getValue());

        assertEquals("КомандаA", entries.get(1).getKey());
        assertEquals(25, entries.get(1).getValue());

        assertEquals("КомандаB", entries.get(2).getKey());
        assertEquals(25, entries.get(2).getValue());

        assertEquals("КомандаE", entries.get(3).getKey());
        assertEquals(12, entries.get(3).getValue());

        assertEquals("КомандаD", entries.get(4).getKey());
        assertEquals(8, entries.get(4).getValue());
    }


    @Test
    void testGetAgencyWithMinPlayersCount() {
        List<Player> players = List.of(
                new Player("Игрок1", "Команда1", Position.FORWARD, "RU", "Агентство1", 1000, 10, 0),
                new Player("Игрок2", "Команда2", Position.FORWARD, "RU", "Агентство1", 1000, 15, 0),
                new Player("Игрок3", "Команда3", Position.FORWARD, "RU", "Агентство2", 1000, 20, 0),
                new Player("Игрок4", "Команда4", Position.FORWARD, "RU", "Агентство2", 1000, 5, 0),
                new Player("Игрок5", "Команда5", Position.FORWARD, "RU", "Агентство2", 1000, 30, 0),
                new Player("Игрок6", "Команда6", Position.FORWARD, "RU", "Агентство3", 1000, 8, 0),
                new Player("Игрок7", "Команда7", Position.FORWARD, "RU", "Агентство4", 1000, 12, 0)
        );
        var streams = new Streams(players);
        String result = streams.getAgencyWithMinPlayersCount();
        assertEquals("Агентство3", result);
    }


    @Test
    void testGetAgencyWithMinPlayersCount_EmptyList() {
        var streams = new Streams(List.of());
        String result = streams.getAgencyWithMinPlayersCount();
        assertNull(result);
    }

    @Test
    void testGetTheRudestTeam() {
        List<Player> players = List.of(
                new Player("Игрок1", "КомандаA", Position.FORWARD, "RU", "Агент", 1000, 10, 2),
                new Player("Игрок2", "КомандаA", Position.FORWARD, "RU", "Агент", 1000, 15, 1),
                new Player("Игрок3", "КомандаB", Position.FORWARD, "RU", "Агент", 1000, 20, 3),
                new Player("Игрок4", "КомандаB", Position.FORWARD, "RU", "Агент", 1000, 5, 1),
                new Player("Игрок5", "КомандаC", Position.FORWARD, "RU", "Агент", 1000, 30, 0)
        );

        var streams = new Streams(players);
        String result = streams.getTheRudestTeam();
        assertEquals("КомандаB", result);
    }

    @Test
    void testConstructorWithMockParser() {
        var streams = new Streams(mockParser);
        assertNotNull(streams);
        assertNotNull(streams.getPlayers());
        assertTrue(streams.getPlayers().isEmpty());
    }

    @Test
    void testSetPlayers() {
        var streams = new Streams();
        assertNotNull(streams.getPlayers());
        assertTrue(streams.getPlayers().isEmpty());

        List<Player> players = List.of(
                new Player("Игрок1", "Команда1", Position.FORWARD, "RU", "Агент", 1000, 10, 0)
        );
        streams.setPlayers(players);
        assertEquals(players, streams.getPlayers());
    }


    @Test
    void testAllMethodsWithNullPlayers() {
        var streams = new Streams();
        try {
            Field playersField = Streams.class.getDeclaredField("players");
            playersField.setAccessible(true);
            playersField.set(streams, null);
        } catch (Exception e) {
            fail("Не удалось");
        }

        assertNull(streams.getPlayers());
        assertEquals(0, streams.getCountWithoutAgency());
        assertEquals(0, streams.getMaxDefenderGoalsCount());
        assertNull(streams.getTheExpensiveGermanPlayerPosition());
        assertTrue(streams.getPlayersByPosition().isEmpty());
        assertTrue(streams.getTeams().isEmpty());
        assertTrue(streams.getTop5TeamsByGoalsCount().isEmpty());
        assertNull(streams.getAgencyWithMinPlayersCount());
        assertEquals("", streams.getTheRudestTeam());
    }

    @Test
    void testGetAgencyWithMinPlayersCount_AllNullAndEmptyAgencies() {
        List<Player> players = List.of(
                new Player("Игрок1", "Команда1", Position.FORWARD, "RU", null, 1000, 10, 0),
                new Player("Игрок2", "Команда2", Position.FORWARD, "RU", "", 1000, 15, 0),
                new Player("Игрок3", "Команда3", Position.FORWARD, "RU", null, 1000, 20, 0),
                new Player("Игрок4", "Команда4", Position.FORWARD, "RU", "", 1000, 5, 0)
        );

        var streams = new Streams(players);
        String result = streams.getAgencyWithMinPlayersCount();
        assertNull(result);
    }


    @Test
    void testGetTheExpensiveGermanPlayerPosition_WithZeroTransferCost() {
        List<Player> players = List.of(
                new Player("Игрок1", "Команда1", Position.FORWARD, "Germany", "Агентство", 0, 10, 0),
                new Player("Игрок2", "Команда2", Position.DEFENDER, "Germany", "Агентство", 0, 5, 0),
                new Player("Игрок3", "Команда3", Position.MIDFIELD, "Germany", "Агентство", 1000, 8, 0)
        );

        var streams = new Streams(players);
        String result = streams.getTheExpensiveGermanPlayerPosition();
        assertEquals("Полузащитник", result);
    }
}