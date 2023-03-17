import jp.mincra.ezsvg.SvgFactory;
import jp.mincra.ezsvg.elements.SvgObject;

import java.nio.file.Paths;

public class SvgTest {
    public SvgObject getSvgObject(String path) {
        return SvgFactory.fromXML(Paths.get(path).toFile());
    }
}
