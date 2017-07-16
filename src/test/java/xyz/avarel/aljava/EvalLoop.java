package xyz.avarel.aljava;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import xyz.avarel.aljava.lexer.Lexer;
import xyz.avarel.aljava.parser.Parser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringJoiner;

public class EvalLoop {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("▶ ");
            String str = sc.nextLine();

            System.out.print("◀ ");

            if (str.contains("=")) {
                Equation expr = new Parser(new Lexer(str)).parseEquation();

                StringJoiner joiner = new StringJoiner(", ");
                for (Object ans : expr.solveFor("x")) {
                    joiner.add(ans.toString());
                }
                String result = joiner.toString();

                System.out.println(result);

                printTex(expr.toTex(), "result");
            } else {
                Expression expr = new Parser(new Lexer(str)).parse();

                System.out.println(expr.simplify());

                printTex(expr.toTex(), "result");
            }
        }
    }

    public static void printTex(String tex, String name) {
        TeXFormula fomule = new TeXFormula(tex);
        TeXIcon ti = fomule.createTeXIcon(TeXConstants.STYLE_DISPLAY, 40);
        BufferedImage b = new BufferedImage(ti.getIconWidth(), ti.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);

        Graphics2D graphics = b.createGraphics();

        graphics.setPaint(Color.WHITE);
        graphics.fillRect(0, 0, b.getWidth(), b.getHeight());

        ti.paintIcon(null, b.getGraphics(), 0, 0);

        try {
            File outputfile = new File(name + ".png");
            ImageIO.write(b, "png", outputfile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
