package org.example;

import java.awt.*;
import java.awt.image.MultiResolutionImage;


public class GetImage {
    // awt kütüphanesiyle mevcut ekranın boyutlarını alırız
    // düsük çözünürlüklü ekran görüntüsü ki ekran 1920x1080 olmasına rağmen 1600x900 gibi bir görüntü verir bulanık cıkar(laptopta)
    // BufferedImage downscaled = robot.createScreenCapture(rectangle);
    private Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    private MultiResolutionImage multiResCapture;
    private Robot robot;
    public void Clear(){
        multiResCapture=null;
        robot=null;
    }

    public Image Start() {
        // ekran görüntüsü almak için kullanılan robot nesnesini oluştururuz
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        // robot ile ekran boyutunu verip mevcut çözünürlüklerin hepsini alırız
        multiResCapture = robot.createMultiResolutionScreenCapture(rectangle);
        robot=null;
        // aldıgımız çözünürlük listeisindeki en sondaki yani en yüksek çözünürlüğe sahip olanı alırız
        return multiResCapture.getResolutionVariants().get(multiResCapture.getResolutionVariants().size() - 1);
    }
}
