package jp.mincra.ezsvg;

import jp.mincra.ezsvg.elements.Circle;
import jp.mincra.ezsvg.elements.Path;
import jp.mincra.ezsvg.elements.SvgObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class SvgFactory {
    private static SvgParser svgParser;

    /**
     * Convert simplified SVG file to SvgObject.
     * example of simplified svg file) As you see, <svg/> does not contain <g/>.
     *<svg
     *    width="26.458334mm"
     *    height="26.458334mm">
     *   <circle
     *           style="fill:none;fill-opacity:1;stroke:#000000;stroke-width:0.129954;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1"
     *           cx="13.229167"
     *           cy="13.229167"
     *           r="13.164189" />
     *   <path
     *           style="fill:none;stroke:#000000;stroke-width:0.128678;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1"
     *           d="M 13.233958,2.5083099 L 6.9184415,21.902415 L 23.427371,9.9244218 L 3.0308783,9.9111168 L 19.524168,21.910637 Z"
     *   />
     *   <circle
     *           style="fill:none;fill-opacity:1;stroke:#000000;stroke-width:0.10836;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1"
     *           cx="13.229167"
     *           cy="13.229167"
     *           r="10.976695" />
     * </svg>
     * @param xmlFile XML File that you want to load.
     */
    public static SvgObject fromXML(File xmlFile)  {
        // Load document and get <svg/> element.
        Document document = getDocument(xmlFile);
        Element svgElement = document.getDocumentElement();

        // Load width and height.
        if (svgParser == null) svgParser = new SvgParser();
        float width = svgParser.parseSizeAsMM(svgElement.getAttribute("width"));
        float height = svgParser.parseSizeAsMM(svgElement.getAttribute("height"));

        // Get <circle/>, <path/> etc
        NodeList paths = svgElement.getElementsByTagName("path");
        NodeList circles = svgElement.getElementsByTagName("circle");
        NodeList svgNodes = joinNodeList(paths, circles);

        SvgObject svgObject = new SvgObject(width, height);

        for (int i = 0; i < svgNodes.getLength(); i++) {
            Element svgNode = (Element) svgNodes.item(i);
            String nodeName = svgNode.getNodeName();
            // Load style and get color.
            Map<String, String> style = svgParser.parseStyle(svgNode.getAttribute("style"));
            Color strokeColor = Color.decode(style.get("stroke"));

            switch (nodeName) {
                case "circle" -> {
                    float cx = Float.parseFloat(svgNode.getAttribute("cx"));
                    float cy = Float.parseFloat(svgNode.getAttribute("cy"));
                    float r = Float.parseFloat(svgNode.getAttribute("r"));
                    svgObject.addSvgElement(new Circle(strokeColor, cx, cy, r));
                }
                case "path" -> {
                    String d = svgNode.getAttribute("d");
                    Path path = new Path(strokeColor, svgParser.parsePathD(d));
                    svgObject.addSvgElement(path);
                }
                default -> System.out.println("Only <circle/>, <path/> are available.");
            }
        }

        return svgObject;
    }

    private static Document getDocument(File xmlFile) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // DocumentBuilderのインスタンスを取得する
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        // DocumentBuilderにXMLを読み込ませ、Documentを作る
        Document document = null;
        try {
            document = builder.parse(xmlFile);
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
        return document;
    }

    private static NodeList joinNodeList(final NodeList... lists) {

        int count = 0;
        for (NodeList list : lists) {
            count += list.getLength();
        }
        final int length = count;

        Node[] joined = new Node[length];
        int outputIndex = 0;
        for (NodeList list : lists) {
            for (int i = 0, n = list.getLength(); i < n; i++) {
                joined[outputIndex++] = list.item(i);
            }
        }
        class JoinedNodeList implements NodeList {
            public int getLength() {
                return length;
            }

            public Node item(int index) {
                return joined[index];
            }
        }

        return new JoinedNodeList();
    }
}
