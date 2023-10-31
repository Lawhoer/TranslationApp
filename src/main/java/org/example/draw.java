package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.MultiResolutionImage;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.example.Main.getImage;

public class draw extends Application {

    private static volatile boolean javaFxLaunched = false;
    private static Image FullHD;
    private static java.awt.Image fullHD;
    private static Stage MainStage;
    private static Robot robot;
    private static MultiResolutionImage multiResCapture;
    private static BufferedImage bufferedFullHD;
    public static void Start() {
        FullHD=SwingFXUtils.toFXImage((java.awt.image.BufferedImage) getImage.Start(), null);
        myLaunch(draw.class);
    }
    private static void myLaunch(Class<? extends Application> applicationClass) {
        // Javafx başlatma rütieli olaki hali hazırda varsa yeni threat acıyor
        if (!javaFxLaunched) {
            Platform.setImplicitExit(false);
            new Thread(() -> Application.launch(applicationClass)).start();
            javaFxLaunched = true;
        } else {
            Platform.runLater(() -> {
                try {
                    Application application = applicationClass.newInstance();
                    MainStage.close();
                    FullHD=SwingFXUtils.toFXImage((java.awt.image.BufferedImage) getImage.Start(), null);
                    Stage primaryStage = new Stage();
                    primaryStage.setAlwaysOnTop(true);
                    primaryStage.toFront();
                    application.start(primaryStage);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
    private double width = 1;
    private double height = 1;
    private double minX = 1;
    private double minY = 1;
    private final Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    private final Canvas mainCanvas = new Canvas(screenBounds.getWidth(), screenBounds.getHeight());
    private final Canvas drawingCanvas = new Canvas(screenBounds.getWidth(), screenBounds.getHeight());
    public static void Clear(){
        // Kendimce optimizasyon
        robot = null;
        multiResCapture=null;
        fullHD=null;
        bufferedFullHD=null;
    }
    public static void runBoy() {
        MainStage.close();
    }
    @Override
    public void start(Stage primaryStage) throws InterruptedException, FileNotFoundException {

        // Stage > scene > canvas

        // PhaseOne
        // GetImage'dan aldıgımız ekran görüntüsünü scene icindeki 1.canvasa cizdiriyoruz
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();
        gc.drawImage(FullHD, 0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());

        // 1.canvasın üstüne 2. gri alanı transparan şeklinde ciziyoruz
        GraphicsContext drawingGC = drawingCanvas.getGraphicsContext2D();
        drawingGC.setFill(new Color(0.2, 0.2, 0.2, 0.4)); // Yarı transparan gri renk
        drawingGC.fillRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());

        // iki canvası birleştirip scene'e sunuyoruz
        StackPane rootCanvas = new StackPane(mainCanvas, drawingCanvas);
        Scene scene = new Scene(rootCanvas, screenBounds.getWidth(), screenBounds.getHeight());

        MainStage = primaryStage;

        // Canvas üzerindeki mouse ayarları
        Image image = new Image(Main.mouseIcon);
        ImageCursor cursor = new ImageCursor(image,image.getWidth()-35,image.getHeight()-35);
        scene.setCursor(cursor);

        // Canvas üzerinde klavye dinlemesine öncelik verdik anca öyle calısıyor
        scene.onKeyPressedProperty().bind(drawingCanvas.onKeyPressedProperty());

        // Gerisi genel ayarlar
        primaryStage.initStyle(StageStyle.UNDECORATED); // Pencere çerçevesini gizleme
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());

        primaryStage.setTitle("TranslationApp");
        primaryStage.setScene(scene);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.toFront();
        primaryStage.show();

        //PhaseTwo
        // Eger ESC tusuna basarsak diye klavyeyi dinliyor canvas üzerindeyken, basarsak kapanıyor stage
        drawingCanvas.requestFocus();
        drawingCanvas.setOnKeyPressed(e ->{
            if (e.getCode() == KeyCode.ESCAPE) {
                primaryStage.close();
            }
        });

        // Burda mousu dinliyoruz artık
        drawingCanvas.setOnMousePressed(e -> {
            // Başlangıç noktasını al
            double startX = e.getX();
            double startY = e.getY();
            // Rectangle'ın başlangıç noktasını ve boyutunu belirlemek için ilk noktayı sakla ve genel ayarlar
            drawingGC.setStroke(Color.BLUEVIOLET);
            drawingGC.setLineWidth(3);
            drawingGC.beginPath();
            drawingGC.lineTo(startX, startY);

            // Mouse sürüklendiğinde yapılan işlemler
            drawingCanvas.setOnMouseDragged(dragEvent -> {
                double endX = dragEvent.getX();
                double endY = dragEvent.getY();
                // Boyutları hesapla
                width = Math.abs(endX - startX);
                height = Math.abs(endY - startY);
                // Minimum değerleri belirle
                minX = Math.min(startX, endX);
                minY = Math.min(startY, endY);
                // Temizle resmi getir çizimi yap
                drawingGC.clearRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());
                drawingGC.setFill(new Color(0.2, 0.2, 0.2, 0.4)); // Yarı transparan gri renk
                drawingGC.fillRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());
                drawingGC.strokeRect(minX, minY, width, height); // Kareyi çiz
                drawingGC.clearRect(minX, minY, width, height);
            });
        });

        drawingCanvas.setOnMouseReleased(e -> {
            // ekran görüntüsü almak için kullanılan robot nesnesini oluştururuz
            // Burda varolan resmi kırpmayı denedim lakin robot daah kullanışlı oluyor,diğer çözümde baya yanlıs resimler kesiyor
            try {
                robot = new Robot();
                // İstediğimiz kısmın(ki burda mouse ile seçtiğimiz yer oluyor) boyutlarını alırız
                Rectangle rectangle = new Rectangle((int) minX, (int) minY, (int) width, (int) height);
                // Farklı çözünürlüklerde ekran görüntüsü alıyoruz
                multiResCapture = robot.createMultiResolutionScreenCapture(rectangle);
                // Aldıgımız çözünürlük listeisindeki en sondaki yani en yüksek çözünürlüğe sahip olanı alırız
                fullHD = multiResCapture.getResolutionVariants().get(multiResCapture.getResolutionVariants().size() - 1);
                // Bunu Image olarak veridiği için bufferedImage'a ceviririz
                // Önce boş bir buffered image oluşturuyoruz

                bufferedFullHD = new BufferedImage(
                        fullHD.getWidth(null),
                        fullHD.getHeight(null),
                        // alttaki renk modeli
                        BufferedImage.TYPE_INT_ARGB
                );

                // Boş bufferedImage üzerinene fullHD resmimizi çiziyoruz sonundada dispose() ile graphic2d yi serbest bırakıyoruz
                Graphics2D g2d = bufferedFullHD.createGraphics();
                g2d.drawImage(fullHD, 0, 0, null);
                g2d.dispose();

                primaryStage.close();
                // Sonra mouse ile alınan kesim PhaseThree'ye gönderiliyor bir sonraki aşama
                PhaseThree(bufferedFullHD);


            } catch (AWTException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });

    }


    public void PhaseThree(BufferedImage bufferedFullHD) throws IOException {
        // Phase Three tamamen sample.RecoText ve sample.Translate bölümlerinden oluşuyor
        RecoText RecoText = new RecoText();
        Translate translate = new Translate();

        String engText;
        engText = (RecoText.recoText(bufferedFullHD));
        System.out.println(engText);

        if(engText!=null && engText.length()>1){

            StringBuilder trText= new StringBuilder();
            trText.setLength(0);
            trText.append(translate.translate(engText));
            System.out.println(trText);

            // fxml kullanıyoruz burda eger fxml üzerinde oynamak isterseniz IDE'nize SceneBuilder uygulamasını indirin
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("display.fxml"));
            Scene rootScene = new Scene(root);

            Text textElement = (Text) root.lookup("#text");
            textElement.setText(String.valueOf(trText));

            MainStage.setScene(rootScene);
            MainStage.setX(screenBounds.getWidth()*3/4-40);
            MainStage.setY(screenBounds.getHeight()*3/4);
            MainStage.setWidth(root.prefWidth(420));
            MainStage.setHeight(root.prefHeight(150));

            MainStage.setTitle("TranslationApp");
            MainStage.setAlwaysOnTop(true);
            MainStage.toFront();
            MainStage.show();
        }
    }
}
