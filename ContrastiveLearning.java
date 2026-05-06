import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ContrastiveLearning {

    public static void main(String[] args) {
        try {

            String dogPath = "C:\\Users\\user\\Desktop\\0506\\05.jpg";
            String cat1Path = "C:\\Users\\user\\Desktop\\0506\\06.png";
            String cat2Path = "C:\\Users\\user\\Desktop\\0506\\07.jpg";

            double C = 3.0; // margin，可改 0.5、1.0、2.0 測試

            double[] dogFeature = getDifferentialFeature(dogPath);
            double[] cat1Feature = getDifferentialFeature(cat1Path);
            double[] cat2Feature = getDifferentialFeature(cat2Path);

            double dDogCat1 = euclideanDistance(dogFeature, cat1Feature) / 100.0;
            double dDogCat2 = euclideanDistance(dogFeature, cat2Feature) / 100.0;
            double dCat1Cat2 = euclideanDistance(cat1Feature, cat2Feature) / 100.0;

            // Positive Pair：同類，距離越小越好
            double positiveLoss = dCat1Cat2 * dCat1Cat2;

            // Negative Pair：不同類，距離太近才懲罰
            double negativeLossDogCat1 = Math.max(0, C - dDogCat1);
            double negativeLossDogCat2 = Math.max(0, C - dDogCat2);

            System.out.println("Assignment 1 - Contrastive Learning");
            System.out.println("----------------------------------");
            System.out.println("C = " + C);
            System.out.println();

            System.out.println("Positive Pair 同類：cat1 vs cat2");
            System.out.println("Distance = " + dCat1Cat2);
            System.out.println("Positive Loss = distance * distance = " + positiveLoss);

            if (dCat1Cat2 < 0.2) {
                System.out.println("結果：cat1 和 cat2 很相似，距離很小");
            } else {
                System.out.println("結果：cat1 和 cat2 不夠相似，距離偏大");
            }

            System.out.println();

            System.out.println("Negative Pair 不同類：dog vs cat1");
            System.out.println("Distance = " + dDogCat1);
            System.out.println("Negative Loss = max(0, C - distance) = " + negativeLossDogCat1);

            if (negativeLossDogCat1 > 0) {
                System.out.println("結果：dog 和 cat1 距離太近，需要拉遠");
            } else {
                System.out.println("結果：dog 和 cat1 距離夠遠，不需要懲罰");
            }

            System.out.println();

            System.out.println("Negative Pair 不同類：dog vs cat2");
            System.out.println("Distance = " + dDogCat2);
            System.out.println("Negative Loss = max(0, C - distance) = " + negativeLossDogCat2);

            if (negativeLossDogCat2 > 0) {
                System.out.println("結果：dog 和 cat2 距離太近，需要拉遠");
            } else {
                System.out.println("結果：dog 和 cat2 距離夠遠，不需要懲罰");
            }

            System.out.println();

            System.out.println("Similarity Result");
            System.out.println("------------------");

            if (dCat1Cat2 < dDogCat1 && dCat1Cat2 < dDogCat2) {
                System.out.println("cat1 和 cat2 最相似");
            } else if (dDogCat1 < dDogCat2) {
                System.out.println("dog 和 cat1 最相似");
            } else {
                System.out.println("dog 和 cat2 最相似");
            }

        } catch (Exception e) {
            System.out.println("程式發生錯誤");
            e.printStackTrace();
        }
    }

    public static double[] getDifferentialFeature(String path) throws Exception {

        BufferedImage img = ImageIO.read(new File(path));

        int width = img.getWidth();
        int height = img.getHeight();

        int[][] gray = new int[width][height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                Color c = new Color(img.getRGB(x, y));

                int g = (c.getRed() + c.getGreen() + c.getBlue()) / 3;

                gray[x][y] = g;
            }
        }

        double sumDx = 0;
        double sumDy = 0;
        double sumGradient = 0;

        int count = 0;

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {

                int dx = gray[x + 1][y] - gray[x - 1][y];
                int dy = gray[x][y + 1] - gray[x][y - 1];

                double gradient = Math.sqrt(dx * dx + dy * dy);

                sumDx += Math.abs(dx);
                sumDy += Math.abs(dy);
                sumGradient += gradient;

                count++;
            }
        }

        double avgDx = sumDx / count;
        double avgDy = sumDy / count;
        double avgGradient = sumGradient / count;

        return new double[] {
                avgDx,
                avgDy,
                avgGradient
        };
    }

    public static double euclideanDistance(double[] a, double[] b) {

        double sum = 0;

        for (int i = 0; i < a.length; i++) {

            double diff = a[i] - b[i];

            sum += diff * diff;
        }

        return Math.sqrt(sum);
    }
}