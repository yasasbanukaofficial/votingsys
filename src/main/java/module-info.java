module lk.ijse.votingsys {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;


    opens lk.ijse.votingsys.controller to javafx.fxml;
    exports lk.ijse.votingsys;
}