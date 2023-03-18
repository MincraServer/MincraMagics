import jp.mincra.ezsvg.SvgFactory;
import jp.mincra.ezsvg.element.SvgObject;

import java.nio.file.Paths;

public class SvgTest {
    public SvgObject getSvgObject(String path) {
        return SvgFactory.fromFile(Paths.get(path).toFile());
    }
}
