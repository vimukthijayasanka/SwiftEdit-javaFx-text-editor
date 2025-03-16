package lk.ijse.dep13.fx.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class AppRouter {
    public enum Routes{
        MAIN,ABOUT
    }

    public static AnchorPane getContainer(Routes route) throws IOException {
        AnchorPane conatiner = null;
        if (route == Routes.MAIN){
            conatiner = FXMLLoader.load(AppRouter.class.getResource("/scene/MainScene.fxml"));
        } if (route == Routes.ABOUT){
            conatiner = FXMLLoader.load(AppRouter.class.getResource("/scene/About.fxml"));
        }
        return conatiner;
    }
}
