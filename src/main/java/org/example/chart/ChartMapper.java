package org.example.chart;

import org.example.model.Player;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChartMapper {
    public static PieDataset mapNationalityDataToChart(List<Player> players) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        Map<String, Long> nationalityCount = players.stream()
                .collect(Collectors.groupingBy(
                        player -> player.nationality() == null ? "" : player.nationality(),
                        Collectors.counting()
                ));

        nationalityCount.forEach((nationality, count) -> {
            dataset.setValue(nationality, count);
        });

        return dataset;
    }
}