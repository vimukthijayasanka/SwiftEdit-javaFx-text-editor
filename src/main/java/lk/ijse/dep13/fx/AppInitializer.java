package lk.ijse.dep13.fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.ijse.dep13.fx.util.AppRouter;

import java.io.IOException;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Scene scene = new Scene(AppRouter.getContainer(AppRouter.Routes.MAIN));
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setTitle("SWIFTEDIT");
        primaryStage.show();
    }
}
