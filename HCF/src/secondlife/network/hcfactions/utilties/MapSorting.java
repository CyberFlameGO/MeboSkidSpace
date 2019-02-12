package secondlife.network.hcfactions.utilties;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MapSorting {

    private static Function  EXTRACT_VALUE = new Function<Map.Entry<Object, Object>, Object>() {
        public Object apply(Map.Entry<Object, Object> input) {
            return (input == null) ? null : input.getValue();
        }
    };

	public static <T, V> List<Map.Entry<T, V>> sortedValues(Map<T, V> map, Comparator<V> valueComparator) {
        return Ordering.from((Comparator) valueComparator).onResultOf(extractValue()).sortedCopy(map.entrySet());
    }

	public static <T, V> Iterable<V> values(List<Map.Entry<T, V>> entryList) {
        return Iterables.transform(entryList, extractValue());
    }


	private static <T, V> Function<Map.Entry<T, V>, V> extractValue() {
        return MapSorting.EXTRACT_VALUE;
    }
}
