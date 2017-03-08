package startpkg;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * Created by Abdullah on 12/22/2015.
 */
public class faqController {

    private StartMain main;

    @FXML
    public void backToMainMenu(ActionEvent event){
        main.showMainMenu();
    }




    public void setMain(StartMain main) {
        this.main = main;
    }
}
