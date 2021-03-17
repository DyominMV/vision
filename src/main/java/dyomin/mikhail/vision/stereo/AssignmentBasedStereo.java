package dyomin.mikhail.vision.stereo;

import dyomin.mikhail.vision.filters.ImageFilter;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Disparity;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.List;

public class AssignmentBasedStereo<V extends Vector<V>> extends DsiBasedStereo<V> {
    public <U extends Vector<U>> AssignmentBasedStereo(
            ImageFilter<U, V> filter,
            ReadableImage<U> left,
            ReadableImage<U> right
    ) {
        super(filter, left, right);
    }

    @Override
    protected List<Disparity> getDisparities(List<V> leftSideRow, List<V> rightSideRow) {
        //TODO add solving
        return null;
    }
}
