package dyomin.mikhail.vision.math.powerseries.util;

import java.util.*;
import java.util.stream.IntStream;

public class CompositionGenerator {
    private final Map<Integer, List<List<Integer>>> compositionsByLength;
    public final int n;

    public List<List<Integer>> getCompositions(int length){
        return Optional.ofNullable(compositionsByLength.get(length)).orElse(Collections.emptyList());
    }

    public CompositionGenerator(int n) {
        this.n =n;
        compositionsByLength = new HashMap<>(n);

        IntStream.range(1,n+1).forEach(i->compositionsByLength.put(i, new ArrayList<>()));

        ArrayList<ArrayList<Integer>> listOfCompositions = new ArrayList<>();
        ArrayList<Integer> composition = new ArrayList<>();
        composition.add(n);
        while (composition != null) {
            listOfCompositions.add(composition);
            composition = getComposition(composition, n);
        }

        listOfCompositions.forEach(list-> compositionsByLength.get(list.size()).add(list));
    }

    /**
     * Copied from Wikipedia:
     * https://ru.wikipedia.org/wiki/%D0%9A%D0%BE%D0%BC%D0%BF%D0%BE%D0%B7%D0%B8%D1%86%D0%B8%D1%8F_%D1%87%D0%B8%D1%81%D0%BB%D0%B0
     *
     * @param previousComposition composition on previous step
     * @param n number to find compositions of
     * @return next composition
     */
    private ArrayList<Integer> getComposition(ArrayList<Integer> previousComposition, int n) {
        ArrayList<Integer> currentComposition = new ArrayList<>(previousComposition);
        for (int i = currentComposition.size() - 1; i >= 0; i--) {
            if (currentComposition.get(i) != 1) {
                currentComposition.set(i, currentComposition.get(i) - 1);
                if (currentComposition.size() > i + 1) {
                    if (((currentComposition.size() - (i + 1)) > 1)) {
                        int sumOfOnes = 0;
                        for (int j = currentComposition.size() - 1; j >= i + 1; j--) {
                            sumOfOnes += currentComposition.get(j);
                            if (j != i + 1) currentComposition.remove(j);
                        }
                        currentComposition.set(i + 1, sumOfOnes + 1);
                    } else currentComposition.set(i + 1, currentComposition.get(i + 1) + 1);
                } else currentComposition.add(1);
                return currentComposition;
            }
        }
        return null;
    }

}
