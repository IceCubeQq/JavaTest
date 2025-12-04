package org.example;

import org.example.chart.ChartMapper;
import org.example.model.Player;
import org.example.model.Position;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ChartMapperTest {

    @Test
    void mapNationalityDataToChart_EmptyList_ReturnsEmptyDataset() {
        List<Player> players = List.of();
        PieDataset dataset = ChartMapper.mapNationalityDataToChart(players);
        assertNotNull(dataset);
        assertTrue(dataset instanceof DefaultPieDataset);
        assertEquals(0, dataset.getItemCount());
    }

    @Test
    void mapNationalityDataToChart_SinglePlayer_ReturnsDatasetWithOneEntry() {
        List<Player> players = List.of(
                new Player("Иванов Иван", "Бульдоги",
                        Position.FORWARD, "Russia", "Агентство1",
                        1000, 10, 3)
        );
        PieDataset dataset = ChartMapper.mapNationalityDataToChart(players);
        assertEquals(1, dataset.getItemCount());
        assertEquals(1L, dataset.getValue("Russia"));
    }

    @Test
    void mapNationalityDataToChart_MultiplePlayersDifferentNationalities_ReturnsCorrectDataset() {
        List<Player> players = List.of(
                new Player("Иванов Иван", "Бульдоги",
                        Position.FORWARD, "Russia", "Агентство1",
                        1000, 10, 3),
                new Player("John Smith", "Team USA",
                        Position.DEFENDER, "USA", "Agency2",
                        1500, 5, 1),
                new Player("Pierre Dupont", "Paris FC",
                        Position.MIDFIELD, "France", "Agence3",
                        2000, 8, 0),
                new Player("Hans Müller", "Bayern",
                        Position.GOALKEEPER, "Germany", "Agentur4",
                        3000, 0, 2)
        );
        PieDataset dataset = ChartMapper.mapNationalityDataToChart(players);
        assertEquals(4, dataset.getItemCount());
        assertEquals(1L, dataset.getValue("Russia"));
        assertEquals(1L, dataset.getValue("USA"));
        assertEquals(1L, dataset.getValue("France"));
        assertEquals(1L, dataset.getValue("Germany"));
    }
    @Test
    void mapNationalityDataToChart_MultiplePlayersSameNationality_ReturnsAggregatedCount() {
        List<Player> players = List.of(
                new Player("Иванов Иван", "Бульдоги",
                        Position.FORWARD, "Russia", "Агентство1",
                        1000, 10, 3),
                new Player("Петров Петр", "Медведи",
                        Position.DEFENDER, "Russia", "Агентство2",
                        1200, 5, 1),
                new Player("Сидоров Алексей", "Тигры",
                        Position.MIDFIELD, "Russia", "Агентство3",
                        1500, 8, 0),
                new Player("John Smith", "Team USA",
                        Position.FORWARD, "USA", "Agency4",
                        2000, 12, 2)
        );
        PieDataset dataset = ChartMapper.mapNationalityDataToChart(players);
        assertEquals(2, dataset.getItemCount());
        assertEquals(3L, dataset.getValue("Russia"));
        assertEquals(1L, dataset.getValue("USA"));
    }

    @Test
    void mapNationalityDataToChart_CaseSensitiveNationalities_TreatsAsDifferent() {
        List<Player> players = List.of(
                new Player("Игрок1", "Команда1",
                        Position.FORWARD, "RUSSIA", "Агент1",
                        1000, 10, 3),
                new Player("Игрок2", "Команда2",
                        Position.DEFENDER, "Russia", "Агент2",
                        1100, 5, 1),
                new Player("Игрок3", "Команда3",
                        Position.MIDFIELD, "russia", "Агент3",
                        1200, 8, 0)
        );
        PieDataset dataset = ChartMapper.mapNationalityDataToChart(players);
        assertEquals(3, dataset.getItemCount());
        assertEquals(1L, dataset.getValue("RUSSIA"));
        assertEquals(1L, dataset.getValue("Russia"));
        assertEquals(1L, dataset.getValue("russia"));
    }

    @Test
    void mapNationalityDataToChart_NullAndEmptyNationalities_HandlesCorrectly() {
        List<Player> players = List.of(
                new Player("Игрок1", "Команда1",
                        Position.FORWARD, null, "Агент1",
                        1000, 10, 3),
                new Player("Игрок2", "Команда2",
                        Position.DEFENDER, "", "Агент2",
                        1100, 5, 1),
                new Player("Игрок3", "Команда3",
                        Position.MIDFIELD, "Russia", "Агент3",
                        1200, 8, 0)
        );
        PieDataset dataset = ChartMapper.mapNationalityDataToChart(players);
        assertEquals(2, dataset.getItemCount());
        assertEquals(1L, dataset.getValue("Russia"));
        assertEquals(2L, dataset.getValue(""));
    }

    @Test
    void mapNationalityDataToChart_LargeDataset_ReturnsCorrectDataset() {
        List<Player> players = List.of(
                new Player("Игрок1", "Команда1", Position.FORWARD, "Russia", "Агент1", 1000, 10, 3),
                new Player("Игрок2", "Команда2", Position.DEFENDER, "Russia", "Агент2", 1100, 5, 1),
                new Player("Игрок3", "Команда3", Position.MIDFIELD, "Russia", "Агент3", 1200, 8, 0),
                new Player("Игрок4", "Команда4", Position.FORWARD, "USA", "Агент4", 1300, 12, 2),
                new Player("Игрок5", "Команда5", Position.DEFENDER, "USA", "Агент5", 1400, 6, 1),
                new Player("Игрок6", "Команда6", Position.MIDFIELD, "Germany", "Агент6", 1500, 9, 0),
                new Player("Игрок7", "Команда7", Position.GOALKEEPER, "Germany", "Агент7", 1600, 0, 2),
                new Player("Игрок8", "Команда8", Position.FORWARD, "Germany", "Агент8", 1700, 15, 3),
                new Player("Игрок9", "Команда9", Position.DEFENDER, "France", "Агент9", 1800, 4, 0),
                new Player("Игрок10", "Команда10", Position.MIDFIELD, "Japan", "Агент10", 1900, 7, 1)
        );
        PieDataset dataset = ChartMapper.mapNationalityDataToChart(players);
        assertEquals(5, dataset.getItemCount());
        assertEquals(3L, dataset.getValue("Russia"));
        assertEquals(2L, dataset.getValue("USA"));
        assertEquals(3L, dataset.getValue("Germany"));
        assertEquals(1L, dataset.getValue("France"));
        assertEquals(1L, dataset.getValue("Japan"));
    }

    private static Stream<Arguments> provideNationalityDataTestCases() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                new Player("A", "T1", Position.FORWARD, "Brazil", "Ag1", 1000, 10, 0),
                                new Player("B", "T2", Position.DEFENDER, "Brazil", "Ag2", 1100, 5, 1),
                                new Player("C", "T3", Position.MIDFIELD, "Argentina", "Ag3", 1200, 8, 0),
                                new Player("D", "T4", Position.FORWARD, "Brazil", "Ag4", 1300, 12, 2)
                        ),
                        new String[]{"Brazil", "Argentina"},
                        new long[]{3L, 1L}
                ),
                Arguments.of(
                        List.of(
                                new Player("A", "T1", Position.FORWARD, "Spain", "Ag1", 1000, 10, 0),
                                new Player("B", "T2", Position.DEFENDER, "Spain", "Ag2", 1100, 5, 1),
                                new Player("C", "T3", Position.MIDFIELD, "Spain", "Ag3", 1200, 8, 0)
                        ),
                        new String[]{"Spain"},
                        new long[]{3L}
                ),
                Arguments.of(
                        List.of(
                                new Player("A", "T1", Position.FORWARD, "Italy", "Ag1", 1000, 10, 0),
                                new Player("B", "T2", Position.DEFENDER, "Portugal", "Ag2", 1100, 5, 1),
                                new Player("C", "T3", Position.MIDFIELD, "Netherlands", "Ag3", 1200, 8, 0)
                        ),
                        new String[]{"Italy", "Portugal", "Netherlands"},
                        new long[]{1L, 1L, 1L}
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideNationalityDataTestCases")
    void mapNationalityDataToChart_ParameterizedTests(List<Player> players,
                                                      String[] expectedCountries,
                                                      long[] expectedCounts) {
        PieDataset dataset = ChartMapper.mapNationalityDataToChart(players);
        assertEquals(expectedCountries.length, dataset.getItemCount());

        for (int i = 0; i < expectedCountries.length; i++) {
            assertEquals(expectedCounts[i], dataset.getValue(expectedCountries[i]));
        }
    }

    @Test
    void mapNationalityDataToChart_DoesNotModifyOriginalList() {
        List<Player> originalPlayers = List.of(
                new Player("Иванов Иван", "Бульдоги",
                        Position.FORWARD, "Russia", "Агентство1",
                        1000, 10, 3),
                new Player("John Smith", "Team USA",
                        Position.DEFENDER, "USA", "Agency2",
                        1500, 5, 1)
        );

        List<Player> playersCopy = List.copyOf(originalPlayers);
        PieDataset dataset = ChartMapper.mapNationalityDataToChart(originalPlayers);
        assertEquals(playersCopy.size(), originalPlayers.size());
        assertEquals(playersCopy.get(0).name(), originalPlayers.get(0).name());
        assertEquals(playersCopy.get(1).name(), originalPlayers.get(1).name());
    }

    @Test
    void mapNationalityDataToChart_ReturnsCorrectType() {
        List<Player> players = List.of(
                new Player("Игрок1", "Команда1", Position.FORWARD, "Russia", "Агент1", 1000, 10, 3)
        );
        PieDataset dataset = ChartMapper.mapNationalityDataToChart(players);
        assertNotNull(dataset);
        assertTrue(dataset instanceof DefaultPieDataset,
                "Метод должен возвращать DefaultPieDataset");
    }

}

